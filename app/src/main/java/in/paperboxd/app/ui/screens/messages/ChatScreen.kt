package `in`.paperboxd.app.ui.screens.messages

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material.icons.outlined.Gif
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.JoinInner
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.FusionRepository
import `in`.paperboxd.app.data.repository.MessageRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.AttachKind
import `in`.paperboxd.app.domain.model.AttachmentRef
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.ChatMessage
import `in`.paperboxd.app.domain.model.ConversationPage
import `in`.paperboxd.app.domain.model.GifList
import `in`.paperboxd.app.domain.model.MediaUpload
import `in`.paperboxd.app.domain.model.MessageAttachment
import `in`.paperboxd.app.domain.model.MessagePerson
import `in`.paperboxd.app.domain.model.SharedList
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.ReportDialog
import `in`.paperboxd.app.ui.components.brutalPlate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ChatUiState(
    val conversationId: String? = null,
    val them: MessagePerson = MessagePerson(),
    /** The viewer's side: accepted | request. */
    val myState: String = "accepted",
    val muted: Boolean = false,
    val theirLastReadId: Long = 0,
    val messages: List<ChatMessage> = emptyList(),
    val hasMore: Boolean = false,
    val loading: Boolean = true,
    val error: String? = null,
    val draft: String = "",
    val pendingBooks: List<Book> = emptyList(),
    val pendingMedia: List<PendingMedia> = emptyList(),
    val sending: Boolean = false,
    val sendError: String? = null,
    val showShared: Boolean = false,
    val shared: SharedList? = null,
    val sharedKind: String? = null,
    val bookResults: List<Book> = emptyList(),
    val gone: Boolean = false,
    val toast: String? = null
) {
    val uploading: Boolean get() = pendingMedia.any { it.upload == null }
    val canSend: Boolean
        get() = (draft.isNotBlank() || pendingBooks.isNotEmpty() || pendingMedia.isNotEmpty()) && !sending && !uploading
}

/** A photo or video picked for the composer, uploading in the background. */
data class PendingMedia(
    val key: String = UUID.randomUUID().toString(),
    val kind: String,
    val uri: Uri,
    val upload: MediaUpload? = null
)

