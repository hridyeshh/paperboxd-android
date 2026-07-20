package `in`.paperboxd.app.ui.screens.jazy

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
import `in`.paperboxd.app.ui.navigation.PipFace
import kotlinx.coroutines.delay

/**
 * Jazy's vibe results — one match card at a time, skip or open.
 * The top card flicks off to the left; the two behind it sit stacked and scaled.
 * iOS twin: `JazyResultsDeck`.
 */
@Composable
fun JazyResultsDeck(
    query: String,
    matches: List<VibeMatch>,
    onClose: () -> Unit,
    onOpenBook: (String) -> Unit
) {
    var index by remember { mutableIntStateOf(0) }
    var leaving by remember { mutableStateOf(false) }
    // Which way the top card flies out: the way it was thrown, or left when the
    // Next button was tapped.
    var leavingDirection by remember { mutableFloatStateOf(-1f) }
    // Live finger position on the top card, in px. Released short of
    // flickDistancePx it springs back; past it the card leaves.
    var dragX by remember { mutableFloatStateOf(0f) }
    val flickDistancePx = with(LocalDensity.current) { 110.dp.toPx() }
    val done = index >= matches.size

    // The flick-off runs on its own clock, then the deck advances.
    LaunchedEffect(leaving) {
        if (leaving) {
            dragX = 0f
            delay(400)
            index += 1
            leaving = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JZ.bg)
            .systemBarsPadding()
            .padding(horizontal = 22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconTile(onClick = onClose, description = "Back to Ask Jazy") {
                Icon(Icons.Outlined.Close, null, tint = JZ.ink, modifier = Modifier.size(15.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Jazy found ${matches.size} book${if (matches.size == 1) "" else "s"} for",
                    fontSize = 12.sp,
                    color = JZ.sub
                )
                Text(
                    "“$query”",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    color = JZ.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            PipFace(thinking = done, modifier = Modifier.size(36.dp, 40.dp))
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(top = 22.dp, bottom = 14.dp)
        ) {
            if (done) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        "That was all ${matches.size}. Another vibe?",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 22.sp,
                        lineHeight = 26.sp,
                        color = JZ.ink
                    )
                    Text(
                        "Search again",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(JZ.ink)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onClose() }
                            .padding(horizontal = 22.dp, vertical = 13.dp)
                    )
                }
            } else {
                // Back-to-front so the top card (depth 0) draws last.
                val visible = matches.subList(index, minOf(index + 3, matches.size))
                visible.asReversed().forEachIndexed { reversed, match ->
                    val depth = visible.size - 1 - reversed
                    JazyMatchCard(
                        match = match,
                        depth = depth,
                        leaving = depth == 0 && leaving,
                        leavingDirection = leavingDirection,
                        dragX = if (depth == 0) dragX else 0f,
                        onDrag = { delta -> if (!leaving) dragX += delta },
                        onDragEnd = {
                            if (leaving) return@JazyMatchCard
                            if (kotlin.math.abs(dragX) > flickDistancePx) {
                                leavingDirection = if (dragX < 0f) -1f else 1f
                                leaving = true
                            } else {
                                dragX = 0f
                            }
                        },
                        onNext = {
                            if (!leaving) {
                                leavingDirection = -1f
                                leaving = true
                            }
                        },
                        onOpen = { onOpenBook(match.book.id) }
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 26.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            matches.indices.forEach { i ->
                val dotWidth by animateDpAsState(
                    if (i == index) 18.dp else 6.dp, tween(300), label = "jazy-dot-$i"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .width(dotWidth)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (i == index) JZ.accent else JZ.ink.copy(alpha = 0.16f))
                )
            }
        }
    }
}

@Composable
private fun ReasonLine(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    tint: Color,
    ink: Color
) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(
            icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.padding(top = 2.dp).size(13.dp)
        )
        Text(text, fontSize = 13.sp, lineHeight = 19.sp, color = ink)
    }
}

@Composable
private fun JazyMatchCard(
    match: VibeMatch,
    depth: Int,
    leaving: Boolean,
    leavingDirection: Float,
    dragX: Float,
    onDrag: (Float) -> Unit,
    onDragEnd: () -> Unit,
    onNext: () -> Unit,
    onOpen: () -> Unit
) {
    val book = match.book
    val density = LocalDensity.current
    // The flick uses the animated Dp; a live drag tracks the finger with no
    // animation in between, so the card doesn't lag behind the touch.
    val flickX by animateDpAsState(
        if (leaving) (520 * leavingDirection).dp else 0.dp, tween(420), label = "jazy-card-x"
    )
    val dragDp = if (leaving) 0.dp else with(density) { dragX.toDp() }
    val cardAlpha by animateFloatAsState(
        if (leaving) 0f else if (depth == 2) 0.55f else 1f,
        tween(380),
        label = "jazy-card-alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = (depth * 14).dp)
            .offset(x = flickX + dragDp)
            .scale(1f - depth * 0.045f)
            // Tilt follows the throw, so the card pivots off the wrist rather
            // than sliding flat.
            .rotate(if (leaving) 9f * leavingDirection else dragX / 60f)
            .alpha(cardAlpha)
            .then(
                if (depth == 0) {
                    Modifier.pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = onDragEnd,
                            onDragCancel = onDragEnd
                        ) { change, delta ->
                            change.consume()
                            onDrag(delta)
                        }
                    }
                } else {
                    Modifier
                }
            )
            .shadow(if (depth == 0) 18.dp else 6.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .background(JZ.card)
            .border(1.dp, JZ.line, RoundedCornerShape(20.dp))
            .padding(24.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            Box(
                modifier = Modifier
                    .width(92.dp)
                    .height(138.dp)
                    .shadow(10.dp, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7A9BB5), Color(0xFF3D5A72), Color(0xFF22333F))
                        )
                    )
            ) {
                val cover = book.coverUrl
                if (cover != null) {
                    AsyncImage(
                        model = cover,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        book.title,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        lineHeight = 15.sp,
                        color = Color.White.copy(alpha = 0.95f),
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Text(
                    "${match.matchPercent}% match",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = JZ.accent,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(JZ.accent.copy(alpha = 0.1f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Text(
                    book.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 21.sp,
                    lineHeight = 24.sp,
                    color = JZ.ink,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                if (book.authorLine.isNotEmpty()) {
                    Text(
                        book.authorLine,
                        fontSize = 13.sp,
                        color = JZ.sub,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (book.categories.isNotEmpty()) {
                    Text(
                        book.categories.take(3).joinToString(" · ").lowercase(),
                        fontSize = 11.5.sp,
                        color = JZ.faint,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        // Why Jazy picked it, and what it's honest about. Both come from Claude
        // alongside the match percent above.
        if (match.matchReason.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)
                    .height(1.dp)
                    .background(JZ.line)
            )
            Column(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ReasonLine(Icons.Outlined.Check, match.matchReason, JZ.accent, JZ.ink)
                if (match.matchCaveat.isNotEmpty()) {
                    ReasonLine(Icons.Outlined.Info, match.matchCaveat, JZ.faint, JZ.sub)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Open is the primary action and takes the full width; Next sits under
        // it, so skipping never competes with opening.
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(JZ.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onOpen() }
                    .padding(vertical = 13.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Open this book",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(start = 7.dp).size(15.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, JZ.line, RoundedCornerShape(12.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onNext() }
                    .padding(vertical = 13.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Next", fontSize = 14.sp, color = JZ.sub)
                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = JZ.sub,
                    modifier = Modifier.padding(start = 4.dp).size(15.dp)
                )
            }
        }
    }
}
