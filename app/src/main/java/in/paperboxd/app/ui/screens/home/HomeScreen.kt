package `in`.paperboxd.app.ui.screens.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyRowItems
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.LastLoggedBook
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.ShimmerBox
import `in`.paperboxd.app.ui.components.SignalPill
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import androidx.compose.ui.tooling.preview.Preview
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme

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

    HomeContent(
        state = state,
        onRefresh = { viewModel.load(refreshing = true) },
        onOpenBook = onOpenBook,
        onWrite = onWrite,
        onBell = {
            viewModel.markActivitiesViewed()
            // In a real app, this might navigate or open a sheet. 
            // The original code had a local state for showNotifications.
        },
        trackImpression = viewModel::trackImpression,
        markActivitiesViewed = viewModel::markActivitiesViewed
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    state: HomeUiState,
    onRefresh: () -> Unit,
    onOpenBook: (String) -> Unit,
    onWrite: () -> Unit,
    onBell: () -> Unit,
    trackImpression: (String) -> Unit = {},
    markActivitiesViewed: () -> Unit = {}
) {
    var showNotifications by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        DotGridBackground()

        PullToRefreshBox(
            isRefreshing = state.isRefreshing,
            onRefresh = onRefresh
        ) {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalItemSpacing = 14.dp,
                modifier = Modifier.fillMaxSize().statusBarsPadding()
                    .padding(horizontal = 20.dp)
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    TopBar(
                        hasUnread = state.hasNewActivities,
                        onBell = {
                            markActivitiesViewed()
                            showNotifications = true
                            onBell()
                        },
                        onWrite = onWrite
                    )
                }

                state.lastLoggedBook?.let { lastBook ->
                    item(span = StaggeredGridItemSpan.FullLine) {
                        LastLoggedCard(lastBook, onClick = { onOpenBook(lastBook.bookId) })
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) {
                    SectionHeader(
                        eyebrow = stringResource(R.string.home_picked_eyebrow),
                        title = stringResource(R.string.home_picked_title)
                    )
                }

                if (state.isLoading) {
                    items(6) {
                        ShimmerBox(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3.4f)
                                .clip(RoundedCornerShape(10.dp))
                        )
                    }
                } else {
                    items(state.pickedForYou, key = { it.id }) { rec ->
                        LaunchedEffect(rec.id) { trackImpression(rec.id) }
                        RecommendationCard(rec, onClick = { onOpenBook(rec.id) })
                    }
                }

                if (state.freshShelves.isNotEmpty()) {
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Column {
                            Spacer(Modifier.height(10.dp))
                            SectionHeader(
                                eyebrow = stringResource(R.string.home_fresh_eyebrow),
                                title = stringResource(R.string.home_fresh_title)
                            )
                        }
                    }
                    item(span = StaggeredGridItemSpan.FullLine) {
                        FreshShelvesRow(state.freshShelves, onOpenBook)
                    }
                }

                state.errorMessage?.let { message ->
                    item(span = StaggeredGridItemSpan.FullLine) {
                        Text(message, style = MaterialTheme.typography.bodyMedium, color = ErrorColor)
                    }
                }

                item(span = StaggeredGridItemSpan.FullLine) { Spacer(Modifier.height(96.dp)) }
            }
        }

        if (showNotifications) {
            NotificationsSheet(
                activities = state.friendsActivities,
                onDismiss = { showNotifications = false }
            )
        }
    }
}

@Composable
private fun TopBar(hasUnread: Boolean, onBell: () -> Unit, onWrite: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.app_name),
            fontFamily = FontFamily.Cursive,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            fontSize = 26.sp,
            color = Accent
        )
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onWrite) {
            Icon(Icons.Outlined.Edit, contentDescription = stringResource(R.string.home_write), tint = TextPrimary)
        }
        Box {
            IconButton(onClick = onBell) {
                Icon(
                    Icons.Outlined.Notifications,
                    contentDescription = stringResource(R.string.home_notifications),
                    tint = TextPrimary
                )
            }
            if (hasUnread) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 10.dp, end = 10.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Accent)
                )
            }
        }
    }
}

/** Barely-visible dot grid, iOS Home background twin. */
@Composable
private fun DotGridBackground() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val spacing = 26.dp.toPx()
        val radius = 1.dp.toPx()
        var y = 0f
        while (y < size.height) {
            var x = 0f
            while (x < size.width) {
                drawCircle(
                    color = TextPrimary.copy(alpha = 0.035f),
                    radius = radius,
                    center = Offset(x, y)
                )
                x += spacing
            }
            y += spacing
        }
    }
}

@Composable
fun SectionHeader(eyebrow: String, title: String) {
    Column {
        Text(
            text = eyebrow.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary
        )
        Spacer(Modifier.height(3.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary
        )
    }
}

@Composable
private fun LastLoggedCard(book: LastLoggedBook, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BookCoverImage(
            url = book.coverUrl,
            title = book.title,
            modifier = Modifier.width(44.dp).aspectRatio(2f / 3f),
            cornerRadius = 4.dp
        )
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_keep_reading).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary
            )
            Text(
                book.title,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                maxLines = 1
            )
            Text(
                stringResource(R.string.home_progress_percent, book.displayPercent),
                style = MaterialTheme.typography.bodySmall,
                color = Accent
            )
        }
    }
}

@Composable
private fun RecommendationCard(rec: RecommendationItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
    ) {
        BookCoverImage(
            url = rec.coverUrl,
            title = rec.title,
            modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
            cornerRadius = 10.dp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            rec.title,
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary,
            maxLines = 2
        )
        Text(
            rec.authorLine,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            maxLines = 1
        )
        rec.reason?.takeIf { it.isNotEmpty() }?.let {
            Spacer(Modifier.height(6.dp))
            SignalPill(it)
        }
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
            onRefresh = {},
            onOpenBook = {},
            onWrite = {},
            onBell = {}
        )
    }
}

@Composable
private fun FreshShelvesRow(books: List<Book>, onOpenBook: (String) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        lazyRowItems(books, key = { it.id }) { book ->
            Column(
                modifier = Modifier
                    .width(104.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenBook(book.id) }
            ) {
                BookCoverImage(
                    url = book.coverUrl,
                    title = book.title,
                    modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
                    cornerRadius = 8.dp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    book.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextPrimary,
                    maxLines = 2
                )
            }
        }
    }
}
