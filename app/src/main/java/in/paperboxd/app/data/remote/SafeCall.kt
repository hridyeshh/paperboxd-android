package `in`.paperboxd.app.data.remote

import com.google.gson.JsonParseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

/**
 * Runs an API call on IO and maps failures to [ApiError] so repositories
 * return Result<T> and never throw to the UI layer.
 */
suspend fun <T> safeApiCall(block: suspend () -> T): Result<T> = withContext(Dispatchers.IO) {
    try {
        Result.success(block())
    } catch (e: HttpException) {
        val body = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        }
        Result.failure(ApiError.fromResponse(e.code(), body))
    } catch (e: IOException) {
        Result.failure(ApiError.NetworkError(e.message ?: "Network error — check your connection"))
    } catch (e: JsonParseException) {
        Result.failure(ApiError.DecodingError(e.message ?: "Failed to decode response"))
    } catch (e: Exception) {
        Result.failure(ApiError.Unknown(e.message ?: "Something went wrong", "UNKNOWN"))
    }
}
