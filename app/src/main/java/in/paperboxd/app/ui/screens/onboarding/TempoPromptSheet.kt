package `in`.paperboxd.app.ui.screens.onboarding

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalButton

enum class ReadingTempo(val title: String, val subtitle: String) {
    Casual("Casual", "0–2 books / month"),
    Regular("Regular", "3–4 books / month"),
    Voracious("Voracious", "5+ books / month");

    companion object {
        private const val PREFS = "pb_prefs"
        private const val KEY = "pb_reading_tempo"

        fun stored(context: Context): ReadingTempo? =
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY, null)
                ?.let { name -> entries.find { it.name == name } }

        fun save(context: Context, tempo: ReadingTempo) {
            context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY, tempo.name).apply()
        }
    }
}

/**
 * One-shot bottom sheet shown over Home right after onboarding finishes.
 * Local-only preference (no backend field yet) — just paces recommendation
 * copy client-side, so it's a plain SharedPreferences write, not a network call.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempoPromptSheet(onDismiss: () -> Unit) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf<ReadingTempo?>(null) }

    fun finish() {
        selected?.let { ReadingTempo.save(context, it) }
        onDismiss()
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = HL.Paper) {
        Column(Modifier.padding(24.dp).padding(bottom = 24.dp)) {
            Text(
                "Set a Tempo",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = HL.Ink
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "What are your reading goals? This will help us pace recommendations.",
                fontSize = 13.5.sp,
                lineHeight = 18.sp,
                color = HL.Muted
            )
            Spacer(Modifier.height(20.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ReadingTempo.entries.forEach { tempo ->
                    Column(
                        Modifier
                            .fillMaxWidth()
                            .brutalButton(onClick = { selected = tempo; finish() }, fill = HL.Card)
                            .padding(14.dp)
                    ) {
                        Text(tempo.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = HL.Ink)
                        Text(tempo.subtitle, fontSize = 12.sp, color = HL.Muted)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    ),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "start reading",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HL.Muted,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = HL.Muted, modifier = Modifier.height(14.dp))
            }
        }
    }
}
