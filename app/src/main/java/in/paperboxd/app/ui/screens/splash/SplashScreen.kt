package `in`.paperboxd.app.ui.screens.splash

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.view.Surface
import android.view.TextureView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.core.view.WindowCompat
import androidx.compose.ui.platform.LocalView
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import `in`.paperboxd.app.R
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme
import kotlinx.coroutines.delay

/** Aspect of res/raw/splash.mp4 — 1080x1350. */
private const val VIDEO_ASPECT = 1080f / 1350f

/**
 * Playback rate for the clip. The 4.87s source runs 3.9s at 1.25x —
 * AppState.holdSplash is floored above that plus the outro fade.
 */
private const val PLAYBACK_SPEED = 1.25f

/**
 * How long the clip takes to fade off the white ground once it ends.
 * AppState.holdSplash is floored above clip + this, so routing never interrupts
 * the fade. Matches iOS SplashView.outroDuration.
 */
private const val OUTRO_MS = 500

/**
 * The mark animation (res/raw/splash.mp4) on its own white ground, with pulsing
 * dots and cycling captions underneath. Bootstrap runs on first composition;
 * AppState routes away when it resolves — its splash floor covers the clip, so
 * the animation is never cut mid-morph. Mirrors iOS SplashView.
 */
@Composable
fun SplashScreen(onBootstrap: suspend () -> Unit) {
    LaunchedEffect(Unit) { onBootstrap() }
    SplashContent()
}

@Composable
fun SplashContent() {
    // Light page → dark status-bar icons while this screen is visible. Same
    // pattern Home uses; enableEdgeToEdge() picks icons from the dark Compose
    // theme, which would leave them white-on-paper.
    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as? android.app.Activity ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(activity.window, view)
        val wasLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = true
        onDispose { controller.isAppearanceLightStatusBars = wasLight }
    }

    val captions = listOf(
        stringResource(R.string.splash_caption_1),
        stringResource(R.string.splash_caption_2),
        stringResource(R.string.splash_caption_3),
    )
    var captionIdx by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(2_000)
            captionIdx = (captionIdx + 1) % captions.size
        }
    }

    var chromeIn by remember { mutableStateOf(false) }
    // Set when the clip reaches its end — everything on the white ground fades
    // out first, so the hand-off to the next screen reads as one movement
    // instead of a cut.
    var outro by remember { mutableStateOf(false) }
    val chromeAlpha by animateFloatAsState(
        targetValue = if (outro) 0f else 1f,
        animationSpec = tween(durationMillis = OUTRO_MS),
        label = "outro",
    )
    val captionAlpha by animateFloatAsState(
        targetValue = if (chromeIn) 1f else 0f,
        animationSpec = tween(durationMillis = 500, delayMillis = 300),
        label = "caption",
    )
    LaunchedEffect(Unit) { chromeIn = true }

    // The clip's ground is tinted to HL.Paper — the home screen's background —
    // so the screen behind it is that colour too: no video frame edge, and the
    // outro fades into the colour the app is about to show rather than white.
    Box(Modifier.fillMaxSize().background(HL.Paper)) {
        SplashVideo(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(VIDEO_ASPECT),
            outro = outro,
            onFinished = { outro = true },
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp)
                .alpha(chromeAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LoadingDots()
            Spacer(Modifier.height(14.dp))
            AnimatedContent(
                targetState = captionIdx,
                transitionSpec = { fadeIn(tween(350)) togetherWith fadeOut(tween(350)) },
                label = "caption",
            ) { idx ->
                Text(
                    text = captions[idx].uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.5f.sp,
                    letterSpacing = 1.4.sp,
                    color = HL.Ink.copy(0.42f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.alpha(captionAlpha),
                )
            }
        }
    }
}

/**
 * The clip on a [TextureView] driven by [MediaPlayer] — no ExoPlayer dependency
 * for one five-second file.
 *
 * Kept smooth by construction: the source is a bundled raw resource (no I/O
 * wait), prepare runs off the main thread, playback starts only from
 * onPrepared, and the view stays fully transparent until the first frame is
 * decoded, so a cold start can never show a half-drawn or black frame.
 * TextureView rather than SurfaceView because it composites and fades with the
 * rest of the screen instead of punching a hole through it.
 */
@Composable
private fun SplashVideo(
    modifier: Modifier = Modifier,
    outro: Boolean = false,
    onFinished: () -> Unit = {},
) {
    var ready by remember { mutableStateOf(false) }
    val videoAlpha by animateFloatAsState(
        targetValue = if (ready && !outro) 1f else 0f,
        // No fade in: the clip's first frame is the same paper as the screen
        // behind it, so it cuts in invisibly. Fading it would read as a video
        // loading. The outro still fades.
        animationSpec = tween(durationMillis = if (outro) OUTRO_MS else 0),
        label = "video",
    )

    val player = remember { MediaPlayer() }
    DisposableEffect(Unit) {
        onDispose {
            runCatching { player.stop() }
            player.release()
        }
    }

    AndroidView(
        modifier = modifier.alpha(videoAlpha),
        factory = { ctx ->
            TextureView(ctx).apply {
                isOpaque = false
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(st: SurfaceTexture, w: Int, h: Int) {
                        runCatching {
                            ctx.resources.openRawResourceFd(R.raw.splash).use { afd ->
                                player.setDataSource(
                                    afd.fileDescriptor,
                                    afd.startOffset,
                                    afd.declaredLength,
                                )
                            }
                            player.setSurface(Surface(st))
                            player.setVolume(0f, 0f)
                            player.setOnPreparedListener {
                                // Some devices start playback the moment
                                // PlaybackParams is set, others need start() —
                                // set the rate first, then start either way.
                                runCatching {
                                    it.playbackParams = it.playbackParams.setSpeed(PLAYBACK_SPEED)
                                }
                                if (!it.isPlaying) it.start()
                            }
                            // Reveal only once a frame has actually reached the
                            // surface. onPrepared fires earlier, while the
                            // surface is still blank — revealing there is what
                            // showed a black flash before the clip appeared.
                            player.setOnInfoListener { _, what, _ ->
                                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) ready = true
                                false
                            }
                            player.setOnCompletionListener { onFinished() }
                            player.prepareAsync()
                        }.onFailure {
                            // A splash that can't play must not be a splash that
                            // never leaves — bootstrap still drives routing.
                            ready = true
                            onFinished()
                        }
                    }

                    override fun onSurfaceTextureSizeChanged(st: SurfaceTexture, w: Int, h: Int) = Unit
                    override fun onSurfaceTextureDestroyed(st: SurfaceTexture) = true
                    override fun onSurfaceTextureUpdated(st: SurfaceTexture) {
                        // Not every device posts MEDIA_INFO_VIDEO_RENDERING_START;
                        // a surface update is by definition a rendered frame.
                        ready = true
                    }
                }
            }
        },
    )
}

@Composable
private fun LoadingDots() {
    val transition = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        repeat(3) { i ->
            val a by transition.animateFloat(
                initialValue = 0.14f,
                targetValue = 0.55f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 480, delayMillis = i * 160),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dot$i",
            )
            Box(
                Modifier
                    .size(5.dp)
                    .scale(0.9f + a * 0.4f)
                    .alpha(a)
                    .clip(CircleShape)
                    .background(HL.Ink),
            )
        }
    }
}

@Preview
@Composable
private fun SplashPreview() {
    PaperBoxdTheme {
        Box(Modifier.fillMaxSize().background(HL.Paper), contentAlignment = Alignment.Center) {
            LoadingDots()
        }
    }
}
