package `in`.paperboxd.app.ui.screens.wrapped

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.paperboxd.app.domain.model.Wrapped
import `in`.paperboxd.app.ui.UiState
import `in`.paperboxd.app.ui.components.rememberGallerySaver
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import java.io.File
import java.io.FileOutputStream

// Monthly Wrapped — a fourteen-chapter story player. Twin of iOS WrappedView.swift.
// The design prototype previewed the story inside phone mockups; here the story
// *is* the screen.

private const val CHAPTER_MILLIS = 6000f
private const val TICK_MILLIS = 60L

@Composable
fun WrappedRoute(
    onClose: () -> Unit,
    viewModel: WrappedViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier.fillMaxSize().background(PBW.Ink)) {
        when (val s = state) {
            is UiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("SETTING YOUR MONTH…", fontFamily = PBW.Mono, fontSize = 10.sp,
                    letterSpacing = 1.6.sp, color = PBW.Muted)
            }
            is UiState.Error -> WrappedEmptyState(s.message, onClose)
            is UiState.Success ->
                if (s.data.hasData) WrappedStoryPlayer(s.data, onClose)
                else WrappedEmptyState(
                    "Nothing logged in ${s.data.month}. Read a few pages and your Wrapped writes itself.",
                    onClose
                )
        }
    }
}

@Composable
private fun WrappedStoryPlayer(w: Wrapped, onClose: () -> Unit) {
    val chapters = remember(w) { wrappedChapters(w) }
    var index by remember { mutableIntStateOf(0) }
    var progress by remember { mutableFloatStateOf(0f) }
    var paused by remember { mutableStateOf(false) }
    var finished by remember { mutableStateOf(false) }
    var shareOpen by remember { mutableStateOf(false) }

    val chapter = chapters[index.coerceIn(0, chapters.lastIndex)]
    val foreground = if (chapter.isLight) PBW.Ink else PBW.Cream
    val insets = WindowInsets.safeDrawing.asPaddingValues()

    fun go(direction: Int) {
        val next = index + direction
        when {
            next < 0 -> progress = 0f
            // The story holds on its last page rather than snapping back.
            next > chapters.lastIndex -> { finished = true; progress = 1f }
            else -> { index = next; progress = 0f }
        }
    }

    BackHandler(onBack = onClose)

    LaunchedEffect(index, paused, shareOpen, finished) {
        if (paused || shareOpen || finished) return@LaunchedEffect
        while (progress < 1f) {
            delay(TICK_MILLIS)
            progress += TICK_MILLIS / CHAPTER_MILLIS
        }
        go(1)
    }

    BoxWithConstraints(Modifier.fillMaxSize().background(PBW.Ink)) {
        // Chapters are drawn on the 402dp design grid and uniformly scaled to the
        // device; the chrome around them stays at true size so tap targets and
        // window insets are not scaled with it.
        val scale = maxWidth / PBW.DesignWidth
        val designHeight = maxHeight / scale

        Box(
            Modifier
                .requiredWidth(PBW.DesignWidth)
                .requiredHeight(designHeight)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    transformOrigin = TransformOrigin(0f, 0f)
                }
                .clipToBounds()
        ) {
            // A new chapter re-runs its own typesetting.
            key(chapter.id) { chapter.content(w) }
        }

        // Tap zones: left third goes back, the rest goes forward, a press and
        // hold pauses until release.
        val widthPx = constraints.maxWidth.toFloat()
        Box(
            Modifier
                .fillMaxSize()
                .pointerInput(chapters, finished) {
                    detectTapGestures(
                        onPress = { offset ->
                            val quickTap = withTimeoutOrNull(220L) { tryAwaitRelease() }
                            if (quickTap == null) {
                                paused = true
                                tryAwaitRelease()
                                paused = false
                            } else if (finished) {
                                finished = false
                                index = 0
                                progress = 0f
                            } else {
                                go(if (offset.x < widthPx * 0.32f) -1 else 1)
                            }
                        }
                    )
                }
        )

        Column(Modifier.fillMaxSize().padding(insets)) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                chapters.forEachIndexed { i, _ ->
                    Box(
                        Modifier.weight(1f).height(2.5.dp).background(foreground.copy(alpha = 0.26f))
                    ) {
                        val fill = when {
                            i < index -> 1f
                            i == index -> progress.coerceIn(0f, 1f)
                            else -> 0f
                        }
                        if (fill > 0f) {
                            Box(Modifier.fillMaxWidth(fill).fillMaxHeight().background(foreground))
                        }
                    }
                }
            }

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${w.monthShort} ${w.year} WRAPPED", fontFamily = PBW.Mono,
                    fontSize = 9.sp, letterSpacing = 1.4.sp, color = foreground)
                Spacer(Modifier.weight(1f))
                Text("%02d/%d".format(index + 1, chapters.size), fontFamily = PBW.Mono,
                    fontSize = 9.sp, letterSpacing = 1.4.sp, color = foreground.copy(alpha = 0.7f))
                Icon(
                    Icons.Outlined.Close,
                    contentDescription = "Close Wrapped",
                    tint = foreground,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(18.dp)
                        .pointerInput(Unit) { detectTapGestures { onClose() } }
                )
            }

            Spacer(Modifier.weight(1f))

            if (paused && !shareOpen) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    PbPauseBars(foreground)
                }
                Spacer(Modifier.weight(1f))
            }

            Box(Modifier.fillMaxWidth().padding(bottom = 22.dp), contentAlignment = Alignment.Center) {
                Row(
                    Modifier
                        .background(
                            if (chapter.isLight) PBW.Ink.copy(alpha = 0.07f) else PBW.Cream.copy(alpha = 0.1f),
                            CircleShape
                        )
                        .border(
                            1.dp,
                            if (chapter.isLight) PBW.Ink.copy(alpha = 0.4f) else PBW.Cream.copy(alpha = 0.34f),
                            CircleShape
                        )
                        .pointerInput(Unit) { detectTapGestures { shareOpen = true } }
                        .padding(horizontal = 20.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Icon(Icons.Outlined.IosShare, contentDescription = null,
                        tint = foreground, modifier = Modifier.size(14.dp))
                    Text("SHARE THIS", fontFamily = PBW.Mono, fontSize = 10.sp,
                        letterSpacing = 1.4.sp, color = foreground)
                }
            }
        }
    }

    if (shareOpen) {
        WrappedShareSheet(w) { shareOpen = false }
    }
}

