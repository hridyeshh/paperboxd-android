package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.RatingPicker
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary

@Composable
fun ProfileScreen(
    username: String,
    viewer: User,
    showBack: Boolean,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenDiaryEntry: (String) -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    var followSheetMode by rememberSaveable { mutableStateOf<FollowListMode?>(null) }

    LaunchedEffect(username) { viewModel.start(username, viewer) }
    LaunchedEffect(Unit) { viewModel.toast.collect { snackbar.showSnackbar(it) } }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 110.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Header(
                    state = state,
                    isOwn = viewModel.isOwnProfile,
                    showBack = showBack,
                    onBack = onBack,
                    onFollow = viewModel::toggleFollow,
                    onOpenSettings = onOpenSettings,
                    onOpenEditProfile = onOpenEditProfile,
                    onStats = { followSheetMode = it }
                )
            }

            state.profile?.let {
                if (state.favoriteBooks.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                            Text(
                                stringResource(R.string.profile_favorites),
                                style = MaterialTheme.typography.headlineSmall,
                                color = TextPrimary
                            )
                            Spacer(Modifier.height(10.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                state.favoriteBooks.take(4).forEach { fav ->
                                    BookCoverImage(
                                        url = fav.coverUrl,
                                        title = fav.title,
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(2f / 3f)
                                            .clickable { onOpenBook(fav.bookId) },
                                        cornerRadius = 6.dp
                                    )
                                }
                            }
                        }
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    TabDock(selected = state.selectedTab, onSelect = viewModel::onTabSelected)
                }

                when (state.selectedTab) {
                    ProfileTab.Bookshelf -> {
                        if (state.shelfBooks.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EmptyTab(stringResource(R.string.profile_empty_shelf))
                            }
                        } else {
                            itemsIndexed(state.shelfBooks, key = { _, b -> b.id }) { _, book ->
                                LaunchedEffect(book.id) { viewModel.fetchShelfIfNeeded(book) }
                                BookCoverImage(
                                    url = book.coverUrl,
                                    title = book.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f / 3f)
                                        .clickable { onOpenBook(book.id) },
                                    cornerRadius = 6.dp
                                )
                            }
                        }
                    }
                    ProfileTab.Diary -> {
                        if (state.diaryEntries.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EmptyTab(stringResource(R.string.profile_empty_diary))
                            }
                        } else {
                            state.diaryEntries.forEach { entry ->
                                item(span = { GridItemSpan(maxLineSpan) }, key = "d_${entry.id}") {
                                    LaunchedEffect(entry.id) { viewModel.fetchDiaryIfNeeded(entry) }
                                    DiaryRow(entry = entry, onClick = { onOpenDiaryEntry(entry.id) })
                                }
                            }
                        }
                    }
                    ProfileTab.Lists -> {
                        val lists = state.ownLists + state.savedLists
                        if (lists.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EmptyTab(stringResource(R.string.profile_empty_lists))
                            }
                        } else {
                            lists.forEach { list ->
                                item(span = { GridItemSpan(maxLineSpan) }, key = "l_${list.id}") {
                                    ListRow(list.title, list.bookCount)
                                }
                            }
                        }
                    }
                    ProfileTab.Tbr -> {
                        if (state.tbrItems.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EmptyTab(stringResource(R.string.profile_empty_tbr))
                            }
                        } else {
                            itemsIndexed(state.tbrItems, key = { _, t -> t.id }) { _, tbr ->
                                BookCoverImage(
                                    url = tbr.book.coverUrl,
                                    title = tbr.book.title,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f / 3f)
                                        .clickable { onOpenBook(tbr.bookId) },
                                    cornerRadius = 6.dp
                                )
                            }
                        }
                    }
                    ProfileTab.Authors -> {
                        if (state.authors.isEmpty()) {
                            item(span = { GridItemSpan(maxLineSpan) }) {
                                EmptyTab(stringResource(R.string.profile_empty_authors))
                            }
                        } else {
                            state.authors.forEach { author ->
                                item(span = { GridItemSpan(maxLineSpan) }, key = "a_${author.name}") {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 20.dp, vertical = 6.dp)
                                    ) {
                                        BookCoverImage(
                                            url = author.coverUrl,
                                            title = author.name,
                                            modifier = Modifier.width(36.dp).aspectRatio(2f / 3f),
                                            cornerRadius = 3.dp
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(author.name, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                                        Spacer(Modifier.weight(1f))
                                        Text(
                                            stringResource(R.string.profile_books_count, author.bookCount),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            state.errorMessage?.let { msg ->
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        msg,
                        style = MaterialTheme.typography.bodyMedium,
                        color = ErrorColor,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }
        }

        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    followSheetMode?.let { mode ->
        FollowListSheet(
            username = username,
            mode = mode,
            onOpenProfile = {
                followSheetMode = null
                onOpenProfile(it)
            },
            onDismiss = { followSheetMode = null }
        )
    }
}

@Composable
private fun Header(
    state: ProfileUiState,
    isOwn: Boolean,
    showBack: Boolean,
    onBack: () -> Unit,
    onFollow: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onStats: (FollowListMode) -> Unit
) {
    val profile = state.profile
    Column {
        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            // Banner: favorite covers blurred behind a bottom fade (iOS gradient banner twin).
            Row(modifier = Modifier.fillMaxSize().blur(24.dp)) {
                val covers = state.favoriteBooks.mapNotNull { it.coverUrl }
                if (covers.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize().background(Surface))
                } else {
                    covers.take(4).forEach { url ->
                        BookCoverImage(
                            url = url,
                            title = "",
                            modifier = Modifier.weight(1f).fillMaxSize(),
                            cornerRadius = 0.dp
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Background.copy(alpha = 0.2f), Background)
                        )
                    )
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 34.dp, start = 8.dp, end = 8.dp)
            ) {
                if (showBack) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = stringResource(R.string.detail_back),
                            tint = TextPrimary
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                if (isOwn) {
                    IconButton(onClick = onOpenSettings) {
                        Icon(
                            Icons.Outlined.Settings,
                            contentDescription = stringResource(R.string.profile_settings),
                            tint = TextPrimary
                        )
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp).offset(y = (-34).dp)) {
            AvatarImage(
                url = profile?.avatarUrl,
                name = profile?.displayName ?: "?",
                size = 80.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(Background)
                    .padding(3.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                profile?.displayName.orEmpty(),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
            Text(
                "@${profile?.username.orEmpty()}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            profile?.bio?.takeIf { it.isNotEmpty() }?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
            Spacer(Modifier.height(12.dp))

            profile?.let { p ->
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                    Stat(p.booksReadCount, stringResource(R.string.profile_books)) {}
                    Stat(p.followersCount, stringResource(R.string.profile_followers)) {
                        onStats(FollowListMode.Followers)
                    }
                    Stat(p.followingCount, stringResource(R.string.profile_following)) {
                        onStats(FollowListMode.Following)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    state.streak?.takeIf { it > 0 }?.let { streak ->
                        Pill(stringResource(R.string.profile_streak, streak))
                        Spacer(Modifier.width(8.dp))
                    }
                    if (isOwn) {
                        Text(
                            stringResource(R.string.profile_edit),
                            style = MaterialTheme.typography.labelMedium,
                            color = TextPrimary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Surface)
                                .clickable(onClick = onOpenEditProfile)
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    } else {
                        val following = p.isFollowing ?: false
                        Text(
                            stringResource(if (following) R.string.profile_unfollow else R.string.profile_follow),
                            style = MaterialTheme.typography.labelMedium,
                            color = if (following) TextPrimary else Background,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (following) Surface else TextPrimary)
                                .clickable(enabled = !state.isFollowLoading, onClick = onFollow)
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Stat(count: Int, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            count.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    }
}

@Composable
private fun Pill(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = Accent,
        modifier = Modifier
            .clip(CircleShape)
            .background(Accent.copy(alpha = 0.12f))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
private fun TabDock(selected: ProfileTab, onSelect: (ProfileTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ProfileTab.entries.forEach { tab ->
            val active = tab == selected
            Text(
                text = when (tab) {
                    ProfileTab.Bookshelf -> stringResource(R.string.profile_tab_shelf)
                    ProfileTab.Diary -> stringResource(R.string.profile_tab_diary)
                    ProfileTab.Lists -> stringResource(R.string.profile_tab_lists)
                    ProfileTab.Tbr -> stringResource(R.string.profile_tab_tbr)
                    ProfileTab.Authors -> stringResource(R.string.profile_tab_authors)
                },
                style = MaterialTheme.typography.labelMedium,
                color = if (active) Background else TextSecondary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (active) Accent else Surface)
                    .clickable { onSelect(tab) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun DiaryRow(entry: `in`.paperboxd.app.domain.model.DiaryEntry, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        entry.book?.let { book ->
            BookCoverImage(
                url = book.coverUrl,
                title = book.title,
                modifier = Modifier.width(40.dp).aspectRatio(2f / 3f),
                cornerRadius = 4.dp
            )
            Spacer(Modifier.width(12.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            entry.book?.let {
                Text(it.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1)
            }
            Text(
                entry.plainTextPreview,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 2
            )
            entry.rating?.let { RatingPicker(rating = it, starSize = 12.dp) }
        }
    }
}

@Composable
private fun ListRow(title: String, bookCount: Long) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Surface)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
            Text(
                stringResource(R.string.profile_books_count, bookCount.toInt()),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
private fun EmptyTab(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}
