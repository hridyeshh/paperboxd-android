package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** Maps types.DiaryEntryResponse. */
data class DiaryEntry(
    val id: String,
    @SerializedName("user_id") val userId: String,
    val username: String,
    val name: String = "",
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("book_id") val bookId: String? = null,
    val book: Book? = null,
    val title: String? = null,
    val content: String,
    @SerializedName("is_private") val isPrivate: Boolean = false,
    val rating: Int? = null,
    @SerializedName("likes_count") val likesCount: Long = 0,
    @SerializedName("is_liked") val isLiked: Boolean = false,
    @SerializedName("can_edit") val canEdit: Boolean = false,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
) {
    /** HTML-stripped preview for list rows. */
    val plainTextPreview: String
        get() = content.replace(Regex("<[^>]+>"), " ").trim().take(200)
}

data class DiaryEntriesResponse(
    val entries: List<DiaryEntry> = emptyList(),
    @SerializedName("total_count") val totalCount: Long = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0,
    val pagination: PaginationMeta? = null
)

/** Body for POST /users/{username}/diary. */
data class DiaryCreateBody(
    @SerializedName("book_id") val bookId: String? = null,
    val content: String,
    val rating: Int? = null,
    @SerializedName("reading_date") val readingDate: String
)