// MARK: - Share sheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WrappedShareSheet(w: Wrapped, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardLayer = rememberGraphicsLayer()
    var toast by remember { mutableStateOf<String?>(null) }
    val saveCard = rememberGallerySaver { toast = it }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2000)
            toast = null
        }
    }

    suspend fun render(): Bitmap = cardLayer.toImageBitmap().asAndroidBitmap()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PBW.InkDeep,
        dragHandle = null
    ) {
        Column(
            Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(width = 40.dp, height = 3.5.dp).background(PBW.Cream.copy(alpha = 0.3f)))

            // Recorded at full card size and only *drawn* scaled, so the export
            // is a 1080-wide asset rather than a screenshot of the preview.
            Box(
                Modifier
                    .padding(top = 18.dp)
                    .graphicsLayer {
                        scaleX = 230f / 360f
                        scaleY = 230f / 360f
                        transformOrigin = TransformOrigin(0.5f, 0f)
                    }
                    .drawWithContent {
                        cardLayer.record { this@drawWithContent.drawContent() }
                        drawLayer(cardLayer)
                    }
            ) {
                WrappedStill {
                    WrappedRecapCard(w, width = 360.dp)
                }
            }

            Text(
                "9:16 STORY",
                fontFamily = PBW.Mono, fontSize = 9.sp, letterSpacing = 1.sp, color = PBW.Cream,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .border(1.dp, PBW.Cream)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            )

            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShareTile(Icons.Outlined.IosShare, "SHARE", Modifier.weight(1f), accent = true) {
                    scope.launch {
                        val uri = cacheWrappedCard(context, render())
                        if (uri != null) {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "My ${w.month} ${w.year} on PaperBoxd"
                                )
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(send, "Share"))
                            onDismiss()
                        } else toast = "Couldn’t prepare image"
                    }
                }
                ShareTile(Icons.Outlined.Download, "SAVE", Modifier.weight(1f)) {
                    scope.launch { saveCard(render()) }
                }
                ShareTile(Icons.Outlined.Link, "COPY", Modifier.weight(1f)) {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("PaperBoxd", "https://paperboxd.in"))
                    toast = "Link copied"
                }
            }

            toast?.let {
                Text(
                    it,
                    fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.sp,
                    color = PBW.Muted, modifier = Modifier.padding(top = 14.dp)
                )
            }
        }
    }
}

@Composable
private fun ShareTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false,
    onClick: () -> Unit
) {
    val tint = if (accent) PBW.Terra else PBW.Cream
    Column(
        modifier
            .border(1.dp, if (accent) PBW.Terra else PBW.Cream.copy(alpha = 0.24f))
            .pointerInput(Unit) { detectTapGestures { onClick() } }
            .padding(vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(17.dp))
        Text(label, fontFamily = PBW.Mono, fontSize = 9.sp, letterSpacing = 1.sp, color = tint)
    }
}

/** Writes the card to cache and returns a FileProvider content:// URI for sharing. */
private fun cacheWrappedCard(context: Context, bitmap: Bitmap): Uri? = runCatching {
    val dir = File(context.cacheDir, "shares").apply { mkdirs() }
    val file = File(dir, "wrapped-card.png")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    androidx.core.content.FileProvider.getUriForFile(
        context, "${context.packageName}.fileprovider", file
    )
}.getOrNull()

// MARK: - Empty / error state

@Composable
private fun WrappedEmptyState(message: String, onClose: () -> Unit) {
    Column(
        Modifier.fillMaxSize().padding(30.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("PaperBoxd", fontFamily = PBW.Script, fontSize = 34.sp, color = PBW.Cream)
        Text("MONTHLY WRAPPED", fontFamily = PBW.Mono, fontSize = 10.sp,
            letterSpacing = 1.8.sp, color = PBW.Muted, modifier = Modifier.padding(top = 10.dp))
        Text(
            message,
            fontFamily = PBW.Display, fontStyle = FontStyle.Italic, fontWeight = FontWeight.Normal,
            fontSize = 21.sp, lineHeight = 30.sp, color = PBW.Cream,
            modifier = Modifier.padding(top = 18.dp)
        )
        Text(
            "CLOSE",
            fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.4.sp,
            color = PBW.Terra, textAlign = TextAlign.Start,
            modifier = Modifier
                .padding(top = 24.dp)
                .pointerInput(Unit) { detectTapGestures { onClose() } }
        )
    }
}
