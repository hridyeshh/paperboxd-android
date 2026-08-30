package `in`.paperboxd.app.data.remote

import `in`.paperboxd.app.BuildConfig
import `in`.paperboxd.app.config.Config
import `in`.paperboxd.app.data.local.SecurePrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App-wide session signals. MainActivity collects [expired] and routes to auth
 * — the Android twin of the iOS `.paperboxdSessionExpired` notification.
 */
@Singleton
class SessionEvents @Inject constructor() {
    private val _expired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val expired: SharedFlow<Unit> = _expired.asSharedFlow()

    fun emitExpired() {
        _expired.tryEmit(Unit)
    }
}

/**
 * Host of [Config.BASE_URL]. The session bearer is scoped to it: not every call
 * the app makes goes to the Go backend (password reset goes to the paperboxd.in
 * web proxy), and a backend JWT has no business on another host.
 */
private val backendHost: String = Config.BASE_URL.toHttpUrl().host

/**
 * Injects Authorization: Bearer + User-Agent on every request when a token exists.
 * The bearer is attached only for [backendHost] — see its docs.
 */
class AuthInterceptor @Inject constructor(
    private val securePrefs: SecurePrefs
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val builder = request.newBuilder()
            .header("User-Agent", Config.userAgent)
            .header("Accept", "application/json")
        if (request.url.host == backendHost) {
            securePrefs.getToken()?.takeIf { it.isNotEmpty() }?.let {
                builder.header("Authorization", "Bearer $it")
            }
        }
        return chain.proceed(builder.build())
    }
}

/**
 * On a 401 from our backend: try the refresh token FIRST, and only sign the user
 * out if that also fails. Scoped to [backendHost] — only that host can judge our
 * session, so a 401 from elsewhere must not sign the user out.
 *
 * The refresh token is opaque and lives in the backend database, so unlike the
 * access token it does not depend on JWT_SECRET. That is what lets a session
 * survive a backend redeploy that rotates the signing secret; clearing
 * credentials on the first 401, as this used to, made every such deploy look
 * like a forced logout.
 */
class UnauthorizedInterceptor @Inject constructor(
    private val securePrefs: SecurePrefs,
    private val sessionEvents: SessionEvents
) : Interceptor {

    private val refreshLock = Any()

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        if (response.code != 401 || request.url.host != backendHost) return response
        // The refresh call itself 401ing means the session is genuinely gone.
        if (request.url.encodedPath.endsWith(REFRESH_PATH)) {
            securePrefs.clearAll()
            sessionEvents.emitExpired()
            return response
        }

        val staleToken = request.header("Authorization")?.removePrefix("Bearer ")?.trim()
        val newToken = synchronized(refreshLock) {
            // Another request may have refreshed while this one was in flight —
            // in that case just retry with what is already stored.
            val current = securePrefs.getToken()
            if (current != null && current != staleToken) current else refreshBlocking()
        } ?: run {
            securePrefs.clearAll()
            sessionEvents.emitExpired()
            return response
        }

        response.close()
        return chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer $newToken")
                .build()
        )
    }

    /** Exchanges the stored refresh token for a new pair. Returns null on failure. */
    private fun refreshBlocking(): String? {
        val refreshToken = securePrefs.getRefreshToken() ?: return null
        return runCatching {
            val body = JSONObject().put("refresh_token", refreshToken).toString()
                .toRequestBody("application/json".toMediaType())
            // A bare client: going through the shared one would recurse into this
            // interceptor.
            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()
            val call = client.newCall(
                okhttp3.Request.Builder()
                    .url("${Config.BASE_URL}$REFRESH_PATH")
                    .post(body)
                    .build()
            )
            call.execute().use { resp ->
                if (!resp.isSuccessful) return null
                val json = JSONObject(resp.body?.string().orEmpty())
                val token = json.optString("token").takeIf { it.isNotBlank() } ?: return null
                securePrefs.saveToken(token)
                json.optString("refresh_token").takeIf { it.isNotBlank() }
                    ?.let(securePrefs::saveRefreshToken)
                token
            }
        }.getOrNull()
    }

    private companion object {
        const val REFRESH_PATH = "/api/mobile/auth/refresh"
    }
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        unauthorizedInterceptor: UnauthorizedInterceptor
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .addInterceptor(unauthorizedInterceptor)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            })
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("${Config.BASE_URL}/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)
}
