package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary

/** Filled capsule CTA — TextPrimary fill, Background label, mirrors iOS PillButton primary. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false
) {
    Button(
        onClick = onClick,
        enabled = enabled && !loading,
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = TextPrimary,
            contentColor = Background,
            disabledContainerColor = TextPrimary.copy(alpha = 0.3f),
            disabledContentColor = Background.copy(alpha = 0.6f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = Background,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(text = text, style = MaterialTheme.typography.titleMedium)
        }
    }
}

/** Ghost variant — transparent fill, border, mirrors iOS PillButton ghost. */
@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    androidx.compose.material3.OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
        border = androidx.compose.foundation.BorderStroke(1.dp, `in`.paperboxd.app.ui.theme.Border),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium, color = TextSecondary)
    }
}
