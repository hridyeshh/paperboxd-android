package `in`.paperboxd.app.ui.components

import android.content.Context
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

// Full-screen celebration takeovers, ported from the iOS CelebrationOverlay /
// "Motion & Celebration" takeover design: slab wipes, poster type, mono chrome.
// Timeline mirrors the web/iOS phase machine — lead slab wipes up, poster holds
// ~2.1s, wipes out.
//   Shelved  — Bookshelf (paper) / TBR (ink) / Read (paper) cover poster
//   Streak   — ink poster, giant number roll N-1 → N
//   LevelUp  — ink poster, gold sheen level name, roman numeral behind

sealed interface Celebration {
    data class Shelved(
        val shelf: CelebShelf,
        val title: String,
        val author: String,
        val coverUrl: String? = null
    ) : Celebration
    data class Streak(val days: Int) : Celebration
    data class LevelUp(val level: Int) : Celebration
}

enum class CelebShelf { Bookshelf, Tbr, Read }

fun LibraryShelf.toCelebShelf(): CelebShelf = when (this) {
    LibraryShelf.Bookshelf -> CelebShelf.Bookshelf
    LibraryShelf.Tbr -> CelebShelf.Tbr
    LibraryShelf.Read -> CelebShelf.Read
}

/** Reader levels, mirroring iOS ReaderLevel's tier ladder. */
object ReaderLevel {
    private val names = listOf("Apprentice", "Reader", "Bibliophile", "Scholar", "Sage", "Luminary")
    private val numerals = listOf("I", "II", "III", "IV", "V", "VI")
    fun name(level: Int) = names[(level - 1).coerceIn(0, names.lastIndex)]
    fun numeral(level: Int) = numerals[(level - 1).coerceIn(0, numerals.lastIndex)]
}

/**
 * Shared celebration bus + increment detection. Android twin of the iOS
 * CelebrationCenter singleton. Injected into the view models that emit
 * celebrations; observed by [CelebrationOverlayHost] via [CelebrationViewModel].
 */
@Singleton
class CelebrationCenter @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = context.getSharedPreferences("celebration_prefs", Context.MODE_PRIVATE)

    private val _current = MutableStateFlow<Celebration?>(null)
    val current: StateFlow<Celebration?> = _current.asStateFlow()

    fun show(celebration: Celebration) { _current.value = celebration }
    fun dismiss() { _current.value = null }

    /** Celebrate when the server-computed streak grows past the last one seen. */
    fun checkStreak(new: Int) {
        val old = prefs.getInt(KEY_STREAK, 0)
        prefs.edit().putInt(KEY_STREAK, new).apply()
        // old == 0 means first run — record silently, don't celebrate stale state.
        if (old > 0 && new > old) show(Celebration.Streak(new))
    }

    /** Celebrate when the user's level rises past the last one seen. */
    fun checkLevel(new: Int?) {
        if (new == null || new <= 0) return
        val old = prefs.getInt(KEY_LEVEL, 0)
        prefs.edit().putInt(KEY_LEVEL, new).apply()
        if (old > 0 && new > old) show(Celebration.LevelUp(new))
    }

    private companion object {
        const val KEY_STREAK = "celebrated_streak"
        const val KEY_LEVEL = "celebrated_level"
    }
}

@HiltViewModel
class CelebrationViewModel @Inject constructor(
    private val center: CelebrationCenter
) : ViewModel() {
    val current: StateFlow<Celebration?> = center.current
    fun dismiss() = center.dismiss()
}

// ── Design tokens (mirror iOS TK / takeover styles.css) ────────────────────
private val Ink = Color(0xFF111111)
private val Paper = Color(0xFFFAF8F4)
private val Sienna = Color(0xFFB85C38)
private val Gold = Color(0xFFA8893F)
private val Gold2 = Color(0xFFD4B06A)

private val Serif = FontFamily.Serif
private val Mono = FontFamily.Monospace

// Easing curves from the iOS timeline.
private val LeadCurve = CubicBezierEasing(0.74f, 0.04f, 0.22f, 1f)
private val BgCurve = CubicBezierEasing(0.72f, 0.06f, 0.2f, 1f)
private val WipeCurve = CubicBezierEasing(0.78f, 0f, 0.2f, 1f)
private val RiseCurve = CubicBezierEasing(0.22f, 0.9f, 0.26f, 1f)
private val RollCurve = CubicBezierEasing(0.85f, 0f, 0.13f, 1f)

