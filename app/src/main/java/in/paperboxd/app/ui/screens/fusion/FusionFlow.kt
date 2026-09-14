package `in`.paperboxd.app.ui.screens.fusion

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.FusionRoute
import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.FusionRepository
import `in`.paperboxd.app.domain.model.FusionBook
import `in`.paperboxd.app.domain.model.FusionPerson
import `in`.paperboxd.app.domain.model.FusionStory
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.screens.wrapped.PbFade
import `in`.paperboxd.app.ui.screens.wrapped.PbSlide
import `in`.paperboxd.app.ui.screens.wrapped.PbStamp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// Opening a Fusion: the invitee's side of the link, the build, and the story.
// Twin of iOS FusionRootView.swift.

/**
 * Lets any screen under the main scaffold open a Fusion (the profile list, the
 * "fused with you" notification) without threading a callback through every
 * screen in between. [active] is the Fusion on screen, if any.
 */
class FusionRouting(val active: FusionRoute?, val open: (FusionRoute) -> Unit)

val LocalFusionRouting = staticCompositionLocalOf { FusionRouting(null) {} }

sealed interface FusionStep {
    data object Loading : FusionStep
    data class Join(val inviter: FusionPerson) : FusionStep
    data class Analyzing(val them: String?) : FusionStep
    data class Joined(val id: String, val them: String) : FusionStep
    data class Story(val story: FusionStory) : FusionStep
    data class Explore(val story: FusionStory) : FusionStep
    data object LowData : FusionStep
    data class Own(val token: String) : FusionStep
    data class Used(val inviter: String) : FusionStep
    data class Expired(val inviter: String) : FusionStep
    data object Unavailable : FusionStep
    data object Gone : FusionStep
    data object Failed : FusionStep
}

@HiltViewModel
class FusionFlowViewModel @Inject constructor(
    private val fusions: FusionRepository,
    private val books: BookRepository
) : ViewModel() {
    private val _step = MutableStateFlow<FusionStep>(FusionStep.Loading)
    val step: StateFlow<FusionStep> = _step.asStateFlow()

    private val _toast = MutableStateFlow<String?>(null)
    val toast: StateFlow<String?> = _toast.asStateFlow()

    private val _fusing = MutableStateFlow(false)
    val fusing: StateFlow<Boolean> = _fusing.asStateFlow()

    private var route: FusionRoute? = null
    private var username: String = ""

    fun start(route: FusionRoute, user: User) {
        if (this.route == route) return
        this.route = route
        username = user.username.orEmpty()
        viewModelScope.launch { run(route) }
    }

    fun retry() {
        val r = route ?: return
        _step.value = FusionStep.Loading
        viewModelScope.launch { run(r) }
    }

    private suspend fun run(route: FusionRoute) = when (route) {
        is FusionRoute.Link -> preview(route.token)
        is FusionRoute.Open ->
            if (route.introFrom != null) _step.value = FusionStep.Joined(route.id, route.introFrom)
            else build(route.id, null)
    }

    private suspend fun preview(token: String) {
        fusions.preview(token).fold(
            onSuccess = { p ->
                val name = p.inviter?.first ?: "your friend"
                when (p.status) {
                    "valid" -> _step.value = p.inviter?.let { FusionStep.Join(it) } ?: FusionStep.Unavailable
                    "joined" -> p.fusionId?.let { build(it, p.inviter?.first) } ?: run { _step.value = FusionStep.Unavailable }
                    "own" -> _step.value = FusionStep.Own(token)
                    "used" -> _step.value = FusionStep.Used(name)
                    "expired" -> _step.value = FusionStep.Expired(name)
                    else -> _step.value = FusionStep.Unavailable
                }
            },
            onFailure = { _step.value = FusionStep.Failed }
        )
    }

    fun fuse(inviter: FusionPerson) {
        val token = (route as? FusionRoute.Link)?.token ?: return
        if (_fusing.value) return
        _fusing.value = true
        viewModelScope.launch {
            fusions.accept(token).fold(
                onSuccess = { build(it, inviter.first) },
                onFailure = { e ->
                    // The 409 carries the link's state; the preview names it the same way.
                    if (e is ApiError.NetworkError) show("Couldn't reach PaperBoxd. Try again.") else preview(token)
                }
            )
            _fusing.value = false
        }
    }

    fun play(id: String, them: String) {
        viewModelScope.launch { build(id, them) }
    }

    /** Shows Analyzing while the story is fetched, long enough to read. */
    private suspend fun build(id: String, them: String?) {
        _step.value = FusionStep.Analyzing(them)
        val started = System.currentTimeMillis()
        val result = fusions.story(id)
        val wait = 2_600 - (System.currentTimeMillis() - started)
        if (wait > 0) delay(wait)
        result.fold(
            onSuccess = { _step.value = if (it.lowData) FusionStep.LowData else FusionStep.Story(it) },
            onFailure = { _step.value = if (it is ApiError.NotFound) FusionStep.Gone else FusionStep.Failed }
        )
    }

    fun explore() {
        (_step.value as? FusionStep.Story)?.let { _step.value = FusionStep.Explore(it.story) }
    }

    fun addToTbr(list: List<FusionBook>) {
        if (username.isEmpty() || list.isEmpty()) return
        viewModelScope.launch {
            val added = list.count { books.addToBookshelf(username, it.id, "to-read").isSuccess }
            show(
                when {
                    added == 0 -> "Couldn't add. Try again."
                    list.size == 1 -> "${list[0].title} added to your TBR"
                    else -> "$added books added to your TBR"
                }
            )
        }
    }

    fun show(message: String) {
        _toast.value = message
        viewModelScope.launch {
            delay(2_400)
            if (_toast.value == message) _toast.value = null
        }
    }
}

