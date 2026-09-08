package `in`.paperboxd.app.ui.screens.jazy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import `in`.paperboxd.app.domain.model.VibeMatch
import `in`.paperboxd.app.ui.screens.scan.CountUpText
import `in`.paperboxd.app.ui.screens.scan.MonoLabel
import `in`.paperboxd.app.ui.screens.scan.SK
import kotlinx.coroutines.delay
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * Beats 03 and 04 of the Ask Jazy arc — the hinge and the resolution.
 *
 *   03 · The pull   one spine turns face-out; hard frames start thinning, the
 *                   hard shadow becomes a faint accent offset, the score counts
 *                   up in mono. Swipe left slots it back, right pulls the next
 *                   one out of the wall.
 *   04 · The match  everything exhales: no frames, no shadows, hairlines,
 *                   whitespace and serif — the same shape as `BreakdownScreen`.
 *
 * The old rounded card with the "92% match" pill and the blurred drop shadow is
 * gone; so is `JZ`'s palette. iOS twin: `JazyResultsDeck`.
 */
@Composable
fun JazyResultsDeck(
    query: String,
    matches: List<VibeMatch>,
    onClose: () -> Unit,
    onOpenBook: (String) -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    /** false = 03 (the pull), true = 04 (the match). One book, two beats. */
    var expanded by remember { mutableStateOf(false) }
    var leaving by remember { mutableStateOf(false) }
    var leavingDirection by remember { mutableFloatStateOf(-1f) }
    var dragX by remember { mutableFloatStateOf(0f) }
    val flickDistancePx = with(LocalDensity.current) { 110.dp.toPx() }
    val done = index >= matches.size

    LaunchedEffect(leaving) {
        if (leaving) {
            dragX = 0f
            delay(350)
            index += 1
            leaving = false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(SK.bgSofter)) {
        when {
            done -> EndOfShelf(count = matches.size, onClose = onClose)

            expanded -> JazyMatchSheet(
                query = query,
                match = matches[index],
                onBack = { expanded = false },
                onOpen = { onOpenBook(matches[index].book.id) },
                onNext = {
                    expanded = false
                    if (!leaving) { leavingDirection = -1f; leaving = true }
                }
            )

            else -> PullBeat(
                query = query,
                match = matches[index],
                total = matches.size,
                index = index,
                dragX = dragX,
                leaving = leaving,
                leavingDirection = leavingDirection,
                onDrag = { if (!leaving) dragX += it },
                onDragEnd = {
                    if (abs(dragX) > flickDistancePx && !leaving) {
                        leavingDirection = if (dragX < 0) -1f else 1f
                        leaving = true
                    } else dragX = 0f
                },
                onWhy = { expanded = true },
                onClose = onClose
            )
        }
    }
}

// ── 03 · The pull ───────────────────────────────────────────────────────────

@Composable
private fun PullBeat(
    query: String,
    match: VibeMatch,
    total: Int,
    index: Int,
    dragX: Float,
    leaving: Boolean,
    leavingDirection: Float,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onWhy: () -> Unit,
    onClose: () -> Unit
) {
    val book = match.book
    val offsetPx = if (leaving) with(LocalDensity.current) { 520.dp.toPx() } * leavingDirection else dragX
    val tilt = if (leaving) 9f * leavingDirection else dragX / 22f

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding().padding(top = 12.dp)) {
        // The 2dp rule thins to a hairline here: the arc turning.
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonoLabel("The pull", size = 10f, tracking = 2.4f, color = SK.ink,
                          weight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                MonoLabel("03 / hinge", size = 9.5f, tracking = 1.8f, color = SK.faint)
            }
            Box(
                Modifier.padding(top = 14.dp).fillMaxWidth().height(1.dp)
                    .background(SK.ink.copy(alpha = 0.22f))
            )
            Text(
                "“$query”",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                color = SK.sub,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 11.dp)
            )
        }

        Spacer(Modifier.weight(1f))

        // The spine, rotated out of the wall to cover-face. Its neighbours stay
        // edge-on — that is what makes swiping read as physical.
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            modifier = Modifier
                .fillMaxWidth()
                .offset { androidx.compose.ui.unit.IntOffset(offsetPx.roundToInt(), 0) }
                .rotate(tilt)
                .alpha(if (leaving) 0f else 1f)
                .pointerInput(match.id) {
                    detectHorizontalDragGestures(
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragEnd
                    ) { _, amount -> onDrag(amount) }
                }
        ) {
            Box(
                Modifier.width(22.dp).height(226.dp).alpha(0.8f)
                    .background(SK.spines[1]).border(1.5.dp, SK.ink)
            )

            Box(contentAlignment = Alignment.Center) {
                // The last trace of the hard shadow: a faint accent offset, no blur.
                Box(
                    Modifier.offset(x = 7.dp, y = 7.dp).width(154.dp).height(231.dp)
                        .background(SK.accent.copy(alpha = 0.22f))
                )
                Box(Modifier.width(154.dp).height(231.dp).border(2.dp, SK.ink)) {
                    CoverArt(match, titleSize = 21f, gutter = 24.dp)
                }
            }

            Box(
                Modifier.width(28.dp).height(200.dp).alpha(0.8f)
                    .background(SK.spines[4]).border(1.5.dp, SK.ink)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(top = 30.dp)
        ) {
            CountUpText(
                target = match.matchPercent,
                durationMs = 1100,
                fontSize = 54f,
                color = SK.ink,
                fontWeight = FontWeight.Medium,
                grouping = false
            )
            MonoLabel("match", size = 10f, tracking = 3f, color = SK.faint,
                      modifier = Modifier.padding(top = 7.dp))
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp, vertical = 0.dp)
                .padding(top = 22.dp)
        ) {
            Text(
                book.volumeInfo.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 27.sp,
                lineHeight = 32.sp,
                color = SK.ink,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            book.volumeInfo.authors?.joinToString(", ")?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = 13.sp, color = SK.sub, maxLines = 1,
                     overflow = TextOverflow.Ellipsis,
                     modifier = Modifier.padding(top = 7.dp))
            }
            book.volumeInfo.categories?.take(3)?.joinToString(" · ")?.takeIf { it.isNotBlank() }?.let {
                MonoLabel(it, size = 9.5f, tracking = 1.8f, color = SK.faint,
                          modifier = Modifier.padding(top = 9.dp))
            }
        }

        Spacer(Modifier.weight(1f))

        Box(Modifier.padding(horizontal = 26.dp).fillMaxWidth().height(1.dp)
            .background(SK.ink.copy(alpha = 0.14f)))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp).padding(top = 15.dp)
        ) {
            MonoLabel("← slot back", size = 9.5f, tracking = 1.6f, color = SK.faint)
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically) {
                repeat(total) { i ->
                    Box(
                        Modifier
                            .width(if (i == index) 18.dp else 4.dp)
                            .height(4.dp)
                            .background(if (i == index) SK.accent else SK.ink.copy(alpha = 0.18f))
                    )
                }
            }
            Spacer(Modifier.weight(1f))
            MonoLabel("pull next →", size = 9.5f, tracking = 1.6f, color = SK.faint)
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp)
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f).height(52.dp).background(SK.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onWhy
                    )
            ) {
                MonoLabel("Why it fits", size = 12f, tracking = 1.4f,
                          color = SK.panel, weight = FontWeight.SemiBold)
                Icon(Icons.Outlined.KeyboardArrowDown, null, tint = SK.panel,
                     modifier = Modifier.size(18.dp))
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(52.dp).border(1.5.dp, SK.ink.copy(alpha = 0.30f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClickLabel = "Back to Ask Jazy", onClick = onClose
                    )
            ) {
                MonoLabel("↺", size = 17f, tracking = 0f, color = SK.ink)
            }
        }
    }
}

