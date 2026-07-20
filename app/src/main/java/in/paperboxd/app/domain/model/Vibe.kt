package `in`.paperboxd.app.domain.model

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
    val matchReason: String = ""
) {
    val book: Book get() = Book(id, slug, volumeInfo, paperboxdStats, null, apiSource)

    /** 0…1 similarity rendered as the card's "% match" pill. */
    val matchPercent: Int get() = (similarityScore * 100).roundToInt().coerceIn(0, 100)
}

/** Response from POST /api/v1/search/vibe. */
data class VibeSearchResponse(
    val query: String = "",
    val personalised: Boolean = false,
    val totalItems: Int = 0,
    val items: List<VibeMatch>? = null
)
