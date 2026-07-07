package `in`.paperboxd.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.DarkPasswordField
import `in`.paperboxd.app.ui.components.DarkTextField
import `in`.paperboxd.app.ui.components.GhostButton
import `in`.paperboxd.app.ui.components.PrimaryButton
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.TextSecondary
import androidx.compose.ui.tooling.preview.Preview
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme

@Composable
fun AuthScreen(
    onSignedIn: (token: String, user: User) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    // Activity context for the Credential Manager picker UI.
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.authSuccess.collect { onSignedIn(it.token, it.user) }
    }

    AuthContent(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onOtpChange = viewModel::onOtpChange,
        onLogin = viewModel::login,
        onRegister = viewModel::register,
        onSendOtp = viewModel::sendOtp,
        onForgotPassword = viewModel::forgotPassword,
        onLoginWithGoogle = { viewModel.loginWithGoogle(context) },
        onSwitchMode = viewModel::switchTo
    )
}

@Composable
fun AuthContent(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onOtpChange: (String) -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onSendOtp: () -> Unit,
    onForgotPassword: () -> Unit,
    onLoginWithGoogle: () -> Unit,
    onSwitchMode: (AuthMode) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 44.sp,
            color = Accent
        )
        Spacer(Modifier.height(32.dp))

        when (state.mode) {
            AuthMode.Login -> LoginForm(
                state = state,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onLogin = onLogin,
                onLoginWithGoogle = onLoginWithGoogle,
                onSendOtp = onSendOtp,
                onForgotPassword = onForgotPassword,
                onSwitchToRegister = { onSwitchMode(AuthMode.Register) }
            )

            AuthMode.Register -> RegisterForm(
                state = state,
                onEmailChange = onEmailChange,
                onPasswordChange = onPasswordChange,
                onConfirmPasswordChange = onConfirmPasswordChange,
                onRegister = onRegister,
                onSwitchToLogin = { onSwitchMode(AuthMode.Login) }
            )

            AuthMode.LoginOtp -> OtpForm(
                state = state,
                onOtpChange = onOtpChange,
                onResendOtp = onSendOtp,
                onSwitchToLogin = { onSwitchMode(AuthMode.Login) }
            )
        }

        state.errorMessage?.let {
            Spacer(Modifier.height(14.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = ErrorColor)
        }
        state.successMessage?.let {
            Spacer(Modifier.height(14.dp))
            Text(it, style = MaterialTheme.typography.bodyMedium, color = Accent)
        }
    }
}

@Composable
private fun LoginForm(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onLoginWithGoogle: () -> Unit,
    onSendOtp: () -> Unit,
    onForgotPassword: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    DarkTextField(
        value = state.email,
        onValueChange = onEmailChange,
        label = stringResource(R.string.auth_email),
        keyboardType = KeyboardType.Email,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    DarkPasswordField(
        value = state.password,
        onValueChange = onPasswordChange,
        label = stringResource(R.string.auth_password),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(20.dp))
    PrimaryButton(
        text = stringResource(R.string.auth_sign_in),
        onClick = onLogin,
        loading = state.isLoading
    )
    Spacer(Modifier.height(10.dp))
    GhostButton(
        text = stringResource(R.string.auth_google),
        onClick = onLoginWithGoogle
    )
    Spacer(Modifier.height(6.dp))
    TextButton(onClick = onSendOtp) {
        Text(stringResource(R.string.auth_login_with_otp), color = TextSecondary)
    }
    TextButton(onClick = onForgotPassword) {
        Text(stringResource(R.string.auth_forgot_password), color = TextSecondary)
    }
    TextButton(onClick = onSwitchToRegister) {
        Text(stringResource(R.string.auth_no_account), color = Accent)
    }
}

@Composable
private fun RegisterForm(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    DarkTextField(
        value = state.email,
        onValueChange = onEmailChange,
        label = stringResource(R.string.auth_email),
        keyboardType = KeyboardType.Email,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
    DarkPasswordField(
        value = state.password,
        onValueChange = onPasswordChange,
        label = stringResource(R.string.auth_password),
        modifier = Modifier.fillMaxWidth()
    )
    if (state.password.isNotEmpty()) {
        val strength = PasswordStrength.from(state.password)
        Spacer(Modifier.height(6.dp))
        Text(
            text = stringResource(R.string.auth_password_strength, strength.label),
            style = MaterialTheme.typography.bodySmall,
            color = strength.color,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Spacer(Modifier.height(12.dp))
    DarkPasswordField(
        value = state.confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = stringResource(R.string.auth_confirm_password),
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(20.dp))
    PrimaryButton(
        text = stringResource(R.string.auth_create_account),
        onClick = onRegister,
        loading = state.isLoading
    )
    Spacer(Modifier.height(6.dp))
    TextButton(onClick = onSwitchToLogin) {
        Text(stringResource(R.string.auth_have_account), color = Accent)
    }
}

@Composable
private fun OtpForm(
    state: AuthUiState,
    onOtpChange: (String) -> Unit,
    onResendOtp: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    Text(
        text = stringResource(R.string.auth_otp_sent_to, state.otpEmail),
        style = MaterialTheme.typography.bodyMedium,
        color = TextSecondary
    )
    Spacer(Modifier.height(20.dp))
    OtpBoxes(code = state.otpCode, onCodeChange = onOtpChange)
    Spacer(Modifier.height(20.dp))
    if (state.otpCountdown > 0) {
        Text(
            text = stringResource(R.string.auth_resend_in, state.otpCountdown),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    } else {
        TextButton(onClick = onResendOtp) {
            Text(stringResource(R.string.auth_resend_code), color = Accent)
        }
    }
    Spacer(Modifier.height(6.dp))
    TextButton(onClick = onSwitchToLogin) {
        Text(stringResource(R.string.auth_login_with_password), color = TextSecondary)
    }
}

@Preview
@Composable
private fun AuthPreview() {
    PaperBoxdTheme {
        AuthContent(
            state = AuthUiState(mode = AuthMode.Login),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onOtpChange = {},
            onLogin = {},
            onRegister = {},
            onSendOtp = {},
            onForgotPassword = {},
            onLoginWithGoogle = {},
            onSwitchMode = {}
        )
    }
}
