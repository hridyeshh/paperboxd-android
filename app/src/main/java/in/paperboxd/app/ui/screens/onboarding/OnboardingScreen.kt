package `in`.paperboxd.app.ui.screens.onboarding

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import `in`.paperboxd.app.R
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.ImageCropper
import `in`.paperboxd.app.ui.components.SpineTitle
import `in`.paperboxd.app.ui.components.brutalButton
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
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.PBPrimaryButton
import `in`.paperboxd.app.ui.components.PBStepHeader
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.delay
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import `in`.paperboxd.app.ui.components.CatAnimation
import `in`.paperboxd.app.ui.components.CatClip
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.components.pageDissolve
import `in`.paperboxd.app.ui.components.pageDissolveSupported
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.math.abs
import kotlin.math.max

/**
 * Multi-step onboarding — iOS `OnboardingContainerView` twin: light editorial
 * theme (dark only on the aha-loading step), a staged pill header, serif titles,
 * a dashed avatar ring, availability-coloured username field, genre chips with a
 * live counter, tempo rows, a pulsing dark loading screen, and the first-match
 * reveal. Steps: username → genres → books → readers → ahaLoading → ahaReveal.
 */

// Onboarding runs on the same paper ground as auth and home — these are named
// roles over the shared HL tokens, so the two flows can't drift apart.
private object OB {
    val ink = HL.Ink
    val muted = HL.Muted
    val faint = Color(0xFFA6A092)    // "you can change this later" register
    val hairline = HL.Ink.copy(alpha = 0.30f)
    val rule = HL.Ink.copy(alpha = 0.14f)
    val line = HL.Ink.copy(alpha = 0.20f)
    val well = HL.Card
    val recessed = HL.Paper2
    val ring = HL.Ink.copy(alpha = 0.28f)
    val green = HL.Sage
    val red = HL.Crimson
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
    // Cropped JPEG bytes, which Coil renders directly — previews the crop the
    // user chose, not the original photo.
    var pickedAvatar by remember { mutableStateOf<Any?>(null) }

    LaunchedEffect(Unit) {
        viewModel.initialUser = user
        viewModel.events.collect { event ->
            when (event) {
                is OnboardingEvent.UsernameSet -> onUsernameSet(event.username)
                is OnboardingEvent.Finished -> onFinished()
            }
        }
    }

    // Picked image goes to the square cropper first — iOS gets the same crop step
    // free from UIImagePickerController's allowsEditing.
    var cropUri by remember { mutableStateOf<Uri?>(null) }

    val pickAvatar = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? -> cropUri = uri }

    cropUri?.let { uri ->
        ImageCropper(
            uri = uri,
            ratio = 1f,
            onCancel = { cropUri = null },
            onCrop = { bytes ->
                pickedAvatar = bytes  // instant local preview, like iOS
                viewModel.uploadAvatar(bytes)
                cropUri = null
            }
        )
    }

    OnboardingContent(
        state = state,
        avatarModel = pickedAvatar ?: state.avatarUrl,
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
        onSaveToShelf = viewModel::saveToShelf,
        onShowAnother = viewModel::showAnother,
        onSkipAha = viewModel::skipAha
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
    onSaveToShelf: () -> Unit,
    onShowAnother: () -> Unit,
    onSkipAha: () -> Unit
) {
    // Whole-content entrance: fade in, once.
    var contentIn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { contentIn = true }
    val contentAlpha by animateFloatAsState(
        if (contentIn) 1f else 0f, tween(600), label = "content-alpha"
    )

    Box(Modifier.fillMaxSize().background(HL.Paper)) {
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
                    if (pageDissolveSupported) {
                        EnterTransition.None togetherWith ExitTransition.None
                    } else if (targetState == OnboardingStep.AhaLoading ||
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
                Box(Modifier.fillMaxSize().then(pageDissolve()), propagateMinConstraints = true) {
                    when (step) {
                        OnboardingStep.Username -> UsernameStep(
                            state, avatarModel, onPickAvatar, onUsernameChange,
                            onDisplayNameChange, onSubmitUsername
                        )
                        OnboardingStep.Genres -> SwipeGenresStep(state, onToggleGenre, onContinueFromGenres)
                        OnboardingStep.AhaLoading -> AhaLoadingStep(Genre.all.filter { it.id in state.selectedGenres }.map { it.label })
                        OnboardingStep.AhaReveal -> AhaRevealStep(state, onSaveToShelf, onShowAnother, onSkipAha)
                    }
                }
            }
        }
    }
}

