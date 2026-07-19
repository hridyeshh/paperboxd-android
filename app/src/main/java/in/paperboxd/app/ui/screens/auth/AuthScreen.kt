package `in`.paperboxd.app.ui.screens.auth

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.BookCoverColumns
import `in`.paperboxd.app.ui.components.BrutalCard
import `in`.paperboxd.app.ui.components.LegalBottomSheet
import `in`.paperboxd.app.ui.components.LegalDoc
import `in`.paperboxd.app.ui.components.BrutalPrimaryButton
import `in`.paperboxd.app.ui.components.BrutalSecureField
import `in`.paperboxd.app.ui.components.BrutalTextField
import `in`.paperboxd.app.ui.components.DarkWash
import `in`.paperboxd.app.ui.components.GoogleButton
import `in`.paperboxd.app.ui.components.MonoLabel
import `in`.paperboxd.app.ui.components.OrDivider
import `in`.paperboxd.app.ui.components.Wordmark
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.PBSans
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme

private val CardHorizontal = 20.dp
private fun w(a: Float) = Color.White.copy(alpha = a)
private val SuccessGreen = Color(red = 0.3f, green = 0.85f, blue = 0.5f)

@Composable
fun AuthScreen(
    onSignedIn: (token: String, user: User) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
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
        onSwitchMode = viewModel::switchTo,
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
    onSwitchMode: (AuthMode) -> Unit,
) {
    Box(Modifier.fillMaxSize().background(Background)) {
        BookCoverColumns(Modifier.fillMaxSize())
        DarkWash(Modifier.fillMaxSize())

        Crossfade(targetState = state.mode, animationSpec = tween(250), label = "authMode") { mode ->
            when (mode) {
                AuthMode.Login -> LoginForm(
                    state = state,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onLogin = onLogin,
                    onLoginWithGoogle = onLoginWithGoogle,
                    onForgotPassword = onForgotPassword,
                    onSwitchToRegister = { onSwitchMode(AuthMode.Register) },
                )

                AuthMode.Register -> RegisterForm(
                    state = state,
                    onEmailChange = onEmailChange,
                    onPasswordChange = onPasswordChange,
                    onConfirmPasswordChange = onConfirmPasswordChange,
                    onRegister = onRegister,
                    onLoginWithGoogle = onLoginWithGoogle,
                    onSwitchToLogin = { onSwitchMode(AuthMode.Login) },
                )

                AuthMode.LoginOtp -> OtpForm(
                    state = state,
                    onOtpChange = onOtpChange,
                    onResendOtp = onSendOtp,
                    onSwitchToLogin = { onSwitchMode(AuthMode.Login) },
                )
            }
        }
    }
}

// --- Shared card chrome ---

@Composable
private fun TopBar(eyebrow: String?, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.padding(horizontal = 26.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Wordmark(fontSize = 30)
        Spacer(Modifier.weight(1f))
        if (eyebrow != null) {
            Box(Modifier.border(1.dp, w(0.30f), RectangleShape).padding(horizontal = 10.dp, vertical = 5.dp)) {
                MonoLabel(eyebrow, color = w(0.55f))
            }
        }
    }
}

@Composable
private fun CardHeader(title: String, subtitle: String) {
    Column {
        Text(
            text = title,
            fontFamily = PBSans,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            lineHeight = 34.sp,
            letterSpacing = (-0.8).sp,
            color = Color.White,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = subtitle,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            lineHeight = 18.sp,
            color = w(0.62f),
        )
    }
}

