package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Lock
import `in`.paperboxd.app.domain.model.ListWithBooksResponse
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalButton
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
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    "NEW LIST",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = HL.Muted
                )
                Text(
                    "What are you collecting?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = HL.Ink
                )
            }

            FormSection("The list") {
                StackedRow("Name", "${title.length}/50", over = title.length > 50) {
                    PlainListField(
                        value = title,
                        placeholder = "Books that ruined me",
                        serif = true,
                        onChange = { if (it.length <= 50) title = it }
                    )
                }
                RowDivider()
                StackedRow("Description", "${description.length}/200", over = description.length > 200) {
                    PlainListField(
                        value = description,
                        placeholder = "Optional",
                        serif = true,
                        italic = true,
                        singleLine = false,
                        onChange = { if (it.length <= 200) description = it }
                    )
                }
            }

            FormSection("Who can see it") {
                // The whole row is the target, not just the switch.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isPrivate = !isPrivate }
                        .padding(horizontal = 16.dp, vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            "Private",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HL.Ink
                        )
                        Text(
                            if (isPrivate) "Only you, and anyone you grant access."
                            else "Anyone can see this list on your profile.",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            lineHeight = 15.sp,
                            color = HL.Muted
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = isPrivate,
                        onCheckedChange = { isPrivate = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = HL.Card,
                            checkedTrackColor = HL.Ink,
                            checkedBorderColor = HL.Ink,
                            uncheckedThumbColor = HL.Muted,
                            uncheckedTrackColor = HL.Paper2,
                            uncheckedBorderColor = HL.Muted.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            error?.let {
                Text(it, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = HL.Accent)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .brutalButton(onClick = onDismiss, fill = HL.Card, borderWidth = 2.dp, offset = 3.dp)
                        .height(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Cancel", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = HL.Ink)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .alpha(if (canSave && !saving) 1f else 0.45f)
                        .brutalButton(
                            onClick = onClick@{
                                if (!canSave || saving) return@onClick
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
                            fill = HL.Ink,
                            borderWidth = 2.dp,
                            offset = 3.dp
                        )
                        .height(38.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (saving) "Creating…" else "Create list",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HL.Cream
                    )
                }
            }
        }
    }
}

// ── Form chrome ──────────────────────────────────────────────────────────────
//
// Same anatomy as EditProfileScreen — mono eyebrow over a Card surface, stacked
// rows with a mono field label — so the two forms read as one app rather than
// one bespoke sheet and one stack of stock Material controls.

private val FormLine = Color(0xFFE6DFD0)

/** Twin of iOS `EmptyTabState`'s copy treatment: serif italic, muted, centred. */
@Composable
private fun ListSheetEmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            color = HL.Muted
        )
    }
}

@Composable
private fun FormSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            title.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.5.sp,
            letterSpacing = 2.sp,
            color = HL.Muted.copy(alpha = 0.7f),
            modifier = Modifier.padding(start = 6.dp)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(HL.Card)
                .border(1.dp, FormLine.copy(alpha = 0.55f), RoundedCornerShape(16.dp)),
            content = content
        )
    }
}

@Composable
private fun RowDivider() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
            .height(1.dp)
            .background(FormLine.copy(alpha = 0.45f))
    )
}

@Composable
private fun StackedRow(
    label: String,
    counter: String,
    over: Boolean,
    control: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                label.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 9.5.sp,
                letterSpacing = 1.5.sp,
                color = HL.Muted
            )
            Text(
                counter,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.5.sp,
                color = if (over) HL.Accent else HL.Muted.copy(alpha = 0.7f)
            )
        }
        control()
    }
}

@Composable
private fun PlainListField(
    value: String,
    placeholder: String,
    onChange: (String) -> Unit,
    serif: Boolean = false,
    italic: Boolean = false,
    singleLine: Boolean = true
) {
    val style = TextStyle(
        fontSize = if (serif) 16.sp else 15.sp,
        color = HL.Ink,
        fontFamily = if (serif) FontFamily.Serif else FontFamily.Default,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal
    )
    BasicTextField(
        value = value,
        onValueChange = onChange,
        singleLine = singleLine,
        textStyle = style,
        cursorBrush = SolidColor(HL.Ink),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (singleLine) Modifier else Modifier.heightIn(min = 46.dp)),
        decorationBox = { inner ->
            Box(contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart) {
                if (value.isEmpty()) {
                    Text(placeholder, style = style.copy(color = HL.Muted))
                }
                inner()
            }
        }
    )
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
                // Eyebrow over serif title over serif-italic body — the same
                // voice the profile header uses. The eyebrow carries the private
                // state, so the lock glyph by the count is gone.
                Text(
                    if (d.isPrivate) "PRIVATE LIST" else "LIST",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 2.sp,
                    color = HL.Muted
                )
                Text(
                    d.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    color = HL.Ink,
                    modifier = Modifier.padding(top = 6.dp)
                )
                if (!d.description.isNullOrEmpty()) {
                    Text(
                        d.description,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 15.sp,
                        lineHeight = 21.sp,
                        color = HL.Ink.copy(alpha = 0.78f),
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }
                Text(
                    "${d.bookCount} ${if (d.bookCount == 1L) "book" else "books"}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 1.6.sp,
                    color = HL.Muted,
                    modifier = Modifier.padding(top = 14.dp)
                )
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                        .height(1.dp)
                        .background(FormLine)
                )

                if (d.books.isEmpty()) {
                    ListSheetEmptyState("No books in this list yet")
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
                ListSheetEmptyState("Couldn't load this list")
            }
        }
    }
}
