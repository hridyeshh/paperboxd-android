package `in`.paperboxd.app.ui.screens.jazy

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.navigation.PipFace
import `in`.paperboxd.app.ui.screens.scan.MonoLabel
import `in`.paperboxd.app.ui.screens.scan.SK
import `in`.paperboxd.app.ui.screens.scan.ScanFlowScreen
import `in`.paperboxd.app.ui.screens.scan.ScanPrefs
import kotlin.math.min

/**
 * Ask Jazy — the merged vibe-search + Scan & Know entry, as a shelf.
 *
 * The feature runs the same four beats the Scan flow already runs, so the two
 * halves read as one thing rather than two design systems behind one bar:
 *
 *   01 raw      Shelf label  — write a feeling on a label, hard ink frames
 *   02 machine  Pulling      — the shelf runs past, log, marquee
 *   03 hinge    The pull     — one spine turns face-out, borders thin
 *   04 calm     The match    — whitespace, hairlines, serif  (JazyResultsDeck)
 *
 * One kit: [SK], which is the app's own light palette. There is no `JZ`.
 * iOS twin: `JazyView`.
 */
object JZ {
    /**
     * The old suggestion chips, re-read as index-card dividers clipped to the
     * top edge of the shelf label.
     * ponytail: four fit the card edge at 390dp; a fifth needs a scrolling row.
     */
    val tabs = listOf("cosy", "wreck me", "found family", "gothic")

    val tabPaper = Color(0xFFE9E2D1)
}

@Composable
fun JazyScreen(
    user: User,
    onDismiss: () -> Unit,
    onOpenBook: (String) -> Unit,
    viewModel: JazyViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var showScan by rememberSaveable { mutableStateOf(false) }
    var showTasteGate by rememberSaveable { mutableStateOf(false) }

    val hasText = state.query.trim().isNotEmpty()
    val scansLeft = remember { ScanPrefs.scansRemaining(context) }

    // Loaded up front so tapping the camera never waits on the network.
    LaunchedEffect(user.username) { viewModel.loadShelfSize(user.username) }

    Box(modifier = Modifier.fillMaxSize().background(SK.bg)) {
        when {
            state.showResults -> JazyResultsDeck(
                query = state.query,
                matches = state.matches,
                onClose = viewModel::closeResults,
                onOpenBook = onOpenBook
            )

            state.isSearching -> JazyPullingLayer(query = state.query)

            else -> JazyLabelLayer(
                query = state.query,
                hasText = hasText,
                scansLeft = scansLeft,
                errorMessage = state.errorMessage,
                focusRequester = focusRequester,
                onQueryChanged = viewModel::onQueryChanged,
                onTab = {
                    viewModel.appendChip(it)
                    focusRequester.requestFocus()
                },
                onSubmit = {
                    keyboard?.hide()
                    viewModel.submit()
                },
                // Scan & Know scores a book against what you already read, so it
                // needs a shelf to score against. Unknown size (offline, request
                // failed) always lets the scan through.
                onScan = {
                    val size = state.shelfSize
                    if (size != null && size < JazyTaste.MINIMUM_SHELF) showTasteGate = true
                    else showScan = true
                },
                onDismiss = onDismiss
            )
        }

        AnimatedVisibility(
            visible = showTasteGate,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            JazyTasteGateSheet(
                shelfSize = state.shelfSize ?: 0,
                onDismiss = { showTasteGate = false }
            )
        }

        if (showScan) {
            ScanFlowScreen(user = user, onDismiss = { showScan = false })
        }
    }
}

// ── 01 · Shelf label ────────────────────────────────────────────────────────

