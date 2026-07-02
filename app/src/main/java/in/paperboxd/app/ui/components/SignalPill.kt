package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.ui.theme.Accent

/** Recommendation-reason pill ("Because you read…"), iOS SignalPillView twin. */
@Composable
fun SignalPill(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = Accent,
        maxLines = 1,
        modifier = modifier
            .clip(CircleShape)
            .background(Accent.copy(alpha = 0.12f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    )
}
