package `in`.paperboxd.app.ui.screens.auth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.LegalBottomSheet
import `in`.paperboxd.app.ui.components.LegalDoc
import `in`.paperboxd.app.ui.components.GoogleButton
import `in`.paperboxd.app.ui.components.OrDivider
import `in`.paperboxd.app.ui.components.PBPrimaryButton
import `in`.paperboxd.app.ui.components.PBStepHeader
import `in`.paperboxd.app.ui.components.Wordmark
import `in`.paperboxd.app.ui.components.looksLikeEmail
import `in`.paperboxd.app.ui.components.pageDissolve
import `in`.paperboxd.app.ui.components.pageDissolveSupported
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@Composable
fun AuthScreen(
    onSignedIn: (token: String, user: User, refreshToken: String?) -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.authSuccess.collect { onSignedIn(it.token, it.user, it.refreshToken) }
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
    // The whole front door runs on the paper ground (same identity as
    // onboarding right after), so status-bar icons stay dark throughout.
    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as? android.app.Activity ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(activity.window, view)
        val wasLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = true
        onDispose { controller.isAppearanceLightStatusBars = wasLight }
    }

    Box(Modifier.fillMaxSize().background(HL.Paper)) {
        // Every page change burns through like onboarding; heading back towards
        // Welcome plays it in reverse.
        val modeTransition = updateTransition(state.mode, label = "authMode")
        val back = modeTransition.targetState.depth < modeTransition.currentState.depth
        modeTransition.AnimatedContent(
            transitionSpec = {
                if (pageDissolveSupported) EnterTransition.None togetherWith ExitTransition.None
                else fadeIn(tween(250)) togetherWith fadeOut(tween(250))
            },
        ) { mode ->
            Box(Modifier.fillMaxSize().then(pageDissolve(reverse = back))) {
                when (mode) {
                    AuthMode.Welcome -> WelcomeForm(
                        onGetStarted = { onSwitchMode(AuthMode.Register) },
                        onSignIn = { onSwitchMode(AuthMode.Login) },
                    )

                    AuthMode.Login -> LoginForm(
                        state = state,
                        onEmailChange = onEmailChange,
                        onPasswordChange = onPasswordChange,
                        onLogin = onLogin,
                        onLoginWithGoogle = onLoginWithGoogle,
                        onForgotPassword = onForgotPassword,
                        onSwitchToRegister = { onSwitchMode(AuthMode.Register) },
                        onBack = { onSwitchMode(AuthMode.Welcome) },
                    )

                    AuthMode.Register -> RegisterForm(
                        state = state,
                        onEmailChange = onEmailChange,
                        onPasswordChange = onPasswordChange,
                        onConfirmPasswordChange = onConfirmPasswordChange,
                        onRegister = onRegister,
                        onLoginWithGoogle = onLoginWithGoogle,
                        onSwitchToLogin = { onSwitchMode(AuthMode.Login) },
                        onBack = { onSwitchMode(AuthMode.Welcome) },
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
}

/** How deep into the front door a page sits; heading back up plays the dissolve in reverse. */
private val AuthMode.depth: Int
    get() = when (this) {
        AuthMode.Welcome -> 0
        AuthMode.Login, AuthMode.Register -> 1
        AuthMode.LoginOtp -> 2
    }

// --- Welcome ---

@Composable
private fun WelcomeForm(onGetStarted: () -> Unit, onSignIn: () -> Unit) {
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(50)
        appear = true
    }
    val alpha by animateFloatAsState(if (appear) 1f else 0f, tween(600), label = "welcome-alpha")

    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(horizontal = 28.dp)
            .graphicsLayer { this.alpha = alpha },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(1f))
        WelcomeIllustration()
        Spacer(Modifier.height(28.dp))
        Text(
            stringResource(R.string.welcome_title),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = HL.Muted,
        )
        Text(
            "PaperBoxd",
            fontFamily = FontFamily.Cursive,
            fontSize = 44.sp,
            color = HL.Ink,
        )
        Spacer(Modifier.weight(1f))
        PBPrimaryButton(
            title = stringResource(R.string.welcome_get_started),
            onClick = onGetStarted,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            stringResource(R.string.welcome_sign_in),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = HL.Muted,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSignIn,
            ),
        )
        Spacer(Modifier.height(36.dp))
    }
}

@Composable
private fun WelcomeIllustration() {
    Image(
        painter = painterResource(R.drawable.welcome_illustration),
        contentDescription = null,
        modifier = Modifier.width(200.dp),
    )
}

// --- Shared paper chrome ---

