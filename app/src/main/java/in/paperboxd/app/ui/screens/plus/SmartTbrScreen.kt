package `in`.paperboxd.app.ui.screens.plus

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.billing.PlusBilling
import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.AddToBookshelfBody
import `in`.paperboxd.app.domain.model.SmartTbr
import `in`.paperboxd.app.domain.model.SmartTbrItem
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.screens.jazy.hardShadow
import `in`.paperboxd.app.ui.screens.scan.MonoLabel
import `in`.paperboxd.app.ui.screens.scan.SK
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Smart TBR — "what should I read next?" — iOS twin: `SmartTBRView`. */
data class SmartTbrUiState(
    val data: SmartTbr? = null,
    val errorMessage: String? = null,
    val showPaywall: Boolean = false,
    /** Books answered on the stale list this session → "Kept" / "Removed". */
    val settled: Map<String, String> = emptyMap()
)

@HiltViewModel
class SmartTbrViewModel @Inject constructor(
    private val api: ApiService,
    val billing: PlusBilling
) : ViewModel() {
    private val _state = MutableStateFlow(SmartTbrUiState())
    val state: StateFlow<SmartTbrUiState> = _state.asStateFlow()

    init {
        billing.track("premium_feature_viewed", "smart_tbr")
        load()
    }

    fun load() {
        viewModelScope.launch {
            safeApiCall { api.smartTbr() }
                .onSuccess { d -> _state.update { it.copy(data = d, errorMessage = null, showPaywall = false) } }
                .onFailure { e ->
                    _state.update {
                        if (e is ApiError.SubscriptionRequired) it.copy(showPaywall = true)
                        else it.copy(errorMessage = e.message ?: "Shelf jammed — try again")
                    }
                }
        }
    }

    fun setPaywall(visible: Boolean) = _state.update { it.copy(showPaywall = visible) }

    fun startReading(username: String, bookId: String) {
        viewModelScope.launch {
            safeApiCall { api.addToBookshelf(username, AddToBookshelfBody(bookId = bookId, status = "reading")) }
            load()
        }
    }

    fun keep(bookId: String) {
        _state.update { it.copy(settled = it.settled + (bookId to "Kept")) }
        viewModelScope.launch { safeApiCall { api.keepTbr(bookId) } }
    }

    fun remove(username: String, bookId: String) {
        _state.update { it.copy(settled = it.settled + (bookId to "Removed")) }
        viewModelScope.launch { safeApiCall { api.removeFromBookshelf(username, bookId) } }
    }
}

@Composable
fun SmartTbrScreen(
    user: User,
    onClose: () -> Unit,
    onOpenBook: (String) -> Unit,
    viewModel: SmartTbrViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val username = user.username.orEmpty()

    Box(Modifier.fillMaxSize().background(SK.bg)) {
        LazyColumn(contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 24.dp)) {
            item { Header(onClose) }
            val data = state.data
            when {
                data == null && state.errorMessage != null -> item { Empty(state.errorMessage!!) }
                data == null -> item {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    ) {
                        CircularProgressIndicator(color = SK.ink, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
                        MonoLabel("Reading your shelf", size = 10f, tracking = 1.6f, color = SK.faint)
                    }
                }
                data.total == 0 -> item { Empty("Nothing on your TBR yet. Add a few books and Jazy will order them.") }
                else -> {
                    data.next?.let { next ->
                        item {
                            NextCard(
                                item = next,
                                onStart = { viewModel.startReading(username, next.id) },
                                onOpen = { onOpenBook(next.id) }
                            )
                        }
                    }
                    if (data.queue.size > 1) {
                        item { SectionHead("My next 5") }
                        items(data.queue.withIndex().toList(), key = { "q-" + it.value.id }) { (i, item) ->
                            ItemRow(item, index = i + 1, onClick = { onOpenBook(item.id) })
                        }
                        item { Spacer(Modifier.height(24.dp)) }
                    }
                    listOf("read_soon" to "Read soon", "maybe" to "Maybe").forEach { (key, title) ->
                        val list = data.buckets[key].orEmpty()
                        if (list.isNotEmpty()) {
                            item { SectionHead(title) }
                            items(list, key = { "$key-" + it.id }) { ItemRow(it, onClick = { onOpenBook(it.id) }) }
                            item { Spacer(Modifier.height(24.dp)) }
                        }
                    }
                    val stale = data.buckets["stale"].orEmpty()
                    if (stale.isNotEmpty()) {
                        item { SectionHead("Still want these?", note = "Untouched for a year. Keep, or let them go.") }
                        items(stale, key = { "stale-" + it.id }) { item ->
                            StaleRow(
                                item = item,
                                answer = state.settled[item.id],
                                onKeep = { viewModel.keep(item.id) },
                                onRemove = { viewModel.remove(username, item.id) }
                            )
                        }
                    }
                }
            }
        }

        if (state.showPaywall) {
            PlusPaywallSheet(
                billing = viewModel.billing,
                feature = PlusFeature.Taste,
                onUnlocked = { viewModel.setPaywall(false); viewModel.load() },
                onDismiss = { viewModel.setPaywall(false); onClose() }
            )
        }
    }
}

