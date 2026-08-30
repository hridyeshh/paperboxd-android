package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/**
 * User payload the mobile auth endpoints return. Mirrors backend mobileUser
 * (internal/auth/mobile.go). `username` nullable: register may not include one
 * yet — the app then routes to onboarding.
 */
data class User(
    val id: String,
    val username: String?,
    val email: String,
    val name: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val level: Int? = null,
    val xp: Int? = null,
    @SerializedName("onboarding_completed") val onboardingCompleted: Boolean? = null
)

/** Login / OTP verify success. */
data class AuthResponse(
    val token: String,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    val user: User
)

data class GoogleAuthResponse(
    val token: String,
    @SerializedName("refresh_token") val refreshToken: String? = null,
    val user: User,
    @SerializedName("is_new_user") val isNewUser: Boolean
)

/** POST /api/mobile/auth/otp/send */
data class OtpSendResponse(
    val message: String,
    @SerializedName("expires_in_seconds") val expiresInSeconds: Int
)

/** POST /api/mobile/auth/refresh */
data class RefreshResponse(
    val token: String,
    @SerializedName("refresh_token") val refreshToken: String? = null
)

/** GET /api/health */
data class HealthResponse(
    val status: String,
    val version: String,
    val timestamp: String
)

/** GET /api/v1/auth/check-username?username=X */
data class CheckUsernameResponse(
    val available: Boolean,
    val username: String,
    val reason: String? = null
)

/** PATCH /api/mobile/users/me → { user } */
data class MobileUserResponse(
    val user: User
)
