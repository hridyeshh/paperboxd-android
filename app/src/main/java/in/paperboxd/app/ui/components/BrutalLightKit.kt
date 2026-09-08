package `in`.paperboxd.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

/**
 * Fixed light-mode palette for the brutalist "paper" screens (Home, Book page).
 * Mirrors iOS BrutalKit.BK + HomeView's hl* tokens — those screens are light-only
 * regardless of system theme, so these are constants, not MaterialTheme colors.
 */
object HL {
    val Paper = Color(0xFFF2EDE1)   // page background
    val Paper2 = Color(0xFFE9E2D1)  // recessed fills (progress track, shimmer)
    val Card = Color(0xFFFDFBF6)    // card face, lighter than paper so ink border reads
    val Ink = Color(0xFF151513)     // text + borders + hard shadows
    val Muted = Color(0xFF6A6456)   // secondary text
    val Accent = Color(0xFFD23B26)  // red progress fill / retry

    // Welcome / onboarding tokens — twin of iOS BK. The auth and onboarding run
    // adds saturated cloth colours so the shelf reads as bound books.
    val Crimson = Color(0xFFC0271C) // primary CTA
    val Ochre = Color(0xFFC9962B)
    val Forest = Color(0xFF1B4B3C)
    val Cream = Color(0xFFFFF6E9)   // type on crimson / ink
    val Field = Color(0xFFF6EFDF)   // input wells
    val Sepia = Color(0xFF7A4A2E)   // serif italic sub-heads
    val Sage = Color(0xFF2F7D5A)    // "available" affirmation
    val Plank = Color(0xFF3A2C1C)   // shelf board and its shadow
}

/**
 * Brutalist plate: hard ink offset shadow behind the face, flat fill, hard border.
 * Reserves [offset] of outer padding so the shadow doesn't clip against siblings
 * (same trick as iOS `.padding(.trailing/.bottom)`).
 */
fun Modifier.brutalPlate(
    fill: Color = HL.Card,
    border: Color = HL.Ink,
    borderWidth: Dp = 1.5.dp,
    offset: Dp = 4.dp,
    shadow: Color = border
): Modifier = this
    .padding(end = offset, bottom = offset)
    .drawBehind {
        val off = offset.toPx()
        drawRect(color = shadow, topLeft = Offset(off, off), size = size)
    }
    .background(fill)
    .border(borderWidth, border)

/**
 * Interactive brutalist button — iOS `BrutalButtonStyle` twin. Identical to
 * [brutalPlate] at rest, but on press the hard offset shadow collapses to 0 and
 * the face pushes into where the shadow was. Instant (no easing), brutalist.
 * Owns the click so it can read the press state; pass [onClick] instead of a
 * separate `.clickable`.
 */
fun Modifier.brutalButton(
    onClick: () -> Unit,
    fill: Color = HL.Card,
    border: Color = HL.Ink,
    borderWidth: Dp = 2.dp,
    offset: Dp = 4.dp,
    shadow: Color = border,
    enabled: Boolean = true
): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    this
        .padding(end = offset, bottom = offset)
        .drawBehind {
            val o = offset.toPx()
            val s = if (pressed) 0f else o
            drawRect(color = shadow, topLeft = Offset(s, s), size = size)
        }
        .graphicsLayer {
            val o = offset.toPx()
            val f = if (pressed) o else 0f
            translationX = f
            translationY = f
        }
        .background(fill)
        .border(borderWidth, border)
        .clickable(
            interactionSource = interaction,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )
}

/**
 * Pop keyframe — iOS `popEffect` twin. Each time [trigger] changes (bump an Int),
 * fires scale 0.6 → 1.35 → 1.0 with a −12° → 4° → 0° rotation wobble. Reused for
 * like-on and star-fill. trigger 0 = no-op so it doesn't fire on first composition.
 */
fun Modifier.popEffect(trigger: Int): Modifier = composed {
    val scale = remember { Animatable(1f) }
    val rotation = remember { Animatable(0f) }
    LaunchedEffect(trigger) {
        if (trigger == 0) return@LaunchedEffect
        scale.snapTo(0.6f)
        rotation.snapTo(-12f)
        launch {
            // Bounded 320ms keyframe — twin of iOS SpringKeyframe(1.35, .snappy) →
            // SpringKeyframe(1.0, .bouncy), undershoot included. An unbounded spring
            // here keeps ringing after the icon looks settled, which reads floppy.
            scale.animateTo(
                targetValue = 1f,
                animationSpec = keyframes {
                    durationMillis = 320
                    0.6f at 0 using FastOutSlowInEasing
                    1.35f at 140 using FastOutSlowInEasing
                    0.96f at 240 using FastOutSlowInEasing
                    1f at 320
                }
            )
        }
        launch {
            rotation.animateTo(4f, tween(140))
            rotation.animateTo(0f, tween(180))
        }
    }
    graphicsLayer {
        scaleX = scale.value
        scaleY = scale.value
        rotationZ = rotation.value
    }
}

/** Mono uppercase eyebrow with wide tracking — iOS `Eyebrow` twin. */
@Composable
fun EyebrowText(
    text: String,
    color: Color = HL.Muted,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 2.sp,
        color = color,
        modifier = modifier
    )
}

/**
 * 45° hazard stripes — iOS `HatchFill` twin. Marks destructive controls without
 * spending a colour on them, so the settings palette stays monochrome.
 * [period] is the stripe pair's width in px.
 */
fun hatchBrush(
    base: Color = HL.Ink,
    stripe: Color = Color(0xFF33332E),
    period: Float = 28f
): Brush = Brush.linearGradient(
    0.0f to base,
    0.5f to base,
    0.5f to stripe,
    1.0f to stripe,
    start = Offset(0f, 0f),
    end = Offset(period, period),
    tileMode = TileMode.Repeated
)
