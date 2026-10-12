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
    @SerializedName("created_at") val createdAt: String = "",
    /** Per-type extras from the Go row; `rating` on "rated" / "finished_reading". */
    val metadata: ActivityMetadata? = null,
    /** Only the community feed sends a cover inline. */
    @SerializedName("book_cover") val bookCover: String? = null
) {
    /**
     * Verb for "<name> <verb> <title>". Types are the ones the Go backend writes
     * (handler/bookshelf.go shelfActivityType, thoughts.go, lists.go); the fallback
     * turns any newer type into readable words.
     */
    val verbPhrase: String
        get() {
            val stars = metadata?.rating
                ?.coerceIn(0, 5)
                ?.takeIf { it > 0 }
                ?.let { " " + "★".repeat(it) }
                .orEmpty()
            return when (activityType) {
                "finished_reading" -> "finished$stars"
                "started_reading" -> "started reading"
                "wants_to_read" -> "wants to read"
                "rated" -> "rated$stars"
                "added_book" -> "shelved"
                "created_thought" -> "wrote about"
                "liked_thought" -> "liked a thought on"
                "reposted_thought" -> "reposted a thought on"
                "created_list" -> "made a list"
                "shared_list" -> "shared a list"
                "shared_book" -> "shared"
                "fusion_joined" -> "fused with you. Tap to play your Fusion"
                else -> activityType.replace('_', ' ')
            }
        }

    val objectTitle: String? get() = bookTitle ?: listTitle

    val displayName: String
        get() = if (name.isEmpty()) username else name.substringBefore(' ')
}

data class ActivityMetadata(
    val rating: Int? = null,
    /** Set on "fusion_joined": the Fusion the tap should open. */
    @SerializedName("fusion_id") val fusionId: String? = null
)

data class FollowingActivitiesResponse(
    val activities: List<ActivityItem> = emptyList(),
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0
)

/**
 * GET /api/v1/community — public "what is happening" snapshot. Only the parts
 * the home screen renders are modelled; lists and readers ship on web first.
 */
data class CommunityResponse(
    @SerializedName("trending_books") val trendingBooks: List<Book> = emptyList(),
    val activity: List<ActivityItem> = emptyList(),
    /**
     * Phase 6 discovery shelves. Each entry carries a server-authored `label`
     * stating the number that earned its place ("shelved by 7 readers this
     * week"), so no client has to invent the claim.
     */
    val rising: List<CommunityShelfBook> = emptyList(),
    @SerializedName("most_tbr") val mostTbr: List<CommunityShelfBook> = emptyList(),
    @SerializedName("hidden_gems") val hiddenGems: List<CommunityShelfBook> = emptyList()
    ,
    /** Right Now on Paperboxd. Empty when too little is happening; show `activity` instead. */
    val now: List<NowCard> = emptyList()
)

/** One "Right Now" event. The headline is server-authored so every client makes the same claim. */
data class NowCard(
    val kind: String = "",
    val headline: String = "",
    val count: Int = 0,
    val book: Book? = null,
    @SerializedName("list_id") val listId: String? = null,
    @SerializedName("thought_id") val thoughtId: String? = null,
    val username: String? = null,
    val excerpt: String? = null
)

/**
 * A book on a discovery shelf. The Go payload inlines the book fields next to
 * `label`, so this mirrors the parts the home carousel needs rather than
 * embedding Book (Gson has no flattening).
 */
data class CommunityShelfBook(
    val id: String,
    val slug: String? = null,
    val volumeInfo: VolumeInfo,
    val label: String = ""
) {
    val title: String get() = volumeInfo.title
    val authors: List<String> get() = volumeInfo.authors ?: emptyList()
    /** Mirrors Book.coverUrl: largest first, forced to HTTPS. */
    val coverUrl: String?
        get() {
            val links = volumeInfo.imageLinks
            val raw = links?.large ?: links?.medium ?: links?.thumbnail
                ?: links?.small ?: links?.smallThumbnail
            return raw?.replace("http://", "https://")
        }
}

/**
 * GET /api/v1/books/{id}/social — what Paperboxd readers did with a book.
 * `books.average_rating` is the publisher's number; this one is ours.
 */
data class BookSocialResponse(
    val readers: BookReaderStats = BookReaderStats(),
    val friends: List<BookSocialFriend> = emptyList(),
    @SerializedName("thoughts_count") val thoughtsCount: Int = 0
)

data class BookSocialFriend(
    @SerializedName("user_id") val userId: String = "",
    val username: String = "",
    val name: String? = null,
    val status: String = "",
    val rating: Int? = null
)

data class BookReaderStats(
    val rating: Double? = null,
    @SerializedName("ratings_count") val ratingsCount: Int = 0,
    val reads: Int = 0,
    val reading: Int = 0,
    val tbr: Int = 0,
    @SerializedName("tbr_30d") val tbr30d: Int = 0
)

/** Body for POST /api/v1/events (impression tracking). */
data class TrackEventBody(
    @SerializedName("event_type") val eventType: String,
    @SerializedName("book_id") val bookId: String? = null,
    val metadata: Map<String, String> = emptyMap(),
    /** events.source — the backend defaults a missing value to "web". */
    val source: String = "mobile"
)
