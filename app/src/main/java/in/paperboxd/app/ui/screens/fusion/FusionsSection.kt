package `in`.paperboxd.app.ui.screens.fusion

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.FusionRepository
import `in`.paperboxd.app.domain.model.FusionInvite
import `in`.paperboxd.app.domain.model.FusionList
import `in`.paperboxd.app.domain.model.FusionSummary
import `in`.paperboxd.app.ui.screens.wrapped.PbFade
import `in`.paperboxd.app.ui.screens.wrapped.PbSlide
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Your side of Fusion: the Create sheet (landing → link, then your Fusions),
// opened from the profile menu. Twin of iOS FusionsSection.swift and the design
// prototype's FLanding / FInvite / FList.

@HiltViewModel
class FusionsViewModel @Inject constructor(
    private val repository: FusionRepository
) : ViewModel() {
    private val _list = MutableStateFlow(FusionList())
    val list: StateFlow<FusionList> = _list.asStateFlow()

    private val _creating = MutableStateFlow(false)
    val creating: StateFlow<Boolean> = _creating.asStateFlow()

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()

    fun load() {
        viewModelScope.launch { repository.list().onSuccess { _list.value = it } }
    }

    fun create(onReady: (FusionInvite) -> Unit) {
        if (_creating.value) return
        _creating.value = true
        viewModelScope.launch {
            repository.createInvite()
                .onSuccess { _list.value = _list.value.copy(invite = it); onReady(it) }
                .onFailure { flash("Couldn't make a link. Try again.") }
            _creating.value = false
        }
    }

    fun cancel(invite: FusionInvite, then: () -> Unit = {}) {
        viewModelScope.launch {
            repository.cancelInvite(invite.token)
                .onSuccess { _list.value = _list.value.copy(invite = null); flash("Link cancelled"); then() }
                .onFailure { flash("Couldn't cancel the link") }
        }
    }

    fun flash(message: String) {
        _toast.value = message
        viewModelScope.launch {
            delay(2_200)
            if (_toast.value == message) _toast.value = null
        }
    }
}

private fun copyLink(context: Context, url: String) {
    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    cm.setPrimaryClip(ClipData.newPlainText("Fusion link", url))
}

private fun shareLink(context: Context, url: String) {
    val send = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, "Fuse with me on PaperBoxd: $url")
    }
    context.startActivity(Intent.createChooser(send, "Share"))
}

