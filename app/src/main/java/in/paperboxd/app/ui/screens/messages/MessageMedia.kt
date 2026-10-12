package `in`.paperboxd.app.ui.screens.messages

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import `in`.paperboxd.app.domain.model.AttachKind
import `in`.paperboxd.app.domain.model.AttachmentRef
import `in`.paperboxd.app.domain.model.GifItem
import `in`.paperboxd.app.domain.model.GifList
import `in`.paperboxd.app.domain.model.MessageAttachment
import `in`.paperboxd.app.ui.components.HL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okio.BufferedSink
import okio.source
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// Photos, videos, GIFs and stickers in DMs. iOS twin: MessageMedia.swift.
// GIFs animate through Coil's GIF decoder (registered in PaperBoxdApp).

/** Fits media into a bubble: 240dp wide at most (stickers 140), 320 tall. */
fun mediaSize(att: MessageAttachment): DpSize {
    val w = (att.int("width") ?: 0).toFloat()
    val h = (att.int("height") ?: 0).toFloat()
    // Unknown or extreme shapes fall back to something sensible.
    val ratio = if (w > 0 && h > 0) (w / h).coerceIn(0.5f, 2.5f) else 1f
    val sticker = att.kind == AttachKind.STICKER
    val maxW = if (sticker) 140f else 240f
    val maxH = if (sticker) 140f else 320f
    var width = maxW
    var height = maxW / ratio
    if (height > maxH) { height = maxH; width = maxH * ratio }
    return DpSize(width.dp, height.dp)
}

fun durationLabel(ms: Int): String {
    val s = ms / 1000
    return "%d:%02d".format(s / 60, s % 60)
}

/** A photo, video, GIF or sticker inside a message, sized from its snapshot. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaAttachment(att: MessageAttachment, onTap: () -> Unit, onLongPress: () -> Unit = {}) {
    val size = mediaSize(att)
    val sticker = att.kind == AttachKind.STICKER
    val shape = RoundedCornerShape(if (sticker) 0.dp else 18.dp)
    Box(
        Modifier
            .size(size)
            .clip(shape)
            .background(if (sticker) Color.Transparent else HL.Paper2)
            .combinedClickable(onClick = onTap, onLongClick = onLongPress),
        contentAlignment = Alignment.Center
    ) {
        val url = if (att.kind == AttachKind.VIDEO) att.str("poster_url") else att.str("url")
        AsyncImage(
            model = url,
            contentDescription = att.kind,
            contentScale = if (sticker) ContentScale.Fit else ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        if (att.kind == AttachKind.VIDEO) {
            Box(
                Modifier.size(52.dp).clip(CircleShape).background(Color.Black.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(28.dp))
            }
            att.int("duration_ms")?.takeIf { it > 0 }?.let { ms ->
                Text(
                    durationLabel(ms),
                    fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = Color.White,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(8.dp)
                        .clip(RoundedCornerShape(50)).background(Color.Black.copy(alpha = 0.5f))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/** Tapping a photo or video opens it edge to edge. */
@Composable
fun MediaViewer(att: MessageAttachment, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            if (att.kind == AttachKind.VIDEO) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setMediaController(MediaController(ctx).also { it.setAnchorView(this) })
                            setOnPreparedListener { start() }
                            setVideoURI(Uri.parse(att.str("url")))
                        }
                    },
                    onRelease = { it.stopPlayback() },
                    modifier = Modifier.align(Alignment.Center).fillMaxWidth()
                )
            } else {
                AsyncImage(
                    model = att.str("url"),
                    contentDescription = "Photo",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                Modifier.statusBarsPadding().padding(16.dp).size(40.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f)).clickable(onClick = onDismiss)
                    .align(Alignment.TopEnd),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Close, "Close", tint = Color.White)
            }
        }
    }
}