@Composable
private fun FeedbackRow(state: AuthUiState) {
    when {
        state.errorMessage != null -> {
            Spacer(Modifier.height(10.dp))
            Text(state.errorMessage, color = ErrorColor, fontSize = 13.sp)
        }
        state.successMessage != null -> {
            Spacer(Modifier.height(10.dp))
            Text(state.successMessage, color = SuccessGreen, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LinkText(prefix: String, action: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(prefix, color = w(0.48f), fontSize = 13.sp)
        Text(
            action,
            color = w(0.90f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}

// --- Login ---

@Composable
private fun LoginForm(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onLoginWithGoogle: () -> Unit,
    onForgotPassword: () -> Unit,
    onSwitchToRegister: () -> Unit,
) {
    Box(Modifier.fillMaxSize().systemBarsPadding()) {
        TopBar(
            eyebrow = stringResource(R.string.auth_eyebrow_signin),
            modifier = Modifier.align(Alignment.TopStart).fillMaxWidth(),
        )

        BrutalCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = CardHorizontal)
                .padding(bottom = 28.dp)
                .imePadding(),
        ) {
            Column(Modifier.padding(horizontal = 24.dp).padding(top = 28.dp, bottom = 26.dp)) {
                CardHeader(stringResource(R.string.auth_login_title), stringResource(R.string.auth_login_subtitle))
                Spacer(Modifier.height(24.dp))

                MonoLabel(stringResource(R.string.auth_email))
                Spacer(Modifier.height(6.dp))
                BrutalTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = stringResource(R.string.auth_email_placeholder),
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                )

                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MonoLabel(stringResource(R.string.auth_password))
                    Spacer(Modifier.weight(1f))
                    Text(
                        stringResource(R.string.auth_forgot_password),
                        color = w(0.65f),
                        fontSize = 11.5f.sp,
                        modifier = Modifier.clickable(onClick = onForgotPassword),
                    )
                }
                Spacer(Modifier.height(6.dp))
                BrutalSecureField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = stringResource(R.string.auth_password_placeholder),
                    imeAction = ImeAction.Go,
                    keyboardActions = KeyboardActions(onGo = { onLogin() }),
                )

                FeedbackRow(state)

                Spacer(Modifier.height(18.dp))
                BrutalPrimaryButton(
                    text = stringResource(R.string.auth_sign_in),
                    onClick = onLogin,
                    enabled = state.email.isNotEmpty() && state.password.isNotEmpty(),
                    loading = state.isLoading,
                )

                Spacer(Modifier.height(14.dp))
                OrDivider()
                Spacer(Modifier.height(14.dp))
                GoogleButton(text = stringResource(R.string.auth_google), onClick = onLoginWithGoogle, enabled = !state.isLoading)

                Spacer(Modifier.height(18.dp))
                LinkText(
                    prefix = stringResource(R.string.auth_new_here),
                    action = stringResource(R.string.auth_create_account_link),
                    onClick = onSwitchToRegister,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

// --- Register ---

@Composable
private fun RegisterForm(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onLoginWithGoogle: () -> Unit,
    onSwitchToLogin: () -> Unit,
) {
    var acceptedTerms by remember { mutableStateOf(false) }
    var legalSheet by remember { mutableStateOf<LegalDoc?>(null) }
    val canSubmit = state.email.isNotEmpty() &&
        state.password.isNotEmpty() &&
        state.password == state.confirmPassword &&
        PasswordStrength.from(state.password) != PasswordStrength.Weak &&
        acceptedTerms

    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(eyebrow = stringResource(R.string.auth_eyebrow_register), modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(12.dp))

        BrutalCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp),
        ) {
            Column(Modifier.padding(24.dp)) {
                CardHeader(stringResource(R.string.auth_register_title), stringResource(R.string.auth_register_subtitle))
                Spacer(Modifier.height(24.dp))

                MonoLabel(stringResource(R.string.auth_email))
                Spacer(Modifier.height(6.dp))
                BrutalTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = stringResource(R.string.auth_email_placeholder),
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                )

                Spacer(Modifier.height(12.dp))
                MonoLabel(stringResource(R.string.auth_password))
                Spacer(Modifier.height(6.dp))
                BrutalSecureField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = stringResource(R.string.auth_password_min),
                    imeAction = ImeAction.Next,
                )
                if (state.password.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))
                    StrengthBar(PasswordStrength.from(state.password))
                }

                Spacer(Modifier.height(12.dp))
                MonoLabel(stringResource(R.string.auth_confirm_password))
                Spacer(Modifier.height(6.dp))
                BrutalSecureField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    placeholder = stringResource(R.string.auth_confirm_placeholder),
                    imeAction = ImeAction.Go,
                    keyboardActions = KeyboardActions(onGo = { if (canSubmit) onRegister() }),
                )

                Spacer(Modifier.height(16.dp))
                TermsConsent(
                    checked = acceptedTerms,
                    onCheckedChange = { acceptedTerms = it },
                    onOpenTerms = { legalSheet = LegalDoc.Terms },
                    onOpenPrivacy = { legalSheet = LegalDoc.Privacy },
                )

                FeedbackRow(state)

                Spacer(Modifier.height(18.dp))
                BrutalPrimaryButton(
                    text = stringResource(R.string.auth_create_account),
                    onClick = onRegister,
                    enabled = canSubmit,
                    loading = state.isLoading,
                )

                Spacer(Modifier.height(14.dp))
                OrDivider()
                Spacer(Modifier.height(14.dp))
                GoogleButton(text = stringResource(R.string.auth_google), onClick = onLoginWithGoogle, enabled = !state.isLoading && acceptedTerms)

                Spacer(Modifier.height(18.dp))
                LinkText(
                    prefix = stringResource(R.string.auth_already_reading),
                    action = stringResource(R.string.auth_sign_in_link),
                    onClick = onSwitchToLogin,
                )
            }
        }

        legalSheet?.let { doc ->
            LegalBottomSheet(doc = doc, onDismiss = { legalSheet = null })
        }
    }
}

