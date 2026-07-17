package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.domain.model.AuthorSummary
import `in`.paperboxd.app.domain.model.BookWithStatus
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.domain.model.LastLoggedBook
import `in`.paperboxd.app.domain.model.ReadingList
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.domain.model.UserProfile
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.screens.settings.SettingsSheet
import `in`.paperboxd.app.ui.theme.PBScript
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val Line = Color(0xFFE6DFD0)

private fun formatCount(n: Int): String =
    if (n >= 1000) "%.1fk".format(n / 1000.0) else "$n"

/**
 * Profile — light-mode-only paper screen, iOS ProfileView twin.
 * Header → currently reading → favourite four → tab dock
 * (Bookshelf | Diary | Lists | TBR | Authors) → editorial signature.
 * Heatmap intentionally absent: no activity endpoint wired on Android yet.
 */
@Composable
fun ProfileScreen(
    username: String,
    viewer: User,
    showBack: Boolean,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onSignOut: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenDiaryEntry: (String) -> Unit,
    reloadKey: Any = Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }

    // `reloadKey` bumps after an edit-profile save so the screen re-fetches.
    LaunchedEffect(username, reloadKey) { viewModel.start(username, viewer) }
    LaunchedEffect(Unit) { viewModel.toast.collect { snackbar.showSnackbar(it) } }

    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as? android.app.Activity ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(activity.window, view)
        val wasLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = true
        onDispose { controller.isAppearanceLightStatusBars = wasLight }
    }

    ProfileContent(
        state = state,
        snackbarHostState = snackbar,
        isOwnProfile = viewModel.isOwnProfile,
        showBack = showBack,
        onBack = onBack,
        viewerEmail = viewer.email,
        onSignOut = onSignOut,
        onOpenBook = onOpenBook,
        onOpenProfile = onOpenProfile,
        onOpenEditProfile = onOpenEditProfile,
        onOpenDiaryEntry = onOpenDiaryEntry,
        onTabSelected = viewModel::onTabSelected,
        onToggleFollow = viewModel::toggleFollow,
        onSelectActivityYear = viewModel::selectActivityYear,
        fetchShelfIfNeeded = viewModel::fetchShelfIfNeeded,
        fetchDiaryIfNeeded = viewModel::fetchDiaryIfNeeded
    )
}

