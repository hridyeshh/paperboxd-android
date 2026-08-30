package `in`.paperboxd.app

import `in`.paperboxd.app.data.remote.SessionEvents
import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.repository.AuthRepository
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
    sessionEvents: SessionEvents
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    private val _destination = MutableStateFlow<AppDestination>(AppDestination.Splash)
    val destination: StateFlow<AppDestination> = _destination.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    init {
        scope.launch {
            sessionEvents.expired.collect {
                _currentUser.value = null
                _destination.value = AppDestination.Auth
            }
        }
    }

    /** Boots the app: prefs → health → refresh. Minimum splash hold 2.5s. */
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

    private suspend fun holdSplash(startMillis: Long, minimumMillis: Long = 2_500) {
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

    fun signOut() {
        authRepository.clearSession()
        _currentUser.value = null
        _destination.value = AppDestination.Auth
    }
}
