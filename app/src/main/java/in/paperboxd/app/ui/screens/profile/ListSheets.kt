package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import `in`.paperboxd.app.domain.model.ListWithBooksResponse
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import kotlinx.coroutines.launch

/** Bottom sheet: create a new list (public or private). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListDialog(
    onCreate: suspend (title: String, description: String?, isPrivate: Boolean) -> Boolean,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    val canSave = title.trim().isNotEmpty() && title.length <= 50 && description.length <= 200

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = HL.Paper) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                "New list",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                color = HL.Ink
            )
            OutlinedTextField(
                value = title,
                onValueChange = { if (it.length <= 50) title = it },
                label = { Text("List name") },
                singleLine = true,
                supportingText = { Text("${title.length}/50", fontFamily = FontFamily.Monospace, fontSize = 10.sp) },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { if (it.length <= 200) description = it },
                label = { Text("Description (optional)") },
                minLines = 2,
                maxLines = 4,
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Private", fontFamily = FontFamily.Serif, fontSize = 15.sp, color = HL.Ink)
                    Text(
                        if (isPrivate) "Only you and people you grant access can see it."
                        else "Anyone can see this list on your profile.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = HL.Muted
                    )
                }
                Switch(checked = isPrivate, onCheckedChange = { isPrivate = it })
            }
            error?.let { Text(it, color = Color.Red, fontSize = 13.sp) }
            Button(
                onClick = {
                    saving = true
                    error = null
                    scope.launch {
                        val ok = onCreate(
                            title.trim(),
                            description.trim().ifEmpty { null },
                            isPrivate
                        )
                        saving = false
                        if (ok) onDismiss() else error = "Couldn't create the list. Try again."
                    }
                },
                enabled = canSave && !saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (saving) "Creating…" else "Create list")
            }
        }
    }
}

/** Bottom sheet: open a list and show its books. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListDetailSheet(
    listId: String,
    onLoadDetail: suspend (String) -> ListWithBooksResponse?,
    onOpenBook: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var detail by remember { mutableStateOf<ListWithBooksResponse?>(null) }
    var loading by remember { mutableStateOf(true) }

    LaunchedEffect(listId) {
        detail = onLoadDetail(listId)
        loading = false
    }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = HL.Paper) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 560.dp)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val d = detail
            if (loading && d == null) {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp)
                }
            } else if (d != null) {
                Text(d.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp, color = HL.Ink)
                if (!d.description.isNullOrEmpty()) {
                    Text(d.description, fontFamily = FontFamily.Serif, fontSize = 14.sp, color = HL.Muted)
                }
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (d.isPrivate) {
                        Icon(Icons.Outlined.Lock, contentDescription = null, tint = HL.Muted, modifier = Modifier.width(10.dp))
                    }
                    Text("${d.bookCount} books", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = HL.Muted)
                }
                if (d.books.isEmpty()) {
                    Text("No books in this list yet", fontFamily = FontFamily.Serif, fontSize = 14.sp, color = HL.Muted, modifier = Modifier.padding(top = 24.dp))
                } else {
                    d.books.chunked(3).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            row.forEach { book ->
                                BookCoverImage(
                                    url = book.coverUrl,
                                    title = book.title,
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(2f / 3f)
                                        .clickable { onOpenBook(book.id) }
                                )
                            }
                            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            } else {
                Text("Couldn't load this list", fontFamily = FontFamily.Serif, fontSize = 14.sp, color = HL.Muted, modifier = Modifier.padding(vertical = 24.dp))
            }
        }
    }
}
