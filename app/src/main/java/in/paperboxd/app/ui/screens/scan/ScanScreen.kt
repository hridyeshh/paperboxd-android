package `in`.paperboxd.app.ui.screens.scan

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Border
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.Terracotta
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * 01 — Scan. Live camera viewfinder with barcode framing — iOS `ScanScreen` twin.
 * When an ISBN barcode is read, a confirmation card slides up. Camera permission
 * is requested on appear; denied / unavailable states show a fallback card.
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    lookupTitle: suspend (String) -> String?,
    search: suspend (String) -> List<ScanSearchHit>,
    onScanIt: (String, String?) -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cameraPermission = rememberPermissionState(Manifest.permission.CAMERA)
    var permissionRequested by rememberSaveable { mutableStateOf(false) }

    var scannedIsbn by remember { mutableStateOf<String?>(null) }
    var status by remember { mutableStateOf(ScannerStatus.Scanning) }
    var torchOn by remember { mutableStateOf(false) }
    var bookTitle by remember { mutableStateOf<String?>(null) }
    var titleLoading by remember { mutableStateOf(false) }
    var showManual by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!cameraPermission.status.isGranted && !permissionRequested) {
            permissionRequested = true
            cameraPermission.launchPermissionRequest()
        }
    }

    fun handleCode(code: String) {
        if (scannedIsbn != null) return
        scannedIsbn = code
        titleLoading = true
        scope.launch {
            bookTitle = runCatching { lookupTitle(code) }.getOrNull()
            titleLoading = false
        }
    }

    val showBrackets = status == ScannerStatus.Scanning && scannedIsbn == null &&
        cameraPermission.status.isGranted

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (cameraPermission.status.isGranted) {
            CameraScannerView(
                torchOn = torchOn,
                onCode = { handleCode(it) },
                onStatus = { status = it },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top + bottom washes for legibility over the live feed.
        Column(Modifier.fillMaxSize()) {
            Box(
                Modifier.fillMaxWidth().height(160.dp).background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.75f), Color.Transparent)
                    )
                )
            )
            Spacer(Modifier.weight(1f))
            Box(
                Modifier.fillMaxWidth().height(280.dp).background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                    )
                )
            )
        }

        // Top bar.
        Box(
            Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 18.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Scan & Know",
                fontFamily = FontFamily.Serif,
                fontSize = 17.sp,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
            CircleIcon(
                Icons.Outlined.Close,
                onClick = onClose,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            CircleIcon(
                if (torchOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                onClick = { torchOn = !torchOn },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }

        if (showBrackets) {
            BracketsLayer(Modifier.align(Alignment.Center))
        }

        // Bottom stack: result / fallback card + manual search.
        Column(
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 18.dp)
                .padding(bottom = 30.dp)
        ) {
            val isbn = scannedIsbn
            when {
                isbn != null -> IsbnCard(
                    isbn = isbn,
                    bookTitle = bookTitle,
                    titleLoading = titleLoading,
                    onScanIt = { onScanIt(isbn, bookTitle) }
                )
                !cameraPermission.status.isGranted || status == ScannerStatus.Denied ->
                    FallbackCard(
                        icon = Icons.Outlined.Lock,
                        title = "Camera access needed",
                        body = "Enable camera access to scan a book's barcode.",
                        showSettings = true,
                        onSettings = {
                            context.startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", context.packageName, null)
                                )
                            )
                        }
                    )
                status == ScannerStatus.Unavailable ->
                    FallbackCard(
                        icon = Icons.Outlined.PhotoCamera,
                        title = "Camera unavailable",
                        body = "This device has no camera — try on your phone."
                    )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { showManual = true }
            ) {
                Icon(
                    Icons.Outlined.Search, contentDescription = null,
                    tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(14.dp)
                )
                Text(
                    "Type the title instead",
                    fontSize = 13.sp,
                    color = Color.White.copy(alpha = 0.7f)
                )
            }
        }
    }

    if (showManual) {
        ManualSearchSheet(
            search = search,
            onDismiss = { showManual = false },
            onPick = { isbn, title ->
                showManual = false
                onScanIt(isbn, title)
            }
        )
    }
}

