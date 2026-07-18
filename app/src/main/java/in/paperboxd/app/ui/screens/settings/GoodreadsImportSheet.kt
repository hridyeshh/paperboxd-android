package `in`.paperboxd.app.ui.screens.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.theme.PBScript
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Goodreads CSV import, presented as a bottom sheet over Settings.
 * Twin of iOS GoodreadsImportView (idle → importing → finished).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoodreadsImportSheet(
    onDismiss: () -> Unit,
    viewModel: GoodreadsImportViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val phase by viewModel.phase.collectAsStateWithLifecycle()

    // Goodreads exports are `.csv`; some devices report them as text/plain or
    // give no type, so accept a permissive set and read the bytes ourselves.
    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        scope.launch {
            val text = withContext(Dispatchers.IO) {
                runCatching {
                    context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                }.getOrNull()?.decodeToString()
            }
            viewModel.runImport(text.orEmpty())
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = HL.Paper,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(24.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Import from Goodreads", fontFamily = PBScript, fontSize = 22.sp, color = HL.Ink)
                Spacer(Modifier.weight(1f))
                Text(
                    "Done",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = HL.Accent,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(HL.Card)
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            when (val p = phase) {
                is GoodreadsImportViewModel.Phase.Idle ->
                    IdleView(onChoose = {
                        picker.launch(arrayOf("text/csv", "text/comma-separated-values", "text/plain", "*/*"))
                    })

                is GoodreadsImportViewModel.Phase.Importing ->
                    ImportingView(done = p.done, total = p.total)

                is GoodreadsImportViewModel.Phase.Finished ->
                    FinishedView(imported = p.imported, skipped = p.skipped, total = p.total, onAnother = viewModel::reset)

                is GoodreadsImportViewModel.Phase.Error ->
                    ErrorView(message = p.message, onRetry = viewModel::reset)
            }
        }
    }
}

@Composable
private fun IdleView(onChoose: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            "Bring your Goodreads shelf",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = HL.Ink
        )
        Text(
            "On Goodreads, open My Books → Import and export → Export Library, then choose the CSV file here. We match each book to our catalogue and add it to your shelf with the right status.",
            fontSize = 14.sp,
            color = HL.Muted
        )
        Text(
            "READ · CURRENTLY READING · TO-READ ARE ALL KEPT",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 1.2.sp,
            color = HL.Muted
        )
        Spacer(Modifier.weight(1f))
        Text(
            "Choose CSV file",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = HL.Paper,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(HL.Ink)
                .clickable(onClick = onChoose)
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun ImportingView(done: Int, total: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(40.dp))
        CircularProgressIndicator(color = HL.Accent, strokeWidth = 2.dp, modifier = Modifier.size(32.dp))
        Text("Importing your books", fontFamily = FontFamily.Serif, fontSize = 18.sp, color = HL.Ink)
        Text("$done / $total", fontFamily = FontFamily.Monospace, fontSize = 13.sp, color = HL.Muted)
        Text("Keep this screen open", fontSize = 12.sp, color = HL.Muted)
    }
}

@Composable
private fun FinishedView(imported: Int, skipped: Int, total: Int, onAnother: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text(
            if (imported > 0) "Import complete" else "Nothing to add",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = HL.Ink
        )
        ResultRow("Added to shelf", imported)
        HorizontalDivider(color = HL.Ink.copy(alpha = 0.12f))
        ResultRow("Skipped (no match)", skipped)
        HorizontalDivider(color = HL.Ink.copy(alpha = 0.12f))
        ResultRow("Total in file", total)
        if (skipped > 0) {
            Text(
                "Skipped books weren’t found in our catalogue. You can add those by searching for them directly.",
                fontSize = 13.sp,
                color = HL.Muted
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            "Import another file",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 13.sp,
            color = HL.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(HL.Card)
                .clickable(onClick = onAnother)
                .padding(vertical = 14.dp)
        )
    }
}

@Composable
private fun ErrorView(message: String, onRetry: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Text("Import failed", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold, fontSize = 22.sp, color = HL.Ink)
        Text(message, fontSize = 14.sp, color = HL.Muted)
        Spacer(Modifier.weight(1f))
        Text(
            "Try again",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = HL.Paper,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(HL.Ink)
                .clickable(onClick = onRetry)
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
private fun ResultRow(label: String, value: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = 15.sp, color = HL.Ink)
        Spacer(Modifier.weight(1f))
        Text("$value", fontFamily = FontFamily.Serif, fontSize = 20.sp, color = HL.Ink)
    }
}
