package `in`.paperboxd.app.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.ActivityItem
import `in`.paperboxd.app.domain.model.LastLoggedBook
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.ShimmerBox
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.theme.AvatarGradient
import `in`.paperboxd.app.ui.theme.PBScript
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme
import `in`.paperboxd.app.ui.theme.Terracotta

/**
 * Home — light-mode-only brutalist paper screen, iOS HomeView twin.
 * Fixed HL tokens (not MaterialTheme): the rest of the app is dark, Home is
 * always light, mirroring "Home - Brutalist Mobile.html".
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    user: User,
    onOpenBook: (String) -> Unit,
    onWrite: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(user.id) { viewModel.user = user }

    // Light page → dark status-bar icons while Home is visible.
    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as? android.app.Activity ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(activity.window, view)
        val wasLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = true
        onDispose { controller.isAppearanceLightStatusBars = wasLight }
    }

    HomeContent(
        state = state,
        username = user.username ?: "reader",
        onRefresh = { viewModel.load(refreshing = true) },
        onOpenBook = onOpenBook,
        onWrite = onWrite,
        trackImpression = viewModel::trackImpression,
        markActivitiesViewed = viewModel::markActivitiesViewed,
        onRespondToRequest = viewModel::respondToFollowRequest
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUiState,
    username: String,
    onRefresh: () -> Unit,
    onOpenBook: (String) -> Unit,
    onWrite: () -> Unit,
    trackImpression: (String) -> Unit = {},
    markActivitiesViewed: () -> Unit = {},
    onRespondToRequest: (username: String, accept: Boolean) -> Unit = { _, _ -> }
) {
    var showNotifications by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(HL.Paper)) {
        DotGridBackground()

        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            HomeHeader(
                hasUnread = state.hasNewActivities,
                onWrite = onWrite,
                onBell = {
                    markActivitiesViewed()
                    showNotifications = true
                }
            )

            when {
                state.isLoading && !state.isRefreshing -> ShimmerFeed()
                state.errorMessage != null && state.recommendations.isEmpty() ->
                    ErrorPane(state.errorMessage, onRetry = onRefresh)
                else -> PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = onRefresh
                ) {
                    FeedList(
                        state = state,
                        username = username,
                        onOpenBook = onOpenBook,
                        trackImpression = trackImpression
                    )
                }
            }
        }

        if (showNotifications) {
            NotificationsSheet(
                activities = state.friendsActivities,
                followRequests = state.followRequests,
                onRespondToRequest = onRespondToRequest,
                onOpenBook = onOpenBook,
                onDismiss = { showNotifications = false }
            )
        }
    }
}

// ---- Header (cursive wordmark + circular card actions) ----

@Composable
private fun HomeHeader(hasUnread: Boolean, onWrite: () -> Unit, onBell: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(HL.Paper.copy(alpha = 0.95f))
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontFamily = PBScript,
            fontSize = 30.sp,
            color = HL.Ink
        )
        Spacer(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            HeaderCircleButton(onClick = onWrite) {
                Icon(
                    Icons.Outlined.Edit,
                    contentDescription = stringResource(R.string.home_write),
                    tint = HL.Ink,
                    modifier = Modifier.size(19.dp)
                )
            }
            Box {
                HeaderCircleButton(onClick = onBell) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = stringResource(R.string.home_notifications),
                        tint = HL.Ink,
                        modifier = Modifier.size(20.dp)
                    )
                }
                if (hasUnread) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-3).dp, y = 3.dp)
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(Terracotta)
                            .border(2.dp, HL.Paper, CircleShape)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderCircleButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(HL.Card)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ---- Background dot grid ----

@Composable
private fun DotGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 26.dp.toPx()
        val radius = 0.75.dp.toPx()
        var y = 0f
        while (y < size.height) {
            var x = 0f
            while (x < size.width) {
                drawCircle(
                    color = HL.Ink.copy(alpha = 0.05f),
                    radius = radius,
                    center = Offset(x, y)
                )
                x += spacing
            }
            y += spacing
        }
    }
}

// ---- Feed ----

@Composable
private fun FeedList(
    state: HomeUiState,
    username: String,
    onOpenBook: (String) -> Unit,
    trackImpression: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(28.dp),
        contentPadding = PaddingValues(bottom = 112.dp)
    ) {
        item {
            GreetingBlock(
                username = username,
                modifier = Modifier.padding(horizontal = 20.dp).padding(top = 12.dp)
            )
        }

        state.lastLoggedBook?.let { book ->
            item {
                BrutalReadingHero(
                    book = book,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .clickable { onOpenBook(book.bookId) }
                )
            }
        }

        if (state.friendsActivities.isNotEmpty()) {
            item { FriendsRail(state.friendsActivities) }
        }

        if (state.pickedForYou.isNotEmpty()) {
            item {
                CarouselSection(
                    eyebrow = stringResource(R.string.home_picked_eyebrow),
                    title = stringResource(R.string.home_picked_title),
                    items = state.pickedForYou,
                    onOpenBook = onOpenBook,
                    trackImpression = trackImpression
                )
            }
        }

        if (state.freshShelves.isNotEmpty()) {
            item {
                CarouselSection(
                    eyebrow = stringResource(R.string.home_fresh_eyebrow),
                    title = stringResource(R.string.home_fresh_title),
                    items = state.freshShelves.map {
                        RecommendationItem(id = it.id, title = it.title, authors = it.authors, coverUrl = it.coverUrl)
                    },
                    onOpenBook = onOpenBook
                )
            }
        }
    }
}

@Composable
private fun GreetingBlock(username: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.home_hello, username),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 26.sp,
            color = HL.Ink
        )
        Text(
            text = stringResource(R.string.home_reading_prompt),
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 22.sp,
            color = HL.Muted
        )
    }
}

// ---- Currently-reading hero (iOS BrutalReadingHero) ----

@Composable
fun BrutalReadingHero(book: LastLoggedBook, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .brutalPlate(offset = 6.dp)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(13.dp)) {
            BookCoverImage(
                url = book.coverUrl,
                title = book.title,
                modifier = Modifier.width(56.dp).aspectRatio(2f / 3f),
                cornerRadius = 3.dp
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    text = stringResource(R.string.home_currently_reading).uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.8.sp,
                    color = HL.Muted
                )
                Text(
                    text = book.title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 19.sp,
                    lineHeight = 23.sp,
                    color = HL.Ink,
                    maxLines = 2
                )
                if (book.author.isNotEmpty()) {
                    Text(text = book.author, fontSize = 12.sp, color = HL.Muted, maxLines = 1)
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                Text(
                    text = "${book.displayPercent}",
                    fontWeight = FontWeight.Black,
                    fontSize = 27.sp,
                    color = HL.Ink
                )
                Text(
                    text = "%",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 10.sp,
                    color = HL.Muted,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }

        // hard-edged progress bar: paper2 track, red fill, ink border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(HL.Paper2)
                .border(1.dp, HL.Ink)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(book.displayPercent.coerceIn(0, 100) / 100f)
                    .height(6.dp)
                    .background(HL.Accent)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = if (book.totalPages > 0) "P.${book.currentPage} / ${book.totalPages}" else "P.${book.currentPage}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = HL.Muted
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier
                    .background(HL.Ink)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.home_log_pages),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = HL.Card
                )
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowForward,
                    contentDescription = null,
                    tint = HL.Card,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}

// ---- Friends rail ("Between covers.") ----

@Composable
private fun FriendsRail(activities: List<ActivityItem>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            EyebrowText(stringResource(R.string.home_friends_eyebrow))
            Text(
                text = stringResource(R.string.home_friends_title),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = HL.Ink
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(activities.take(6), key = { it.id }) { FriendCard(it) }
        }
    }
}

@Composable
private fun FriendCard(a: ActivityItem) {
    Column(
        modifier = Modifier
            .width(166.dp)
            .brutalPlate(offset = 4.dp)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(modifier = Modifier.size(24.dp).border(1.dp, HL.Ink)) {
                if (a.avatarUrl != null) {
                    AsyncImage(
                        model = a.avatarUrl,
                        contentDescription = a.displayName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(AvatarGradient))
                }
            }
            Text(
                text = a.displayName,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = HL.Ink,
                maxLines = 1,
                modifier = Modifier.weight(1f)
            )
        }
        Column {
            Text(
                text = a.verbPhrase,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = HL.Muted,
                maxLines = 1
            )
            Text(
                text = a.objectTitle.orEmpty(),
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 11.5.sp,
                lineHeight = 15.sp,
                color = HL.Ink,
                maxLines = 2
            )
        }
        Text(
            text = relativeTime(a.createdAt),
            fontFamily = FontFamily.Monospace,
            fontSize = 9.5.sp,
            letterSpacing = 0.5.sp,
            color = HL.Muted
        )
    }
}

// ---- Carousels ----

@Composable
private fun CarouselSection(
    eyebrow: String,
    title: String,
    items: List<RecommendationItem>,
    onOpenBook: (String) -> Unit,
    trackImpression: (String) -> Unit = {}
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            EyebrowText(eyebrow)
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp,
                color = HL.Ink,
                maxLines = 2
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 20.dp)
        ) {
            items(items, key = { it.id }) { item ->
                LaunchedEffect(item.id) { trackImpression(item.id) }
                CarouselCoverCard(item, onClick = { onOpenBook(item.id) })
            }
        }
    }
}

@Composable
private fun CarouselCoverCard(item: RecommendationItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(108.dp).clickable(onClick = onClick),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        BookCoverImage(
            url = item.coverUrl,
            title = item.title,
            modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
            cornerRadius = 6.dp
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = item.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                lineHeight = 16.sp,
                color = HL.Ink,
                maxLines = 2
            )
            if (item.authorLine.isNotEmpty()) {
                Text(text = item.authorLine, fontSize = 11.sp, color = HL.Muted, maxLines = 1)
            }
        }
    }
}

// ---- Shimmer + error ----

@Composable
private fun ShimmerFeed() {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp).padding(top = 12.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        ShimmerBox(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(18.dp))
        )
        repeat(2) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) {
                    ShimmerBox(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(2f / 3f)
                            .clip(RoundedCornerShape(6.dp))
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorPane(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 40.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            color = HL.Muted,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.common_retry),
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = HL.Accent,
            modifier = Modifier.clickable(onClick = onRetry)
        )
    }
}

@Preview
@Composable
private fun HomePreview() {
    PaperBoxdTheme {
        HomeContent(
            state = HomeUiState(
                recommendations = listOf(
                    RecommendationItem(id = "1", title = "The Great Gatsby", authors = listOf("F. Scott Fitzgerald")),
                    RecommendationItem(id = "2", title = "1984", authors = listOf("George Orwell"))
                ),
                lastLoggedBook = LastLoggedBook(
                    bookId = "1",
                    title = "The Great Gatsby",
                    author = "F. Scott Fitzgerald",
                    cover = "",
                    currentPage = 50,
                    totalPages = 100
                ),
                latestBooks = emptyList(),
                isLoading = false
            ),
            username = "reader",
            onRefresh = {},
            onOpenBook = {},
            onWrite = {}
        )
    }
}
