package `in`.paperboxd.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val PaperBoxdColorScheme = darkColorScheme(
    background = Background,
    surface = Surface,
    primary = Accent,
    onPrimary = Background,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Error,
    onError = TextPrimary,
    secondary = TextSecondary,
    onSecondary = Background,
    outline = Border,
    surfaceVariant = Surface,
    onSurfaceVariant = TextSecondary
)

/** Single dark theme — no dynamic color, no light variant. */
@Composable
fun PaperBoxdTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PaperBoxdColorScheme,
        typography = PaperBoxdTypography,
        content = content
    )
}
