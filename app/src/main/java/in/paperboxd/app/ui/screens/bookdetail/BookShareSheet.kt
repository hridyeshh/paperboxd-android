package `in`.paperboxd.app.ui.screens.bookdetail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.StarOutline
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.theme.PBScript
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

/**
 * Card canvas, in iOS points mapped 1:1 to dp — mirrors iOS BookShareCardView.Format
 * (540×960 story, 540×540 square). The card is always composed at this base size and
 * scaled to fit the preview, so the raster ships at full resolution and every inset
 * below can be copied straight from the Swift.
 */
private enum class ShareFormat(val label: String, val baseW: Dp, val baseH: Dp) {
    Story("STORY", 540.dp, 960.dp),
    Square("SQUARE", 540.dp, 540.dp)
}

/** Reading status pill on the card — iOS ShareStatus twin. */
enum class ShareStatus(val label: String) {
    Finished("Just finished"),
    Reading("Now reading"),
    Want("Want to read"),
    Favourite("Favourite of 2026")
}

// Card palette — fixed values from iOS BookShareCardView (rendered light-theme).
private val Cream = Color(0xFFF2EDE1)
private val PaperTop = Color(0xFFF2EEE7)
private val PaperBottom = Color(0xFFD9C9AD)
private val CardInk = Color(0xFF1A140D)

/**
 * Brutalist "Share book" sheet — iOS BookShareSheet twin. Renders a STORY/SQUARE
 * card (cover + title + rating + handle) and routes it to the system share sheet,
 * the photo gallery, or the clipboard. The visible card is rasterized on demand
 * via a GraphicsLayer, so what the user sees is exactly what ships.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookShareSheet(
    book: Book,
    handle: String?,
    rating: Int?,
    note: String?,
    status: ShareStatus?,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val graphicsLayer = rememberGraphicsLayer()

    var format by remember { mutableStateOf(ShareFormat.Story) }
    var toast by remember { mutableStateOf<String?>(null) }

    val bookUrl = "https://paperboxd.in/b/${book.slug ?: book.id}"

    LaunchedEffect(toast) {
        if (toast != null) { delay(2000); toast = null }
    }

    suspend fun render(): Bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = HL.Paper,
        dragHandle = null
    ) {
        Column(
            Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 4.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SHARE",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 17.sp,
                    letterSpacing = 2.sp,
                    color = HL.Ink
                )
                Spacer(Modifier.weight(1f))
                Box(
                    Modifier
                        .size(34.dp)
                        .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 2.dp, shadow = Color.Transparent)
                        .clickable(onClick = onDismiss),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Close, "Close", tint = HL.Ink, modifier = Modifier.size(15.dp))
                }
            }

            // Preview — the card is always laid out at its full base size (540×960 /
            // 540×540) and only *drawn* scaled, so `graphicsLayer.record` captures the
            // full-resolution card rather than the shrunken preview. The outer
            // graphicsLayer sits before drawWithContent in the chain, so the recorded
            // layer is the unscaled content.
            BoxWithConstraints(
                Modifier.fillMaxWidth().height(360.dp).padding(horizontal = 24.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val fit = minOf(maxWidth / format.baseW, maxHeight / format.baseH)
                Box(Modifier.fillMaxSize().clipToBounds(), contentAlignment = Alignment.Center) {
                    ShareCard(
                        book = book,
                        handle = handle,
                        rating = rating,
                        note = note,
                        statusLabel = status?.label.orEmpty(),
                        format = format,
                        modifier = Modifier
                            .graphicsLayer { scaleX = fit; scaleY = fit }
                            .drawWithContent {
                                graphicsLayer.record { this@drawWithContent.drawContent() }
                                drawLayer(graphicsLayer)
                            }
                    )
                }
            }

            // Format tabs
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ShareFormat.entries.forEach { f ->
                    val on = format == f
                    Box(
                        Modifier
                            .weight(1f)
                            .brutalPlate(
                                fill = if (on) HL.Ink else HL.Card,
                                borderWidth = 1.5.dp, offset = 2.dp, shadow = Color.Transparent
                            )
                            .clickable { format = f }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            f.label,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.5.sp,
                            color = if (on) HL.Paper else HL.Ink
                        )
                    }
                }
            }

            // Options
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 24.dp).padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OptionTile(Icons.Outlined.IosShare, "MORE", Modifier.weight(1f)) {
                    scope.launch {
                        val uri = cacheShareImage(context, render())
                        if (uri != null) {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                putExtra(Intent.EXTRA_TEXT, bookUrl)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(send, "Share"))
                        } else toast = "Couldn’t prepare image"
                    }
                }
                OptionTile(Icons.Outlined.Download, "SAVE", Modifier.weight(1f)) {
                    scope.launch {
                        toast = if (saveToGallery(context, render())) "Saved to Photos" else "Couldn’t save image"
                    }
                }
                OptionTile(Icons.Outlined.Link, "COPY", Modifier.weight(1f)) {
                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    cm.setPrimaryClip(ClipData.newPlainText("PaperBoxd book", bookUrl))
                    toast = "Link copied"
                }
            }

            toast?.let {
                Text(
                    it,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = HL.Paper,
                    modifier = Modifier
                        .padding(top = 18.dp)
                        .clip(RoundedCornerShape(50))
                        .background(HL.Ink.copy(alpha = 0.92f))
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }
    }
}

/**
 * The shareable card — iOS BookShareCardView (light theme) twin: a warm paper
 * gradient card floating on a plain white canvas, with the script wordmark and a
 * status pill up top, the cover, a star row, a big serif title, author · year, a
 * hairline, the italic note, and the @handle pill in the footer.
 *
 * Composed at [ShareFormat.baseW] × [ShareFormat.baseH] via requiredSize, so all
 * insets are the Swift's point values verbatim; the caller scales it for display.
 */
