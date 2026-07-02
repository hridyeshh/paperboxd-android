package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import `in`.paperboxd.app.ui.theme.AvatarGradient
import `in`.paperboxd.app.ui.theme.TextPrimary

/** Circular avatar with terracotta-gradient initials fallback (iOS AvatarView twin). */
@Composable
fun AvatarImage(
    url: String?,
    name: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val secureUrl = url?.replace("http://", "https://")
    Box(
        modifier = modifier.size(size).clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (secureUrl.isNullOrEmpty()) {
            InitialsFallback(name, size)
        } else {
            SubcomposeAsyncImage(
                model = secureUrl,
                contentDescription = name,
                contentScale = ContentScale.Crop,
                loading = { InitialsFallback(name, size) },
                error = { InitialsFallback(name, size) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun InitialsFallback(name: String, size: Dp) {
    val initials = name.split(' ', '_', '-')
        .filter { it.isNotEmpty() }
        .take(2)
        .map { it.first().uppercaseChar() }
        .joinToString("")
        .ifEmpty { "?" }
    Box(
        modifier = Modifier.fillMaxSize().background(AvatarGradient),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = TextPrimary,
            fontWeight = FontWeight.SemiBold,
            fontSize = (size.value * 0.36f).sp
        )
    }
}