/** Back arrow + mono label: [PBStepHeader]'s top row for pages that aren't a numbered signup step. */
@Composable
private fun PaperBackHeader(title: String, onBack: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onBack,
        ),
    ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = HL.Ink, modifier = Modifier.size(14.dp))
        Text(
            title.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = HL.Muted,
        )
    }
}

@Composable
private fun FeedbackRow(state: AuthUiState) {
    when {
        state.errorMessage != null -> {
            Spacer(Modifier.height(10.dp))
            Text(state.errorMessage, color = HL.Accent, fontSize = 13.sp)
        }
        state.successMessage != null -> {
            Spacer(Modifier.height(10.dp))
            Text(state.successMessage, color = HL.Sage, fontSize = 13.sp)
        }
    }
}

@Composable
private fun LinkText(
    prefix: String,
    action: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally)) {
        Text(prefix, color = HL.Muted, fontSize = 13.sp)
        Text(
            action,
            color = HL.Ink,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable(onClick = onClick),
        )
    }
}

// --- Login (paper) ---

@Composable
private fun LoginForm(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLogin: () -> Unit,
    onLoginWithGoogle: () -> Unit,
    onForgotPassword: () -> Unit,
    onSwitchToRegister: () -> Unit,
    onBack: () -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }
    val canSubmit = state.email.isNotEmpty() && state.password.isNotEmpty()

    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(80)
        appear = true
    }

    // Shake the card once per new error, same beat as the iOS shake.
    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            for (target in listOf(-8f, 8f, -6f, 6f, -3f, 0f)) {
                shakeX.animateTo(target, animationSpec = tween(50))
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 28.dp)
            .offset(x = shakeX.value.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        PaperBackHeader(stringResource(R.string.auth_sign_in), onBack)
        Spacer(Modifier.height(22.dp))

        Reveal(appear, delayMillis = 0) {
            Column {
                Text(
                    stringResource(R.string.auth_login_title),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 33.sp,
                    color = HL.Ink,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringResource(R.string.auth_login_subtitle),
                    fontSize = 13.5.sp,
                    lineHeight = 18.sp,
                    color = HL.Muted,
                )
            }
        }
        Spacer(Modifier.height(26.dp))

        Reveal(appear, delayMillis = 100) {
            Column {
                PaperFieldLabel(stringResource(R.string.auth_email))
                Spacer(Modifier.height(6.dp))
                PaperTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    placeholder = stringResource(R.string.auth_email_placeholder_paper),
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Reveal(appear, delayMillis = 140) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PaperFieldLabel(stringResource(R.string.auth_password))
                    Spacer(Modifier.weight(1f))
                    Text(
                        stringResource(R.string.auth_forgot_password),
                        color = HL.Muted,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp,
                        modifier = Modifier.clickable(onClick = onForgotPassword),
                    )
                }
                Spacer(Modifier.height(6.dp))
                PaperTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    placeholder = stringResource(R.string.auth_password_placeholder),
                    isPassword = true,
                    showPassword = showPassword,
                    onToggleShowPassword = { showPassword = !showPassword },
                    imeAction = ImeAction.Go,
                    keyboardActions = KeyboardActions(onGo = { if (canSubmit) onLogin() }),
                )
            }
        }

        FeedbackRow(state)

        Spacer(Modifier.height(20.dp))
        Reveal(appear, delayMillis = 180) {
            PBPrimaryButton(
                title = stringResource(R.string.auth_sign_in),
                onClick = onLogin,
                enabled = canSubmit,
                loading = state.isLoading,
            )
        }

        Reveal(appear, delayMillis = 210) {
            Column {
                Spacer(Modifier.height(16.dp))
                OrDivider()
                Spacer(Modifier.height(16.dp))
                GoogleButton(
                    text = stringResource(R.string.auth_google),
                    onClick = onLoginWithGoogle,
                    enabled = !state.isLoading,
                )
            }
        }

        Reveal(appear, delayMillis = 240) {
            Column {
                Spacer(Modifier.height(18.dp))
                LinkText(
                    prefix = stringResource(R.string.auth_new_here),
                    action = stringResource(R.string.auth_create_account_link),
                    onClick = onSwitchToRegister,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
        Spacer(Modifier.height(32.dp))
    }
}

/**
 * Fade + rise-in gated by a shared [appear] flag with a per-element delay —
 * turns the card from one flat slide-in into fields arriving in sequence.
 * Mirrors iOS's `RevealEffect`.
 */
@Composable
private fun Reveal(appear: Boolean, delayMillis: Int, content: @Composable () -> Unit) {
    val progress by animateFloatAsState(
        targetValue = if (appear) 1f else 0f,
        animationSpec = tween(durationMillis = 420, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label = "reveal",
    )
    Box(Modifier.reveal(appear, delayMillis)) { content() }
}

/** [Reveal]'s animation as a bare `Modifier`, for call sites that already own a layout composable (e.g. a `Row`). */
@Composable
private fun Modifier.reveal(appear: Boolean, delayMillis: Int): Modifier {
    val progress by animateFloatAsState(
        targetValue = if (appear) 1f else 0f,
        animationSpec = tween(durationMillis = 420, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label = "reveal",
    )
    return this.graphicsLayer {
        alpha = progress
        translationY = (1f - progress) * 14.dp.toPx()
    }
}

// --- Register (paper) ---

@Composable
private fun RegisterForm(
    state: AuthUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onRegister: () -> Unit,
    onLoginWithGoogle: () -> Unit,
    onSwitchToLogin: () -> Unit,
    onBack: () -> Unit,
) {
    var showPassword by remember { mutableStateOf(false) }
    var legalSheet by remember { mutableStateOf<LegalDoc?>(null) }
    val canSubmit = looksLikeEmail(state.email) && state.password.length >= 8

    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(80)
        appear = true
    }
    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            for (target in listOf(-8f, 8f, -6f, 6f, -3f, 0f)) {
                shakeX.animateTo(target, animationSpec = tween(50))
            }
        }
    }

    fun submit() {
        onConfirmPasswordChange(state.password)
        onRegister()
    }

    Box(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .offset(x = shakeX.value.dp),
        ) {
            Spacer(Modifier.height(20.dp))
            PBStepHeader(step = 1, onBack = onBack)
            Spacer(Modifier.height(22.dp))

            Reveal(appear, delayMillis = 0) {
                Column {
                    Text(
                        stringResource(R.string.auth_register_title),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        lineHeight = 33.sp,
                        color = HL.Ink,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.auth_register_subtitle),
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        color = HL.Muted,
                    )
                }
            }
            Spacer(Modifier.height(26.dp))

            Reveal(appear, delayMillis = 100) {
                Column {
                    PaperFieldLabel(stringResource(R.string.auth_email))
                    Spacer(Modifier.height(6.dp))
                    PaperTextField(
                        value = state.email,
                        onValueChange = onEmailChange,
                        placeholder = stringResource(R.string.auth_email_placeholder_paper),
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Reveal(appear, delayMillis = 140) {
                Column {
                    PaperFieldLabel(stringResource(R.string.auth_password))
                    Spacer(Modifier.height(6.dp))
                    PaperTextField(
                        value = state.password,
                        onValueChange = onPasswordChange,
                        placeholder = stringResource(R.string.auth_password_min),
                        isPassword = true,
                        showPassword = showPassword,
                        onToggleShowPassword = { showPassword = !showPassword },
                        imeAction = ImeAction.Go,
                        keyboardActions = KeyboardActions(onGo = { if (canSubmit) submit() }),
                    )
                }
            }

            FeedbackRow(state)

            Spacer(Modifier.height(4.dp))
            Reveal(appear, delayMillis = 180) {
                PBPrimaryButton(
                    title = stringResource(R.string.auth_create_account),
                    onClick = { submit() },
                    enabled = canSubmit,
                    loading = state.isLoading,
                )
            }

            Reveal(appear, delayMillis = 210) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    OrDivider()
                    Spacer(Modifier.height(16.dp))
                    GoogleButton(
                        text = stringResource(R.string.auth_google),
                        onClick = onLoginWithGoogle,
                        enabled = !state.isLoading,
                    )
                    Spacer(Modifier.height(10.dp))
                    ContinueWithEmailButton(
                        enabled = !state.isLoading,
                        onClick = { if (canSubmit) submit() },
                    )
                }
            }

            Reveal(appear, delayMillis = 240) {
                LegalFooter(
                    onOpenTerms = { legalSheet = LegalDoc.Terms },
                    onOpenPrivacy = { legalSheet = LegalDoc.Privacy },
                )
            }

            Reveal(appear, delayMillis = 240) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    LinkText(
                        prefix = stringResource(R.string.auth_already_reading),
                        action = stringResource(R.string.auth_sign_in_link),
                        onClick = onSwitchToLogin,
                        accent = false,
                    )
                }
            }
            Spacer(Modifier.height(32.dp))
        }

        legalSheet?.let { doc ->
            LegalBottomSheet(doc = doc, onDismiss = { legalSheet = null })
        }
    }
}

