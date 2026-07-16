package `in`.paperboxd.app.ui.screens.scan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * The three endless "while-you-wait" games for the Scan & Know analysis screen —
 * Compose Canvas ports of the iOS SpriteKit scenes (`ScanGameScene`,
 * `ScanCatchScene`, `ScanStackScene`). Light brutalist: game canvas paper,
 * ink paddle/ball, book-spine colors as the only color. Fixed 60Hz timestep so
 * the iOS per-frame tuning constants carry over unchanged.
 */

/** Game canvas paper `#FCFBF7` and ink `#141414` (light theme, like iOS). */
private val GamePaper = Color(0xFFFCFBF7)
private val GameInk = Color(0xFF141414)

/** HUD sink — iOS `ScanGameHUDChanged` notification twin. lives == -1 → uses best. */
typealias GameHud = (score: Int, lives: Int, best: Int) -> Unit

private const val STEP = 1f / 60f

/** Runs a fixed-timestep loop, redrawing every frame. */
@Composable
private fun GameLoop(onStep: () -> Unit): Long {
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) {
        var last = 0L
        var acc = 0f
        while (true) {
            withFrameNanos { now ->
                if (last != 0L) {
                    acc += min((now - last) / 1e9f, 1f / 20f)
                    while (acc >= STEP) {
                        acc -= STEP
                        onStep()
                    }
                }
                last = now
                tick = now
            }
        }
    }
    return tick
}

// ═════════════════════════════════════════════════════════════════════════════
// BREAKOUT — drag the paddle; break the shelf of spines. iOS `ScanGameScene`.
// ═════════════════════════════════════════════════════════════════════════════

/** Muted, editorial spine tones — warm/cool rotation, no pure primaries. */
private val BreakoutSpines = listOf(
    Color(0xFFD97757), // terracotta
    Color(0xFFA8674D), // clay
    Color(0xFFE8D5B7), // paper
    Color(0xFF7A846F), // sage
    Color(0xFF687382), // dusty slate-blue
    Color(0xFFBC9A5A), // muted gold
    Color(0xFF8B5A56), // oxblood-brown
    Color(0xFF5A6B67), // teal-slate
)

private fun Color.adjustBrightness(delta: Float) = Color(
    red = (red + delta).coerceIn(0f, 1f),
    green = (green + delta).coerceIn(0f, 1f),
    blue = (blue + delta).coerceIn(0f, 1f),
    alpha = alpha
)

private class Brick(
    val x: Float, val y: Float, val w: Float, val h: Float,
    val color: Color, val row: Int, val hasBand: Boolean
)

private class Frag(
    var x: Float, var y: Float, var vx: Float, var vy: Float,
    val w: Float, val h: Float, val color: Color,
    var rot: Float, val vr: Float, var life: Float = 0.6f
)

private class Dot(
    var x: Float, var y: Float, val vx: Float, val vy: Float,
    var life: Float, val maxLife: Float
)

private class BreakoutState(val density: Float, val onHud: GameHud) {
    var w = 0f; var h = 0f
    var ready = false

    // Tuning (iOS points ≈ dp; scaled to px by density).
    val ballSpeed get() = 520f * density
    val ballR get() = 11f * density
    val paddleW get() = 96f * density
    val paddleH get() = 16f * density

    var paddleX = 0f
    var ballX = 0f; var ballY = 0f
    var vx = 0f; var vy = 0f

    val bricks = ArrayList<Brick>()
    val frags = ArrayList<Frag>()
    val dots = ArrayList<Dot>()
    var score = 0; var lives = 3
    var refillDelay = -1f   // seconds until reseed; <0 = off

    val paddleY get() = h - maxOf(54f * density, h * 0.11f)

    fun layout(size: Size) {
        if (ready && size.width == w && size.height == h) return
        w = size.width; h = size.height
        paddleX = w / 2
        ballX = w / 2; ballY = h * 0.58f
        buildBricks()
        launchBall()
        ready = true
        onHud(score, lives, 0)
    }

