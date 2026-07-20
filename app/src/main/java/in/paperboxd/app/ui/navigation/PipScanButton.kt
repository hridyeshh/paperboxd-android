package `in`.paperboxd.app.ui.navigation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * Pip — the scan buddy. iOS PipScanButton + web design-system twin.
 *
 * Three independent motion systems layer on top of each other, exactly like
 * the web reference:
 *  1. Line-boil — every path point is re-jittered off a seeded RNG that
 *     re-rolls every [BOIL_MS] (~6.7fps at speed 1). This deliberately low,
 *     stepped rate is what makes the ink read as hand-drawn instead of glassy.
 *     Driven off withFrameMillis (not a coalescible timer). amp = 0.55*wobble.
 *  2. Sway — whole-body rotate ±SWAY_DEG over 3.4s, origin at 50%/78%.
 *  3. Pose machine — idle blink/glance loop + tap startle.
 */
private const val BOIL_MS = 150L          // 150ms ≈ 6.7fps re-jitter
// ponytail: WOBBLE and SWAY_DEG are the calibration knobs — turn these, not the
// per-point amps in drawPip, which all derive from AMP.
private const val WOBBLE = 0.5f           // global wobble multiplier
private const val SWAY_DEG = 0.9f         // whole-body sway half-range, degrees
private const val BLINK_EVERY = 3.4       // seconds between blink attempts
private const val AMP = 0.55f * WOBBLE     // jitter amplitude in glyph units (40-wide canvas)

@Composable
fun PipScanButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var blink by remember { mutableStateOf(false) }
    var gaze by remember { mutableFloatStateOf(0f) } // -1..1 eye drift
    var excited by remember { mutableStateOf(false) }
    var boilSeed by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    // System 1 — line boil. Re-roll the seed on a stepped ~150ms cadence,
    // gated off frame callbacks so Android's timer coalescing can't smooth it.
    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameMillis { now ->
                if (now - last >= BOIL_MS) {
                    last = now
                    boilSeed++
                }
            }
        }
    }

    // System 2 — sway. ±SWAY_DEG over 3.4s, ease-in-out.
    val swayTransition = rememberInfiniteTransition(label = "pip-sway")
    val sway by swayTransition.animateFloat(
        initialValue = -SWAY_DEG,
        targetValue = SWAY_DEG,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pip-sway-angle"
    )

    // System 3 — idle pose machine: blink every few seconds, sometimes glance.
    LaunchedEffect(Unit) {
        while (true) {
            val wait = (BLINK_EVERY * (0.7 + Math.random() * 0.7) * 1000).toLong()
            delay(wait)
            if (Math.random() < 0.75) {
                blink = true
                delay(140)
                blink = false
            } else {
                gaze = if (Math.random() < 0.5) 1f else -1f
                delay(1100)
                gaze = 0f
            }
        }
    }

    Box(
        modifier = modifier
            .size(64.dp)
            .shadow(elevation = 10.dp, shape = CircleShape, clip = false)
            .clip(CircleShape)
            .background(Color.White)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                excited = true
                onClick()
                scope.launch {
                    delay(600)
                    excited = false
                }
            }
            .semantics { contentDescription = "Scan a book" }
    ) {
        Canvas(
            modifier = Modifier
                .size(40.dp, 44.dp)
                .align(Alignment.Center)
        ) {
            val pivot = Offset(size.width * 0.5f, size.height * 0.78f)
            val sc = if (excited) 1.12f else 1f
            scale(sc, sc, pivot) {
                rotate(sway, pivot) {
                    drawPip(seed = boilSeed, blink = blink, gaze = gaze, excited = excited)
                }
            }
        }
    }
}

/**
 * Pip's face on its own — no circle, no tap. Reused at rest by the Ask Jazy
 * entry + results header. Keeps the line boil, drops the sway and pose machine.
 */
@Composable
fun PipFace(
    modifier: Modifier = Modifier,
    thinking: Boolean = false
) {
    var boilSeed by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        var last = 0L
        while (true) {
            withFrameMillis { now ->
                if (now - last >= BOIL_MS) {
                    last = now
                    boilSeed++
                }
            }
        }
    }
    Canvas(modifier = modifier) {
        drawPip(seed = boilSeed, blink = false, gaze = if (thinking) 1f else 0f, excited = false)
    }
}

/** Deterministic per-(frame,point) noise in -1..1 — cheap hash, no state. */
private fun jitter(seed: Int, salt: Int): Float {
    var h = seed * 2654435761L + salt * 40503L
    h = h xor (h ushr 13)
    h *= -0x61c8864680b583ebL // 0x9E3779B97F4A7C15
    h = h xor (h ushr 32)
    val m = ((h % 1000) + 1000) % 1000
    return (m / 500f) - 1f
}

// MARK: - Ink drawing (40×44 canvas), every point re-jittered per boil seed.