@Composable
fun ProfileContent(
    state: ProfileUiState,
    snackbarHostState: SnackbarHostState,
    isOwnProfile: Boolean,
    showBack: Boolean,
    onBack: () -> Unit,
    viewerEmail: String,
    onSignOut: () -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenEditProfile: () -> Unit,
    onOpenDiaryEntry: (String) -> Unit,
    onTabSelected: (ProfileTab) -> Unit,
    onToggleFollow: () -> Unit,
    onSelectActivityYear: (Int) -> Unit = {},
    fetchShelfIfNeeded: (BookWithStatus) -> Unit,
    fetchDiaryIfNeeded: (DiaryEntry) -> Unit
) {
    var followSheet by remember { mutableStateOf<FollowListMode?>(null) }
    var showShare by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) } // SHOW MORE lifts the 9-item cap

    Box(modifier = Modifier.fillMaxSize().background(HL.Paper)) {
        when {
            state.isLoading && state.profile == null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
            }
            state.errorMessage != null && state.profile == null -> Text(
                state.errorMessage ?: "",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp,
                color = HL.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center).padding(32.dp)
            )
            state.profile != null -> {
                val profile = state.profile!!
                LazyColumn(
                    modifier = Modifier.fillMaxSize().statusBarsPadding(),
                    contentPadding = PaddingValues(top = 68.dp, bottom = 140.dp)
                ) {
                    item {
                        ProfileHeader(
                            profile = profile,
                            booksCount = state.shelfTotal ?: profile.booksReadCount,
                            streak = state.streak ?: 0,
                            isOwnProfile = isOwnProfile,
                            isFollowLoading = state.isFollowLoading,
                            onFollow = onToggleFollow,
                            onEdit = onOpenEditProfile,
                            onShare = { showShare = true },
                            onFollowers = { followSheet = FollowListMode.Followers }
                        )
                    }

                    state.activity?.let { activity ->
                        item {
                            ReadingHeatmap(
                                activity = activity,
                                selectedYear = state.activityYear,
                                onSelectYear = onSelectActivityYear,
                                modifier = Modifier
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 28.dp)
                            )
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier.padding(top = 28.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            EyebrowText(
                                if (isOwnProfile) "Currently reading"
                                else "${profile.displayName.substringBefore(' ')} is reading",
                                modifier = Modifier.padding(horizontal = 20.dp)
                            )
                            val lb = state.lastLoggedBook
                            if (lb != null) {
                                ReadingCard(
                                    book = lb,
                                    modifier = Modifier
                                        .padding(horizontal = 20.dp)
                                        .clickable { onOpenBook(lb.bookId) }
                                )
                            } else if (isOwnProfile) {
                                EmptyReadingCard(Modifier.padding(horizontal = 20.dp))
                            }
                        }
                    }

                    if (state.favoriteBooks.isNotEmpty()) {
                        item {
                            FavouriteFour(
                                state = state,
                                title = if (isOwnProfile) "Four books I love."
                                else "${profile.displayName.substringBefore(' ')}'s four.",
                                onOpenBook = onOpenBook
                            )
                        }
                    }

                    item {
                        TabDock(
                            selected = state.selectedTab,
                            state = state,
                            onSelect = {
                                expanded = false
                                onTabSelected(it)
                            },
                            modifier = Modifier.padding(top = 24.dp)
                        )
                    }

                    tabContent(
                        state = state,
                        expanded = expanded,
                        onExpand = { expanded = true },
                        onOpenBook = onOpenBook,
                        onOpenDiaryEntry = onOpenDiaryEntry,
                        fetchShelfIfNeeded = fetchShelfIfNeeded,
                        fetchDiaryIfNeeded = fetchDiaryIfNeeded
                    )

                    item {
                        EditorialSignature(
                            profile = profile,
                            modifier = Modifier.padding(top = 28.dp)
                        )
                    }
                }
            }
        }

        TopBar(
            showBack = showBack && !isOwnProfile,
            showSettings = isOwnProfile,
            onBack = onBack,
            onSettings = { showSettings = true },
            modifier = Modifier.statusBarsPadding()
        )

        SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }

    followSheet?.let { mode ->
        FollowListSheet(
            username = state.profile?.username ?: "",
            mode = mode,
            onOpenProfile = onOpenProfile,
            onDismiss = { followSheet = null }
        )
    }

    if (showShare) {
        ShareProfileSheet(
            username = state.profile?.username ?: "",
            onDismiss = { showShare = false }
        )
    }

    if (showSettings) {
        SettingsSheet(
            email = viewerEmail,
            onDismiss = { showSettings = false },
            onSignOut = onSignOut
        )
    }
}

// ---- Floating top bar ----

@Composable
private fun TopBar(
    showBack: Boolean,
    showSettings: Boolean,
    onBack: () -> Unit,
    onSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBack) {
            CircleChip(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = HL.Ink,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Spacer(Modifier.size(36.dp))
        }
        Spacer(Modifier.weight(1f))
        Text("PaperBoxd", fontFamily = PBScript, fontSize = 22.sp, color = HL.Ink)
        Spacer(Modifier.weight(1f))
        if (showSettings) {
            CircleChip(onClick = onSettings) {
                Icon(
                    Icons.Outlined.Menu,
                    contentDescription = "Settings",
                    tint = HL.Ink,
                    modifier = Modifier.size(16.dp)
                )
            }
        } else {
            Spacer(Modifier.size(36.dp))
        }
    }
}