@Composable
private fun JazyLabelLayer(
    query: String,
    hasText: Boolean,
    scansLeft: Int,
    errorMessage: String?,
    focusRequester: FocusRequester,
    onQueryChanged: (String) -> Unit,
    onTab: (String) -> Unit,
    onSubmit: () -> Unit,
    onScan: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // The wall at rest: context, not the reading surface.
        JazyShelfWall(
            running = false,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(470.dp)
                .alpha(0.30f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MonoLabel("Ask Jazy", size = 10f, tracking = 2.4f, color = SK.ink,
                          weight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                BrutalTile(onClick = onDismiss, description = "Close Ask Jazy", size = 44.dp) {
                    Icon(Icons.Outlined.Close, null, tint = SK.ink, modifier = Modifier.size(15.dp))
                }
            }

            Box(
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 14.dp)
                    .fillMaxWidth()
                    .height(2.dp)
                    .background(SK.ink)
            )

            // Tabs clipped to the top edge of the label. The bottom 14dp tucks
            // behind the card, so the tap target stays 44 while only the tab reads.
            Row(
                modifier = Modifier
                    .padding(start = 24.dp, end = 20.dp, top = 34.dp)
                    .offset(y = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                JZ.tabs.forEach { tab ->
                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .background(JZ.tabPaper)
                            .border(2.dp, SK.ink)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTab(tab) }
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        MonoLabel(tab, size = 9.5f, tracking = 1.2f, color = SK.ink)
                    }
                }
            }

            ShelfLabelCard(
                query = query,
                hasText = hasText,
                focusRequester = focusRequester,
                onQueryChanged = onQueryChanged,
                onSubmit = onSubmit,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            // Vibe search and Scan are siblings — same frame, same height, same
            // weight. The camera no longer hides at the side of a pill.
            Row(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, end = 4.dp, bottom = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .alpha(if (hasText) 1f else 0.45f)
                        .hardShadow(SK.ink.copy(alpha = 0.78f))
                        .background(SK.ink)
                        .border(2.dp, SK.ink)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            enabled = hasText
                        ) { onSubmit() }
                        .height(56.dp)
                        .padding(horizontal = 12.dp)
                ) {
                    MonoLabel("Pull the shelf", size = 12.5f, tracking = 1.4f,
                              color = SK.panel, weight = FontWeight.SemiBold)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null,
                         tint = SK.panel, modifier = Modifier.size(16.dp))
                }

                BrutalTile(onClick = onScan, description = "Scan a cover", size = 56.dp) {
                    Icon(Icons.Outlined.PhotoCamera, null, tint = SK.ink, modifier = Modifier.size(22.dp))
                }
            }

            if (errorMessage != null) {
                MonoLabel(
                    errorMessage, size = 10.5f, tracking = 1.2f, color = SK.accent,
                    modifier = Modifier.padding(horizontal = 24.dp).padding(top = 14.dp)
                )
            }

            Spacer(Modifier.weight(1f))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                MonoLabel("$scansLeft free scans left", size = 9.5f, tracking = 1.8f, color = SK.sub)
                Spacer(Modifier.weight(1f))
                MonoLabel("01 / label", size = 9.5f, tracking = 1.8f, color = SK.faint)
            }
        }
    }
}

@Composable
private fun ShelfLabelCard(
    query: String,
    hasText: Boolean,
    focusRequester: FocusRequester,
    onQueryChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(end = 4.dp, bottom = 4.dp)
            .fillMaxWidth()
            .hardShadow(SK.ink)
            .background(SK.panel)
            .border(2.dp, SK.ink)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            MonoLabel("Shelf label", size = 9.5f, tracking = 2.2f, color = SK.faint)
            Spacer(Modifier.weight(1f))
            PipFace(thinking = hasText, modifier = Modifier.size(30.dp, 32.dp).offset(y = (-4).dp))
        }

        Text(
            "What shelf are you\nlooking for?",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 25.sp,
            lineHeight = 29.sp,
            color = SK.ink,
            modifier = Modifier.padding(top = 8.dp)
        )

        Box(
            modifier = Modifier
                .padding(top = 18.dp)
                .fillMaxWidth()
                .drawBehind {
                    drawRect(SK.ink, topLeft = Offset(0f, size.height - 2.dp.toPx()),
                             size = Size(size.width, 2.dp.toPx()))
                }
                .padding(bottom = 9.dp)
        ) {
            if (!hasText) {
                Text(
                    "a cosy autumn mystery…",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    color = SK.faint,
                    maxLines = 1
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChanged,
                singleLine = true,
                textStyle = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 16.sp,
                    color = SK.ink
                ),
                cursorBrush = SolidColor(SK.accent),
                keyboardOptions = KeyboardOptions(imeAction = androidx.compose.ui.text.input.ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSubmit() }),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
        }

        MonoLabel("Write a feeling, not a title", size = 9f, tracking = 1.6f,
                  color = SK.faint, modifier = Modifier.padding(top = 9.dp))
    }
}

