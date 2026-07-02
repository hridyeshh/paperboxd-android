package `in`.paperboxd.app.data.remote

import com.google.gson.JsonObject
import com.google.gson.JsonParser

/**
 * Typed errors surfaced to repositories/UI. Maps the backend envelope
 * `{error, code, message?}` (plus the older nested `{error:{code,message}}`).
 */
sealed class ApiError(override val message: String) : Exception(message) {
    class Unauthorized(message: String) : ApiError(message)
    class Forbidden(message: String) : ApiError(message)
    class NotFound(message: String) : ApiError(message)
    class ValidationError(message: String) : ApiError(message)
    class RateLimited(message: String) : ApiError(message)
    class ServerError(message: String, val code: String) : ApiError(message)
    class NetworkError(message: String) : ApiError(message)
    class DecodingError(message: String) : ApiError(message)
    class Unknown(message: String, val code: String) : ApiError(message)

    companion object {
        private const val FALLBACK = "Something went wrong — please try again"

        /** Builds a typed error from an HTTP status + raw error body. */
        fun fromResponse(status: Int, errorBody: String?): ApiError {
            val envelope = parseEnvelope(errorBody)
            val msg = displayString(envelope)
            return when (envelope.code) {
                "UNAUTHORIZED", "INVALID_TOKEN", "EXPIRED_TOKEN" -> Unauthorized(msg)
                "FORBIDDEN" -> Forbidden(msg)
                "NOT_FOUND" -> NotFound(msg)
                "VALIDATION_ERROR" -> ValidationError(msg)
                "RATE_LIMITED" -> RateLimited(msg)
                "INTERNAL_ERROR" -> ServerError(msg, envelope.code)
                else -> when {
                    status == 401 -> Unauthorized(msg)
                    status == 403 -> Forbidden(msg)
                    status == 404 -> NotFound(msg)
                    status == 429 -> RateLimited(msg)
                    status in 500..599 -> ServerError(msg, envelope.code ?: "INTERNAL_ERROR")
                    status in 400..499 -> ValidationError(msg)
                    else -> Unknown(msg, envelope.code ?: "UNKNOWN")
                }
            }
        }

        private fun displayString(e: Envelope): String {
            e.message?.takeIf { it.isNotEmpty() }?.let { return it }
            friendlyForCode(e.error ?: e.code)?.let { return it }
            e.error?.takeIf { it.isNotEmpty() }?.let { return it }
            return FALLBACK
        }

        /** Friendly copy for known machine codes (scan flow). */
        private fun friendlyForCode(code: String?): String? = when (code) {
            "book_not_found" -> "Couldn't find this book — try searching by title"
            "scans_exhausted" -> "You've used all your free scans"
            "scoring_failed" -> "Something took too long — your scan hasn't been used"
            else -> null
        }

        /** Tolerates both flat {error, code, message} and nested {error:{code,message}}. */
        private fun parseEnvelope(body: String?): Envelope {
            if (body.isNullOrEmpty()) return Envelope()
            return try {
                val root = JsonParser.parseString(body).asJsonObject
                var error: String? = null
                var code: String? = null
                var message: String? = null

                val errorEl = root.get("error")
                when {
                    errorEl == null -> {}
                    errorEl.isJsonPrimitive -> error = errorEl.asString
                    errorEl.isJsonObject -> {
                        val obj = errorEl.asJsonObject
                        code = obj.stringOrNull("code")
                        message = obj.stringOrNull("message")
                    }
                }
                root.stringOrNull("code")?.let { code = it }
                root.stringOrNull("message")?.takeIf { it.isNotEmpty() }?.let { message = it }
                Envelope(error, code, message)
            } catch (_: Exception) {
                Envelope()
            }
        }

        private fun JsonObject.stringOrNull(key: String): String? {
            val el = get(key) ?: return null
            return if (el.isJsonPrimitive) el.asString else null
        }
    }

    private data class Envelope(
        val error: String? = null,
        val code: String? = null,
        val message: String? = null
    )
}
