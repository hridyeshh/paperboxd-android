package `in`.paperboxd.app.ui.screens.write

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.RatingPicker
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import androidx.compose.ui.tooling.preview.Preview
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme
import `in`.paperboxd.app.domain.model.Book

@Composable
fun WriteScreen(
    user: User,
    onDismiss: () -> Unit,
    viewModel: WriteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.didSubmit) { if (state.didSubmit) onDismiss() }

    WriteContent(
        state = state,
        onDismiss = onDismiss,
        onPost = { viewModel.submit(user.username.orEmpty()) },
        onContentChange = viewModel::onContentChange,
        onRate = viewModel::onRate,
        onSearchChange = viewModel::onSearchChange,
        onSelectBook = viewModel::selectBook
    )
}

@Composable
fun WriteContent(
    state: WriteUiState,
    onDismiss: () -> Unit,
    onPost: () -> Unit,
    onContentChange: (String) -> Unit,
    onRate: (Int) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectBook: (Book?) -> Unit
) {
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }

    val requestClose: () -> Unit = {
        if (state.isDirty) showDiscardDialog = true else onDismiss()
    }
    BackHandler(onBack = requestClose)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .systemBarsPadding()
            .imePadding()
    ) {
        // Top bar: Cancel | wordmark | Post
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = requestClose) {
                Text(stringResource(R.string.write_cancel), color = TextSecondary)
            }
            Spacer(Modifier.weight(1f))
            Text(
                stringResource(R.string.app_name),
                fontFamily = FontFamily.Cursive,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                fontSize = 22.sp,
                color = Accent
            )
            Spacer(Modifier.weight(1f))
            TextButton(
                onClick = onPost,
                enabled = state.canSubmit
            ) {
                Text(
                    stringResource(R.string.write_post),
                    color = if (state.canSubmit) Accent else TextSecondary.copy(alpha = 0.5f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            // Book attachment
            val book = state.selectedBook
            if (book != null) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Surface)
                        .padding(10.dp)
                ) {
                    BookCoverImage(
                        url = book.coverUrl,
                        title = book.title,
                        modifier = Modifier.width(34.dp).aspectRatio(2f / 3f),
                        cornerRadius = 3.dp
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(book.title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, maxLines = 1)
                        Text(book.authorLine, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                    }
                    IconButton(onClick = { onSelectBook(null) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = TextSecondary)
                    }
                }
            } else {
                BookAttachSearch(state, onSearchChange, onSelectBook)
            }

            Spacer(Modifier.height(14.dp))

            // Body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp)
            ) {
                if (state.content.isEmpty()) {
                    Text(
                        stringResource(R.string.write_placeholder),
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary
                    )
                }
                BasicTextField(
                    value = state.content,
                    onValueChange = onContentChange,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                    cursorBrush = SolidColor(Accent),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 200.dp)
                )
            }

            state.errorMessage?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = ErrorColor)
            }

            Spacer(Modifier.height(14.dp))

            // Bottom bar: rating + char count
            Row(verticalAlignment = Alignment.CenterVertically) {
                RatingPicker(rating = state.rating ?: 0, onRate = onRate, starSize = 22.dp)
                Spacer(Modifier.weight(1f))
                Text(
                    stringResource(R.string.write_chars, state.charCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (state.charCount >= 10) TextSecondary else ErrorColor
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            containerColor = Surface,
            title = { Text(stringResource(R.string.write_discard_title), color = TextPrimary) },
            text = { Text(stringResource(R.string.write_discard_body), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.write_discard_confirm), color = ErrorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(stringResource(R.string.write_keep_editing), color = TextPrimary)
                }
            }
        )
    }
}

@Composable
private fun BookAttachSearch(
    state: WriteUiState,
    onSearchChange: (String) -> Unit,
    onSelectBook: (Book) -> Unit
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Surface)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (state.bookSearchQuery.isEmpty()) {
                    Text(
                        stringResource(R.string.write_attach_book),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                BasicTextField(
                    value = state.bookSearchQuery,
                    onValueChange = onSearchChange,
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(color = TextPrimary),
                    cursorBrush = SolidColor(Accent),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        state.bookSearchResults.forEach { result ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectBook(result) }
                    .padding(vertical = 6.dp)
            ) {
                BookCoverImage(
                    url = result.coverUrl,
                    title = result.title,
                    modifier = Modifier.width(30.dp).aspectRatio(2f / 3f),
                    cornerRadius = 3.dp
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(result.title, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, maxLines = 1)
                    Text(result.authorLine, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
                }
            }
        }
    }
}

@Preview
@Composable
private fun WritePreview() {
    PaperBoxdTheme {
        WriteContent(
            state = WriteUiState(content = "This is a preview diary entry."),
            onDismiss = {},
            onPost = {},
            onContentChange = {},
            onRate = {},
            onSearchChange = {},
            onSelectBook = {}
        )
    }
}
