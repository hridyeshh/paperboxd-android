package `in`.paperboxd.app.ui.screens.fusion

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.domain.model.FusionBook
import `in`.paperboxd.app.domain.model.FusionStory
import `in`.paperboxd.app.domain.model.AttachKind
import `in`.paperboxd.app.domain.model.AttachmentRef
import `in`.paperboxd.app.ui.screens.messages.SendSheet
import `in`.paperboxd.app.ui.screens.wrapped.PbPauseBars
import `in`.paperboxd.app.ui.screens.wrapped.PbRise
import `in`.paperboxd.app.ui.screens.wrapped.WrappedStill
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.io.FileOutputStream

// The Fusion story player, the Explore page it ends on, and the share card.
// Twin of iOS FusionPlayerView.swift; playback mirrors WrappedStoryPlayer.

private const val PAGE_MILLIS = 6200f
private const val TICK_MILLIS = 60L

@Composable
fun FusionStoryPlayer(story: FusionStory, actions: FusionActions, onClose: () -> Unit) {
    val chapters = remember(story) { fusionChapters(story) }
    var index by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }
    var paused by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var shareOpen by remember { mutableStateOf(false) }

    val chapter = chapters[index.coerceIn(0, chapters.lastIndex)]
    val fg = if (chapter.isDark) FZ.Bg else FZ.Ink
    val insets = WindowInsets.safeDrawing.asPaddingValues()

    fun go(d: Int) {
        val next = index + d
        when {
            next < 0 -> progress = 0f
            next > chapters.lastIndex -> { finished = true; progress = 1f }
            else -> { index = next; progress = 0f }
        }
    }

    val pageActions = FusionActions(
        toast = actions.toast, explore = actions.explore, addToTbr = actions.addToTbr,
        share = { shareOpen = true }, next = { go(1) }
    )

    LaunchedEffect(index, paused, shareOpen, finished) {
        if (paused || shareOpen || finished) return@LaunchedEffect
        while (progress < 1f) {
            delay(TICK_MILLIS)
            progress += TICK_MILLIS / PAGE_MILLIS
        }
        go(1)
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(if (chapter.isDark) FZ.Ink else FZ.Bg)) {
        val scale = maxWidth / FZ.DesignWidth
        val designHeight = maxHeight / scale
        val widthPx = constraints.maxWidth.toFloat()

        // Taps and holds are read on the page's parent so buttons inside a page
        // (Add to TBR, Explore) consume their own taps first.
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(chapters) {
                    detectTapGestures(
                        onPress = { offset ->
                            val quick = withTimeoutOrNull(220L) { tryAwaitRelease() }
                            if (quick == null) {
                                paused = true
                                tryAwaitRelease()
                                paused = false
                            } else if (!finished) {
                                go(if (offset.x < widthPx * 0.32f) -1 else 1)
                            }
                        }
                    )
                }
        ) {
            Box(
                Modifier
                    .requiredWidth(FZ.DesignWidth)
                    .requiredHeight(designHeight)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        transformOrigin = TransformOrigin(0f, 0f)
                    }
                    .clipToBounds()
            ) {
                key(chapter.id) { chapter.content(story, index + 1, chapters.size, pageActions) }
            }
        }

        Column(Modifier.fillMaxSize().padding(insets)) {
            Row(
                Modifier.fillMaxWidth().padding(start = 16.dp, end = 12.dp, top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                chapters.forEachIndexed { i, _ ->
                    Box(Modifier.weight(1f).height(2.dp).background(fg.copy(alpha = if (chapter.isDark) 0.24f else 0.16f))) {
                        val fill = when {
                            i < index -> 1f
                            i == index -> progress.coerceIn(0f, 1f)
                            else -> 0f
                        }
                        if (fill > 0f) Box(Modifier.fillMaxWidth(fill).fillMaxHeight().background(fg))
                    }
                }
                Icon(
                    Icons.Outlined.Close, contentDescription = "Close Fusion", tint = fg,
                    modifier = Modifier.padding(start = 10.dp).size(20.dp).fzClick(onClose)
                )
            }
            Spacer(Modifier.weight(1f))
            if (paused && !shareOpen) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { PbPauseBars(fg) }
                Spacer(Modifier.weight(1f))
            }
            Box(Modifier.fillMaxWidth().padding(bottom = 14.dp), contentAlignment = Alignment.Center) {
                Box(
                    Modifier
                        .background(if (chapter.isDark) FZ.Bg.copy(alpha = 0.1f) else FZ.Ink.copy(alpha = 0.05f))
                        .border(1.dp, if (chapter.isDark) FZ.Bg.copy(alpha = 0.4f) else FZ.Ink.copy(alpha = 0.34f))
                        .fzClick { shareOpen = true }
                        .padding(horizontal = 18.dp, vertical = 11.dp)
                ) {
                    Text("SHARE THIS", color = fg, fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold, fontSize = 10.sp, letterSpacing = 0.14.em)
                }
            }
        }
    }

    if (shareOpen) FusionShareSheet(story) { shareOpen = false }
}