    /** Packed "shelf" of tall, narrow spines across the upper ~60%. */
    fun buildBricks() {
        bricks.clear()
        val rows = 5
        val rowSpacing = 8f * density
        val colSpacing = 7f * density
        val sideInset = 14f * density
        val topInset = maxOf(56f * density, h * 0.12f)
        val gridBottom = h * 0.60f
        val availableH = gridBottom - topInset
        val brickH = (availableH - rowSpacing * (rows - 1)) / rows
        val nominalW = maxOf(22f * density, brickH / 2.2f)
        val usableW = w - sideInset * 2
        val cols = maxOf(4, ((usableW + colSpacing) / (nominalW + colSpacing)).toInt())
        val brickW = (usableW - colSpacing * (cols - 1)) / cols

        for (row in 0 until rows) {
            val y = topInset + brickH / 2 + row * (brickH + rowSpacing)
            for (col in 0 until cols) {
                val x = sideInset + brickW / 2 + col * (brickW + colSpacing)
                val base = BreakoutSpines[(row * 3 + Random.nextInt(3)) % BreakoutSpines.size]
                bricks.add(
                    Brick(
                        x, y, brickW, brickH,
                        base.adjustBrightness(Random.nextFloat() * 0.10f - 0.05f),
                        row, Random.nextInt(4) == 0
                    )
                )
            }
        }
    }

    /** Upward cone, 54°..126° — never dead-vertical, never too horizontal. */
    fun launchBall() {
        val angle = (Math.PI * (0.30 + Random.nextDouble() * 0.40)).toFloat()
        vx = cos(angle) * ballSpeed
        vy = -sin(angle) * ballSpeed
    }

    fun resetBall() {
        ballX = w / 2; ballY = h * 0.58f
        launchBall()
    }

    fun movePaddle(x: Float) {
        val half = paddleW / 2
        paddleX = x.coerceIn(half, w - half)
    }

    /** Holds the ball at a constant speed; never lets it go near-horizontal. */
    fun maintainSpeed() {
        val speed = sqrt(vx * vx + vy * vy)
        if (speed < 1f) return
        val minVertical = ballSpeed * 0.22f
        if (abs(vy) < minVertical) vy = (if (vy < 0) -1 else 1) * minVertical
        val mag = sqrt(vx * vx + vy * vy)
        val scale = ballSpeed / mag
        vx *= scale; vy *= scale
    }

    fun step() {
        if (!ready) return
        ballX += vx * STEP
        ballY += vy * STEP

        // Walls (left / top / right; bottom open so a missed ball falls out).
        if (ballX - ballR < 0) { ballX = ballR; vx = abs(vx) }
        if (ballX + ballR > w) { ballX = w - ballR; vx = -abs(vx) }
        if (ballY - ballR < 0) { ballY = ballR; vy = abs(vy) }

        // Paddle.
        if (vy > 0 &&
            ballY + ballR >= paddleY - paddleH / 2 && ballY + ballR <= paddleY + paddleH &&
            abs(ballX - paddleX) <= paddleW / 2 + ballR
        ) {
            // English: edge hits add noticeably more sideways kick than centre hits.
            val normalized = ((ballX - paddleX) / (paddleW / 2)).coerceIn(-1f, 1f)
            vx += normalized * ballSpeed * 0.45f
            vy = -abs(vy)
            ballY = paddleY - paddleH / 2 - ballR
        }

        // Bricks — circle vs AABB, reflect on the shallow axis.
        val hit = bricks.indexOfFirst { b ->
            abs(ballX - b.x) <= b.w / 2 + ballR && abs(ballY - b.y) <= b.h / 2 + ballR
        }
        if (hit >= 0) {
            val b = bricks.removeAt(hit)
            val overlapX = b.w / 2 + ballR - abs(ballX - b.x)
            val overlapY = b.h / 2 + ballR - abs(ballY - b.y)
            if (overlapX < overlapY) vx = if (ballX < b.x) -abs(vx) else abs(vx)
            else vy = if (ballY < b.y) -abs(vy) else abs(vy)
            score += 1
            onHud(score, lives, 0)
            spawnFragments(b)
            emitPageBurst(b.x, b.y)
            // Keep the loading screen alive: an empty shelf feels broken, reseed it.
            if (bricks.isEmpty() && refillDelay < 0) refillDelay = 0.35f
        }

        if (refillDelay >= 0) {
            refillDelay -= STEP
            if (refillDelay < 0 && bricks.isEmpty()) buildBricks()
        }

        maintainSpeed()

        // Missed — fell off the bottom.
        if (ballY - ballR > h) {
            lives -= 1
            if (lives <= 0) { lives = 3; score = 0; buildBricks() }
            onHud(score, lives, 0)
            resetBall()
        }

        stepEffects()
    }

