package `in`.paperboxd.app.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.drawable.toBitmap
import coil.imageLoader
import coil.request.ImageRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Fixed-ratio cropper for avatar and banner uploads. iOS twin:
 * Components/ImageCropper.swift — same behaviour, deliberately pan-only: the
 * image fills the crop window at `max` scale and the user drags to reposition
 * within the overflow. No zoom, no third-party dependency.
 *
 * Decoding goes through Coil (already a dependency) rather than
 * BitmapFactory because Coil applies EXIF orientation — a portrait photo from
 * the camera roll decodes sideways otherwise, and the crop would then not
 * match what the user positioned on screen.
 */
@Composable
fun ImageCropper(
    uri: Uri,
    ratio: Float,
    maxDimension: Int = 1024,
    onCancel: () -> Unit,
    onCrop: (ByteArray) -> Unit
) {
    val context = LocalContext.current
    var source by remember(uri) { mutableStateOf<Bitmap?>(null) }
    var failed by remember(uri) { mutableStateOf(false) }

    LaunchedEffect(uri) {
        val bitmap = decodeOriented(context, uri)
        if (bitmap == null) failed = true else source = bitmap
    }

    LaunchedEffect(failed) { if (failed) onCancel() }

    Dialog(
        onDismissRequest = onCancel,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(Modifier.fillMaxSize().background(Color.Black)) {
            val bitmap = source
            if (bitmap == null) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.align(Alignment.Center).size(28.dp)
                )
            } else {
                CropStage(
                    bitmap = bitmap,
                    ratio = ratio,
                    maxDimension = maxDimension,
                    onCancel = onCancel,
                    onCrop = onCrop
                )
            }
        }
    }
}

@Composable
private fun CropStage(
    bitmap: Bitmap,
    ratio: Float,
    maxDimension: Int,
    onCancel: () -> Unit,
    onCrop: (ByteArray) -> Unit
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    var offsetX by remember(bitmap) { mutableStateOf(0f) }
    var offsetY by remember(bitmap) { mutableStateOf(0f) }
    var zoom by remember(bitmap) { mutableStateOf(1f) }
    var rendering by remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val cropW = with(density) { maxWidth.toPx() }
        val cropH = cropW / ratio
        // Fill the window, then pan/zoom inside the overflow. Zoom floors at 1
        // so the image always covers the window, matching UIKit's cropper.
        val baseScale = max(cropW / bitmap.width, cropH / bitmap.height)
        val scale = baseScale * zoom
        val dispW = bitmap.width * scale
        val dispH = bitmap.height * scale
        val maxX = max(0f, (dispW - cropW) / 2f)
        val maxY = max(0f, (dispH - cropH) / 2f)

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(with(density) { cropW.toDp() }, with(density) { cropH.toDp() })
                    .clipToBounds()
                    .border(1.dp, Color.White.copy(alpha = 0.85f))
                    .pointerInput(bitmap, cropW, cropH, baseScale) {
                        detectTransformGestures { _, pan, gestureZoom, _ ->
                            zoom = (zoom * gestureZoom).coerceIn(1f, 5f)
                            // Re-derive the pan limits at the new zoom, else an
                            // offset valid a moment ago can leave a blank edge.
                            val zw = bitmap.width * baseScale * zoom
                            val zh = bitmap.height * baseScale * zoom
                            val limitX = max(0f, (zw - cropW) / 2f)
                            val limitY = max(0f, (zh - cropH) / 2f)
                            offsetX = (offsetX + pan.x).coerceIn(-limitX, limitX)
                            offsetY = (offsetY + pan.y).coerceIn(-limitY, limitY)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .size(with(density) { dispW.toDp() }, with(density) { dispH.toDp() })
                        .graphicsLayer {
                            translationX = offsetX
                            translationY = offsetY
                        }
                )
            }

            Spacer(Modifier.size(16.dp))

            Text(
                "Drag to reposition · pinch to zoom",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 13.sp
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cancel",
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.clickable(enabled = !rendering, onClick = onCancel)
            )
            Spacer(Modifier.weight(1f))
            if (rendering) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    "Use photo",
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable {
                        rendering = true
                        scope.launch {
                            val bytes = withContext(Dispatchers.Default) {
                                renderCrop(
                                    source = bitmap,
                                    scale = scale,
                                    dispW = dispW,
                                    dispH = dispH,
                                    cropW = cropW,
                                    cropH = cropH,
                                    offsetX = offsetX,
                                    offsetY = offsetY,
                                    maxDimension = maxDimension
                                )
                            }
                            rendering = false
                            if (bytes != null) onCrop(bytes) else onCancel()
                        }
                    }
                )
            }
        }
    }
}

/**
 * Maps the on-screen crop window back into source-bitmap pixels and encodes it.
 * Mirrors PageProgress-era iOS `renderCrop` — same four expressions.
 */
private fun renderCrop(
    source: Bitmap,
    scale: Float,
    dispW: Float,
    dispH: Float,
    cropW: Float,
    cropH: Float,
    offsetX: Float,
    offsetY: Float,
    maxDimension: Int
): ByteArray? {
    val left = ((dispW / 2f - offsetX - cropW / 2f) / scale).roundToInt()
    val top = ((dispH / 2f - offsetY - cropH / 2f) / scale).roundToInt()
    val width = (cropW / scale).roundToInt()
    val height = (cropH / scale).roundToInt()

    // Rounding can push the window a pixel past the edge; clamp so
    // createBitmap never throws on an out-of-bounds rect.
    val x = left.coerceIn(0, max(0, source.width - 1))
    val y = top.coerceIn(0, max(0, source.height - 1))
    val w = width.coerceIn(1, source.width - x)
    val h = height.coerceIn(1, source.height - y)

    return runCatching {
        var out = Bitmap.createBitmap(source, x, y, w, h)
        val over = max(out.width, out.height) / maxDimension.toFloat()
        if (over > 1f) {
            out = Bitmap.createScaledBitmap(
                out, (out.width / over).toInt(), (out.height / over).toInt(), true
            )
        }
        ByteArrayOutputStream().use { stream ->
            out.compress(Bitmap.CompressFormat.JPEG, 90, stream)
            stream.toByteArray()
        }
    }.getOrNull()
}

private suspend fun decodeOriented(context: Context, uri: Uri): Bitmap? =
    withContext(Dispatchers.IO) {
        val request = ImageRequest.Builder(context)
            .data(uri)
            .allowHardware(false) // hardware bitmaps can't be read back by createBitmap
            .build()
        runCatching {
            context.imageLoader.execute(request).drawable?.toBitmap()
        }.getOrNull()
    }
