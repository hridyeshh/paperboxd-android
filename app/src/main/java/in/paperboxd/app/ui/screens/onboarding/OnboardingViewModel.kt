package `in`.paperboxd.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.AnalyticsRepository
import `in`.paperboxd.app.data.repository.AuthRepository
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.RecommendationRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.ONBOARDING_MAX_PICKS
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.SuggestedReader
import `in`.paperboxd.app.domain.model.User
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class OnboardingStep { Username, Genres, Books, Readers, AhaLoading, AhaReveal }

sealed class UsernameAvailability {
    data object Idle : UsernameAvailability()
    data object Checking : UsernameAvailability()
    data object Available : UsernameAvailability()
    data class Taken(val reason: String?) : UsernameAvailability()
    data class CheckFailed(val message: String) : UsernameAvailability()
}

data class OnboardingUiState(
    val step: OnboardingStep = OnboardingStep.Username,
    val username: String = "",
    val displayName: String = "",
    val availability: UsernameAvailability = UsernameAvailability.Idle,
    val avatarUrl: String? = null,
    val isUploadingAvatar: Boolean = false,
    val selectedGenres: Set<String> = emptySet(),
    // Books-you-love: picks become favourites (Top 4) *and* `read` shelf rows —
    // `read` is what the recommendation engine ranks on.
    val pickSuggestions: List<RecommendationItem> = emptyList(),
    val isLoadingSuggestions: Boolean = false,
    val picks: List<RecommendationItem> = emptyList(),
    val searchQuery: String = "",
    val searchResults: List<RecommendationItem> = emptyList(),
    val isSearching: Boolean = false,
    // Readers-to-follow
    val readers: List<SuggestedReader> = emptyList(),
    val isLoadingReaders: Boolean = false,
    val followed: Set<String> = emptySet(),
    val ahaBooks: List<RecommendationItem> = emptyList(),
    val ahaIndex: Int = 0,
    val isAddingBook: Boolean = false,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val currentAhaBook: RecommendationItem? get() = ahaBooks.getOrNull(ahaIndex)

    /** What the picker grid shows: search hits while typing, suggestions otherwise. */
    val pickGrid: List<RecommendationItem>
        get() = if (searchQuery.trim().length >= 2) searchResults else pickSuggestions

    fun isPicked(id: String): Boolean = picks.any { it.id == id }
}

