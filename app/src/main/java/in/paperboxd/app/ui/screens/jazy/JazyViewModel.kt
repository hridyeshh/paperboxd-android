package `in`.paperboxd.app.ui.screens.jazy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.domain.model.VibeMatch
import kotlinx.coroutines.Job
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
    val errorMessage: String? = null
)

/** Ask Jazy's vibe search. iOS twin: the search half of `JazyView`. */
@HiltViewModel
class JazyViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JazyUiState())
    val state: StateFlow<JazyUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

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
            // The vibe endpoint is the only source of the score + reason the card
            // shows, so a failure has to surface rather than quietly show nothing.
            bookRepository.vibeSearch(q, limit = 5)
                .onSuccess { resp ->
                    val items = resp.items.orEmpty()
                    _state.update {
                        if (items.isEmpty()) {
                            it.copy(
                                isSearching = false,
                                errorMessage = "Jazy couldn't find anything for that. Try another vibe."
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
                            errorMessage = e.message ?: "Jazy is unavailable right now. Try again."
                        )
                    }
                }
        }
    }

    fun closeResults() {
        _state.update { it.copy(showResults = false) }
    }
}
