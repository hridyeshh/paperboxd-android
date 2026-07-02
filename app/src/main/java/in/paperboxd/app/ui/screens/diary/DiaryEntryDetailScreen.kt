package `in`.paperboxd.app.ui.screens.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.FavoriteBorder
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.data.repository.DiaryRepository
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.RatingPicker
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.LikeRed
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiaryDetailUiState(
    val entry: DiaryEntry? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isLiked: Boolean = false,
    val likesCount: Long = 0,
    val isDeleting: Boolean = false,
    val deleted: Boolean = false
)

@HiltViewModel
class DiaryEntryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val username: String = savedStateHandle.get<String>("username").orEmpty()
    private val entryId: String = savedStateHandle.get<String>("entryId").orEmpty()

    private val _state = MutableStateFlow(DiaryDetailUiState())
    val state: StateFlow<DiaryDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            diaryRepository.entry(username, entryId).fold(
                onSuccess = { entry ->
                    _state.update {
                        it.copy(entry = entry, isLiked = entry.isLiked, likesCount = entry.likesCount, isLoading = false)
                    }
                },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message, isLoading = false) } }
            )
        }
    }

    fun toggleLike() {
        val prev = _state.value
        _state.update {
            it.copy(
                isLiked = !prev.isLiked,
                likesCount = prev.likesCount + if (prev.isLiked) -1 else 1
            )
        }
        viewModelScope.launch {
            val result = if (prev.isLiked) diaryRepository.unlike(username, entryId)
            else diaryRepository.like(username, entryId)
            result.onFailure {
                _state.update { it.copy(isLiked = prev.isLiked, likesCount = prev.likesCount) }
            }
        }
    }

    fun delete() {
        viewModelScope.launch {
            _state.update { it.copy(isDeleting = true) }
            diaryRepository.delete(username, entryId).fold(
                onSuccess = { _state.update { it.copy(deleted = true) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            _state.update { it.copy(isDeleting = false) }
        }
    }
}

@Composable
fun DiaryEntryDetailScreen(
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    viewModel: DiaryEntryDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(state.deleted) { if (state.deleted) onBack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = stringResource(R.string.detail_back),
                    tint = TextPrimary
                )
            }
            Spacer(Modifier.weight(1f))
            if (state.entry?.canEdit == true) {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        Icons.Outlined.Delete,
                        contentDescription = stringResource(R.string.diary_delete),
                        tint = ErrorColor
                    )
                }
            }
        }

        state.errorMessage?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodyMedium,
                color = ErrorColor,
                modifier = Modifier.padding(20.dp)
            )
        }

        state.entry?.let { entry ->
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                entry.book?.let { book ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = entry.bookId != null) {
                                entry.bookId?.let(onOpenBook)
                            }
                    ) {
                        BookCoverImage(
                            url = book.coverUrl,
                            title = book.title,
                            modifier = Modifier.width(56.dp).aspectRatio(2f / 3f),
                            cornerRadius = 5.dp
                        )
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(book.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(book.authorLine, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                entry.rating?.let {
                    RatingPicker(rating = it, starSize = 18.dp)
                    Spacer(Modifier.height(10.dp))
                }

                Text(
                    entry.content.replace(Regex("<[^>]+>"), " ").trim(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
                Spacer(Modifier.height(14.dp))
                Text(
                    entry.createdAt.take(10),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(14.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = viewModel::toggleLike) {
                        Icon(
                            if (state.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.detail_like),
                            tint = if (state.isLiked) LikeRed else TextSecondary
                        )
                    }
                    Text(
                        state.likesCount.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
                Spacer(Modifier.height(40.dp))
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Surface,
            title = { Text(stringResource(R.string.diary_delete_title), color = TextPrimary) },
            text = { Text(stringResource(R.string.diary_delete_body), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.delete()
                }) {
                    Text(stringResource(R.string.diary_delete), color = ErrorColor)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.write_cancel), color = TextPrimary)
                }
            }
        )
    }
}
