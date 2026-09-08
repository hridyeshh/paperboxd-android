package `in`.paperboxd.app.ui.components

import android.provider.Settings
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * One board on the shelf. Widths, heights, cloth and foil all vary so the row
 * reads as a shelf of books rather than a row of swatches; [lean] tips the
 * occasional leaner so the tops don't march like a picket fence.
 */
data class ShelfSpine(
    val title: String,
    val cloth: Color,
    val foil: Color,
    val width: Dp,
    val height: Dp,
    val lean: Float = 0f,
)

private fun board(title: String, cloth: Long, foil: Long, w: Int, h: Int) =
    ShelfSpine(title, Color(cloth), Color(foil), w.dp, h.dp)

/** Two leaners break the rhythm; the rest stand square. */
private fun deck(vararg rows: ShelfSpine): List<ShelfSpine> = rows.mapIndexed { i, s ->
    when (i) {
        5 -> s.copy(lean = -3.5f)
        11 -> s.copy(lean = 2.5f)
        else -> s
    }
}

/**
 * Sign-in deck. Cloth / foil pairs at real saturation — oxblood, ochre, forest,
 * indigo, plum, teal — plus bone and cream boards for rhythm.
 */
val signInShelf: List<ShelfSpine> = deck(
    board("Middlemarch", 0xFF8C2B22, 0xFFE7C77A, 46, 190),
    board("The Waves", 0xFF1B4B3C, 0xFFD8B45C, 30, 176),
    board("Dubliners", 0xFFC9962B, 0xFF3A2C1C, 38, 184),
    board("Beloved", 0xFF23304F, 0xFFD8B45C, 27, 168),
    board("Wide Sargasso Sea", 0xFF6A2350, 0xFFEDD9A8, 42, 192),
    board("The Idiot", 0xFFEDE0C4, 0xFF8C2B22, 34, 172),
    board("Cannery Row", 0xFF14595E, 0xFFE7C77A, 25, 182),
    board("A Room of One's Own", 0xFFC0271C, 0xFFFFF0CC, 48, 178),
    board("Ficciones", 0xFF33306B, 0xFFD8B45C, 31, 194),
    board("The Leopard", 0xFFB5642A, 0xFF2A1C0E, 36, 166),
    board("Pale Fire", 0xFF1E6B52, 0xFFEDD9A8, 28, 186),
    board("Stoner", 0xFFE3D5B8, 0xFF23304F, 44, 174),
    board("The Sea, The Sea", 0xFF2A3A5A, 0xFFC9962B, 26, 190),
    board("Silas Marner", 0xFF7A2E1B, 0xFFE7C77A, 40, 170),
    board("Orlando", 0xFFB5456B, 0xFFFFF0CC, 32, 188),
    board("The Rings of Saturn", 0xFF23211E, 0xFFD8B45C, 29, 180),
)

/** Register deck: same library, reshuffled and shorter for the lower band. */
val registerShelf: List<ShelfSpine> = deck(
    board("Wide Sargasso Sea", 0xFF6A2350, 0xFFEDD9A8, 42, 152),
    board("The Waves", 0xFF1B4B3C, 0xFFD8B45C, 30, 140),
    board("Dubliners", 0xFFC9962B, 0xFF3A2C1C, 38, 148),
    board("Beloved", 0xFF23304F, 0xFFD8B45C, 27, 134),
    board("Middlemarch", 0xFF8C2B22, 0xFFE7C77A, 46, 154),
    board("The Idiot", 0xFFEDE0C4, 0xFF8C2B22, 34, 138),
    board("Cannery Row", 0xFF14595E, 0xFFE7C77A, 25, 146),
    board("Orlando", 0xFFB5456B, 0xFFFFF0CC, 32, 150),
    board("Ficciones", 0xFF33306B, 0xFFD8B45C, 31, 156),
    board("The Leopard", 0xFFB5642A, 0xFF2A1C0E, 36, 132),
    board("Pale Fire", 0xFF1E6B52, 0xFFEDD9A8, 28, 149),
    board("Stoner", 0xFFE3D5B8, 0xFF23304F, 44, 140),
    board("The Sea, The Sea", 0xFF2A3A5A, 0xFFC9962B, 26, 152),
    board("Silas Marner", 0xFF7A2E1B, 0xFFE7C77A, 40, 136),
    board("A Room of One's Own", 0xFFC0271C, 0xFFFFF0CC, 48, 144),
    board("The Rings of Saturn", 0xFF23211E, 0xFFD8B45C, 29, 147),
)

/**
 * A title running down a spine. Compose has no vertical writing mode, so the
 * line is laid out flat, capped to [run] (which truncates a long title), then
 * turned a quarter clockwise. [run] becomes the drawn height, so the caller
 * reserves it. Twin of iOS `SpineTitle`.
 */
