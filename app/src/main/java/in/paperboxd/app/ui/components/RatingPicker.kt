package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.TextSecondary

/** 1–5 star picker; pass onRate = null for display-only. */
@Composable
fun RatingPicker(
    rating: Int,
    onRate: ((Int) -> Unit)? = null,
    starSize: Dp = 28.dp,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (star in 1..5) {
            val filled = star <= rating
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = if (filled) Accent else TextSecondary.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(starSize)
                    .let { m ->
                        if (onRate != null) m.clickable { onRate(star) } else m
                    }
            )
        }
    }
}
