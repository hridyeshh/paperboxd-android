package `in`.paperboxd.app

import android.net.Uri
import `in`.paperboxd.app.data.local.SecurePrefs
import `in`.paperboxd.app.data.remote.SessionEvents
import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.repository.AuthRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * How a Fusion gets opened: an invite link (App Link or paperboxd://fusion/<token>),
 * or an existing Fusion from the profile list or a notification. Mirrors iOS FusionRoute.
 */
sealed interface FusionRoute {
    val key: String

    data class Link(val token: String) : FusionRoute {
        override val key get() = "link-$token"
    }

    data class Open(val id: String, val introFrom: String?) : FusionRoute {
        override val key get() = "fusion-$id"
    }

    companion object {
        private val TOKEN = Regex("^[a-z0-9]{12}$")

        fun from(uri: Uri?): FusionRoute? {
            uri ?: return null
            val segments = uri.pathSegments
            val token = when {
                uri.scheme == "paperboxd" && uri.host == "fusion" -> segments.firstOrNull()
                uri.scheme == "https" && uri.host in setOf("paperboxd.in", "www.paperboxd.in") &&
                    segments.firstOrNull() == "fusion" -> segments.getOrNull(1)
                else -> null
            }?.lowercase() ?: return null
            return if (TOKEN.matches(token)) Link(token) else null
        }
    }
}

/** Root navigation states. Mirrors iOS AppScreen. */
sealed class AppDestination {
    data object Splash : AppDestination()
    data object Auth : AppDestination()
    data class Onboarding(val user: User) : AppDestination()
    data class Main(val user: User) : AppDestination()
}

/**
 * App-level session state machine. Mirrors iOS AppState: bootstrap
 * (token → health → refresh), sign-in/out routing, onboarding gate,
 * and global 401 handling via [SessionEvents].
 */
@Singleton
class AppState @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val securePrefs: SecurePrefs,
    sessionEvents: SessionEvents
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _destination = MutableStateFlow<AppDestination>(AppDestination.Splash)
    val destination: StateFlow<AppDestination> = _destination.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    /**
     * A Fusion waiting to open over the main tabs. A link that arrives signed
     * out waits here (and survives process death) until sign-in or onboarding
     * finishes.
     */
    private val _fusionRoute = MutableStateFlow<FusionRoute?>(securePrefs.getPendingFusion()?.let { FusionRoute.Link(it) })
    val fusionRoute: StateFlow<FusionRoute?> = _fusionRoute.asStateFlow()

    fun openFusion(route: FusionRoute?) {
        _fusionRoute.value = route
        securePrefs.setPendingFusion((route as? FusionRoute.Link)?.token)
    }

    /** App Links and paperboxd:// URIs. Returns false for anything that is not ours. */
    fun handleIncomingUri(uri: Uri?): Boolean {
        val route = FusionRoute.from(uri) ?: return false
        openFusion(route)
        return true
    }

    init {
        scope.launch {
            sessionEvents.expired.collect {
                _currentUser.value = null
                _destination.value = AppDestination.Auth
            }
        }
        // First launch: MainActivity.onStart runs before bootstrap resolves, so
        // also record the open the moment we land on Main.
        scope.launch {
            _destination.collect { if (it is AppDestination.Main) recordDailyOpen() }
        }
    }

    /** Boots the app: prefs → health → refresh. Hold covers the clip at 1.25x + its outro fade. */
    suspend fun bootstrap() {
        val start = System.currentTimeMillis()

        val token = authRepository.cachedToken()
        if (token.isNullOrEmpty()) {
            holdSplash(start)
            _destination.value = AppDestination.Auth
            return
        }

        // Reachability probe before the refresh call. If the backend is down,
        // keep the token and route from the cached user (matches iOS).
        val healthOk = authRepository.health().isSuccess
        if (!healthOk) {
            holdSplash(start)
            val cached = authRepository.cachedUser()
            if (cached != null) {
                _currentUser.value = cached
                _destination.value = route(cached)
            } else {
                _destination.value = AppDestination.Auth
            }
            return
        }

        authRepository.refresh().fold(
            onSuccess = { resp ->
                authRepository.persistToken(resp.token, resp.refreshToken)
                val user = authRepository.cachedUser()
                holdSplash(start)
                if (user != null) {
                    _currentUser.value = user
                    _destination.value = route(user)
                } else {
                    // Token valid but no cached user (reinstall edge) — re-login.
                    authRepository.clearSession()
                    _destination.value = AppDestination.Auth
                }
            },
            onFailure = { error ->
                holdSplash(start)
                // Only a genuine auth failure ends the session. A transport
                // error or a 5xx — the window while a deploy swaps containers,
                // a flaky network — must not, or every backend restart looks
                // like a forced logout. The health probe above narrows that
                // window but cannot close it: the refresh can still land
                // mid-swap.
                val cached = authRepository.cachedUser()
                if (error is ApiError.Unauthorized || cached == null) {
                    authRepository.clearSession()
                    _destination.value = AppDestination.Auth
                } else {
                    _currentUser.value = cached
                    _destination.value = route(cached)
                }
            }
        )
    }

    private suspend fun holdSplash(startMillis: Long, minimumMillis: Long = 4_600) {
        val elapsed = System.currentTimeMillis() - startMillis
        if (elapsed < minimumMillis) delay(minimumMillis - elapsed)
    }

    private fun route(user: User): AppDestination =
        if (needsOnboarding(user)) AppDestination.Onboarding(user) else AppDestination.Main(user)

    /** onboarding_completed missing (legacy payload) → fall back to username presence. */
    private fun needsOnboarding(user: User): Boolean =
        user.onboardingCompleted?.let { !it } ?: user.username.isNullOrEmpty()

    fun signedIn(token: String, user: User, refreshToken: String? = null) {
        authRepository.persistSession(token, refreshToken, user)
        _currentUser.value = user
        _destination.value = route(user)
    }

    /** Mid-onboarding username claim — updates cache without routing. */
    fun setOnboardingUsername(username: String) {
        val user = _currentUser.value ?: return
        val updated = user.copy(username = username)
        _currentUser.value = updated
        authRepository.persistUser(updated)
    }

    /** Keeps the cached avatar in sync after an edit-profile upload. */
    fun avatarUpdated(url: String?) {
        val user = _currentUser.value ?: return
        val updated = user.copy(avatarUrl = url)
        _currentUser.value = updated
        authRepository.persistUser(updated)
    }

    fun finishOnboarding() {
        val user = _currentUser.value ?: return
        val updated = user.copy(onboardingCompleted = true)
        _currentUser.value = updated
        authRepository.persistUser(updated)
        _destination.value = AppDestination.Main(updated)
    }

    /**
     * Records an app open for DAU/streaks. The backend awards daily-open XP at
     * most once per UTC day; the local guard just saves the call. Only fires
     * once signed in and past onboarding, matching the web auth-provider.
     */
    private var lastDailyOpenDay: String? = null
    fun recordDailyOpen() {
        if (_destination.value !is AppDestination.Main) return
        val today = java.time.LocalDate.now(java.time.ZoneOffset.UTC).toString()
        if (lastDailyOpenDay == today) return
        scope.launch {
            if (userRepository.dailyOpen().isSuccess) lastDailyOpenDay = today
        }
    }

    fun signOut() {
        authRepository.clearSession()
        _currentUser.value = null
        _destination.value = AppDestination.Auth
    }
}
