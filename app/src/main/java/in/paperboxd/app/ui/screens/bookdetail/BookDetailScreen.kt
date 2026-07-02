package `in`.paperboxd.app.ui.screens.bookdetail

import android.content.Intent
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkAdd
import androidx.compose.material.icons.outlined.BookmarkAdded
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.WatchLater
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.BookReview
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.DarkTextField
import `in`.paperboxd.app.ui.components.HorizontalCarousel
import `in`.paperboxd.app.ui.components.PrimaryButton
import `in`.paperboxd.app.ui.components.RatingPicker
import `in`.paperboxd.app.ui.components.ShimmerBox
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Error as ErrorColor
import `in`.paperboxd.app.ui.theme.LikeRed
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary

private enum class DetailTab { Overview, Reviews, Highlights, Lists }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    user: User,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    var tab by rememberSaveable { mutableIntStateOf(0) }
    var showRateSheet by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(user.id) { viewModel.user = user }
    LaunchedEffect(Unit) { viewModel.toast.collect { snackbar.showSnackbar(it) } }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Top bar: back + share
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.detail_back),
                        tint = TextPrimary
                    )
                }
                Spacer(Modifier.weight(1f))
                state.book?.let { book ->
                    IconButton(onClick = { shareBook(context, book) }) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = stringResource(R.string.detail_share),
                            tint = TextPrimary
                        )
                    }
                }
            }

            when {
                state.isLoading -> DetailShimmer()
                state.book == null -> Text(
                    state.errorMessage ?: stringResource(R.string.detail_error),
                    style = MaterialTheme.typography.bodyMedium,
                    color = ErrorColor,
                    modifier = Modifier.padding(20.dp)
                )
                else -> {
                    val book = state.book!!
                    Hero(book)
                    ActionRow(state, viewModel)
                    Spacer(Modifier.height(8.dp))

                    TabRow(
                        selectedTabIndex = tab,
                        containerColor = Background,
                        contentColor = TextPrimary,
                        indicator = { positions ->
                            TabRowDefaults.SecondaryIndicator(
                                color = Accent,
                                modifier = Modifier.tabIndicatorOffset(positions[tab])
                            )
                        }
                    ) {
                        DetailTab.entries.forEachIndexed { index, dt ->
                            Tab(
                                selected = tab == index,
                                onClick = { tab = index },
                                text = {
                                    Text(
                                        text = when (dt) {
                                            DetailTab.Overview -> stringResource(R.string.detail_tab_overview)
                                            DetailTab.Reviews -> stringResource(R.string.detail_tab_reviews)
                                            DetailTab.Highlights -> stringResource(R.string.detail_tab_highlights)
                                            DetailTab.Lists -> stringResource(R.string.detail_tab_lists)
                                        },
                                        color = if (tab == index) TextPrimary else TextSecondary
                                    )
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))

                    when (DetailTab.entries[tab]) {
                        DetailTab.Overview -> OverviewTab(state, viewModel, onOpenBook, onOpenProfile)
                        DetailTab.Reviews -> ReviewsTab(state, onRate = { showRateSheet = true })
                        DetailTab.Highlights -> EmptyTabState(stringResource(R.string.detail_highlights_empty))
                        DetailTab.Lists -> EmptyTabState(stringResource(R.string.detail_lists_empty))
                    }
                    Spacer(Modifier.height(48.dp))
                }
            }
        }

        SnackbarHost(hostState = snackbar, modifier = Modifier.align(Alignment.BottomCenter))
    }

    if (showRateSheet) {
        RateReviewSheet(
            isSubmitting = state.isSubmittingReview,
            initialRating = 0,
            onSubmit = { rating, review ->
                viewModel.submitReview(rating, review) { ok -> if (ok) showRateSheet = false }
            },
            onDismiss = { showRateSheet = false }
        )
    }
}

private fun shareBook(context: android.content.Context, book: Book) {
    val text = "${book.title} — ${book.authorLine}\nhttps://paperboxd.in/book/${book.slug ?: book.id}"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, null))
}

