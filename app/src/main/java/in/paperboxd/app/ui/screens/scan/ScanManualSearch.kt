package `in`.paperboxd.app.ui.screens.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

/**
 * "Type the title instead" — iOS `ManualSearchSheet` twin. Searches
 * `/books/search` and, on tap, feeds the chosen book's ISBN into the scan
 * analysis (the backend scores by ISBN).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualSearchSheet(
    search: suspend (String) -> List<ScanSearchHit>,
    onDismiss: () -> Unit,
    onPick: (String, String?) -> Unit
) {
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var hits by remember { mutableStateOf<List<ScanSearchHit>>(emptyList()) }
    var loading by remember { mutableStateOf(false) }
    var searched by remember { mutableStateOf(false) }
    var noIsbnNote by remember { mutableStateOf(false) }

    fun run() {
        val q = query.trim()
        if (q.isEmpty()) return
        loading = true
        noIsbnNote = false
        scope.launch {
            hits = runCatching { search(q) }.getOrDefault(emptyList())
            loading = false
            searched = true
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = SK.bg,
        dragHandle = null
    ) {
        Column(Modifier.fillMaxWidth()) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 12.dp)
            ) {
                Text(
                    "Find your book",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = SK.ink
                )
                Spacer(Modifier.weight(1f))
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(32.dp).clickable(onClick = onDismiss)
                ) {
                    Icon(
                        Icons.Outlined.Close, contentDescription = "Close",
                        tint = SK.sub, modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Field
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .background(SK.panel)
                    .border(1.5.dp, SK.line, RectangleShape)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Icon(
                    Icons.Outlined.Search, contentDescription = null,
                    tint = SK.sub, modifier = Modifier.size(14.dp)
                )
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(fontSize = 15.sp, color = SK.ink),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { run() }),
                    decorationBox = { inner ->
                        Box {
                            if (query.isEmpty()) {
                                Text("Title, author or ISBN", fontSize = 15.sp, color = SK.faint)
                            }
                            inner()
                        }
                    },
                    modifier = Modifier.weight(1f)
                )
                if (loading) {
                    CircularProgressIndicator(
                        color = SK.sub, strokeWidth = 1.5.dp, modifier = Modifier.size(14.dp)
                    )
                }
            }

            if (noIsbnNote) {
                Note("No ISBN for that edition — try another result.")
            }
            if (searched && hits.isEmpty() && !loading) {
                Note("Nothing found. Try a different search.")
            }

            LazyColumn(Modifier.fillMaxWidth().weight(1f, fill = false)) {
                items(hits, key = { it.id }) { hit ->
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .alpha(if (hit.isbn == null) 0.55f else 1f)
                                .clickable {
                                    val isbn = hit.isbn
                                    if (isbn == null) {
                                        noIsbnNote = true
                                    } else {
                                        onPick(isbn, hit.title)
                                    }
                                }
                                .padding(horizontal = 20.dp, vertical = 12.dp)
                        ) {
                            Box(
                                Modifier
                                    .size(34.dp, 51.dp)
                                    .background(SK.coverGradient)
                                    .border(1.dp, SK.line)
                            ) {
                                if (hit.coverUrl != null) {
                                    AsyncImage(
                                        model = hit.coverUrl,
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            Column(
                                verticalArrangement = Arrangement.spacedBy(3.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    hit.title, fontFamily = FontFamily.Serif, fontSize = 15.sp,
                                    color = SK.ink, maxLines = 2
                                )
                                Text(hit.author, fontSize = 12.sp, color = SK.sub, maxLines = 1)
                            }
                            Icon(
                                Icons.AutoMirrored.Outlined.ArrowForward,
                                contentDescription = null,
                                tint = if (hit.isbn == null) SK.faint else SK.ink,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Hairline(Modifier.padding(start = 20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Note(text: String) {
    Text(
        text,
        fontFamily = FontFamily.Monospace,
        fontSize = 11.sp,
        color = SK.sub,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp)
    )
}
