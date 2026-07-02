package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** Maps types.CurrentlyReadingResponse. */
data class CurrentlyReading(
    val id: String,
    @SerializedName("book_id") val bookId: String,
    val book: Book,
    val status: String = "",
    @SerializedName("current_page") val currentPage: Int? = null,
    @SerializedName("progress_percentage") val progressPercentage: Double = 0.0,
    @SerializedName("pages_remaining") val pagesRemaining: Int = 0,
    @SerializedName("started_at") val startedAt: String? = null,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
)

/** The user's most-recently logged book, from GET /users/{username}/reading/last. */
data class LastLoggedBook(
    @SerializedName("book_id") val bookId: String,
    val title: String,
    val slug: String = "",
    val author: String = "",
    val cover: String = "",
    @SerializedName("current_page") val currentPage: Int = 0,
    @SerializedName("total_pages") val totalPages: Int = 0
) {
    val coverUrl: String? get() = cover.takeIf { it.isNotEmpty() }
    val displayPercent: Int
        get() = if (totalPages > 0) {
            minOf(100, ((currentPage.toDouble() / totalPages) * 100).toInt())
        } else 0
}

data class LastLoggedBookResponse(
    @SerializedName("last_book") val lastBook: LastLoggedBook? = null
)
