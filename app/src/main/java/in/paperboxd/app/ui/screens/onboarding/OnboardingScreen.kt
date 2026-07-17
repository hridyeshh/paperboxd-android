package `in`.paperboxd.app.ui.screens.onboarding

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import `in`.paperboxd.app.domain.model.Genre
import `in`.paperboxd.app.domain.model.ReadingTempo
import `in`.paperboxd.app.domain.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

/**
 * Multi-step onboarding — iOS `OnboardingContainerView` twin: light editorial
 * theme (dark only on the aha-loading step), a staged pill header, serif titles,
 * a dashed avatar ring, availability-coloured username field, genre chips with a
 * live counter, tempo rows, a pulsing dark loading screen, and the first-match
 * reveal. Steps: username → genres → tempo → ahaLoading → ahaReveal.
 */

// iOS light-onboarding palette (white-scale grays + green/red accents).
private object OB {
    val ink = Color(0xFF121212)      // white 0.07
    val sub53 = Color(0xFF878787)
    val sub60 = Color(0xFF999999)
    val sub65 = Color(0xFFA6A6A6)
    val sub50 = Color(0xFF808080)
    val sub45 = Color(0xFF737373)
    val sub40 = Color(0xFF666666)
    val sub25 = Color(0xFF404040)
    val border88 = Color(0xFFE0E0E0)
    val border90 = Color(0xFFE5E5E5)
    val line85 = Color(0xFFD9D9D9)
    val fill92 = Color(0xFFEBEBEB)
    val fill96 = Color(0xFFF5F5F5)
    val fill97 = Color(0xFFF7F7F7)
    val ring = Color(0xFFC7C7C7)     // white 0.78
    val green = Color(0xFF2EAD4D)
    val red = Color(0xFFD13838)
    val darkBg = Color(0xFF0F0F0F)   // white 0.06
    val avatarGradient = Brush.linearGradient(listOf(Color(0xFFF2D19E), Color(0xFF7A52B8)))
}

@Composable
fun OnboardingScreen(
    user: User,
    onUsernameSet: (String) -> Unit,
    onFinished: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var pickedUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(Unit) {
        viewModel.initialUser = user
        viewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.UsernameSet -> onUsernameSet(event.username)
                is OnboardingEvent.Finished -> onFinished()
            }
        }
    }

    val pickAvatar = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pickedUri = uri  // instant local preview, like iOS
            scope.launch {
                val bytes = readAndDownscale(context, uri)
                if (bytes != null) viewModel.uploadAvatar(bytes)
            }
        }
    }

    OnboardingContent(
        state = state,
        avatarModel = pickedUri ?: state.avatarUrl,
        onPickAvatar = {
            pickAvatar.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onUsernameChange = viewModel::onUsernameChange,
        onDisplayNameChange = viewModel::onDisplayNameChange,
        onSubmitUsername = viewModel::submitUsername,
        onToggleGenre = viewModel::toggleGenre,
        onContinueFromGenres = viewModel::continueFromGenres,
        onSelectTempo = viewModel::selectTempo,
        onContinueFromTempo = viewModel::continueFromTempo,
        onSaveToShelf = viewModel::saveToShelf,
        onShowAnother = viewModel::showAnother
    )
}

