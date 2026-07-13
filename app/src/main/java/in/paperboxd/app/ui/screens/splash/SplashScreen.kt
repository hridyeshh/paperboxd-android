package `in`.paperboxd.app.ui.screens.splash

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.R
import `in`.paperboxd.app.ui.components.BookCoverColumns
import `in`.paperboxd.app.ui.components.DarkWash
import `in`.paperboxd.app.ui.components.Wordmark
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme
import kotlinx.coroutines.delay

/**
 * Animated book-cover wall behind a dark wash, with the script wordmark, a
 * serif-italic tagline, pulsing dots, and cycling captions. Bootstrap runs on
 * first composition; AppState routes away when it resolves. Mirrors iOS
 * SplashView.
 */
@Composable
fun SplashScreen(onBootstrap: suspend () -> Unit) {
    LaunchedEffect(Unit) { onBootstrap() }
    SplashContent()
}

@Composable
fun SplashContent() {
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

    var contentIn by remember { mutableStateOf(false) }
    val wordmarkAlpha by animateFloatAsState(
        targetValue = if (contentIn) 1f else 0f,
        animationSpec = tween(durationMillis = 700),
        label = "wordmark",
    )
    val taglineAlpha by animateFloatAsState(
        targetValue = if (contentIn) 1f else 0f,
        animationSpec = tween(durationMillis = 600, delayMillis = 400),
        label = "tagline",
    )
    LaunchedEffect(Unit) { contentIn = true }

    Box(Modifier.fillMaxSize().background(Background)) {
        BookCoverColumns(Modifier.fillMaxSize())
        DarkWash(Modifier.fillMaxSize())

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Wordmark(fontSize = 46, modifier = Modifier.alpha(wordmarkAlpha))
            Spacer(Modifier.height(14.dp))
            Text(
                text = stringResource(R.string.tagline),
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp,
                color = Color.White.copy(0.58f),
                modifier = Modifier.alpha(taglineAlpha),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp),
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
                    color = Color.White.copy(0.42f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
private fun LoadingDots() {
    val transition = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        repeat(3) { i ->
            val a by transition.animateFloat(
                initialValue = 0.2f,
                targetValue = 0.9f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 480, delayMillis = i * 160),
                    repeatMode = RepeatMode.Reverse,
                ),
                label = "dot$i",
            )
            Box(
                Modifier
                    .size(5.dp)
                    .scale(0.9f + (a - 0.2f) * 0.4f)
                    .alpha(a)
                    .clip(CircleShape)
                    .background(Color.White),
            )
        }
    }
}

@Preview
@Composable
private fun SplashPreview() {
    PaperBoxdTheme {
        Box(Modifier.fillMaxSize().background(Background), contentAlignment = Alignment.Center) {
            Wordmark(fontSize = 46)
        }
    }
}
