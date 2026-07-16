package `in`.paperboxd.app.ui.screens.scan

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.random.Random

/**
 * Scan & Know — brutalist design kit (light theme). iOS `SK` twin.
 *
 * Raw BRUTALISM (analyzing → reveal) resolving into calm MINIMALISM (breakdown).
 * Hard ink frames, mono readouts and hard offset shadows give way to whitespace,
 * hairlines and serif. Monochrome by system — the book cover is the only color.
 */
object SK {
    val bg = Color(0xFFF4F3F0)
    val bgSoft = Color(0xFFF8F7F5)
    val bgSofter = Color(0xFFFCFBFA)
    val panel = Color.White
    val ink = Color(0xFF141414)
    val sub = Color(0xFF6A665E)
    val faint = Color(0xFFA6A299)
    val line = Color(0xFF141414)

    /** Cover-green accent — pulled straight from the book cover. */
    val accent = Color(0xFF5B8A4E)

    /** Game canvas paper. */
    val gameBg = Color(0xFFFCFBF7)

    /** Hairline used in the minimalist breakdown. */
    val border = Color(0xFF141414).copy(alpha = 0.10f)
    val track = Color(0xFF141414).copy(alpha = 0.10f)

    /** Deep jewel book-spine tones (covers = color), shared with the games. */
    val spines = listOf(
        Color(0xFF6B2A3A), Color(0xFF3A4A2A), Color(0xFF2A4A6A), Color(0xFF5A3A6A),
        Color(0xFF2A6A5A), Color(0xFF7A4A2A), Color(0xFF8A6A2A), Color(0xFF3A3A44),
    )

    /** Linear cover gradient used by the tinted cover fallback. */
    val coverGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF5B8A4E), Color(0xFF2C5132), Color(0xFF16271A))
    )
}

// ── Brutalist book cover (hard ink frame + optional offset shadow) ──────────

/**
 * Book cover tile — iOS `ScanCover` twin. Real artwork via `cover_url` when
 * present; otherwise a green editorial gradient with the title set in serif.
 */
@Composable
fun ScanCover(result: ScanResult, width: Dp, shadow: Dp = 0.dp) {
    val height = width * 1.5f
    Box(
        modifier = Modifier
            .padding(end = shadow, bottom = shadow)
            .drawBehind {
                if (shadow > 0.dp) {
                    val off = shadow.toPx()
                    drawRect(SK.line, topLeft = Offset(off, off), size = size)
                }
            }
            .size(width, height)
    ) {
        val url = result.coverUrl?.replace("http://", "https://")
        if (url != null) {
            AsyncImage(
                model = url,
                contentDescription = result.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize().background(SK.coverGradient)) {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .padding(start = width * 0.16f)
                        .width(1.dp)
                        .background(Color.White.copy(alpha = 0.28f))
                )
                Text(
                    text = result.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = (width.value * 0.155f).sp,
                    lineHeight = (width.value * 0.19f).sp,
                    color = Color.White.copy(alpha = 0.96f),
                    maxLines = 3,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(width * 0.12f)
                )
            }
        }
        Box(Modifier.fillMaxSize().border(2.dp, SK.line))
    }
}

// ── Mono helpers ─────────────────────────────────────────────────────────────

/** Wide-tracked uppercase mono label — iOS `MonoLabel` twin. */
@Composable
fun MonoLabel(
    text: String,
    size: Float = 10f,
    tracking: Float = 1.8f,
    color: Color = SK.sub,
    weight: FontWeight = FontWeight.Normal,
    modifier: Modifier = Modifier,
    textAlign: TextAlign? = null
) {
    Text(
        text = text.uppercase(),
        fontFamily = FontFamily.Monospace,
        fontWeight = weight,
        fontSize = size.sp,
        letterSpacing = tracking.sp,
        color = color,
        textAlign = textAlign,
        modifier = modifier
    )
}

// ── Count-up number ──────────────────────────────────────────────────────────

/**
 * Placeholder that rolls random digits while the real number is still loading,
 * so the source cell reads as "tallying" rather than blank — iOS `RollingNumber`.
 */