// ── Stage header ─────────────────────────────────────────────────────────────

/** Signup is step 1 of the shared 3-step run; onboarding picks up at 2. */
@Composable
private fun StageHeader(step: OnboardingStep, modifier: Modifier = Modifier) {
    val subStep = when (step) {
        OnboardingStep.Username -> 2
        OnboardingStep.Genres -> 3
        else -> null
    } ?: return

    PBStepHeader(step = subStep, modifier = modifier)
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
    Text(text, fontSize = 13.5.sp, lineHeight = 18.sp, color = OB.muted)
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text.uppercase(),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.2.sp,
        color = OB.muted
    )
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
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Image(
                painter = painterResource(R.drawable.reading_buddy_icon),
                contentDescription = null,
                modifier = Modifier.width(50.dp)
            )
            Column {
                OnbTitle("Choose your name\non the shelves.")
                Spacer(Modifier.height(8.dp))
                OnbSubtitle("This is how readers will find and follow you.")
            }
        }
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
        PBPrimaryButton(
            title = "Continue",
            onClick = onSubmitUsername,
            loading = state.isSubmitting,
            enabled = state.availability == UsernameAvailability.Available
        )
        Spacer(Modifier.height(12.dp))
        Text(
            "You can change this any time.",
            fontSize = 13.sp, color = OB.faint,
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
                modifier = Modifier.size(76.dp).clip(CircleShape).background(OB.avatarGradient)
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
                modifier = Modifier.size(24.dp).clip(CircleShape).background(HL.Crimson)
            ) {
                Icon(Icons.Filled.Add, null, tint = Color.White, modifier = Modifier.size(12.dp))
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                if (avatarModel == null) "Add a picture" else "Change picture",
                fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold, color = OB.ink
            )
            Text("Optional · square, 400px+", fontSize = 12.sp, color = OB.muted)
        }
    }
}