@Composable
private fun PaperFieldLabel(text: String) {
    Text(
        text.uppercase(),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        color = HL.Muted,
    )
}

@Composable
private fun PaperTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isPassword: Boolean = false,
    showPassword: Boolean = false,
    onToggleShowPassword: (() -> Unit)? = null,
) {
    Box(
        modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(HL.Field)
            .border(2.dp, HL.Ink.copy(alpha = 0.30f)),
        contentAlignment = Alignment.CenterStart,
    ) {
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp, color = HL.Ink),
            visualTransformation = if (isPassword && !showPassword) {
                androidx.compose.ui.text.input.PasswordVisualTransformation()
            } else {
                androidx.compose.ui.text.input.VisualTransformation.None
            },
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction,
            ),
            keyboardActions = keyboardActions,
            decorationBox = { inner ->
                Box(Modifier.padding(horizontal = 14.dp)) {
                    if (value.isEmpty()) {
                        Text(placeholder, fontSize = 14.sp, color = HL.Muted)
                    }
                    inner()
                }
            },
            modifier = Modifier.fillMaxWidth().padding(end = if (isPassword) 40.dp else 0.dp),
        )
        if (isPassword && onToggleShowPassword != null) {
            Icon(
                if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                contentDescription = null,
                tint = HL.Muted,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 14.dp)
                    .size(18.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onToggleShowPassword,
                    ),
            )
        }
    }
}