@Composable
private fun ShareCard(
    book: Book,
    handle: String?,
    rating: Int?,
    note: String?,
    statusLabel: String,
    format: ShareFormat,
    modifier: Modifier = Modifier
) {
    val story = format == ShareFormat.Story
    Box(
        modifier
            .requiredSize(format.baseW, format.baseH)
            .background(Color.White)
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (story) 76.dp else 80.dp,
                    vertical = if (story) 210.dp else 80.dp
                )
                .shadow(18.dp, RoundedCornerShape(40.dp))
                .clip(RoundedCornerShape(40.dp))
                .background(Brush.verticalGradient(listOf(PaperTop, PaperBottom)))
                .padding(if (story) 36.dp else 28.dp)
        ) {
            Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.Start) {
                CardTopBar(statusLabel)
                Spacer(Modifier.weight(1f))
                if (story) {
                    CardCover(book, 176.dp, Modifier.align(Alignment.CenterHorizontally))
                    Spacer(Modifier.weight(1f))
                    CardDetails(book, rating, note)
                } else {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(22.dp)
                    ) {
                        CardCover(book, 150.dp)
                        CardDetails(book, rating, note, Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.weight(1f))
                CardFooter(handle)
            }
        }
    }
}

@Composable
private fun CardTopBar(statusLabel: String) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("PaperBoxd", fontFamily = PBScript, fontSize = 26.sp, color = CardInk)
        Spacer(Modifier.weight(1f))
        if (statusLabel.isNotEmpty()) {
            Row(
                Modifier
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.88f))
                    .padding(horizontal = 12.dp, vertical = 7.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(5.dp).clip(CircleShape).background(Cream))
                Text(
                    statusLabel.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 1.2.sp,
                    color = Cream
                )
            }
        }
    }
}

@Composable
private fun CardCover(book: Book, width: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(width, width * 1.5f)
            .shadow(14.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
    ) {
        val secure = book.coverUrl?.replace("http://", "https://")
        if (!secure.isNullOrEmpty()) {
            SubcomposeAsyncImage(
                model = secure,
                contentDescription = book.title,
                contentScale = ContentScale.Crop,
                loading = { CoverFallback(book.title) },
                error = { CoverFallback(book.title) },
                modifier = Modifier.fillMaxSize()
            )
        } else CoverFallback(book.title)
    }
}

@Composable
private fun CardDetails(book: Book, rating: Int?, note: String?, modifier: Modifier = Modifier) {
    val inkSoft = CardInk.copy(alpha = 0.62f)
    Column(modifier, horizontalAlignment = Alignment.Start) {
        if ((rating ?: 0) > 0) {
            Row(
                Modifier.padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(5) { i ->
                    Icon(
                        if (i < rating!!) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        null,
                        tint = if (i < rating) CardInk else CardInk.copy(alpha = 0.3f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        Text(
            book.title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Black,
            fontSize = 34.sp,
            lineHeight = 38.sp,
            color = CardInk,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            book.publishedYear?.takeIf { it.isNotEmpty() }
                ?.let { "${book.authorLine} · $it" } ?: book.authorLine,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = inkSoft,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 8.dp)
        )
        if (!note.isNullOrBlank()) {
            Box(
                Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(CardInk.copy(alpha = 0.18f))
            )
            Text(
                "“${note.trim()}”",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp,
                color = inkSoft,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun CardFooter(handle: String?) {
    if (handle.isNullOrBlank()) return
    Text(
        if (handle.startsWith("@")) handle else "@$handle",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        color = Cream,
        modifier = Modifier
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.9f))
            .padding(horizontal = 20.dp, vertical = 14.dp)
    )
}

@Composable
private fun CoverFallback(title: String) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFA8875E), Color(0xFF735738)))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = Cream,
            textAlign = TextAlign.Center,
            maxLines = 4,
            modifier = Modifier.padding(14.dp)
        )
    }
}

@Composable
private fun OptionTile(icon: ImageVector, label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier
            .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 3.dp, shadow = Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(icon, null, tint = HL.Ink, modifier = Modifier.size(20.dp))
        Text(
            label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            letterSpacing = 0.8.sp,
            color = HL.Muted
        )
    }
}

/** Writes the card to cache and returns a FileProvider content:// URI for sharing. */
private fun cacheShareImage(context: Context, bitmap: Bitmap): Uri? = runCatching {
    val dir = File(context.cacheDir, "shares").apply { mkdirs() }
    val file = File(dir, "book-share.png")
    FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    androidx.core.content.FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}.getOrNull()

/** Saves the card into the device gallery (Pictures/PaperBoxd) via MediaStore. */
private fun saveToGallery(context: Context, bitmap: Bitmap): Boolean = runCatching {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "paperboxd-${System.currentTimeMillis()}.png")
        put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/PaperBoxd")
        }
    }
    val resolver = context.contentResolver
    val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return false
    resolver.openOutputStream(uri)?.use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    true
}.getOrDefault(false)
