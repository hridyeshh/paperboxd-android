package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName
import kotlin.math.roundToInt

/**
 * One vibe-search match: the book plus why Jazy picked it.
 * Maps `types.VibeBookResult` — the score rides on the same object as the book
 * fields, so this is flat, not nested. iOS twin: `VibeMatch`.
 */
data class VibeMatch(
    val id: String,
    val slug: String? = null,
    val volumeInfo: VolumeInfo,
    val paperboxdStats: PaperboxdStats? = null,
    val apiSource: String? = null,
    val similarityScore: Double = 0.0,
    val matchReason: String = "",
    /** One honest note on what might not land. Empty when Claude is unavailable. */
    val matchCaveat: String = "",
    /**
     * Claude's own match number, so the pill and [matchReason] make the same
     * claim. Null against a backend running without an Anthropic key — Gson
     * leaves absent fields null regardless of the Kotlin default.
     */
    @SerializedName("matchPercent") val serverMatchPercent: Int? = null
) {
    val book: Book get() = Book(id, slug, volumeInfo, paperboxdStats, null, apiSource)

    /** The card's "% match" pill — Claude's number, else raw cosine similarity. */
    val matchPercent: Int
        get() = (serverMatchPercent ?: (similarityScore * 100).roundToInt()).coerceIn(0, 100)
}

/** Response from POST /api/v1/search/vibe. */
data class VibeSearchResponse(
    val query: String = "",
    val personalised: Boolean = false,
    val totalItems: Int = 0,
    val items: List<VibeMatch>? = null
)

/**
 * One turn with Jazy: either a deck, or one clarifying question.
 *
 * Mirrors `service.ConciergeResponse`. [question] is set when Jazy wants one
 * thing settled before answering; the client re-sends the same query with the
 * tapped option as `answer`.
 */
data class JazyTurn(
    val kind: String = "",
    @SerializedName("sessionId") val sessionId: String = "",
    val query: String = "",
    val question: String? = null,
    val options: List<String>? = null,
    val intro: String? = null,
    val personalised: Boolean = false,
    val items: List<VibeMatch>? = null,
    /** The accumulated ask ("like Murakami · under 300 pages"); [refined] when this turn edited the last. */
    val understood: String? = null,
    val refined: Boolean = false
) {
    val isQuestion: Boolean get() = kind == "jazy#question" && question != null
}

/** Response from GET /api/v1/subscriptions/me and the link endpoints. */
data class SubscriptionStatus(
    val active: Boolean = false,
    @SerializedName("scans_remaining") val scansRemaining: Int? = null,
    @SerializedName("scans_unlimited") val scansUnlimited: Boolean? = null
)

/** One TBR book, ranked — mirrors `service.SmartTBRItem`. */
data class SmartTbrItem(
    val id: String = "",
    val title: String = "",
    val authors: List<String> = emptyList(),
    @SerializedName("cover_url") val coverUrl: String? = null,
    val reason: String? = null,
    val confidence: String? = null,
    val bucket: String = "",
    @SerializedName("days_on_shelf") val daysOnShelf: Int = 0,
    @SerializedName("page_count") val pageCount: Int? = null,
    val lines: List<String> = emptyList()
)

/** GET /api/v1/users/me/tbr/smart. */
data class SmartTbr(
    val next: SmartTbrItem? = null,
    val queue: List<SmartTbrItem> = emptyList(),
    val buckets: Map<String, List<SmartTbrItem>> = emptyMap(),
    val total: Int = 0
)
