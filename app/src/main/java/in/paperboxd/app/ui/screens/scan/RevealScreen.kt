package `in`.paperboxd.app.ui.screens.scan

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOut
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * 03 — Reveal · THE HINGE — iOS `RevealScreen` twin. Brutalism resolving into
 * minimalism: a hairline header, a soft rounded score card, a calm count-up,
 * a serif verdict, whitespace opening up.
 */
@Composable
fun RevealScreen(
    result: ScanResult,
    onBreakdown: () -> Unit,
    onClose: () -> Unit
) {
    val ringProgress = remember { Animatable(0f) }
    var coverIn by remember { mutableStateOf(false) }
    var verdictIn by remember { mutableStateOf(false) }

    /** Score-band colour for the arc: green (strong), orange (mixed), red (weak). */
    val arcColor = when (result.matchScore) {
        in 71..100 -> Color(0xFF34A853)
        in 40..70 -> Color(0xFFF29900)
        else -> Color(0xCCEA4335)
    }

    LaunchedEffect(Unit) {
        coverIn = true
        delay(300)
        ringProgress.animateTo(result.matchScore / 100f, tween(1400, easing = EaseOut))
    }
    LaunchedEffect(Unit) {
        delay(1100)
        verdictIn = true
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(SK.bgSofter)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    drawRect(
                        SK.border,
                        topLeft = Offset(0f, size.height - 1.dp.toPx()),
                        size = Size(size.width, 1.dp.toPx())
                    )
                }
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
        ) {
            MonoLabel(text = "Match score", size = 10.5f, tracking = 1.8f, color = SK.sub)
            Spacer(Modifier.weight(1f))
            MonoLabel(text = "for you", size = 10f, tracking = 1.2f, color = SK.faint)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(28.dp).clickable(onClick = onClose)
            ) {
                Icon(
                    Icons.Outlined.Close, contentDescription = "Close",
                    tint = SK.sub, modifier = Modifier.size(15.dp)
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                Modifier
                    .alpha(if (coverIn) 1f else 0f)
                    .offset(y = if (coverIn) 0.dp else 14.dp)
                    .padding(bottom = 28.dp)
            ) {
                ScanCover(result, width = 74.dp)
            }

            // Score card: ring + count-up, soft rounded frame with slight offset —
            // the last trace of the hard shadows.
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .padding(end = 3.dp, bottom = 3.dp)
                    .drawBehind {
                        val off = 3.dp.toPx()
                        drawRoundRect(
                            Color.White,
                            topLeft = Offset(off, off),
                            size = size,
                            cornerRadius = CornerRadius(12.dp.toPx())
                        )
                    }
                    .border(1.5.dp, SK.ink, RoundedCornerShape(12.dp))
                    .padding(20.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(168.dp)) {
                    Canvas(Modifier.fillMaxSize()) {
                        val stroke = 8.dp.toPx()
                        val inset = stroke / 2
                        val arcSize = Size(size.width - stroke, size.height - stroke)
                        drawArc(
                            SK.track, 0f, 360f, useCenter = false,
                            topLeft = Offset(inset, inset), size = arcSize,
                            style = Stroke(stroke)
                        )
                        drawArc(
                            arcColor, -90f, 360f * ringProgress.value, useCenter = false,
                            topLeft = Offset(inset, inset), size = arcSize,
                            style = Stroke(stroke, cap = StrokeCap.Round)
                        )
                    }
                    Row {
                        CountUpText(
                            target = result.matchScore, durationMs = 1400,
                            fontSize = 60f, color = SK.ink,
                            fontWeight = FontWeight.Medium, grouping = false
                        )
                        Text(
                            "/100",
                            fontFamily = FontFamily.Monospace, fontSize = 14.sp,
                            color = SK.sub, modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }

            Text(
                result.verdict,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 27.sp,
                color = SK.ink,
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier
                    .padding(horizontal = 28.dp)
                    .padding(top = 26.dp)
                    .alpha(if (verdictIn) 1f else 0f)
            )
            Text(
                result.verdictSub,
                fontSize = 13.sp,
                color = SK.sub,
                textAlign = TextAlign.Center,
                lineHeight = 19.sp,
                modifier = Modifier
                    .padding(horizontal = 36.dp)
                    .padding(top = 11.dp)
                    .alpha(if (verdictIn) 1f else 0f)
            )

            // AI-content disclosure: the score/verdict come from an LLM. Backs the
            // ToS AI-generated-content clause. iOS RevealScreen twin.
            Box(
                Modifier
                    .padding(top = 14.dp)
                    .alpha(if (verdictIn) 1f else 0f)
            ) {
                MonoLabel(text = "AI-GENERATED · MAY BE INACCURATE", size = 9f, tracking = 1.5f, color = SK.faint)
            }
        }

        Spacer(Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.navigationBarsPadding().alpha(if (verdictIn) 1f else 0f)
        ) {
            BruButton(
                title = "See the breakdown",
                trailing = Icons.Outlined.KeyboardArrowDown,
                onClick = onBreakdown,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 12.dp)
            )
            ScansRemainingFooter(Modifier.padding(bottom = 20.dp))
        }
    }
}