// Content entrances are authored relative to takeover mount (t≈0.7s in the
// shared timeline) — call sites add this to the design delay.
private const val MOUNT = 700

private data class TakeoverConfig(
    val bg: Color,
    val fg: Color,
    val lead: Color,
    val rightText: String,
    val footText: String
)

private fun configFor(c: Celebration): TakeoverConfig = when (c) {
    is Celebration.Shelved -> {
        val foot = if (c.author.isEmpty()) c.title else "${c.title} — ${c.author}"
        when (c.shelf) {
            CelebShelf.Tbr -> TakeoverConfig(Ink, Paper, Sienna, "To be read", foot)
            CelebShelf.Bookshelf -> TakeoverConfig(Paper, Ink, Ink, "Bookshelf", foot)
            CelebShelf.Read -> TakeoverConfig(Paper, Ink, Ink, "Read", foot)
        }
    }
    is Celebration.Streak -> TakeoverConfig(Ink, Paper, Sienna, "Daily log", "Read every day")
    is Celebration.LevelUp -> TakeoverConfig(
        Ink, Paper, Gold, "Tier change",
        "The Reading Order · Tier ${ReaderLevel.numeral(c.level)} of VI"
    )
}

/** Mount once, above the tab bar / write sheet. */
@Composable
fun CelebrationOverlayHost() {
    val vm: CelebrationViewModel = hiltViewModel()
    val current by vm.current.collectAsState()
    val c = current ?: return
    // key() restarts the phase machine if a new celebration lands mid-play.
    key(c) {
        TakeoverScaffold(config = configFor(c), onDone = vm::dismiss) {
            when (c) {
                is Celebration.Shelved -> ShelvedPoster(c.shelf, c.title, c.author, c.coverUrl)
                is Celebration.Streak -> StreakPoster(c.days)
                is Celebration.LevelUp -> LevelUpPoster(c.level)
            }
        }
    }
}

// ── Scaffold: slab wipe in → poster hold → wipe out ────────────────────────
//   0      lead slab rises from the bottom to cover
//   150    poster bg reveals beneath it, bottom-up
//   320    lead exits through the top; content entrances play (own delays)
//   2960   whole takeover wipes out toward the top
//   3520   done
@Composable
private fun TakeoverScaffold(
    config: TakeoverConfig,
    onDone: () -> Unit,
    content: @Composable () -> Unit
) {
    var leadPhase by remember { mutableStateOf(0) } // 0 below · 1 covering · 2 exited top
    var bgRevealed by remember { mutableStateOf(false) }
    var wipedOut by remember { mutableStateOf(false) }

    val leadFrac by animateFloatAsState(
        targetValue = when (leadPhase) { 0 -> 1f; 1 -> 0f; else -> -1f },
        animationSpec = tween(if (leadPhase == 1) 320 else 380, easing = LeadCurve),
        label = "lead"
    )
    val bgFrac by animateFloatAsState(
        targetValue = if (bgRevealed) 1f else 0f,
        animationSpec = tween(460, delayMillis = 150, easing = BgCurve),
        label = "bg"
    )
    val wipeFrac by animateFloatAsState(
        targetValue = if (wipedOut) 1f else 0f,
        animationSpec = tween(520, easing = WipeCurve),
        label = "wipe"
    )

    LaunchedEffect(Unit) {
        leadPhase = 1; bgRevealed = true
        delay(320); leadPhase = 2
        delay(2640); wipedOut = true
        delay(560); onDone()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onDone() } // tap to skip
            // Wipe-out: viewport collapses toward the top edge.
            .drawWithContent {
                val visible = size.height * (1f - wipeFrac)
                clipRect(top = 0f, bottom = visible) { this@drawWithContent.drawContent() }
            }
    ) {
        // Poster bg + chrome + content, revealed bottom-up behind the lead.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(config.bg)
                .drawWithContent {
                    val h = size.height
                    clipRect(top = h * (1f - bgFrac), bottom = h) { this@drawWithContent.drawContent() }
                }
        ) {
            Chrome(config)
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { content() }
        }

        // Lead slab: rises to cover, then exits through the top.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { translationY = leadFrac * size.height }
                .background(config.lead)
        )
    }
}