@HiltViewModel
class ChatViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    @ApplicationContext private val context: Context,
    private val repo: MessageRepository,
    private val users: UserRepository,
    private val books: BookRepository,
    private val fusions: FusionRepository
) : ViewModel() {
    private val _state = MutableStateFlow(
        ChatUiState(
            conversationId = savedStateHandle.get<String>("conversationId")?.takeIf { it.isNotBlank() },
            them = MessagePerson(username = savedStateHandle.get<String>("username").orEmpty())
        )
    )
    val state: StateFlow<ChatUiState> = _state.asStateFlow()
    private var searchJob: Job? = null
    // One client id per draft, so a retried send after a timeout is not doubled.
    private var clientId = UUID.randomUUID().toString()

    init {
        viewModelScope.launch {
            val s = _state.value
            if (s.conversationId == null && s.them.username.isNotBlank()) {
                repo.conversationWith(s.them.username)
                    .onSuccess { w ->
                        _state.update { it.copy(conversationId = w.conversationId.ifBlank { null }, them = w.them) }
                    }
                    .onFailure { e -> _state.update { it.copy(loading = false, error = e.message); }; return@launch }
            }
            if (_state.value.conversationId == null) _state.update { it.copy(loading = false) } else loadLatest()
        }
    }

    private fun apply(p: ConversationPage) = _state.update {
        it.copy(them = p.them, myState = p.state, muted = p.muted, theirLastReadId = p.theirLastReadId)
    }

    private suspend fun loadLatest() {
        val id = _state.value.conversationId ?: return
        repo.messages(id)
            .onSuccess { p ->
                apply(p)
                _state.update { it.copy(messages = p.messages, hasMore = p.hasMore, loading = false, error = null) }
                markRead(id)
            }
            .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
    }

    /** The polling path: whatever arrived since the newest message we hold. */
    suspend fun poll() {
        val s = _state.value
        val id = s.conversationId ?: return
        if (s.loading) return
        val after = s.messages.lastOrNull()?.id ?: 0
        repo.messages(id, after = after.takeIf { it > 0 }).onSuccess { p ->
            apply(p)
            val fresh = if (after > 0) p.messages else emptyList()
            if (after == 0L) _state.update { it.copy(messages = p.messages, hasMore = p.hasMore) }
            if (fresh.isNotEmpty()) {
                _state.update { st ->
                    val known = st.messages.map { it.id }.toSet()
                    st.copy(messages = st.messages + fresh.filter { it.id !in known })
                }
                if (fresh.any { !it.fromMe }) markRead(id)
            }
        }
    }

    // A request stays unread until accepted, so the sender does not see "Seen".
    private suspend fun markRead(id: String) {
        if (_state.value.myState != "request") repo.markRead(id)
    }

    fun loadOlder() {
        val s = _state.value
        val id = s.conversationId ?: return
        val first = s.messages.firstOrNull()?.id ?: return
        viewModelScope.launch {
            repo.messages(id, before = first).onSuccess { p ->
                _state.update { it.copy(messages = p.messages + it.messages, hasMore = p.hasMore) }
            }
        }
    }

    fun setDraft(t: String) = _state.update { it.copy(draft = t.take(2000), sendError = null) }

    fun addBook(b: Book) = _state.update {
        if (it.pendingBooks.size >= 10 || it.pendingBooks.any { p -> p.id == b.id }) it
        else it.copy(pendingBooks = it.pendingBooks + b, bookResults = emptyList())
    }

    fun removeBook(b: Book) = _state.update { it.copy(pendingBooks = it.pendingBooks - b) }

    fun searchBooks(q: String) {
        searchJob?.cancel()
        if (q.isBlank()) { _state.update { it.copy(bookResults = emptyList()) }; return }
        searchJob = viewModelScope.launch {
            delay(300)
            books.searchBooks(q.trim(), pageSize = 15).onSuccess { r -> _state.update { it.copy(bookResults = r.items.orEmpty()) } }
        }
    }

    fun send() {
        val s = _state.value
        if (!s.canSend) return
        val attachments = s.pendingBooks.map { AttachmentRef(AttachKind.BOOK, it.id) } +
            s.pendingMedia.mapNotNull { m -> m.upload?.let { AttachmentRef(it.kind, it.refId) } }
        viewModelScope.launch {
            if (post(s.draft.trim(), attachments, clientId)) {
                clientId = UUID.randomUUID().toString()
                _state.update { it.copy(draft = "", pendingBooks = emptyList(), pendingMedia = emptyList()) }
            }
        }
    }

    /** A GIF or sticker goes out on its own, leaving the draft alone. */
    fun sendNow(ref: AttachmentRef) {
        if (_state.value.sending) return
        viewModelScope.launch { post("", listOf(ref), UUID.randomUUID().toString()) }
    }

    private suspend fun post(body: String, attachments: List<AttachmentRef>, clientId: String): Boolean {
        val s = _state.value
        _state.update { it.copy(sending = true, sendError = null) }
        var ok = false
        repo.send(
            conversationId = s.conversationId,
            to = if (s.conversationId == null) listOf(s.them.username) else null,
            body = body,
            attachments = attachments,
            clientId = clientId
        ).onSuccess { r ->
            val msg = r.sent.firstOrNull()
            if (msg == null) {
                _state.update { it.copy(sending = false, sendError = r.failed.firstOrNull()?.error ?: "Couldn't send") }
                return@onSuccess
            }
            ok = true
            _state.update { st ->
                st.copy(
                    conversationId = msg.conversationId,
                    messages = if (st.messages.any { it.id == msg.id }) st.messages else st.messages + msg,
                    sending = false,
                    // Replying to a request accepts it.
                    myState = "accepted"
                )
            }
        }.onFailure { e -> _state.update { it.copy(sending = false, sendError = sendErrorText(e)) } }
        return ok
    }

    /**
     * Picked photos and videos start uploading straight away, so Send is
     * quick; it waits until every upload has its ref.
     */
    fun addMedia(uris: List<Uri>) {
        val s = _state.value
        val room = (10 - s.pendingBooks.size - s.pendingMedia.size).coerceAtLeast(0)
        uris.take(room).forEach { uri ->
            val kind = if (context.contentResolver.getType(uri).orEmpty().startsWith("video/")) AttachKind.VIDEO else AttachKind.PHOTO
            val pending = PendingMedia(kind = kind, uri = uri)
            _state.update { it.copy(pendingMedia = it.pendingMedia + pending, sendError = null) }
            viewModelScope.launch {
                runCatching { prepareMedia(context, uri) }
                    .fold(
                        onSuccess = { p -> repo.uploadMedia(p.kind, p.fileName, p.body) },
                        onFailure = { Result.failure(it) }
                    )
                    .onSuccess { up ->
                        _state.update { st -> st.copy(pendingMedia = st.pendingMedia.map { if (it.key == pending.key) it.copy(upload = up) else it }) }
                    }
                    .onFailure { e ->
                        _state.update { st ->
                            st.copy(pendingMedia = st.pendingMedia.filterNot { it.key == pending.key }, sendError = sendErrorText(e))
                        }
                    }
            }
        }
    }

    fun removeMedia(key: String) = _state.update { st -> st.copy(pendingMedia = st.pendingMedia.filterNot { it.key == key }) }

    suspend fun gifs(type: String, query: String): Result<GifList> = repo.gifs(type, query)

    fun unsend(m: ChatMessage) = viewModelScope.launch {
        repo.unsend(m.id).onSuccess {
            _state.update { st ->
                st.copy(messages = st.messages.map { if (it.id == m.id) it.copy(unsent = true, body = "", attachments = emptyList()) else it })
            }
        }.onFailure { e -> toast(e.message ?: "Couldn't unsend") }
    }

    fun accept() = memberAction { id ->
        repo.accept(id).onSuccess {
            _state.update { s -> s.copy(myState = "accepted") }
            repo.markRead(id)
        }
    }

    fun toggleMute() = memberAction { id ->
        val to = !_state.value.muted
        repo.setMuted(id, to).onSuccess { _state.update { it.copy(muted = to) }; toast(if (to) "Muted" else "Unmuted") }
    }

    fun clearChat() = memberAction { repo.clear(it).onSuccess { _state.update { s -> s.copy(gone = true) } } }

    fun block() = viewModelScope.launch {
        users.block(_state.value.them.username)
            .onSuccess { _state.update { it.copy(gone = true) } }
            .onFailure { e -> toast(e.message ?: "Couldn't block") }
    }

    fun reportMessage(m: ChatMessage, reason: String) = viewModelScope.launch {
        users.report("message", m.id.toString(), reason)
            .onSuccess { toast("Reported. We review reports within 24 hours.") }
            .onFailure { e -> toast(e.message ?: "Couldn't report") }
    }

    fun reportUser(reason: String) = viewModelScope.launch {
        users.report("user", _state.value.them.username, reason)
            .onSuccess { toast("Reported. We review reports within 24 hours.") }
    }

    fun showShared(on: Boolean) {
        _state.update { it.copy(showShared = on) }
        if (on) loadShared(_state.value.sharedKind)
    }

    fun loadShared(kind: String?) {
        val id = _state.value.conversationId ?: return
        _state.update { it.copy(sharedKind = kind) }
        viewModelScope.launch { repo.shared(id, kind).onSuccess { l -> _state.update { it.copy(shared = l) } } }
    }

    /**
     * "Make a Fusion": drops your live Fusion link into this chat. It is the
     * same one-time link as the profile's, so the first reader to fuse wins.
     */
    fun makeFusion() = viewModelScope.launch {
        fusions.createInvite()
            .onSuccess { inv ->
                _state.update { it.copy(draft = "Fuse with me on PaperBoxd: ${inv.url}") }
                send()
            }
            .onFailure { e -> toast(e.message ?: "Couldn't make a Fusion link") }
    }

    fun toastShown() = _state.update { it.copy(toast = null) }
    private fun toast(t: String) = _state.update { it.copy(toast = t) }

    private fun memberAction(block: suspend (String) -> Unit) {
        val id = _state.value.conversationId ?: return
        viewModelScope.launch { block(id) }
    }
}