/**
 * "Create a Fusion", opened from the profile menu: the landing, or your live
 * link once made, with the Fusions you already have listed underneath.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FusionCreateSheet(
    myInitial: String,
    onOpenFusion: (FusionSummary) -> Unit,
    onDismiss: () -> Unit,
    viewModel: FusionsViewModel = hiltViewModel()
) {
    val list by viewModel.list.collectAsStateWithLifecycle()
    val creating by viewModel.creating.collectAsStateWithLifecycle()
    val toast by viewModel.toast.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { viewModel.load() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = FZ.Bg,
        dragHandle = null
    ) {
        Column(Modifier.fillMaxWidth().fillMaxHeight(0.92f)) {
            Box(Modifier.fillMaxWidth().weight(1f)) {
                val inv = list.invite
                if (inv == null) {
                    FusionLandingScreen(creating) { viewModel.create {} }
                } else {
                    // Cancelling drops back to the landing; list.invite clears.
                    FusionInviteScreen(inv, viewModel, onCancelled = {})
                }
                toast?.let { FzToast(it, Modifier.align(Alignment.BottomCenter).padding(bottom = 64.dp)) }
            }
            if (list.fusions.isNotEmpty()) {
                YourFusions(list.fusions, myInitial, onOpenFusion)
            }
        }
    }
}

@Composable
private fun YourFusions(fusions: List<FusionSummary>, myInitial: String, onOpen: (FusionSummary) -> Unit) {
    Column(
        Modifier.fillMaxWidth().heightIn(max = 240.dp).border(1.dp, FZ.Ink)
            .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            FzLabel("Your Fusions", 9.5f, FZ.Accent)
            Spacer(Modifier.weight(1f))
            FzLabel("${fusions.size} ${if (fusions.size == 1) "reader" else "readers"}", 9f)
        }
        fusions.forEach { f ->
            Row(
                Modifier.padding(top = 12.dp).fillMaxWidth().fzClick { onOpen(f) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row {
                    FzMonogram(myInitial, 30.dp, accent = true)
                    FzMonogram(f.them.initial, 30.dp)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    FzHeading(f.them.displayName, 17f, maxLines = 1)
                    FzLabel("@${f.them.username}", 9f)
                }
                FzNum(f.score?.let { "$it%" } ?: "New", 22f, FZ.Accent)
            }
        }
    }
}

@Composable
fun FusionLandingScreen(creating: Boolean, onCreate: () -> Unit) {
    FzScreen(top = 28.dp, bottom = 28.dp) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text("PaperBoxd", color = FZ.Ink, fontFamily = FZ.Script, fontSize = 28.sp)
            Spacer(Modifier.weight(1f))
            FzLabel("Fusion")
        }
        FzRule(200, modifier = Modifier.padding(top = 12.dp))
        Spacer(Modifier.weight(1f))
        Box(Modifier.padding(bottom = 34.dp)) { FzDiscs(260) }
        FzStack(listOf("Two tastes.", "One discovery."), 46f, accentLast = true, delayMillis = 420, stepMillis = 90)
        FzRule(720, modifier = Modifier.padding(top = 24.dp, bottom = 18.dp))
        PbFade(800) { FzBody("Send a link to one reader. When they open it and tap Fuse, PaperBoxd puts your two shelves side by side.", 15f) }
        Spacer(Modifier.weight(1f))
        PbFade(960) { FzPill(if (creating) "Making your link…" else "Create a Fusion", solid = true, enabled = !creating, onClick = onCreate) }
        PbFade(1040, modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { FzLabel("A Fusion is private until you share it", 9f) }
        }
    }
}

/** The link, and what happens to it: a ledger, like Analyzing. */
@Composable
fun FusionInviteScreen(invite: FusionInvite, viewModel: FusionsViewModel, onCancelled: () -> Unit) {
    val context = LocalContext.current
    FzScreen(top = 28.dp, bottom = 28.dp) {
        Row {
            FzLabel("Create a Fusion")
            Spacer(Modifier.weight(1f))
            FzLabel("Invite")
        }
        FzRule(160, modifier = Modifier.padding(top = 12.dp))
        Box(Modifier.padding(top = 24.dp)) { FzStack(listOf("Send this to", "one reader."), 36f, accentLast = true, delayMillis = 220) }
        PbFade(420, modifier = Modifier.padding(top = 14.dp)) {
            FzBody("The first person to open it and tap Fuse becomes your Fusion. It works once.", 14f)
        }
        PbFade(540, modifier = Modifier.padding(top = 26.dp)) {
            Row(Modifier.fillMaxWidth().border(1.dp, FZ.Ink), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    invite.url.removePrefix("https://"), color = FZ.Ink, fontFamily = FZ.Sans, fontWeight = FontWeight.Medium,
                    fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f).padding(horizontal = 14.dp, vertical = 15.dp)
                )
                Box(Modifier.width(1.dp).height(48.dp).background(FZ.Ink))
                FzLabel("Copy", 10f, FZ.Ink, Modifier.fzClick { copyLink(context, invite.url); viewModel.flash("Fusion link copied") }.padding(horizontal = 16.dp, vertical = 16.dp))
            }
        }
        PbFade(620, modifier = Modifier.padding(top = 10.dp)) {
            FzPill("Share link", solid = true) { shareLink(context, invite.url) }
        }
        Column(Modifier.padding(top = 30.dp)) {
            listOf(Triple("Link made", true, "Ready"), Triple("Waiting for them to open it", false, null), Triple("Your Fusion builds", false, null))
                .forEachIndexed { i, (text, done, trailing) ->
                    FzRule(760 + i * 110)
                    PbSlide(800 + i * 110, distance = 14.dp) {
                        Row(Modifier.padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.width(18.dp)) { FzNum(fzIndex(i + 1), 10f, if (done) FZ.Accent else FZ.Grey) }
                            FzBody(text, 13.5f, alpha = if (done || i == 1) 0.86f else 0.45f, modifier = Modifier.weight(1f))
                            trailing?.let { FzLabel(it, 9f) }
                            if (i == 1) PulseDot()
                        }
                    }
                }
            FzRule(1100)
        }
        Spacer(Modifier.weight(1f))
        PbFade(1180) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FzLabel("Single use · expires in ${invite.daysLeft} ${if (invite.daysLeft == 1) "day" else "days"}", 9f)
                Spacer(Modifier.weight(1f))
                FzLabel("Cancel link", 9f, FZ.Ink, Modifier.fzClick { viewModel.cancel(invite, onCancelled) })
            }
        }
    }
}

@Composable
private fun PulseDot() {
    var on by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(550)
            on = !on
        }
    }
    Box(Modifier.size(7.dp).background(if (on) FZ.Accent else FZ.Accent.copy(alpha = 0f)))
}
