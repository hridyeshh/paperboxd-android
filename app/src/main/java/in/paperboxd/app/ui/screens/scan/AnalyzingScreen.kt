package `in`.paperboxd.app.ui.screens.scan

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.HourglassEmpty
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 02 — Analyzing · BRUTALIST — iOS `AnalyzingScreen` twin. The whole screen is a
 * machine at work: a framed 3D knowledge-graph, count-up source cells, a live
 * terminal log. Tap into a fullscreen game while the backend scores the scan.
 * When the result lands a "RESULTS READY" popup slides up; the game never stops
 * on its own.
 *
 * `book == null` → still scoring. `book != null` → done (reveal available).
 */
@Composable
fun AnalyzingScreen(
    book: ScanResult?,
    pendingTitle: String?,
    error: String?,
    exhausted: Boolean,
    onReveal: () -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    onClose: () -> Unit
) {
    var gameOpen by remember { mutableStateOf(false) }
    var popupDismissed by remember { mutableStateOf(false) }
    var liveIdx by remember { mutableIntStateOf(0) }

    val done = book != null
    val showPopup = done && !popupDismissed

    val reading = remember {
        listOf(
            "pulling r/books + r/suggestmeabook",
            "weighting reviews by reader similarity",
            "cross-referencing your rated books",
            "checking friends who shelved this",
            "scoring genre / pacing / depth for you",
        )
    }

    LaunchedEffect(done) {
        while (!done) {
            delay(1500)
            liveIdx = (liveIdx + 1) % reading.size
        }
    }

    Box(Modifier.fillMaxSize().background(SK.bg)) {
        when {
            error != null && exhausted -> ExhaustedView(error, onBack, onClose)
            error != null -> ErrorView(error, onRetry, onClose)
            else -> {
                Column(Modifier.fillMaxSize().statusBarsPadding()) {
                    StatusBar(done, onClose)
                    Column(
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {
                        BookRow(
                            book, pendingTitle,
                            Modifier.padding(horizontal = 16.dp).padding(top = 14.dp)
                        )
                        OrbFrame(done, Modifier.padding(horizontal = 16.dp).padding(top = 16.dp))
                        LiveLine(
                            done, reading[liveIdx],
                            Modifier.padding(horizontal = 16.dp).padding(top = 12.dp)
                        )
                        SourceCells(book, Modifier.padding(horizontal = 16.dp).padding(top = 4.dp))
                        TerminalLog(
                            done, liveIdx, reading,
                            Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
                        )
                    }
                    BottomAction(done, onReveal, onPlay = { gameOpen = true })
                }

                AnimatedVisibility(
                    visible = gameOpen,
                    enter = slideInVertically(initialOffsetY = { it }),
                    exit = slideOutVertically(targetOffsetY = { it })
                ) {
                    ScanGameHost(onClose = { gameOpen = false })
                }

                AnimatedVisibility(
                    visible = showPopup,
                    enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 }),
                    exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 2 })
                ) {
                    ResultPopup(
                        title = book?.title ?: "Your book",
                        onReveal = onReveal,
                        onKeepPlaying = { popupDismissed = true }
                    )
                }
            }
        }
    }
}

// ── Status bar ───────────────────────────────────────────────────────────────

@Composable
private fun StatusBar(done: Boolean, onClose: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(
                    SK.line,
                    topLeft = Offset(0f, size.height - 2.dp.toPx()),
                    size = Size(size.width, 2.dp.toPx())
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BlinkDot(color = SK.accent, size = 8.dp)
            MonoLabel(
                text = if (done) "Analysis · Complete" else "Analyzing",
                size = 11f, tracking = 1.6f, color = SK.ink, weight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.weight(1f))
        MonoLabel(
            text = if (done) "08.4s" else "For you",
            size = 9.5f, tracking = 1.2f, color = SK.sub
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(28.dp).clickable(onClick = onClose)
        ) {
            Icon(
                Icons.Outlined.Close, contentDescription = "Close",
                tint = SK.sub, modifier = Modifier.size(15.dp)
            )
        }
    }
}

// ── Book row ─────────────────────────────────────────────────────────────────

