package `in`.paperboxd.app.ui.screens.write

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.DiaryRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.DiaryCreateBody
import `in`.paperboxd.app.ui.components.CelebrationCenter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class WriteUiState(
    val content: String = "",
    val selectedBook: Book? = null,
    val rating: Int? = null,
    val readingDateMillis: Long = System.currentTimeMillis(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val didSubmit: Boolean = false,
    val bookSearchQuery: String = "",
    val bookSearchResults: List<Book> = emptyList(),
    val isSearchingBooks: Boolean = false
) {
    val charCount: Int get() = content.trim().length
    val canSubmit: Boolean get() = charCount >= 10 && !isLoading
    val isDirty: Boolean get() = content.isNotBlank() || selectedBook != null
}

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val diaryRepository: DiaryRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val celebrationCenter: CelebrationCenter
) : ViewModel() {

    private val _state = MutableStateFlow(WriteUiState())
    val state: StateFlow<WriteUiState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onContentChange(v: String) = _state.update { it.copy(content = v) }
    fun onRate(v: Int) = _state.update { it.copy(rating = if (it.rating == v) null else v) }
    fun onDateChange(millis: Long) = _state.update { it.copy(readingDateMillis = millis) }
    fun selectBook(book: Book?) = _state.update {
        it.copy(selectedBook = book, bookSearchQuery = "", bookSearchResults = emptyList())
    }

    fun onSearchChange(q: String) {
        _state.update { it.copy(bookSearchQuery = q) }
        searchJob?.cancel()
        val trimmed = q.trim()
        if (trimmed.isEmpty()) {
            _state.update { it.copy(bookSearchResults = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(400)
            _state.update { it.copy(isSearchingBooks = true) }
            val items = bookRepository.searchBooks(trimmed, pageSize = 8).getOrNull()?.items.orEmpty()
            _state.update { it.copy(bookSearchResults = items, isSearchingBooks = false) }
        }
    }

    fun submit(username: String) {
        val s = _state.value
        if (!s.canSubmit) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val readingDate = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(s.readingDateMillis))
            diaryRepository.create(
                username,
                DiaryCreateBody(
                    bookId = s.selectedBook?.id,
                    content = s.content.trim(),
                    rating = s.rating,
                    readingDate = readingDate
                )
            ).fold(
                onSuccess = {
                    _state.update { it.copy(didSubmit = true) }
                    // Logging extends the reading streak — refetch it and
                    // celebrate if it grew past the last value we've seen.
                    userRepository.streak(username).onSuccess { celebrationCenter.checkStreak(it.streak) }
                },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            _state.update { it.copy(isLoading = false) }
        }
    }
}
