package `in`.paperboxd.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.config.Config
import `in`.paperboxd.app.data.remote.ApiService
import javax.inject.Inject

/**
 * Backing calls for the settings screen — mirrors iOS SettingsView's two live
 * endpoints: password reset (email link, since there is no in-app change) and
 * account deletion. Sign-out itself routes through AppState, not here.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val api: ApiService
) : ViewModel() {

    /**
     * Triggers the same forgot-password email flow used on the auth screen —
     * the web proxy, not the backend, since only the proxy sends the email.
     */
    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        api.forgotPassword(
            Config.FORGOT_PASSWORD_URL,
            mapOf("email" to email.trim().lowercase())
        )
        Unit
    }

    /** DELETE /api/v1/users/me with the exit-survey reasons — caller signs out on success. */
    suspend fun deleteAccount(reasons: List<String>): Result<Unit> = runCatching {
        api.deleteMe(mapOf("reasons" to reasons))
        Unit
    }
}