// ── 02 · Pulling the shelf ──────────────────────────────────────────────────

/**
 * The wait, as a machine at work rather than a thinking indicator: the shelf
 * runs past under a fixed reading rule, and the log says what is actually
 * happening. No skeleton covers, no "weighing the vibe…".
 */
@Composable
private fun JazyPullingLayer(query: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(top = 12.dp)
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonoLabel("Pulling the shelf", size = 10f, tracking = 2.4f, color = SK.ink,
                          weight = FontWeight.SemiBold)
                Spacer(Modifier.weight(1f))
                MonoLabel("02 / pull", size = 9.5f, tracking = 1.8f, color = SK.faint)
            }
            Box(Modifier.padding(top = 14.dp).fillMaxWidth().height(2.dp).background(SK.ink))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(11.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SK.panel)
                    .border(2.dp, SK.ink)
                    .padding(horizontal = 13.dp, vertical = 11.dp)
            ) {
                PaperBoxdMark(tint = SK.ink, width = 25.dp)
                Text(
                    "“$query”",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 15.sp,
                    color = SK.ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        JazyShelfWall(
            running = true,
            modifier = Modifier.padding(top = 26.dp).fillMaxWidth().height(246.dp)
        )

        JazyLog(modifier = Modifier.padding(horizontal = 20.dp).padding(top = 22.dp))

        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 20.dp).padding(top = 20.dp)
        ) {
            Text(
                "${JazyShelfWall.FLAGGED_COUNT}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 30.sp,
                color = SK.ink
            )
            MonoLabel("spines flagged", size = 9.5f, tracking = 2f, color = SK.sub,
                      modifier = Modifier.padding(bottom = 5.dp))
        }

        Spacer(Modifier.weight(1f))
        JazyMarquee(text = "Pulling —", modifier = Modifier.padding(bottom = 8.dp))
    }
}

@Composable
private fun JazyLog(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth().background(SK.panel).border(2.dp, SK.ink)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(SK.ink, topLeft = Offset(0f, size.height - 2.dp.toPx()),
                             size = Size(size.width, 2.dp.toPx()))
                }
                .padding(horizontal = 11.dp, vertical = 7.dp)
        ) {
            MonoLabel("Log", size = 9f, tracking = 1.8f, color = SK.sub)
            Spacer(Modifier.weight(1f))
            MonoLabel("live", size = 9f, tracking = 1.8f, color = SK.faint)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.padding(horizontal = 11.dp).padding(top = 11.dp, bottom = 13.dp)
        ) {
            LogLine("> embedding query", SK.faint)
            LogLine("> reading the shelves", SK.sub)
            LogLine("> scoring pace · tone · length", SK.ink, caret = true)
        }
    }
}

@Composable
private fun LogLine(text: String, color: Color, caret: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = color)
        if (caret) Box(Modifier.size(7.dp, 12.dp).background(SK.ink))
    }
}

/** iOS `ScanMarquee` twin, kept here so the pulling beat matches the analyzing one. */
@Composable
private fun JazyMarquee(text: String, modifier: Modifier = Modifier, speed: Float = 22f) {
    val cell = "$text   "
    val transition = rememberInfiniteTransition(label = "marquee")
    val t by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing)),
        label = "marqueeOffset"
    )
    Column(modifier = modifier.fillMaxWidth().clipToBounds()) {
        Box(Modifier.fillMaxWidth().height(2.dp).background(SK.ink))
        Box(Modifier.fillMaxWidth().height(22.dp)) {
            Text(
                cell.repeat(8),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.5.sp,
                letterSpacing = 1.6.sp,
                color = SK.ink,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .offset(x = (-t * cell.length * 7.2f).dp)
                    .padding(start = 20.dp)
            )
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(SK.ink))
    }
}

// ── The shelf ───────────────────────────────────────────────────────────────

