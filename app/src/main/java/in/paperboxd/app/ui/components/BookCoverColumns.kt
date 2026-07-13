package `in`.paperboxd.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

/**
 * Animated tiled book-cover wall. Mirrors the iOS `BookCoverColumns`: five
 * columns of gradient "covers" scroll vertically (alternating up/down at
 * different speeds), the whole grid rotated -8° and over-scaled so it bleeds
 * off every edge. Sits behind [DarkWash]. Purely decorative.
 */

private val coverWidth = 70.dp
private val coverGap = 10.dp
private const val COVER_RATIO = 1.5f

// 18-cover gradient library, matching the design's PB_COVERS array.
private val pbCovers: List<Brush> = listOf(
    "6b5b95" to "2f1f50", "c44536" to "6a1f1a", "c49a6c" to "7a5230",
    "2a4a3a" to "10201a", "3a5a7a" to "15253a", "8a3a5a" to "3d1528",
    "4a6a2a" to "1e2e10", "b85c38" to "5c2810", "2a3a5a" to "0e1525",
    "8a7a2a" to "3d3510", "5a2a6a" to "250e30", "1a5a4a" to "081f18",
    "6b4a2a" to "2d1a08", "3a2a6a" to "150e30", "5a4a1a" to "251e06",
    "1a3a5a" to "081525", "6a3a2a" to "2d1510", "2a5a3a" to "0e2518",
).map { (a, b) ->
    Brush.linearGradient(
        colors = listOf(hex(a), hex(b)),
        start = Offset.Zero,
        end = Offset.Infinite,
    )
}

private data class ColSpec(val covers: List<Brush>, val up: Boolean, val durMs: Int, val frac: Float)

// Durations ~halved vs iOS so motion reads clearly on a phone.
private val colSpecs = listOf(
    ColSpec(listOf(pbCovers[0], pbCovers[5], pbCovers[10], pbCovers[3], pbCovers[12]), true, 20_000, 0.00f),
    ColSpec(listOf(pbCovers[1], pbCovers[7], pbCovers[14], pbCovers[9], pbCovers[4]), false, 24_000, 0.24f),
    ColSpec(listOf(pbCovers[2], pbCovers[8], pbCovers[11], pbCovers[16], pbCovers[6]), true, 28_000, 0.48f),
    ColSpec(listOf(pbCovers[15], pbCovers[13], pbCovers[17], pbCovers[4], pbCovers[2]), false, 25_000, 0.72f),
    ColSpec(listOf(pbCovers[6], pbCovers[1], pbCovers[9], pbCovers[12], pbCovers[15]), true, 20_000, 0.16f),
)

@Composable
fun BookCoverColumns(modifier: Modifier = Modifier) {
    Box(modifier.clipToBounds(), contentAlignment = Alignment.Center) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(coverGap),
            modifier = Modifier.graphicsLayer {
                rotationZ = -8f
                scaleX = 1.5f
                scaleY = 1.5f
            },
        ) {
            colSpecs.forEach { spec -> CoverColumn(spec) }
        }
    }
}

@Composable
private fun CoverColumn(spec: ColSpec) {
    val density = LocalDensity.current
    val coverH = coverWidth * COVER_RATIO
    val setHDp = (coverH + coverGap) * spec.covers.size
    val setHpx = with(density) { setHDp.toPx() }
    val init0 = spec.frac * setHpx

    val transition = rememberInfiniteTransition(label = "coverCol")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = setHpx,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = spec.durMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "phase",
    )

    // Upward runs more negative; downward less negative. Tripled content keeps
    // the strip full through the whole travel; the parent clips overflow.
    val dy = if (spec.up) -(init0 + phase) else -(2 * setHpx - init0) + phase

    Column(
        verticalArrangement = Arrangement.spacedBy(coverGap),
        modifier = Modifier.graphicsLayer { translationY = dy },
    ) {
        repeat(spec.covers.size * 3) { i ->
            Box(
                Modifier
                    .size(coverWidth, coverH)
                    .clip(RoundedCornerShape(8.dp))
                    .background(spec.covers[i % spec.covers.size]),
            )
        }
    }
}

private fun hex(h: String): Color {
    val v = h.toLong(16)
    return Color(
        red = ((v shr 16) and 0xFF) / 255f,
        green = ((v shr 8) and 0xFF) / 255f,
        blue = (v and 0xFF) / 255f,
        alpha = 1f,
    )
}