// ── Brutalist chrome: grid, inset frame, corner ticks, top row, foot ───────
@Composable
private fun Chrome(config: TakeoverConfig) {
    Box(Modifier.fillMaxSize()) {
        GridLines(config.fg)
        // Inset frame.
        Box(
            Modifier
                .fillMaxSize()
                .padding(12.dp)
                .drawWithContent {
                    drawContent()
                    drawRect(config.fg.copy(alpha = 0.16f), style = Stroke(1f))
                }
        )
        CornerTicks(config.fg)

        Column(Modifier.fillMaxSize()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 58.dp, start = 22.dp, end = 22.dp)
                    .entrance(EntranceKind.Fade, 380 + MOUNT),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MonoLabel("PAPERBOXD", config.fg)
                MonoLabel(config.rightText.uppercase(), config.fg)
            }
            Spacer(Modifier.weight(1f))
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 40.dp, start = 22.dp, end = 22.dp)
                    .entrance(EntranceKind.Fade, 700 + MOUNT),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = config.footText.uppercase(),
                    color = config.fg.copy(alpha = 0.55f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = TextStyle(fontFamily = Mono, fontSize = 8.sp, letterSpacing = 2.sp)
                )
            }
        }
    }
}

@Composable
private fun MonoLabel(text: String, color: Color) {
    Text(
        text = text,
        color = color.copy(alpha = 0.55f),
        style = TextStyle(fontFamily = Mono, fontSize = 8.sp, letterSpacing = 2.2.sp)
    )
}

@Composable
private fun GridLines(color: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val c = color.copy(alpha = 0.055f)
        var x = 39f
        while (x < size.width) { drawLine(c, Offset(x, 0f), Offset(x, size.height), 1f); x += 39f }
        var y = 41f
        while (y < size.height) { drawLine(c, Offset(0f, y), Offset(size.width, y), 1f); y += 41f }
    }
}

@Composable
private fun CornerTicks(color: Color) {
    Canvas(Modifier.fillMaxSize()) {
        val c = color.copy(alpha = 0.6f)
        val inset = 18f
        val topY = 62f
        for (i in 0 until 4) {
            val x = if (i % 2 == 0) inset else size.width - inset
            val y = if (i < 2) topY else size.height - topY
            drawLine(c, Offset(x, y - 4.5f), Offset(x, y + 4.5f), 1f)
            drawLine(c, Offset(x - 4.5f, y), Offset(x + 4.5f, y), 1f)
        }
    }
}

// ── Entrance modifiers (rise / slam / fade) ────────────────────────────────
private enum class EntranceKind { Rise, Slam, Fade }

@Composable
private fun Modifier.entrance(kind: EntranceKind, delayMs: Int): Modifier {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { delay(delayMs.toLong()); shown = true }
    val spec = when (kind) {
        EntranceKind.Rise -> tween<Float>(480, easing = RiseCurve)
        EntranceKind.Slam -> spring<Float>(dampingRatio = 0.72f, stiffness = 158f)
        EntranceKind.Fade -> tween<Float>(420, easing = EaseInOut)
    }
    val p by animateFloatAsState(if (shown) 1f else 0f, spec, label = "entrance")
    return this.graphicsLayer {
        alpha = p
        if (kind == EntranceKind.Rise) translationY = (1f - p) * 26.dp.toPx()
        if (kind == EntranceKind.Slam) { val s = 1f + (1f - p) * 0.32f; scaleX = s; scaleY = s }
    }
}

/** Horizontal rules that draw in from their edges, flanking a mono label. */
@Composable
private fun RuleLabel(label: String, color: Color, delayMs: Int, modifier: Modifier = Modifier) {
    var drawn by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { drawn = true }
    val rule by animateFloatAsState(
        if (drawn) 1f else 0f,
        tween(520, delayMillis = delayMs, easing = CubicBezierEasing(0.7f, 0f, 0.2f, 1f)),
        label = "rule"
    )
    Row(modifier, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .graphicsLayer { scaleX = rule; transformOrigin = TransformOrigin(0f, 0.5f) }
                .background(color)
        )
        Text(
            text = label.uppercase(),
            color = color,
            modifier = Modifier.entrance(EntranceKind.Fade, delayMs + 40),
            style = TextStyle(fontFamily = Mono, fontSize = 10.sp, letterSpacing = 4.sp)
        )
        Box(
            Modifier
                .weight(1f)
                .height(1.dp)
                .graphicsLayer { scaleX = rule; transformOrigin = TransformOrigin(1f, 0.5f) }
                .background(color)
        )
    }
}