@Composable
private fun Header(onClose: () -> Unit) {
    Column(Modifier.padding(bottom = 18.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            MonoLabel("Smart TBR · Plus", size = 9.5f, tracking = 2.2f, color = SK.faint)
            Spacer(Modifier.weight(1f))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(32.dp).border(1.5.dp, SK.ink).clickable(onClick = onClose)
            ) { Icon(Icons.Outlined.Close, contentDescription = "Close", tint = SK.ink, modifier = Modifier.size(16.dp)) }
        }
        Text(
            "Read this next.",
            fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 30.sp,
            color = SK.ink, modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun NextCard(item: SmartTbrItem, onStart: () -> Unit, onOpen: () -> Unit) {
    Column(
        Modifier
            .padding(end = 5.dp, bottom = 24.dp)
            .hardShadow(SK.ink, 5.dp)
            .background(SK.panel)
            .border(2.dp, SK.ink)
            .padding(18.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Cover(item, 84.dp)
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MonoLabel(item.confidence ?: "Top of your shelf", size = 9f, tracking = 1.6f, color = SK.accent)
                Text(item.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 21.sp,
                    lineHeight = 26.sp, color = SK.ink, maxLines = 3, overflow = TextOverflow.Ellipsis)
                Text(item.authors.joinToString(", "), fontSize = 12.5.sp, color = SK.sub)
            }
        }
        item.reason?.takeIf { it.isNotEmpty() }?.let {
            Text(it, fontFamily = FontFamily.Serif, fontStyle = FontStyle.Italic, fontSize = 15.sp, lineHeight = 21.sp,
                color = SK.ink, modifier = Modifier.padding(top = 14.dp))
        }
        item.lines.forEach { MonoLabel(it, size = 9.5f, tracking = 1.2f, color = SK.sub, modifier = Modifier.padding(top = 6.dp)) }
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.padding(top = 18.dp, end = 4.dp, bottom = 4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.weight(1f).height(46.dp).hardShadow(SK.ink.copy(alpha = 0.78f))
                    .background(SK.ink).border(2.dp, SK.ink).clickable(onClick = onStart)
            ) { MonoLabel("Start reading", size = 11f, tracking = 1.4f, color = SK.panel, weight = FontWeight.SemiBold) }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.width(90.dp).height(46.dp).hardShadow(SK.ink.copy(alpha = 0.78f))
                    .background(SK.panel).border(2.dp, SK.ink).clickable(onClick = onOpen)
            ) { MonoLabel("Open", size = 11f, tracking = 1.4f, color = SK.ink, weight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun SectionHead(title: String, note: String? = null) {
    Column {
        MonoLabel(title, size = 10f, tracking = 2f, color = SK.ink, weight = FontWeight.SemiBold)
        note?.let { Text(it, fontSize = 12.5.sp, color = SK.sub, modifier = Modifier.padding(top = 4.dp)) }
        Box(Modifier.padding(top = 8.dp).fillMaxWidth().height(2.dp).background(SK.ink))
    }
}

@Composable
private fun ItemRow(item: SmartTbrItem, index: Int? = null, onClick: () -> Unit) {
    Column(Modifier.clickable(onClick = onClick)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
        ) {
            index?.let { MonoLabel(String.format("%02d", it), size = 10f, tracking = 1f, color = SK.faint) }
            Cover(item, 34.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(item.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                    color = SK.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.lines.firstOrNull() ?: item.authors.joinToString(", "), fontSize = 11.5.sp, color = SK.sub,
                    maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            item.confidence?.let { MonoLabel(it, size = 8.5f, tracking = 1.2f, color = SK.faint) }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(SK.ink.copy(alpha = 0.12f)))
    }
}

@Composable
private fun StaleRow(item: SmartTbrItem, answer: String?, onKeep: () -> Unit, onRemove: () -> Unit) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
        ) {
            Cover(item, 34.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(item.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 15.sp,
                    color = SK.ink, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(item.lines.firstOrNull().orEmpty(), fontSize = 11.5.sp, color = SK.sub, maxLines = 1)
            }
            if (answer != null) {
                MonoLabel(answer, size = 9f, tracking = 1.4f, color = SK.faint)
            } else {
                MonoLabel("Keep", size = 10f, tracking = 1.2f, color = SK.ink, weight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onKeep).padding(6.dp))
                MonoLabel("Remove", size = 10f, tracking = 1.2f, color = SK.accent, weight = FontWeight.SemiBold,
                    modifier = Modifier.clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onRemove).padding(6.dp))
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(SK.ink.copy(alpha = 0.12f)))
    }
}

@Composable
private fun Cover(item: SmartTbrItem, width: Dp) {
    Box(Modifier.size(width, width * 1.5f).background(SK.spines[item.id.hashCode().mod(SK.spines.size)]).border(1.5.dp, SK.ink).clipToBounds()) {
        item.coverUrl?.let {
            AsyncImage(model = it, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun Empty(text: String) {
    Text(text, fontSize = 14.sp, color = SK.sub, textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(top = 80.dp))
}