sealed class OnboardingEvent {
    data class UsernameSet(val username: String) : OnboardingEvent()
    data object Finished : OnboardingEvent()
}

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val bookRepository: BookRepository,
    private val recommendationRepository: RecommendationRepository,
    private val userRepository: UserRepository,
    private val analytics: AnalyticsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingUiState())
    val state: StateFlow<OnboardingUiState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<OnboardingEvent>(extraBufferCapacity = 2)
    val events: SharedFlow<OnboardingEvent> = _events.asSharedFlow()

    /** Set once by the screen from AppState's user (id/email/username fallback). */
    var initialUser: User? = null

    private var claimedUsername: String? = null
    private var debounceJob: Job? = null
    private var searchJob: Job? = null
    private val startedAt = System.currentTimeMillis()

    init {
        analytics.track("onboarding_started", metadata = mapOf("entry_step" to "username"))
    }

    /** Claimed username wins; the init-time handle may be a stale auto-generated value. */
    private fun resolvedUsername(): String? =
        claimedUsername?.ifEmpty { null }
            ?: _state.value.username.ifEmpty { null }
            ?: initialUser?.username

    // MARK: Username

    fun onUsernameChange(raw: String) {
        val trimmed = raw.trim().lowercase()
        _state.update { it.copy(username = trimmed) }
        debounceJob?.cancel()

        if (trimmed.isEmpty()) {
            _state.update { it.copy(availability = UsernameAvailability.Idle) }
            return
        }
        if (!isLocallyValid(trimmed)) {
            _state.update {
                it.copy(availability = UsernameAvailability.Taken("3–50 chars, letters / numbers / _ / - only."))
            }
            return
        }
        _state.update { it.copy(availability = UsernameAvailability.Checking) }
        debounceJob = viewModelScope.launch {
            delay(500)
            check(trimmed)
        }
    }

    fun onDisplayNameChange(v: String) = _state.update { it.copy(displayName = v) }

    private suspend fun check(username: String) {
        authRepository.checkUsername(username).fold(
            onSuccess = { resp ->
                if (_state.value.username != username) return
                _state.update {
                    it.copy(
                        availability = if (resp.available) UsernameAvailability.Available
                        else UsernameAvailability.Taken(resp.reason)
                    )
                }
            },
            onFailure = { e ->
                _state.update {
                    it.copy(availability = UsernameAvailability.CheckFailed(e.message ?: "Could not check username."))
                }
            }
        )
    }

    fun submitUsername() {
        val s = _state.value
        if (s.availability != UsernameAvailability.Available) return
        val body = buildMap {
            put("username", s.username)
            s.displayName.trim().takeIf { it.isNotEmpty() }?.let { put("name", it) }
        }
        withSubmitting {
            authRepository.updateMobileMe(body).fold(
                onSuccess = { resp ->
                    claimedUsername = resp.user.username ?: s.username
                    _events.tryEmit(OnboardingEvent.UsernameSet(claimedUsername!!))
                    _state.update { it.copy(step = OnboardingStep.Genres, errorMessage = null) }
                },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
        }
    }

    /** Skip the whole flow: claim the fallback username, then finish. */
    fun skip() {
        val fallback = initialUser?.username
            ?: initialUser?.email?.substringBefore('@')
            ?: "reader"
        withSubmitting {
            authRepository.updateMobileMe(mapOf("username" to fallback)).fold(
                onSuccess = { resp ->
                    claimedUsername = resp.user.username ?: fallback
                    _events.tryEmit(OnboardingEvent.UsernameSet(claimedUsername!!))
                },
                onFailure = {
                    claimedUsername = fallback
                    _events.tryEmit(OnboardingEvent.UsernameSet(fallback))
                }
            )
            finish(aha = "skipped_all")
        }
    }

    // MARK: Avatar

    fun uploadAvatar(bytes: ByteArray) {
        viewModelScope.launch {
            _state.update { it.copy(isUploadingAvatar = true) }
            authRepository.uploadAvatar(bytes).fold(
                onSuccess = { resp -> _state.update { it.copy(avatarUrl = resp.avatarUrl) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            _state.update { it.copy(isUploadingAvatar = false) }
        }
    }

    // MARK: Genres

    fun toggleGenre(id: String) {
        _state.update {
            val next = it.selectedGenres.toMutableSet()
            if (!next.remove(id)) next.add(id)
            it.copy(selectedGenres = next)
        }
    }

    /**
     * Genres are saved here, not at the end: the picker suggestions and every
     * later recommendation call are genre-aware, and a reader who bails after
     * this step is still onboarded instead of being bounced back in.
     */
    fun continueFromGenres() {
        if (_state.value.selectedGenres.size < 3) return
        _state.update { it.copy(step = OnboardingStep.Books, errorMessage = null, isLoadingSuggestions = true) }
        viewModelScope.launch {
            authRepository.saveOnboarding(_state.value.selectedGenres.toList())
            val books = recommendationRepository.home().getOrNull()?.recommendations.orEmpty()
                .filter { it.coverUrl.isValidCover() && it.title.isNotEmpty() }
                .take(12)
            _state.update { it.copy(pickSuggestions = books, isLoadingSuggestions = false) }
        }
    }

    // MARK: Books you love

    fun onSearchChange(raw: String) {
        _state.update { it.copy(searchQuery = raw) }
        searchJob?.cancel()
        val q = raw.trim()
        if (q.length < 2) {
            _state.update { it.copy(searchResults = emptyList(), isSearching = false) }
            return
        }
        _state.update { it.copy(isSearching = true) }
        searchJob = viewModelScope.launch {
            delay(300)
            val items = bookRepository.searchBooks(q, pageSize = 8).getOrNull()?.items.orEmpty()
                .map { b ->
                    RecommendationItem(
                        id = b.id,
                        title = b.volumeInfo.title,
                        authors = b.volumeInfo.authors.orEmpty(),
                        coverUrl = b.coverUrl
                    )
                }
            _state.update { it.copy(searchResults = items, isSearching = false) }
        }
    }

    fun togglePick(book: RecommendationItem) {
        val s = _state.value
        if (s.isPicked(book.id)) {
            _state.update { it.copy(picks = it.picks.filterNot { p -> p.id == book.id }) }
            return
        }
        if (s.picks.size >= ONBOARDING_MAX_PICKS) {
            _state.update { it.copy(errorMessage = "That's your top $ONBOARDING_MAX_PICKS — remove one to swap.") }
            return
        }
        analytics.track(
            "book_selected_during_onboarding",
            metadata = mapOf(
                "title" to book.title,
                "source" to if (s.searchResults.any { it.id == book.id }) "search" else "suggested"
            )
        )
        searchJob?.cancel()
        _state.update {
            it.copy(picks = it.picks + book, searchQuery = "", searchResults = emptyList(), isSearching = false, errorMessage = null)
        }
    }

    /**
     * Persists picks (favourite + read shelf entry, in parallel — one failure
     * must not block the rest) while the readers list loads.
     */
    fun continueFromBooks() {
        _state.update { it.copy(step = OnboardingStep.Readers, errorMessage = null, isLoadingReaders = true) }
        viewModelScope.launch {
            val username = resolvedUsername()
            // display_order = pick order, so the Top 4 reads in the order they chose.
            val saves = if (username != null) {
                _state.value.picks.mapIndexed { i, book ->
                    async {
                        bookRepository.addToBookshelf(username, book.id, "read")
                        bookRepository.addFavorite(username, book.id, displayOrder = i + 1)
                    }
                }
            } else emptyList()
            val load = async { userRepository.suggested(limit = 6).getOrNull()?.users.orEmpty() }
            saves.awaitAll()
            val readers = load.await()
            _state.update { it.copy(readers = readers, isLoadingReaders = false) }
        }
    }

    // MARK: Readers to follow

    fun follow(reader: SuggestedReader) {
        if (_state.value.followed.contains(reader.username)) return
        _state.update { it.copy(followed = it.followed + reader.username, errorMessage = null) } // optimistic
        viewModelScope.launch {
            userRepository.follow(reader.username).fold(
                onSuccess = {
                    analytics.track(
                        "person_followed_during_onboarding",
                        metadata = mapOf("username" to reader.username, "reason" to reader.reason)
                    )
                },
                onFailure = {
                    _state.update {
                        it.copy(followed = it.followed - reader.username, errorMessage = "Couldn't follow ${reader.displayName}.")
                    }
                }
            )
        }
    }

    /** Fetches the aha reveal books, excluding anything already picked. */
    fun continueFromReaders() {
        _state.update { it.copy(step = OnboardingStep.AhaLoading, errorMessage = null) }
        viewModelScope.launch {
            // Keep the loading state visible long enough to feel intentional.
            val minLoad = launch { delay(1_600) }
            val pickedIds = _state.value.picks.map { it.id }.toSet()
            val books = recommendationRepository.home().getOrNull()?.recommendations.orEmpty()
                .filter { it.coverUrl.isValidCover() && it.title.isNotEmpty() && it.id !in pickedIds }
            minLoad.join()

            if (books.isEmpty()) {
                finish(aha = "none")
                return@launch
            }
            _state.update {
                it.copy(step = OnboardingStep.AhaReveal, ahaBooks = books.take(8), ahaIndex = 0)
            }
        }
    }

    // MARK: Aha reveal

    fun showAnother() {
        _state.update {
            if (it.ahaBooks.isEmpty()) it else it.copy(ahaIndex = (it.ahaIndex + 1) % it.ahaBooks.size)
        }
    }

    fun skipAha() = finish(aha = "skipped")

    fun saveToShelf() {
        val book = _state.value.currentAhaBook ?: run {
            finish(aha = "none")
            return
        }
        val username = resolvedUsername()
        if (username == null) {
            _state.update { it.copy(errorMessage = "Couldn't find your username — restart onboarding.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isAddingBook = true, errorMessage = null) }
            bookRepository.addToBookshelf(username, book.id, "to-read").fold(
                onSuccess = { finish(aha = "saved") },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            _state.update { it.copy(isAddingBook = false) }
        }
    }

    /** Single exit so `onboarding_completed` fires exactly once, same metadata as web/iOS. */
    private fun finish(aha: String) {
        val s = _state.value
        analytics.track(
            "onboarding_completed",
            metadata = mapOf(
                "books_selected" to s.picks.size.toString(),
                "people_followed" to s.followed.size.toString(),
                "genres" to s.selectedGenres.size.toString(),
                "seconds" to ((System.currentTimeMillis() - startedAt) / 1000).toString(),
                "aha" to aha
            )
        )
        _events.tryEmit(OnboardingEvent.Finished)
    }

    // MARK: Helpers

    private fun isLocallyValid(s: String): Boolean =
        s.length in 3..50 && s.matches(Regex("^[a-z0-9_-]+$"))

    private fun withSubmitting(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isSubmitting = true, errorMessage = null) }
            block()
            _state.update { it.copy(isSubmitting = false) }
        }
    }

    private fun String?.isValidCover(): Boolean {
        val l = this?.lowercase() ?: return false
        if (!l.startsWith("http")) return false
        val bad = listOf("no_cover", "nocover", "placeholder", "default_cover", "missing")
        return bad.none { l.contains(it) }
    }
}
