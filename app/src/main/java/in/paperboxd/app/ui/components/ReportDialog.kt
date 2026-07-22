package `in`.paperboxd.app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val REPORT_REASONS = listOf(
    "Spam",
    "Harassment or hate",
    "Inappropriate content",
    "Misinformation",
    "Something else"
)

/**
 * Store-compliance report dialog (Apple 1.2 / Play UGC): pick a reason,
 * submit. Caller wires [onSubmit] to UserRepository.report(...).
 */
@Composable
fun ReportDialog(
    title: String,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var selected by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HL.Card,
        title = { Text(title, color = HL.Ink, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                REPORT_REASONS.forEach { reason ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selected = reason }
                            .padding(vertical = 2.dp)
                    ) {
                        RadioButton(
                            selected = selected == reason,
                            onClick = { selected = reason },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = HL.Accent,
                                unselectedColor = HL.Muted
                            )
                        )
                        Text(reason, color = HL.Ink, fontSize = 14.sp)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = selected != null,
                onClick = { selected?.let(onSubmit); onDismiss() }
            ) { Text("Report", color = if (selected != null) HL.Accent else HL.Muted) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = HL.Muted) }
        }
    )
}