@Composable
fun FusionRootScreen(
    route: FusionRoute,
    user: User,
    onClose: () -> Unit,
    viewModel: FusionFlowViewModel = hiltViewModel(key = route.key)
) {
    val step by viewModel.step.collectAsStateWithLifecycle()
    val toast by viewModel.toast.collectAsStateWithLifecycle()
    val fusing by viewModel.fusing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(route) { viewModel.start(route, user) }
    BackHandler(onBack = onClose)

    val actions = FusionActions(toast = viewModel::show, explore = viewModel::explore, addToTbr = viewModel::addToTbr)

    Box(Modifier.fillMaxSize().background(FZ.Bg)) {
        AnimatedContent(
            targetState = step,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
            contentKey = { it::class },
            label = "fusion-step"
        ) { s ->
            when (s) {
                FusionStep.Loading -> Box(Modifier.fillMaxSize().background(FZ.Bg))
                is FusionStep.Join -> FusionJoinScreen(s.inviter, user, fusing, onFuse = { viewModel.fuse(s.inviter) }, onNotNow = onClose)
                is FusionStep.Analyzing -> FusionAnalyzingScreen(user.username?.let { "@$it" }.orEmpty(), s.them)
                is FusionStep.Joined -> FusionJoinedScreen(user.username.orEmpty(), s.them) { viewModel.play(s.id, s.them) }
                is FusionStep.Story -> FusionStoryPlayer(s.story, actions, onClose)
                is FusionStep.Explore -> FusionExploreScreen(s.story, actions, onClose)
                FusionStep.LowData -> FusionStateScreen("Not enough yet", listOf("We need a little", "more reading history."),
                    "Once you have both rated a few books, Fusion can compare your tastes properly. Anything less would be a guess with a number on it.",
                    "Close", onClose)
                is FusionStep.Own -> FusionStateScreen("Your link", listOf("This is your", "own invite."),
                    "A Fusion needs two readers. Send this link to someone else.", "Share link") {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "https://paperboxd.in/fusion/${s.token}")
                    }
                    context.startActivity(Intent.createChooser(send, "Share"))
                }
                is FusionStep.Used -> FusionStateScreen("Used", listOf("Someone already", "used this invite."),
                    "Each link makes one Fusion with one reader. Ask ${s.inviter} for a link of your own.", "Close", onClose)
                is FusionStep.Expired -> FusionStateScreen("Expired", listOf("This invite", "has expired."),
                    "Fusion links last seven days. Ask ${s.inviter} to send a new one.", "Close", onClose)
                FusionStep.Unavailable -> FusionStateScreen("Unavailable", listOf("This invite isn't", "available."),
                    "It may have been cancelled. Nothing was shared.", "Close", onClose)
                FusionStep.Gone -> FusionStateScreen("Unavailable", listOf("This Fusion is", "no longer available."),
                    "One of you removed it, or this reader's account is no longer visible to you.", "Close", onClose)
                FusionStep.Failed -> FusionStateScreen("Failed", listOf("We couldn't build", "this Fusion right now."),
                    "Both taste profiles are intact. We just couldn't put them together. Nothing was lost.", "Try again", viewModel::retry)
            }
        }
        toast?.let {
            FzToast(it, Modifier.align(Alignment.BottomCenter).padding(WindowInsets.safeDrawing.asPaddingValues()).padding(bottom = 72.dp))
        }
    }
}