private fun DrawScope.drawPip(seed: Int, blink: Boolean, gaze: Float, excited: Boolean) {
    val s = size.width / 40f // px per glyph unit
    val ink = Stroke(width = 2.7f * s, cap = StrokeCap.Round, join = StrokeJoin.Round)

    // Per-point jitter: each coordinate gets its own salt so the whole outline
    // boils independently, not as a rigid rotation. `k` walks the salt space.
    var k = 0
    fun jp(x: Float, y: Float, amp: Float = AMP): Offset {
        val jx = jitter(seed, k * 2 + 1) * amp
        val jy = jitter(seed, k * 2 + 2) * amp
        k++
        return Offset((x + jx) * s, (y + jy) * s)
    }

    // Wobbly closed-book cover.
    val outline = Path().apply {
        val p0 = jp(8.2f, 13f)
        moveTo(p0.x, p0.y)
        cubic(jp(8.6f, 9.2f), jp(10.5f, 6.9f), jp(13.5f, 6.4f))
        cubic(jp(17.5f, 5.8f), jp(22.5f, 6.6f), jp(26f, 6.2f))
        cubic(jp(28.8f, 6.0f), jp(30.4f, 8.2f), jp(30.6f, 11.5f))
        cubic(jp(30.9f, 18f), jp(31.3f, 27f), jp(30.9f, 33f))
        cubic(jp(30.7f, 36.4f), jp(28.8f, 38.4f), jp(26.2f, 38.6f))
        cubic(jp(21.5f, 38.9f), jp(16.8f, 39.3f), jp(13.2f, 38.9f))
        cubic(jp(10.4f, 38.6f), jp(8.7f, 36.2f), jp(8.4f, 33.5f))
        cubic(jp(7.9f, 26.5f), jp(8.1f, 19f), p0.let { Offset(8.2f * s, 13f * s) })
        close()
    }
    drawPath(outline, Color.Black, style = ink)

    // Detached spine line.
    val spine = Path().apply {
        val a = jp(33.6f, 10.5f)
        moveTo(a.x, a.y)
        cubic(jp(34.3f, 18f), jp(33.6f, 27f), jp(33.9f, 34f))
    }
    drawPath(spine, Color.Black, style = ink)

    // Pages block — gray bar, jittered position (amp*0.5).
    val pg = jp(37.2f - 1.8f, 22f - 7.5f, AMP * 0.5f)
    drawRoundRect(
        color = Color(0xFF595959),
        topLeft = pg,
        size = Size(3.6f * s, 15f * s),
        cornerRadius = CornerRadius(1.8f * s)
    )

    // Unibrow — jumps up when startled (amp*0.8).
    val lift = if (excited) -2.5f else 0f
    val brow = Path().apply {
        val b0 = jp(12.2f, 17.2f + lift, AMP * 0.8f)
        moveTo(b0.x, b0.y)
        quad(jp(14.6f, 13.2f + lift, AMP * 0.8f), jp(18f, 15.2f + lift, AMP * 0.8f))
        quad(jp(20.5f, 16.6f + lift, AMP * 0.8f), jp(23f, 14.4f + lift, AMP * 0.8f))
        quad(jp(25.2f, 13.2f + lift, AMP * 0.8f), jp(26.8f, 15.6f + lift, AMP * 0.8f))
    }
    drawPath(brow, Color.Black, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round, join = StrokeJoin.Round))

    // Eyes — group centred at (19.5, 23.5 / 22 excited), jittered (amp*0.6).
    val eyeCy = if (excited) 22f else 23.5f
    val ox = gaze * 1.5f
    val oy = abs(gaze) * -1.4f
    val ry = (5.2f / 2f) * (if (blink) 0.12f else 1f)
    val rx = 5.2f / 2f
    for (dx in listOf(-3.9f, 3.9f)) {
        val c = jp(19.5f + dx + ox, eyeCy + oy, AMP * 0.6f)
        drawOval(
            color = Color.Black,
            topLeft = Offset(c.x - rx * s, c.y - ry * s),
            size = Size(rx * 2f * s, ry * 2f * s)
        )
    }

    // Startle rays on tap.
    if (excited) {
        val sparks = Path().apply {
            var a = jp(10f, 1.5f); moveTo(a.x, a.y); a = jp(8f, -2f); lineTo(a.x, a.y)
            a = jp(17f, 0.5f); moveTo(a.x, a.y); a = jp(17f, -3.5f); lineTo(a.x, a.y)
            a = jp(31f, 1.0f); moveTo(a.x, a.y); a = jp(33.5f, -2.2f); lineTo(a.x, a.y)
        }
        drawPath(sparks, Color.Black, style = ink)
    }
}

private fun Path.cubic(c1: Offset, c2: Offset, end: Offset) =
    cubicTo(c1.x, c1.y, c2.x, c2.y, end.x, end.y)

private fun Path.quad(c: Offset, end: Offset) =
    quadraticTo(c.x, c.y, end.x, end.y)
