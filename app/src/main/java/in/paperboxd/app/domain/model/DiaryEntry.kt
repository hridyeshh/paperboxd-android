package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** Maps types.ThoughtResponse. Kotlin still calls a thought a DiaryEntry. */
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
    @SerializedName("reposts_count") val repostsCount: Long = 0,
    @SerializedName("is_reposted") val isReposted: Boolean = false,
    /** Set on a follow-up: the thought that started its thread. */
    @SerializedName("thread_root_id") val threadRootId: String? = null,
    /** Follow-ups under a thread-starting thought; 0 on follow-ups. */
    @SerializedName("thread_count") val threadCount: Long = 0,
    /** Set when the row is on a profile because that reader reposted it. */
    @SerializedName("reposted_by") val repostedBy: Reposter? = null,
    @SerializedName("can_edit") val canEdit: Boolean = false,
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
) {
    data class Reposter(val username: String = "", val name: String = "")

    val displayName: String get() = name.ifBlank { username }

    /** List key: a repost and the original can share a page, so `id` alone collides. */
    val rowKey: String get() = repostedBy?.let { "$id-repost-${it.username}" } ?: id

    /** Rich-text HTML from the web editor or plain text from the apps, as paragraphs. */
    val plainText: String
        get() = content
            .replace(Regex("</p>|<br\\s*/?>"), "\n")
            .replace(Regex("<[^>]+>"), "")
            .replace("&nbsp;", " ").replace("&amp;", "&").replace("&lt;", "<")
            .replace("&gt;", ">").replace("&quot;", "\"").replace("&#39;", "'")
            .replace(Regex("\n{3,}"), "\n\n")
            .trim()

    /** Preview for list rows. */
    val plainTextPreview: String get() = plainText.take(280)

    /** This thought with the like flipped; [count] wins when the server sent one. */
    fun withLike(on: Boolean, count: Long? = null) =
        copy(isLiked = on, likesCount = count ?: (likesCount + if (on) 1 else -1).coerceAtLeast(0))

    /** This thought with the repost flipped; [count] wins when the server sent one. */
    fun withRepost(on: Boolean, count: Long? = null) =
        copy(isReposted = on, repostsCount = count ?: (repostsCount + if (on) 1 else -1).coerceAtLeast(0))
}

data class DiaryEntriesResponse(
    val thoughts: List<DiaryEntry> = emptyList(),
    @SerializedName("total_count") val totalCount: Long = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0,
    val pagination: PaginationMeta? = null
)

/** GET /users/{username}/thoughts/{id}/thread */
data class ThoughtThreadResponse(val thoughts: List<DiaryEntry> = emptyList())

/** Like/unlike and repost/unrepost answer with the fresh count. */
data class ThoughtCountResponse(
    @SerializedName("likes_count") val likesCount: Long? = null,
    @SerializedName("reposts_count") val repostsCount: Long? = null
)

/** Body for POST /users/{username}/thoughts. */
data class DiaryCreateBody(
    @SerializedName("book_id") val bookId: String? = null,
    val content: String,
    val rating: Int? = null,
    @SerializedName("reading_date") val readingDate: String? = null,
    /** Continues this thought's thread; the backend takes book and privacy from it. */
    @SerializedName("thread_parent_id") val threadParentId: String? = null
)
