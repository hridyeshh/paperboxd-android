package `in`.paperboxd.app.ui.screens.profile

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MailOutline
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import `in`.paperboxd.app.ui.components.rememberGallerySaver
import `in`.paperboxd.app.ui.theme.PBScript
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Fixed warm-light palette so the QR card always scans — iOS ShareProfileSheet twin.
private val Ink = Color(0xFF1C1A14)
private val QrBg = Color(0xFFFDFBF6)

/**
 * Brutalist "Share Profile" sheet — hard-edged offset-shadow QR card with the
 * PaperBoxd mark punched into the centre, plus Share / Copy tiles. Mirrors
 * "Share Profile - Brutalist Mobile.html" + iOS ShareProfileSheet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareProfileSheet(
    username: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cardLayer = rememberGraphicsLayer()
    val profileUrl = "https://paperboxd.in/u/$username"
    var toast by remember { mutableStateOf<String?>(null) }
    val saveCard = rememberGallerySaver { toast = it }

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2000)
            toast = null
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFFCFC7B5), Color(0xFFA89E8A))
                    )
                )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = 14.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // top rail
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Ink.copy(alpha = 0.10f))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = Ink,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.weight(1f))
                    Text(
                        "YOUR CODE",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        letterSpacing = 3.sp,
                        color = Ink,
                        modifier = Modifier
                            .border(1.5.dp, Ink.copy(alpha = 0.34f), CircleShape)
                            .padding(horizontal = 18.dp, vertical = 7.dp)
                    )
                    Spacer(Modifier.weight(1f))
                    Spacer(Modifier.size(44.dp))
                }

                // QR card — recorded into `cardLayer` as it draws so Download can
                // rasterize exactly what is on screen (iOS uses ImageRenderer here).
                QrCard(
                    username = username,
                    profileUrl = profileUrl,
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .drawWithContent {
                            cardLayer.record { this@drawWithContent.drawContent() }
                            drawLayer(cardLayer)
                        }
                )

                // action tiles
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(top = 22.dp),
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    ActionTile(Icons.Outlined.IosShare, "Share profile", Modifier.weight(1f)) {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, profileUrl)
                        }
                        context.startActivity(Intent.createChooser(send, "Share profile"))
                    }
                    ActionTile(Icons.Outlined.Link, "Copy link", Modifier.weight(1f)) {
                        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        cm.setPrimaryClip(ClipData.newPlainText("PaperBoxd profile", profileUrl))
                        toast = "Link copied"
                    }
                    ActionTile(Icons.Outlined.Download, "Download", Modifier.weight(1f)) {
                        scope.launch {
                            val card = renderCard(cardLayer)
                            if (card == null) toast = "Couldn’t render card" else saveCard(card)
                        }
                    }
                }

                toast?.let {
                    Text(
                        it,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = QrBg,
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .clip(CircleShape)
                            .background(Ink.copy(alpha = 0.9f))
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QrCard(username: String, profileUrl: String, modifier: Modifier = Modifier) {
    val qr = remember(profileUrl) { qrBitmap(profileUrl) }

    Column(
        modifier = modifier
            .width(290.dp)
            .padding(end = 7.dp, bottom = 7.dp)
            .drawBehind {
                drawRoundRect(
                    Ink,
                    topLeft = Offset(7.dp.toPx(), 7.dp.toPx()),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
            .clip(RoundedCornerShape(4.dp))
            .background(QrBg)
            .border(1.5.dp, Ink, RoundedCornerShape(4.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("PaperBoxd", fontFamily = PBScript, fontSize = 24.sp, color = Ink)
            Spacer(Modifier.weight(1f))
            Text(
                "SCAN TO FOLLOW",
                fontFamily = FontFamily.Monospace,
                fontSize = 8.sp,
                letterSpacing = 2.sp,
                color = Ink.copy(alpha = 0.55f)
            )
        }

        Box(
            modifier = Modifier.fillMaxWidth().padding(top = 16.dp).aspectRatio(1f),
            contentAlignment = Alignment.Center
        ) {
            if (qr != null) {
                Image(
                    bitmap = qr.asImageBitmap(),
                    contentDescription = "Profile QR code",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Box(Modifier.fillMaxSize().background(Ink.copy(alpha = 0.06f)))
            }
            // PaperBoxd mark punched into the code's centre clear zone
            Box(
                modifier = Modifier
                    .fillMaxSize(0.28f)
                    .clip(RoundedCornerShape(6.dp))
                    .background(QrBg),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize(0.76f)
                        .border(2.4.dp, Ink, RoundedCornerShape(5.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        Modifier
                            .width(2.2.dp)
                            .fillMaxSize(0.68f)
                            .background(Ink)
                    )
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            modifier = Modifier.padding(top = 18.dp)
        ) {
            Icon(Icons.Outlined.MailOutline, contentDescription = null, tint = Ink, modifier = Modifier.size(16.dp))
            Text(
                "@$username",
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-0.4).sp,
                color = Ink
            )
        }
        Text(
            "paperboxd.in/u/$username".uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 8.5.sp,
            letterSpacing = 1.6.sp,
            color = Ink.copy(alpha = 0.5f),
            modifier = Modifier.padding(top = 5.dp)
        )
    }
}

@Composable
private fun ActionTile(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(QrBg)
            .border(1.dp, Ink.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Ink, modifier = Modifier.size(20.dp))
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
    }
}

/**
 * Rasterizes the recorded card for saving. The layer is transparent outside the card's
 * rounded corners and offset shadow, so it is flattened onto the card's paper colour —
 * gallery apps render a transparent PNG on black, which would swallow the ink artwork.
 */
private suspend fun renderCard(layer: GraphicsLayer): Bitmap? = runCatching {
    val raw = layer.toImageBitmap().asAndroidBitmap()
    // A HARDWARE-config bitmap cannot be drawn into a software Canvas.
    val card = if (raw.config == Bitmap.Config.HARDWARE) {
        raw.copy(Bitmap.Config.ARGB_8888, false)
    } else raw
    Bitmap.createBitmap(card.width, card.height, Bitmap.Config.ARGB_8888).also { out ->
        android.graphics.Canvas(out).apply {
            drawColor(QrBg.toArgb())
            drawBitmap(card, 0f, 0f, null)
        }
    }
}.getOrNull()

/** Ink-on-paper QR bitmap via zxing, error correction H (matches iOS). */
private fun qrBitmap(content: String, size: Int = 512): Bitmap? = runCatching {
    val matrix = QRCodeWriter().encode(
        content,
        BarcodeFormat.QR_CODE,
        size,
        size,
        mapOf(EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H, EncodeHintType.MARGIN to 1)
    )
    val ink = android.graphics.Color.argb(255, 28, 26, 20)
    val paper = android.graphics.Color.argb(255, 253, 251, 246)
    val pixels = IntArray(size * size) { i ->
        if (matrix.get(i % size, i / size)) ink else paper
    }
    Bitmap.createBitmap(pixels, size, size, Bitmap.Config.ARGB_8888)
}.getOrNull()
