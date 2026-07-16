package `in`.paperboxd.app.ui.screens.scan

import android.content.Context
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Live free-scan count — iOS `@AppStorage("pb_scans_remaining")` twin.
 * Persisted by the scan flow after each analyze response; read by the
 * Reveal/Breakdown footers and Settings.
 */
object ScanPrefs {
    private const val FILE = "pb_scan"
    private const val KEY = "pb_scans_remaining"
    private const val DEFAULT = 7

    fun scansRemaining(context: Context): Int =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE).getInt(KEY, DEFAULT)

    fun setScansRemaining(context: Context, value: Int) {
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
            .edit().putInt(KEY, value).apply()
    }

    fun footerText(remaining: Int): String = when (remaining) {
        0 -> "No free scans remaining"
        1 -> "1 free scan remaining"
        else -> "$remaining free scans remaining"
    }
}

/**
 * Live free-scan count footer — iOS `ScansRemainingFooter` twin. Shown at the
 * foot of the Reveal + Breakdown screens.
 */
@Composable
fun ScansRemainingFooter(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val remaining by remember { mutableIntStateOf(ScanPrefs.scansRemaining(context)) }
    Text(
        text = ScanPrefs.footerText(remaining),
        fontSize = 13.sp,
        color = SK.sub,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(vertical = 12.dp)
    )
}
