package `in`.paperboxd.app.ui.screens.fusion

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.domain.model.FusionBook
import `in`.paperboxd.app.ui.screens.wrapped.PbCoverArt
import `in`.paperboxd.app.ui.screens.wrapped.PbRise
import `in`.paperboxd.app.ui.screens.wrapped.PbRule
import `in`.paperboxd.app.ui.screens.wrapped.PbStamp
import `in`.paperboxd.app.ui.theme.PBScript

// Fusion, Swiss cut: one grotesk, one accent, hairlines and a real grid. Twin of
// iOS FusionKit.swift and the design prototype's fusion2/kit.jsx. Motion reuses
// the Wrapped primitives so the share card's still mode works the same way.

object FZ {
    val Bg = Color(0xFFF2EFE9)
    val Ink = Color(0xFF16161A)
    val Accent = Color(0xFFE33D21)
    val Grey = Color(0xFF8C8C88)
    val Line = Color(0xFF16161A).copy(alpha = 0.16f)
    val LineOnDark = Color(0xFFF2EFE9).copy(alpha = 0.2f)
    val MuteOnDark = Color(0xFFF2EFE9).copy(alpha = 0.56f)

    /** Story pages are laid out on this width and scaled to the device. */
    val DesignWidth = 402.dp

    /** The design's Helvetica; the platform grotesk is the honest stand-in. */
    val Sans = FontFamily.SansSerif
    val Script = PBScript

    fun tracking(size: Float): TextUnit = (if (size > 90) -0.055f else if (size > 44) -0.04f else -0.03f).em
}

