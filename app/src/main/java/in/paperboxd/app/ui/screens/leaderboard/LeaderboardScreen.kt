package `in`.paperboxd.app.ui.screens.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.domain.model.LeaderboardEntry
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.theme.Terracotta

/** Extra light tokens from "Leaderboard - Brutalist Mobile.html". */
private val Soft = Color(0xFFE9E2D1)
private val Line = Color(0xFFE6DFD0)

private val LeaderboardTab.label: String
    get() = if (this == LeaderboardTab.Global) "All-time" else name

private val LeaderboardTab.unit: String
    get() = when (this) {
        LeaderboardTab.Global, LeaderboardTab.Friends -> "xp"
        LeaderboardTab.Books -> "books"
        LeaderboardTab.Pages -> "pages"
        LeaderboardTab.Streak -> "days"
        LeaderboardTab.Diary -> "entries"
    }

private fun formatValue(n: Int): String = when {
    n >= 1_000_000 -> "%.1fM".format(n / 1_000_000.0)
    n >= 1_000 -> "%.1fK".format(n / 1_000.0)
    else -> "$n"
}

/**
 * Leaderboard — light-mode-only brutalist paper screen, iOS LeaderboardView twin.
 * Podium (2·1·3) + ranked list + floating "your rank" ink bar.
 */
@Composable
fun LeaderboardScreen(
    viewer: User,
    onOpenProfile: (String) -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as? android.app.Activity ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(activity.window, view)
        val wasLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = true
        onDispose { controller.isAppearanceLightStatusBars = wasLight }
    }

    LeaderboardContent(
        state = state,
        onTabSelected = viewModel::onTabSelected,
        onRefresh = viewModel::refresh,
        onOpenProfile = onOpenProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardContent(
    state: LeaderboardUiState,
    onTabSelected: (LeaderboardTab) -> Unit,
    onRefresh: () -> Unit = {},
    onOpenProfile: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(HL.Paper)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(top = 12.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                EyebrowText("Ranked by devotion")
                Text(
                    "The Reading Order",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = HL.Ink
                )
            }

            TabPills(selected = state.tab, onSelect = onTabSelected)

            when {
                state.isLoading && state.entries.isEmpty() -> Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                }
                state.errorMessage != null && state.entries.isEmpty() ->
                    ErrorState(state.errorMessage ?: "", onRefresh)
                state.entries.isEmpty() -> EmptyState(state.tab)
                else -> PullToRefreshBox(isRefreshing = false, onRefresh = onRefresh) {
                    RankedContent(state, onOpenProfile)
                }
            }
        }

        state.myStats?.let { me ->
            YourRankBar(
                me = me,
                rank = state.myRank,
                onClick = { onOpenProfile(me.username) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 96.dp)
            )
        }
    }
}

// ---- Tabs ----

@Composable
private fun TabPills(selected: LeaderboardTab, onSelect: (LeaderboardTab) -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 18.dp)
            .padding(top = 6.dp, bottom = 16.dp)
            .clip(CircleShape)
            .background(Soft)
            .padding(6.dp)
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        LeaderboardTab.entries.forEach { tab ->
            val on = tab == selected
            Text(
                text = tab.label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (on) HL.Ink else HL.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (on) HL.Card else Color.Transparent)
                    .clickable { onSelect(tab) }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            )
        }
    }
}

// ---- Ranked content: podium + list ----

@Composable
private fun RankedContent(state: LeaderboardUiState, onOpenProfile: (String) -> Unit) {
    val top3 = state.entries.take(3)
    val hasPodium = top3.size == 3
    val rest = if (hasPodium) state.entries.drop(3) else state.entries

    LazyColumn(
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 180.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        if (hasPodium) {
            item {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    Box(Modifier.weight(1f)) { PodiumCard(top3[1], 2, state.tab) { onOpenProfile(top3[1].username) } }
                    Box(Modifier.weight(1f)) { PodiumCard(top3[0], 1, state.tab) { onOpenProfile(top3[0].username) } }
                    Box(Modifier.weight(1f)) { PodiumCard(top3[2], 3, state.tab) { onOpenProfile(top3[2].username) } }
                }
            }
        }
        item {
            Column {
                val startRank = if (hasPodium) 4 else 1
                rest.forEachIndexed { idx, entry ->
                    val isMe = entry.userId == state.myStats?.userId
                    RankRow(
                        entry = entry,
                        rank = startRank + idx,
                        isMe = isMe,
                        tab = state.tab,
                        onClick = { onOpenProfile(entry.username) }
                    )
                    val nextIsMe = idx + 1 < rest.size && rest[idx + 1].userId == state.myStats?.userId
                    if (idx < rest.size - 1 && !isMe && !nextIsMe) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = 59.dp)
                                .height(1.dp)
                                .background(Line)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PodiumCard(entry: LeaderboardEntry, rank: Int, tab: LeaderboardTab, onClick: () -> Unit) {
    val isFirst = rank == 1

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .brutalPlate(
                offset = if (isFirst) 6.dp else 5.dp,
                shadow = if (isFirst) HL.Accent else HL.Ink
            )
            .clickable(onClick = onClick)
            .padding(top = if (isFirst) 18.dp else 12.dp, bottom = 11.dp)
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (isFirst) 26.dp else 22.dp)
                .background(if (isFirst) HL.Accent else HL.Ink),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "$rank",
                fontSize = if (isFirst) 14.sp else 12.sp,
                fontWeight = FontWeight.Black,
                color = HL.Card
            )
        }
        Spacer(Modifier.height(10.dp))
        LbAvatar(entry.username, if (isFirst) 50.dp else 40.dp)
        Spacer(Modifier.height(8.dp))
        Text(
            entry.username.substringBefore(' '),
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Bold,
            color = HL.Ink,
            maxLines = 1
        )
        Spacer(Modifier.height(3.dp))
        Text(
            formatValue(tab.value(entry)),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = HL.Ink
        )
        Spacer(Modifier.height(2.dp))
        Text(
            tab.unit.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            letterSpacing = 0.8.sp,
            color = HL.Muted
        )
    }
}