@Composable
private fun UsernameField(state: OnboardingUiState, onChange: (String) -> Unit) {
    val borderColor = when (state.availability) {
        is UsernameAvailability.Available -> OB.green
        is UsernameAvailability.Taken, is UsernameAvailability.CheckFailed -> OB.red
        is UsernameAvailability.Checking -> HL.Ink.copy(alpha = 0.45f)
        UsernameAvailability.Idle -> OB.hairline
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .background(OB.well)
            .border(2.dp, borderColor)
    ) {
        Text(
            "@", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OB.muted,
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
                        Text("yourname", fontSize = 14.sp, color = OB.muted)
                    }
                    inner()
                }
            },
            modifier = Modifier.weight(1f).padding(vertical = 13.dp)
        )
        Box(Modifier.padding(horizontal = 14.dp).size(18.dp), contentAlignment = Alignment.Center) {
            when (state.availability) {
                UsernameAvailability.Checking -> CircularProgressIndicator(
                    color = OB.muted, strokeWidth = 1.5.dp, modifier = Modifier.size(14.dp)
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
            Text("Letters, numbers, _ or -", fontSize = 12.sp, color = OB.faint)
        UsernameAvailability.Available ->
            Text("@${state.username} is available", fontSize = 12.5.sp, color = OB.green)
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
            .background(OB.well)
            .border(2.dp, OB.hairline)
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
                    if (value.isEmpty()) Text("Your name", fontSize = 14.sp, color = OB.muted)
                    inner()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ── Step 3: Genres — swipe away ──────────────────────────────────────────────
//
// Deck of genre cards. Drag past 90dp (or tap a button) to add or skip: the card
// tints and stamps ADD/SKIP as it leans, the matching button lights up and the
// mascot quips. Picked genres drop into the shelf tray; three auto-advance.

@Composable
private fun SwipeGenresStep(
    state: OnboardingUiState,
    onToggleGenre: (String) -> Unit,
    onContinueFromGenres: () -> Unit
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val commitPx = with(density) { 90.dp.toPx() }
    val latest by rememberUpdatedState(state)

    var deck by remember { mutableStateOf(Genre.all.shuffled()) }
    var dragX by remember { mutableFloatStateOf(0f) }
    var dragY by remember { mutableFloatStateOf(0f) }
    var flying by remember { mutableStateOf(false) }
    var pastThreshold by remember { mutableStateOf(false) }
    val topGenre = deck.firstOrNull()
    val lean = (dragX / commitPx).coerceIn(-1f, 1f)
    val leaningAdd = lean > 0.3f
    val leaningSkip = lean < -0.3f

    // Parse both cat loops while the reader swipes, so the next screens start instantly.
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            CatClip.load(context, R.raw.cat_play)
            CatClip.load(context, R.raw.cat_match)
        }
    }

    fun swipe(liked: Boolean) {
        val genre = deck.firstOrNull() ?: return
        if (flying) return
        flying = true
        haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
        scope.launch {
            val fromX = dragX
            val fromY = dragY
            val toX = (if (liked) 1f else -1f) * commitPx * 6f
            animate(0f, 1f, animationSpec = tween(200, easing = FastOutLinearInEasing)) { p, _ ->
                dragX = fromX + (toX - fromX) * p
                dragY = fromY + 30f * density.density * p
            }
            if (deck.firstOrNull()?.id == genre.id) deck = deck.drop(1)
            dragX = 0f
            dragY = 0f
            if (liked) onToggleGenre(genre.id)
            // Ran out of cards before three — reshuffle the skipped ones so nobody gets stuck.
            val picked = latest.selectedGenres + if (liked) setOf(genre.id) else emptySet()
            if (deck.isEmpty() && picked.size < 3) deck = Genre.all.filter { it.id !in picked }.shuffled()
            flying = false
            if (deck.isEmpty()) {
                // Seen every genre with 3+ liked: nothing left to decide, so move on.
                delay(350)
                onContinueFromGenres()
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 26.dp)) {
        Column(Modifier.padding(top = 18.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            OnbTitle("Swipe away!")
            OnbSubtitle("Right for genres you love, left to skip. Pick at least 3.")
        }
        ShelfTray(
            // selectedGenres is insertion-ordered, so this is pick order.
            picked = state.selectedGenres.mapNotNull { id -> Genre.all.find { it.id == id } },
            pending = if (leaningAdd) topGenre else null,
            modifier = Modifier.padding(top = 16.dp)
        )
        // Shrinks the deck on short phones so Continue stays on screen.
        BoxWithConstraints(Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
            val fit = (maxHeight / 320.dp).coerceAtMost(1f)
            deck.take(3).reversed().forEach { genre ->
                key(genre.id) {
                    val depth = deck.indexOf(genre)
                    val isTop = depth == 0
                    val scaled = Modifier.graphicsLayer { scaleX = fit; scaleY = fit }
                    GenreCard(
                        genre = genre,
                        depth = depth,
                        dragX = if (isTop) dragX else 0f,
                        dragY = if (isTop) dragY else 0f,
                        lean = if (isTop) lean else 0f,
                        modifier = if (!isTop) scaled else scaled.pointerInput(genre.id) {
                            detectDragGestures(
                                onDragEnd = {
                                    pastThreshold = false
                                    when {
                                        dragX > commitPx -> swipe(true)
                                        dragX < -commitPx -> swipe(false)
                                        else -> scope.launch {
                                            val fx = dragX
                                            val fy = dragY
                                            animate(1f, 0f, animationSpec = spring(dampingRatio = 0.7f, stiffness = Spring.StiffnessMediumLow)) { p, _ ->
                                                dragX = fx * p
                                                dragY = fy * p
                                            }
                                        }
                                    }
                                },
                                onDragCancel = {
                                    pastThreshold = false
                                    dragX = 0f
                                    dragY = 0f
                                }
                            ) { change, amount ->
                                if (flying) return@detectDragGestures
                                change.consume()
                                dragX += amount.x
                                dragY += amount.y
                                val past = abs(dragX) > commitPx
                                if (past != pastThreshold) {
                                    pastThreshold = past
                                    if (past) haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                }
                            }
                        }
                    )
                }
            }
        }
        HintRow(leaningAdd, leaningSkip)
        Row(
            Modifier.fillMaxWidth().padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val label = topGenre?.label.orEmpty()
            SwipeAction(add = false, active = leaningSkip, dimmed = leaningAdd, enabled = topGenre != null && !flying, genre = label) { swipe(false) }
            Spacer(Modifier.weight(1f))
            SwipeMascot(lean)
            Spacer(Modifier.weight(1f))
            SwipeAction(add = true, active = leaningAdd, dimmed = leaningSkip, enabled = topGenre != null && !flying, genre = label) { swipe(true) }
        }
        PBPrimaryButton(
            title = "Continue",
            onClick = onContinueFromGenres,
            enabled = state.selectedGenres.size >= 3,
            modifier = Modifier.navigationBarsPadding().padding(top = 18.dp, bottom = 20.dp)
        )
    }
}

@Composable
private fun ShelfTray(picked: List<Genre>, pending: Genre?, modifier: Modifier = Modifier) {
    // Scrolls once it outgrows the screen, following the newest (or
    // about-to-be-added) chip at the end.
    val scroll = rememberScrollState()
    LaunchedEffect(scroll.maxValue) { scroll.animateScrollTo(scroll.maxValue) }
    Column(modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            if (picked.size < 3) "YOUR SHELF · ${picked.size} OF 3" else "YOUR SHELF · ${picked.size} PICKED",
            fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium,
            fontSize = 11.sp, letterSpacing = 1.6.sp, color = OB.muted
        )
        Row(Modifier.horizontalScroll(scroll), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            picked.forEach { FilledChip(it.label) }
            when {
                pending != null -> DashedChip(pending.label, HL.Sage, HL.Sage, plus = true)
                picked.size < 3 -> DashedChip("${3 - picked.size} more", OB.muted, HL.Ink.copy(alpha = 0.35f), plus = false)
            }
        }
    }
}

@Composable
private fun FilledChip(label: String) {
    Row(
        Modifier.height(32.dp).clip(CircleShape).background(HL.Ink).padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(Icons.Filled.Check, null, tint = HL.Card, modifier = Modifier.size(12.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = HL.Card)
    }
}

@Composable
private fun DashedChip(label: String, color: Color, dash: Color, plus: Boolean) {
    Row(
        Modifier
            .height(32.dp)
            .drawBehind {
                val w = 1.5.dp.toPx()
                drawRoundRect(
                    dash,
                    topLeft = Offset(w / 2, w / 2),
                    size = Size(size.width - w, size.height - w),
                    cornerRadius = CornerRadius(size.height / 2),
                    style = Stroke(w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 3.dp.toPx())))
                )
            }
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        if (plus) Icon(Icons.Filled.Add, null, tint = color, modifier = Modifier.size(12.dp))
        Text(label, fontSize = 13.sp, fontWeight = if (plus) FontWeight.SemiBold else FontWeight.Medium, color = color)
    }
}

@Composable
private fun GenreCard(genre: Genre, depth: Int, dragX: Float, dragY: Float, lean: Float, modifier: Modifier) {
    val isTop = depth == 0
    val number = Genre.all.indexOf(genre) + 1
    Box(
        modifier
            .graphicsLayer {
                translationX = dragX
                translationY = dragY
                rotationZ = if (isTop) dragX / (14f * density) else if (depth == 1) -4f else 5f
            }
            .size(width = 250.dp, height = 300.dp)
            .background(if (isTop) HL.Ink else if (depth == 1) HL.Paper2 else HL.Card)
            .border(2.dp, HL.Ink)
    ) {
        if (isTop) {
            Box(Modifier.matchParentSize().background((if (lean >= 0f) HL.Sage else HL.Crimson).copy(alpha = abs(lean))))
            Column(Modifier.fillMaxSize().padding(22.dp)) {
                Text(
                    String.format(Locale.US, "GENRE %02d / %d", number, Genre.all.size),
                    fontFamily = FontFamily.Monospace, fontSize = 11.sp, letterSpacing = 1.6.sp,
                    color = Color.White.copy(alpha = 0.65f)
                )
                Spacer(Modifier.weight(1f))
                Text(
                    genre.label,
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 40.sp, lineHeight = 42.sp, maxLines = 2, color = Color.White
                )
            }
            SwipeStamp(add = true, alpha = max(0f, lean), modifier = Modifier.align(Alignment.TopStart).padding(top = 48.dp, start = 18.dp))
            SwipeStamp(add = false, alpha = max(0f, -lean), modifier = Modifier.align(Alignment.TopEnd).padding(top = 48.dp, end = 18.dp))
        }
    }
}

@Composable
private fun SwipeStamp(add: Boolean, alpha: Float, modifier: Modifier) {
    Row(
        modifier
            .graphicsLayer { this.alpha = alpha; rotationZ = if (add) -14f else 14f }
            .border(3.dp, Color.White)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(if (add) Icons.Filled.Check else Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(if (add) "ADD" else "SKIP", fontSize = 24.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp, color = Color.White)
    }
}

@Composable
private fun HintRow(leaningAdd: Boolean, leaningSkip: Boolean) {
    Row(Modifier.fillMaxWidth().padding(top = 10.dp)) {
        HintText("← SKIP", if (leaningSkip) HL.Crimson else OB.muted, if (leaningAdd) 0.4f else 1f)
        Spacer(Modifier.weight(1f))
        HintText("ADD →", if (leaningAdd) HL.Sage else OB.muted, if (leaningSkip) 0.4f else 1f)
    }
}

@Composable
private fun HintText(text: String, color: Color, alpha: Float) {
    Text(
        text, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold,
        fontSize = 11.sp, letterSpacing = 1.4.sp, color = color, modifier = Modifier.alpha(alpha)
    )
}

@Composable
private fun SwipeAction(add: Boolean, active: Boolean, dimmed: Boolean, enabled: Boolean, genre: String, onClick: () -> Unit) {
    val scale by animateFloatAsState(if (active) 1.06f else 1f, spring(dampingRatio = 0.6f), label = "swipe-action")
    val fill = when {
        active -> if (add) HL.Sage else HL.Crimson
        add -> HL.Ink
        else -> HL.Card
    }
    val fg = if (active || add) Color.White else HL.Ink
    Row(
        Modifier
            .graphicsLayer { scaleX = scale; scaleY = scale; alpha = if (dimmed) 0.35f else 1f }
            .semantics { contentDescription = "${if (add) "Add" else "Skip"} $genre" }
            .size(width = 112.dp, height = 58.dp)
            .brutalButton(onClick = onClick, fill = fill, enabled = enabled),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(if (add) Icons.Filled.Check else Icons.Filled.Close, null, tint = fg, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(7.dp))
        Text(if (add) "Add" else "Skip", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = fg)
    }
}

@Composable
private fun SwipeMascot(lean: Float) {
    val quip = when {
        lean > 0.3f -> "ooh, yes!"
        lean < -0.3f -> "nah, next!"
        else -> null
    }
    var lastQuip by remember { mutableStateOf("ooh, yes!") }
    LaunchedEffect(quip) { if (quip != null) lastQuip = quip }
    val shown by animateFloatAsState(if (quip != null) 1f else 0f, tween(150), label = "quip")
    Box(Modifier.width(100.dp).height(92.dp), contentAlignment = Alignment.BottomCenter) {
        Image(
            painter = painterResource(R.drawable.swipe_mascot_icon),
            contentDescription = null,
            modifier = Modifier.width(70.dp).graphicsLayer {
                scaleX = if (lean < -0.3f) -1f else 1f
                rotationZ = lean * -6f
            }
        )
        Text(
            lastQuip, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = HL.Ink, maxLines = 1, softWrap = false,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .graphicsLayer {
                    alpha = shown
                    scaleX = 0.6f + 0.4f * shown
                    scaleY = 0.6f + 0.4f * shown
                    rotationZ = if (lean > 0f) -3f else 3f
                }
                .background(HL.Card)
                .border(2.dp, HL.Ink)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}

// ── Aha loading: the cat plays while the first match loads ───────────────────

@Composable
private fun AhaLoadingStep(genres: List<String>) {
    var stage by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (stage < 2) {
            delay(1000)
            stage++
        }
    }
    // Frame-clock driven so the bar still moves when the system animator scale is 0.
    var progress by remember { mutableFloatStateOf(0.06f) }
    LaunchedEffect(Unit) {
        var start = -1L
        while (progress < 0.92f) {
            withFrameNanos { now ->
                if (start < 0) start = now
                val t = ((now - start) / 3.0e9f).coerceAtMost(1f)
                progress = 0.06f + 0.86f * (1f - (1f - t) * (1f - t))
            }
        }
    }
    val steps = listOf(
        "Reading your taste" to genres.joinToString(" · ").ifEmpty { null },
        "Checking the shelves" to null,
        "Picking the one" to null
    )
    Column(
        Modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 26.dp).padding(bottom = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "ALMOST THERE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium,
            fontSize = 11.sp, letterSpacing = 2.sp, color = OB.muted, modifier = Modifier.padding(top = 28.dp)
        )
        CatAnimation(
            R.raw.cat_play,
            Modifier.padding(top = 14.dp).fillMaxWidth().weight(3f, fill = false).aspectRatio(1.5f)
        )
        Text(
            "Finding your\nfirst book…", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
            fontSize = 28.sp, lineHeight = 32.sp, textAlign = TextAlign.Center, color = OB.ink,
            modifier = Modifier.padding(top = 16.dp)
        )
        Text(
            "Matching your picks against the shelves.", fontSize = 14.sp, color = OB.muted,
            textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp)
        )
        Column(
            Modifier.padding(top = 22.dp).fillMaxWidth().brutalPlate(borderWidth = 2.dp).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            steps.forEachIndexed { i, (title, detail) -> LoadingStepRow(i, stage, title, detail) }
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth().height(14.dp).background(HL.Card).border(2.dp, HL.Ink).padding(2.dp)) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(progress).background(HL.Ink))
        }
        Text(
            "HANG TIGHT", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium,
            fontSize = 11.sp, letterSpacing = 1.6.sp, color = OB.muted, modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Composable
private fun LoadingStepRow(i: Int, stage: Int, title: String, detail: String?) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) {
            when {
                i < stage -> Box(Modifier.fillMaxSize().clip(CircleShape).background(HL.Ink), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(13.dp))
                }
                i == stage -> Box(Modifier.fillMaxSize().border(2.dp, HL.Ink, CircleShape), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(HL.Crimson))
                }
                else -> Box(Modifier.fillMaxSize().drawBehind {
                    val w = 2.dp.toPx()
                    drawCircle(
                        HL.Ink.copy(alpha = 0.3f), radius = size.minDimension / 2 - w / 2,
                        style = Stroke(w, pathEffect = PathEffect.dashPathEffect(floatArrayOf(3.dp.toPx(), 3.dp.toPx())))
                    )
                })
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                title, fontSize = 15.sp, color = if (i > stage) OB.muted else OB.ink,
                fontWeight = when {
                    i == stage -> FontWeight.Bold
                    i < stage -> FontWeight.SemiBold
                    else -> FontWeight.Medium
                }
            )
            if (detail != null) Text(detail, fontSize = 13.sp, color = OB.muted)
        }
    }
}