@Composable
fun FzHeading(text: String, size: Float = 34f, color: Color = FZ.Ink, modifier: Modifier = Modifier, maxLines: Int = Int.MAX_VALUE) {
    Text(
        text, modifier = modifier, color = color, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold,
        fontSize = size.sp, lineHeight = (size * 1.02f).sp, letterSpacing = FZ.tracking(size),
        maxLines = maxLines, overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun FzLabel(text: String, size: Float = 10f, color: Color = FZ.Grey, modifier: Modifier = Modifier) {
    Text(
        text.uppercase(), modifier = modifier, color = color, fontFamily = FZ.Sans,
        fontWeight = FontWeight.SemiBold, fontSize = size.sp, letterSpacing = 0.17.em, maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun FzBody(text: String, size: Float = 14.5f, color: Color = FZ.Ink, alpha: Float = 0.78f, modifier: Modifier = Modifier) {
    Text(
        text, modifier = modifier, color = color.copy(alpha = alpha), fontFamily = FZ.Sans,
        fontSize = size.sp, lineHeight = (size * 1.5f).sp
    )
}

@Composable
fun FzNum(text: String, size: Float = 13f, color: Color = FZ.Ink, bold: Boolean = true, modifier: Modifier = Modifier) {
    Text(
        text, modifier = modifier, color = color, fontFamily = FZ.Sans,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Medium, fontSize = size.sp,
        style = TextStyle(fontFeatureSettings = "tnum"), maxLines = 1
    )
}

/** Hairline that draws itself from the leading edge. */
@Composable
fun FzRule(delayMillis: Int = 0, dark: Boolean = false, color: Color? = null, thickness: Dp = 1.dp, modifier: Modifier = Modifier) {
    PbRule(delayMillis = delayMillis, durationMillis = 620, color = color ?: if (dark) FZ.LineOnDark else FZ.Line, thickness = thickness, modifier = modifier)
}

/** Clickable without the ripple: the design has no ink splash. */
fun Modifier.fzClick(onClick: () -> Unit): Modifier =
    this.clickable(interactionSource = null, indication = null, onClick = onClick)

/** The square button every screen ends on. */
@Composable
fun FzPill(
    title: String,
    solid: Boolean = false,
    light: Boolean = false,
    compact: Boolean = false,
    fullWidth: Boolean = true,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fg = if (solid) (if (light) FZ.Ink else FZ.Bg) else (if (light) FZ.Bg else FZ.Ink)
    val fill = if (solid) (if (light) FZ.Bg else FZ.Ink) else Color.Transparent
    val stroke = if (light) FZ.Bg.copy(alpha = 0.5f) else FZ.Ink
    Box(
        modifier
            .let { if (fullWidth) it.fillMaxWidth() else it }
            .background(fill)
            .border(1.dp, stroke)
            .let { if (enabled) it.fzClick(onClick) else it }
            .graphicsLayer { alpha = if (enabled) 1f else 0.5f }
            .padding(horizontal = if (compact) 12.dp else 18.dp, vertical = if (compact) 9.dp else 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            title.uppercase(), color = fg, fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold,
            fontSize = if (compact) 9.5.sp else 11.sp, letterSpacing = 0.1.em, maxLines = 1
        )
    }
}

/** A square initial. Accent is always the viewer's half. */
@Composable
fun FzMonogram(initial: String, size: Dp = 40.dp, accent: Boolean = false, onDark: Boolean = false) {
    Box(
        Modifier.size(size).background(if (accent) FZ.Accent else if (onDark) FZ.Bg else FZ.Ink),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initial, color = if (onDark && !accent) FZ.Ink else FZ.Bg, fontFamily = FZ.Sans,
            fontWeight = FontWeight.Bold, fontSize = (size.value * 0.4f).sp
        )
    }
}

/** The two-disc mark: the only place the Fusion symbol appears. */
@Composable
fun FzDiscs(delayMillis: Int = 0) {
    PbStamp(delayMillis = delayMillis, durationMillis = 420) {
        Canvas(Modifier.size(108.dp, 62.dp).graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }) {
            val r = size.height / 2
            drawCircle(FZ.Accent, r, Offset(r, r))
            drawCircle(FZ.Ink, r, Offset(size.width - r, r), blendMode = BlendMode.Multiply)
        }
    }
}

/** Two names printed on top of each other, accent under ink. */
@Composable
fun FzOverprint(a: String, b: String, size: Float = 44f, delayMillis: Int = 0) {
    Column {
        PbRise(delayMillis = delayMillis) { FzHeading(a, size, FZ.Accent) }
        PbRise(delayMillis = delayMillis + 110, modifier = Modifier.offset(y = (-size * 0.34f).dp).padding(start = (size * 0.5f).dp)) {
            FzHeading(b, size, FZ.Ink)
        }
    }
}

enum class CoverOwner { You, Them, Both }

/**
 * Covers are one uniform object: bone board, ink type, a rule in whoever's
 * colour owns it. No artwork, on purpose.
 */
@Composable
fun FzCover(book: FusionBook, width: Dp = 96.dp, owner: CoverOwner = CoverOwner.Both, dark: Boolean = false, delayMillis: Int = 0, rotation: Float = 0f) {
    val w = width.value
    PbStamp(delayMillis = delayMillis, durationMillis = 420, rotation = rotation) {
        Box(
            Modifier
                .size(width, width * 1.5f)
                .background(if (dark) Color(0xFF222227) else Color(0xFFE7E3DB))
                .let { if (dark) it.border(1.dp, FZ.Bg.copy(alpha = 0.16f)) else it }
                .clipToBounds()
        ) {
            PbCoverArt(book.coverUrl) { FzCoverBoard(book, w, owner, dark) }
        }
    }
}

/** The drawn board: bone board, ink type, a rule in whoever's colour owns it. */
@Composable
private fun FzCoverBoard(book: FusionBook, w: Float, owner: CoverOwner, dark: Boolean) {
        Box(Modifier.fillMaxSize()) {
            Box(Modifier.width((w * 0.055f).dp).fillMaxHeight().background(if (owner == CoverOwner.Them) FZ.Ink else FZ.Accent))
            if (owner == CoverOwner.Both) {
                Box(
                    Modifier.align(Alignment.TopEnd).width((w * 0.055f).dp).fillMaxHeight()
                        .background(if (dark) FZ.Bg else FZ.Ink)
                )
            }
            Column(
                Modifier.align(Alignment.BottomStart).padding((w * 0.09f).dp).padding(start = (w * 0.04f).dp),
                verticalArrangement = Arrangement.spacedBy((w * 0.05f).dp)
            ) {
                Text(
                    book.title, color = if (dark) FZ.Bg else FZ.Ink, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold,
                    fontSize = (w * 0.125f).sp, lineHeight = (w * 0.135f).sp, maxLines = 4, overflow = TextOverflow.Ellipsis
                )
                if (book.author.isNotEmpty()) {
                    Text(
                        book.author.uppercase(), color = if (dark) FZ.Bg.copy(alpha = 0.6f) else FZ.Grey,
                        fontFamily = FZ.Sans, fontWeight = FontWeight.Medium, fontSize = (w * 0.07f).sp,
                        letterSpacing = 0.1.em, maxLines = 2
                    )
                }
            }
        }
}

/** A rating is a number, set tabular. Not five shapes. */
@Composable
fun FzRate(who: String, n: Int, color: Color = FZ.Ink, size: Float = 13f) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        Text(
            who.uppercase(), color = FZ.Grey, fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold,
            fontSize = (size * 0.66f).sp, letterSpacing = 0.12.em, modifier = Modifier.padding(bottom = 1.dp)
        )
        FzNum("$n", size, color)
    }
}

/** Every page is the same grid: 30dp margins, a running head, a baseline. */
@Composable
fun FzScreen(dark: Boolean = false, top: Dp = 94.dp, bottom: Dp = 112.dp, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().background(if (dark) FZ.Ink else FZ.Bg)
            .padding(top = top, bottom = bottom).padding(horizontal = 30.dp),
        content = content
    )
}

/** Heading on the design's line breaks, the last line optionally in the accent. */
@Composable
fun FzStack(lines: List<String>, size: Float = 34f, color: Color = FZ.Ink, accentLast: Boolean = false, delayMillis: Int = 0, stepMillis: Int = 80) {
    Column {
        lines.forEachIndexed { i, line ->
            PbRise(delayMillis = delayMillis + i * stepMillis) {
                FzHeading(line, size, if (accentLast && i == lines.lastIndex) FZ.Accent else color)
            }
        }
    }
}

@Composable
fun FzToast(message: String, modifier: Modifier = Modifier) {
    Box(modifier.background(FZ.Ink).padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(message.uppercase(), color = FZ.Bg, fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, letterSpacing = 0.1.em)
    }
}

fun fzIndex(i: Int): String = "%02d".format(i)