@Composable
private fun OnboardingContent(
    state: OnboardingUiState,
    avatarModel: Any?,
    onPickAvatar: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSubmitUsername: () -> Unit,
    onToggleGenre: (String) -> Unit,
    onContinueFromGenres: () -> Unit,
    onSelectTempo: (String) -> Unit,
    onContinueFromTempo: () -> Unit,
    onSaveToShelf: () -> Unit,
    onShowAnother: () -> Unit
) {
    val isDark = state.step == OnboardingStep.AhaLoading
    val bg by animateColorAsState(
        if (isDark) OB.darkBg else Color.White,
        animationSpec = tween(400),
        label = "onboarding-bg"
    )

    // Whole-content entrance: fade in, once.
    var contentIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentIn = true }
    val contentAlpha by animateFloatAsState(
        if (contentIn) 1f else 0f, tween(600), label = "content-alpha"
    )

    Box(Modifier.fillMaxSize().background(bg)) {
        Column(Modifier.fillMaxSize().statusBarsPadding().alpha(contentAlpha)) {
            if (state.step != OnboardingStep.AhaLoading) {
                StageHeader(
                    step = state.step,
                    modifier = Modifier.padding(horizontal = 26.dp).padding(top = 18.dp)
                )
            }

            AnimatedContent(
                targetState = state.step,
                transitionSpec = {
                    if (targetState == OnboardingStep.AhaLoading ||
                        initialState == OnboardingStep.AhaLoading
                    ) {
                        fadeIn(tween(400)) togetherWith fadeOut(tween(400))
                    } else {
                        (slideInHorizontally(tween(450)) { it / 2 } + fadeIn(tween(450))) togetherWith
                            (slideOutHorizontally(tween(450)) { -it / 2 } + fadeOut(tween(450)))
                    }
                },
                label = "onboarding-step",
                modifier = Modifier.fillMaxSize()
            ) { step ->
                when (step) {
                    OnboardingStep.Username -> UsernameStep(
                        state, avatarModel, onPickAvatar, onUsernameChange,
                        onDisplayNameChange, onSubmitUsername
                    )
                    OnboardingStep.Genres -> GenresStep(state, onToggleGenre, onContinueFromGenres)
                    OnboardingStep.Tempo -> TempoStep(state, onSelectTempo, onContinueFromTempo)
                    OnboardingStep.AhaLoading -> AhaLoadingStep()
                    OnboardingStep.AhaReveal -> AhaRevealStep(state, onSaveToShelf, onShowAnother)
                }
            }
        }
    }
}

// ── Stage header ─────────────────────────────────────────────────────────────

@Composable
private fun StageHeader(step: OnboardingStep, modifier: Modifier = Modifier) {
    val stages = listOf("Sign up", "Set up", "Aha")
    val activeStage = when (step) {
        OnboardingStep.Username, OnboardingStep.Genres, OnboardingStep.Tempo -> 1
        OnboardingStep.AhaLoading, OnboardingStep.AhaReveal -> 2
    }
    val subStep = when (step) {
        OnboardingStep.Username -> 1
        OnboardingStep.Genres -> 2
        OnboardingStep.Tempo -> 3
        else -> null
    }

    Column(modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            stages.forEachIndexed { idx, label ->
                StagePill(label, done = idx < activeStage, current = idx == activeStage)
                if (idx < stages.size - 1) {
                    Box(
                        Modifier
                            .padding(horizontal = 8.dp)
                            .width(14.dp)
                            .height(1.dp)
                            .background(OB.line85)
                    )
                }
            }
        }
        if (subStep != null) {
            Text(
                "STEP $subStep OF 3",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 1.4.sp,
                color = OB.sub60
            )
        }
    }
}

@Composable
private fun StagePill(label: String, done: Boolean, current: Boolean) {
    val fill = when {
        current -> OB.ink
        done -> OB.fill92
        else -> OB.fill96
    }
    val textColor = when {
        current -> Color.White
        done -> OB.sub45
        else -> OB.sub65
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(fill)
            .padding(horizontal = 11.dp, vertical = 5.dp)
    ) {
        if (done) {
            Icon(Icons.Filled.Check, null, tint = textColor, modifier = Modifier.size(9.dp))
        }
        Text(label, fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = textColor)
    }
}

// ── Shared components ────────────────────────────────────────────────────────

@Composable
private fun OnbTitle(text: String) {
    Text(
        text,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 33.sp,
        color = OB.ink
    )
}

@Composable
private fun OnbSubtitle(text: String) {
    Text(text, fontSize = 13.5.sp, lineHeight = 18.sp, color = OB.sub53)
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text.uppercase(),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        color = OB.sub60
    )
}

/** Dark pill CTA on the light onboarding background — iOS `OnboardingPrimaryButton`. */
@Composable
private fun OnbPrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true
) {
    val alpha by animateFloatAsState(if (enabled) 1f else 0.38f, tween(180), label = "cta-alpha")
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .alpha(alpha)
            .clip(CircleShape)
            .background(OB.ink)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                enabled = enabled && !loading,
                onClick = onClick
            )
    ) {
        if (loading) {
            CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(title, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward, null,
                    tint = Color.White, modifier = Modifier.size(15.dp)
                )
            }
        }
    }
}

