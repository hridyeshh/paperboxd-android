package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.FollowingActivitiesResponse
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

    suspend fun similar(bookId: String): Result<SimilarBooksResponse> =
        safeApiCall { api.similarBooks(bookId) }

    suspend fun followingActivities(pageSize: Int = 10): Result<FollowingActivitiesResponse> =
        safeApiCall { api.followingActivities(pageSize = pageSize) }

    /** Fire-and-forget impression tracking — never blocks or surfaces errors. */
    fun trackImpression(bookId: String, source: String = "home_feed") {
        fireAndForgetScope.launch {
            runCatching {
                api.trackEvent(
                    TrackEventBody(
                        eventType = "book_impression",
                        bookId = bookId,
                        metadata = mapOf("source" to source)
                    )
                )
            }
        }
    }
}
