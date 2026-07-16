package `in`.paperboxd.app.ui.screens.scan

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/** Add-to-TBR button state — iOS `TBRState` twin. */
enum class TbrState { Idle, Loading, Added, Failed }

/**
 * 04 — Breakdown · MINIMALIST resolution — iOS `BreakdownScreen` twin. Everything
 * exhales: no boxes, no shadows — whitespace, hairline splits, a serif headline,
 * a thin radar, and plain-English reasons.
 */
@Composable
fun BreakdownScreen(
    result: ScanResult,
    tbrState: TbrState,
    toast: String?,
    onAddToTbr: () -> Unit,
    onToastShown: () -> Unit,
    onClose: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(toast) {
        if (toast != null) {
            delay(2500)
            onToastShown()
        }
    }

    Box(Modifier.fillMaxSize().background(SK.bgSofter)) {
        Column(Modifier.fillMaxSize().statusBarsPadding()) {
            Column(
                verticalArrangement = Arrangement.spacedBy(34.dp),
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 26.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                // Header
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    ScanCover(result, width = 62.dp)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            result.title, fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium, fontSize = 21.sp,
                            color = SK.ink, maxLines = 2
                        )
                        Text(result.author, fontSize = 12.5.sp, color = SK.sub)
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "${result.matchScore}",
                            fontFamily = FontFamily.Monospace, fontSize = 34.sp, color = SK.ink
                        )
                        MonoLabel(text = "match", size = 10.5f, tracking = 1f, color = SK.faint)
                    }
                }

                // Internet vs you (hairline split)
                CompareRow(result)

                // Radar
                Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    MonoLabel(
                        text = "How it breaks down",
                        size = 11f, tracking = 0.8f, color = SK.faint
                    )
                    RadarChart(result.dimensions)
                }

                // Why for you
                Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
                    MonoLabel(
                        text = "Why this score, for you",
                        size = 11f, tracking = 0.8f, color = SK.faint
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        result.reasons.forEach { (ok, text) ->
                            Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                                Icon(
                                    if (ok) Icons.Outlined.Check else Icons.Outlined.Close,
                                    contentDescription = null,
                                    tint = if (ok) SK.ink else SK.faint,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text, fontSize = 13.5.sp, lineHeight = 19.sp,
                                    color = if (ok) SK.ink else SK.sub
                                )
                            }
                        }
                    }
                }

                // Sources caption
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Outlined.ChatBubbleOutline, contentDescription = null,
                        tint = SK.faint, modifier = Modifier.size(13.dp)
                    )
                    MonoLabel(
                        text = "reddit · web reviews · your shelf of ${result.shelfCount} books",
                        size = 9.5f, tracking = 0.6f, color = SK.faint
                    )
                }

                ScansRemainingFooter(Modifier.fillMaxWidth())
            }

            // Sticky action bar: Add to TBR (fills), ghost dismiss, share.
            Column(Modifier.fillMaxWidth().background(SK.bgSofter)) {
                Hairline()
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 26.dp)
                        .padding(top = 12.dp, bottom = 8.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(7.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(CircleShape)
                            .background(if (tbrState == TbrState.Added) SK.sub else SK.ink)
                            .clickable(
                                enabled = tbrState == TbrState.Idle || tbrState == TbrState.Failed
                            ) { onAddToTbr() }
                    ) {
                        when (tbrState) {
                            TbrState.Loading -> CircularProgressIndicator(
                                color = SK.bg, strokeWidth = 2.dp, modifier = Modifier.size(18.dp)
                            )
                            TbrState.Added -> {
                                Icon(
                                    Icons.Outlined.Check, contentDescription = null,
                                    tint = SK.bg, modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    "Added to TBR", fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold, color = SK.bg
                                )
                            }
                            else -> {
                                Icon(
                                    Icons.Outlined.Add, contentDescription = null,
                                    tint = SK.bg, modifier = Modifier.size(17.dp)
                                )
                                Text(
                                    "Add to TBR", fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold, color = SK.bg
                                )
                            }
                        }
                    }

                    // "Not for me" — ghost dismiss, no negative signal logged, no API call.
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp, 48.dp)
                            .clip(CircleShape)
                            .border(1.dp, SK.border, CircleShape)
                            .clickable(onClick = onClose)
                    ) {
                        Icon(
                            Icons.Outlined.Close, contentDescription = "Not for me",
                            tint = SK.ink, modifier = Modifier.size(18.dp)
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(50.dp, 48.dp)
                            .clip(CircleShape)
                            .border(1.dp, SK.border, CircleShape)
                            .clickable {
                                val text = "${result.title} by ${result.author} — scored " +
                                    "${result.matchScore}/100 for me on PaperBoxd. ${result.oneLine}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, text)
                                }
                                context.startActivity(Intent.createChooser(intent, null))
                            }
                    ) {
                        Icon(
                            Icons.Outlined.Share, contentDescription = "Share",
                            tint = SK.ink, modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }
        }

        // Close (top-right, floats over the scroll)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(horizontal = 16.dp)
                .padding(top = 6.dp)
                .size(34.dp)
                .clickable(onClick = onClose)
        ) {
            Icon(
                Icons.Outlined.Close, contentDescription = "Close",
                tint = SK.sub, modifier = Modifier.size(15.dp)
            )
        }

        // Toast
        if (toast != null) {
            Text(
                toast,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = SK.bg,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 84.dp)
                    .clip(CircleShape)
                    .background(SK.ink)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            )
        }
    }
}

