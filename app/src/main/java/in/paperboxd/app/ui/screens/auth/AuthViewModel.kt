package `in`.paperboxd.app.ui.screens.auth

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.auth.google.GoogleSignInHelper
import `in`.paperboxd.app.auth.google.GoogleSignInResult
import `in`.paperboxd.app.data.repository.AuthRepository
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class AuthMode { Login, LoginOtp, Register }

data class AuthUiState(
    val mode: AuthMode = AuthMode.Login,
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val otpCode: String = "",
    val otpEmail: String = "",
    val otpCountdown: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/** Emitted once when auth succeeds; the container forwards to AppState.signedIn. */
data class AuthSuccess(val token: String, val user: User)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val googleSignInHelper: GoogleSignInHelper
) : ViewModel() {

    private val _state = MutableStateFlow(AuthUiState())
    val state: StateFlow<AuthUiState> = _state.asStateFlow()

    private val _authSuccess = MutableSharedFlow<AuthSuccess>(extraBufferCapacity = 1)
    val authSuccess: SharedFlow<AuthSuccess> = _authSuccess.asSharedFlow()

    private var countdownJob: Job? = null

    fun onEmailChange(v: String) = _state.update { it.copy(email = v) }
    fun onPasswordChange(v: String) = _state.update { it.copy(password = v) }
    fun onConfirmPasswordChange(v: String) = _state.update { it.copy(confirmPassword = v) }

    fun onOtpChange(v: String) {
        val digits = v.filter { it.isDigit() }.take(6)
        _state.update { it.copy(otpCode = digits) }
        if (digits.length == 6) verifyOtp()
    }

    fun switchTo(next: AuthMode) {
        _state.update {
            it.copy(
                mode = next,
                errorMessage = null,
                successMessage = null,
                password = if (next != AuthMode.LoginOtp) "" else it.password,
                confirmPassword = "",
                otpCode = ""
            )
        }
    }

    fun login() {
        val s = _state.value
        val email = s.email.trim().lowercase()
        if (email.isEmpty() || s.password.isEmpty()) {
            _state.update { it.copy(errorMessage = "Email and password are required.") }
            return
        }
        withLoading {
            authRepository.login(email, s.password).fold(
                onSuccess = { resp -> _authSuccess.tryEmit(AuthSuccess(resp.token, resp.user)) },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
        }
    }

    fun register() {
        val s = _state.value
        val email = s.email.trim().lowercase()
        if (email.isEmpty() || s.password.isEmpty()) {
            _state.update { it.copy(errorMessage = "Email and password are required.") }
            return
        }
        if (s.password != s.confirmPassword) {
            _state.update { it.copy(errorMessage = "Passwords do not match.") }
            return
        }
        if (PasswordStrength.from(s.password) == PasswordStrength.Weak) {
            _state.update {
                it.copy(errorMessage = "Password must be at least 8 characters with upper, lower, and a digit.")
            }
            return
        }
        withLoading {
            authRepository.register(email, s.password).fold(
                onSuccess = { resp -> _authSuccess.tryEmit(AuthSuccess(resp.token, resp.user)) },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
        }
    }

    fun sendOtp() {
        val email = _state.value.email.trim().lowercase()
        if (email.isEmpty()) {
            _state.update { it.copy(errorMessage = "Enter your email to receive a code.") }
            return
        }
        withLoading {
            authRepository.sendOtp(email).fold(
                onSuccess = { resp ->
                    _state.update {
                        it.copy(
                            mode = AuthMode.LoginOtp,
                            otpEmail = email,
                            otpCode = "",
                            errorMessage = null,
                            successMessage = null
                        )
                    }
                    startCountdown(resp.expiresInSeconds)
                },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
        }
    }

    fun verifyOtp() {
        val s = _state.value
        val code = s.otpCode.trim()
        if (code.length != 6) {
            _state.update { it.copy(errorMessage = "Enter the 6-digit code.") }
            return
        }
        withLoading {
            authRepository.verifyOtp(s.otpEmail, code).fold(
                onSuccess = { resp ->
                    stopCountdown()
                    _authSuccess.tryEmit(AuthSuccess(resp.token, resp.user))
                },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
        }
    }

    fun forgotPassword() {
        val email = _state.value.email.trim().lowercase()
        if (email.isEmpty()) {
            _state.update { it.copy(errorMessage = "Enter your email first.") }
            return
        }
        withLoading {
            authRepository.forgotPassword(email).fold(
                onSuccess = {
                    _state.update { it.copy(successMessage = "Reset link sent — check your inbox.") }
                },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
        }
    }

    /**
     * Google Sign-In: get an ID token via Credential Manager, then reuse the
     * existing backend exchange + AppState routing. On success emits AuthSuccess
     * (same path as email/OTP login). User-cancelled picker is a silent no-op;
     * genuine failures surface an error, matching the other auth flows here.
     */
    fun loginWithGoogle(activityContext: Context) {
        withLoading {
            when (val result = googleSignInHelper.getIdToken(activityContext)) {
                is GoogleSignInResult.Cancelled -> Unit // silent, matches iOS
                is GoogleSignInResult.Failure ->
                    _state.update { it.copy(errorMessage = result.message) }
                is GoogleSignInResult.Success ->
                    authRepository.googleAuth(result.idToken).fold(
                        onSuccess = { resp -> _authSuccess.tryEmit(AuthSuccess(resp.token, resp.user)) },
                        onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
                    )
            }
        }
    }

    private fun startCountdown(seconds: Int) {
        stopCountdown()
        _state.update { it.copy(otpCountdown = maxOf(seconds, 0)) }
        countdownJob = viewModelScope.launch {
            while (_state.value.otpCountdown > 0) {
                delay(1_000)
                _state.update { it.copy(otpCountdown = it.otpCountdown - 1) }
            }
        }
    }

    private fun stopCountdown() {
        countdownJob?.cancel()
        countdownJob = null
    }

    private fun withLoading(block: suspend () -> Unit) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            block()
            _state.update { it.copy(isLoading = false) }
        }
    }

    override fun onCleared() {
        stopCountdown()
        super.onCleared()
    }
}

enum class PasswordStrength(val label: String) {
    Weak("Weak"), Fair("Fair"), Strong("Strong");

    val color: Color
        get() = when (this) {
            Weak -> ErrorColor
            Fair, Strong -> Accent
        }

    companion object {
        fun from(password: String): PasswordStrength {
            val hasUpper = password.any { it.isUpperCase() }
            val hasLower = password.any { it.isLowerCase() }
            val hasDigit = password.any { it.isDigit() }
            val long = password.length >= 12
            if (password.length < 8 || !(hasUpper && hasLower && hasDigit)) return Weak
            if (long && password.any { !it.isLetterOrDigit() }) return Strong
            return Fair
        }
    }
}