// MARK: - Explore

@Composable
fun FusionExploreScreen(story: FusionStory, actions: FusionActions, onClose: () -> Unit) {
    var tab by remember { mutableIntStateOf(0) }
    var shareOpen by remember { mutableStateOf(false) }
    val tabs = listOf("For both", "For you", "For ${story.them.first}")
    val rows: List<FusionBook> = listOf(story.both, story.forYou, story.forThem)[tab]
    val insets = WindowInsets.safeDrawing.asPaddingValues()

    Column(Modifier.fillMaxSize().background(FZ.Bg).padding(insets).padding(top = 20.dp, bottom = 12.dp)) {
        Column(Modifier.padding(horizontal = 30.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FzMonogram(story.me.initial, 28.dp, accent = true)
                FzMonogram(story.them.initial, 28.dp)
                Spacer(Modifier.weight(1f))
                FzNum("${story.score}%", 26f, FZ.Accent)
                Icon(Icons.Outlined.Close, "Close Fusion", tint = FZ.Ink, modifier = Modifier.padding(start = 12.dp).size(20.dp).fzClick(onClose))
            }
            PbRise(140, modifier = Modifier.padding(top = 14.dp)) { FzHeading("Your Fusion", 32f) }
            Row(Modifier.padding(top = 18.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                tabs.forEachIndexed { i, t ->
                    Box(
                        Modifier.background(if (i == tab) FZ.Ink else FZ.Bg).border(1.dp, FZ.Ink)
                            .fzClick { tab = i }.padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(t.uppercase(), color = if (i == tab) FZ.Bg else FZ.Ink, fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold, fontSize = 9.5.sp, letterSpacing = 0.1.em, maxLines = 1)
                    }
                }
            }
        }
        Column(Modifier.weight(1f).padding(top = 22.dp).verticalScroll(rememberScrollState()).padding(horizontal = 30.dp)) {
            if (rows.isEmpty()) FzBody("Nothing here yet. Rate a few more books and this fills in.", 13.5f, modifier = Modifier.padding(top = 14.dp))
            rows.forEachIndexed { i, b ->
                Box(Modifier.fillMaxWidth().height(1.dp).background(FZ.Line))
                Row(Modifier.padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                    Box(Modifier.width(20.dp)) { FzNum(fzIndex(i + 1), 11f, FZ.Grey) }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FzHeading(b.title, 19f)
                        FzLabel(b.author, 9f)
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        b.match?.let { FzLabel(it, 8.5f, if (it == "Wildcard") FZ.Ink else FZ.Accent) }
                        FzPill("+ TBR", compact = true, fullWidth = false) { actions.addToTbr(listOf(b)) }
                    }
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(FZ.Line))
            FzBody("Rebuilt when either of you rates something new.", 12.5f, modifier = Modifier.padding(top = 14.dp))
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(FZ.Line))
        FzPill("Share this Fusion", solid = true, modifier = Modifier.padding(horizontal = 30.dp).padding(top = 16.dp)) { shareOpen = true }
    }

    if (shareOpen) FusionShareSheet(story) { shareOpen = false }
}

// MARK: - Share card

/**
 * The artifact: ink ground, the two names overprinted, the number at full
 * width. Designed at 300×534. Only counts and first names — no ratings or
 * titles leave the app on it.
 */
@Composable
fun FusionShareCard(story: FusionStory, width: Dp = 300.dp) {
    val k = width.value / 300f
    Column(
        Modifier.size(width, (534 * k).dp).background(FZ.Ink)
            .padding(horizontal = (26 * k).dp, vertical = (28 * k).dp)
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("PaperBoxd", color = FZ.Bg, fontFamily = FZ.Script, fontSize = (21 * k).sp)
            Spacer(Modifier.weight(1f))
            Text("FUSION", color = FZ.Bg.copy(alpha = 0.6f), fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold, fontSize = (9 * k).sp, letterSpacing = 0.22.em)
        }
        Box(Modifier.padding(top = (12 * k).dp).fillMaxWidth().height(1.dp).background(FZ.Bg.copy(alpha = 0.24f)))
        Column(Modifier.padding(top = (26 * k).dp)) {
            Text(story.me.first.uppercase(), color = FZ.Accent, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = (30 * k).sp, lineHeight = (30 * k).sp, letterSpacing = (-0.035).em)
            Text(story.them.first.uppercase(), color = FZ.Bg, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = (30 * k).sp, lineHeight = (30 * k).sp, letterSpacing = (-0.035).em,
                modifier = Modifier.offset(y = (-6 * k).dp).padding(start = (13 * k).dp))
        }
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.Bottom) {
            Text("${story.score}", color = FZ.Bg, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = (128 * k).sp, lineHeight = (110 * k).sp, letterSpacing = (-0.055).em)
            Text("%", color = FZ.Accent, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = (42 * k).sp, modifier = Modifier.padding(bottom = (14 * k).dp))
        }
        Text("TASTE FUSION", color = FZ.Bg.copy(alpha = 0.72f), fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold, fontSize = (11 * k).sp, letterSpacing = 0.26.em, modifier = Modifier.padding(top = (6 * k).dp))
        Box(Modifier.padding(top = (18 * k).dp).fillMaxWidth().height(1.dp).background(FZ.Bg.copy(alpha = 0.24f)))
        listOf(story.lovedCount to "shared favourites", story.sharedAuthors.size to "shared authors", story.waitingCount to "books discovered").forEach { (n, l) ->
            Row(Modifier.padding(vertical = (9 * k).dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy((12 * k).dp)) {
                Text("$n", color = FZ.Bg, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = (20 * k).sp, modifier = Modifier.width((30 * k).dp))
                Text(l, color = FZ.Bg.copy(alpha = 0.74f), fontFamily = FZ.Sans, fontSize = (12.5f * k).sp)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(FZ.Bg.copy(alpha = 0.12f)))
        }
        Row(Modifier.padding(top = (22 * k).dp), verticalAlignment = Alignment.Bottom) {
            Text("Two tastes.\nOne discovery.", color = FZ.Bg, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = (17 * k).sp, lineHeight = (21 * k).sp)
            Spacer(Modifier.weight(1f))
            Text("CREATE YOUR\nFUSION ON\nPAPERBOXD", color = FZ.Bg.copy(alpha = 0.56f), fontFamily = FZ.Sans, fontWeight = FontWeight.SemiBold, fontSize = (8 * k).sp, lineHeight = (12.8f * k).sp, letterSpacing = 0.14.em, textAlign = TextAlign.End)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FusionShareSheet(story: FusionStory, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardLayer = rememberGraphicsLayer()
    var failed by remember { mutableStateOf(false) }
    var showSend by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = FZ.Ink,
        dragHandle = null
    ) {
        Column(Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 28.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(width = 40.dp, height = 3.5.dp).background(FZ.Bg.copy(alpha = 0.3f)))
            // Recorded at full size and only drawn scaled, so the export is a
            // 1080-wide asset rather than a screenshot of the preview.
            // The slot is the preview's size; the card inside overflows it
            // centred and is scaled about its centre to fit exactly.
            Box(Modifier.padding(top = 18.dp).size(250.dp, (534f * 250f / 360f).dp)) {
                Box(
                    Modifier
                        .requiredSize(360.dp, 534.dp)
                        .graphicsLayer { scaleX = 250f / 360f; scaleY = 250f / 360f; transformOrigin = TransformOrigin.Center }
                        .drawWithContent {
                            cardLayer.record { this@drawWithContent.drawContent() }
                            drawLayer(cardLayer)
                        }
                ) {
                    WrappedStill { FusionShareCard(story, 360.dp) }
                }
            }
            FzLabel("No ratings, titles or notes are on the card", 9f, FZ.Bg.copy(alpha = 0.5f), Modifier.padding(top = 16.dp))
            FzPill("Send on Paperboxd", solid = true, light = true, modifier = Modifier.padding(horizontal = 26.dp).padding(top = 16.dp)) {
                showSend = true
            }
            FzPill("Share card", solid = false, light = true, modifier = Modifier.padding(horizontal = 26.dp).padding(top = 10.dp)) {
                scope.launch {
                    val uri = cacheCard(context, cardLayer.toImageBitmap().asAndroidBitmap())
                    if (uri == null) { failed = true; return@launch }
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "image/png"
                        putExtra(Intent.EXTRA_STREAM, uri)
                        putExtra(Intent.EXTRA_TEXT, "Our Fusion on PaperBoxd")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(send, "Share"))
                    onDismiss()
                }
            }
            if (failed) FzLabel("Couldn't prepare the image", 9f, FZ.Accent, Modifier.padding(top = 12.dp))
        }
    }
    if (showSend) SendSheet(AttachmentRef(AttachKind.FUSION, story.id), onDismiss = { showSend = false })
}

private fun cacheCard(context: Context, bitmap: Bitmap): Uri? = runCatching {
    val dir = File(context.cacheDir, "shares").apply { mkdirs() }
    val file = File(dir, "fusion-card.png")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}.getOrNull()
