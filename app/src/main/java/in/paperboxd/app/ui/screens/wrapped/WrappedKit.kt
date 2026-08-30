package `in`.paperboxd.app.ui.screens.wrapped

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import `in`.paperboxd.app.ui.theme.PBScript
import `in`.paperboxd.app.ui.theme.PBSans
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

// Print-in-motion primitives for Monthly Wrapped: type sets itself out of its
// own baseline, paper slides, ink stamps down. Twin of the iOS WrappedKit.swift.

// MARK: - Palette + type

object PBW {
    val Ink = Color(0xFF1A1410)
    val InkDeep = Color(0xFF120E0B)
    val Cream = Color(0xFFF5EDE0)
    val Terra = Color(0xFFD97757)
    val TerraDeep = Color(0xFF8C4A3A)
    val Brown = Color(0xFF6B3520)
    val Amber = Color(0xFFE8B04B)
    val Muted = Color(0xFF8A7A68)

    /** The chapters are laid out against this width and scaled to the device. */
    val DesignWidth = 402.dp
    val DesignHeight = 874.dp

    val Display = FontFamily.Serif
    val Mono = FontFamily.Monospace
    val Sans = PBSans
    val Script = PBScript

    val Ease = CubicBezierEasing(0.2f, 0.9f, 0.24f, 1f)
    val EaseInk = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1f)

    /** Spine and genre colours — the backend sends no colour, only order. */
    private val spines = listOf(
        Color(0xFF8C4A3A), Color(0xFF5A6B4A), Color(0xFFC9B48A),
        Color(0xFFA85D6B), Color(0xFF3D4A5C), Color(0xFF7A5C8A)
    )
    private val spineAccents = listOf(
        Color(0xFFE8B04B), Color(0xFFD9C77B), Color(0xFF3A3A3A),
        Color(0xFFF5EDE0), Color(0xFFD97757), Color(0xFFEFE6F2)
    )
    private val genreColors = listOf(Cream, Ink, Amber, Brown, TerraDeep)

    fun spine(i: Int) = spines[i.mod(spines.size)]
    fun spineAccent(i: Int) = spineAccents[i.mod(spineAccents.size)]
    fun genre(i: Int) = genreColors[i.mod(genreColors.size)]
}

/**
 * True while rendering the share card. Every primitive draws its finished state
 * instead of animating — a `graphicsLayer.record` of a half-run animation would
 * export a half-drawn card.
 */
val LocalWrappedStill = staticCompositionLocalOf { false }

@Composable
fun WrappedStill(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalWrappedStill provides true, content = content)
}

/**
 * 0f before the reveal, 1f after. Still mode starts settled; everything else
 * waits out its delay and then runs the shared easing.
 */
@Composable
private fun reveal(delayMillis: Int, durationMillis: Int): Float {
    val still = LocalWrappedStill.current
    var on by remember { mutableStateOf(still) }
    LaunchedEffect(Unit) {
        if (!still) {
            delay(delayMillis.toLong())
            on = true
        }
    }
    val p by animateFloatAsState(
        targetValue = if (on) 1f else 0f,
        animationSpec = tween(durationMillis = if (still) 0 else durationMillis, easing = PBW.Ease),
        label = "wrapped-reveal"
    )
    return p
}

// MARK: - Reveals

/** A line rises out of its own baseline clip — the signature of this design. */
@Composable
fun PbRise(
    delayMillis: Int = 0,
    durationMillis: Int = 620,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val p = reveal(delayMillis, durationMillis)
    Box(
        modifier
            .clipToBounds()
            .graphicsLayer { translationY = (1f - p) * size.height * 1.05f }
    ) { content() }
}

@Composable
fun PbFade(
    delayMillis: Int = 0,
    durationMillis: Int = 700,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val p = reveal(delayMillis, durationMillis)
    Box(
        modifier.graphicsLayer {
            alpha = p
            translationY = (1f - p) * 9.dp.toPx()
        }
    ) { content() }
}

enum class SlideFrom { Start, End, Top }

@Composable
fun PbSlide(
    delayMillis: Int = 0,
    durationMillis: Int = 700,
    from: SlideFrom = SlideFrom.Start,
    distance: Dp = 40.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val p = reveal(delayMillis, durationMillis)
    Box(
        modifier.graphicsLayer {
            alpha = p
            val offset = (1f - p) * distance.toPx()
            when (from) {
                SlideFrom.Start -> translationX = -offset
                SlideFrom.End -> translationX = offset
                SlideFrom.Top -> translationY = -offset
            }
        }
    ) { content() }
}

/** Ink stamps down: overshoot scale, hard settle, faint rotation. */
@Composable
fun PbStamp(
    delayMillis: Int = 0,
    durationMillis: Int = 380,
    rotation: Float = 0f,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val still = LocalWrappedStill.current
    var on by remember { mutableStateOf(still) }
    LaunchedEffect(Unit) {
        if (!still) {
            delay(delayMillis.toLong())
            on = true
        }
    }
    val p by animateFloatAsState(
        targetValue = if (on) 1f else 0f,
        animationSpec = tween(if (still) 0 else durationMillis, easing = PBW.EaseInk),
        label = "wrapped-stamp"
    )
    Box(
        modifier.graphicsLayer {
            alpha = p
            val scale = 1.16f - 0.16f * p
            scaleX = scale
            scaleY = scale
            rotationZ = rotation
        }
    ) { content() }
}

