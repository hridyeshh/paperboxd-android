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