/**
 * Two ranks of spines running at different speeds. The speed difference is the
 * whole effect — it reads as a deep shelf being gone through, not a strip
 * sliding sideways. Nothing here loads: it is geometry, so the wait costs
 * nothing and cannot itself be slow. iOS `JazyShelfWall` twin.
 */
object JazyShelfWall {
    const val FLAGGED_COUNT = 5

    data class Spine(val width: Float, val height: Float, val color: Color, val flagged: Boolean)

    data class Rank(
        val spines: List<Spine>,
        /** Dp per second. Fast enough to read as a lot of books going by. */
        val speed: Float,
        val alpha: Float,
        /** How far above the shelf board this rank stands, in dp. */
        val lift: Float,
        val border: Float
    ) {
        val cellWidth: Float = spines.sumOf { it.width.toDouble() }.toFloat()
    }

    /** Deterministic — the shelf is the same shelf every time it opens. */
    private fun rank(
        count: Int,
        widths: ClosedFloatingPointRange<Float>,
        heights: ClosedFloatingPointRange<Float>,
        flagEvery: Int?,
        seed: Long
    ): List<Spine> {
        var state = seed
        fun next(range: ClosedFloatingPointRange<Float>): Float {
            state = state * 6364136223846793005L + 1442695040888963407L
            val unit = ((state ushr 33) % 1000).toFloat() / 1000f
            return kotlin.math.round(range.start + unit * (range.endInclusive - range.start))
        }
        return (0 until count).map { i ->
            Spine(
                width = next(widths),
                height = next(heights),
                color = SK.spines[i % SK.spines.size],
                flagged = flagEvery != null && i % flagEvery == 1
            )
        }
    }

    val near = Rank(
        spines = rank(26, 14f..36f, 150f..220f, flagEvery = 5, seed = 20260905L),
        speed = 150f, alpha = 1f, lift = 0f, border = 2f
    )
    val far = Rank(
        spines = rank(30, 10f..22f, 92f..138f, flagEvery = null, seed = 77L),
        speed = 46f, alpha = 0.28f, lift = 30f, border = 1.5f
    )
}

@Composable
private fun JazyShelfWall(running: Boolean, modifier: Modifier = Modifier) {
    // Each rank gets its own 0→1 clock lasting exactly one cell, so the wrap
    // lands on the seam every time. A single shared clock would jump whenever
    // its period was not a whole number of cells.
    val nearPhase = cellPhase(JazyShelfWall.near, running, "near")
    val farPhase = cellPhase(JazyShelfWall.far, running, "far")

    Box(modifier = modifier.clipToBounds()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRank(JazyShelfWall.far, farPhase)
            drawRank(JazyShelfWall.near, nearPhase)
            // The shelf board the run sits on.
            drawRect(SK.ink, topLeft = Offset(0f, size.height - 16.dp.toPx()),
                     size = Size(size.width, 3.dp.toPx()))
        }

        if (running) {
            // The shelf moves; the scanner does not.
            Box(
                Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 104.dp)
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(SK.accent)
            )
            Box(
                Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 82.dp)
                    .background(SK.accent)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                MonoLabel("reading", size = 9f, tracking = 1.6f, color = SK.panel)
            }
        }

        // Edges dissolve into paper so the run has no visible seam.
        Box(Modifier.align(Alignment.CenterStart).width(40.dp).fillMaxHeight()
            .background(Brush.horizontalGradient(listOf(SK.bg, Color.Transparent))))
        Box(Modifier.align(Alignment.CenterEnd).width(40.dp).fillMaxHeight()
            .background(Brush.horizontalGradient(listOf(Color.Transparent, SK.bg))))
    }
}

/** 0→1 across exactly one cell of [rank], at the rank's own speed. */
@Composable
private fun cellPhase(rank: JazyShelfWall.Rank, running: Boolean, label: String): Float {
    if (!running) return 0f
    val transition = rememberInfiniteTransition(label = "shelf-$label")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween((rank.cellWidth / rank.speed * 1000f).toInt(), easing = LinearEasing)
        ),
        label = "phase-$label"
    )
    return phase
}