private val kindTabs = listOf(null to "ALL", AttachKind.BOOK to "BOOKS", AttachKind.LIST to "LISTS",
    AttachKind.THOUGHT to "THOUGHTS", AttachKind.PROFILE to "READERS", AttachKind.FUSION to "FUSIONS",
    AttachKind.PHOTO to "PHOTOS", AttachKind.VIDEO to "VIDEOS")

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    onBack: () -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenAttachment: (MessageAttachment) -> Unit,
    onOpenFusionLink: (token: String) -> Unit = {},
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var menuOpen by remember { mutableStateOf(false) }
    var confirm by remember { mutableStateOf<String?>(null) } // "clear" | "block" | "fusion"
    var unsendTarget by remember { mutableStateOf<ChatMessage?>(null) }
    var reportTarget by remember { mutableStateOf<ChatMessage?>(null) }
    var reportingUser by remember { mutableStateOf(false) }
    var pickBook by remember { mutableStateOf(false) }
    var gifType by remember { mutableStateOf<String?>(null) }
    var viewing by remember { mutableStateOf<MessageAttachment?>(null) }
    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(10)) { uris ->
        if (uris.isNotEmpty()) viewModel.addMedia(uris)
    }

    PollWhileResumed(4_000) { viewModel.poll() }
    LaunchedEffect(state.gone) { if (state.gone) onBack() }

    Column(Modifier.fillMaxSize().background(HL.Paper).statusBarsPadding().imePadding()) {
        // Header: back · who · menu, then the Chat / Shared switch.
        Column(Modifier.fillMaxWidth().background(HL.Card)) {
            Row(
                Modifier.fillMaxWidth().padding(start = 4.dp, end = 4.dp, top = 6.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = HL.Ink)
                }
                Row(
                    Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                        .clickable(enabled = state.them.username.isNotBlank()) { onOpenProfile(state.them.username) }
                        .padding(vertical = 4.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                ) {
                    AvatarImage(state.them.avatarUrl, state.them.displayName.ifBlank { "?" }, 40.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        Text(
                            state.them.displayName, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold,
                            fontSize = 18.sp, color = HL.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis
                        )
                        if (state.them.username.isNotBlank()) {
                            Text(
                                "@${state.them.username}" + if (state.muted) "  ·  muted" else "",
                                fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = HL.Muted, maxLines = 1
                            )
                        }
                    }
                }
                Box {
                    Box(Modifier.size(44.dp).clip(CircleShape).clickable { menuOpen = true }, contentAlignment = Alignment.Center) {
                        Icon(Icons.Outlined.MoreVert, "More", tint = HL.Ink)
                    }
                    DropdownMenu(expanded = menuOpen, onDismissRequest = { menuOpen = false }, containerColor = HL.Card) {
                        DropdownMenuItem(
                            text = { Text("Make a Fusion") },
                            leadingIcon = { Icon(Icons.Outlined.JoinInner, null) },
                            onClick = { menuOpen = false; confirm = "fusion" }
                        )
                        if (state.conversationId != null) {
                            DropdownMenuItem(
                                text = { Text(if (state.muted) "Unmute" else "Mute") },
                                leadingIcon = { Icon(if (state.muted) Icons.Outlined.Notifications else Icons.Outlined.NotificationsOff, null) },
                                onClick = { menuOpen = false; viewModel.toggleMute() }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete chat") },
                                leadingIcon = { Icon(Icons.Outlined.DeleteOutline, null) },
                                onClick = { menuOpen = false; confirm = "clear" }
                            )
                        }
                        DropdownMenuItem(
                            text = { Text("Report") },
                            leadingIcon = { Icon(Icons.Outlined.Flag, null) },
                            onClick = { menuOpen = false; reportingUser = true }
                        )
                        DropdownMenuItem(
                            text = { Text("Block", color = HL.Accent) },
                            leadingIcon = { Icon(Icons.Outlined.Block, null, tint = HL.Accent) },
                            onClick = { menuOpen = false; confirm = "block" }
                        )
                    }
                }
            }
            if (state.conversationId != null) {
                Row(Modifier.fillMaxWidth()) {
                    Segment("Chat", !state.showShared, Modifier.weight(1f)) { viewModel.showShared(false) }
                    Segment("Shared", state.showShared, Modifier.weight(1f)) { viewModel.showShared(true) }
                }
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(HL.Ink.copy(alpha = 0.12f)))
        }

        if (state.myState == "request") RequestBanner(
            username = state.them.username,
            onAccept = viewModel::accept,
            onDelete = { confirm = "clear" },
            onBlock = { confirm = "block" }
        )

        if (state.showShared) {
            SharedPane(state, onKind = viewModel::loadShared, onOpen = onOpenAttachment, onView = { viewing = it }, modifier = Modifier.weight(1f))
        } else {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when {
                    state.loading -> CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(26.dp).align(Alignment.Center))
                    state.error != null && state.messages.isEmpty() -> CenterNote(state.error ?: "")
                    state.messages.isEmpty() -> EmptyChat(state.them)
                    else -> {
                        val msgs = state.messages
                        val newestMine = msgs.lastOrNull()?.takeIf { it.fromMe }
                        LazyColumn(
                            reverseLayout = true,
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            // reverseLayout: index 0 is the newest message.
                            val n = msgs.size
                            items(n, key = { msgs[n - 1 - it].id }) { i ->
                                val idx = n - 1 - i
                                val m = msgs[idx]
                                val older = msgs.getOrNull(idx - 1)
                                val newer = msgs.getOrNull(idx + 1)
                                val newDay = older == null || !sameDay(older.createdAt, m.createdAt)
                                val joinsOlder = !newDay && older!!.fromMe == m.fromMe && minutesApart(older.createdAt, m.createdAt) < 5
                                val joinsNewer = newer != null && newer.fromMe == m.fromMe &&
                                    sameDay(m.createdAt, newer.createdAt) && minutesApart(m.createdAt, newer.createdAt) < 5
                                Column(Modifier.fillMaxWidth()) {
                                    if (newDay) DayDivider(dayLabel(m.createdAt))
                                    Bubble(
                                        m,
                                        joinsOlder = joinsOlder,
                                        joinsNewer = joinsNewer,
                                        seen = m == newestMine && state.theirLastReadId >= m.id,
                                        onOpen = onOpenAttachment,
                                        onView = { viewing = it },
                                        onOpenFusionLink = onOpenFusionLink,
                                        onLongPress = { if (!m.unsent) { if (m.fromMe) unsendTarget = m else reportTarget = m } }
                                    )
                                }
                            }
                            if (state.hasMore) item {
                                TextButton(onClick = viewModel::loadOlder, modifier = Modifier.fillMaxWidth()) {
                                    Text("Load earlier", color = HL.Muted, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
            Composer(
                state = state,
                onDraft = viewModel::setDraft,
                onAttach = { what ->
                    when (what) {
                        "book" -> pickBook = true
                        "media" -> pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                        else -> gifType = what
                    }
                },
                onRemoveBook = viewModel::removeBook,
                onRemoveMedia = viewModel::removeMedia,
                onSend = viewModel::send
            )
        }
    }

    state.toast?.let { t ->
        AlertDialog(
            onDismissRequest = viewModel::toastShown,
            confirmButton = { TextButton(onClick = viewModel::toastShown) { Text("OK", color = HL.Ink) } },
            text = { Text(t, color = HL.Ink) },
            containerColor = HL.Card
        )
    }

    confirm?.let { what ->
        val (title, body, action) = when (what) {
            "block" -> Triple("Block @${state.them.username}?", "They won't be able to message you, and this chat disappears for both of you.", "Block")
            "fusion" -> Triple(
                "Make a Fusion with ${state.them.displayName.substringBefore(' ').ifBlank { "them" }}?",
                "Sends your Fusion link here. When they tap Fuse, your two shelves line up side by side. The link works once.",
                "Send link"
            )
            else -> Triple("Delete this chat?", "It's removed for you only. If they message again, the chat comes back from that message.", "Delete")
        }
        AlertDialog(
            onDismissRequest = { confirm = null },
            containerColor = HL.Card,
            title = { Text(title, color = HL.Ink, fontWeight = FontWeight.Bold) },
            text = { Text(body, color = HL.Ink) },
            confirmButton = {
                TextButton(onClick = {
                    confirm = null
                    when (what) {
                        "block" -> viewModel.block()
                        "fusion" -> viewModel.makeFusion()
                        else -> viewModel.clearChat()
                    }
                }) { Text(action, color = if (what == "fusion") HL.Ink else HL.Accent, fontWeight = FontWeight.SemiBold) }
            },
            dismissButton = { TextButton(onClick = { confirm = null }) { Text("Cancel", color = HL.Muted) } }
        )
    }

    unsendTarget?.let { m ->
        AlertDialog(
            onDismissRequest = { unsendTarget = null },
            containerColor = HL.Card,
            title = { Text("Unsend message?", color = HL.Ink, fontWeight = FontWeight.Bold) },
            text = { Text("It's removed for both of you.", color = HL.Ink) },
            confirmButton = { TextButton(onClick = { unsendTarget = null; viewModel.unsend(m) }) { Text("Unsend", color = HL.Accent) } },
            dismissButton = { TextButton(onClick = { unsendTarget = null }) { Text("Cancel", color = HL.Muted) } }
        )
    }

    reportTarget?.let { m ->
        ReportDialog(
            title = "Report message",
            onDismiss = { reportTarget = null },
            onSubmit = { reason -> reportTarget = null; viewModel.reportMessage(m, reason) }
        )
    }
    if (reportingUser) {
        ReportDialog(
            title = "Report @${state.them.username}",
            onDismiss = { reportingUser = false },
            onSubmit = { reason -> reportingUser = false; viewModel.reportUser(reason) }
        )
    }

    gifType?.let { type ->
        GifPickerSheet(
            initialType = type,
            search = viewModel::gifs,
            onPick = { ref -> gifType = null; viewModel.sendNow(ref) },
            onDismiss = { gifType = null }
        )
    }
    viewing?.let { MediaViewer(it) { viewing = null } }

    if (pickBook) {
        ModalBottomSheet(
            onDismissRequest = { pickBook = false; viewModel.searchBooks("") },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = HL.Paper
        ) {
            var q by remember { mutableStateOf("") }
            Column(Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("SEND A BOOK", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 2.sp, color = HL.Ink)
                SheetField(q, { q = it; viewModel.searchBooks(it) }, "Search books")
                LazyColumn(Modifier.fillMaxWidth().heightIn(max = 420.dp)) {
                    items(state.bookResults, key = { it.id }) { b ->
                        Row(
                            Modifier.fillMaxWidth().clickable { viewModel.addBook(b); pickBook = false }.padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BookCoverImage(b.coverUrl, b.title, Modifier.width(40.dp).aspectRatio(2f / 3f), 3.dp)
                            Column(Modifier.weight(1f)) {
                                Text(b.title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = HL.Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(b.authorLine, fontSize = 12.sp, color = HL.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Segment(label: String, on: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Column(modifier.clickable(onClick = onClick), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = if (on) FontWeight.Bold else FontWeight.Medium,
            fontSize = 11.sp,
            letterSpacing = 1.6.sp,
            color = if (on) HL.Ink else HL.Muted,
            modifier = Modifier.padding(vertical = 10.dp)
        )
        Box(Modifier.fillMaxWidth(0.5f).height(2.dp).background(if (on) HL.Ink else Color.Transparent))
    }
}

@Composable
private fun TabChip(label: String, on: Boolean, onClick: () -> Unit) {
    Box(
        Modifier
            .clip(RoundedCornerShape(50))
            .background(if (on) HL.Ink else HL.Card)
            .border(1.dp, HL.Ink.copy(alpha = if (on) 1f else 0.25f), RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(label, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, letterSpacing = 1.2.sp, color = if (on) HL.Paper else HL.Ink)
    }
}

@Composable
private fun DayDivider(label: String) {
    Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 10.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(HL.Ink.copy(alpha = 0.1f)))
        Text(
            label.uppercase(), fontFamily = FontFamily.Monospace, fontSize = 9.5.sp, letterSpacing = 1.6.sp,
            color = HL.Muted, modifier = Modifier.padding(horizontal = 12.dp)
        )
        Box(Modifier.weight(1f).height(1.dp).background(HL.Ink.copy(alpha = 0.1f)))
    }
}

@Composable
private fun EmptyChat(them: MessagePerson) {
    Column(
        Modifier.fillMaxSize().padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AvatarImage(them.avatarUrl, them.displayName.ifBlank { "?" }, 84.dp)
        Text(
            them.displayName, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 22.sp,
            color = HL.Ink, modifier = Modifier.padding(top = 14.dp)
        )
        if (them.username.isNotBlank()) Text("@${them.username}", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = HL.Muted)
        Text(
            "Say hi, or send them a book.",
            fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 15.sp, color = HL.Muted,
            textAlign = TextAlign.Center, modifier = Modifier.padding(top = 18.dp)
        )
    }
}

@Composable
private fun RequestBanner(username: String, onAccept: () -> Unit, onDelete: () -> Unit, onBlock: () -> Unit) {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)
            .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 3.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("MESSAGE REQUEST", fontFamily = FontFamily.Monospace, fontSize = 9.5.sp, letterSpacing = 1.6.sp, color = HL.Muted)
        Text(
            "@$username wants to message you. They won't see that you've read this until you accept.",
            fontSize = 14.sp, lineHeight = 19.sp, color = HL.Ink
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TabChip("ACCEPT", true, onAccept)
            TabChip("DELETE", false, onDelete)
            TabChip("BLOCK", false, onBlock)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Bubble(
    m: ChatMessage,
    joinsOlder: Boolean,
    joinsNewer: Boolean,
    seen: Boolean,
    onOpen: (MessageAttachment) -> Unit,
    onView: (MessageAttachment) -> Unit,
    onOpenFusionLink: (String) -> Unit,
    onLongPress: () -> Unit
) {
    val mine = m.fromMe
    val align = if (mine) Alignment.End else Alignment.Start
    Column(
        Modifier.fillMaxWidth().padding(top = if (joinsOlder) 2.dp else 8.dp),
        horizontalAlignment = align,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (m.unsent) {
            Text(
                "Message unsent",
                fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 13.sp, color = HL.Muted,
                modifier = Modifier.border(1.dp, HL.Ink.copy(alpha = 0.15f), RoundedCornerShape(18.dp)).padding(horizontal = 14.dp, vertical = 8.dp)
            )
        } else {
            m.attachments.forEach { a ->
                if (a.isMedia) {
                    MediaAttachment(a, onTap = { if (a.kind == AttachKind.PHOTO || a.kind == AttachKind.VIDEO) onView(a) }, onLongPress = onLongPress)
                } else {
                    Box(Modifier.combinedClickable(onClick = { onOpen(a) }, onLongClick = onLongPress)) {
                        AttachmentCard(a, onOpen)
                    }
                }
            }
            val token = fusionToken(m.body)
            if (token != null) {
                FusionInviteCard(mine, onOpen = { onOpenFusionLink(token) }, onLongPress = onLongPress)
            } else if (m.body.isNotBlank()) {
                // Tails: the corner on the sender's side tightens where bubbles join.
                val big = 20.dp
                val small = 6.dp
                val shape = if (mine) {
                    RoundedCornerShape(topStart = big, topEnd = if (joinsOlder) small else big, bottomEnd = if (joinsNewer) small else big, bottomStart = big)
                } else {
                    RoundedCornerShape(topStart = if (joinsOlder) small else big, topEnd = big, bottomEnd = big, bottomStart = if (joinsNewer) small else big)
                }
                Text(
                    m.body,
                    fontSize = 15.sp,
                    lineHeight = 21.sp,
                    color = if (mine) HL.Paper else HL.Ink,
                    modifier = Modifier
                        .widthIn(max = 290.dp)
                        .clip(shape)
                        .background(if (mine) HL.Ink else HL.Card)
                        .then(if (mine) Modifier else Modifier.border(1.dp, HL.Ink.copy(alpha = 0.12f), shape))
                        .combinedClickable(onClick = {}, onLongClick = onLongPress)
                        .padding(horizontal = 14.dp, vertical = 9.dp)
                )
            }
        }
        if (!joinsNewer || seen) {
            Text(
                listOfNotNull(timeLabel(m.createdAt).ifBlank { null }, if (seen) "Seen" else null).joinToString(" · "),
                fontSize = 10.5.sp, color = HL.Muted, modifier = Modifier.padding(horizontal = 6.dp)
            )
        }
    }
}

/** A Fusion link dropped into a chat, drawn as an invitation rather than a URL. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun FusionInviteCard(mine: Boolean, onOpen: () -> Unit, onLongPress: () -> Unit) {
    Column(
        Modifier.width(250.dp)
            .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 3.dp)
            .combinedClickable(onClick = onOpen, onLongClick = onLongPress)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text("FUSION INVITE", fontFamily = FontFamily.Monospace, fontSize = 9.5.sp, letterSpacing = 1.6.sp, color = HL.Accent)
        Text("Two tastes. One discovery.", fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, lineHeight = 21.sp, color = HL.Ink)
        Text(
            if (mine) "Waiting for them to fuse. The link works once." else "Tap to line up your shelves side by side.",
            fontSize = 12.5.sp, lineHeight = 17.sp, color = HL.Muted
        )
        if (!mine) {
            Text(
                "FUSE →", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.4.sp,
                color = HL.Paper, modifier = Modifier.padding(top = 4.dp).background(HL.Ink).padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun Composer(
    state: ChatUiState,
    onDraft: (String) -> Unit,
    /** "book" | "media" | "gifs" | "stickers" */
    onAttach: (String) -> Unit,
    onRemoveBook: (Book) -> Unit,
    onRemoveMedia: (String) -> Unit,
    onSend: () -> Unit
) {
    var attachMenu by remember { mutableStateOf(false) }
    Column(
        Modifier.fillMaxWidth().background(HL.Paper).navigationBarsPadding().padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        if (state.pendingBooks.isNotEmpty()) {
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                state.pendingBooks.forEach { b ->
                    Row(
                        Modifier.clip(RoundedCornerShape(12.dp)).background(HL.Card)
                            .border(1.dp, HL.Ink.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(start = 6.dp, end = 6.dp, top = 5.dp, bottom = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BookCoverImage(b.coverUrl, b.title, Modifier.width(22.dp).aspectRatio(2f / 3f), 2.dp)
                        Text(b.title, fontSize = 12.sp, color = HL.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.widthIn(max = 120.dp))
                        Icon(Icons.Outlined.Close, "Remove", tint = HL.Muted, modifier = Modifier.size(16.dp).clickable { onRemoveBook(b) })
                    }
                }
            }
        }
        if (state.pendingMedia.isNotEmpty()) {
            Row(Modifier.horizontalScroll(rememberScrollState()).padding(top = 6.dp, bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                state.pendingMedia.forEach { m -> PendingThumb(m) { onRemoveMedia(m.key) } }
            }
        }
        state.sendError?.let { Text(it, fontSize = 12.sp, color = HL.Accent, modifier = Modifier.padding(bottom = 6.dp, start = 8.dp)) }
        val canSend = state.canSend
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp)).background(HL.Card)
                .border(1.dp, HL.Ink.copy(alpha = 0.18f), RoundedCornerShape(26.dp))
                .padding(start = 4.dp, end = 5.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            Box {
                Box(Modifier.size(42.dp).clip(CircleShape).clickable { attachMenu = true }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Add, "Attach", tint = HL.Ink, modifier = Modifier.size(22.dp))
                }
                DropdownMenu(expanded = attachMenu, onDismissRequest = { attachMenu = false }, containerColor = HL.Card) {
                    listOf(
                        Triple("book", "Book", Icons.AutoMirrored.Filled.MenuBook),
                        Triple("media", "Photo or video", Icons.Outlined.PhotoLibrary),
                        Triple("gifs", "GIF", Icons.Outlined.Gif),
                        Triple("stickers", "Sticker", Icons.Outlined.EmojiEmotions)
                    ).forEach { (what, label, icon) ->
                        DropdownMenuItem(
                            text = { Text(label, color = HL.Ink) },
                            leadingIcon = { Icon(icon, null, tint = HL.Ink) },
                            onClick = { attachMenu = false; onAttach(what) }
                        )
                    }
                }
            }
            BasicTextField(
                value = state.draft,
                onValueChange = onDraft,
                textStyle = TextStyle(fontSize = 15.sp, color = HL.Ink, lineHeight = 20.sp),
                cursorBrush = SolidColor(HL.Ink),
                maxLines = 5,
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp, vertical = 11.dp),
                decorationBox = { inner ->
                    if (state.draft.isEmpty()) Text("Message…", fontSize = 15.sp, color = HL.Muted)
                    inner()
                }
            )
            Box(
                Modifier.size(42.dp).clip(CircleShape).background(if (canSend) HL.Ink else HL.Paper2).clickable(enabled = canSend, onClick = onSend),
                contentAlignment = Alignment.Center
            ) {
                if (state.sending) CircularProgressIndicator(color = HL.Paper, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                else Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = if (canSend) HL.Paper else HL.Muted, modifier = Modifier.size(19.dp))
            }
        }
    }
}

/** A picked photo/video in the composer: spinner until its upload lands. */
@Composable
private fun PendingThumb(m: PendingMedia, onRemove: () -> Unit) {
    Box {
        Box(Modifier.size(58.dp).clip(RoundedCornerShape(10.dp)).background(HL.Ink), contentAlignment = Alignment.Center) {
            // Coil reads a photo Uri directly; a video shows its server poster once uploaded.
            val preview: Any? = if (m.kind == AttachKind.VIDEO) m.upload?.posterUrl else m.uri
            AsyncImage(model = preview, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            when {
                m.upload == null -> CircularProgressIndicator(color = HL.Paper, strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                m.kind == AttachKind.VIDEO -> Icon(Icons.Filled.PlayArrow, null, tint = HL.Paper, modifier = Modifier.size(20.dp))
            }
        }
        Box(
            Modifier.align(Alignment.TopEnd).offset(x = 6.dp, y = (-6).dp).size(20.dp).clip(CircleShape).background(HL.Ink)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.Close, "Remove", tint = HL.Paper, modifier = Modifier.size(12.dp))
        }
    }
}

@Composable
private fun SharedPane(
    state: ChatUiState,
    onKind: (String?) -> Unit,
    onOpen: (MessageAttachment) -> Unit,
    onView: (MessageAttachment) -> Unit,
    modifier: Modifier
) {
    val shared = state.shared
    Column(modifier.fillMaxWidth()) {
        Row(Modifier.horizontalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            kindTabs.forEach { (kind, label) ->
                val n = if (kind == null) shared?.counts?.values?.sum() else shared?.counts?.get(kind)
                if (kind == null || (n ?: 0) > 0) TabChip(if (n != null && n > 0) "$label $n" else label, state.sharedKind == kind) { onKind(kind) }
            }
        }
        when {
            shared == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(26.dp))
            }
            shared.items.isEmpty() -> CenterNote("Everything you two send each other collects here.")
            else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(shared.items, key = { "${it.messageId}-${it.kind}-${it.refId}" }) { item ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Eyebrow((if (item.fromMe) "YOU" else "@${state.them.username}".uppercase()) + " · " + shortAgo(item.createdAt).uppercase())
                        val a = item.attachment
                        if (a.isMedia) MediaAttachment(a, onTap = { onView(a) }) else AttachmentCard(a, onOpen)
                    }
                }
            }
        }
    }
}

@Composable
private fun CenterNote(text: String) {
    Box(Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(text, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 15.sp, lineHeight = 21.sp, color = HL.Muted, textAlign = TextAlign.Center)
    }
}