/** Selectable genre chip — iOS `OnboardingChip`. */
@Composable
private fun OnbChip(label: String, isOn: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clip(CircleShape)
            .background(if (isOn) OB.ink else OB.fill96)
            .border(1.dp, if (isOn) OB.ink else OB.border88, CircleShape)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        if (isOn) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(11.dp))
        }
        Text(
            label, fontSize = 13.5.sp, fontWeight = FontWeight.Medium,
            color = if (isOn) Color.White else OB.sub25
        )
    }
}

// ── Step 1: Username ─────────────────────────────────────────────────────────

@Composable
private fun UsernameStep(
    state: OnboardingUiState,
    avatarModel: Any?,
    onPickAvatar: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onDisplayNameChange: (String) -> Unit,
    onSubmitUsername: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 26.dp)
            .padding(top = 28.dp, bottom = 48.dp)
    ) {
        OnbTitle("Choose your name\non the shelves.")
        Spacer(Modifier.height(8.dp))
        OnbSubtitle("This is how readers will find and follow you.")
        Spacer(Modifier.height(24.dp))

        AvatarRow(avatarModel, state.isUploadingAvatar, onPickAvatar)
        Spacer(Modifier.height(22.dp))

        FieldLabel("Username")
        Spacer(Modifier.height(6.dp))
        UsernameField(state, onUsernameChange)
        Spacer(Modifier.height(6.dp))
        AvailabilityHint(state)

        Spacer(Modifier.height(18.dp))
        FieldLabel("Display name")
        Spacer(Modifier.height(6.dp))
        DisplayNameField(state.displayName, onDisplayNameChange)

        state.errorMessage?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, fontSize = 12.sp, color = OB.red)
        }

        Spacer(Modifier.height(28.dp))
        OnbPrimaryButton(
            title = "Continue",
            onClick = onSubmitUsername,
            loading = state.isSubmitting,
            enabled = state.availability == UsernameAvailability.Available
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "You can change this any time.",
            fontSize = 13.sp, color = OB.sub65,
            textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun AvatarRow(avatarModel: Any?, uploading: Boolean, onPick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.clickable(onClick = onPick)
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(64.dp).clip(CircleShape).background(OB.avatarGradient)
            ) {
                if (avatarModel != null) {
                    AsyncImage(
                        model = avatarModel,
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                // Dashed ring when empty, solid ring once a picture is set.
                val hasImage = avatarModel != null
                Box(
                    Modifier.fillMaxSize().drawBehind {
                        val stroke = if (hasImage) {
                            Stroke(width = 1.5.dp.toPx())
                        } else {
                            Stroke(
                                width = 1.5.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(
                                    floatArrayOf(4.dp.toPx(), 3.dp.toPx()), 0f
                                )
                            )
                        }
                        drawCircle(
                            color = OB.ring,
                            radius = size.minDimension / 2 - stroke.width / 2,
                            style = stroke
                        )
                    }
                )
                if (uploading) {
                    Box(
                        Modifier.fillMaxSize().clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(22.dp).clip(CircleShape).background(OB.ink)
            ) {
                Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                if (avatarModel == null) "Add a picture" else "Change picture",
                fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = OB.ink
            )
            Text("Optional · square, 400px+", fontSize = 12.sp, color = OB.sub60)
        }
    }
}

@Composable
private fun UsernameField(state: OnboardingUiState, onChange: (String) -> Unit) {
    val borderColor = when (state.availability) {
        is UsernameAvailability.Available -> OB.green
        is UsernameAvailability.Taken, is UsernameAvailability.CheckFailed -> OB.red
        is UsernameAvailability.Checking -> OB.sub60
        UsernameAvailability.Idle -> OB.border88
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
    ) {
        Text(
            "@", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OB.sub40,
            modifier = Modifier.padding(start = 14.dp, end = 2.dp)
        )
        // TextFieldValue overload so the caret survives mid-string edits — the String
        // overload snaps it to the end whenever the sanitized value round-trips through
        // the ViewModel (onUsernameChange lowercases every keystroke).
        var tfv by remember { mutableStateOf(TextFieldValue(state.username)) }
        // Re-sync only on external changes (prefill/reset), never our own edits.
        if (state.username != tfv.text) {
            tfv = tfv.copy(text = state.username, selection = TextRange(state.username.length))
        }
        BasicTextField(
            value = tfv,
            onValueChange = { new ->
                val sanitized = new.text.lowercase() // length-preserving, so caret stays valid
                tfv = new.copy(
                    text = sanitized,
                    selection = TextRange(new.selection.end.coerceAtMost(sanitized.length))
                )
                onChange(sanitized)
            },
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = OB.ink),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Ascii,
                imeAction = ImeAction.Next
            ),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (tfv.text.isEmpty()) {
                        Text("yourname", fontSize = 14.sp, color = OB.sub60)
                    }
                    inner()
                }
            },
            modifier = Modifier.weight(1f).padding(vertical = 13.dp)
        )
        Box(Modifier.padding(horizontal = 14.dp).size(18.dp), contentAlignment = Alignment.Center) {
            when (state.availability) {
                UsernameAvailability.Checking -> CircularProgressIndicator(
                    color = OB.sub60, strokeWidth = 1.5.dp, modifier = Modifier.size(14.dp)
                )
                UsernameAvailability.Available -> Icon(
                    Icons.Filled.Check, null, tint = OB.green, modifier = Modifier.size(18.dp)
                )
                is UsernameAvailability.Taken, is UsernameAvailability.CheckFailed -> Icon(
                    Icons.Filled.Close, null, tint = OB.red, modifier = Modifier.size(18.dp)
                )
                UsernameAvailability.Idle -> {}
            }
        }
    }
}