// ── Streak poster (ink · sienna lead · giant number roll) ──────────────────
@Composable
private fun StreakPoster(days: Int) {
    var rolled by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { rolled = true }
    val fontPx = 150
    // Float + graphicsLayer, not animateDpAsState + offset(): a Dp offset relayouts
    // the 150sp glyph every frame; a layer translation stays in the draw phase.
    val rollP by animateFloatAsState(
        if (rolled) 1f else 0f,
        tween(640, delayMillis = 560 + MOUNT, easing = RollCurve),
        label = "roll"
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "🔥",
            fontSize = 46.sp,
            modifier = Modifier.entrance(EntranceKind.Slam, 360 + MOUNT)
        )
        Box(Modifier.height(fontPx.dp).clipToBounds()) {
            Column(Modifier.graphicsLayer { translationY = -rollP * fontPx.dp.toPx() }) {
                RollDigits("${days - 1}", fontPx)
                RollDigits("$days", fontPx)
            }
        }
        RuleLabel("Day streak", Paper, 820 + MOUNT, Modifier.width(230.dp).padding(top = 12.dp))
        Text(
            text = "+1 · STREAK KEPT",
            color = Sienna,
            modifier = Modifier.padding(top = 18.dp).entrance(EntranceKind.Rise, 980 + MOUNT),
            style = TextStyle(fontFamily = Mono, fontSize = 9.sp, letterSpacing = 2.sp)
        )
    }
}

@Composable
private fun RollDigits(s: String, sizePx: Int) {
    Box(Modifier.height(sizePx.dp), contentAlignment = Alignment.Center) {
        Text(
            text = s,
            color = Paper,
            maxLines = 1,
            style = TextStyle(
                fontFamily = Serif, fontWeight = FontWeight.SemiBold,
                fontSize = sizePx.sp, letterSpacing = (-3).sp
            )
        )
    }
}

// ── Shelved / TBR / Read poster (cover slam + rules) ───────────────────────
@Composable
private fun ShelvedPoster(shelf: CelebShelf, title: String, author: String, coverUrl: String?) {
    val label = when (shelf) {
        CelebShelf.Bookshelf -> "Shelved"; CelebShelf.Tbr -> "Want to read"; CelebShelf.Read -> "Read"
    }
    val sub = when (shelf) {
        CelebShelf.Bookshelf -> "Added to your bookshelf"
        CelebShelf.Tbr -> "Patience is literary"
        CelebShelf.Read -> "Another spine on the shelf"
    }
    val fg = if (shelf == CelebShelf.Tbr) Paper else Ink
    val coverGradient = when (shelf) {
        CelebShelf.Tbr -> Brush.linearGradient(listOf(Color(0xFF4A5A3B), Color(0xFF242E17)))
        CelebShelf.Bookshelf -> Brush.linearGradient(listOf(Color(0xFF7A4A2E), Color(0xFF3D2114)))
        CelebShelf.Read -> Brush.linearGradient(listOf(Color(0xFF3B4A6B), Color(0xFF141C33)))
    }
    val shadow = if (shelf == CelebShelf.Tbr) Sienna.copy(alpha = 0.9f) else Ink.copy(alpha = 0.92f)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(198.dp, 286.dp)
                .graphicsLayer { rotationZ = if (shelf == CelebShelf.Tbr) 2.5f else -2.5f }
                .entrance(EntranceKind.Slam, 420 + MOUNT)
        ) {
            // Hard offset drop-shadow slab.
            Box(Modifier.size(186.dp, 274.dp).offset(x = 12.dp, y = 12.dp).background(shadow))
            // The book's real cover, square-cornered to keep the brutalist slab
            // edge. Falls back to the typographic cover while loading or when the
            // book has no artwork.
            val secureUrl = coverUrl?.replace("http://", "https://")?.takeIf { it.isNotEmpty() }
            Box(Modifier.size(186.dp, 274.dp).background(coverGradient)) {
                // Poster type sits underneath and the artwork paints over it once
                // decoded. Plain Image + painter, never SubcomposeAsyncImage — that
                // subcomposes during layout, which janks the slam spring.
                TypographicCover(title, author)
                if (secureUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(model = secureUrl),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
        RuleLabel(label, fg, 820 + MOUNT, Modifier.width(250.dp).padding(top = 34.dp))
        Text(
            text = sub.uppercase(),
            color = fg.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 16.dp).entrance(EntranceKind.Rise, 980 + MOUNT),
            style = TextStyle(fontFamily = Mono, fontSize = 9.sp, letterSpacing = 2.sp)
        )
    }
}

