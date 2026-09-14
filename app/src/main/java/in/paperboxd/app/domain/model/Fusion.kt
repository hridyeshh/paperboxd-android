package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

// Fusion — two readers' shelves side by side, made from a one-time link.
// Shapes mirror internal/service/fusion.go and fusion_view.go in the backend,
// and Models/Fusion.swift on iOS field for field.

data class FusionPerson(
    @SerializedName("user_id") val userId: String = "",
    val username: String = "",
    val name: String = "",
    val first: String = "",
    @SerializedName("avatar_url") val avatarUrl: String? = null
) {
    val initial: String get() = (first.firstOrNull() ?: username.firstOrNull() ?: '?').uppercase()
    val displayName: String get() = name.ifEmpty { username }
}

data class FusionInvite(
    val token: String = "",
    val url: String = "",
    @SerializedName("expires_at") val expiresAt: String = ""
) {
    /** Whole days left, for "6 days left". Never below 1 while the link is live. */
    val daysLeft: Int
        get() = runCatching {
            val secs = java.time.Instant.parse(expiresAt).epochSecond - java.time.Instant.now().epochSecond
            maxOf(1, Math.ceil(secs / 86_400.0).toInt())
        }.getOrDefault(7)
}

data class FusionInvitePreview(
    /** valid, expired, used, own, joined, unavailable */
    val status: String = "unavailable",
    val inviter: FusionPerson? = null,
    @SerializedName("fusion_id") val fusionId: String? = null
)

data class FusionAcceptResponse(@SerializedName("fusion_id") val fusionId: String = "")

data class FusionSummary(
    val id: String = "",
    val them: FusionPerson = FusionPerson(),
    val score: Int? = null,
    @SerializedName("created_at") val createdAt: String = ""
)

data class FusionList(
    val invite: FusionInvite? = null,
    val fusions: List<FusionSummary> = emptyList()
)

data class FusionBook(
    val id: String = "",
    val title: String = "",
    val slug: String? = null,
    val authors: List<String> = emptyList(),
    @SerializedName("cover_url") val coverUrl: String? = null,
    val you: Int? = null,
    val them: Int? = null,
    val reason: String? = null,
    val match: String? = null
) {
    val author: String get() = authors.firstOrNull().orEmpty()
}

data class FusionAgree(
    /** "axis" (a taste axis) or "genre" (shelf-share fallback). */
    val kind: String = "",
    val key: String = "",
    val phrase: String = "",
    val you: Int = 0,
    val them: Int = 0
)

data class FusionSplit(val label: String = "", val you: String = "", val them: String = "")

/** who is "both", "you" or "them". */
data class FusionReason(val who: String = "", val text: String = "")

data class FusionPick(
    val book: FusionBook = FusionBook(),
    val label: String = "",
    val reasons: List<FusionReason> = emptyList()
)

/**
 * The whole story from the viewer's side. Empty lists and null picks mean "no
 * honest content" — the player skips those pages.
 */
data class FusionStory(
    val id: String = "",
    val me: FusionPerson = FusionPerson(),
    val them: FusionPerson = FusionPerson(),
    val score: Int = 0,
    @SerializedName("score_line") val scoreLine: String = "",
    @SerializedName("shared_count") val sharedCount: Int = 0,
    val agreement: Int = 0,
    @SerializedName("loved_count") val lovedCount: Int = 0,
    @SerializedName("shared_authors") val sharedAuthors: List<String> = emptyList(),
    @SerializedName("shared_genres") val sharedGenres: List<String> = emptyList(),
    @SerializedName("shared_loved") val sharedLoved: List<FusionBook> = emptyList(),
    val agree: List<FusionAgree> = emptyList(),
    val splits: List<FusionSplit> = emptyList(),
    val disagreement: FusionBook? = null,
    @SerializedName("for_you") val forYou: List<FusionBook> = emptyList(),
    @SerializedName("for_them") val forThem: List<FusionBook> = emptyList(),
    val pick: FusionPick? = null,
    val also: List<FusionBook> = emptyList(),
    val wildcard: FusionPick? = null,
    val both: List<FusionBook> = emptyList(),
    @SerializedName("waiting_count") val waitingCount: Int = 0,
    val verdict: String = "",
    @SerializedName("verdict_sub") val verdictSub: String = "",
    @SerializedName("low_data") val lowData: Boolean = false
)
