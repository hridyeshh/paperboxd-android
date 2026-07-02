package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** POST /api/v1/scan/analyze response. */
data class ScanAnalyzeResponse(
    val book: ScanBook,
    val score: ScanScore,
    val sources: ScanSources? = null,
    @SerializedName("scans_remaining") val scansRemaining: Int? = null
)

data class ScanBook(
    val isbn: String = "",
    val title: String,
    val authors: List<String> = emptyList(),
    val genres: List<String> = emptyList(),
    val pages: Int = 0,
    val description: String? = null,
    @SerializedName("cover_url") val coverUrl: String? = null
)

data class ScanScore(
    @SerializedName("overall_score") val overallScore: Int,
    val dimensions: ScanDimensions,
    val verdict: String = "",
    @SerializedName("for_you") val forYou: List<String> = emptyList(),
    @SerializedName("against_you") val againstYou: List<String> = emptyList(),
    @SerializedName("one_line") val oneLine: String = ""
)

/** Each dimension is scored out of 20 — normalize by 20 for radar axes. */
data class ScanDimensions(
    @SerializedName("genre_fit") val genreFit: Int = 0,
    @SerializedName("writing_style") val writingStyle: Int = 0,
    @SerializedName("length_complexity") val lengthComplexity: Int = 0,
    @SerializedName("community_love") val communityLove: Int = 0,
    @SerializedName("personal_fit") val personalFit: Int = 0
)

/** Real source counts for the analyzing screen. */
data class ScanSources(
    val readers: Int = 0,
    val ratings: Int = 0,
    val rating: Double? = null,
    val shelf: Int = 0,
    val friends: Int = 0
)

data class ScanAnalyzeBody(val isbn: String)
