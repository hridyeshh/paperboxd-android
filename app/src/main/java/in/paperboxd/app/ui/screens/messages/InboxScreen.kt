package `in`.paperboxd.app.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.MessageRepository
import `in`.paperboxd.app.domain.model.ConversationSummary
import `in`.paperboxd.app.domain.model.InboxBadge
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InboxUiState(
    val box: String = "accepted",
    val conversations: List<ConversationSummary> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val badge: InboxBadge = InboxBadge()
)

/** Backs both the inbox and the dock badge (which only calls [refreshBadge]). */
@HiltViewModel
class InboxViewModel @Inject constructor(
    private val repo: MessageRepository
) : ViewModel() {
    private val _state = MutableStateFlow(InboxUiState())
    val state: StateFlow<InboxUiState> = _state.asStateFlow()

    fun select(box: String) {
        if (box == _state.value.box) return
        _state.update { it.copy(box = box, conversations = emptyList(), loading = true) }
        viewModelScope.launch { load() }
    }

    suspend fun load() {
        val box = _state.value.box
        repo.conversations(box)
            .onSuccess { list ->
                if (_state.value.box == box) _state.update { it.copy(conversations = list.conversations, loading = false, error = null) }
            }
            .onFailure { e -> _state.update { it.copy(loading = false, error = e.message) } }
        refreshBadge()
    }

    suspend fun refreshBadge() {
        repo.badge().onSuccess { b -> _state.update { it.copy(badge = b) } }
    }
}

@Composable
fun InboxScreen(
    /** Null when Messages is a dock tab: no back arrow. */
    onBack: (() -> Unit)?,
    onOpenChat: (conversationId: String) -> Unit,
    viewModel: InboxViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    PollWhileResumed(15_000) { viewModel.load() }

    Column(Modifier.fillMaxSize().background(HL.Paper).statusBarsPadding()) {
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (onBack != null) {
                Box(Modifier.size(44.dp).clip(CircleShape).clickable(onClick = onBack), contentAlignment = Alignment.Center) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Back", tint = HL.Ink)
                }
            }
            Text(
                "Messages",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp,
                color = HL.Ink,
                modifier = Modifier.padding(start = if (onBack != null) 4.dp else 8.dp).heightIn(min = 44.dp).wrapContentHeight()
            )
        }

        Row(Modifier.padding(horizontal = 20.dp, vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BoxChip("CHATS", state.box == "accepted", 0) { viewModel.select("accepted") }
            BoxChip("REQUESTS", state.box == "request", state.badge.requests) { viewModel.select("request") }
        }

        when {
            state.loading && state.conversations.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(26.dp))
            }
            state.conversations.isEmpty() -> EmptyInbox(state.box, state.error)
            // Clear the dock when this is a tab.
            else -> LazyColumn(contentPadding = PaddingValues(bottom = if (onBack == null) 110.dp else 40.dp)) {
                itemsIndexed(state.conversations, key = { _, c -> c.id }) { i, c ->
                    ConversationRow(c) { onOpenChat(c.id) }
                    if (i < state.conversations.lastIndex) {
                        Box(
                            Modifier.fillMaxWidth().padding(start = 86.dp).height(1.dp)
                                .background(HL.Ink.copy(alpha = 0.08f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxChip(label: String, on: Boolean, count: Int, onClick: () -> Unit) {
    Box(
        Modifier
            .brutalPlate(fill = if (on) HL.Ink else HL.Card, borderWidth = 1.5.dp, offset = 2.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            if (count > 0) "$label · $count" else label,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            letterSpacing = 1.4.sp,
            color = if (on) HL.Paper else HL.Ink
        )
    }
}

@Composable
private fun ConversationRow(c: ConversationSummary, onClick: () -> Unit) {
    val unread = c.unreadCount > 0
    Row(
        Modifier.fillMaxWidth()
            .background(if (unread) HL.Paper2.copy(alpha = 0.5f) else HL.Paper)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AvatarImage(
            c.them.avatarUrl, c.them.displayName, 52.dp,
            modifier = if (unread) Modifier.border(2.dp, HL.Ink, CircleShape) else Modifier
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    c.them.displayName,
                    fontSize = 15.sp,
                    fontWeight = if (unread) FontWeight.Bold else FontWeight.SemiBold,
                    color = HL.Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (c.muted) Icon(Icons.Outlined.NotificationsOff, "Muted", tint = HL.Muted, modifier = Modifier.size(14.dp))
            }
            Text(
                (if (c.lastMessage.fromMe) "You: " else "") + c.lastMessage.text,
                fontSize = 13.sp,
                fontWeight = if (unread) FontWeight.SemiBold else FontWeight.Normal,
                color = if (unread) HL.Ink else HL.Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                shortAgo(c.lastMessage.createdAt),
                fontFamily = FontFamily.Monospace, fontSize = 11.sp,
                color = if (unread) HL.Ink else HL.Muted
            )
            if (unread) {
                Box(
                    Modifier.sizeIn(minWidth = 18.dp, minHeight = 18.dp).clip(CircleShape).background(HL.Ink),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        if (c.unreadCount > 9) "9+" else "${c.unreadCount}",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        color = HL.Paper,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            } else {
                Spacer(Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun EmptyInbox(box: String, error: String?) {
    Box(Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
        Text(
            error ?: if (box == "request") "No message requests." else
                "No messages yet. Send a book to someone from its Share button, or tap Message on a reader's profile.",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 15.sp,
            lineHeight = 21.sp,
            color = HL.Muted,
            textAlign = TextAlign.Center
        )
    }
}