@Composable
private fun CircleIcon(icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.16f))
            .clickable(onClick = onClick)
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
    }
}

// ── Framing brackets + sweep ─────────────────────────────────────────────────

@Composable
private fun BracketsLayer(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "sweep")
    val sweepY by transition.animateFloat(
        initialValue = -84f,
        targetValue = 84f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sweep-y"
    )
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier) {
        Box(Modifier.size(260.dp, 180.dp)) {
            Canvas(Modifier.fillMaxSize()) {
                val c = 28.dp.toPx()
                val w = size.width
                val h = size.height
                val p = Path().apply {
                    moveTo(0f, c); lineTo(0f, 0f); lineTo(c, 0f)
                    moveTo(w - c, 0f); lineTo(w, 0f); lineTo(w, c)
                    moveTo(w, h - c); lineTo(w, h); lineTo(w - c, h)
                    moveTo(c, h); lineTo(0f, h); lineTo(0f, h - c)
                }
                drawPath(
                    p, Color.White.copy(alpha = 0.9f),
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                )
            }
            Box(
                Modifier
                    .align(Alignment.Center)
                    .offset(y = sweepY.dp)
                    .width(258.dp)
                    .height(2.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                Terracotta.copy(alpha = 0.85f),
                                Color.Transparent
                            )
                        )
                    )
            )
        }
        Text(
            "Line up the barcode on the back cover",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.8f),
            modifier = Modifier.padding(top = 24.dp)
        )
    }
}

// ── ISBN found card ──────────────────────────────────────────────────────────

@Composable
private fun IsbnCard(
    isbn: String,
    bookTitle: String?,
    titleLoading: Boolean,
    onScanIt: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(38.dp, 54.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Border)
        ) {
            Icon(
                Icons.Outlined.QrCodeScanner, contentDescription = null,
                tint = TextSecondary, modifier = Modifier.size(16.dp)
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp), modifier = Modifier.weight(1f)) {
            MonoLabel(
                text = if (bookTitle == null) "ISBN found" else "Book found",
                size = 10f, tracking = 2f, color = TextSecondary
            )
            when {
                bookTitle != null -> {
                    Text(
                        bookTitle, fontFamily = FontFamily.Serif, fontSize = 16.sp,
                        color = TextPrimary, maxLines = 2
                    )
                    Text(
                        isbn, fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                        color = TextSecondary, maxLines = 1
                    )
                }
                titleLoading -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = TextSecondary, strokeWidth = 1.5.dp,
                            modifier = Modifier.size(12.dp)
                        )
                        Text("Finding the book…", fontSize = 13.sp, color = TextSecondary)
                    }
                    Text(
                        isbn, fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                        color = TextSecondary.copy(alpha = 0.7f), maxLines = 1
                    )
                }
                else -> {
                    Text(
                        isbn, fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium, fontSize = 15.sp,
                        color = TextPrimary, maxLines = 1
                    )
                    Text(
                        "Couldn't find the title — scan anyway",
                        fontSize = 11.sp, color = TextSecondary, maxLines = 1
                    )
                }
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(CircleShape)
                .background(TextPrimary)
                .clickable(onClick = onScanIt)
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Text("Scan it", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Background)
            Icon(
                Icons.AutoMirrored.Outlined.ArrowForward, contentDescription = null,
                tint = Background, modifier = Modifier.size(12.dp)
            )
        }
    }
}

// ── Permission / unavailable fallback ────────────────────────────────────────

@Composable
private fun FallbackCard(
    icon: ImageVector,
    title: String,
    body: String,
    showSettings: Boolean = false,
    onSettings: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .border(1.dp, Border, RoundedCornerShape(14.dp))
            .padding(18.dp)
    ) {
        Icon(icon, contentDescription = null, tint = Accent, modifier = Modifier.size(22.dp))
        Text(title, fontFamily = FontFamily.Serif, fontSize = 16.sp, color = TextPrimary)
        Text(
            body, fontSize = 12.sp, color = TextSecondary,
            textAlign = TextAlign.Center
        )
        if (showSettings) {
            Text(
                "Open Settings",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Background,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Accent)
                    .clickable(onClick = onSettings)
                    .padding(horizontal = 18.dp, vertical = 10.dp)
            )
        }
    }
}