@Composable
private fun CircleChip(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(HL.Ink.copy(alpha = 0.08f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ---- Header ----

@Composable
private fun ProfileHeader(
    profile: UserProfile,
    booksCount: Int,
    streak: Int,
    isOwnProfile: Boolean,
    isFollowLoading: Boolean,
    onFollow: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onFollowers: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AvatarImage(url = profile.avatarUrl, name = profile.displayName, size = 88.dp)
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    profile.displayName,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 28.sp,
                    lineHeight = 32.sp,
                    color = HL.Ink
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "@${profile.username}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = HL.Muted
                    )
                    if (profile.pronouns.isNotEmpty()) {
                        Text("·", color = HL.Muted.copy(alpha = 0.6f))
                        Text(
                            profile.pronouns.joinToString("/"),
                            fontSize = 12.sp,
                            color = HL.Muted
                        )
                    }
                }
            }
        }

        profile.bio?.takeIf { it.isNotEmpty() }?.let {
            Text(
                it,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp,
                lineHeight = 21.sp,
                color = HL.Ink.copy(alpha = 0.78f),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .drawBehind {
                    val h = 1.dp.toPx()
                    drawRect(Line, size = Size(size.width, h))
                    drawRect(Line, topLeft = Offset(0f, size.height - h), size = Size(size.width, h))
                }
                .padding(vertical = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatCell(booksCount, "Books", Modifier.weight(1f))
            StatCell(streak, "Streak", Modifier.weight(1f))
            StatCell(profile.followersCount, "Friends", Modifier.weight(1f).clickable(onClick = onFollowers))
            StatCell(profile.diaryEntriesCount, "Reviews", Modifier.weight(1f))
        }

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isOwnProfile) {
                BrutalPill("Edit profile", filled = true, modifier = Modifier.weight(1f), onClick = onEdit)
                BrutalPill("Share profile", filled = false, modifier = Modifier.weight(1f), onClick = onShare)
            } else {
                val following = profile.isFollowing ?: false
                BrutalPill(
                    if (following) "Following" else "Follow",
                    filled = !following,
                    loading = isFollowLoading,
                    modifier = Modifier.weight(1f),
                    onClick = onFollow
                )
            }
        }
    }
}

@Composable
private fun StatCell(value: Int, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            formatCount(value),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            color = HL.Ink
        )
        Text(
            label.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 9.5.sp,
            letterSpacing = 1.6.sp,
            color = HL.Muted
        )
    }
}

