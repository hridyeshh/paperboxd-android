package `in`.paperboxd.app.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.paperboxd.app.config.Config
import `in`.paperboxd.app.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encrypted storage for the JWT + cached session user. Android twin of the iOS
 * KeychainManager: same two keys, user serialized as JSON.
 */
@Singleton
class SecurePrefs @Inject constructor(
    @ApplicationContext context: Context
) {
    private val gson = Gson()

    private val prefs: SharedPreferences = run {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        EncryptedSharedPreferences.create(
            context,
            Config.SECURE_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun saveUser(user: User) {
        prefs.edit().putString(KEY_USER, gson.toJson(user)).apply()
    }

    fun getUser(): User? {
        val json = prefs.getString(KEY_USER, null) ?: return null
        return runCatching { gson.fromJson(json, User::class.java) }.getOrNull()
    }

    fun clearAll() {
        prefs.edit().remove(KEY_TOKEN).remove(KEY_USER).apply()
    }

    private companion object {
        const val KEY_TOKEN = "auth.token"
        const val KEY_USER = "auth.user"
    }
}
