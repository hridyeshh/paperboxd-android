package `in`.paperboxd.app.ui.screens.bookdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.RecommendationRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.BookReview
import `in`.paperboxd.app.domain.model.FriendBookReview
import `in`.paperboxd.app.domain.model.FriendOnBook
import `in`.paperboxd.app.domain.model.ReadingProgress
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.Celebration
import `in`.paperboxd.app.ui.components.CelebrationCenter
import `in`.paperboxd.app.ui.components.toCelebShelf
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class BookUserState(
    val isRead: Boolean = false,
    val isLiked: Boolean = false,
    val isTbr: Boolean = false,
    val isOnShelf: Boolean = false,
    val isMutating: Boolean = false
)

/** Pages a reader must log before rating or reviewing. Mirrors MinPagesToRate in the Go backend. */
const val MIN_PAGES_TO_RATE = 20

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
    private val recommendationRepository: RecommendationRepository,
    private val userRepository: UserRepository,
    private val celebrationCenter: CelebrationCenter
) : ViewModel() {

    /**
     * Book UUID. In-app navigation always passes one, but a `paperboxd.in/b/{slug}`
     * deep link passes a web slug instead — [fetchAll] resolves that to the real ID
     * before any other call runs, since every other endpoint requires the UUID.
     */
    var bookId: String = savedStateHandle.get<String>("bookId").orEmpty()
        private set

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

    /** Reports another reader's review (store UGC compliance). */
    fun reportReview(review: BookReview, reason: String) {
        viewModelScope.launch {
            userRepository.report("review", "book:$bookId:user:${review.userId}", reason).fold(
                onSuccess = { _toast.tryEmit("Report received — we review within 24 hours") },
                onFailure = { e -> _toast.tryEmit(e.message ?: "Couldn't send report") }
            )
        }
    }

    /** The viewer's own review, if present. */
    val myReview: BookReview?
        get() {
            val uname = user?.username?.lowercase() ?: return null
            return _state.value.reviews.firstOrNull { it.username.lowercase() == uname }
        }

    /**
     * Whether the viewer has read far enough to rate or review. Finishing counts
     * on its own, since a book with no known page count never accumulates logged
     * pages. An existing rating keeps its owner unlocked so the gate can't strand
     * a review. The Go backend enforces the same rule — see canRateEntry.
     */
    val canRate: Boolean
        get() {
            val mine = myReview
            if ((mine?.rating ?: 0) > 0 || !mine?.review.isNullOrEmpty()) return true
            val progress = _state.value.progress ?: return false
            if (!progress.onShelf) return false
            if (progress.status == "read") return true
            return (progress.currentPage ?: 0) >= MIN_PAGES_TO_RATE
        }

    fun fetchAll() {
        val username = user?.username
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            // Deep link handed us a slug — swap in the UUID before fanning out, or the
            // seven calls below all hit /books/{slug} and 400.
            if (!isUuid(bookId) && !resolveSlug()) return@launch

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

    private fun isUuid(value: String): Boolean =
        runCatching { UUID.fromString(value) }.isSuccess

    /** Swaps [bookId] from a web slug to the real UUID. `false` if the slug is unknown. */
    private suspend fun resolveSlug(): Boolean =
        bookRepository.bookBySlug(bookId).fold(
            onSuccess = { bookId = it.id; true },
            onFailure = { e ->
                _state.update { it.copy(isLoading = false, errorMessage = e.message) }
                false
            }
        )

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

    /**
     * Posts the viewer's rating + optional review. Reviews live on the bookshelf
     * entry, which only exists once the reader has logged progress — rating no
     * longer silently shelves the book, so [canRate] gates the call site.
     */
    fun submitReview(rating: Int, review: String?, onDone: (Boolean) -> Unit) {
        val username = user?.username ?: return onDone(false)
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingReview = true) }
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

    /** Clears the viewer's rating AND review (PATCH rating:0, review:null). */
    fun deleteReview(onDone: (Boolean) -> Unit = {}) {
        val username = user?.username ?: return onDone(false)
        viewModelScope.launch {
            _state.update { it.copy(isSubmittingReview = true) }
            bookRepository.updateRating(username, bookId, 0, null).fold(
                onSuccess = {
                    val refreshed = bookRepository.reviews(bookId).getOrNull()?.reviews
                    if (refreshed != null) _state.update { it.copy(reviews = refreshed) }
                    _toast.tryEmit("Rating & review removed")
                    onDone(true)
                },
                onFailure = { e ->
                    _toast.tryEmit(e.message ?: "Couldn't remove review")
                    onDone(false)
                }
            )
            _state.update { it.copy(isSubmittingReview = false) }
        }
    }

    // MARK: - Add to Library (3 shelves) — iOS twin

    enum class LibraryShelf(val status: String, val toast: String) {
        Bookshelf("reading", "Added to Bookshelf"),
        Tbr("to-read", "Added to TBR"),
        Read("read", "Marked as read")
    }

    /** The shelf the book currently sits on, derived from bookState. */
    val currentShelf: LibraryShelf?
        get() = _state.value.bookState.let {
            when {
                it.isRead -> LibraryShelf.Read
                it.isTbr -> LibraryShelf.Tbr
                it.isOnShelf -> LibraryShelf.Bookshelf
                else -> null
            }
        }

    /**
     * Moves the book to [target], or removes it when [target] is null (tapping
     * the current shelf again). Mark-as-read pushes progress to 100%.
     */
    fun selectShelf(target: LibraryShelf?) {
        val username = user?.username ?: return
        val prev = _state.value.bookState
        val next = when (target) {
            null -> prev.copy(isOnShelf = false, isTbr = false, isRead = false)
            LibraryShelf.Bookshelf -> prev.copy(isOnShelf = true, isTbr = false, isRead = false)
            LibraryShelf.Tbr -> prev.copy(isTbr = true, isOnShelf = false, isRead = false)
            LibraryShelf.Read -> prev.copy(isRead = true, isOnShelf = true, isTbr = false)
        }
        _state.update { it.copy(bookState = next.copy(isMutating = true)) }
        viewModelScope.launch {
            val result = if (target != null) {
                bookRepository.addToBookshelf(username, bookId, target.status)
            } else {
                bookRepository.removeFromBookshelf(username, bookId)
            }
            result.fold(
                onSuccess = {
                    if (target == LibraryShelf.Read) {
                        _state.value.book?.pageCount?.takeIf { it > 0 }?.let { total ->
                            // Suppressed: the shelved takeover plays for this moment.
                            updateProgress(total, total, celebrateStreak = false)
                        }
                    }
                    if (target != null) {
                        val book = _state.value.book
                        celebrationCenter.show(
                            Celebration.Shelved(
                                shelf = target.toCelebShelf(),
                                title = book?.title.orEmpty(),
                                author = book?.authorLine.orEmpty(),
                                coverUrl = book?.coverUrl
                            )
                        )
                    } else {
                        _toast.tryEmit("Removed from library")
                    }
                    _state.update { it.copy(bookState = it.bookState.copy(isMutating = false)) }
                },
                onFailure = { e ->
                    _state.update { it.copy(bookState = prev.copy(isMutating = false)) }
                    _toast.tryEmit(e.message ?: "Couldn't update library")
                }
            )
        }
    }

    /**
     * Logging pages is what extends the reading streak (the server counts any
     * UTC day with >=1 page logged), so a successful log refetches it and
     * celebrates a growth. [celebrateStreak] is false for the mark-as-read path —
     * that moment already gets the shelved takeover, and only one can play.
     */
    fun updateProgress(currentPage: Int, totalPages: Int, celebrateStreak: Boolean = true) {
        val username = user?.username ?: return
        // Only forward progress earns a reading day / streak celebration. Logging
        // a lower page (e.g. correcting 192 → 19) is not a reading day.
        val previousPage = _state.value.progress?.currentPage ?: 0
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
                    if (celebrateStreak && currentPage > previousPage) {
                        userRepository.streak(username).onSuccess { celebrationCenter.checkStreak(it.streak) }
                    }
                },
                onFailure = { e -> _toast.tryEmit(e.message ?: "Couldn't save progress") }
            )
            _state.update { it.copy(isSavingProgress = false) }
        }
    }
}