// ── 04 · The match ──────────────────────────────────────────────────────────

/**
 * The calm resolution. Deliberately built like `BreakdownScreen`: no boxes, no
 * shadows, hairline splits, a mono score, a serif verdict, whitespace.
 */
@Composable
private fun JazyMatchSheet(
    query: String,
    match: VibeMatch,
    onBack: () -> Unit,
    onOpen: () -> Unit,
    onNext: () -> Unit
) {
    val book = match.book

    Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 26.dp)
                .padding(top = 12.dp, bottom = 24.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonoLabel("04 / the match", size = 10f, tracking = 2.2f, color = SK.faint)
                Spacer(Modifier.weight(1f))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(34.dp).clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClickLabel = "Back to the pull", onClick = onBack
                    )
                ) {
                    Icon(Icons.Outlined.KeyboardArrowUp, null, tint = SK.sub,
                         modifier = Modifier.size(18.dp))
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(18.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp)
            ) {
                // The one radius left in the feature — the same one covers use.
                Box(Modifier.width(62.dp).height(93.dp).clip(RoundedCornerShape(3.dp))) {
                    CoverArt(match, titleSize = 9.5f, gutter = 10.dp)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        book.volumeInfo.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium,
                        fontSize = 21.sp,
                        lineHeight = 25.sp,
                        color = SK.ink,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    book.volumeInfo.authors?.joinToString(", ")?.takeIf { it.isNotBlank() }?.let {
                        Text(it, fontSize = 12.5.sp, color = SK.sub, maxLines = 2,
                             overflow = TextOverflow.Ellipsis,
                             modifier = Modifier.padding(top = 5.dp))
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "${match.matchPercent}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 34.sp,
                        color = SK.ink
                    )
                    MonoLabel("match", size = 10.5f, tracking = 1f, color = SK.faint,
                              modifier = Modifier.padding(top = 4.dp))
                }
            }

            Hairline(Modifier.padding(top = 30.dp))

            // What you asked, against what the book actually is. Both sides are
            // real: the left is the query, the right is the book's categories.
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f).padding(vertical = 18.dp)) {
                    MonoLabel("You asked", size = 11f, tracking = 0.8f, color = SK.faint)
                    Text(
                        query,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = SK.ink,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                Box(Modifier.width(1.dp).fillMaxHeight().background(SK.ink.copy(alpha = 0.10f)))
                Column(
                    modifier = Modifier.weight(1f).padding(vertical = 18.dp, horizontal = 0.dp)
                        .padding(start = 22.dp)
                ) {
                    MonoLabel("What it is", size = 11f, tracking = 0.8f, color = SK.faint)
                    Text(
                        book.volumeInfo.categories?.take(3)?.joinToString(", ")?.lowercase()
                            ?.takeIf { it.isNotBlank() } ?: "—",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = SK.ink,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Hairline()

            Section(
                eyebrow = "Why it fits",
                body = match.matchReason.ifBlank {
                    "Jazy matched this on the shelf's own signals."
                },
                modifier = Modifier.padding(top = 30.dp)
            )

            if (match.matchCaveat.isNotBlank()) {
                Section(
                    eyebrow = "What it isn't",
                    body = match.matchCaveat,
                    muted = true,
                    modifier = Modifier.padding(top = 26.dp)
                )
            }

            Hairline(Modifier.padding(top = 28.dp))
            MonoLabel("Pulled from your vibe · not a bestseller list",
                      size = 10f, tracking = 1.4f, color = SK.faint,
                      modifier = Modifier.padding(top = 14.dp))
        }

        Hairline()
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 26.dp)
                .padding(top = 16.dp, bottom = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f).height(52.dp).background(SK.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClick = onOpen
                    )
            ) {
                MonoLabel("Open book", size = 12f, tracking = 1.4f,
                          color = SK.panel, weight = FontWeight.SemiBold)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = SK.panel,
                     modifier = Modifier.size(15.dp))
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(52.dp).border(1.dp, SK.ink.copy(alpha = 0.18f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null, onClickLabel = "Pull the next book", onClick = onNext
                    )
            ) {
                MonoLabel("→|", size = 14f, tracking = 0f, color = SK.ink)
            }
        }
    }
}

