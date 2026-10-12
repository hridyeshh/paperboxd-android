package `in`.paperboxd.app.ui.screens.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.data.repository.DiaryRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.domain.model.VolumeInfo
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.ReportDialog
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DiaryDetailUiState(
    /** The first thought, then its follow-ups in writing order. */
    val thread: List<DiaryEntry> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val isPosting: Boolean = false,
    /** The first thought was deleted — the whole thread is gone. */
    val deleted: Boolean = false,
    val notice: String? = null
) {
    val root: DiaryEntry? get() = thread.firstOrNull()
    /** `can_edit` is true only for the author, so it doubles as "this is my thread". */
    val isOwnThread: Boolean get() = root?.canEdit == true
}

@HiltViewModel
class DiaryEntryDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val diaryRepository: DiaryRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    /** The thought's author — the thread route sits behind their profile-privacy gate. */
    private val username: String = savedStateHandle.get<String>("username").orEmpty()
    private val entryId: String = savedStateHandle.get<String>("entryId").orEmpty()
    val compose: Boolean = savedStateHandle.get<Boolean>("compose") ?: false

    private val _state = MutableStateFlow(DiaryDetailUiState())
    val state: StateFlow<DiaryDetailUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            diaryRepository.thread(username, entryId).fold(
                onSuccess = { thread -> _state.update { it.copy(thread = thread, isLoading = false) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message, isLoading = false) } }
            )
        }
    }

    fun toggleLike(entry: DiaryEntry) =
        mutate(entry, entry.withLike(!entry.isLiked)) { diaryRepository.toggleLike(entry) }

    fun toggleRepost(entry: DiaryEntry) =
        mutate(entry, entry.withRepost(!entry.isReposted)) { diaryRepository.toggleRepost(entry) }

    private fun mutate(entry: DiaryEntry, optimistic: DiaryEntry, call: suspend () -> Result<DiaryEntry>) {
        replace(optimistic)
        viewModelScope.launch {
            call().fold(
                onSuccess = ::replace,
                onFailure = { e ->
                    replace(entry)
                    _state.update { it.copy(notice = e.message ?: "Couldn't update") }
                }
            )
        }
    }

    private fun replace(t: DiaryEntry) =
        _state.update { s -> s.copy(thread = s.thread.map { if (it.id == t.id) t else it }) }

    /** Adds [content] to the end of the thread. Returns true when it posted. */
    suspend fun post(content: String): Boolean {
        val last = _state.value.thread.lastOrNull() ?: return false
        _state.update { it.copy(isPosting = true) }
        return diaryRepository.continueThread(last, content.trim()).fold(
            onSuccess = { created ->
                _state.update { it.copy(thread = it.thread + created, isPosting = false) }
                true
            },
            onFailure = { e ->
                _state.update { it.copy(isPosting = false, notice = e.message ?: "Couldn't add to thread") }
                false
            }
        )
    }

    fun report(entry: DiaryEntry, reason: String) {
        viewModelScope.launch {
            userRepository.report("thought", entry.id, reason).fold(
                onSuccess = { _state.update { it.copy(notice = "Report received — we review within 24 hours") } },
                onFailure = { e -> _state.update { it.copy(notice = e.message ?: "Couldn't send report") } }
            )
        }
    }

    fun delete(entry: DiaryEntry) {
        viewModelScope.launch {
            diaryRepository.delete(entry.username, entry.id).fold(
                onSuccess = {
                    if (entry.id == _state.value.root?.id) _state.update { it.copy(deleted = true) }
                    else _state.update { s -> s.copy(thread = s.thread.filterNot { it.id == entry.id }) }
                },
                onFailure = { e -> _state.update { it.copy(notice = e.message ?: "Couldn't delete") } }
            )
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
    LaunchedEffect(state.deleted) { if (state.deleted) onBack() }
    val scope = rememberCoroutineScope()

    DiaryEntryDetailContent(
        state = state,
        focusComposer = viewModel.compose,
        onBack = onBack,
        onOpenBook = onOpenBook,
        onToggleLike = viewModel::toggleLike,
        onToggleRepost = viewModel::toggleRepost,
        onPost = { text, done -> scope.launch { done(viewModel.post(text)) } },
        onDelete = viewModel::delete,
        onReport = viewModel::report
    )
}

private data class ShareTarget(val entry: DiaryEntry, val label: String?)

/** A thought and its thread, joined by a line, with a composer under the last post for the author. */
@Composable
fun DiaryEntryDetailContent(
    state: DiaryDetailUiState,
    focusComposer: Boolean = false,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    onToggleLike: (DiaryEntry) -> Unit,
    onToggleRepost: (DiaryEntry) -> Unit,
    onPost: (text: String, done: (Boolean) -> Unit) -> Unit = { _, _ -> },
    onDelete: (DiaryEntry) -> Unit = {},
    onReport: (DiaryEntry, String) -> Unit = { _, _ -> }
) {
    var deleteTarget by remember { mutableStateOf<DiaryEntry?>(null) }
    var reportTarget by remember { mutableStateOf<DiaryEntry?>(null) }
    var shareTarget by remember { mutableStateOf<ShareTarget?>(null) }
    var draft by rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val isThread = state.thread.size > 1

    Column(Modifier.fillMaxSize().background(HL.Paper).statusBarsPadding().imePadding()) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, stringResource(R.string.detail_back), tint = HL.Ink)
            }
            Text(
                if (isThread) "Thread" else "Thought",
                fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = HL.Ink
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(HL.Ink.copy(alpha = 0.08f)))

        state.notice?.let {
            Text(it, fontSize = 12.sp, color = HL.Muted, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
        }
        state.errorMessage?.let {
            Text(it, fontSize = 14.sp, color = HL.Accent, modifier = Modifier.padding(20.dp))
        }
        if (state.isLoading) {
            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(24.dp))
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 40.dp)
        ) {
            itemsIndexed(state.thread, key = { _, e -> e.id }) { idx, entry ->
                ThreadItem(
                    entry = entry,
                    isFirst = idx == 0,
                    joinsNext = idx < state.thread.lastIndex || state.isOwnThread,
                    onOpenBook = onOpenBook,
                    onToggleLike = { onToggleLike(entry) },
                    onToggleRepost = { onToggleRepost(entry) },
                    onShare = { shareTarget = ShareTarget(entry, if (isThread) "${idx + 1}/${state.thread.size}" else null) },
                    onDelete = { deleteTarget = entry },
                    onReport = { reportTarget = entry }
                )
            }
            if (state.isOwnThread) {
                item(key = "composer") {
                    Composer(
                        root = state.root!!,
                        draft = draft,
                        onDraft = { draft = it },
                        isPosting = state.isPosting,
                        focus = focusComposer,
                        onPost = {
                            onPost(draft) { posted -> if (posted) draft = "" }
                        }
                    )
                }
            }
        }
    }

    // Keep a newly posted follow-up in view — but not on first load, which
    // must open at the first thought.
    var seenSize by remember { mutableStateOf(-1) }
    LaunchedEffect(state.thread.size, state.isLoading) {
        if (state.isLoading) return@LaunchedEffect
        if (seenSize in 1 until state.thread.size) listState.animateScrollToItem(state.thread.size)
        seenSize = state.thread.size
    }

    shareTarget?.let { t ->
        ThoughtShareSheet(entry = t.entry, threadLabel = t.label, onDismiss = { shareTarget = null })
    }

    reportTarget?.let { target ->
        ReportDialog(
            title = "Report thought",
            onDismiss = { reportTarget = null },
            onSubmit = { reason -> onReport(target, reason); reportTarget = null }
        )
    }

    deleteTarget?.let { target ->
        val wholeThread = target.id == state.root?.id && isThread
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = HL.Card,
            title = { Text(if (wholeThread) "Delete this thread?" else "Delete this thought?", color = HL.Ink) },
            text = {
                Text(
                    if (wholeThread) "The first thought takes the whole thread with it. This can't be undone."
                    else "This can't be undone.",
                    color = HL.Muted
                )
            },
            confirmButton = {
                TextButton(onClick = { deleteTarget = null; onDelete(target) }) { Text("Delete", color = HL.Accent) }
            },
            dismissButton = {
                TextButton(onClick = { deleteTarget = null }) { Text("Cancel", color = HL.Ink) }
            }
        )
    }
}

