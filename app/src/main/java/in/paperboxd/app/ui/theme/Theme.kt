package `in`.paperboxd.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

// lightColorScheme, not darkColorScheme: Material derives elevation tints and
// default on-colours from which builder you use, so a dark scheme holding light
// values still tints surfaces the wrong way. `primary` is ink rather than the
// warm accent because the app's primary actions are ink plates; the accent is
// reserved for progress fills and destructive marks, so it sits on `secondary`.
private val PaperBoxdColorScheme = lightColorScheme(
    background = Background,
    surface = Surface,
    primary = TextPrimary,
    onPrimary = Background,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    error = Error,
    onError = Background,
    secondary = Accent,
    onSecondary = Background,
    outline = Border,
    surfaceVariant = Surface,
    onSurfaceVariant = TextSecondary
)

/**
 * Single light theme — no dynamic colour, no dark variant, and it deliberately
 * never consults `isSystemInDarkTheme()`. The app is paper-and-ink on every
 * page whatever the device is set to.
 */
@Composable
fun PaperBoxdTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PaperBoxdColorScheme,
        typography = PaperBoxdTypography,
        content = content
    )
}
