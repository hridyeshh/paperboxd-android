package `in`.paperboxd.app.ui.screens.jazy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
    /**
     * Bookshelf total + TBR count, gating the camera. Null = not determined
     * yet or the request failed — callers must fail OPEN on null, since a
     * network hiccup is not a reason to take a feature away.
     */
    val shelfSize: Int? = null
)

/** Ask Jazy's vibe search. iOS twin: the search half of `JazyView`. */
@HiltViewModel
class JazyViewModel @Inject constructor(
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JazyUiState())
    val state: StateFlow<JazyUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

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

    fun submit() {
        val q = _state.value.query.trim()
        if (q.isEmpty() || _state.value.isSearching) return
        searchJob?.cancel()
        _state.update { it.copy(isSearching = true, errorMessage = null) }
        searchJob = viewModelScope.launch {
            val startedAt = System.currentTimeMillis()
            // The vibe endpoint is the only source of the score + reason the card
            // shows, so a failure has to surface rather than quietly show nothing.
            bookRepository.vibeSearch(q, limit = 5)
                .also { holdSearchingScreen(startedAt) }
                .onSuccess { resp ->
                    val items = resp.items.orEmpty()
                    _state.update {
                        if (items.isEmpty()) {
                            it.copy(
                                isSearching = false,
                                errorMessage = "Nothing on this shelf — try dropping a word"
                            )
                        } else {
                            it.copy(isSearching = false, matches = items, showResults = true)
                        }
                    }
                }
                .onFailure { e ->
                    _state.update {
                        it.copy(
                            isSearching = false,
                            errorMessage = e.message ?: "Shelf jammed — try again"
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