@Composable
private fun AvailabilityHint(state: OnboardingUiState) {
    when (val a = state.availability) {
        UsernameAvailability.Idle, UsernameAvailability.Checking ->
            Text("Letters, numbers, _ or -", fontSize = 12.sp, color = OB.sub65)
        UsernameAvailability.Available ->
            Text("@${state.username} is available ✓", fontSize = 12.sp, color = OB.green)
        is UsernameAvailability.Taken ->
            Text(a.reason ?: "@${state.username} is taken", fontSize = 12.sp, color = OB.red)
        is UsernameAvailability.CheckFailed ->
            Text(a.message, fontSize = 12.sp, color = OB.red)
    }
}

@Composable
private fun DisplayNameField(value: String, onChange: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, OB.border88, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onChange,
            singleLine = true,
            textStyle = TextStyle(fontSize = 14.sp, color = OB.ink),
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Words,
                imeAction = ImeAction.Done
            ),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) Text("Your name", fontSize = 14.sp, color = OB.sub60)
                    inner()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Step 2: Genres ───────────────────────────────────────────────────────────

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenresStep(
    state: OnboardingUiState,
    onToggleGenre: (String) -> Unit,
    onContinueFromGenres: () -> Unit
) {
    val remaining = (3 - state.selectedGenres.size).coerceAtLeast(0)
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
                .padding(top = 28.dp, bottom = 24.dp)
        ) {
            OnbTitle("What lives on\nyour shelves?")
            Spacer(Modifier.height(8.dp))
            OnbSubtitle("Pick at least three. Your recs are built on these — you can tune later.")
            Spacer(Modifier.height(22.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Genre.all.forEach { genre ->
                    OnbChip(
                        label = genre.label,
                        isOn = genre.id in state.selectedGenres,
                        onClick = { onToggleGenre(genre.id) }
                    )
                }
            }

            Spacer(Modifier.height(18.dp))
            Text(
                (if (remaining == 0) "${state.selectedGenres.size} SELECTED · READY"
                else "${state.selectedGenres.size} SELECTED · PICK $remaining MORE"),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 0.8.sp,
                color = OB.sub50
            )
        }
        OnbPrimaryButton(
            title = "Continue",
            onClick = onContinueFromGenres,
            enabled = state.selectedGenres.size >= 3,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 26.dp)
                .padding(bottom = 16.dp)
        )
    }
}

// ── Step 3: Tempo ────────────────────────────────────────────────────────────

@Composable
private fun TempoStep(
    state: OnboardingUiState,
    onSelectTempo: (String) -> Unit,
    onContinueFromTempo: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
                .padding(top = 28.dp, bottom = 24.dp)
        ) {
            OnbTitle("Set a tempo,\nnot a target.")
            Spacer(Modifier.height(8.dp))
            OnbSubtitle("Helps us pace recommendations. No streaks, no pressure.")
            Spacer(Modifier.height(22.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ReadingTempo.all.forEach { option ->
                    TempoRow(option.label, option.sub, state.tempo == option.id) {
                        onSelectTempo(option.id)
                    }
                }
            }
        }
        OnbPrimaryButton(
            title = "Get my recommendations",
            onClick = onContinueFromTempo,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(horizontal = 26.dp)
                .padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun TempoRow(label: String, sub: String, active: Boolean, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (active) OB.ink else OB.fill97)
            .border(1.dp, if (active) OB.ink else OB.border90, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 15.dp)
    ) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                label, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold,
                color = if (active) Color.White else Color(0xFF1F1F1F)
            )
            Text(
                sub, fontSize = 12.5.sp,
                color = if (active) Color.White.copy(alpha = 0.7f) else OB.sub50
            )
        }
        if (active) {
            Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(15.dp))
        }
    }
}