@Composable
private fun ContinueWithEmailButton(enabled: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(HL.Card)
            .border(2.dp, HL.Ink)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
    ) {
        Icon(Icons.Filled.Email, null, tint = HL.Ink, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(10.dp))
        Text(
            stringResource(R.string.auth_continue_with_email),
            fontSize = 14.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = HL.Ink,
        )
    }
}

@Composable
private fun LegalFooter(onOpenTerms: () -> Unit, onOpenPrivacy: () -> Unit) {
    Text(
        buildAnnotatedString {
            append("By continuing you agree to our ")
            withLink(
                LinkAnnotation.Clickable(
                    "terms",
                    TextLinkStyles(SpanStyle(color = HL.Ink, fontWeight = FontWeight.SemiBold)),
                ) { _ -> onOpenTerms() },
            ) { append("terms") }
            append(" and ")
            withLink(
                LinkAnnotation.Clickable(
                    "privacy",
                    TextLinkStyles(SpanStyle(color = HL.Ink, fontWeight = FontWeight.SemiBold)),
                ) { _ -> onOpenPrivacy() },
            ) { append("privacy policy") }
            append(".")
        },
        color = HL.Muted,
        fontSize = 11.5.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp),
    )
}

// --- OTP ---

@Composable
private fun OtpForm(
    state: AuthUiState,
    onOtpChange: (String) -> Unit,
    onResendOtp: () -> Unit,
    onSwitchToLogin: () -> Unit,
) {
    var appear by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(80)
        appear = true
    }
    val shakeX = remember { Animatable(0f) }
    LaunchedEffect(state.errorMessage) {
        if (state.errorMessage != null) {
            for (target in listOf(-8f, 8f, -6f, 6f, -3f, 0f)) {
                shakeX.animateTo(target, animationSpec = tween(50))
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .imePadding()
            .padding(horizontal = 28.dp)
            .offset(x = shakeX.value.dp),
    ) {
        Spacer(Modifier.height(20.dp))
        PaperBackHeader(stringResource(R.string.auth_sign_in), onSwitchToLogin)
        Spacer(Modifier.height(22.dp))

        Reveal(appear, delayMillis = 0) {
            Column {
                Text(
                    text = stringResource(R.string.auth_otp_title),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    lineHeight = 33.sp,
                    color = HL.Ink,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.auth_otp_sent_to, state.otpEmail),
                    color = HL.Muted,
                    fontSize = 13.5.sp,
                )
            }
        }

        Spacer(Modifier.height(28.dp))
        Reveal(appear, delayMillis = 150) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                OtpBoxes(code = state.otpCode, onCodeChange = onOtpChange)
            }
        }

        FeedbackRow(state)

        Spacer(Modifier.height(20.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().reveal(appear, delayMillis = 200),
        ) {
            if (state.otpCountdown > 0) {
                Text(
                    stringResource(R.string.auth_otp_expires_in, formatCountdown(state.otpCountdown)),
                    color = HL.Muted,
                    fontSize = 13.sp,
                )
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.auth_resend_code), color = HL.Muted.copy(alpha = 0.6f), fontSize = 13.sp)
            } else {
                Text(stringResource(R.string.auth_otp_expired), color = HL.Accent, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                Text(
                    stringResource(R.string.auth_resend_code),
                    color = HL.Ink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(onClick = onResendOtp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.auth_login_with_password),
            color = HL.Muted,
            fontSize = 13.sp,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onSwitchToLogin)
                .reveal(appear, delayMillis = 230),
            textAlign = TextAlign.Center,
        )
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
