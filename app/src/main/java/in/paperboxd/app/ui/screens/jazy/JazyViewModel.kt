package `in`.paperboxd.app.ui.screens.jazy

import android.content.Context
import androidx.lifecycle.ViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.billing.PlusBilling
import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.VibeMatch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class JazyUiState(
    val query: String = "",
    val isSearching: Boolean = false,
    val matches: List<VibeMatch> = emptyList(),
    val showResults: Boolean = false,
    val errorMessage: String? = null,
    /** Jazy's one-line framing above the deck, when Claude wrote one. */
    val deckIntro: String? = null,
    /**
     * What the deck header quotes: the raw ask, or on a refinement the
     * accumulated one — "shorter" alone would read as a search for that word.
     */
    val deckAsk: String = "",
    /**
     * Jazy's one clarifying question and its tappable replies. Set instead of
     * results when the ask was too open to answer well; cleared on the next
     * submit. Jazy never asks twice in a row.
     */
    val pendingQuestion: String? = null,
    val questionOptions: List<String> = emptyList(),
    /**
     * Bookshelf total + TBR count, gating the camera. Null = not determined
     * yet or the request failed — callers must fail OPEN on null, since a
     * network hiccup is not a reason to take a feature away.
     */
    val shelfSize: Int? = null,
    /** Ask Jazy is Plus-only: set when the ask needs a subscription first. */
    val showPaywall: Boolean = false
)

/** Ask Jazy's vibe search. iOS twin: the search half of `JazyView`. */
@HiltViewModel
class JazyViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    val billing: PlusBilling
) : ViewModel() {

    private val _state = MutableStateFlow(JazyUiState())
    val state: StateFlow<JazyUiState> = _state.asStateFlow()

    init {
        // Entitlement check + link any purchase Play already holds, once per open.
        billing.start()
    }

    fun setPaywall(visible: Boolean) = _state.update { it.copy(showPaywall = visible) }

    private var searchJob: Job? = null

    /** Search conversation id from the backend; "shorter" refines the last ask. */
    private var sessionId: String? = null

    /**
     * Stable per-install id so a logged-out reader can still hold a search
     * conversation. Same key and contract as web's localStorage `pb_anon_id`.
     */
    private val anonId: String = context
        .getSharedPreferences("pb_identity", Context.MODE_PRIVATE)
        .let { prefs ->
            prefs.getString("pb_anon_id", null) ?: java.util.UUID.randomUUID().toString().also {
                prefs.edit().putString("pb_anon_id", it).apply()
            }
        }

    /**
     * Bookshelf + TBR size, for the Scan gate. Loaded once per open.
     * ponytail: a book on both the shelf and the TBR counts twice. The two are
     * near-disjoint in practice (TBR means not read yet) and this only decides
     * a soft nudge — union the ids if that ever proves wrong.
     */
    fun loadShelfSize(username: String?) {
        if (username.isNullOrEmpty() || _state.value.shelfSize != null) return
        viewModelScope.launch {
            val shelf = userRepository.bookshelf(username, status = null, page = 1, pageSize = 1)
                .getOrNull()?.totalCount?.toInt()
            val tbr = userRepository.tbr(username).getOrNull()?.size
            if (shelf == null && tbr == null) return@launch
            _state.update { it.copy(shelfSize = (shelf ?: 0) + (tbr ?: 0)) }
        }
    }

    fun onQueryChanged(raw: String) {
        _state.update { it.copy(query = raw, errorMessage = null) }
    }

    fun appendChip(chip: String) {
        val current = _state.value.query.trim()
        _state.update { it.copy(query = if (current.isEmpty()) chip else "$current $chip") }
    }

    /** Tapping one of Jazy's options re-sends the same ask with the answer. */
    fun answer(option: String) = submit(answer = option)

    fun submit(answer: String? = null) {
        val q = _state.value.query.trim()
        if (q.isEmpty() || _state.value.isSearching) return
        // Known not-subscribed opens the paywall before the ask; unknown
        // (null) lets it through and the backend's 402 opens it.
        if (billing.isActive.value == false) {
            _state.update { it.copy(showPaywall = true) }
            return
        }
        searchJob?.cancel()
        _state.update {
            it.copy(isSearching = true, errorMessage = null, pendingQuestion = null, questionOptions = emptyList())
        }
        searchJob = viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            // The concierge is the only source of the score + reason the card
            // shows, so a failure has to surface rather than quietly show nothing.
            bookRepository.jazy(q, sessionId = sessionId, answer = answer, anonId = anonId, limit = 5)
                .also { holdSearchingScreen(startedAt) }
                .onSuccess { turn ->
                    sessionId = turn.sessionId.ifEmpty { sessionId }
                    // One question, then an answer — never a second question.
                    if (turn.isQuestion && answer == null) {
                        _state.update {
                            it.copy(
                                isSearching = false,
                                pendingQuestion = turn.question,
                                questionOptions = turn.options.orEmpty()
                            )
                        }
                        return@onSuccess
                    }
                    val items = turn.items.orEmpty()
                    _state.update {
                        if (items.isEmpty()) {
                            it.copy(
                                isSearching = false,
                                errorMessage = "Nothing on this shelf — try dropping a word"
                            )
                        } else {
                            it.copy(
                                isSearching = false,
                                matches = items,
                                deckIntro = turn.intro,
                                deckAsk = turn.understood?.takeIf { turn.refined } ?: it.query,
                                showResults = true
                            )
                        }
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isSearching = false,
                            showPaywall = e is ApiError.SubscriptionRequired,
                            errorMessage = if (e is ApiError.SubscriptionRequired) null
                                           else e.message ?: "Shelf jammed — try again"
                        )
                    }
                }
        }
    }

    /**
     * Not thinking-theatre: the running shelf has to be on screen long enough
     * to read as a shelf. Under [MIN_SEARCH_DISPLAY_MS] it flashes and looks
     * like a glitch.
     */
    private suspend fun holdSearchingScreen(startedAt: Long) {
        val remaining = MIN_SEARCH_DISPLAY_MS - (System.currentTimeMillis() - startedAt)
        if (remaining > 0) delay(remaining)
    }

    fun closeResults() {
        _state.update { it.copy(showResults = false) }
    }

    private companion object {
        const val MIN_SEARCH_DISPLAY_MS = 800L
    }
}
