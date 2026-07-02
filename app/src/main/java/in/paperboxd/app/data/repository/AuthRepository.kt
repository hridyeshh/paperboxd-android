package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.local.SecurePrefs
import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.AuthResponse
import `in`.paperboxd.app.domain.model.AvatarUploadResponse
import `in`.paperboxd.app.domain.model.CheckUsernameResponse
import `in`.paperboxd.app.domain.model.GoogleAuthResponse
import `in`.paperboxd.app.domain.model.HealthResponse
import `in`.paperboxd.app.domain.model.MobileUserResponse
import `in`.paperboxd.app.domain.model.OnboardingBody
import `in`.paperboxd.app.domain.model.OtpSendResponse
import `in`.paperboxd.app.domain.model.RefreshResponse
import `in`.paperboxd.app.domain.model.User
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val api: ApiService,
    private val securePrefs: SecurePrefs
) {
    suspend fun health(): Result<HealthResponse> = safeApiCall { api.health() }

    suspend fun login(email: String, password: String): Result<AuthResponse> =
        safeApiCall { api.login(mapOf("email" to email, "password" to password)) }

    suspend fun register(email: String, password: String): Result<AuthResponse> =
        safeApiCall { api.register(mapOf("email" to email, "password" to password)) }

    suspend fun sendOtp(email: String): Result<OtpSendResponse> =
        safeApiCall { api.sendOtp(mapOf("email" to email)) }

    suspend fun verifyOtp(email: String, code: String): Result<AuthResponse> =
        safeApiCall { api.verifyOtp(mapOf("email" to email, "code" to code)) }

    suspend fun googleAuth(idToken: String): Result<GoogleAuthResponse> =
        safeApiCall { api.googleAuth(mapOf("id_token" to idToken)) }

    suspend fun refresh(): Result<RefreshResponse> = safeApiCall { api.refresh() }

    suspend fun forgotPassword(email: String): Result<Unit> =
        safeApiCall { api.forgotPassword(mapOf("email" to email)); Unit }

    suspend fun checkUsername(username: String): Result<CheckUsernameResponse> =
        safeApiCall { api.checkUsername(username) }

    /** PATCH /api/mobile/users/me — claim username and/or display name. */
    suspend fun updateMobileMe(fields: Map<String, String>): Result<MobileUserResponse> =
        safeApiCall { api.updateMobileMe(fields) }

    suspend fun saveOnboarding(genres: List<String>): Result<Unit> =
        safeApiCall { api.saveOnboarding(OnboardingBody(genres)); Unit }

    suspend fun uploadAvatar(bytes: ByteArray, fileName: String = "avatar.jpg"): Result<AvatarUploadResponse> =
        safeApiCall {
            val part = MultipartBody.Part.createFormData(
                "file", fileName, bytes.toRequestBody("image/jpeg".toMediaType())
            )
            api.uploadAvatar(part)
        }

    suspend fun deleteAccount(): Result<Unit> = safeApiCall { api.deleteMe(); Unit }

    // Session persistence
    fun cachedToken(): String? = securePrefs.getToken()
    fun cachedUser(): User? = securePrefs.getUser()
    fun persistSession(token: String, user: User) {
        securePrefs.saveToken(token)
        securePrefs.saveUser(user)
    }
    fun persistToken(token: String) = securePrefs.saveToken(token)
    fun persistUser(user: User) = securePrefs.saveUser(user)
    fun clearSession() = securePrefs.clearAll()
}
