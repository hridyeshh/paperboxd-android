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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.LeaderboardEntry
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.ShimmerBox
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary

@Composable
fun LeaderboardScreen(
    viewer: User,
    onOpenProfile: (String) -> Unit,
    viewModel: LeaderboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            Text(
                stringResource(R.string.leaderboard_title),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
            )
            DimensionTabs(selected = state.tab, onSelect = viewModel::onTabSelected)
            Spacer(Modifier.height(10.dp))

            when {
                state.isLoading -> LoadingList()
                state.errorMessage != null -> Text(
                    state.errorMessage.orEmpty(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorColor,
                    modifier = Modifier.padding(20.dp)
                )
                else -> LazyColumn(contentPadding = PaddingValues(bottom = 170.dp)) {
                    if (state.entries.size >= 3) {
                        item { Podium(state, onOpenProfile) }
                    }
                    itemsIndexed(
                        state.entries.drop(if (state.entries.size >= 3) 3 else 0),
                        key = { _, e -> e.userId }
                    ) { index, entry ->
                        val rank = index + if (state.entries.size >= 3) 4 else 1
                        EntryRow(rank, entry, state.tab, onClick = { onOpenProfile(entry.username) })
                    }
                }
            }
        }

        // Floating "your rank" bar
        state.myStats?.let { mine ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 20.dp, end = 20.dp, bottom = 84.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    state.myRank?.let { stringResource(R.string.leaderboard_your_rank, it) }
                        ?: stringResource(R.string.leaderboard_unranked),
                    style = MaterialTheme.typography.titleMedium,
                    color = Accent
                )
                Spacer(Modifier.weight(1f))
                Text(
                    state.tab.value(mine).toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
private fun DimensionTabs(selected: LeaderboardTab, onSelect: (LeaderboardTab) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LeaderboardTab.entries.forEach { tab ->
            val active = tab == selected
            Text(
                text = tabLabel(tab),
                style = MaterialTheme.typography.labelMedium,
                color = if (active) Background else TextSecondary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (active) Accent else Surface)
                    .clickable { onSelect(tab) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun tabLabel(tab: LeaderboardTab): String = when (tab) {
    LeaderboardTab.Global -> stringResource(R.string.leaderboard_all_time)
    LeaderboardTab.Books -> stringResource(R.string.leaderboard_books)
    LeaderboardTab.Pages -> stringResource(R.string.leaderboard_pages)
    LeaderboardTab.Streak -> stringResource(R.string.leaderboard_streak)
    LeaderboardTab.Diary -> stringResource(R.string.leaderboard_diary)
    LeaderboardTab.Friends -> stringResource(R.string.leaderboard_friends)
}

@Composable
private fun Podium(state: LeaderboardUiState, onOpenProfile: (String) -> Unit) {
    val top = state.entries.take(3)
    // Order: 2nd, 1st, 3rd
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        PodiumSpot(top[1], 2, state.tab, 64.dp, Modifier.weight(1f)) { onOpenProfile(top[1].username) }
        PodiumSpot(top[0], 1, state.tab, 84.dp, Modifier.weight(1f)) { onOpenProfile(top[0].username) }
        PodiumSpot(top[2], 3, state.tab, 56.dp, Modifier.weight(1f)) { onOpenProfile(top[2].username) }
    }
}

@Composable
private fun PodiumSpot(
    entry: LeaderboardEntry,
    rank: Int,
    tab: LeaderboardTab,
    avatarSize: Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        AvatarImage(url = null, name = entry.username, size = avatarSize)
        Spacer(Modifier.height(6.dp))
        Text(
            "#$rank",
            style = MaterialTheme.typography.labelMedium,
            color = if (rank == 1) Accent else TextSecondary
        )
        Text(
            entry.username,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            maxLines = 1
        )
        Text(
            tab.value(entry).toString(),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Accent
        )
    }
}

@Composable
private fun EntryRow(rank: Int, entry: LeaderboardEntry, tab: LeaderboardTab, onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 9.dp)
    ) {
        Text(
            "#$rank",
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            modifier = Modifier.width(40.dp)
        )
        AvatarImage(url = null, name = entry.username, size = 34.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(entry.username, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, maxLines = 1)
            if (entry.levelName.isNotEmpty()) {
                Text(entry.levelName, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
        Text(
            tab.value(entry).toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
    }
}

@Composable
private fun LoadingList() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        repeat(8) {
            ShimmerBox(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
        }
    }
}