// ── Shared ──────────────────────────────────────────────────────────────────

@Composable
private fun Hairline(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(1.dp).background(SK.ink.copy(alpha = 0.10f)))
}

@Composable
private fun Section(
    eyebrow: String,
    body: String,
    muted: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        MonoLabel(eyebrow, size = 10f, tracking = 2.2f, color = SK.faint)
        Text(
            body,
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 26.sp,
            color = if (muted) SK.sub else SK.ink,
            modifier = Modifier.padding(top = 11.dp)
        )
    }
}

@Composable
private fun EndOfShelf(count: Int, onClose: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        modifier = Modifier.fillMaxSize().padding(horizontal = 32.dp)
    ) {
        MonoLabel("end of shelf", size = 10f, tracking = 2.4f, color = SK.faint)
        Text(
            "That was all $count. Another shelf?",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            color = SK.ink
        )
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(end = 4.dp, bottom = 4.dp)
                .hardShadow(SK.ink.copy(alpha = 0.78f))
                .background(SK.ink)
                .border(2.dp, SK.ink)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, onClick = onClose
                )
                .height(52.dp)
                .padding(horizontal = 24.dp)
        ) {
            MonoLabel("New label", size = 12f, tracking = 1.4f,
                      color = SK.panel, weight = FontWeight.SemiBold)
        }
    }
}

/** Real artwork when there is a cover URL; otherwise the app's editorial fallback. */
@Composable
private fun CoverArt(match: VibeMatch, titleSize: Float, gutter: androidx.compose.ui.unit.Dp) {
    val url = match.book.volumeInfo.imageLinks?.thumbnail?.replace("http://", "https://")
    if (!url.isNullOrBlank()) {
        AsyncImage(
            model = url,
            contentDescription = match.book.volumeInfo.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    } else {
        Box(Modifier.fillMaxSize().background(SK.coverGradient)) {
            Box(Modifier.offset(x = gutter).width(1.dp).fillMaxHeight()
                .background(Color.White.copy(alpha = 0.28f)))
            Text(
                match.book.volumeInfo.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = titleSize.sp,
                lineHeight = (titleSize * 1.15f).sp,
                color = Color.White.copy(alpha = 0.96f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = gutter + 10.dp, end = 10.dp, bottom = 12.dp)
            )
        }
    }
}