// ── Aha loading (dark) ───────────────────────────────────────────────────────

@Composable
private fun AhaLoadingStep() {
    val messages = listOf(
        "Mapping your taste…",
        "Scanning 5M+ books…",
        "Weighing your picks…",
        "Almost there…"
    )
    var msgIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1200)
            msgIndex = (msgIndex + 1) % messages.size
        }
    }

    // Pulse driven off the frame clock (immune to the system animator-scale setting).
    var pulse by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(Unit) {
        var start = 0L
        while (true) {
            withFrameNanos { now ->
                if (start == 0L) start = now
                val t = (now - start) / 1_000_000_000f
                pulse = (t % 1.4f) / 1.4f
            }
        }
    }

    // iOS pins the card to the upper third (1 spacer top : 2 bottom), not dead-center.
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.weight(1f))
        Box(contentAlignment = Alignment.Center) {
            // Expanding, fading pulse ring.
            Box(
                Modifier
                    .size(96.dp)
                    .scale(0.95f + 0.20f * pulse)
                    .alpha(0.6f * (1f - pulse))
                    .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
            )
            Box(
                Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.MenuBook, null,
                    tint = Color.White.copy(alpha = 0.65f), modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(Modifier.height(28.dp))
        Text(
            "Finding your book",
            fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
            fontSize = 28.sp, color = Color.White
        )
        Spacer(Modifier.height(10.dp))
        AnimatedContent(
            targetState = msgIndex,
            transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(350)) },
            label = "aha-msg"
        ) { i ->
            Text(messages[i], fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
        }
        Spacer(Modifier.weight(2f))
    }
}

// ── Aha reveal (light) ───────────────────────────────────────────────────────

@Composable
private fun AhaRevealStep(
    state: OnboardingUiState,
    onSaveToShelf: () -> Unit,
    onShowAnother: () -> Unit
) {
    val book = state.currentAhaBook ?: return
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "YOUR FIRST MATCH",
                fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium,
                fontSize = 10.5.sp, letterSpacing = 2.sp, color = OB.sub60
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "We think you'll\nlove this.",
                fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 30.sp, lineHeight = 35.sp, color = OB.ink,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(28.dp))

            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(150.dp)
                    .aspectRatio(2f / 3f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(OB.fill92)
            )
            Spacer(Modifier.height(18.dp))
            Text(
                book.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold,
                fontSize = 22.sp, color = OB.ink, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "by ${book.authorLine}", fontSize = 13.sp, color = OB.sub50,
                textAlign = TextAlign.Center
            )
            book.reason?.takeIf { it.isNotEmpty() }?.let {
                Spacer(Modifier.height(14.dp))
                Text(
                    it, fontSize = 13.5.sp, lineHeight = 19.sp, color = OB.sub40,
                    textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }

        Column(
            Modifier.navigationBarsPadding().padding(horizontal = 26.dp).padding(bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.errorMessage?.let {
                Text(it, fontSize = 12.sp, color = OB.red, textAlign = TextAlign.Center)
            }
            OnbPrimaryButton(
                title = "Save to my shelves",
                onClick = onSaveToShelf,
                loading = state.isAddingBook
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !state.isAddingBook,
                        onClick = onShowAnother
                    )
            ) {
                Text("Show me another", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OB.sub25)
            }
        }
    }
}

/** Reads + downscales the picked image off the main thread (max 1024px, JPEG 85). */
internal suspend fun readAndDownscale(context: Context, uri: Uri): ByteArray? =
    withContext(Dispatchers.IO) {
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
            ?: return@withContext null
        val source = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@withContext bytes
        val scale = maxOf(source.width, source.height) / 1024f
        val bitmap = if (scale > 1f) {
            Bitmap.createScaledBitmap(
                source, (source.width / scale).toInt(), (source.height / scale).toInt(), true
            )
        } else source
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        out.toByteArray()
    }