@Composable
private fun TermsConsent(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = w(0.5f),
                checkmarkColor = Color.Black,
            ),
        )
        Spacer(Modifier.width(4.dp))
        Text(
            buildAnnotatedString {
                append("I agree to the ")
                withLink(
                    LinkAnnotation.Clickable(
                        "terms",
                        TextLinkStyles(SpanStyle(color = Accent, textDecoration = TextDecoration.Underline)),
                    ) { _ -> onOpenTerms() },
                ) { append("Terms of Service") }
                append(" and ")
                withLink(
                    LinkAnnotation.Clickable(
                        "privacy",
                        TextLinkStyles(SpanStyle(color = Accent, textDecoration = TextDecoration.Underline)),
                    ) { _ -> onOpenPrivacy() },
                ) { append("Privacy Policy") }
                append(".")
            },
            color = w(0.6f),
            fontSize = 12.5.sp,
        )
    }
}

@Composable
private fun StrengthBar(strength: PasswordStrength) {
    val filled = when (strength) {
        PasswordStrength.Weak -> 1
        PasswordStrength.Fair -> 2
        PasswordStrength.Strong -> 3
    }
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { i ->
            Box(
                Modifier
                    .height(3.dp)
                    .width(28.dp)
                    .background(if (i < filled) strength.color else w(0.16f)),
            )
        }
        Spacer(Modifier.width(2.dp))
        Text(strength.label, color = w(0.55f), fontSize = 11.sp)
    }
}

// --- OTP ---

@Composable
private fun OtpForm(
    state: AuthUiState,
    onOtpChange: (String) -> Unit,
    onResendOtp: () -> Unit,
    onSwitchToLogin: () -> Unit,
) {
    Box(Modifier.fillMaxSize().systemBarsPadding()) {
        TopBar(eyebrow = null, modifier = Modifier.align(Alignment.TopStart).fillMaxWidth())

        BrutalCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(bottom = 32.dp)
                .imePadding(),
        ) {
            Column(Modifier.padding(horizontal = 24.dp).padding(top = 28.dp, bottom = 26.dp)) {
                Text(
                    text = stringResource(R.string.auth_otp_title),
                    fontFamily = PBSans,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    letterSpacing = (-0.8).sp,
                    color = Color.White,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.auth_otp_sent_to, state.otpEmail),
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 14.sp,
                    color = w(0.7f),
                )

                Spacer(Modifier.height(24.dp))
                OtpBoxes(code = state.otpCode, onCodeChange = onOtpChange)

                FeedbackRow(state)

                Spacer(Modifier.height(20.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (state.otpCountdown > 0) {
                        Text(
                            stringResource(R.string.auth_otp_expires_in, formatCountdown(state.otpCountdown)),
                            color = w(0.5f),
                            fontSize = 13.sp,
                        )
                        Spacer(Modifier.weight(1f))
                        Text(stringResource(R.string.auth_resend_code), color = w(0.25f), fontSize = 13.sp)
                    } else {
                        Text(stringResource(R.string.auth_otp_expired), color = ErrorColor, fontSize = 13.sp)
                        Spacer(Modifier.weight(1f))
                        Text(
                            stringResource(R.string.auth_resend_code),
                            color = Accent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable(onClick = onResendOtp),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.auth_login_with_password),
                    color = w(0.55f),
                    fontSize = 13.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.fillMaxWidth().clickable(onClick = onSwitchToLogin),
                )
            }
        }
    }
}

private fun formatCountdown(totalSeconds: Int): String {
    val m = totalSeconds / 60
    val s = totalSeconds % 60
    return "%d:%02d".format(m, s)
}

@Preview
@Composable
private fun AuthPreview() {
    PaperBoxdTheme {
        AuthContent(
            state = AuthUiState(mode = AuthMode.Login, email = "reader@paperboxd.com"),
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onOtpChange = {},
            onLogin = {},
            onRegister = {},
            onSendOtp = {},
            onForgotPassword = {},
            onLoginWithGoogle = {},
            onSwitchMode = {},
        )
    }
}