@Composable
private fun ThreadItem(
    entry: DiaryEntry,
    isFirst: Boolean,
    joinsNext: Boolean,
    onOpenBook: (String) -> Unit,
    onToggleLike: () -> Unit,
    onToggleRepost: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onReport: () -> Unit
) {
    var menu by remember { mutableStateOf(false) }
    Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        Column(Modifier.width(40.dp).fillMaxHeight(), horizontalAlignment = Alignment.CenterHorizontally) {
            AvatarImage(url = entry.avatarUrl, name = entry.displayName, size = 40.dp)
            if (joinsNext) {
                Box(Modifier.padding(top = 4.dp).width(2.dp).weight(1f).background(HL.Ink.copy(alpha = 0.12f)))
            }
        }
        Column(Modifier.weight(1f).padding(bottom = 14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ThoughtHeader(entry, Modifier.weight(1f))
                Box {
                    IconButton(onClick = { menu = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Outlined.MoreHoriz, "More", tint = HL.Muted, modifier = Modifier.size(18.dp))
                    }
                    DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                        if (entry.canEdit) {
                            DropdownMenuItem(text = { Text("Delete", color = HL.Accent) }, onClick = { menu = false; onDelete() })
                        } else {
                            DropdownMenuItem(text = { Text("Report") }, onClick = { menu = false; onReport() })
                        }
                    }
                }
            }
            if (isFirst && entry.book == null && !entry.title.isNullOrBlank()) {
                Text(entry.title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = HL.Ink)
            }
            androidx.compose.foundation.text.selection.SelectionContainer {
                Text(
                    entry.plainText,
                    fontFamily = FontFamily.Serif, fontSize = 16.5.sp, lineHeight = 24.sp, color = HL.Ink
                )
            }
            if (isFirst && entry.book != null) {
                Box(Modifier.padding(top = 4.dp)) { ThoughtBookChip(entry, onOpenBook) }
            }
            ThoughtActionBar(
                entry = entry,
                isOwn = entry.canEdit,
                onToggleRepost = onToggleRepost,
                onToggleLike = onToggleLike,
                onShare = onShare
            )
        }
    }
}

