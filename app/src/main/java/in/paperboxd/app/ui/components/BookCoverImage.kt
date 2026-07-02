package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextSecondary

/**
 * Async book cover with HTTPS force (Google Books returns http://) and a
 * tinted spine fallback showing the title.
 */
@Composable
fun BookCoverImage(
    url: String?,
    title: String,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 6.dp
) {
    val secureUrl = url?.replace("http://", "https://")
    Box(modifier = modifier.clip(RoundedCornerShape(cornerRadius)).background(Surface)) {
        if (secureUrl.isNullOrEmpty()) {
            SpineFallback(title)
        } else {
            SubcomposeAsyncImage(
                model = secureUrl,
                contentDescription = title,
                contentScale = ContentScale.Crop,
                loading = { ShimmerBox(modifier = Modifier.fillMaxSize()) },
                error = { SpineFallback(title) },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun SpineFallback(title: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Surface),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            maxLines = 4,
            modifier = Modifier.padding(6.dp)
        )
    }
}
