package `in`.paperboxd.app.ui.screens.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** The three "while-you-wait" games, with their brutalist chrome — iOS twin. */
enum class ScanGameKind {
    Breakout, CatchDrops, Stack;

    val title: String
        get() = when (this) {
            Breakout -> "Break some spines"
            CatchDrops -> "Catch the drops"
            Stack -> "Stack the shelf"
        }

    val sub: String
        get() = when (this) {
            Breakout -> "drag to move the paddle · tap to serve"
            CatchDrops -> "drag the shelf · don't let them fall"
            Stack -> "tap to drop · line them up to build higher"
        }

    val hudLabel: String
        get() = when (this) {
            Breakout -> "spines"
            CatchDrops -> "caught"
            Stack -> "height"
        }

    /** true → HUD hint shows hearts (lives); false → shows "best N". */
    val usesLives: Boolean get() = this != Stack
}

/**
 * Fullscreen brutalist game overlay — iOS `ScanGameHost` twin. A random game is
 * picked when it opens; ↻ NEW swaps to a different one; ✕ returns to the
 * analyzing screen. The game never stops on its own — the analysis result lands
 * as a popup over the top.
 */
@Composable
fun ScanGameHost(onClose: () -> Unit) {
    var kind by remember { mutableStateOf(ScanGameKind.entries.random()) }
    var gameId by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var lives by remember { mutableIntStateOf(3) }
    var best by remember { mutableIntStateOf(0) }

    val onHud: GameHud = { s, l, b ->
        score = s
        if (l >= 0) lives = l
        best = b
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SK.bgSoft)
            .statusBarsPadding()
            .padding(top = 8.dp)
    ) {
        // Header
        Box(
            Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        SK.line,
                        topLeft = Offset(0f, size.height - 2.dp.toPx()),
                        size = Size(size.width, 2.dp.toPx())
                    )
                }
                .padding(horizontal = 16.dp)
                .padding(bottom = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BlinkDot(color = SK.accent, size = 7.dp)
                        MonoLabel(
                            text = "Analyzing · Background",
                            size = 9f, tracking = 1.6f, color = SK.accent
                        )
                    }
                    Text(
                        kind.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp,
                        color = SK.ink,
                        maxLines = 1
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(40.dp)
                        .border(2.dp, SK.line)
                        .clickable {
                            val next = ScanGameKind.entries.filter { it != kind }.random()
                            kind = next
                            gameId += 1
                            score = 0; lives = 3; best = 0
                        }
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        "↻ NEW",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        letterSpacing = 1.sp,
                        color = SK.ink
                    )
                }
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(40.dp)
                        .background(SK.ink)
                        .clickable(onClick = onClose)
                ) {
                    Icon(
                        Icons.Outlined.Close, contentDescription = "Close game",
                        tint = SK.panel, modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Game shell (HUD bar + framed canvas with hard offset shadow)
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 14.dp)
                .weight(1f)
                .padding(end = 5.dp, bottom = 5.dp)
                .drawBehind {
                    val off = 5.dp.toPx()
                    drawRect(
                        SK.ink.copy(alpha = 0.55f),
                        topLeft = Offset(off, off),
                        size = size
                    )
                }
                .border(2.dp, SK.line)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SK.ink)
                    .padding(horizontal = 11.dp, vertical = 7.dp)
            ) {
                Text(
                    "${kind.hudLabel.uppercase()}  ·  $score",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    color = SK.panel
                )
                Spacer(Modifier.weight(1f))
                val hint = if (kind.usesLives) {
                    val l = lives.coerceIn(0, 3)
                    "♥".repeat(l) + "·".repeat(3 - l)
                } else {
                    "BEST $best"
                }
                Text(
                    hint,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5.sp,
                    letterSpacing = 0.8.sp,
                    color = SK.panel.copy(alpha = 0.7f)
                )
            }

            key(kind, gameId) {
                val gameModifier = Modifier.fillMaxWidth().weight(1f).background(SK.gameBg)
                when (kind) {
                    ScanGameKind.Breakout -> BreakoutGame(onHud, gameModifier)
                    ScanGameKind.CatchDrops -> CatchGame(onHud, gameModifier)
                    ScanGameKind.Stack -> StackGame(onHud, gameModifier)
                }
            }
        }

        // Footer
        Text(
            "${kind.sub.uppercase()} · NEVER STOPS ON ITS OWN",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.5.sp,
            letterSpacing = 1.sp,
            color = SK.faint,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 24.dp)
        )
    }
}
