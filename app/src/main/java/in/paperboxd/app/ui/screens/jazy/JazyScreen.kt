package `in`.paperboxd.app.ui.screens.jazy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.navigation.PipFace
import `in`.paperboxd.app.ui.screens.scan.ScanFlowScreen
import `in`.paperboxd.app.ui.screens.scan.ScanPrefs
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Ask Jazy — the merged vibe-search + Scan & Know entry. iOS twin: `JazyView`.
 *
 * A quiet page: vibe phrases drift up through the background behind hand-drawn
 * doodles; one hairline bar with the camera parked at its side. Typing slides the
 * camera away and a send arrow takes its place. Submitting raises the results
 * deck; tapping the camera raises the untouched brutalist Scan & Know flow.
 *
 * Design source: `Paperboxd design elements/ask-jazy/v3.jsx`.
 */
object JZ {
    val bg = Color(0xFFFBFAF7)
    val card = Color(0xFFFFFFFF)
    val ink = Color(0xFF37352F)
    val sub = Color(0xFF9B9891)
    val faint = Color(0xFFC4C1B8)
    val accent = Color(0xFFB85C38)
    val line = Color(0x2437352F)

    val prompts = listOf(
        "a cosy autumn mystery…",
        "something that will wreck me…",
        "found family in space…",
        "smart, but under 200 pages…",
        "like a warm hug…"
    )
    val chips = listOf("cosy", "will wreck me", "found family", "slow burn", "gothic")
}

@Composable
fun JazyScreen(
    user: User,
    onDismiss: () -> Unit,
    onOpenBook: (String) -> Unit,
    viewModel: JazyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var showScan by rememberSaveable { mutableStateOf(false) }
    var focused by remember { mutableStateOf(false) }

    val hasText = state.query.trim().isNotEmpty()
    val dimmed = focused || state.showResults
    val scansLeft = remember { ScanPrefs.scansRemaining(context) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(JZ.bg)
    ) {
        JazyDriftField(dimmed)
        JazyDoodleField(dimmed)

        // ── the quiet layer ──
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .alpha(if (state.showResults) 0f else 1f)
                .padding(top = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp)) {
                Spacer(Modifier.weight(1f))
                IconTile(onClick = onDismiss, description = "Close Ask Jazy") {
                    Icon(Icons.Outlined.Close, null, tint = JZ.ink, modifier = Modifier.size(15.dp))
                }
            }

            Spacer(Modifier.weight(if (focused) 0.35f else 1f))

            PipFace(thinking = focused && hasText, modifier = Modifier.size(56.dp, 62.dp))

            Text(
                text = if (focused && hasText) "Jazy is reading…" else "What are you in the mood for?",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                lineHeight = 28.sp,
                color = JZ.ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp, start = 24.dp, end = 24.dp)
            )
            if (!focused) {
                Text(
                    "Describe a feeling — or scan a cover in the store.",
                    fontSize = 13.sp,
                    color = JZ.sub,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 6.dp, start = 24.dp, end = 24.dp)
                )
            }

            JazyBar(
                query = state.query,
                hasText = hasText,
                focused = focused,
                isSearching = state.isSearching,
                focusRequester = focusRequester,
                onQueryChanged = viewModel::onQueryChanged,
                onFocusChanged = { focused = it },
                onSubmit = {
                    keyboard?.hide()
                    viewModel.submit()
                },
                onCamera = { showScan = true },
                modifier = Modifier.padding(top = 24.dp, start = 24.dp, end = 24.dp)
            )

            JazyChips(
                onChip = {
                    viewModel.appendChip(it)
                    focusRequester.requestFocus()
                },
                modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp)
            )

            state.errorMessage?.let {
                Text(
                    it,
                    fontSize = 12.5.sp,
                    color = JZ.accent,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 12.dp, start = 24.dp, end = 24.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Text(
                "$scansLeft free scans left",
                fontSize = 11.5.sp,
                color = JZ.faint,
                modifier = Modifier.padding(bottom = 26.dp)
            )
        }

        // ── vibe results · the card deck rises ──
        AnimatedVisibility(
            visible = state.showResults,
            enter = slideInVertically(tween(450)) { it } + fadeIn(tween(300)),
            exit = slideOutVertically(tween(450)) { it } + fadeOut(tween(300))
        ) {
            JazyResultsDeck(
                query = state.query,
                matches = state.matches,
                onClose = viewModel::closeResults,
                onOpenBook = onOpenBook
            )
        }

        // ── the original brutalist Scan & Know rises, untouched ──
        if (showScan) {
            ScanFlowScreen(user = user, onDismiss = { showScan = false })
        }
    }
}