@Composable
private fun Composer(
    root: DiaryEntry,
    draft: String,
    onDraft: (String) -> Unit,
    isPosting: Boolean,
    focus: Boolean,
    onPost: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(focus) { if (focus) runCatching { focusRequester.requestFocus() } }
    val canPost = draft.isNotBlank() && !isPosting

    Row(Modifier.fillMaxWidth().padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        AvatarImage(url = root.avatarUrl, name = root.displayName, size = 40.dp)
        Column(Modifier.weight(1f)) {
            Box(Modifier.fillMaxWidth().heightIn(min = 40.dp).padding(top = 10.dp)) {
                if (draft.isEmpty()) {
                    Text("Add to this thread…", fontFamily = FontFamily.Serif, fontSize = 15.5.sp, color = HL.Muted)
                }
                BasicTextField(
                    value = draft,
                    onValueChange = onDraft,
                    textStyle = TextStyle(fontFamily = FontFamily.Serif, fontSize = 15.5.sp, color = HL.Ink),
                    cursorBrush = SolidColor(HL.Ink),
                    maxLines = 8,
                    modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
                )
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(HL.Ink.copy(alpha = 0.1f)))
            Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (root.isPrivate) "Private, like the rest of the thread" else "Joins the thread under the same book",
                    fontSize = 11.5.sp, color = HL.Muted, modifier = Modifier.weight(1f)
                )
                Box(
                    Modifier
                        .clip(RoundedCornerShape(50))
                        .background(HL.Ink)
                        .alpha(if (canPost) 1f else 0.4f)
                        .clickable(enabled = canPost, onClickLabel = "Post", onClick = onPost)
                        .padding(horizontal = 18.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (isPosting) CircularProgressIndicator(color = HL.Paper, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                    else Text("Post", color = HL.Paper, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                }
            }
        }
    }
    Spacer(Modifier.height(8.dp))
}

@Preview
@Composable
private fun DiaryEntryDetailPreview() {
    val root = DiaryEntry(
        id = "1", userId = "user1", username = "alice", name = "Alice",
        book = Book(id = "book1", volumeInfo = VolumeInfo(title = "Never Let Me Go", authors = listOf("Kazuo Ishiguro"))),
        bookId = "book1", content = "The quietest devastation I have read in years.",
        rating = 5, likesCount = 12, isLiked = true, repostsCount = 3, threadCount = 1, canEdit = true,
        createdAt = "2026-09-10T10:00:00Z"
    )
    PaperBoxdTheme {
        DiaryEntryDetailContent(
            state = DiaryDetailUiState(
                thread = listOf(root, root.copy(id = "2", content = "Ruth is the one I argue with.", threadRootId = "1", book = null)),
                isLoading = false
            ),
            onBack = {}, onOpenBook = {}, onToggleLike = {}, onToggleRepost = {}
        )
    }
}
