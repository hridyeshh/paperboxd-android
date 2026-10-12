package `in`.paperboxd.app.ui.components

import android.content.Context
import android.provider.Settings
import androidx.annotation.RawRes
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

/**
 * A pre-drawn cat loop (res/raw/cat_play, cat_match) — vector frames exported from the
 * design rig, shared byte-for-byte with iOS. `.frames` format: header
 * `fps=30 vb=x,y,w,h colors=151513,FFFFFF,ECE7DD frames=N`, then one line per frame of
 * `|`-joined shapes, each a colour index followed by an SVG path using M/L/l/z.
 */
class CatClip private constructor(
    val fps: Int,
    val vbX: Float, val vbY: Float, val vbW: Float, val vbH: Float,
    val frames: List<List<Pair<Color, Path>>>
) {
    companion object {
        private val cache = ConcurrentHashMap<Int, CatClip>()

        fun load(context: Context, @RawRes res: Int): CatClip = cache.getOrPut(res) {
            parse(context.resources.openRawResource(res).bufferedReader().use { it.readText() })
        }

        private fun parse(text: String): CatClip {
            val lines = text.split('\n').filter { it.isNotEmpty() }
            var fps = 30
            var vb = floatArrayOf(0f, 0f, 1f, 1f)
            var colors = listOf(Color.Black)
            for (field in lines.first().split(' ')) {
                val (key, value) = field.split('=', limit = 2).takeIf { it.size == 2 } ?: continue
                when (key) {
                    "fps" -> fps = value.toIntOrNull() ?: 30
                    "vb" -> value.split(',').mapNotNull { it.toFloatOrNull() }.takeIf { it.size == 4 }?.let { vb = it.toFloatArray() }
                    "colors" -> colors = value.split(',').map { Color(0xFF000000 or it.toLong(16)) }
                }
            }
            val frames = lines.drop(1).map { line ->
                line.split('|').mapNotNull { piece ->
                    val index = piece.firstOrNull()?.digitToIntOrNull() ?: return@mapNotNull null
                    colors.getOrNull(index)?.let { it to path(piece, 1) }
                }
            }
            return CatClip(fps, vb[0], vb[1], vb[2], vb[3], frames)
        }

        private fun path(s: String, from: Int): Path {
            val path = Path()
            var command = 'M'
            var x = 0f
            var y = 0f
            var pendingX = Float.NaN
            var i = from
            while (i < s.length) {
                when (val c = s[i]) {
                    'M', 'L', 'l' -> { command = c; i++ }
                    'z', 'Z' -> { path.close(); i++ }
                    ' ', ',' -> i++
                    else -> {
                        val tokenStart = i
                        var negative = false
                        if (c == '-') { negative = true; i++ }
                        var whole = 0f
                        var fraction = 0f
                        var scale = 1f
                        var dot = false
                        while (i < s.length) {
                            val d = s[i]
                            if (d in '0'..'9') {
                                if (dot) { scale /= 10f; fraction += (d - '0') * scale } else whole = whole * 10f + (d - '0')
                            } else if (d == '.' && !dot) {
                                dot = true
                            } else break
                            i++
                        }
                        if (i == tokenStart) { i++; continue }
                        val v = if (negative) -(whole + fraction) else whole + fraction
                        if (pendingX.isNaN()) { pendingX = v; continue }
                        val px = pendingX
                        pendingX = Float.NaN
                        when (command) {
                            'M' -> { x = px; y = v; path.moveTo(x, y); command = 'L' }
                            'L' -> { x = px; y = v; path.lineTo(x, y) }
                            else -> { x += px; y += v; path.lineTo(x, y) }
                        }
                    }
                }
            }
            return path
        }
    }
}

/** Plays [res] at the clip's own 30fps; holds the first frame when system animations are off. */
@Composable
fun CatAnimation(@RawRes res: Int, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val clip by produceState<CatClip?>(initialValue = null, res) {
        value = withContext(Dispatchers.Default) { CatClip.load(context, res) }
    }
    val still = remember {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) == 0f
    }
    var frame by remember { mutableIntStateOf(0) }
    LaunchedEffect(clip) {
        val c = clip ?: return@LaunchedEffect
        if (still || c.frames.isEmpty()) return@LaunchedEffect
        var start = -1L
        while (true) {
            withFrameNanos { now ->
                if (start < 0) start = now
                frame = (max(0L, now - start) / 1_000_000_000.0 * c.fps).toInt() % c.frames.size
            }
        }
    }
    Canvas(modifier) {
        val c = clip ?: return@Canvas
        val shapes = c.frames.getOrNull(frame) ?: return@Canvas
        val s = min(size.width / c.vbW, size.height / c.vbH)
        withTransform({
            translate((size.width - c.vbW * s) / 2f, (size.height - c.vbH * s) / 2f)
            scale(s, s, pivot = Offset.Zero)
            translate(-c.vbX, -c.vbY)
        }) {
            for ((color, path) in shapes) drawPath(path, color)
        }
    }
}