    /** 2-3 small shards flung outward + slightly down, then faded out. */
    fun spawnFragments(b: Brick) {
        repeat(2 + Random.nextInt(2)) {
            frags.add(
                Frag(
                    x = b.x, y = b.y,
                    vx = (Random.nextFloat() * 140f - 70f) * density,
                    vy = (15f + Random.nextFloat() * 75f) * density,
                    w = b.w * (0.30f + Random.nextFloat() * 0.20f),
                    h = b.h * (0.16f + Random.nextFloat() * 0.12f),
                    color = b.color,
                    rot = 0f,
                    vr = Random.nextFloat() * 240f - 120f
                )
            )
        }
    }

    /** A short, low-count charcoal burst to suggest scattering pages. */
    fun emitPageBurst(x: Float, y: Float) {
        repeat(8 + Random.nextInt(5)) {
            val a = Random.nextFloat() * 2f * Math.PI.toFloat()
            val sp = (95f + (Random.nextFloat() - 0.5f) * 110f) * density
            val life = 0.5f + (Random.nextFloat() - 0.5f) * 0.36f
            dots.add(Dot(x, y, cos(a) * sp, sin(a) * sp, life, life))
        }
    }

    fun stepEffects() {
        frags.removeAll { f ->
            f.x += f.vx * STEP; f.y += f.vy * STEP
            f.vx *= (1 - 1.4f * STEP); f.vy *= (1 - 1.4f * STEP)
            f.rot += f.vr * STEP
            f.life -= STEP
            f.life <= 0
        }
        dots.removeAll { d ->
            d.x += d.vx * STEP; d.y += d.vy * STEP
            d.life -= STEP
            d.life <= 0
        }
    }

    fun draw(scope: DrawScope) = with(scope) {
        drawRect(GamePaper)
        val corner = CornerRadius(2.5f * density)
        for (b in bricks) {
            drawRoundRect(
                b.color,
                topLeft = Offset(b.x - b.w / 2, b.y - b.h / 2),
                size = Size(b.w, b.h),
                cornerRadius = corner
            )
            if (b.hasBand) {
                // Thin "title band" line near the top of the spine.
                drawRect(
                    Color.Black.copy(alpha = 0.30f),
                    topLeft = Offset(b.x - b.w * 0.31f, b.y - b.h * 0.32f),
                    size = Size(b.w * 0.62f, 1.5f * density)
                )
            }
        }
        for (f in frags) {
            rotate(f.rot, pivot = Offset(f.x, f.y)) {
                drawRect(
                    f.color.copy(alpha = (f.life / 0.6f).coerceIn(0f, 1f)),
                    topLeft = Offset(f.x - f.w / 2, f.y - f.h / 2),
                    size = Size(f.w, f.h)
                )
            }
        }
        for (d in dots) {
            val a = (d.life / d.maxLife).coerceIn(0f, 1f) * 0.9f
            drawCircle(
                Color(0xFF404040).copy(alpha = a),
                radius = 1.6f * density,
                center = Offset(d.x, d.y)
            )
        }
        // Paddle.
        drawRoundRect(
            GameInk,
            topLeft = Offset(paddleX - paddleW / 2, paddleY - paddleH / 2),
            size = Size(paddleW, paddleH),
            cornerRadius = CornerRadius(8f * density)
        )
        // Ball + decorative bookmark ribbon hanging below.
        val rw = ballR * 0.42f
        val rh = ballR
        val ribbonTop = ballY + ballR * 0.56f
        val ribbon = Path().apply {
            moveTo(ballX - rw, ribbonTop)
            lineTo(ballX - rw, ribbonTop + rh)
            lineTo(ballX, ribbonTop + rh * 0.55f)
            lineTo(ballX + rw, ribbonTop + rh)
            lineTo(ballX + rw, ribbonTop)
            close()
        }
        drawPath(ribbon, GameInk.copy(alpha = 0.82f))
        drawCircle(GameInk, radius = ballR, center = Offset(ballX, ballY))
    }
}

