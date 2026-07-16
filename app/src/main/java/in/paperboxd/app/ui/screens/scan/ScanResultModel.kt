package `in`.paperboxd.app.ui.screens.scan

import `in`.paperboxd.app.domain.model.ScanAnalyzeResponse

/**
 * Everything the reveal + breakdown screens render — iOS `ScanResult` twin.
 * Built from the backend response; no hardcoded book or score.
 */
data class ScanResult(
    val isbn: String,
    val title: String,
    val author: String,
    val pages: Int,
    val genres: List<String>,
    val coverUrl: String?,
    val matchScore: Int,            // 0...100
    val verdict: String,
    val oneLine: String,
    val dimensions: List<Dimension>, // five radar axes
    val forYou: List<String>,
    val againstYou: List<String>,
    /** "The internet" rating (e.g. Goodreads avg). Not always supplied. */
    val internetRating: Double?,
    /** Human ratings count, e.g. "23.4k". */
    val ratingsCount: String?,
    /** Real source counts shown on the analyzing screen (from the backend). */
    val readersCount: Int,
    val communityRatings: Int,
    val shelfCount: Int,
    val friendsCount: Int
) {
    data class Dimension(val name: String, val value: Double) // value 0...1

    /** Reveal sub-line: anchors the personal score against the crowd's average. */
    val verdictSub: String
        get() = internetRating?.let {
            "Goodreads says ${"%.2f".format(it)}★ — but this is read against your shelf."
        } ?: oneLine

    /** "Why this score, for you" — positives first (✓), then caveats (✗). */
    val reasons: List<Pair<Boolean, String>>
        get() = forYou.map { true to it } + againstYou.map { false to it }

    companion object {
        fun from(response: ScanAnalyzeResponse): ScanResult {
            val d = response.score.dimensions
            return ScanResult(
                isbn = response.book.isbn,
                title = response.book.title,
                author = response.book.authors.firstOrNull() ?: "Unknown",
                pages = response.book.pages,
                genres = response.book.genres,
                coverUrl = response.book.coverUrl,
                matchScore = response.score.overallScore,
                verdict = response.score.verdict,
                oneLine = response.score.oneLine,
                // Each Claude dimension is scored out of 20, so normalize by 20 to
                // fill the radar (0...1).
                dimensions = listOf(
                    Dimension("Genre fit", d.genreFit / 20.0),
                    Dimension("Writing", d.writingStyle / 20.0),
                    Dimension("Depth", d.lengthComplexity / 20.0),
                    Dimension("Community", d.communityLove / 20.0),
                    Dimension("For you", d.personalFit / 20.0),
                ),
                forYou = response.score.forYou,
                againstYou = response.score.againstYou,
                internetRating = response.sources?.rating?.takeIf { it > 0 },
                ratingsCount = compactCount(response.sources?.ratings ?: 0),
                readersCount = response.sources?.readers ?: 0,
                communityRatings = response.sources?.ratings ?: 0,
                shelfCount = response.sources?.shelf ?: 0,
                friendsCount = response.sources?.friends ?: 0
            )
        }

        /** Compact human count, e.g. 1896 → "1.9k", 662 → "662". Null for 0. */
        fun compactCount(n: Int): String? {
            if (n <= 0) return null
            if (n < 1000) return "$n"
            val k = n / 1000.0
            return if (k < 10) "%.1fk".format(k) else "%.0fk".format(k)
        }
    }
}

/** One title-search result usable by the scan flow — iOS `ScanSearchHit` twin. */
data class ScanSearchHit(
    val id: String,
    val title: String,
    val author: String,
    val isbn: String?,
    val coverUrl: String?
)
