package `in`.paperboxd.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    /** Triggers the same forgot-password email flow used on the auth screen. */
    suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        api.forgotPassword(mapOf("email" to email))
        Unit
    }

    /** DELETE /api/v1/users/me — caller signs out on success. */
    suspend fun deleteAccount(): Result<Unit> = runCatching {
        api.deleteMe()
        Unit
    }
}
