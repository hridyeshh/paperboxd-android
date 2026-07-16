package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.AddToBookshelfBody
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.BookListResponse
import `in`.paperboxd.app.domain.model.BookReviewsResponse
import `in`.paperboxd.app.domain.model.BookStatusResponse
import `in`.paperboxd.app.domain.model.FriendReviewsResponse
import `in`.paperboxd.app.domain.model.FriendsReadingResponse
import `in`.paperboxd.app.domain.model.ProgressBody
import `in`.paperboxd.app.domain.model.ProgressUpdateResponse
import `in`.paperboxd.app.domain.model.ReadingProgress
import `in`.paperboxd.app.domain.model.ReviewBody
import `in`.paperboxd.app.domain.model.ReviewUpdateResponse
import `in`.paperboxd.app.domain.model.ScanAnalyzeBody
import `in`.paperboxd.app.domain.model.ScanAnalyzeResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun book(id: String): Result<Book> = safeApiCall { api.book(id) }

    suspend fun searchBooks(query: String, page: Int? = null, pageSize: Int? = 20): Result<BookListResponse> =
        safeApiCall { api.searchBooks(query, page, pageSize) }

    suspend fun latestBooks(page: Int? = null, pageSize: Int? = null): Result<BookListResponse> =
        safeApiCall { api.latestBooks(page, pageSize) }

    suspend fun randomBooks(pageSize: Int? = null): Result<BookListResponse> =
        safeApiCall { api.randomBooks(pageSize) }

    suspend fun vibeSearch(query: String, limit: Int = 10): Result<BookListResponse> =
        safeApiCall { api.vibeSearch(mapOf("query" to query, "limit" to limit)) }

    suspend fun like(bookId: String): Result<Unit> = safeApiCall { api.likeBook(bookId); Unit }
    suspend fun unlike(bookId: String): Result<Unit> = safeApiCall { api.unlikeBook(bookId); Unit }

    suspend fun reviews(bookId: String): Result<BookReviewsResponse> =
        safeApiCall { api.bookReviews(bookId) }

    suspend fun friendReviews(bookId: String): Result<FriendReviewsResponse> =
        safeApiCall { api.bookReviewsByFriends(bookId) }

    suspend fun friendsReading(bookId: String): Result<FriendsReadingResponse> =
        safeApiCall { api.bookFriendsReading(bookId) }

    // Bookshelf
    suspend fun addToBookshelf(username: String, bookId: String, status: String): Result<Unit> =
        safeApiCall { api.addToBookshelf(username, AddToBookshelfBody(bookId, status)); Unit }

    /** Scan flow: add by ISBN — the backend auto-creates the book if uncached. */
    suspend fun addToBookshelfByIsbn(username: String, isbn: String, status: String): Result<Unit> =
        safeApiCall {
            api.addToBookshelf(username, AddToBookshelfBody(status = status, isbn = isbn)); Unit
        }

    suspend fun removeFromBookshelf(username: String, bookId: String): Result<Unit> =
        safeApiCall { api.removeFromBookshelf(username, bookId); Unit }

    suspend fun updateRating(username: String, bookId: String, rating: Int, review: String?): Result<ReviewUpdateResponse> =
        safeApiCall { api.updateBookshelfRating(username, bookId, ReviewBody(rating, review)) }

    suspend fun bookStatus(username: String, bookId: String): Result<BookStatusResponse> =
        safeApiCall { api.bookStatus(username, bookId) }

    suspend fun readingProgress(username: String, bookId: String): Result<ReadingProgress> =
        safeApiCall { api.readingProgress(username, bookId) }

    suspend fun updateProgress(username: String, bookId: String, currentPage: Int, totalPages: Int): Result<ProgressUpdateResponse> =
        safeApiCall { api.updateReadingProgress(username, bookId, ProgressBody(currentPage, totalPages)) }

    // Scan & Know
    suspend fun scanAnalyze(isbn: String): Result<ScanAnalyzeResponse> =
        safeApiCall { api.scanAnalyze(ScanAnalyzeBody(isbn)) }
}