// ── Aha reveal: the first match, presented by the celebrating cat ────────────

@Composable
private fun AhaRevealStep(
    state: OnboardingUiState,
    onSaveToShelf: () -> Unit,
    onShowAnother: () -> Unit,
    onSkip: () -> Unit
) {
    val book = state.currentAhaBook ?: return
    val reason = Genre.all.filter { it.id in state.selectedGenres }.take(2).map { it.label }
        .takeIf { it.isNotEmpty() }?.joinToString(" + ", prefix = "Picked from ")
    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
                .padding(top = 20.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "YOUR FIRST MATCH", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium,
                fontSize = 11.sp, letterSpacing = 2.sp, color = OB.muted
            )
            Spacer(Modifier.height(18.dp))
            Box(
                Modifier
                    .rotate(-2f)
                    .padding(end = 6.dp, bottom = 6.dp)
                    .drawBehind { drawRect(HL.Ink, topLeft = Offset(6.dp.toPx(), 6.dp.toPx()), size = size) }
                    .border(2.dp, HL.Ink)
            ) {
                AsyncImage(
                    model = book.coverUrl,
                    contentDescription = book.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.width(160.dp).aspectRatio(2f / 3f).background(OB.recessed)
                )
            }
            Spacer(Modifier.height(22.dp))
            Text(
                book.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 24.sp, lineHeight = 28.sp, color = OB.ink, textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text("by ${book.authorLine}", fontSize = 14.sp, color = OB.muted, textAlign = TextAlign.Center)
            Row(
                Modifier.padding(top = 14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                CatAnimation(R.raw.cat_match, Modifier.size(147.dp))
                LoveBubble(reason, Modifier.padding(top = 16.dp))
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
            PBPrimaryButton(
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
                Text("Show me another", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = OB.ink)
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = !state.isAddingBook,
                        onClick = onSkip
                    )
            ) {
                Text("Skip to home", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = OB.faint)
            }
        }
    }
}

@Composable
private fun LoveBubble(reason: String?, modifier: Modifier = Modifier) {
    Box(modifier) {
        Column(
            Modifier.brutalPlate(borderWidth = 2.dp).padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "You'll love\nthis!", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                fontSize = 26.sp, lineHeight = 27.sp, color = OB.ink
            )
            reason?.let { Text(it, fontSize = 13.sp, color = OB.muted) }
        }
        Canvas(Modifier.offset(x = (-11).dp, y = 22.dp).size(width = 12.dp, height = 18.dp)) {
            val tail = Path().apply {
                moveTo(size.width, 0f)
                lineTo(0f, size.height / 2)
                lineTo(size.width, size.height)
            }
            drawPath(tail, HL.Card)
            drawPath(tail, HL.Ink, style = Stroke(2.dp.toPx()))
        }
    }
}

// readAndDownscale lived here until ImageCropper took over decoding, downscaling
// and encoding for both avatar call sites. It used BitmapFactory, which ignores
// EXIF — portrait photos uploaded sideways. Coil (inside the cropper) applies it.