// MARK: - The bar

@Composable
private fun JazyBar(
    query: String,
    hasText: Boolean,
    focused: Boolean,
    isSearching: Boolean,
    focusRequester: FocusRequester,
    onQueryChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onSubmit: () -> Unit,
    onCamera: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The camera slides away the moment typing starts; the send arrow takes its place.
    val cameraWidth by animateDpAsState(if (hasText) 0.dp else 48.dp, tween(350), label = "jazy-cam")
    val sendWidth by animateDpAsState(if (hasText) 32.dp else 0.dp, tween(300), label = "jazy-send")

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(12.dp))
                .background(JZ.card)
                .border(
                    1.dp,
                    if (focused) Color(0x4737352F) else JZ.line,
                    RoundedCornerShape(12.dp)
                )
                .padding(start = 16.dp, end = 6.dp, top = 11.dp, bottom = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = TextStyle(fontSize = 15.sp, color = JZ.ink),
                cursorBrush = SolidColor(JZ.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) RotatingPlaceholder()
                        inner()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusChanged { onFocusChanged(it.isFocused) }
            )
            Box(
                modifier = Modifier
                    .width(sendWidth)
                    .height(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(JZ.accent)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        enabled = hasText && !isSearching
                    ) { onSubmit() },
                contentAlignment = Alignment.Center
            ) {
                if (hasText) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Find my book",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .width(cameraWidth)
                .height(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(JZ.card)
                .border(if (hasText) 0.dp else 1.dp, JZ.line, RoundedCornerShape(12.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    enabled = !hasText
                ) { onCamera() },
            contentAlignment = Alignment.Center
        ) {
            if (!hasText) {
                Icon(
                    Icons.Outlined.PhotoCamera,
                    contentDescription = "Scan a cover",
                    tint = JZ.ink,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/** The placeholder cycles through the vibe prompts every 3s. */
@Composable
private fun RotatingPlaceholder() {
    var index by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            index = (index + 1) % JZ.prompts.size
        }
    }
    Text(JZ.prompts[index], fontSize = 15.sp, color = JZ.faint, maxLines = 1)
}

@Composable
private fun JazyChips(onChip: (String) -> Unit, modifier: Modifier = Modifier) {
    // ponytail: fixed two-row layout — the chip set is a constant five.
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JZ.chips.take(3).forEach { Chip(it, onChip) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            JZ.chips.drop(3).forEach { Chip(it, onChip) }
        }
    }
}

@Composable
private fun Chip(text: String, onChip: (String) -> Unit) {
    Text(
        text,
        fontSize = 12.5.sp,
        color = JZ.sub,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(JZ.card)
            .border(1.dp, JZ.line, RoundedCornerShape(8.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onChip(text) }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
internal fun IconTile(
    onClick: () -> Unit,
    description: String,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(JZ.card)
            .border(1.dp, JZ.line, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = description
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) { content() }
}

/** Offsets by a fraction of the parent's size — the CSS `left:%/top:%` twin. */
internal fun Modifier.offsetFraction(x: Float, y: Float): Modifier =
    this.layout { measurable, constraints ->
        val placeable = measurable.measure(constraints.copy(minWidth = 0, minHeight = 0))
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place((constraints.maxWidth * x).toInt(), (constraints.maxHeight * y).toInt())
        }
    }

// MARK: - Background: drifting vibe phrases

private data class DriftPhrase(
    val text: String, val x: Float, val y: Float,
    val size: Int, val durationMs: Int, val delay: Float
)

private val DRIFT = listOf(
    DriftPhrase("a gothic house", 0.06f, 0.16f, 21, 17000, 0f),
    DriftPhrase("slow burn, high stakes", 0.52f, 0.11f, 17, 14000, 0.25f),
    DriftPhrase("like a warm hug", 0.14f, 0.38f, 24, 19000, 0.37f),
    DriftPhrase("an unreliable narrator", 0.44f, 0.52f, 18, 15000, 0.12f),
    DriftPhrase("found family", 0.62f, 0.31f, 20, 16000, 0.59f),
    DriftPhrase("will wreck me", 0.10f, 0.62f, 19, 18000, 0.29f),
    DriftPhrase("under 200 pages", 0.48f, 0.72f, 17, 14000, 0.79f),
    DriftPhrase("cosy autumn mystery", 0.18f, 0.84f, 22, 20000, 0.13f)
)

@Composable
private fun JazyDriftField(dimmed: Boolean) {
    val transition = rememberInfiniteTransition(label = "jazy-drift")
    val dim by animateFloatAsState(if (dimmed) 0.35f else 1f, tween(500), label = "jazy-drift-dim")

    Box(modifier = Modifier.fillMaxSize()) {
        DRIFT.forEach { phrase ->
            val t by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(phrase.durationMs, easing = LinearEasing)),
                label = "jazy-drift-${phrase.text}"
            )
            // Each phrase runs the same 0…1 clock, offset by its own delay.
            val p = (t + phrase.delay) % 1f
            val fade = when {
                p < 0.2f -> p / 0.2f
                p > 0.68f -> (1f - p) / 0.32f
                else -> 1f
            }
            Box(modifier = Modifier.offsetFraction(phrase.x, phrase.y)) {
                Text(
                    phrase.text,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = phrase.size.sp,
                    color = JZ.ink,
                    maxLines = 1,
                    modifier = Modifier
                        .alpha(0.09f * fade * dim)
                        .padding(top = (30f - 68f * p).coerceAtLeast(0f).dp)
                )
            }
        }
    }
}

// MARK: - Background: hand-drawn doodles

private enum class DoodleKind { OpenBook, Sparkle, Asterisk, Moon, Cup, Heart, Squiggle, Glasses, Bookmark }

private data class Doodle(
    val kind: DoodleKind, val x: Float, val y: Float,
    val scale: Float, val rotation: Float, val durationMs: Int
)

private val DOODLES = listOf(
    Doodle(DoodleKind.OpenBook, 0.06f, 0.09f, 1.15f, -9f, 8000),
    Doodle(DoodleKind.Sparkle, 0.78f, 0.13f, 0.7f, 12f, 6000),
    Doodle(DoodleKind.Moon, 0.86f, 0.30f, 0.85f, 14f, 9000),
    Doodle(DoodleKind.Glasses, 0.10f, 0.28f, 0.95f, 6f, 7000),
    Doodle(DoodleKind.Squiggle, 0.40f, 0.21f, 0.8f, -4f, 8000),
    Doodle(DoodleKind.Cup, 0.82f, 0.56f, 1.0f, -7f, 7000),
    Doodle(DoodleKind.Heart, 0.05f, 0.52f, 0.75f, -12f, 6000),
    Doodle(DoodleKind.Bookmark, 0.30f, 0.66f, 0.85f, 8f, 9000),
    Doodle(DoodleKind.Asterisk, 0.68f, 0.74f, 0.65f, 0f, 6000),
    Doodle(DoodleKind.OpenBook, 0.58f, 0.88f, 0.9f, 7f, 8000),
    Doodle(DoodleKind.Sparkle, 0.12f, 0.80f, 0.6f, -15f, 7000)
)

/**
 * ponytail: static ink doodles with a slow bob — the design's ~5fps line-boil
 * (re-jittering every path every frame) isn't worth the redraw cost here; Pip
 * already carries the boiling-ink signature on this screen.
 */
@Composable
private fun JazyDoodleField(dimmed: Boolean) {
    val transition = rememberInfiniteTransition(label = "jazy-doodles")
    val dim by animateFloatAsState(if (dimmed) 0.3f else 1f, tween(500), label = "jazy-doodle-dim")

    Box(modifier = Modifier.fillMaxSize()) {
        DOODLES.forEachIndexed { i, d ->
            val bob by transition.animateFloat(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(d.durationMs, easing = LinearEasing)),
                label = "jazy-doodle-$i"
            )
            Canvas(
                modifier = Modifier
                    .offsetFraction(d.x, d.y)
                    .size((54 * d.scale).dp)
                    .alpha(dim)
            ) {
                val lift = -7f * sin(bob * 2 * Math.PI).toFloat()
                rotate(d.rotation) {
                    translate(0f, lift) { drawDoodle(d.kind) }
                }
            }
        }
    }
}

/** The doodle glyph set, drawn on the design's 48×48 canvas and scaled to fit. */
private fun DrawScope.drawDoodle(kind: DoodleKind) {
    val s = size.minDimension / 48f
    val stroke = Stroke(width = 1.7f * s, cap = StrokeCap.Round, join = StrokeJoin.Round)
    val path = Path()

    fun line(pts: List<Pair<Float, Float>>, close: Boolean = false) {
        path.moveTo(pts[0].first * s, pts[0].second * s)
        pts.drop(1).forEach { path.lineTo(it.first * s, it.second * s) }
        if (close) path.close()
    }
    // Quadratic smoothing through the midpoints — the design's a3Path().
    fun curve(pts: List<Pair<Float, Float>>) {
        if (pts.size < 3) return line(pts)
        path.moveTo(pts[0].first * s, pts[0].second * s)
        for (i in 1 until pts.size - 1) {
            val mx = (pts[i].first + pts[i + 1].first) / 2f * s
            val my = (pts[i].second + pts[i + 1].second) / 2f * s
            path.quadraticTo(pts[i].first * s, pts[i].second * s, mx, my)
        }
        path.lineTo(pts.last().first * s, pts.last().second * s)
    }
    fun circle(cx: Float, cy: Float, r: Float) {
        path.addOval(
            Rect(
                (cx - r) * s, (cy - r) * s,
                (cx + r) * s, (cy + r) * s
            )
        )
    }

    when (kind) {
        DoodleKind.OpenBook -> {
            curve(listOf(4f to 16f, 13f to 11f, 23f to 14f))
            curve(listOf(23f to 14f, 33f to 11f, 42f to 16f))
            curve(listOf(4f to 16f, 4f to 34f, 13f to 30f, 23f to 33f))
            curve(listOf(42f to 16f, 42f to 34f, 33f to 30f, 23f to 33f))
            line(listOf(23f to 14f, 23f to 33f))
        }
        DoodleKind.Sparkle -> {
            line(listOf(24f to 10f, 24f to 38f))
            line(listOf(10f to 24f, 38f to 24f))
        }
        DoodleKind.Asterisk -> {
            line(listOf(24f to 12f, 24f to 36f))
            line(listOf(15f to 18f, 33f to 32f))
            line(listOf(33f to 18f, 15f to 32f))
        }
        DoodleKind.Moon -> curve(
            listOf(
                30f to 8f, 20f to 12f, 16f to 24f, 20f to 36f,
                30f to 40f, 24f to 33f, 22f to 24f, 24f to 15f, 30f to 8f
            )
        )
        DoodleKind.Cup -> {
            curve(listOf(13f to 18f, 15f to 36f, 31f to 36f, 33f to 18f))
            line(listOf(11f to 18f, 35f to 18f))
            line(listOf(20f to 13f, 23f to 8f))
            line(listOf(27f to 13f, 30f to 8f))
        }
        DoodleKind.Heart -> curve(
            listOf(
                24f to 38f, 10f to 24f, 12f to 14f, 20f to 12f,
                24f to 18f, 28f to 12f, 36f to 14f, 38f to 24f, 24f to 38f
            )
        )
        DoodleKind.Squiggle -> curve(
            listOf(6f to 24f, 14f to 18f, 22f to 28f, 30f to 18f, 38f to 26f)
        )
        DoodleKind.Glasses -> {
            circle(13f, 26f, 7f)
            circle(35f, 26f, 7f)
            curve(listOf(20f to 25f, 24f to 23f, 28f to 25f))
        }
        DoodleKind.Bookmark ->
            line(listOf(18f to 8f, 30f to 8f, 30f to 36f, 24f to 28f, 18f to 36f), close = true)
    }

    drawPath(path, JZ.ink.copy(alpha = 0.11f), style = stroke)
}
