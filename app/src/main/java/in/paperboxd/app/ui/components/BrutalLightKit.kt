package `in`.paperboxd.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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

/**
 * "‹ STEP n / total" + a dash progress track — iOS `PBStepHeader` twin.
 * Shared by the paper-themed signup screen (Auth) and the onboarding screens
 * so the run reads as one continuous three-step flow.
 */
@Composable
fun PBStepHeader(step: Int, total: Int = 3, onBack: (() -> Unit)? = null, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            onBack?.let {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack, null,
                    tint = HL.Ink,
                    modifier = Modifier.size(14.dp).clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = it
                    )
                )
            }
            Text(
                "STEP $step / $total",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 1.4.sp,
                color = HL.Muted
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 1..total) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(3.dp)
                        .background(if (i <= step) HL.Ink else HL.Card)
                        .border(1.dp, HL.Ink.copy(alpha = if (i <= step) 0f else 0.3f))
                )
            }
        }
    }
}

/**
 * Black CTA plate on the paper ground — iOS `OnboardingPrimaryButton` twin.
 * Shared by the paper-themed signup screen and every onboarding step so the
 * run has one primary-button look.
 */
@Composable
fun PBPrimaryButton(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
) {
    val alpha by animateFloatAsState(if (enabled) 1f else 0.45f, tween(180), label = "cta-alpha")
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer { this.alpha = alpha }
            .brutalButton(onClick = onClick, fill = HL.Ink, enabled = enabled && !loading),
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = HL.Cream, strokeWidth = 2.dp, modifier = Modifier.size(22.dp)
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(title, fontSize = 14.5.sp, fontWeight = FontWeight.SemiBold, color = HL.Cream)
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward, null,
                    tint = HL.Cream, modifier = Modifier.size(15.dp)
                )
            }
        }
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
