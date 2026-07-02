package `in`.paperboxd.app.ui.screens.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.RecommendationRepository
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.BookReview
import `in`.paperboxd.app.domain.model.FriendBookReview
import `in`.paperboxd.app.domain.model.FriendOnBook
import `in`.paperboxd.app.domain.model.ReadingProgress
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BookUserState(
    val isRead: Boolean = false,
    val isLiked: Boolean = false,
    val isTbr: Boolean = false,
    val isOnShelf: Boolean = false,
    val isMutating: Boolean = false
)

data class BookDetailUiState(
    val book: Book? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val similarBooks: List<RecommendationItem> = emptyList(),
    val bookState: BookUserState = BookUserState(),
    val friendsOnBook: List<FriendOnBook> = emptyList(),
    val friendsReadingCount: Int = 0,
    val friendReviews: List<FriendBookReview> = emptyList(),
    val reviews: List<BookReview> = emptyList(),
    val isSubmittingReview: Boolean = false,
    val progress: ReadingProgress? = null,
    val isSavingProgress: Boolean = false
)

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository,
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    val bookId: String = savedStateHandle.get<String>("bookId").orEmpty()

    private val _state = MutableStateFlow(BookDetailUiState())
    val state: StateFlow<BookDetailUiState> = _state.asStateFlow()

    /** One-shot snackbar messages ("Review posted", revert errors). */
    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    var user: User? = null
        set(value) {
            val wasNull = field == null
            field = value
            if (wasNull && value != null) fetchAll()
        }

    /** The viewer's own review, if present. */
    val myReview: BookReview?
        get() {
            val uname = user?.username?.lowercase() ?: return null
            return _state.value.reviews.firstOrNull { it.username.lowercase() == uname }
        }

    fun fetchAll() {
        val username = user?.username
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val bookTask = async { bookRepository.book(bookId) }
            val similarTask = async { recommendationRepository.similar(bookId) }
            val statusTask = async { username?.let { bookRepository.bookStatus(it, bookId).getOrNull() } }
            val friendsTask = async { bookRepository.friendsReading(bookId).getOrNull() }
            val friendReviewsTask = async { bookRepository.friendReviews(bookId).getOrNull() }
            val reviewsTask = async { bookRepository.reviews(bookId).getOrNull() }
            val progressTask = async { username?.let { bookRepository.readingProgress(it, bookId).getOrNull() } }

            bookTask.await().fold(
                onSuccess = { book -> _state.update { it.copy(book = book) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            val status = statusTask.await()
            val friends = friendsTask.await()
            _state.update {
                it.copy(
                    similarBooks = similarTask.await().getOrNull()?.similar.orEmpty(),
                    bookState = status?.let { s ->
                        BookUserState(s.isRead, s.isLiked, s.isTBR, s.isOnShelf)
                    } ?: it.bookState,
                    friendsOnBook = friends?.friends.orEmpty(),
                    friendsReadingCount = friends?.readingCount ?: 0,
                    friendReviews = friendReviewsTask.await()?.reviews.orEmpty(),
                    reviews = reviewsTask.await()?.reviews.orEmpty(),
                    progress = progressTask.await(),
                    isLoading = false
                )
            }
        }
    }

    // MARK: - Optimistic toggles

    fun toggleBookshelf() {
        val username = user?.username ?: return
        val prev = _state.value.bookState
        _state.update {
            it.copy(
                bookState = prev.copy(
                    isOnShelf = !prev.isOnShelf,
                    isRead = if (prev.isOnShelf) false else prev.isRead,
                    isTbr = if (prev.isOnShelf) false else prev.isTbr,
                    isMutating = true
                )
            )
        }
        viewModelScope.launch {
            val result = if (prev.isOnShelf) {
                bookRepository.removeFromBookshelf(username, bookId)
            } else {
                bookRepository.addToBookshelf(username, bookId, "reading")
            }
            result.onFailure { e ->
                _state.update { it.copy(bookState = prev.copy(isMutating = false)) }
                _toast.tryEmit(e.message ?: "Couldn't update bookshelf")
            }
            _state.update { it.copy(bookState = it.bookState.copy(isMutating = false)) }
        }
    }

    fun toggleLike() {
        val prev = _state.value.bookState
        _state.update { it.copy(bookState = prev.copy(isLiked = !prev.isLiked, isMutating = true)) }
        viewModelScope.launch {
            val result = if (prev.isLiked) bookRepository.unlike(bookId) else bookRepository.like(bookId)
            result.onFailure { e ->
                _state.update { it.copy(bookState = prev.copy(isMutating = false)) }
                _toast.tryEmit(e.message ?: "Couldn't update like")
            }
            _state.update { it.copy(bookState = it.bookState.copy(isMutating = false)) }
        }
    }

    fun toggleTbr() {
        val username = user?.username ?: return
        val prev = _state.value.bookState
        _state.update {
            it.copy(
                bookState = prev.copy(
                    isTbr = !prev.isTbr,
                    isRead = if (!prev.isTbr) false else prev.isRead,
                    isMutating = true
                )
            )
        }
        viewModelScope.launch {
            val result = if (prev.isTbr) {
                bookRepository.removeFromBookshelf(username, bookId)
            } else {
                bookRepository.addToBookshelf(username, bookId, "to-read")
            }
            result.onFailure { e ->
                _state.update { it.copy(bookState = prev.copy(isMutating = false)) }
                _toast.tryEmit(e.message ?: "Couldn't update TBR")
            }
            _state.update { it.copy(bookState = it.bookState.copy(isMutating = false)) }
        }
    }

    // MARK: - Review + progress

    /** Shelves the book first if needed (backend stores reviews on the shelf entry). */
    fun submitReview(rating: Int, review: String?, onDone: (Boolean) -> Unit) {
        val username = user?.username ?: return onDone(false)
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingReview = true) }
            if (!_state.value.bookState.isOnShelf) {
                val added = bookRepository.addToBookshelf(username, bookId, "reading")
                if (added.isSuccess) {
                    _state.update { it.copy(bookState = it.bookState.copy(isOnShelf = true)) }
                }
            }
            val result = bookRepository.updateRating(username, bookId, rating, review)
            result.fold(
                onSuccess = {
                    val refreshed = bookRepository.reviews(bookId).getOrNull()?.reviews
                    if (refreshed != null) _state.update { it.copy(reviews = refreshed) }
                    _toast.tryEmit("Review posted")
                    onDone(true)
                },
                onFailure = { e ->
                    _toast.tryEmit(e.message ?: "Couldn't post review")
                    onDone(false)
                }
            )
            _state.update { it.copy(isSubmittingReview = false) }
        }
    }

    fun updateProgress(currentPage: Int, totalPages: Int) {
        val username = user?.username ?: return
        viewModelScope.launch {
            _state.update { it.copy(isSavingProgress = true) }
            bookRepository.updateProgress(username, bookId, currentPage, totalPages).fold(
                onSuccess = { resp ->
                    _state.update {
                        it.copy(
                            progress = ReadingProgress(
                                onShelf = it.progress?.onShelf ?: true,
                                status = it.progress?.status ?: "reading",
                                currentPage = resp.currentPage ?: currentPage,
                                totalPages = resp.totalPages ?: it.progress?.totalPages,
                                percent = resp.percent ?: it.progress?.percent
                            )
                        )
                    }
                    _toast.tryEmit("Progress saved")
                },
                onFailure = { e -> _toast.tryEmit(e.message ?: "Couldn't save progress") }
            )
            _state.update { it.copy(isSavingProgress = false) }
        }
    }
}