@Composable
private fun RankRow(
    entry: LeaderboardEntry,
    rank: Int,
    isMe: Boolean,
    tab: LeaderboardTab,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(if (isMe) 6.dp else 0.dp)
            .let { if (isMe) it.brutalPlate(offset = 4.dp) else it }
            .clickable(onClick = onClick)
            .padding(horizontal = if (isMe) 12.dp else 14.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Text(
            "$rank",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = HL.Muted,
            modifier = Modifier.width(26.dp)
        )
        LbAvatar(entry.username, 38.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    entry.username,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HL.Ink,
                    maxLines = 1
                )
                if (isMe) {
                    Text(
                        "YOU",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 8.sp,
                        letterSpacing = 0.6.sp,
                        color = HL.Card,
                        modifier = Modifier.background(HL.Accent).padding(horizontal = 5.dp, vertical = 2.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                Text(entry.levelName, fontSize = 11.sp, color = HL.Muted, maxLines = 1)
                Text("·", color = HL.Muted.copy(alpha = 0.5f))
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = Terracotta,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    "${entry.currentStreak}d",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Terracotta
                )
            }
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                formatValue(tab.value(entry)),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = HL.Ink
            )
            if (tab == LeaderboardTab.Global) {
                Text(
                    " xp",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = HL.Muted
                )
            }
        }
    }
}

// ---- Your rank bar ----

@Composable
private fun YourRankBar(
    me: LeaderboardEntry,
    rank: Int?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .brutalPlate(fill = HL.Ink, offset = 5.dp, shadow = HL.Accent)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "RANK",
                fontSize = 8.5.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp,
                color = HL.Paper.copy(alpha = 0.6f)
            )
            Text(
                rank?.toString() ?: "—",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp,
                color = HL.Paper
            )
        }
        Box(Modifier.width(1.dp).height(32.dp).background(HL.Paper.copy(alpha = 0.18f)))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                me.username,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = HL.Paper,
                maxLines = 1
            )
            Text(
                "${me.levelName} · ${formatValue(me.totalXp)} XP",
                fontSize = 11.sp,
                color = HL.Paper.copy(alpha = 0.62f),
                maxLines = 1
            )
        }
        Row(
            modifier = Modifier
                .clip(CircleShape)
                .background(HL.Paper.copy(alpha = 0.14f))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(Icons.Filled.Bolt, contentDescription = null, tint = HL.Paper, modifier = Modifier.size(12.dp))
            Text("Profile", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = HL.Paper)
        }
    }
}

// ---- Initials avatar (gradient by username hash) — iOS LBAvatar twin ----

private val LbGradients = listOf(
    listOf(Color(0xFF3B7AD6), Color(0xFF1A1A5E)),
    listOf(Color(0xFFB85C38), Color(0xFF7A3821)),
    listOf(Color(0xFF59804F), Color(0xFF2E4A26)),
    listOf(Color(0xFFA88A40), Color(0xFF5C4A1A)),
    listOf(Color(0xFF6B4D99), Color(0xFF3B2161)),
    listOf(Color(0xFF297D8A), Color(0xFF143B42)),
    listOf(Color(0xFFBF382B), Color(0xFF7A1A14)),
    listOf(Color(0xFF17A185), Color(0xFF0D594A)),
)

@Composable
fun LbAvatar(username: String, size: Dp) {
    var hash = 0
    for (c in username) hash = (hash * 31 + c.code) and 0x7fffffff
    val colors = LbGradients[hash % LbGradients.size]
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors))
            .border(1.dp, Line, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            username.take(2).uppercase(),
            fontSize = (size.value * 0.34f).sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

// ---- States ----

@Composable
private fun EmptyState(tab: LeaderboardTab) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            if (tab == LeaderboardTab.Friends) "No friends on the board yet" else "No data yet",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = HL.Ink
        )
        Spacer(Modifier.height(10.dp))
        Text(
            if (tab == LeaderboardTab.Friends) "Follow some readers to see how you stack up."
            else "Start reading to claim your spot.",
            fontSize = 13.sp,
            color = HL.Muted,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            message,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            color = HL.Muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(14.dp))
        Text(
            "Retry",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = HL.Accent,
            modifier = Modifier.clickable(onClick = onRetry)
        )
    }
}
