package `in`.paperboxd.app.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.MessageRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.AttachmentRef
import `in`.paperboxd.app.domain.model.MessagePerson
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val MAX_RECIPIENTS = 5

data class SendUiState(
    val query: String = "",
    val recents: List<MessagePerson> = emptyList(),
    val results: List<MessagePerson> = emptyList(),
    val selected: List<MessagePerson> = emptyList(),
    val note: String = "",
    val sending: Boolean = false,
    val error: String? = null,
    val sent: Boolean = false
)

@HiltViewModel
class SendViewModel @Inject constructor(
    private val messages: MessageRepository,
    private val users: UserRepository
) : ViewModel() {
    private val _state = MutableStateFlow(SendUiState())
    val state: StateFlow<SendUiState> = _state.asStateFlow()
    private var searchJob: Job? = null

    init {
        viewModelScope.launch {
            messages.conversations("accepted").onSuccess { list ->
                _state.update { s -> s.copy(recents = list.conversations.take(12).map { it.them }) }
            }
        }
    }

    fun setQuery(q: String) {
        _state.update { it.copy(query = q) }
        searchJob?.cancel()
        if (q.isBlank()) {
            _state.update { it.copy(results = emptyList()) }
            return
        }
        searchJob = viewModelScope.launch {
            delay(300)
            users.searchUsers(q.trim(), pageSize = 15).onSuccess { r ->
                _state.update { s ->
                    s.copy(results = r.users.map { MessagePerson(it.id, it.username, it.name, it.avatarUrl) })
                }
            }
        }
    }

    fun toggle(p: MessagePerson) = _state.update { s ->
        when {
            s.selected.any { it.username == p.username } -> s.copy(selected = s.selected.filterNot { it.username == p.username })
            s.selected.size >= MAX_RECIPIENTS -> s.copy(error = "You can send to $MAX_RECIPIENTS readers at a time")
            else -> s.copy(selected = s.selected + p, error = null)
        }
    }

    fun setNote(n: String) = _state.update { it.copy(note = n.take(2000)) }

    fun send(attachment: AttachmentRef) {
        val s = _state.value
        if (s.selected.isEmpty() || s.sending) return
        _state.update { it.copy(sending = true, error = null) }
        viewModelScope.launch {
            messages.send(conversationId = null, to = s.selected.map { it.username }, body = s.note.trim(), attachments = listOf(attachment))
                .onSuccess { r ->
                    if (r.sent.isEmpty()) {
                        _state.update { it.copy(sending = false, error = r.failed.firstOrNull()?.error ?: "Couldn't send") }
                    } else {
                        _state.update { it.copy(sending = false, sent = true) }
                    }
                }
                .onFailure { e -> _state.update { it.copy(sending = false, error = sendErrorText(e)) } }
        }
    }
}

/**
 * "Send on Paperboxd": pick up to five readers — recent chats first, or search —
 * add an optional note, and the object lands in each DM as a card.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendSheet(
    attachment: AttachmentRef,
    onDismiss: () -> Unit,
    onSent: () -> Unit = {},
    viewModel: SendViewModel = hiltViewModel(key = "send-${attachment.kind}-${attachment.refId}")
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(state.sent) { if (state.sent) { onSent(); onDismiss() } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = HL.Paper
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 20.dp).padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "SEND ON PAPERBOXD",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp,
                letterSpacing = 2.sp,
                color = HL.Ink
            )
            SheetField(state.query, viewModel::setQuery, "Search readers")

            val people = if (state.query.isBlank()) state.recents else state.results
            if (state.query.isBlank() && people.isNotEmpty()) Eyebrow("RECENT")
            LazyColumn(Modifier.fillMaxWidth().heightIn(max = 320.dp)) {
                items(people, key = { it.username }) { p ->
                    val on = state.selected.any { it.username == p.username }
                    Row(
                        Modifier.fillMaxWidth().clickable { viewModel.toggle(p) }.padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        AvatarImage(p.avatarUrl, p.displayName, 40.dp)
                        Column(Modifier.weight(1f)) {
                            Text(p.displayName, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = HL.Ink, maxLines = 1)
                            Text("@${p.username}", fontSize = 12.sp, color = HL.Muted, maxLines = 1)
                        }
                        Box(
                            Modifier.size(24.dp).clip(CircleShape)
                                .background(if (on) HL.Ink else HL.Card)
                                .border(1.5.dp, HL.Ink, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (on) Icon(Icons.Filled.Check, null, tint = HL.Paper, modifier = Modifier.size(14.dp))
                        }
                    }
                }
                if (people.isEmpty()) item {
                    Text(
                        if (state.query.isBlank()) "Search for a reader to send this to." else "No readers found.",
                        fontSize = 13.sp,
                        color = HL.Muted,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                }
            }

            if (state.selected.isNotEmpty()) {
                SheetField(state.note, viewModel::setNote, "Add a message (optional)")
            }
            state.error?.let { Text(it, fontSize = 13.sp, color = HL.Accent) }

            val n = state.selected.size
            Box(
                Modifier.fillMaxWidth()
                    .brutalPlate(fill = if (n > 0) HL.Ink else HL.Paper2, borderWidth = 2.dp, offset = 3.dp)
                    .clickable(enabled = n > 0 && !state.sending) { viewModel.send(attachment) }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                if (state.sending) {
                    CircularProgressIndicator(color = HL.Paper, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Text(
                        when (n) { 0 -> "Choose readers"; 1 -> "Send"; else -> "Send separately to $n" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (n > 0) HL.Paper else HL.Muted
                    )
                }
            }
            Spacer(Modifier.size(4.dp))
        }
    }
}

@Composable
fun SheetField(value: String, onChange: (String) -> Unit, placeholder: String, modifier: Modifier = Modifier) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        placeholder = { Text(placeholder, color = HL.Muted) },
        singleLine = false,
        maxLines = 4,
        shape = RoundedCornerShape(10.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HL.Ink,
            unfocusedBorderColor = HL.Ink.copy(alpha = 0.4f),
            focusedContainerColor = HL.Card,
            unfocusedContainerColor = HL.Card,
            cursorColor = HL.Ink,
            focusedTextColor = HL.Ink,
            unfocusedTextColor = HL.Ink
        ),
        modifier = modifier.fillMaxWidth()
    )
}