/** iOS PillButton brutalPrimary/brutalGhost — hard border + offset ink shadow. */
@Composable
private fun BrutalPill(
    title: String,
    filled: Boolean,
    loading: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .brutalPlate(
                fill = if (filled) HL.Ink else HL.Paper,
                borderWidth = 2.dp,
                offset = 3.dp
            )
            .clickable(enabled = !loading, onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (loading) {
            CircularProgressIndicator(
                color = if (filled) HL.Paper else HL.Ink,
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Text(
                title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (filled) HL.Paper else HL.Ink
            )
        }
    }
}

// ---- Currently reading card (iOS LastLoggedBookCard) ----

@Composable
private fun ReadingCard(book: LastLoggedBook, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .brutalPlate(fill = HL.Paper, borderWidth = 2.dp, offset = 5.dp)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BookCoverImage(
            url = book.coverUrl,
            title = book.title,
            modifier = Modifier.width(68.dp).aspectRatio(2f / 3f),
            cornerRadius = 3.dp
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                book.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 22.sp,
                color = HL.Ink,
                maxLines = 2
            )
            if (book.author.isNotEmpty()) {
                Text(book.author, fontSize = 12.sp, color = HL.Muted, maxLines = 1)
            }
            Column(
                modifier = Modifier.padding(top = 10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Text(
                    if (book.totalPages > 0)
                        "P.${book.currentPage} / ${book.totalPages} · ${book.displayPercent}%"
                    else "P.${book.currentPage}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = HL.Muted
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .background(Line)
                        .border(1.dp, HL.Ink)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(book.displayPercent.coerceIn(0, 100) / 100f)
                            .height(4.dp)
                            .background(HL.Ink)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyReadingCard(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .brutalPlate(fill = HL.Paper, borderWidth = 2.dp, offset = 5.dp)
            .padding(16.dp)
    ) {
        Text(
            "No reading logged yet",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            color = HL.Muted
        )
    }
}

// ---- Favourite four ----

@Composable
private fun FavouriteFour(state: ProfileUiState, title: String, onOpenBook: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(top = 28.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            EyebrowText("Favourites")
            Text(
                title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                color = HL.Ink
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.favoriteBooks.take(4).forEach { fav ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOpenBook(fav.bookId) },
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    BookCoverImage(
                        url = fav.coverUrl,
                        title = fav.title,
                        modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
                        cornerRadius = 6.dp
                    )
                    fav.note?.trim()?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            it,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.5.sp,
                            letterSpacing = 0.5.sp,
                            color = HL.Muted,
                            maxLines = 1,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            repeat(maxOf(0, 4 - state.favoriteBooks.size)) {
                Box(
                    Modifier
                        .weight(1f)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(HL.Card)
                )
            }
        }
    }
}

// ---- Tab dock ----

@Composable
private fun TabDock(
    selected: ProfileTab,
    state: ProfileUiState,
    onSelect: (ProfileTab) -> Unit,
    modifier: Modifier = Modifier
) {
    fun count(tab: ProfileTab): Int? {
        val p = state.profile ?: return null
        return when (tab) {
            ProfileTab.Bookshelf -> state.shelfTotal ?: p.booksReadCount
            ProfileTab.Diary -> p.diaryEntriesCount
            ProfileTab.Lists -> p.listsCount
            ProfileTab.Tbr -> state.tbrItems.size.takeIf { it > 0 }
            ProfileTab.Authors -> state.authors.size.takeIf { it > 0 }
        }
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ProfileTab.entries.forEach { tab ->
                val on = tab == selected
                Column(
                    modifier = Modifier.clickable { onSelect(tab) },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.padding(top = 4.dp, bottom = 11.dp)
                    ) {
                        Text(
                            if (tab == ProfileTab.Tbr) "TBR" else tab.name,
                            fontSize = 13.5.sp,
                            fontWeight = if (on) FontWeight.SemiBold else FontWeight.Medium,
                            color = if (on) HL.Ink else HL.Muted
                        )
                        count(tab)?.let {
                            Text(
                                "$it",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = HL.Muted.copy(alpha = 0.8f)
                            )
                        }
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(2.dp)
                            .background(if (on) HL.Ink else Color.Transparent)
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
    }
}

// ---- Tab content (items inside the outer LazyColumn) ----

private fun LazyListScope.tabContent(
    state: ProfileUiState,
    expanded: Boolean,
    onExpand: () -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenDiaryEntry: (String) -> Unit,
    fetchShelfIfNeeded: (BookWithStatus) -> Unit,
    fetchDiaryIfNeeded: (DiaryEntry) -> Unit
) {
    when (state.selectedTab) {
        ProfileTab.Bookshelf -> {
            if (state.shelfBooks.isEmpty()) {
                item { EmptyTab("No books read yet") }
            } else {
                val books = if (expanded) state.shelfBooks else state.shelfBooks.take(9)
                items(books.chunked(3), key = { row -> "shelf-" + row.first().id }) { row ->
                    LaunchedEffect(row.last().id) {
                        if (expanded) fetchShelfIfNeeded(row.last())
                    }
                    CoverRow(cells = row.map { it.coverUrl to it.id }, onOpen = onOpenBook)
                }
                if (!expanded && state.shelfBooks.size > 9) {
                    item { ShowMore(onExpand) }
                }
            }
        }
        ProfileTab.Diary -> {
            if (state.diaryEntries.isEmpty()) {
                item { EmptyTab("No diary entries yet") }
            } else {
                items(state.diaryEntries, key = { it.id }) { entry ->
                    LaunchedEffect(entry.id) { fetchDiaryIfNeeded(entry) }
                    DiaryRow(entry, onClick = { onOpenDiaryEntry(entry.id) })
                }
            }
        }
        ProfileTab.Lists -> {
            if (state.ownLists.isEmpty() && state.savedLists.isEmpty()) {
                item { EmptyTab("No lists yet") }
            } else {
                if (state.ownLists.isNotEmpty()) {
                    item { ListsRail("Made", state.ownLists) }
                }
                if (state.savedLists.isNotEmpty()) {
                    item { ListsRail("Saved", state.savedLists) }
                }
            }
        }
        ProfileTab.Tbr -> {
            if (state.tbrItems.isEmpty()) {
                item { EmptyTab("Nothing on the TBR list yet") }
            } else {
                val capped = if (expanded) state.tbrItems else state.tbrItems.take(9)
                items(capped.chunked(3), key = { row -> "tbr-" + row.first().id }) { row ->
                    CoverRow(cells = row.map { it.book.coverUrl to it.bookId }, onOpen = onOpenBook)
                }
                if (!expanded && state.tbrItems.size > 9) {
                    item { ShowMore(onExpand) }
                }
            }
        }
        ProfileTab.Authors -> {
            if (state.authors.isEmpty()) {
                item { EmptyTab("No authors yet") }
            } else {
                val capped = if (expanded) state.authors else state.authors.take(9)
                items(capped.chunked(3), key = { row -> "author-" + row.first().name }) { row ->
                    AuthorRow(row)
                }
                if (!expanded && state.authors.size > 9) {
                    item { ShowMore(onExpand) }
                }
            }
        }
    }
}

@Composable
private fun CoverRow(cells: List<Pair<String?, String>>, onOpen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cells.forEach { (url, id) ->
            BookCoverImage(
                url = url,
                title = "",
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(2f / 3f)
                    .clickable { onOpen(id) },
                cornerRadius = 6.dp
            )
        }
        repeat(3 - cells.size) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
private fun AuthorRow(row: List<AuthorSummary>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        row.forEach { author ->
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                BookCoverImage(
                    url = author.coverUrl,
                    title = author.name,
                    modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
                    cornerRadius = 6.dp
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        author.name,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.5.sp,
                        color = HL.Ink,
                        maxLines = 1
                    )
                    Text(
                        "${author.bookCount} book" + if (author.bookCount == 1) "" else "s",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.5.sp,
                        color = HL.Muted
                    )
                }
            }
        }
        repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
    }
}

@Composable
private fun DiaryRow(entry: DiaryEntry, onClick: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BookCoverImage(
                url = entry.book?.coverUrl,
                title = entry.book?.title ?: "",
                modifier = Modifier.width(44.dp).aspectRatio(2f / 3f),
                cornerRadius = 4.dp
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        entry.book?.title ?: entry.title ?: "Untitled",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp,
                        color = HL.Ink,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        monthDay(entry.createdAt),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.5.sp,
                        letterSpacing = 1.sp,
                        color = HL.Muted
                    )
                }
                if (entry.plainTextPreview.isNotEmpty()) {
                    Text(
                        "“${entry.plainTextPreview}”",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.5.sp,
                        lineHeight = 18.sp,
                        color = HL.Ink.copy(alpha = 0.72f),
                        maxLines = 3
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    entry.rating?.let { r ->
                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            for (i in 1..5) {
                                Icon(
                                    if (i <= r) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                    contentDescription = null,
                                    tint = if (i <= r) HL.Accent else HL.Muted.copy(alpha = 0.5f),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                    entry.book?.categories?.firstOrNull()?.let {
                        Text(
                            it,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = HL.Accent,
                            maxLines = 1,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(HL.Accent.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
    }
}

@Composable
private fun ListsRail(eyebrow: String, lists: List<ReadingList>) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        EyebrowText(eyebrow, modifier = Modifier.padding(horizontal = 20.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(lists, key = { it.id }) { list ->
                Column(
                    modifier = Modifier
                        .width(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HL.Card)
                        .padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        for (i in 0 until 3) {
                            if (i < list.coverUrls.size) {
                                BookCoverImage(
                                    url = list.coverUrls[i],
                                    title = "",
                                    modifier = Modifier.width(41.dp).aspectRatio(2f / 3f),
                                    cornerRadius = 3.dp
                                )
                            } else {
                                Box(
                                    Modifier
                                        .width(41.dp)
                                        .aspectRatio(2f / 3f)
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(HL.Paper)
                                )
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.padding(horizontal = 4.dp).padding(bottom = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(3.dp)
                    ) {
                        Text(
                            list.title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp,
                            color = HL.Ink,
                            maxLines = 1
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (list.isPrivate) {
                                Icon(
                                    Icons.Outlined.Lock,
                                    contentDescription = null,
                                    tint = HL.Muted,
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                            Text(
                                "${list.bookCount} books",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.5.sp,
                                color = HL.Muted
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ShowMore(onClick: () -> Unit) {
    Text(
        "SHOW MORE",
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 1.5.sp,
        color = HL.Ink,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp)
            .border(1.5.dp, HL.Ink)
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp)
    )
}

@Composable
private fun EmptyTab(message: String) {
    Text(
        message,
        fontFamily = FontFamily.Serif,
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp,
        color = HL.Muted,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(top = 56.dp)
    )
}

// ---- Editorial signature footer ----

@Composable
private fun EditorialSignature(profile: UserProfile, modifier: Modifier = Modifier) {
    val pages = profile.totalPagesRead
    val pagesStr = if (pages >= 1000) "%.1fk".format(pages / 1000.0) else "$pages"
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(HL.Card)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        EyebrowText("This year")
        Text(
            "${profile.booksReadCount} books · $pagesStr pages · ${profile.diaryEntriesCount} reviews.",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            color = HL.Ink
        )
        if (profile.favoriteGenres.isNotEmpty()) {
            Text(
                profile.favoriteGenres.take(3).joinToString(" · "),
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = HL.Muted
            )
        }
    }
}

private fun monthDay(iso: String): String = runCatching {
    DateTimeFormatter.ofPattern("MMM d")
        .format(Instant.parse(iso).atZone(ZoneId.systemDefault()))
        .uppercase()
}.getOrDefault("")