@Composable
fun BreakoutGame(onHud: GameHud, modifier: Modifier = Modifier) {
    val density = LocalDensity.current.density
    val state = remember { BreakoutState(density, onHud) }
    val tick = GameLoop { state.step() }

    Canvas(
        modifier.pointerInput(Unit) {
            awaitEachGesture {
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull() ?: break
                    state.movePaddle(change.position.x)
                    if (event.changes.all { !it.pressed }) break
                }
            }
        }
    ) {
        @Suppress("UNUSED_EXPRESSION") tick
        state.layout(size)
        state.draw(this)
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// CATCH — drag the shelf; catch falling books before they hit the floor.
// iOS `ScanCatchScene`.
// ═════════════════════════════════════════════════════════════════════════════

private class FallingBook(
    var x: Float, var y: Float, val vy: Float,
    val w: Float, val h: Float, val color: Color, val rot: Float
)

private class CatchState(val density: Float, val onHud: GameHud) {
    var w = 0f; var h = 0f
    var ready = false

    val shelfW get() = 92f * density
    val shelfH get() = 12f * density
    val shelfY get() = h - 34f * density

    var shelfX = 0f
    val books = ArrayList<FallingBook>()
    var score = 0; var lives = 3
    var spawnTick = 0
    var rate = 64f

    fun layout(size: Size) {
        if (ready && size.width == w && size.height == h) return
        w = size.width; h = size.height
        shelfX = w / 2
        ready = true
        onHud(score, lives, 0)
    }

    fun moveShelf(x: Float) {
        val half = shelfW / 2
        shelfX = x.coerceIn(half, w - half)
    }

    fun spawn() {
        val bw = (20f + Random.nextFloat() * 12f) * density
        val bh = bw * 1.45f
        val x = (12f * density + bw / 2) +
            Random.nextFloat() * (w - 24f * density - bw)
        val vy = (1.5f + Random.nextFloat() * 1.0f + score * 0.004f) * density
        books.add(
            FallingBook(
                x, -bh, vy, bw, bh,
                SK.spines[Random.nextInt(SK.spines.size)],
                Random.nextFloat() * 0.44f - 0.22f
            )
        )
    }

    fun step() {
        if (!ready) return
        spawnTick += 1
        if (spawnTick >= rate) {
            spawnTick = 0
            spawn()
            if (rate > 34f) rate -= 0.4f
        }

        val shelfTop = shelfY - shelfH / 2 - 8f * density
        val it = books.listIterator(books.size)
        while (it.hasPrevious()) {
            val b = it.previous()
            b.y += b.vy
            val bottom = b.y + b.h / 2
            if (bottom >= shelfTop && bottom <= shelfY + 6f * density &&
                abs(b.x - shelfX) <= shelfW / 2 + 6f * density
            ) {
                score += 1
                it.remove()
                onHud(score, lives, 0)
            } else if (b.y - b.h / 2 > h) {
                it.remove()
                lives -= 1
                if (lives <= 0) {
                    lives = 3; score = 0; rate = 64f
                    books.clear()
                }
                onHud(score, lives, 0)
            }
        }
    }

    fun draw(scope: DrawScope) = with(scope) {
        drawRect(GamePaper)
        for (b in books) {
            rotate(Math.toDegrees(b.rot.toDouble()).toFloat(), pivot = Offset(b.x, b.y)) {
                drawRect(
                    b.color,
                    topLeft = Offset(b.x - b.w / 2, b.y - b.h / 2),
                    size = Size(b.w, b.h)
                )
            }
        }
        drawRect(
            GameInk,
            topLeft = Offset(shelfX - shelfW / 2, shelfY - shelfH / 2),
            size = Size(shelfW, shelfH)
        )
    }
}

@Composable
fun CatchGame(onHud: GameHud, modifier: Modifier = Modifier) {
    val density = LocalDensity.current.density
    val state = remember { CatchState(density, onHud) }
    val tick = GameLoop { state.step() }

    Canvas(
        modifier.pointerInput(Unit) {
            awaitEachGesture {
                while (true) {
                    val event = awaitPointerEvent()
                    val change = event.changes.firstOrNull() ?: break
                    state.moveShelf(change.position.x)
                    if (event.changes.all { !it.pressed }) break
                }
            }
        }
    ) {
        @Suppress("UNUSED_EXPRESSION") tick
        state.layout(size)
        state.draw(this)
    }
}

// ═════════════════════════════════════════════════════════════════════════════
// STACK — tap to drop the sliding book; overlap trims it. Build the tower.
// iOS `ScanStackScene`.
// ═════════════════════════════════════════════════════════════════════════════

private class StackBlock(var x: Float, var y: Float, var w: Float, val color: Color)

private class StackState(val density: Float, val onHud: GameHud) {
    var w = 0f; var h = 0f
    var ready = false

    val blockH get() = 26f * density
    val baseW get() = 150f * density

    val blocks = ArrayList<StackBlock>()
    var moving: StackBlock? = null
    var falling: StackBlock? = null
    var fallElapsed = 0f
    var dir = 1f
    var slideSpeed = 2.3f
    var camTarget = 0f
    var worldShift = 0f      // px the world is pushed down by (camera pan)
    var score = 0; var best = 0
    var resetting = false
    var resetDelay = 0f

    fun layout(size: Size) {
        if (ready && size.width == w && size.height == h) return
        w = size.width; h = size.height
        reset()
        ready = true
    }

    /** y-center of a tower level, in world space (pre-camera), Compose y-down. */
    fun centerY(level: Int) = h - (40f * density + blockH / 2 + level * blockH)

    fun reset() {
        resetting = false
        worldShift = 0f
        camTarget = 0f
        blocks.clear()
        moving = null
        falling = null
        score = 0
        blocks.add(StackBlock(w / 2, centerY(0), baseW, SK.spines[0]))
        newMover()
        onHud(score, -1, best)
    }

    fun newMover() {
        val top = blocks.lastOrNull() ?: return
        slideSpeed = 2.3f + score * 0.05f
        dir = if (blocks.size % 2 == 0) -1f else 1f
        moving = StackBlock(
            x = if (dir > 0) -top.w / 2 else w + top.w / 2,
            y = centerY(blocks.size),
            w = top.w,
            color = SK.spines[blocks.size % SK.spines.size]
        )
    }

    fun drop() {
        val m = moving ?: return
        val top = blocks.lastOrNull() ?: return
        if (resetting) return
        val left = maxOf(m.x - m.w / 2, top.x - top.w / 2)
        val right = min(m.x + m.w / 2, top.x + top.w / 2)
        val overlap = right - left
        if (overlap <= 2f * density) {
            // Missed — the block tumbles off and the tower resets.
            resetting = true
            falling = m
            fallElapsed = 0f
            moving = null
            best = maxOf(best, score)
            onHud(score, -1, best)
            resetDelay = 0.65f
            return
        }
        m.w = overlap
        m.x = (left + right) / 2
        blocks.add(m)
        moving = null
        score += 1; best = maxOf(best, score)

        val towerTopWorld = centerY(blocks.size - 1) - blockH / 2
        // Keep the tower's top below 45% of the screen (iOS: above 55% from bottom).
        if (towerTopWorld < h * 0.45f) camTarget = h * 0.45f - towerTopWorld
        newMover()
        onHud(score, -1, best)
    }

    fun step() {
        if (!ready) return
        val m = moving
        if (m != null && !resetting) {
            m.x += dir * slideSpeed * density
            val half = m.w / 2
            if (m.x + half > w) { m.x = w - half; dir = -1f }
            if (m.x - half < 0) { m.x = half; dir = 1f }
        }
        falling?.let { f ->
            fallElapsed += STEP
            f.y += (h / 0.6f) * STEP
            if (fallElapsed >= 0.6f) falling = null
        }
        if (resetting) {
            resetDelay -= STEP
            if (resetDelay <= 0) reset()
        }
        worldShift += (camTarget - worldShift) * 0.12f
    }

    fun draw(scope: DrawScope) = with(scope) {
        drawRect(GamePaper)
        for (b in blocks) {
            drawRect(
                b.color,
                topLeft = Offset(b.x - b.w / 2, b.y - blockH / 2 + worldShift),
                size = Size(b.w, blockH)
            )
        }
        moving?.let { m ->
            drawRect(
                m.color,
                topLeft = Offset(m.x - m.w / 2, m.y - blockH / 2 + worldShift),
                size = Size(m.w, blockH)
            )
        }
        falling?.let { f ->
            rotate(
                fallElapsed / 0.6f * 34.4f,   // 0.6 rad over the fall, in degrees
                pivot = Offset(f.x, f.y + worldShift)
            ) {
                drawRect(
                    f.color,
                    topLeft = Offset(f.x - f.w / 2, f.y - blockH / 2 + worldShift),
                    size = Size(f.w, blockH)
                )
            }
        }
    }
}

@Composable
fun StackGame(onHud: GameHud, modifier: Modifier = Modifier) {
    val density = LocalDensity.current.density
    val state = remember { StackState(density, onHud) }
    val tick = GameLoop { state.step() }

    Canvas(
        modifier.pointerInput(Unit) {
            detectTapGestures { state.drop() }
        }
    ) {
        @Suppress("UNUSED_EXPRESSION") tick
        state.layout(size)
        state.draw(this)
    }
}
