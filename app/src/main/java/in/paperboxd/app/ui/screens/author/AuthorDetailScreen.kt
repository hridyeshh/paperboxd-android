package `in`.paperboxd.app.ui.screens.author

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.domain.model.AuthorInfo
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val EXTRACT_LIMIT = 420

data class AuthorDetailUiState(
    val name: String = "",
    val info: AuthorInfo? = null,
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class AuthorDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val bookRepository: BookRepository
) : ViewModel() {

    private val name: String = savedStateHandle.get<String>("name").orEmpty()

    private val _state = MutableStateFlow(AuthorDetailUiState(name = name))
    val state: StateFlow<AuthorDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val infoTask = async { bookRepository.authorInfo(name).getOrNull() }
            val booksTask = async { bookRepository.booksByAuthor(name).getOrNull() }
            val info = infoTask.await()
            _state.update {
                it.copy(
                    info = info?.takeIf { i -> i.found },
                    books = booksTask.await()?.items.orEmpty(),
                    isLoading = false
                )
            }
        }
    }
}

/**
 * Author page: portrait, Wikipedia blurb, and the books PaperBoxd holds by them.
 * Mirrors the web `/authors/[authorName]` route and the iOS AuthorDetailView.
 */
@Composable
fun AuthorDetailScreen(
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    viewModel: AuthorDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    AuthorDetailContent(state = state, onBack = onBack, onOpenBook = onOpenBook)
}

@Composable
private fun AuthorDetailContent(
    state: AuthorDetailUiState,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit
) {
    val displayName = state.info?.name?.takeIf { it.isNotEmpty() } ?: state.name

    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = HL.Ink)
            }
            Text(
                displayName,
                fontFamily = FontFamily.Serif,
                fontSize = 16.sp,
                color = HL.Ink,
                maxLines = 1
            )
        }

        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                CircularProgressIndicator(color = HL.Ink, modifier = Modifier.padding(top = 80.dp))
            }
            return@Column
        }

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            item { AuthorHeader(state, displayName) }
            if (state.books.isEmpty()) {
                item {
                    Text(
                        "No books by $displayName yet",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        color = HL.Muted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 56.dp)
                    )
                }
            } else {
                item {
                    Text(
                        "BOOKS",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        color = HL.Muted,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
                items(state.books.chunked(3)) { row ->
                    BookCoverRow(row, onOpenBook)
                }
            }
            item { Spacer(Modifier.height(40.dp)) }
        }
    }
}

@Composable
private fun AuthorHeader(state: AuthorDetailUiState, displayName: String) {
    var expanded by remember { mutableStateOf(false) }
    val extract = state.info?.extract.orEmpty()

    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            BookCoverImage(
                url = state.info?.photoUrl?.takeIf { it.isNotEmpty() },
                title = displayName,
                modifier = Modifier.width(92.dp).aspectRatio(2f / 3f),
                cornerRadius = 8.dp
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    displayName,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    color = HL.Ink
                )
                state.info?.description?.takeIf { it.isNotEmpty() }?.let {
                    Text(
                        it,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        color = HL.Muted
                    )
                }
                Text(
                    "${state.books.size} book" + (if (state.books.size == 1) "" else "s") + " on PaperBoxd",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = HL.Muted
                )
            }
        }

        if (extract.isNotEmpty()) {
            val shown = if (expanded || extract.length <= EXTRACT_LIMIT) {
                extract
            } else {
                extract.take(EXTRACT_LIMIT).trimEnd() + "…"
            }
            Text(
                shown,
                fontFamily = FontFamily.Serif,
                fontSize = 14.sp,
                color = HL.Ink.copy(alpha = 0.85f)
            )
            if (extract.length > EXTRACT_LIMIT) {
                TextButton(onClick = { expanded = !expanded }) {
                    Text(
                        if (expanded) "Show less" else "Read more",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = HL.Muted
                    )
                }
            }
        }
    }
}

@Composable
private fun BookCoverRow(row: List<Book>, onOpenBook: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        row.forEach { book ->
            BookCoverImage(
                url = book.coverUrl,
                title = book.title,
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(2f / 3f)
                    .clickable { onOpenBook(book.id) },
                cornerRadius = 6.dp
            )
        }
        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
    }
}