@Composable
fun RollingNumber(
    fontSize: Float = 21f,
    color: Color = SK.sub,
    digits: Int = 3
) {
    var value by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        val upper = 10.0.pow(digits).toInt()
        while (true) {
            delay(80)
            value = Random.nextInt(upper)
        }
    }
    Text(
        text = "$value",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold,
        fontSize = fontSize.sp,
        color = color
    )
}

/** Eases 0 → `target` over `durationMs`, monospaced — iOS `CountUpText` twin. */
@Composable
fun CountUpText(
    target: Int,
    durationMs: Int = 1800,
    fontSize: Float = 21f,
    color: Color = SK.ink,
    fontWeight: FontWeight = FontWeight.SemiBold,
    grouping: Boolean = true
) {
    var value by remember { mutableIntStateOf(0) }
    LaunchedEffect(target) {
        if (target <= 0) { value = 0; return@LaunchedEffect }
        val steps = minOf(target, 60)
        for (s in 1..steps) {
            val p = s / steps.toFloat()
            delay((durationMs / steps).toLong())
            val eased = 1 - (1 - p).pow(3)
            value = (eased * target).toInt()
        }
        value = target
    }
    Text(
        text = if (grouping) "%,d".format(value) else "$value",
        fontFamily = FontFamily.Monospace,
        fontWeight = fontWeight,
        fontSize = fontSize.sp,
        color = color
    )
}

// ── Crop-mark corners (brutalist frame accents) ─────────────────────────────

/** Four corner crop marks — iOS `CropCorners` twin. Draw over a frame. */
fun Modifier.cropCorners(
    color: Color = SK.accent,
    length: Dp = 16.dp,
    weight: Dp = 3.dp
): Modifier = drawBehind {
    val l = length.toPx()
    val w = size.width
    val h = size.height
    val p = Path().apply {
        moveTo(0f, l); lineTo(0f, 0f); lineTo(l, 0f)
        moveTo(w - l, 0f); lineTo(w, 0f); lineTo(w, l)
        moveTo(w, h - l); lineTo(w, h); lineTo(w - l, h)
        moveTo(l, h); lineTo(0f, h); lineTo(0f, h - l)
    }
    drawPath(p, color, style = Stroke(width = weight.toPx()))
}

// ── Brutalist primary button (ink fill, hard offset shadow) ─────────────────

/** Ink-filled brutalist CTA — iOS `BruButton` twin. */
@Composable
fun BruButton(
    title: String,
    modifier: Modifier = Modifier,
    glyph: String? = null,
    leading: ImageVector? = null,
    trailing: ImageVector? = null,
    onClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .padding(end = 4.dp, bottom = 4.dp)
            .drawBehind {
                val off = 4.dp.toPx()
                drawRect(SK.ink.copy(alpha = 0.78f), topLeft = Offset(off, off), size = size)
            }
            .fillMaxWidth()
            .background(SK.ink)
            .border(1.875.dp, SK.line)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 15.dp)
    ) {
        if (glyph != null) {
            Text(glyph, color = SK.panel, fontSize = 13.sp)
        }
        if (leading != null) {
            Icon(leading, contentDescription = null, tint = SK.panel, modifier = Modifier.size(16.dp))
        }
        Text(
            text = title.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp,
            letterSpacing = 1.sp,
            color = SK.panel
        )
        if (trailing != null) {
            Icon(trailing, contentDescription = null, tint = SK.panel, modifier = Modifier.size(16.dp))
        }
    }
}

/** Small blinking square dot used across the brutalist chrome — iOS `BlinkDot`. */
@Composable
fun BlinkDot(color: Color = SK.accent, size: Dp = 8.dp) {
    val transition = rememberInfiniteTransition(label = "blink")
    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "blink-alpha"
    )
    Box(Modifier.size(size).alpha(alpha).background(color))
}

/** Hairline divider used across the minimalist screens. */
@Composable
fun Hairline(modifier: Modifier = Modifier, color: Color = SK.border, thickness: Dp = 1.dp) {
    Box(modifier.fillMaxWidth().height(thickness).background(color))
}