@Composable
fun SpineTitle(
    text: String,
    fontSize: TextUnit,
    color: Color,
    run: Dp,
    fontWeight: FontWeight = FontWeight.SemiBold,
    letterSpacing: TextUnit = 0.4.sp,
) {
    // Compose has no shrink-to-fit, so a long title steps down one size — the
    // twin of iOS's `minimumScaleFactor` on the same label.
    val size = if (text.length > 15) fontSize * 0.78f else fontSize
    Box(
        modifier = Modifier.width(fontSize.value.dp * 1.5f).height(run),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            fontFamily = FontFamily.Serif,
            fontWeight = fontWeight,
            fontSize = size,
            letterSpacing = letterSpacing,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Clip,
            softWrap = false,
            // requiredWidth so the parent's narrower constraint can't squeeze it.
            modifier = Modifier.requiredWidth(run).rotate(90f),
        )
    }
}

private val ShelfGap = 3.dp
private val PlankHeight = 5.dp
private val FloorHeight = 30.dp
private val CastHeight = 16.dp

/**
 * The auth hero: a shelf of cloth-bound spines drifting slowly left, standing on
 * a plank that casts a shadow onto the floor in front of it. Twin of iOS
 * `BookShelfHero`; replaces the old gradient cover wall.
 */
@Composable
fun BookShelfHero(
    modifier: Modifier = Modifier,
    spines: List<ShelfSpine> = signInShelf,
    height: Dp = 244.dp,
    rowHeight: Dp = 200.dp,
    leadingPad: Dp = 14.dp,
    durationMillis: Int = 90_000,
    titleSize: TextUnit = 10.5.sp,
    maxTitleRun: Dp = 118.dp,
    spinePadding: Dp = 9.dp,
) {
    // Distance from the start of one copy of the deck to the start of the next:
    // n boards plus n gaps. Drifting exactly this far hides the loop point.
    val setWidth = remember(spines) {
        spines.fold(0.dp) { acc, s -> acc + s.width + ShelfGap }
    }

    // Honour "remove animations" — the shelf is decorative, so it just parks.
    val context = LocalContext.current
    val animated = remember {
        Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) != 0f
    }
    val drift by rememberInfiniteTransition(label = "shelf").animateFloat(
        initialValue = 0f,
        targetValue = if (animated) 1f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shelf-drift",
    )

    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clipToBounds()
            .clearAndSetSemantics {}
            .background(
                Brush.verticalGradient(
                    0f to Color(0xFFEFE7D6),
                    0.62f to Color(0xFFE8DCC4),
                    1f to Color(0xFFDCCFB2),
                )
            ),
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(ShelfGap),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = FloorHeight + PlankHeight - 1.dp)
                .height(rowHeight)
                .wrapContentWidth(align = Alignment.Start, unbounded = true)
                .offset(x = leadingPad - setWidth * drift),
        ) {
            // Three copies: one walks off the left edge each pass, so the two
            // behind it have to cover the band on their own.
            repeat(spines.size * 3) { i ->
                Spine(spines[i % spines.size], titleSize, maxTitleRun, spinePadding)
            }
        }

        // Floor, then the shadow the boards throw across it, then the plank they
        // stand on. Painted in that order so the shadow reads.
        Box(
            Modifier.align(Alignment.BottomStart).fillMaxWidth()
                .height(FloorHeight).background(Color(0xFFDCCFB2))
        )
        Box(
            Modifier.align(Alignment.BottomStart).fillMaxWidth()
                .padding(bottom = FloorHeight - CastHeight)
                .height(CastHeight)
                .background(
                    Brush.verticalGradient(
                        listOf(HL.Plank.copy(alpha = 0.30f), HL.Plank.copy(alpha = 0f))
                    )
                )
        )
        Box(
            Modifier.align(Alignment.BottomStart).fillMaxWidth()
                .padding(bottom = FloorHeight).height(PlankHeight).background(HL.Plank)
        )
        // The floor dissolves into the paper the rest of the screen sits on.
        Box(
            Modifier.align(Alignment.BottomStart).fillMaxWidth().height(FloorHeight)
                .background(
                    Brush.verticalGradient(listOf(Color(0x00DCCFB2), HL.Paper))
                )
        )
    }
}

@Composable
private fun Spine(spine: ShelfSpine, titleSize: TextUnit, maxTitleRun: Dp, spinePadding: Dp) {
    val run = minOf(maxTitleRun, spine.height - spinePadding * 2 - 16.dp)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .graphicsLayer {
                rotationZ = spine.lean
                transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
            }
            .width(spine.width)
            .height(spine.height)
            .background(spine.cloth)
            // Board curvature: lit at the hinge, falling into shadow at the fore-edge.
            .background(
                Brush.horizontalGradient(
                    0f to Color.White.copy(alpha = 0.10f),
                    0.12f to Color.Transparent,
                    0.82f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.28f),
                )
            )
            // The bottom edge of this border is hidden by the plank over it.
            .border(1.5.dp, Color(0xFF15100A).copy(alpha = 0.55f), RectangleShape)
            .padding(vertical = spinePadding),
    ) {
        FoilRule(spine)
        SpineTitle(text = spine.title, fontSize = titleSize, color = spine.foil, run = run)
        FoilRule(spine)
    }
}

@Composable
private fun FoilRule(spine: ShelfSpine) {
    Box(Modifier.size(width = spine.width * 0.76f, height = 2.dp).background(spine.foil))
}