/** Poster-type stand-in used until the real cover loads, or when there is none. */
@Composable
private fun TypographicCover(title: String, author: String) {
    Column(Modifier.fillMaxSize().padding(14.dp)) {
        Text(
            text = author.uppercase(),
            color = Color.White.copy(alpha = 0.75f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.End,
            style = TextStyle(fontFamily = Mono, fontSize = 8.sp, letterSpacing = 1.6.sp)
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = title,
            color = Color.White,
            maxLines = 4,
            overflow = TextOverflow.Ellipsis,
            style = TextStyle(
                fontFamily = Serif, fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp, letterSpacing = (-0.5).sp
            )
        )
    }
}

// ── Level-up poster (ink · gold lead · sheen name) ─────────────────────────
@Composable
private fun LevelUpPoster(level: Int) {
    var sheenGo by remember { mutableStateOf(false) }
    var textWidth by remember { mutableStateOf(0) }
    LaunchedEffect(Unit) { delay((980 + MOUNT).toLong()); sheenGo = true }
    val sheen by animateFloatAsState(
        if (sheenGo) -1f else 1f,
        tween(1100, easing = EaseInOut),
        label = "sheen"
    )

    Box(contentAlignment = Alignment.Center) {
        // Giant faint roman numeral behind everything.
        Text(
            text = ReaderLevel.numeral(level),
            color = Gold2.copy(alpha = 0.14f),
            modifier = Modifier.offset(y = (-30).dp).entrance(EntranceKind.Fade, 300 + MOUNT),
            style = TextStyle(fontFamily = Serif, fontWeight = FontWeight.SemiBold, fontSize = 280.sp)
        )
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "LEVEL ${ReaderLevel.numeral(level)} REACHED",
                color = Paper.copy(alpha = 0.65f),
                modifier = Modifier.entrance(EntranceKind.Rise, 420 + MOUNT),
                style = TextStyle(fontFamily = Mono, fontSize = 9.sp, letterSpacing = 4.sp)
            )
            // Gold sheen sweeps across the level name.
            val w = textWidth.toFloat().coerceAtLeast(1f)
            val brush = Brush.linearGradient(
                colors = listOf(Gold, Gold2, Color(0xFFF2E3AE), Gold2, Gold),
                start = Offset(sheen * w - w * 0.35f, 0f),
                end = Offset(sheen * w + w * 0.35f, 0f)
            )
            Text(
                text = ReaderLevel.name(level),
                maxLines = 1,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .onSizeChanged { textWidth = it.width }
                    .entrance(EntranceKind.Slam, 520 + MOUNT),
                style = TextStyle(
                    brush = brush, fontFamily = Serif, fontWeight = FontWeight.SemiBold,
                    fontSize = 72.sp, letterSpacing = (-1.5).sp
                )
            )
            val fromTo = if (level > 1) "${ReaderLevel.name(level - 1)}  →  ${ReaderLevel.name(level)}"
            else ReaderLevel.name(level)
            RuleLabel(fromTo, Paper, 880 + MOUNT, Modifier.width(260.dp).padding(top = 22.dp))
            Text(
                text = "Where pages turn into prestige.",
                color = Paper.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 20.dp).entrance(EntranceKind.Rise, 1040 + MOUNT),
                style = TextStyle(fontFamily = Serif, fontStyle = FontStyle.Italic, fontSize = 14.sp)
            )
        }
    }
}
