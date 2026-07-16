package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