// MARK: - Their side of the link

@Composable
fun FusionJoinScreen(inviter: FusionPerson, viewer: User, fusing: Boolean, onFuse: () -> Unit, onNotNow: () -> Unit) {
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    Box(Modifier.fillMaxSize().background(FZ.Bg).padding(insets)) {
        FzScreen(top = 40.dp, bottom = 24.dp) {
            Row {
                FzLabel("Fusion invite")
                Spacer(Modifier.weight(1f))
                viewer.username?.let { FzLabel("@$it") }
            }
            FzRule(200, modifier = Modifier.padding(top = 12.dp))
            Row(Modifier.padding(top = 34.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FzDiscs(260)
                PbFade(420) {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        FzLabel("From", 9f)
                        FzHeading(inviter.displayName, 20f)
                        FzLabel("@${inviter.username}", 9f)
                    }
                }
            }
            Box(Modifier.padding(top = 30.dp)) {
                FzStack(listOf("${inviter.first} invited you", "to a Fusion."), 42f, accentLast = true, delayMillis = 520)
            }
            PbFade(780, modifier = Modifier.padding(top = 26.dp)) { FzLabel("${inviter.first} will see", 9f) }
            Column(Modifier.padding(top = 8.dp)) {
                listOf(
                    "Your ratings on books you have both read",
                    "Books you loved that ${inviter.first} has not read",
                    "Where your tastes agree, and where they split",
                ).forEachIndexed { i, line ->
                    FzRule(840 + i * 90)
                    PbSlide(870 + i * 90, distance = 14.dp) {
                        Row(Modifier.padding(vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.width(18.dp).padding(top = 2.dp)) { FzNum(fzIndex(i + 1), 10f, FZ.Accent) }
                            FzBody(line, 13.5f, alpha = 0.86f)
                        }
                    }
                }
                FzRule(1120)
            }
            PbFade(1180, modifier = Modifier.padding(top = 12.dp)) { FzBody("Your diary entries and notes are never included.", 12.5f) }
            Spacer(Modifier.weight(1f))
            PbFade(1260) { FzPill(if (fusing) "Fusing…" else "Fuse with ${inviter.first}", solid = true, enabled = !fusing, onClick = onFuse) }
            PbFade(1320, modifier = Modifier.fillMaxWidth().padding(top = 14.dp)) {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    FzLabel("Not now", 9f, FZ.Ink, Modifier.fzClick(onNotNow).padding(8.dp))
                }
            }
        }
    }
}

private val LEDGER = listOf(
    "Reading two shelves",
    "Comparing the books you have both read",
    "Weighing how you rated them",
    "Measuring nine taste axes",
    "Finding books neither of you has read",
)

