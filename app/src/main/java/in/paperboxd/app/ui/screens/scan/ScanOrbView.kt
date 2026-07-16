package `in`.paperboxd.app.ui.screens.scan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * The analyze hero — a rotating "knowledge graph". iOS `ScanOrbView` twin.
 * Nodes sit on a spherical shell; connection lines draw in as the analysis
 * cross-references sources, and the live % counts up in the clear core.
 * Monochrome; the cover-green accent marks "books on your shelf".
 */
@Composable
fun ScanOrbView(done: Boolean, size: Dp = 192.dp) {
    val model = remember { OrbModel(132) }
    var elapsed by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        val start = System.nanoTime()
        while (true) {
            withFrameNanos { now -> elapsed = (now - start) / 1_000_000_000f }
        }
    }

    val tilt = 0.46f
    val revealSecs = 6.8f
    val countSecs = 8.4f

    val pct = if (done) 100 else {
        val p = min(1f, elapsed / countSecs)
        ((1 - (1 - p).pow(3)) * 96).toInt()
    }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(size)) {
        Canvas(Modifier.size(size)) {
            val sizePx = this.size.width
            val r = sizePx * 0.40f
            val focal = r * 3.4f
            val cx = sizePx / 2
            val cy = sizePx / 2
            val angY = elapsed * 0.252f
            val cyaw = cos(angY); val syaw = sin(angY)
            val ct = cos(tilt); val st = sin(tilt)
            val reveal = if (done) 1f else min(1f, elapsed / revealSecs)
            val shown = (reveal * model.edges.size).toInt()

            // Project every node.
            val sx = FloatArray(model.nodes.size)
            val sy = FloatArray(model.nodes.size)
            val depth = FloatArray(model.nodes.size)
            val scArr = FloatArray(model.nodes.size)
            val alpha = FloatArray(model.nodes.size)
            model.nodes.forEachIndexed { i, n ->
                val x1 = n.x * cyaw - n.z * syaw
                val z1 = n.x * syaw + n.z * cyaw
                val y2 = n.y * ct - z1 * st
                val z2 = n.y * st + z1 * ct
                val sc = focal / (focal + z2 * r)
                sx[i] = cx + x1 * r * sc
                sy[i] = cy + y2 * r * sc
                depth[i] = z2
                scArr[i] = sc
                alpha[i] = 0.30f + (z2 + 1) / 2 * 0.70f
            }

            // Edges — one settled pass + accent flare on the freshest few.
            val settled = Path()
            val fresh = Path()
            for (e in 0 until shown) {
                val (i, j) = model.edges[e]
                val isFresh = !done && (shown - e) < 6
                val target = if (isFresh) fresh else settled
                target.moveTo(sx[i], sy[i])
                target.lineTo(sx[j], sy[j])
            }
            drawPath(settled, SK.ink.copy(alpha = 0.20f), style = Stroke(width = 1f))
            drawPath(fresh, SK.accent.copy(alpha = 0.65f), style = Stroke(width = 1.2f))

            // Nodes back-to-front.
            val order = model.nodes.indices.sortedBy { depth[it] }
            for (i in order) {
                val n = model.nodes[i]
                val pulse = 0.6f + 0.4f * sin(elapsed / 0.6f + n.phase)
                val nodeR = maxOf(0.6f, n.size * scArr[i] * (if (n.special) 1.7f * pulse else 1f))
                val color = if (n.special) SK.accent else SK.ink
                drawOval(
                    color = color.copy(alpha = alpha[i].coerceIn(0f, 1f)),
                    topLeft = Offset(sx[i] - nodeR, sy[i] - nodeR),
                    size = Size(nodeR * 2, nodeR * 2)
                )
            }

            // Progress ring.
            val ringD = sizePx * 0.95f
            val ringRect = Rect(
                Offset((sizePx - ringD) / 2, (sizePx - ringD) / 2),
                Size(ringD, ringD)
            )
            drawArc(
                color = SK.track,
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = ringRect.topLeft, size = ringRect.size,
                style = Stroke(width = 2.dp.toPx())
            )
            drawArc(
                color = SK.ink,
                startAngle = -90f, sweepAngle = 360f * pct / 100f, useCenter = false,
                topLeft = ringRect.topLeft, size = ringRect.size,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Core
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
                Text(
                    "$pct",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 38.sp,
                    color = SK.ink
                )
                Text(
                    "%",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 16.sp,
                    color = SK.sub,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            MonoLabel(
                text = if (done) "read" else "reading",
                size = 10f, tracking = 1.8f, color = SK.sub
            )
        }
    }
}

// ── Orb geometry (built once) ────────────────────────────────────────────────

private class OrbModel(count: Int) {
    class Node(
        val x: Float, val y: Float, val z: Float,
        val size: Float, val phase: Float, val special: Boolean
    )

    val nodes: List<Node>
    val edges: List<Pair<Int, Int>>

    init {
        val ns = ArrayList<Node>(count)
        val gold = (Math.PI * (1 + sqrt(5.0))).toFloat()
        for (i in 0 until count) {
            val phi = acos(1 - 2 * (i + 0.5f) / count)
            val theta = gold * i
            val rr = 0.80f + Random.nextFloat() * 0.20f
            ns.add(
                Node(
                    x = sin(phi) * cos(theta) * rr,
                    y = sin(phi) * sin(theta) * rr,
                    z = cos(phi) * rr,
                    size = 0.8f + Random.nextFloat() * 1.5f,
                    phase = Random.nextFloat() * 2f * Math.PI.toFloat(),
                    special = Random.nextFloat() < 0.13f
                )
            )
        }
        nodes = ns

        // Each node → its 2 nearest neighbours, de-duped, then shuffled so the
        // reveal looks scattered rather than ordered.
        val seen = HashSet<Int>()
        val es = ArrayList<Pair<Int, Int>>()
        for (i in 0 until count) {
            val d = ArrayList<Pair<Float, Int>>(count - 1)
            for (j in 0 until count) {
                if (j == i) continue
                val dx = ns[i].x - ns[j].x
                val dy = ns[i].y - ns[j].y
                val dz = ns[i].z - ns[j].z
                d.add((dx * dx + dy * dy + dz * dz) to j)
            }
            d.sortBy { it.first }
            for (k in 0 until 2) {
                val j = d[k].second
                val key = if (i < j) i * count + j else j * count + i
                if (!seen.add(key)) continue
                es.add(i to j)
            }
        }
        es.shuffle()
        edges = es
    }
}
