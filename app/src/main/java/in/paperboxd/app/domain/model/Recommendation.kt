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
    @SerializedName("reasonType") val reasonType: String? = null,
    /** Confidence tier as a sentence ("Wild card"); the number is never sent. */
    val confidence: String? = null
) {
    val authorLine: String get() = authors.joinToString(", ")
}

/**
 * One section of the modular home. Mirrors `service.FeedModule`: the server
 * writes the title and the reason on every book, so all three clients say
 * the same thing and copy changes without a release.
 */
data class FeedModule(
    val kind: String = "",
    val title: String = "",
    val subtitle: String? = null,
    val books: List<RecommendationItem> = emptyList()
)

/** Response from GET /recommendations/feed. */
data class FeedResponse(
    val greeting: String? = null,
    val modules: List<FeedModule> = emptyList()
)

data class HomeRecommendationsResponse(
    val recommendations: List<RecommendationItem> = emptyList(),
    val source: String? = null
)

data class SimilarBooksResponse(
    val similar: List<RecommendationItem> = emptyList()
)