@Composable
private fun Hero(book: Book) {
    Row(modifier = Modifier.padding(horizontal = 20.dp)) {
        BookCoverImage(
            url = book.coverUrl,
            title = book.title,
            modifier = Modifier.width(120.dp).aspectRatio(2f / 3f),
            cornerRadius = 10.dp
        )
        Spacer(Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(book.title, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
            Spacer(Modifier.height(4.dp))
            Text(book.authorLine, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            val meta = listOfNotNull(
                book.publishedYear,
                book.pageCount?.let { stringResource(R.string.detail_pages, it) }
            ).joinToString(" · ")
            if (meta.isNotEmpty()) {
                Text(meta, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            book.averageRating?.let { rating ->
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RatingPicker(rating = rating.toInt(), starSize = 16.dp)
                    Spacer(Modifier.width(6.dp))
                    Text(
                        String.format(java.util.Locale.US, "%.1f", rating),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
            if (book.categories.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    book.categories.take(3).joinToString(" · "),
                    style = MaterialTheme.typography.bodySmall,
                    color = Accent,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun ActionRow(state: BookDetailUiState, viewModel: BookDetailViewModel) {
    val s = state.bookState
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        ActionChip(
            icon = { tint ->
                Icon(
                    if (s.isOnShelf) Icons.Outlined.BookmarkAdded else Icons.Outlined.BookmarkAdd,
                    contentDescription = null, tint = tint
                )
            },
            label = stringResource(if (s.isOnShelf) R.string.detail_on_shelf else R.string.detail_add_shelf),
            active = s.isOnShelf,
            onClick = viewModel::toggleBookshelf,
            modifier = Modifier.weight(1f)
        )
        ActionChip(
            icon = { tint ->
                Icon(
                    if (s.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = null,
                    tint = if (s.isLiked) LikeRed else tint
                )
            },
            label = stringResource(R.string.detail_like),
            active = s.isLiked,
            onClick = viewModel::toggleLike,
            modifier = Modifier.weight(1f)
        )
        ActionChip(
            icon = { tint -> Icon(Icons.Outlined.WatchLater, contentDescription = null, tint = tint) },
            label = stringResource(R.string.detail_tbr),
            active = s.isTbr,
            onClick = viewModel::toggleTbr,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun ActionChip(
    icon: @Composable (androidx.compose.ui.graphics.Color) -> Unit,
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tint = if (active) Accent else TextSecondary
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (active) Accent.copy(alpha = 0.12f) else Surface)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        icon(tint)
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelMedium, color = tint, maxLines = 1)
    }
}

@Composable
private fun OverviewTab(
    state: BookDetailUiState,
    viewModel: BookDetailViewModel,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    val book = state.book ?: return
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        book.description?.let { ExpandableDescription(it) }

        if (state.friendsOnBook.isNotEmpty()) {
            Spacer(Modifier.height(22.dp))
            Text(
                stringResource(R.string.detail_friends_reading, state.friendsReadingCount),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            Spacer(Modifier.height(10.dp))
            state.friendsOnBook.take(5).forEach { friend ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenProfile(friend.username) }
                        .padding(vertical = 6.dp)
                ) {
                    AvatarImage(url = friend.avatarUrl, name = friend.displayName, size = 32.dp)
                    Spacer(Modifier.width(10.dp))
                    Text(friend.displayName, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                    Spacer(Modifier.weight(1f))
                    if (friend.isReadingNow) {
                        Text(
                            stringResource(R.string.detail_reading_now),
                            style = MaterialTheme.typography.bodySmall,
                            color = Accent
                        )
                    }
                }
            }
        }
    }

    if (state.similarBooks.isNotEmpty()) {
        Spacer(Modifier.height(22.dp))
        Text(
            stringResource(R.string.detail_similar),
            style = MaterialTheme.typography.headlineSmall,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        Spacer(Modifier.height(10.dp))
        HorizontalCarousel(items = state.similarBooks, key = { it.id }) { rec ->
            Column(
                modifier = Modifier
                    .width(104.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onOpenBook(rec.id) }
            ) {
                BookCoverImage(
                    url = rec.coverUrl,
                    title = rec.title,
                    modifier = Modifier.fillMaxWidth().aspectRatio(2f / 3f),
                    cornerRadius = 8.dp
                )
                Spacer(Modifier.height(6.dp))
                Text(rec.title, style = MaterialTheme.typography.bodySmall, color = TextPrimary, maxLines = 2)
            }
        }
    }
}

@Composable
private fun ExpandableDescription(description: String) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val plain = remember(description) {
        description.replace(Regex("<[^>]+>"), " ").replace(Regex("\\s+"), " ").trim()
    }
    Column {
        Text(
            plain,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            maxLines = if (expanded) Int.MAX_VALUE else 4
        )
        TextButton(onClick = { expanded = !expanded }) {
            Text(
                stringResource(if (expanded) R.string.detail_read_less else R.string.detail_read_more),
                color = Accent
            )
        }
    }
}

@Composable
private fun ReviewsTab(state: BookDetailUiState, onRate: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        PrimaryButton(text = stringResource(R.string.detail_rate_review), onClick = onRate)
        Spacer(Modifier.height(18.dp))
        if (state.reviews.isEmpty()) {
            EmptyTabState(stringResource(R.string.detail_reviews_empty))
        } else {
            state.reviews.forEach { review -> ReviewRow(review) }
        }
    }
}

@Composable
private fun ReviewRow(review: BookReview) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        AvatarImage(url = review.avatarUrl, name = review.username, size = 34.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "@${review.username}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )
                Spacer(Modifier.width(8.dp))
                review.rating?.let { RatingPicker(rating = it, starSize = 13.dp) }
            }
            review.review?.takeIf { it.isNotEmpty() }?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            }
        }
    }
}

@Composable
private fun EmptyTabState(message: String) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Composable
private fun DetailShimmer() {
    Row(modifier = Modifier.padding(20.dp)) {
        ShimmerBox(
            modifier = Modifier.width(120.dp).aspectRatio(2f / 3f).clip(RoundedCornerShape(10.dp))
        )
        Spacer(Modifier.width(16.dp))
        Column {
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.8f).height(24.dp).clip(RoundedCornerShape(4.dp)))
            Spacer(Modifier.height(10.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.5f).height(16.dp).clip(RoundedCornerShape(4.dp)))
        }
    }
}

/** Rate + review bottom sheet (iOS RateReviewSheet twin). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RateReviewSheet(
    isSubmitting: Boolean,
    initialRating: Int,
    onSubmit: (rating: Int, review: String?) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by rememberSaveable { mutableIntStateOf(initialRating) }
    var review by rememberSaveable { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Surface) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
            Text(
                stringResource(R.string.detail_rate_review),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            Spacer(Modifier.height(16.dp))
            RatingPicker(rating = rating, onRate = { rating = it }, starSize = 36.dp)
            Spacer(Modifier.height(16.dp))
            DarkTextField(
                value = review,
                onValueChange = { review = it },
                label = stringResource(R.string.detail_review_hint),
                singleLine = false,
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )
            Spacer(Modifier.height(16.dp))
            PrimaryButton(
                text = stringResource(R.string.detail_post),
                onClick = { onSubmit(rating, review.trim().ifEmpty { null }) },
                enabled = rating in 1..5,
                loading = isSubmitting
            )
            Spacer(Modifier.height(28.dp))
        }
    }
}
