package `in`.paperboxd.app.ui.screens.write

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.theme.PBScript

private val Line = Color(0xFFE6DFD0)

/**
 * Compose sheet — iOS WriteView twin on the light paper palette (all PaperBoxd
 * screens are light). Quiet editor; the brutalist signatures are the attached
 * book card, the attach button, and the accent shadow under an active Post.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    user: User,
    onDismiss: () -> Unit,
    viewModel: WriteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDiscard by remember { mutableStateOf(false) }
    var showBookSearch by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(state.didSubmit) { if (state.didSubmit) onDismiss() }

    val handleCancel = {
        if (state.isDirty) showDiscard = true else onDismiss()
    }
    BackHandler(onBack = handleCancel)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HL.Paper)
            .statusBarsPadding()
            .imePadding()
    ) {
        NavBar(
            canSubmit = state.canSubmit,
            isLoading = state.isLoading,
            onCancel = handleCancel,
            onPost = { viewModel.submit(user.username ?: "") }
        )
        Box(Modifier.fillMaxWidth().height(1.dp).background(Line))

        state.errorMessage?.let {
            Text(
                it,
                fontSize = 12.sp,
                color = HL.Accent,
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp)
            )
        }

        if (state.selectedBook != null) {
            AttachedBookCard(
                book = state.selectedBook!!,
                rating = state.rating,
                onRate = viewModel::onRate,
                onRemove = { viewModel.selectBook(null) }
            )
        } else {
            AttachButton(
                open = showBookSearch,
                onClick = { showBookSearch = !showBookSearch }
            )
        }

        if (showBookSearch && state.selectedBook == null) {
            BookSearchPanel(
                query = state.bookSearchQuery,
                results = state.bookSearchResults,
                searching = state.isSearchingBooks,
                onQueryChange = viewModel::onSearchChange,
                onPick = {
                    viewModel.selectBook(it)
                    showBookSearch = false
                }
            )
        }

        Editor(
            content = state.content,
            onChange = viewModel::onContentChange,
            modifier = Modifier.weight(1f)
        )

        BottomToolbar(
            rating = state.rating,
            charCount = state.charCount,
            datePickerOpen = showDatePicker,
            onCalendar = { showDatePicker = true },
            onStar = { viewModel.onRate(((state.rating ?: 0) % 5) + 1) }
        )
    }

    if (showDiscard) {
        AlertDialog(
            onDismissRequest = { showDiscard = false },
            containerColor = HL.Card,
            title = { Text("Discard entry?", fontFamily = FontFamily.Serif, color = HL.Ink) },
            text = { Text("Your entry will not be saved.", color = HL.Muted) },
            confirmButton = {
                TextButton(onClick = onDismiss) { Text("Discard", color = HL.Accent) }
            },
            dismissButton = {
                TextButton(onClick = { showDiscard = false }) { Text("Keep Editing", color = HL.Ink) }
            }
        )
    }

    if (showDatePicker) {
        val dpState = rememberDatePickerState(initialSelectedDateMillis = state.readingDateMillis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dpState.selectedDateMillis?.let(viewModel::onDateChange)
                    showDatePicker = false
                }) { Text("OK", color = HL.Ink) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = HL.Muted) }
            }
        ) {
            DatePicker(state = dpState)
        }
    }
}

// ---- Nav bar: Cancel · wordmark · Post ----

@Composable
private fun NavBar(
    canSubmit: Boolean,
    isLoading: Boolean,
    onCancel: () -> Unit,
    onPost: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "Cancel",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = HL.Muted,
            modifier = Modifier.clickable(onClick = onCancel)
        )
        Spacer(Modifier.weight(1f))
        Text(
            "PaperBoxd",
            fontFamily = PBScript,
            fontSize = 20.sp,
            color = HL.Ink
        )
        Spacer(Modifier.weight(1f))
        if (isLoading) {
            CircularProgressIndicator(
                color = HL.Ink,
                strokeWidth = 2.dp,
                modifier = Modifier.size(20.dp)
            )
        } else {
            Text(
                "Post",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (canSubmit) HL.Paper else HL.Muted,
                modifier = Modifier
                    .drawBehind {
                        // hard accent offset shadow only when active
                        if (canSubmit) {
                            drawRoundRect(
                                color = HL.Accent,
                                topLeft = Offset(3.dp.toPx(), 3.dp.toPx()),
                                cornerRadius = CornerRadius(size.height / 2)
                            )
                        }
                    }
                    .clip(CircleShape)
                    .background(if (canSubmit) HL.Ink else HL.Muted.copy(alpha = 0.2f))
                    .clickable(enabled = canSubmit, onClick = onPost)
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }
    }
}

// ---- Book attachment ----

@Composable
private fun AttachedBookCard(
    book: Book,
    rating: Int?,
    onRate: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 22.dp, top = 16.dp, bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .brutalPlate(offset = 4.dp)
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(13.dp)
        ) {
            BookCoverImage(
                url = book.coverUrl,
                title = book.title,
                modifier = Modifier.width(50.dp).aspectRatio(2f / 3f),
                cornerRadius = 2.dp
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    book.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    color = HL.Ink,
                    maxLines = 2
                )
                if (book.authorLine.isNotEmpty()) {
                    Text(book.authorLine, fontSize = 12.sp, color = HL.Muted, maxLines = 1)
                }
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    for (i in 1..5) {
                        Icon(
                            if ((rating ?: 0) >= i) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "Rate $i",
                            tint = HL.Ink,
                            modifier = Modifier.size(17.dp).clickable { onRate(i) }
                        )
                    }
                }
            }
        }
        // remove chip riding the top-right corner
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-8).dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(HL.Ink)
                .border(1.5.dp, HL.Paper, CircleShape)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Outlined.Close,
                contentDescription = "Remove book",
                tint = HL.Paper,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

@Composable
private fun AttachButton(open: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 18.dp, end = 21.dp, top = 14.dp, bottom = 10.dp)
            .brutalPlate(offset = 3.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            if (open) Icons.Outlined.Close else Icons.Outlined.Add,
            contentDescription = null,
            tint = HL.Ink,
            modifier = Modifier.size(15.dp)
        )
        Text(
            if (open) "Close search" else "Attach a book",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = HL.Ink
        )
        Spacer(Modifier.weight(1f))
        Icon(
            Icons.Outlined.MenuBook,
            contentDescription = null,
            tint = HL.Ink,
            modifier = Modifier.size(15.dp)
        )
    }
}

// ---- Book search panel ----

@Composable
private fun BookSearchPanel(
    query: String,
    results: List<Book>,
    searching: Boolean,
    onQueryChange: (String) -> Unit,
    onPick: (Book) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(Line, topLeft = Offset(0f, size.height - 1.dp.toPx()))
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 10.dp, bottom = 6.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(HL.Card)
                .border(1.dp, Line, RoundedCornerShape(8.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                Icons.Outlined.Search,
                contentDescription = null,
                tint = HL.Muted,
                modifier = Modifier.size(14.dp)
            )
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(fontSize = 14.sp, color = HL.Ink),
                cursorBrush = SolidColor(HL.Accent),
                decorationBox = { inner ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (query.isEmpty()) Text("Search books...", fontSize = 14.sp, color = HL.Muted)
                        inner()
                    }
                },
                modifier = Modifier.weight(1f)
            )
            if (searching) {
                CircularProgressIndicator(
                    color = HL.Muted,
                    strokeWidth = 1.5.dp,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        if (results.isNotEmpty()) {
            LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                items(results, key = { it.id }) { book ->
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onPick(book) }
                                .padding(horizontal = 18.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            BookCoverImage(
                                url = book.coverUrl,
                                title = book.title,
                                modifier = Modifier.width(28.dp).aspectRatio(2f / 3f),
                                cornerRadius = 3.dp
                            )
                            Column {
                                Text(
                                    book.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HL.Ink,
                                    maxLines = 1
                                )
                                if (book.authorLine.isNotEmpty()) {
                                    Text(book.authorLine, fontSize = 11.sp, color = HL.Muted, maxLines = 1)
                                }
                            }
                        }
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 56.dp)
                                .height(1.dp)
                                .background(Line)
                        )
                    }
                }
            }
        }
    }
}

// ---- Editor ----

@Composable
private fun Editor(content: String, onChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        BasicTextField(
            value = content,
            onValueChange = onChange,
            textStyle = TextStyle(
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = HL.Ink
            ),
            cursorBrush = SolidColor(HL.Accent),
            decorationBox = { inner ->
                Box {
                    if (content.isEmpty()) {
                        Text(
                            "What are you reading?",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 16.sp,
                            color = HL.Muted.copy(alpha = 0.6f)
                        )
                    }
                    inner()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 220.dp)
                .padding(horizontal = 18.dp)
                .padding(top = 14.dp, bottom = 14.dp)
        )
    }
}

// ---- Bottom toolbar ----

@Composable
private fun BottomToolbar(
    rating: Int?,
    charCount: Int,
    datePickerOpen: Boolean,
    onCalendar: () -> Unit,
    onStar: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind { drawRect(Line, size = Size(size.width, 1.dp.toPx())) }
            .background(HL.Paper.copy(alpha = 0.97f))
            .padding(horizontal = 18.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Icon(
            Icons.Outlined.CalendarMonth,
            contentDescription = "Reading date",
            tint = if (datePickerOpen) HL.Accent else HL.Muted,
            modifier = Modifier.size(20.dp).clickable(onClick = onCalendar)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.clickable(onClick = onStar)
        ) {
            Icon(
                if (rating != null) Icons.Filled.Star else Icons.Outlined.StarOutline,
                contentDescription = "Rating",
                tint = if (rating != null) HL.Accent else HL.Muted,
                modifier = Modifier.size(20.dp)
            )
            rating?.let {
                Text(
                    "$it",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = HL.Accent
                )
            }
        }
        Spacer(Modifier.weight(1f))
        Text(
            "$charCount chars",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            color = if (charCount >= 10) HL.Accent else HL.Muted.copy(alpha = 0.5f)
        )
    }
}