/** A rule that draws itself across. */
@Composable
fun PbRule(
    delayMillis: Int = 0,
    durationMillis: Int = 700,
    color: Color = PBW.Terra,
    thickness: Dp = 2.dp,
    modifier: Modifier = Modifier
) {
    val p = reveal(delayMillis, durationMillis)
    Box(
        modifier
            .fillMaxWidth()
            .height(thickness)
            .graphicsLayer {
                scaleX = p
                transformOrigin = TransformOrigin(0f, 0.5f)
            }
            .background(color)
    )
}

/** A bar that grows from one edge. Used for genre blocks and every histogram. */
@Composable
fun PbGrow(
    delayMillis: Int = 0,
    durationMillis: Int = 900,
    vertical: Boolean = false,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val p = reveal(delayMillis, durationMillis)
    Box(
        modifier.graphicsLayer {
            if (vertical) {
                scaleY = p
                transformOrigin = TransformOrigin(0.5f, 1f)
            } else {
                scaleX = p
                transformOrigin = TransformOrigin(0f, 0.5f)
            }
        }
    ) { content() }
}

/** Counting number — the press running off copies. */
@Composable
fun PbCount(
    value: Int,
    delayMillis: Int = 0,
    durationMillis: Int = 1400,
    fontSize: Int = 92,
    color: Color = PBW.Amber,
    modifier: Modifier = Modifier
) {
    val still = LocalWrappedStill.current
    var shown by remember(value) { mutableIntStateOf(if (still) value else 0) }
    LaunchedEffect(value, still) {
        if (still) {
            shown = value
            return@LaunchedEffect
        }
        delay(delayMillis.toLong())
        val steps = max(1, durationMillis / 40)
        for (step in 1..steps) {
            val p = step.toFloat() / steps
            // Cubic ease-out, so the number lands rather than stopping dead.
            shown = (value * (1f - (1f - p).pow(3))).roundToInt()
            delay(40)
        }
        shown = value
    }
    Text(
        text = shown.formatted(),
        fontFamily = PBW.Display,
        fontWeight = FontWeight.Black,
        fontSize = fontSize.sp,
        lineHeight = (fontSize * 0.9f).sp,
        color = color,
        modifier = modifier
    )
}

// MARK: - Shared pieces

/** The running head on every page: mono, uppercase, wide tracking. */
@Composable
fun PbKicker(
    text: String,
    color: Color = PBW.Muted,
    delayMillis: Int = 0,
    modifier: Modifier = Modifier
) {
    PbFade(delayMillis, 520, modifier) {
        Text(
            text.uppercase(),
            fontFamily = PBW.Mono,
            fontSize = 10.5.sp,
            letterSpacing = 1.9.sp,
            color = color
        )
    }
}

/** A book as printed matter: spine, board, no artwork. */
@Composable
fun PbBookBlock(
    title: String,
    author: String = "",
    spine: Color,
    accent: Color,
    width: Dp = 96.dp,
    delayMillis: Int = 0,
    rotation: Float = 0f
) {
    PbStamp(delayMillis = delayMillis, rotation = rotation) {
        Box(
            Modifier
                .width(width)
                .height(width * 1.52f)
                .background(spine)
                .clipToBounds()
        ) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.14f), Color.Transparent))
                    )
            )
            Box(
                Modifier
                    .padding(start = width * 0.055f)
                    .width(1.5.dp)
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.22f))
            )
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(width * 0.1f)
                    .padding(start = width * 0.05f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    title,
                    fontFamily = PBW.Display,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = (width.value * 0.125f).sp,
                    lineHeight = (width.value * 0.14f).sp,
                    color = accent,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                if (author.isNotEmpty()) {
                    Text(
                        author.uppercase(),
                        fontFamily = PBW.Mono,
                        fontSize = (width.value * 0.072f).sp,
                        letterSpacing = 0.5.sp,
                        color = accent.copy(alpha = 0.72f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

/** Every chapter sits on this: full-bleed colour, the design's page margins. */
@Composable
fun WrappedScreen(
    background: Color = PBW.Ink,
    topPadding: Dp = 76.dp,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(background)
            .clipToBounds()
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(top = topPadding, bottom = 112.dp)
                .padding(horizontal = 30.dp),
            content = content
        )
    }
}

/** Thousands separators, matching the iOS `formatted()` the design calls for. */
fun Int.formatted(): String = "%,d".format(this)

/** Chip outline used by the archetype traits and the author note. */
fun Modifier.pbOutline(color: Color, width: Dp = 1.dp) = border(width, color)

/** The pause bars drawn over a held story. */
@Composable
fun PbPauseBars(color: Color) {
    androidx.compose.foundation.layout.Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        repeat(2) {
            Box(Modifier.size(width = 5.dp, height = 26.dp).background(color.copy(alpha = 0.9f)))
        }
    }
}
