package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** One friend activity. Maps types.ActivityResponse. */
data class ActivityItem(
    val id: String,
    @SerializedName("user_id") val userId: String,
    val username: String,
    val name: String = "",
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("activity_type") val activityType: String,
    @SerializedName("book_id") val bookId: String? = null,
    @SerializedName("book_title") val bookTitle: String? = null,
    @SerializedName("book_slug") val bookSlug: String? = null,
    @SerializedName("list_id") val listId: String? = null,
    @SerializedName("list_title") val listTitle: String? = null,
    @SerializedName("created_at") val createdAt: String = ""
) {
    val verbPhrase: String
        get() = when (activityType) {
            "book_read" -> "finished"
            "book_shelved" -> "shelved"
            "book_reading" -> "started reading"
            "book_tbr" -> "wants to read"
            "diary_entry" -> "wrote about"
            "book_liked" -> "liked"
            "list_created" -> "made a list"
            "list_saved" -> "saved a list"
            "follow" -> "followed"
            else -> activityType.replace('_', ' ')
        }

    val objectTitle: String? get() = bookTitle ?: listTitle

    val displayName: String
        get() = if (name.isEmpty()) username else name.substringBefore(' ')
}

data class FollowingActivitiesResponse(
    val activities: List<ActivityItem> = emptyList(),
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0
)

/** Body for POST /api/v1/events (impression tracking). */
data class TrackEventBody(
    @SerializedName("event_type") val eventType: String,
    @SerializedName("book_id") val bookId: String? = null,
    val metadata: Map<String, String> = emptyMap()
)
