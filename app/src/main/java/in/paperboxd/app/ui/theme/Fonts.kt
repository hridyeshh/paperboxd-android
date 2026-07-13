package `in`.paperboxd.app.ui.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import `in`.paperboxd.app.R

/**
 * Brand fonts.
 *
 * - [PBScript] — Pinyon Script, the calligraphic wordmark. Stands in for the
 *   iOS SnellRoundhand-Black wordmark. Wordmark use only; never body text.
 * - [PBSans] — Space Grotesk, the primary geometric-sans UI face (headings +
 *   body), replacing Roboto to match the PB kit.
 *
 * Eyebrows / numeric labels still use [FontFamily.Monospace] (Space Grotesk is
 * proportional, not mono) — see [PaperBoxdTypography].
 */
val PBScript = FontFamily(Font(R.font.pinyon_script, FontWeight.Normal))

val PBSans = FontFamily(
    Font(R.font.space_grotesk_light, FontWeight.Light),
    Font(R.font.space_grotesk_regular, FontWeight.Normal),
    Font(R.font.space_grotesk_medium, FontWeight.Medium),
    Font(R.font.space_grotesk_semibold, FontWeight.SemiBold),
    Font(R.font.space_grotesk_bold, FontWeight.Bold),
)
