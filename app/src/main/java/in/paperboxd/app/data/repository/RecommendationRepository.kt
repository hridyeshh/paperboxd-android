package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.CommunityResponse
import `in`.paperboxd.app.domain.model.FollowingActivitiesResponse
import `in`.paperboxd.app.domain.model.FeedResponse
import `in`.paperboxd.app.domain.model.HomeRecommendationsResponse
import `in`.paperboxd.app.domain.model.SimilarBooksResponse
import `in`.paperboxd.app.domain.model.TrackEventBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecommendationRepository @Inject constructor(
    private val api: ApiService
) {
    private val fireAndForgetScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun home(): Result<HomeRecommendationsResponse> = safeApiCall { api.homeRecommendations() }

    suspend fun feed(tz: String?): Result<FeedResponse> = safeApiCall { api.feed(tz) }

    suspend fun similar(bookId: String): Result<SimilarBooksResponse> =
        safeApiCall { api.similarBooks(bookId) }

    suspend fun followingActivities(pageSize: Int = 10): Result<FollowingActivitiesResponse> =
        safeApiCall { api.followingActivities(pageSize = pageSize) }

    suspend fun community(): Result<CommunityResponse> = safeApiCall { api.community() }

    /**
     * Fire-and-forget verdict on a card. The five verdicts mirror
     * service.Verdict* on the backend; reasonType is the card's reason bucket
     * so the discovery funnel can see which *explanations* readers reject.
     */
    fun giveVerdict(bookId: String, verdict: String, reasonType: String?) {
        fireAndForgetScope.launch {
            runCatching {
                api.recommendationFeedback(
                    buildMap {
                        put("book_id", bookId)
                        put("verdict", verdict)
                        reasonType?.let { put("reason_type", it) }
                    }
                )
            }
        }
    }

    /**
     * Fire-and-forget impression tracking — never blocks or surfaces errors.
     *
     * "rec_impression" is the canonical name in the backend taxonomy; the old
     * "book_impression" is still accepted as an alias for shipped builds.
     * reasonType is what /analytics/discovery groups the funnel by — an
     * impression sent without one lands in the 'unknown' bucket and cannot be
     * compared against any other reason.
     */
    fun trackImpression(bookId: String, source: String = "home_feed", reasonType: String? = null) {
        fireAndForgetScope.launch {
            runCatching {
                api.trackEvent(
                    TrackEventBody(
                        eventType = "rec_impression",
                        bookId = bookId,
                        metadata = mapOf(
                            "source" to source,
                            "reason_type" to (reasonType ?: "unknown")
                        )
                    )
                )
            }
        }
    }
}