/** GIPHY search. Tapping one sends it straight away. [type] is "gifs" or "stickers". */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GifPickerSheet(
    initialType: String,
    search: suspend (type: String, query: String) -> Result<GifList>,
    onPick: (AttachmentRef) -> Unit,
    onDismiss: () -> Unit
) {
    var type by remember { mutableStateOf(initialType) }
    var query by remember { mutableStateOf("") }
    var items by remember { mutableStateOf<List<GifItem>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Debounced: re-runs when the tab or the query changes.
    LaunchedEffect(type, query) {
        if (query.isNotBlank()) delay(300)
        loading = true
        search(type, query.trim())
            .onSuccess { items = it.items; error = if (it.items.isEmpty()) "Nothing found." else null }
            .onFailure { items = emptyList(); error = it.message ?: "GIFs are not available right now" }
        loading = false
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = HL.Paper
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PickerChip("GIFS", type == "gifs") { type = "gifs" }
                PickerChip("STICKERS", type == "stickers") { type = "stickers" }
            }
            SheetField(query, { query = it }, if (type == "gifs") "Search GIFs" else "Search stickers")
            Box(Modifier.fillMaxWidth().heightIn(min = 200.dp, max = 460.dp)) {
                when {
                    loading && items.isEmpty() -> CircularProgressIndicator(
                        color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(26.dp).align(Alignment.Center)
                    )
                    items.isEmpty() -> Text(error.orEmpty(), fontSize = 14.sp, color = HL.Muted, modifier = Modifier.align(Alignment.Center))
                    else -> LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        items(items, key = { it.id }) { g ->
                            AsyncImage(
                                model = g.previewUrl,
                                contentDescription = if (type == "gifs") "GIF" else "Sticker",
                                contentScale = if (type == "gifs") ContentScale.Crop else ContentScale.Fit,
                                modifier = Modifier.fillMaxWidth().height(104.dp).clip(RoundedCornerShape(8.dp))
                                    .background(if (type == "gifs") HL.Paper2 else Color.Transparent)
                                    .clickable { onPick(AttachmentRef(if (type == "gifs") AttachKind.GIF else AttachKind.STICKER, g.id)) }
                            )
                        }
                    }
                }
            }
            Text(
                "POWERED BY GIPHY", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 9.5.sp,
                letterSpacing = 1.6.sp, color = HL.Muted, modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
private fun PickerChip(label: String, on: Boolean, onClick: () -> Unit) {
    Box(
        Modifier.clip(RoundedCornerShape(50)).background(if (on) HL.Ink else HL.Card).clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(label, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.4.sp,
            color = if (on) HL.Paper else HL.Ink)
    }
}

// ── Preparing picks for upload ───────────────────────────────────────────────

const val MAX_VIDEO_BYTES = 50L * 1024 * 1024

class PreparedMedia(val kind: String, val fileName: String, val body: RequestBody)

/**
 * Photos become a ≤2048px JPEG (HEIC doesn't travel). Videos stream from the
 * picker's Uri as they are — no transcoder on Android — so the cap bites.
 */
suspend fun prepareMedia(context: Context, uri: Uri): PreparedMedia = withContext(Dispatchers.IO) {
    val cr = context.contentResolver
    val type = cr.getType(uri).orEmpty()
    if (type.startsWith("video/")) {
        val size = cr.query(uri, arrayOf(OpenableColumns.SIZE), null, null, null)
            ?.use { c -> if (c.moveToFirst() && !c.isNull(0)) c.getLong(0) else -1L } ?: -1L
        if (size > MAX_VIDEO_BYTES) throw IllegalArgumentException("That video is too big to send (max 50 MB)")
        val mediaType = type.toMediaTypeOrNull()
        val body = object : RequestBody() {
            override fun contentType() = mediaType
            override fun contentLength() = size
            // Re-opened per write, so an OkHttp retry can resend it.
            override fun writeTo(sink: BufferedSink) {
                val input = cr.openInputStream(uri) ?: throw IllegalStateException("Couldn't read that video")
                input.source().use { sink.writeAll(it) }
            }
        }
        PreparedMedia(AttachKind.VIDEO, "video." + (type.substringAfter('/').ifBlank { "mp4" }), body)
    } else {
        val bitmap = decodeScaled(cr, uri, 2048)
        val bytes = ByteArrayOutputStream().also { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }.toByteArray()
        bitmap.recycle()
        PreparedMedia(AttachKind.PHOTO, "photo.jpg", bytes.toRequestBody("image/jpeg".toMediaType()))
    }
}

private fun decodeScaled(cr: ContentResolver, uri: Uri, maxSide: Int): Bitmap =
    if (Build.VERSION.SDK_INT >= 28) {
        // ImageDecoder also applies EXIF rotation and reads HEIC.
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(cr, uri)) { decoder, info, _ ->
            val w = info.size.width
            val h = info.size.height
            val scale = min(1.0, maxSide.toDouble() / max(w, h))
            decoder.setTargetSize((w * scale).roundToInt().coerceAtLeast(1), (h * scale).roundToInt().coerceAtLeast(1))
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        // ponytail: API 26–27 skips EXIF rotation; add androidx.exifinterface if sideways photos show up.
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) sample *= 2
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, BitmapFactory.Options().apply { inSampleSize = sample }) }
            ?: throw IllegalArgumentException("Couldn't read that photo")
    }
