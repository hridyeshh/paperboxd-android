package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** One friend who has the current book on their shelf. */
data class FriendOnBook(
    @SerializedName("user_id") val userId: String,
    val username: String,
    val name: String = "",
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val status: String = "",
    @SerializedName("current_page") val currentPage: Int? = null,
    @SerializedName("started_at") val startedAt: String? = null
) {
    val displayName: String get() = name.ifEmpty { username }
    val isReadingNow: Boolean get() = status == "reading"
}

data class FriendsReadingResponse(
    val friends: List<FriendOnBook> = emptyList(),
    @SerializedName("reading_count") val readingCount: Int = 0
)

/** One review from a friend on the current book. */
data class FriendBookReview(
    @SerializedName("user_id") val userId: String,
    val username: String,
    val name: String = "",
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val rating: Int? = null,
    val review: String? = null,
    @SerializedName("reviewed_at") val reviewedAt: String? = null,
    val edited: Boolean = false
) {
    val displayName: String get() = name.ifEmpty { username }
}

data class FriendReviewsResponse(
    val reviews: List<FriendBookReview> = emptyList()
)

/** One public review on the current book. */
data class BookReview(
    @SerializedName("user_id") val userId: String,
    val username: String,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val rating: Int? = null,
    val review: String? = null,
    @SerializedName("reviewed_at") val reviewedAt: String? = null,
    val edited: Boolean = false
)

data class BookReviewsResponse(
    val reviews: List<BookReview> = emptyList()
)

/** Reading progress on a specific book for the authenticated user. */
data class ReadingProgress(
    @SerializedName("on_shelf") val onShelf: Boolean = false,
    val status: String = "",
    @SerializedName("current_page") val currentPage: Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    val percent: Double? = null,
    @SerializedName("estimated_finish_date") val estimatedFinishDate: String? = null,
    @SerializedName("started_at") val startedAt: String? = null,
    @SerializedName("finished_at") val finishedAt: String? = null
)

/** GET .../bookshelf/{bookId}/status */
data class BookStatusResponse(
    val isRead: Boolean = false,
    val isLiked: Boolean = false,
    val isTBR: Boolean = false,
    val isOnShelf: Boolean = false
)

/**
 * Bodies for bookshelf mutations. The backend accepts book_id (UUID) or isbn to
 * identify the book — the scan flow adds by ISBN (auto-creates uncached books).
 */
data class AddToBookshelfBody(
    @SerializedName("book_id") val bookId: String? = null,
    val status: String,
    val isbn: String? = null
)

data class ReviewBody(
    val rating: Int,
    val review: String? = null
)

data class ProgressBody(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("total_pages") val totalPages: Int
)

data class ProgressUpdateResponse(
    @SerializedName("current_page") val currentPage: Int? = null,
    @SerializedName("total_pages") val totalPages: Int? = null,
    val percent: Double? = null
)

data class ReviewUpdateResponse(
    val rating: Int? = null,
    val review: String? = null,
    val edited: Boolean? = null
)

/** GET /api/v1/authors/info — Wikipedia-backed, cached 24h server-side. */
data class AuthorInfo(
    val found: Boolean = false,
    val name: String = "",
    val description: String? = null,
    val extract: String = "",
    @SerializedName("photo_url") val photoUrl: String = "",
    @SerializedName("wikipedia_url") val wikipediaUrl: String = ""
)