private fun DrawScope.drawRank(rank: JazyShelfWall.Rank, phase: Float) {
    val base = size.height - 16.dp.toPx() - rank.lift.dp.toPx()
    val cell = rank.cellWidth.dp.toPx()
    val a = rank.alpha
    // One cell scrolls fully out, then repeats — so the loop has no seam.
    var x = -(phase * cell)
    while (x < size.width) {
        for (spine in rank.spines) {
            val w = spine.width.dp.toPx()
            val h = spine.height.dp.toPx()
            if (x + w > 0f && x < size.width) {
                val top = Offset(x, base - h)
                drawRect(spine.color.copy(alpha = spine.color.alpha * a), topLeft = top, size = Size(w, h))
                drawRect(SK.ink.copy(alpha = a), topLeft = top, size = Size(w, h),
                         style = Stroke(rank.border.dp.toPx()))

                // Head and tail bands — what makes a coloured bar read as a spine.
                for (inset in listOf(13.dp.toPx(), h - 17.dp.toPx())) {
                    drawRect(Color.White.copy(alpha = 0.24f * a),
                             topLeft = Offset(x, base - h + inset),
                             size = Size(w, 1.dp.toPx()))
                }

                if (spine.flagged) {
                    drawRect(SK.accent.copy(alpha = a),
                             topLeft = Offset(x + 1.5f.dp.toPx(), base - h + 1.5f.dp.toPx()),
                             size = Size(w - 3f.dp.toPx(), h - 3f.dp.toPx()),
                             style = Stroke(3.dp.toPx()))
                    drawRect(SK.accent.copy(alpha = a),
                             topLeft = Offset(x + w / 2 - 3.5f.dp.toPx(), base - h - 9.dp.toPx()),
                             size = Size(7.dp.toPx(), 7.dp.toPx()))
                }
            }
            x += w
        }
    }
}

// ── Shared bits ─────────────────────────────────────────────────────────────

/** Hard offset shadow — no blur, ever. The feature's only shadow. */
fun Modifier.hardShadow(color: Color, offset: Dp = 4.dp): Modifier =
    this.drawBehind {
        val off = offset.toPx()
        drawRect(color, topLeft = Offset(off, off), size = size)
    }

/** Square ink-framed icon button with the hard offset shadow. */
@Composable
internal fun BrutalTile(
    onClick: () -> Unit,
    description: String,
    size: Dp = 44.dp,
    content: @Composable () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .hardShadow(SK.ink, 3.dp)
            .size(size)
            .background(SK.panel)
            .border(2.dp, SK.ink)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClickLabel = description,
                onClick = onClick
            )
    ) { content() }
}

/** The PaperBoxd mark, traced from `public/paperboxd.svg` (viewBox 276.08×199.6). */
@Composable
internal fun PaperBoxdMark(tint: Color, width: Dp) {
    val path = remember { PathParser().parsePathString(MARK_PATH).toPath() }
    Canvas(modifier = Modifier.size(width, width * (199.6f / 276.08f))) {
        val s = min(size.width / 276.08f, size.height / 199.6f)
        scale(s, s, pivot = Offset.Zero) {
            // The two counters are holes, not shapes — even-odd, like the SVG.
            path.fillType = PathFillType.EvenOdd
            drawPath(path, tint)
        }
    }
}

private const val MARK_PATH =
    "M200.95,79.8c2.02-4.1,3.06-8.35,3.06-12.69,0-16.35-11.63-27.94-36.62-27.94h-55.68" +
    "c-45.23,0-61.25,20.14-61.25,49.57s15.77,49.1,60.16,49.42v4.74c0,11.24,6.05,17.29,17.29,17.29h61.81" +
    "c34.35,0,50-16.86,50-41.68,0-21.71-16.34-36.96-38.78-38.71ZM93.73,95.67c-8.87,0-15.42-6.15-15.42-14.66" +
    "s6.55-14.61,15.42-14.61,15.4,6.15,15.4,14.61-6.55,14.66-15.4,14.66ZM196.79,132.77c-8.07,0-14.04-5.62-14.04-13.36" +
    "s5.96-13.31,14.04-13.31,14.06,5.6,14.06,13.31-5.96,13.36-14.06,13.36Z"
