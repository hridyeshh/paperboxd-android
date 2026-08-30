package `in`.paperboxd.app.ui.screens.settings

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.config.Config
import `in`.paperboxd.app.data.remote.ApiService
import javax.inject.Inject
import `in`.paperboxd.app.data.local.SecurePrefs
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.FollowRequestUser
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Backing calls for the settings screen — mirrors iOS SettingsView's two live
 * endpoints: password reset (email link, since there is no in-app change) and
 * account deletion. Sign-out itself routes through AppState, not here.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val api: ApiService,
    private val userRepository: UserRepository,
    private val securePrefs: SecurePrefs
) : ViewModel() {

    // ── Private profile ───────────────────────────────────────────────────────

    private val _isPublic = MutableStateFlow(true)
    val isPublic: StateFlow<Boolean> = _isPublic.asStateFlow()

    private val _followRequests = MutableStateFlow<List<FollowRequestUser>>(emptyList())
    val followRequests: StateFlow<List<FollowRequestUser>> = _followRequests.asStateFlow()

    init {
        val username = securePrefs.getUser()?.username
        if (username != null) {
            viewModelScope.launch {
                userRepository.profile(username).onSuccess { profile ->
                    _isPublic.value = profile.isPublic
                    if (!profile.isPublic) loadFollowRequests()
                }
            }
        }
    }

    fun setPublic(isPublic: Boolean) {
        val previous = _isPublic.value
        _isPublic.value = isPublic
        viewModelScope.launch {
            userRepository.updateVisibility(isPublic).fold(
                onSuccess = { if (!isPublic) loadFollowRequests() else _followRequests.value = emptyList() },
                onFailure = { _isPublic.value = previous }
            )
        }
    }

    fun loadFollowRequests() {
        viewModelScope.launch {
            userRepository.followRequests().onSuccess { _followRequests.value = it.requests }
        }
    }

    fun respondToRequest(username: String, accept: Boolean) {
        viewModelScope.launch {
            val result = if (accept) {
                userRepository.acceptFollowRequest(username).map { }
            } else {
                userRepository.rejectFollowRequest(username)
            }
            result.onSuccess {
                _followRequests.value = _followRequests.value.filterNot { it.username == username }
            }
        }
    }

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
