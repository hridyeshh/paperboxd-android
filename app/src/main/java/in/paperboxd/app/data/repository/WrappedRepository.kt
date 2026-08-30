package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.Wrapped
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WrappedRepository @Inject constructor(
    private val api: ApiService
) {
    /**
     * [month] is YYYY-MM; null asks for the month in progress. The device zone
     * goes along so the backend buckets days and hours on the reader's clock.
     */
    suspend fun monthly(month: String? = null): Result<Wrapped> =
        safeApiCall { api.wrapped(tz = TimeZone.getDefault().id, month = month) }
}