@Composable
private fun BookRow(book: ScanResult?, pendingTitle: String?, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        if (book != null) {
            ScanCover(book, width = 40.dp)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    book.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = SK.ink, maxLines = 2
                )
                MonoLabel(text = book.author, size = 10f, tracking = 1.2f, color = SK.sub)
            }
        } else {
            Box(
                Modifier
                    .size(40.dp, 60.dp)
                    .background(SK.faint.copy(alpha = 0.4f))
                    .border(2.dp, SK.line)
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    pendingTitle ?: "Scoring your scan",
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 18.sp, color = SK.ink, maxLines = 2
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CircularProgressIndicator(
                        color = SK.sub, strokeWidth = 1.5.dp, modifier = Modifier.size(11.dp)
                    )
                    MonoLabel(text = "scoring for you", size = 10f, tracking = 1.2f, color = SK.sub)
                }
            }
        }
    }
}

// ── Orb frame ────────────────────────────────────────────────────────────────

@Composable
private fun OrbFrame(done: Boolean, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .background(SK.panel)
            .border(2.dp, SK.line)
            .cropCorners(color = SK.accent, length = 16.dp, weight = 3.dp)
            .padding(14.dp)
    ) {
        ScanOrbView(done = done, size = 192.dp)
        CornerLabel("NODES·132", Modifier.align(Alignment.TopStart).padding(7.dp))
        CornerLabel("GRAPH·3D", Modifier.align(Alignment.TopEnd).padding(7.dp))
        CornerLabel(
            if (done) "DONE" else "LIVE",
            Modifier.align(Alignment.BottomEnd).padding(7.dp),
            color = if (done) SK.accent else SK.faint
        )
    }
}

@Composable
private fun CornerLabel(text: String, modifier: Modifier = Modifier, color: Color = SK.faint) {
    Text(
        text,
        fontFamily = FontFamily.Monospace,
        fontSize = 8.5.sp,
        letterSpacing = 1.sp,
        color = color,
        modifier = modifier
    )
}

// ── Live line ────────────────────────────────────────────────────────────────

@Composable
private fun LiveLine(done: Boolean, current: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().height(26.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("> ", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SK.accent)
        Text(
            if (done) "cross-referenced — building your match" else current,
            fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SK.ink
        )
        Text("_", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = SK.accent)
    }
}

// ── Source cells ─────────────────────────────────────────────────────────────

@Composable
private fun SourceCells(book: ScanResult?, modifier: Modifier = Modifier) {
    val sources: List<Pair<Int?, String>> = if (book == null) {
        listOf(null to "READERS", null to "RATINGS", null to "YOUR SHELF")
    } else {
        listOf<Pair<Int?, String>>(
            book.readersCount to "READERS",
            book.communityRatings to "RATINGS",
            book.shelfCount to "YOUR SHELF"
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = modifier.fillMaxWidth()) {
        sources.forEach { (value, label) ->
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 3.dp, bottom = 3.dp)
                    .drawBehind {
                        val off = 3.dp.toPx()
                        drawRect(SK.ink.copy(alpha = 0.5f), topLeft = Offset(off, off), size = size)
                    }
                    .background(SK.panel)
                    .border(1.875.dp, SK.line)
                    .padding(vertical = 12.dp, horizontal = 8.dp)
            ) {
                if (value != null) {
                    CountUpText(target = value, fontSize = 21f, color = SK.ink)
                } else {
                    // Still scoring: roll digits so the number feels like it's being
                    // tallied, then CountUpText settles on the real value.
                    RollingNumber(fontSize = 21f, color = SK.faint)
                }
                MonoLabel(text = label, size = 8.5f, tracking = 1.4f, color = SK.sub)
            }
        }
    }
}

// ── Terminal log ─────────────────────────────────────────────────────────────

@Composable
private fun TerminalLog(
    done: Boolean,
    liveIdx: Int,
    reading: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(7.dp),
        modifier = modifier.padding(bottom = 8.dp)
    ) {
        reading.forEachIndexed { i, line ->
            val isLive = !done && i == liveIdx
            val passed = done || i < liveIdx
            Row(
                horizontalArrangement = Arrangement.spacedBy(9.dp),
                modifier = Modifier.alpha(if (passed || isLive) 1f else 0.35f)
            ) {
                Text(
                    if (passed) "✓" else if (isLive) "▸" else "·",
                    fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                    color = if (passed) SK.accent else SK.sub,
                    modifier = Modifier.width(14.dp)
                )
                Text(
                    line,
                    fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                    fontWeight = if (isLive) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isLive) SK.ink else SK.sub
                )
            }
        }
    }
}

