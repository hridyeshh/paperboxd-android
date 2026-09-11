package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.domain.model.TrackEventBody
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Product analytics. One call → one row in the backend `events` table.
 *
 * Fire and forget: never blocks or surfaces errors. Requires a signed-in
 * session (events.user_id is NOT NULL). Naming is snake_case
 * `<object>_<verb>`, matching web `lib/analytics.ts` and iOS `Analytics`.
 */
@Singleton
class AnalyticsRepository @Inject constructor(
    private val api: ApiService
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun track(eventType: String, bookId: String? = null, metadata: Map<String, String> = emptyMap()) {
        scope.launch {
            runCatching {
                api.trackEvent(
                    TrackEventBody(
                        eventType = eventType,
                        bookId = bookId,
                        metadata = metadata
                    )
                )
            }
        }
    }
}