/** The work named as a ledger, not a spinner. */
@Composable
fun FusionAnalyzingScreen(me: String, them: String?) {
    var percent by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        // Eases toward 96 and waits there: the real finish is the story.
        while (percent < 96) {
            delay(90)
            percent = minOf(96, percent + maxOf(1, (100 - percent) / 12))
        }
    }
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    Box(Modifier.fillMaxSize().background(FZ.Ink).padding(insets)) {
        FzScreen(dark = true, top = 48.dp, bottom = 32.dp) {
            Row {
                FzLabel("Building your Fusion", color = FZ.MuteOnDark)
                Spacer(Modifier.weight(1f))
                them?.let { FzNum("$me × $it", 11f, FZ.MuteOnDark, bold = false) }
            }
            Box(Modifier.padding(top = 14.dp).fillMaxWidth().height(2.dp).background(FZ.Bg.copy(alpha = 0.2f))) {
                Box(Modifier.fillMaxWidth(percent / 100f).height(2.dp).background(FZ.Accent))
            }
            Spacer(Modifier.weight(1f))
            Row(verticalAlignment = Alignment.Bottom) {
                Text("$percent", color = FZ.Bg, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = 132.sp, lineHeight = 120.sp, letterSpacing = FZ.tracking(132f))
                Text("%", color = FZ.Accent, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = 44.sp, modifier = Modifier.padding(bottom = 18.dp))
            }
            Spacer(Modifier.weight(1f))
            LEDGER.forEachIndexed { i, line ->
                PbFade(i * 500, 460) {
                    Column {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(FZ.Bg.copy(alpha = 0.18f)))
                        Row(Modifier.padding(vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(Modifier.width(18.dp)) { FzNum(fzIndex(i + 1), 10f, FZ.Accent) }
                            FzBody(line, 13f, FZ.Bg, 0.8f)
                        }
                    }
                }
            }
        }
    }
}

/** The inviter's side, once someone has tapped Fuse. The number waits for the story. */
@Composable
fun FusionJoinedScreen(me: String, them: String, onPlay: () -> Unit) {
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    Box(Modifier.fillMaxSize().background(FZ.Ink).padding(insets)) {
        FzScreen(dark = true, top = 48.dp, bottom = 32.dp) {
            FzLabel("Fusion", color = FZ.MuteOnDark)
            FzRule(200, dark = true, modifier = Modifier.padding(top = 12.dp))
            Spacer(Modifier.weight(1f))
            PbStamp(260) {
                Row {
                    FzMonogram(me.take(1).uppercase(), 56.dp, accent = true)
                    FzMonogram(them.take(1).uppercase(), 56.dp, onDark = true)
                }
            }
            Box(Modifier.padding(top = 30.dp)) { FzStack(listOf("$them fused", "with you."), 46f, FZ.Bg, accentLast = true, delayMillis = 420) }
            FzRule(700, dark = true, modifier = Modifier.padding(vertical = 20.dp))
            PbFade(780) {
                FzBody("Your Fusion is built. One number, where you agree and split, and a book neither of you has read.", 15f, FZ.Bg, 0.8f)
            }
            Spacer(Modifier.weight(1f))
            PbFade(920) { FzPill("Play your Fusion", solid = true, light = true, onClick = onPlay) }
        }
    }
}

@Composable
fun FusionStateScreen(label: String, head: List<String>, message: String, cta: String, action: () -> Unit) {
    val insets = WindowInsets.safeDrawing.asPaddingValues()
    Box(Modifier.fillMaxSize().background(FZ.Bg).padding(insets)) {
        FzScreen(top = 40.dp, bottom = 24.dp) {
            Row {
                FzLabel("Fusion")
                Spacer(Modifier.weight(1f))
                FzLabel(label)
            }
            FzRule(200, modifier = Modifier.padding(top = 12.dp))
            Spacer(Modifier.weight(1f))
            FzStack(head, 34f, delayMillis = 300)
            FzRule(560, modifier = Modifier.padding(vertical = 18.dp))
            PbFade(640) { FzBody(message, 14.5f) }
            Spacer(Modifier.weight(1f))
            PbFade(780) { FzPill(cta, solid = true, onClick = action) }
        }
    }
}