// ── Bottom action ────────────────────────────────────────────────────────────

@Composable
private fun BottomAction(done: Boolean, onReveal: () -> Unit, onPlay: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .background(SK.bg)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(top = 14.dp, bottom = 26.dp)
    ) {
        if (done) {
            BruButton(
                title = "See your match score",
                trailing = Icons.AutoMirrored.Outlined.ArrowForward,
                onClick = onReveal
            )
        } else {
            BruButton(title = "Play a game while you wait", glyph = "▶", onClick = onPlay)
        }
        MonoLabel(
            text = if (done) "Ready · tap to reveal" else "Est ~15s · no need to wait",
            size = 9.5f, tracking = 1.2f, color = SK.faint
        )
    }
}

// ── Result popup ─────────────────────────────────────────────────────────────

@Composable
private fun ResultPopup(title: String, onReveal: () -> Unit, onKeepPlaying: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable(onClick = onKeepPlaying)
        )
        Column(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 14.dp)
                .padding(bottom = 22.dp)
                .padding(end = 7.dp)
                .drawBehind {
                    val off = 7.dp.toPx()
                    drawRect(SK.accent, topLeft = Offset(off, off), size = size)
                }
                .border(2.dp, SK.line)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SK.ink)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                BlinkDot(color = SK.accent, size = 7.dp)
                MonoLabel(
                    text = "Results Ready", size = 10f, tracking = 1.8f,
                    color = SK.panel, weight = FontWeight.SemiBold
                )
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth().background(SK.bg).padding(18.dp)
            ) {
                Text(
                    "Your match score is in.",
                    fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold,
                    fontSize = 25.sp, color = SK.ink
                )
                Text(
                    "$title read against your shelf. Your game keeps going — pick it up whenever.",
                    fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                    color = SK.sub, lineHeight = 16.sp
                )
                BruButton(
                    title = "See your score",
                    trailing = Icons.AutoMirrored.Outlined.ArrowForward,
                    onClick = onReveal,
                    modifier = Modifier.padding(top = 10.dp)
                )
                MonoLabel(
                    text = "Keep playing", size = 10.5f, tracking = 1.6f, color = SK.faint,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onKeepPlaying)
                        .padding(vertical = 12.dp)
                )
            }
        }
    }
}

// ── Exhausted (free-scan quota used up) ──────────────────────────────────────

@Composable
private fun ExhaustedView(message: String, onBack: () -> Unit, onClose: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp)
    ) {
        Icon(
            Icons.Outlined.HourglassEmpty, contentDescription = null,
            tint = SK.ink, modifier = Modifier.size(28.dp)
        )
        Text(
            message, fontFamily = FontFamily.Serif, fontSize = 18.sp,
            color = SK.ink, textAlign = TextAlign.Center
        )
        Text(
            "You've used your free scans. More scans coming soon.",
            fontSize = 13.sp, color = SK.sub, textAlign = TextAlign.Center
        )
        BruButton(title = "Back", onClick = onBack, modifier = Modifier.padding(top = 6.dp))
        MonoLabel(
            text = "Close", size = 11f, tracking = 1.4f, color = SK.sub,
            modifier = Modifier.clickable(onClick = onClose).padding(8.dp)
        )
    }
}

// ── Error ────────────────────────────────────────────────────────────────────

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit, onClose: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp)
    ) {
        Icon(
            Icons.Outlined.WarningAmber, contentDescription = null,
            tint = SK.ink, modifier = Modifier.size(28.dp)
        )
        Text(
            message, fontFamily = FontFamily.Serif, fontSize = 18.sp,
            color = SK.ink, textAlign = TextAlign.Center
        )
        BruButton(title = "Try again", onClick = onRetry)
        MonoLabel(
            text = "Close", size = 11f, tracking = 1.4f, color = SK.sub,
            modifier = Modifier.clickable(onClick = onClose).padding(8.dp)
        )
    }
}
