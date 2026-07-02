package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Flat recommendation from GET /recommendations/home or /recommendations/similar.
 * Maps service.BookCandidate.
 */
data class RecommendationItem(
    val id: String,
    val title: String,
    val authors: List<String> = emptyList(),
    @SerializedName("cover_url") val coverUrl: String? = null,
    val categories: List<String> = emptyList(),
    @SerializedName("similarity_score") val similarityScore: Double? = null,
    val reason: String? = null,
    @SerializedName("reasonType") val reasonType: String? = null
) {
    val authorLine: String get() = authors.joinToString(", ")
}

data class HomeRecommendationsResponse(
    val recommendations: List<RecommendationItem> = emptyList(),
    val source: String? = null
)

data class SimilarBooksResponse(
    val similar: List<RecommendationItem> = emptyList()
)
