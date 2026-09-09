package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path

/**
 * The four profile-dock icons as measurable [Path]s.
 *
 * Hand-drawn rather than Material icons on purpose: `TabDock` animates a tab by
 * trimming its icon's stroke away with `PathMeasure.getSegment`, and a vector
 * drawable gives you nothing to measure. Coordinates are authored in a 20×20 box
 * and scaled to the requested [size] in pixels.
 *
 * Twin of iOS `ProfileTabMark`.
 */
enum class ProfileTabMarkKind { Bookshelf, Thoughts, Lists, Tbr }

fun profileTabPath(kind: ProfileTabMarkKind, size: Float): Path {
    val s = size / 20f
    fun x(v: Float) = v * s
    fun y(v: Float) = v * s
    val p = Path()

    when (kind) {
        ProfileTabMarkKind.Bookshelf -> {
            // Three spines, the rightmost leaning.
            p.addRect(Rect(x(3.6f), y(4.6f), x(6.5f), y(15.6f)))
            p.addRect(Rect(x(8.1f), y(4.6f), x(11f), y(15.6f)))
            p.moveTo(x(13.1f), y(5.7f))
            p.lineTo(x(15.9f), y(6.5f))
            p.lineTo(x(13.4f), y(16f))
            p.lineTo(x(10.6f), y(15.2f))
            p.close()
        }

        ProfileTabMarkKind.Thoughts -> {
            // Rounded speech bubble, tail, two text rules.
            p.addRoundRect(
                RoundRect(
                    Rect(x(3f), y(4.2f), x(17f), y(13.8f)),
                    CornerRadius(x(2.7f), y(2.7f))
                )
            )
            p.moveTo(x(8.8f), y(13.8f))
            p.lineTo(x(4.6f), y(16.3f))
            p.lineTo(x(4.6f), y(13.7f))
            p.moveTo(x(6.6f), y(7.5f))
            p.lineTo(x(13.4f), y(7.5f))
            p.moveTo(x(6.6f), y(10.1f))
            p.lineTo(x(11f), y(10.1f))
        }

        ProfileTabMarkKind.Lists -> {
            p.moveTo(x(7.4f), y(5.6f)); p.lineTo(x(17f), y(5.6f))
            p.moveTo(x(7.4f), y(10f)); p.lineTo(x(17f), y(10f))
            p.moveTo(x(7.4f), y(14.4f)); p.lineTo(x(17f), y(14.4f))
        }

        ProfileTabMarkKind.Tbr -> {
            // Bookmark: square shoulders, notched foot.
            p.moveTo(x(4.4f), y(4.7f))
            p.lineTo(x(4.4f), y(16.3f))
            p.lineTo(x(10f), y(12.9f))
            p.lineTo(x(15.6f), y(16.3f))
            p.lineTo(x(15.6f), y(4.7f))
            p.close()
        }
    }
    return p
}

/**
 * Centres of the Lists bullets, in pixels for a mark of [size]. Dots have no
 * length to trim, so the caller fades them — otherwise a row of bullets sits
 * there after the rules have gone.
 */
fun profileTabListDots(size: Float): List<Offset> {
    val s = size / 20f
    return listOf(5.6f, 10f, 14.4f).map { Offset(4f * s, it * s) }
}

fun profileTabListDotRadius(size: Float): Float = 1.05f * (size / 20f)