// ── Internet vs you (hairline split) ─────────────────────────────────────────

@Composable
private fun CompareRow(result: ScanResult) {
    Column {
        Hairline()
        Row {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f).padding(vertical = 18.dp)
            ) {
                MonoLabel(text = "The internet", size = 11f, tracking = 0.8f, color = SK.faint)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        result.internetRating?.let { "%.2f".format(it) } ?: "—",
                        fontFamily = FontFamily.Monospace, fontSize = 22.sp, color = SK.ink
                    )
                    Text("★ avg", fontSize = 12.sp, color = SK.sub)
                }
                Text("${result.ratingsCount ?: "—"} ratings", fontSize = 11.sp, color = SK.faint)
            }
            Box(
                Modifier
                    .width(1.dp)
                    .height(120.dp)
                    .background(SK.border)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f).padding(vertical = 18.dp).padding(start = 20.dp)
            ) {
                MonoLabel(text = "For you", size = 11f, tracking = 0.8f, color = SK.faint)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        "${result.matchScore}",
                        fontFamily = FontFamily.Monospace, fontSize = 22.sp, color = SK.ink
                    )
                    Text("/100", fontSize = 12.sp, color = SK.sub)
                }
                Text(
                    result.verdict.lowercase(), fontSize = 11.sp,
                    color = SK.faint, maxLines = 1
                )
            }
        }
        Hairline()
    }
}

// ── Radar chart (light brutalist palette) ────────────────────────────────────

@Composable
fun RadarChart(dimensions: List<ScanResult.Dimension>) {
    Box(Modifier.fillMaxWidth().height(240.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = min(size.width, size.height) / 2 * 0.7f
            val n = dimensions.size

            fun angle(i: Int) = (-Math.PI / 2 + 2 * Math.PI * i / n).toFloat()

            // Grid rings
            for (ring in 1..4) {
                val r = radius * ring / 4
                val p = Path()
                for (i in 0 until n) {
                    val a = angle(i)
                    val pt = Offset(center.x + cos(a) * r, center.y + sin(a) * r)
                    if (i == 0) p.moveTo(pt.x, pt.y) else p.lineTo(pt.x, pt.y)
                }
                p.close()
                drawPath(p, SK.border, style = Stroke(1.dp.toPx()))
            }

            // Data polygon
            val data = Path()
            dimensions.forEachIndexed { i, d ->
                val a = angle(i)
                val r = radius * d.value.toFloat()
                val pt = Offset(center.x + cos(a) * r, center.y + sin(a) * r)
                if (i == 0) data.moveTo(pt.x, pt.y) else data.lineTo(pt.x, pt.y)
            }
            data.close()
            drawPath(data, SK.ink.copy(alpha = 0.08f))
            drawPath(data, SK.ink, style = Stroke(2.dp.toPx()))
        }

        // Axis labels around the polygon.
        val n = dimensions.size
        dimensions.forEachIndexed { i, d ->
            val a = -Math.PI / 2 + 2 * Math.PI * i / n
            RadarLabel(
                text = d.name,
                xFrac = (0.5 + cos(a) * 0.42).toFloat(),
                yFrac = (0.5 + sin(a) * 0.40).toFloat()
            )
        }
    }
}

/** Places a small mono label centered at a fractional position of the parent. */
@Composable
private fun RadarLabel(text: String, xFrac: Float, yFrac: Float) {
    Layout(
        content = {
            Text(text, fontFamily = FontFamily.Monospace, fontSize = 9.5.sp, color = SK.sub)
        }
    ) { measurables, constraints ->
        val placeable = measurables.first().measure(constraints.copy(minWidth = 0, minHeight = 0))
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(
                x = (constraints.maxWidth * xFrac - placeable.width / 2).toInt(),
                y = (constraints.maxHeight * yFrac - placeable.height / 2).toInt()
            )
        }
    }
}
