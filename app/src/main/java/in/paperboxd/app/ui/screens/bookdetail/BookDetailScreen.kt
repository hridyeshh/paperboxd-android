package `in`.paperboxd.app.ui.screens.bookdetail

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.LibraryBooks
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.TextStyle as JavaTextStyle
import java.util.Locale
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.BookReview
import `in`.paperboxd.app.domain.model.FriendBookReview
import `in`.paperboxd.app.domain.model.FriendOnBook
import `in`.paperboxd.app.domain.model.ReadingProgress
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.ReportDialog
import `in`.paperboxd.app.ui.components.brutalButton
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.components.popEffect
import `in`.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel.LibraryShelf
import `in`.paperboxd.app.ui.theme.LikeRed
import kotlin.math.roundToInt

private val Line = HL.Ink

@Composable
fun BookDetailScreen(
    user: User,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    onOpenAuthor: (String) -> Unit,
    viewModel: BookDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var toast by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(user.id) { viewModel.user = user }
    LaunchedEffect(Unit) {
        viewModel.toast.collect {
            toast = it
            kotlinx.coroutines.delay(2500)
            toast = null
        }
    }

    BookDetailContent(
        state = state,
        toast = toast,
        handle = user.username,
        myReview = viewModel.myReview,
        currentShelf = viewModel.currentShelf,
        canRate = viewModel.canRate,
        onBack = onBack,
        onOpenBook = onOpenBook,
        onOpenAuthor = onOpenAuthor,
        onToggleLike = viewModel::toggleLike,
        onSelectShelf = viewModel::selectShelf,
        onSubmitReview = { rating, review, done -> viewModel.submitReview(rating, review, done) },
        onDeleteReview = { done -> viewModel.deleteReview(done) },
        onReportReview = viewModel::reportReview,
        onUpdateProgress = viewModel::updateProgress
    )
}

@Composable
private fun BookDetailContent(
    state: BookDetailUiState,
    toast: String?,
    handle: String?,
    myReview: BookReview?,
    currentShelf: LibraryShelf?,
    canRate: Boolean,
    onBack: () -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenAuthor: (String) -> Unit = {},
    onToggleLike: () -> Unit,
    onSelectShelf: (LibraryShelf?) -> Unit,
    onSubmitReview: (Int, String?, (Boolean) -> Unit) -> Unit,
    onDeleteReview: ((Boolean) -> Unit) -> Unit,
    onReportReview: (BookReview, String) -> Unit = { _, _ -> },
    onUpdateProgress: (Int, Int) -> Unit
) {
    var showLibrarySheet by remember { mutableStateOf(false) }
    var showShare by remember { mutableStateOf(false) }
    var activePanel by remember { mutableStateOf<InlinePanel?>(null) }
    var showRateGate by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(HL.Paper)) {
        Column(modifier = Modifier.fillMaxSize().statusBarsPadding()) {
            MiniHeader(book = state.book, onBack = onBack)

            when {
                state.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                }
                state.book == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        state.errorMessage ?: "Couldn't load book",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        color = HL.Muted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(40.dp)
                    )
                }
                else -> DetailBody(
                    state = state,
                    myReview = myReview,
                    activePanel = activePanel,
                    onOpenBook = onOpenBook,
                    onOpenAuthor = onOpenAuthor,
                    onShare = { showShare = true },
                    onSetPanel = { if (it != null && !canRate) showRateGate = true else activePanel = it },
                    onSubmitReview = onSubmitReview,
                    onDeleteReview = onDeleteReview,
                    onReportReview = onReportReview,
                    onUpdateProgress = onUpdateProgress,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (state.book != null) {
            BottomDock(
                currentShelf = currentShelf,
                isLiked = state.bookState.isLiked,
                onAdd = { showLibrarySheet = true },
                onLike = onToggleLike,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        toast?.let { ToastBar(it, modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 100.dp)) }
    }

    if (showRateGate) {
        AlertDialog(
            onDismissRequest = { showRateGate = false },
            containerColor = HL.Card,
            title = { Text("Log a few pages first", color = HL.Ink) },
            text = {
                Text(
                    "Rating and reviewing open up once you're $MIN_PAGES_TO_RATE pages in — " +
                        "or once you've marked the book as finished.",
                    color = HL.Muted
                )
            },
            confirmButton = {
                TextButton(onClick = { showRateGate = false }) {
                    Text("Got it", color = HL.Ink)
                }
            }
        )
    }

    if (showLibrarySheet) {
        AddToLibrarySheet(
            current = currentShelf,
            onSelect = {
                showLibrarySheet = false
                onSelectShelf(it)
            },
            onDismiss = { showLibrarySheet = false }
        )
    }

    if (showShare && state.book != null) {
        BookShareSheet(
            book = state.book,
            handle = handle,
            rating = myReview?.rating,
            note = myReview?.review,
            // Mirrors iOS `shareStatus`: shelf state picks the badge, null hides it.
            status = with(state.bookState) {
                when {
                    isRead -> ShareStatus.Finished
                    isOnShelf -> ShareStatus.Reading
                    isTbr -> ShareStatus.Want
                    isLiked -> ShareStatus.Favourite
                    else -> null
                }
            },
            onDismiss = { showShare = false }
        )
    }
}

private enum class InlinePanel { Rate, Review }

// ---- Mini header ----

@Composable
private fun MiniHeader(book: Book?, onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(Line, topLeft = Offset(0f, size.height - 2.dp.toPx()), size = Size(size.width, 2.dp.toPx()))
            }
            .padding(horizontal = 14.dp)
            .padding(top = 16.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SquareIcon(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Back", tint = HL.Ink, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.weight(1f))
        book?.categories?.takeIf { it.isNotEmpty() }?.let {
            Text(
                it.take(2).joinToString(" · ").uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 1.5.sp,
                color = HL.Muted,
                maxLines = 1
            )
        }
        Spacer(Modifier.weight(1f))
        Spacer(Modifier.size(36.dp)) // balance the back button
    }
}

@Composable
private fun SquareIcon(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .border(2.dp, Line, RectangleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

// ---- Scroll body ----

@Composable
private fun DetailBody(
    state: BookDetailUiState,
    myReview: BookReview?,
    activePanel: InlinePanel?,
    onOpenBook: (String) -> Unit,
    onOpenAuthor: (String) -> Unit,
    onShare: () -> Unit,
    onSetPanel: (InlinePanel?) -> Unit,
    onSubmitReview: (Int, String?, (Boolean) -> Unit) -> Unit,
    onDeleteReview: ((Boolean) -> Unit) -> Unit,
    onReportReview: (BookReview, String) -> Unit,
    onUpdateProgress: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val book = state.book!!
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 140.dp)
    ) {
        item { CoverBlock(book, state.friendsOnBook, state.friendsReadingCount) }
        item { TitleBlock(book, onOpenAuthor) }
        item { StatStrip(book) }

        item {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp).padding(top = 18.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (book.pageCount != null || (state.progress?.onShelf == true)) {
                    PageProgressCard(
                        progress = state.progress,
                        bookPageCount = book.pageCount,
                        isSaving = state.isSavingProgress,
                        onUpdate = onUpdateProgress
                    )
                }
                ActionRow(
                    book = book,
                    myReview = myReview,
                    activePanel = activePanel,
                    onTogglePanel = { onSetPanel(if (activePanel == it) null else it) },
                    onShare = onShare
                )
                InlinePanelView(
                    panel = activePanel,
                    myReview = myReview,
                    isSubmitting = state.isSubmittingReview,
                    onSubmit = { rating, review -> onSubmitReview(rating, review) { ok -> if (ok) onSetPanel(null) } },
                    onDelete = { onDeleteReview { ok -> if (ok) onSetPanel(null) } },
                    onClose = { onSetPanel(null) }
                )
                DescriptionSection(book)
            }
        }

        if (state.similarBooks.isNotEmpty()) {
            item { SimilarSection(state.similarBooks, onOpenBook) }
        }
        item { FriendsSaySection(state.friendReviews) }
        if (state.reviews.isNotEmpty()) {
            item { AllReviewsSection(state.reviews, onReport = onReportReview) }
        }
    }
}

// ---- Cover + friends ----

@Composable
private fun CoverBlock(book: Book, friends: List<FriendOnBook>, readingCount: Int) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(modifier = Modifier.padding(end = 8.dp, bottom = 8.dp)) {
            Box(
                Modifier
                    .offset(x = 8.dp, y = 8.dp)
                    .size(width = 150.dp, height = 225.dp)
                    .background(Line)
            )
            BookCoverImage(
                url = book.coverUrl,
                title = book.title,
                modifier = Modifier
                    .size(width = 150.dp, height = 225.dp)
                    .border(2.5.dp, Line, RectangleShape),
                cornerRadius = 0.dp
            )
        }

        if (friends.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .border(2.dp, Line, RectangleShape)
                    .padding(horizontal = 11.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy((-7).dp)) {
                    friends.take(3).forEach { f ->
                        Box(modifier = Modifier.border(1.5.dp, HL.Paper, RectangleShape)) {
                            AvatarImage(url = f.avatarUrl, name = f.displayName, size = 20.dp)
                        }
                    }
                }
                val copy = if (readingCount > 0) {
                    "$readingCount friend${if (readingCount == 1) "" else "s"} reading"
                } else {
                    "${friends.size} of your friends"
                }
                Text(
                    copy.uppercase(),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.sp,
                    color = HL.Ink
                )
            }
        }
    }
}

// ---- Title ----

@Composable
private fun TitleBlock(book: Book, onOpenAuthor: (String) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            book.title.uppercase(),
            fontWeight = FontWeight.Black,
            fontSize = 30.sp,
            lineHeight = 32.sp,
            letterSpacing = (-1).sp,
            color = HL.Ink,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
        bookTagline(book)?.let {
            Text(
                it,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 15.sp,
                color = HL.Ink.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 30.dp)
            )
        }
        if (book.authors.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                book.authors.forEachIndexed { idx, author ->
                    if (idx > 0) {
                        Text(
                            "·",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = HL.Muted
                        )
                    }
                    Text(
                        author.uppercase(),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        color = HL.Ink,
                        textDecoration = TextDecoration.Underline,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.clickable { onOpenAuthor(author) }
                    )
                }
            }
        }
    }
}

private fun bookTagline(book: Book): String? {
    val desc = book.description ?: return null
    val first = desc.split(Regex("[.!?]")).firstOrNull()?.trim() ?: return null
    return if (first.length in 21..139) "$first." else null
}

// ---- Stat strip ----

@Composable
private fun StatStrip(book: Book) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(62.dp)
            .drawBehind {
                val h = 2.dp.toPx()
                drawRect(Line, size = Size(size.width, h))
                drawRect(Line, topLeft = Offset(0f, size.height - h), size = Size(size.width, h))
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatCell(book.averageRating?.let { "%.1f".format(it) } ?: "—", "Rating", Modifier.weight(1f))
        Box(Modifier.width(2.dp).height(62.dp).background(Line))
        StatCell(readingTime(book), "Time to read", Modifier.weight(1f))
        Box(Modifier.width(2.dp).height(62.dp).background(Line))
        StatCell(book.pageCount?.toString() ?: "—", "Pages", Modifier.weight(1f))
    }
}

@Composable
private fun StatCell(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(value, fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = (-0.5).sp, color = HL.Ink)
        Text(
            label.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 8.sp,
            letterSpacing = 1.2.sp,
            color = HL.Muted
        )
    }
}

private fun readingTime(book: Book): String {
    val pages = book.pageCount ?: return "—"
    val totalMin = (pages / 40.0 * 60.0).roundToInt()
    val h = totalMin / 60
    val m = totalMin % 60
    return if (h > 0) "${h}h ${m}m" else "${m}m"
}

// ---- Action row (Review / Rate / Share) ----

@Composable
private fun ActionRow(
    book: Book,
    myReview: BookReview?,
    activePanel: InlinePanel?,
    onTogglePanel: (InlinePanel) -> Unit,
    onShare: () -> Unit
) {
    val rated = (myReview?.rating ?: 0) > 0
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ActionTile(
            icon = { Icon(Icons.Filled.Edit, null, tint = it, modifier = Modifier.size(18.dp)) },
            label = "Review",
            active = activePanel == InlinePanel.Review,
            modifier = Modifier.weight(1f),
            onClick = { onTogglePanel(InlinePanel.Review) }
        )
        ActionTile(
            icon = { Icon(Icons.Filled.Star, null, tint = it, modifier = Modifier.size(18.dp)) },
            label = if (rated) "${myReview!!.rating}/5" else "Rate",
            active = activePanel == InlinePanel.Rate || rated,
            modifier = Modifier.weight(1f),
            onClick = { onTogglePanel(InlinePanel.Rate) }
        )
        ActionTile(
            icon = { Icon(Icons.Outlined.Share, null, tint = it, modifier = Modifier.size(18.dp)) },
            label = "Share",
            active = false,
            modifier = Modifier.weight(1f),
            onClick = onShare
        )
    }
}

@Composable
private fun ActionTile(
    icon: @Composable (Color) -> Unit,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val fg = if (active) HL.Paper else HL.Ink
    Column(
        modifier = modifier
            .brutalButton(onClick = onClick, fill = if (active) HL.Accent else HL.Paper, borderWidth = 2.dp, offset = 3.dp, shadow = Color.Transparent)
            .padding(vertical = 13.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        icon(fg)
        Text(
            label.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 9.sp,
            letterSpacing = 1.sp,
            color = fg
        )
    }
}

// ---- Inline rate / review panels ----

@Composable
private fun InlinePanelView(
    panel: InlinePanel?,
    myReview: BookReview?,
    isSubmitting: Boolean,
    onSubmit: (Int, String?) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    val hasReviewOrRating = (myReview?.rating ?: 0) > 0 || !myReview?.review.isNullOrEmpty()
    AnimatedVisibility(
        visible = panel != null,
        // iOS `.brutalReveal`: drops in from the top with a slight bounce, fades out flat.
        enter = fadeIn(tween(180)) +
            expandVertically(spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow), expandFrom = Alignment.Top) +
            slideInVertically(spring(dampingRatio = 0.72f, stiffness = Spring.StiffnessLow)) { -it / 5 },
        exit = fadeOut(tween(140)) + shrinkVertically(tween(160))
    ) {
        when (panel) {
            InlinePanel.Rate -> RatePanel(
                initial = myReview?.rating ?: 0,
                isSubmitting = isSubmitting,
                canDelete = hasReviewOrRating,
                onSubmit = { rating -> onSubmit(rating, myReview?.review) },
                onDelete = onDelete,
                onClose = onClose
            )
            InlinePanel.Review -> ReviewPanel(
                initial = myReview?.review ?: "",
                isSubmitting = isSubmitting,
                canDelete = hasReviewOrRating,
                onPost = { text -> onSubmit(myReview?.rating ?: 0, text) },
                onDelete = onDelete,
                onClose = onClose
            )
            null -> {}
        }
    }
}

@Composable
private fun RatePanel(
    initial: Int,
    isSubmitting: Boolean,
    canDelete: Boolean,
    onSubmit: (Int) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    var pending by remember { mutableIntStateOf(initial) }
    val starPulse = remember { mutableStateListOf(0, 0, 0, 0, 0, 0) }
    PanelShell {
        Text(
            "TAP TO SET YOUR RATING",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            letterSpacing = 2.sp,
            color = HL.Muted,
            modifier = Modifier.padding(top = 12.dp, bottom = 8.dp)
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            for (n in 1..5) {
                Icon(
                    if (n <= pending) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = "star $n",
                    tint = if (n <= pending) HL.Accent else HL.Muted.copy(alpha = 0.4f),
                    modifier = Modifier
                        .size(30.dp)
                        .popEffect(trigger = starPulse[n])
                        .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) {
                            pending = n
                            starPulse[n]++
                        }
                )
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            if (pending > 0) "$pending.0 / 5" else "—",
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = HL.Ink,
            modifier = Modifier.padding(bottom = if (canDelete) 8.dp else 12.dp)
        )
        if (canDelete) {
            Text(
                "REMOVE RATING & REVIEW",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp,
                letterSpacing = 1.2.sp,
                color = HL.Accent,
                modifier = Modifier.clickable(enabled = !isSubmitting, onClick = onDelete).padding(bottom = 12.dp)
            )
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(Line))
        PanelSubmitRow(
            label = "SUBMIT RATING",
            enabled = pending > 0 && !isSubmitting,
            isSubmitting = isSubmitting,
            onSubmit = { onSubmit(pending) },
            onClose = onClose
        )
    }
}

@Composable
private fun ReviewPanel(
    initial: String,
    isSubmitting: Boolean,
    canDelete: Boolean,
    onPost: (String) -> Unit,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    var text by remember { mutableStateOf(initial) }
    PanelShell(horizontalAlignment = Alignment.Start) {
        Text(
            "WRITE A REVIEW",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 9.sp,
            letterSpacing = 2.sp,
            color = HL.Muted,
            modifier = Modifier.padding(horizontal = 14.dp).padding(top = 12.dp, bottom = 4.dp)
        )
        Box(modifier = Modifier.padding(horizontal = 10.dp)) {
            if (text.isEmpty()) {
                Text(
                    "What stayed with you? Half-formed thoughts welcome…",
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic,
                    fontSize = 15.sp,
                    color = HL.Muted,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                )
            }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                textStyle = TextStyle(
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    color = HL.Ink
                ),
                cursorBrush = SolidColor(HL.Accent),
                modifier = Modifier.fillMaxWidth().heightIn(min = 84.dp).padding(vertical = 8.dp)
            )
        }
        if (canDelete) {
            Box(Modifier.fillMaxWidth().height(2.dp).background(Line))
            Text(
                "REMOVE RATING & REVIEW",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 9.sp,
                letterSpacing = 1.2.sp,
                color = HL.Accent,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !isSubmitting, onClick = onDelete)
                    .padding(vertical = 10.dp)
            )
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(Line))
        PanelSubmitRow(
            label = "POST REVIEW",
            enabled = text.trim().isNotEmpty() && !isSubmitting,
            isSubmitting = isSubmitting,
            onSubmit = { onPost(text) },
            onClose = onClose
        )
    }
}

@Composable
private fun PanelShell(
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .brutalPlate(fill = HL.Card, borderWidth = 2.5.dp, offset = 6.dp),
        horizontalAlignment = horizontalAlignment,
        content = content
    )
}

@Composable
private fun PanelSubmitRow(
    label: String,
    enabled: Boolean,
    isSubmitting: Boolean,
    onSubmit: () -> Unit,
    onClose: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().height(46.dp)) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .background(if (enabled || isSubmitting) HL.Ink else HL.Ink.copy(alpha = 0.4f))
                .clickable(enabled = enabled, onClick = onSubmit),
            contentAlignment = Alignment.Center
        ) {
            if (isSubmitting) {
                CircularProgressIndicator(color = HL.Paper, strokeWidth = 2.dp, modifier = Modifier.size(16.dp))
            } else {
                Text(
                    label,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    letterSpacing = 1.5.sp,
                    color = HL.Paper
                )
            }
        }
        Box(Modifier.width(2.dp).fillMaxSize().background(Line))
        Box(
            modifier = Modifier.width(50.dp).fillMaxSize().clickable(onClick = onClose),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Close, "close", tint = HL.Ink, modifier = Modifier.size(14.dp))
        }
    }
}

// ---- Page progress card ----

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PageProgressCard(
    progress: ReadingProgress?,
    bookPageCount: Int?,
    isSaving: Boolean,
    onUpdate: (Int, Int) -> Unit
) {
    val savedPage = progress?.currentPage ?: 0
    // A total typed via SET TOTAL wins over the API's — some books come back
    // with no page count at all, and without one progress can never be saved.
    var total by remember(progress?.totalPages, bookPageCount) {
        mutableIntStateOf(progress?.totalPages ?: bookPageCount ?: 0)
    }
    var localPage by remember(savedPage) { mutableIntStateOf(savedPage) }
    var editingPage by remember { mutableStateOf(false) }
    var editingTotal by remember { mutableStateOf(false) }
    var pageInput by remember { mutableStateOf("") }
    var totalInput by remember { mutableStateOf("") }
    val pageFocus = remember { FocusRequester() }
    val totalFocus = remember { FocusRequester() }
    val cellCount = 12
    val percent = if (total > 0) (localPage.toFloat() / total).coerceIn(0f, 1f) else 0f
    val litCells = (cellCount * percent).roundToInt()
    val hasChanges = localPage != savedPage

    fun step(delta: Int) {
        val max = if (total > 0) total else Int.MAX_VALUE
        localPage = (localPage + delta).coerceIn(0, max)
    }

    fun commitPage() {
        pageInput.toIntOrNull()?.let {
            localPage = it.coerceIn(0, if (total > 0) total else Int.MAX_VALUE)
        }
        editingPage = false
    }

    fun commitTotal() {
        totalInput.toIntOrNull()?.takeIf { it > 0 }?.let { total = it }
        editingTotal = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .brutalPlate(fill = HL.Paper2, borderWidth = 2.5.dp, offset = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "READING PROGRESS",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = HL.Muted
            )
            Spacer(Modifier.weight(1f))
            Text(
                "${(percent * 100).roundToInt()}%",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                letterSpacing = (-0.5).sp,
                color = HL.Ink
            )
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(Line))
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (i in 0 until cellCount) {
                Box(
                    Modifier
                        .weight(1f)
                        .height(16.dp)
                        .background(if (i < litCells) HL.Accent else Color.Transparent)
                        .border(2.dp, Line, RectangleShape)
                )
            }
        }
        Box(Modifier.fillMaxWidth().height(2.dp).background(Line))
        Row(modifier = Modifier.fillMaxWidth().height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            StepButton("−", onStep = { step(-1) }, onLongStep = { step(-10) })
            Box(Modifier.width(2.dp).fillMaxSize().background(Line))
            Column(
                modifier = Modifier.weight(1f).padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    when {
                        // No page count anywhere — let the reader supply one, else
                        // there is nothing to measure progress against.
                        total == 0 && editingTotal -> {
                            Text("p. $localPage /", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = HL.Muted)
                            NumberField(
                                value = totalInput,
                                onValueChange = { totalInput = it },
                                onDone = { commitTotal() },
                                focusRequester = totalFocus
                            )
                            LaunchedEffect(Unit) { totalFocus.requestFocus() }
                        }
                        total == 0 -> {
                            Text(
                                "p. $localPage",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = HL.Ink
                            )
                            Text(
                                "/ SET TOTAL",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp,
                                color = HL.Accent,
                                modifier = Modifier.clickable { totalInput = ""; editingTotal = true }
                            )
                        }
                        editingPage -> {
                            Text("p.", fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = HL.Muted)
                            NumberField(
                                value = pageInput,
                                onValueChange = { pageInput = it },
                                onDone = { commitPage() },
                                focusRequester = pageFocus
                            )
                            Text("/ $total", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = HL.Muted)
                            Text(
                                "DONE",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 9.sp,
                                color = HL.Accent,
                                modifier = Modifier.clickable { commitPage() }
                            )
                            LaunchedEffect(Unit) { pageFocus.requestFocus() }
                        }
                        else -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.clickable {
                                    pageInput = localPage.toString()
                                    editingPage = true
                                }
                            ) {
                                Text("p. $localPage", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = HL.Ink)
                                Text("/ $total", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = HL.Muted)
                                Icon(
                                    Icons.Filled.Edit,
                                    contentDescription = "Edit page",
                                    tint = HL.Muted,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }
                    }
                }
                if (hasChanges && total > 0) {
                    Box(
                        modifier = Modifier
                            .background(HL.Ink)
                            .clickable(enabled = !isSaving) { onUpdate(localPage, total) }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = HL.Paper, strokeWidth = 2.dp, modifier = Modifier.size(12.dp))
                        } else {
                            Text("TAP TO SAVE", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold, fontSize = 8.sp, letterSpacing = 1.5.sp, color = HL.Paper)
                        }
                    }
                } else {
                    Text(
                        "TAP ± TO LOG PAGES",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        letterSpacing = 1.2.sp,
                        color = HL.Muted
                    )
                }
            }
            Box(Modifier.width(2.dp).fillMaxSize().background(Line))
            StepButton("+", onStep = { step(1) }, onLongStep = { step(10) })
        }
        finishLine(progress?.estimatedFinishDate)?.let { line ->
            Box(Modifier.fillMaxWidth().height(2.dp).background(Line))
            Text(
                line.uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                color = HL.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp)
            )
        }
    }
}

/** "at this pace, finished by Tuesday" — iOS twin: PageProgressView.finishLine. */
private fun finishLine(estimatedFinishDate: String?): String? {
    val date = estimatedFinishDate?.let { runCatching { LocalDate.parse(it) }.getOrNull() } ?: return null
    val day = date.dayOfWeek.getDisplayName(JavaTextStyle.FULL, Locale.getDefault())
    return "at this pace, finished by $day"
}

/** Monospace inline number entry for the page / total fields. */
@Composable
private fun NumberField(
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit,
    focusRequester: FocusRequester
) {
    BasicTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() }.take(5)) },
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = HL.Ink,
            textAlign = TextAlign.Center
        ),
        singleLine = true,
        cursorBrush = SolidColor(HL.Accent),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
        modifier = Modifier
            .width(52.dp)
            .focusRequester(focusRequester)
            .drawBehind {
                drawRect(
                    HL.Accent,
                    topLeft = Offset(0f, size.height - 2.dp.toPx()),
                    size = Size(size.width, 2.dp.toPx())
                )
            }
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StepButton(symbol: String, onStep: () -> Unit, onLongStep: () -> Unit) {
    Box(
        modifier = Modifier
            .width(52.dp)
            .fillMaxSize()
            .background(HL.Paper)
            .combinedClickable(onClick = onStep, onLongClick = onLongStep),
        contentAlignment = Alignment.Center
    ) {
        Text(symbol, fontSize = 22.sp, color = HL.Ink)
    }
}

// ---- Sections ----

@Composable
private fun BrutalSectionHeader(num: String, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(Line, topLeft = Offset(0f, size.height - 2.dp.toPx()), size = Size(size.width, 2.dp.toPx()))
            }
            .padding(bottom = 6.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(num, fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = HL.Muted)
        Text(title.uppercase(), fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 0.5.sp, color = HL.Ink)
    }
}

@Composable
private fun DescriptionSection(book: Book) {
    val desc = book.description ?: return
    var expanded by remember { mutableStateOf(false) }
    val plain = remember(desc) { desc.replace(Regex("<[^>]+>"), " ").replace(Regex("\\s+"), " ").trim() }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        BrutalSectionHeader("01", "About this book")
        Text(
            plain,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            color = HL.Ink.copy(alpha = 0.9f),
            maxLines = if (expanded) Int.MAX_VALUE else 6
        )
        if (plain.length > 280) {
            Text(
                if (expanded) "SHOW LESS" else "READ MORE",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                letterSpacing = 1.sp,
                color = HL.Accent,
                modifier = Modifier.clickable { expanded = !expanded }
            )
        }
    }
}

@Composable
private fun SimilarSection(items: List<RecommendationItem>, onOpenBook: (String) -> Unit) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp)) {
            BrutalSectionHeader("02", "Readers also enjoyed")
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(items, key = { it.id }) { item ->
                Column(
                    modifier = Modifier.width(84.dp).clickable { onOpenBook(item.id) },
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    BookCoverImage(
                        url = item.coverUrl,
                        title = item.title,
                        modifier = Modifier.width(84.dp).aspectRatio(2f / 3f).border(1.5.dp, Line, RectangleShape),
                        cornerRadius = 0.dp
                    )
                    Text(item.title, fontWeight = FontWeight.SemiBold, fontSize = 11.sp, color = HL.Ink, maxLines = 2)
                }
            }
        }
    }
}

@Composable
private fun FriendsSaySection(reviews: List<FriendBookReview>) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        BrutalSectionHeader("03", "What friends say")
        if (reviews.isEmpty()) {
            Text(
                "No reviews from people you follow yet.",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp,
                color = HL.Muted
            )
        } else {
            reviews.take(5).forEach { r ->
                Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                    Box(modifier = Modifier.border(2.dp, Line, RectangleShape)) {
                        AvatarImage(url = r.avatarUrl, name = r.displayName, size = 34.dp)
                    }
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(r.displayName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HL.Ink, modifier = Modifier.weight(1f))
                            r.rating?.takeIf { it > 0 }?.let {
                                Text(starsString(it), fontSize = 11.sp, color = HL.Accent)
                            }
                        }
                        r.review?.let {
                            Text(
                                it,
                                fontFamily = FontFamily.Serif,
                                fontSize = 13.5.sp,
                                lineHeight = 17.sp,
                                color = HL.Ink.copy(alpha = 0.88f),
                                maxLines = 5
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AllReviewsSection(
    reviews: List<BookReview>,
    onReport: (BookReview, String) -> Unit = { _, _ -> }
) {
    var reportTarget by remember { mutableStateOf<BookReview?>(null) }
    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)) {
        BrutalSectionHeader("04", "All reviews")
        reviews.forEachIndexed { idx, review ->
            if (idx > 0) Box(Modifier.fillMaxWidth().height(1.dp).background(Line.copy(alpha = 0.3f)))
            Row(
                modifier = Modifier.padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(11.dp)
            ) {
                Box(modifier = Modifier.border(2.dp, Line, RectangleShape)) {
                    AvatarImage(url = review.avatarUrl, name = review.username, size = 34.dp)
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(review.username, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = HL.Ink)
                        review.rating?.takeIf { it > 0 }?.let {
                            Text(starsString(it), fontSize = 11.sp, color = HL.Accent)
                        }
                    }
                    review.review?.takeIf { it.isNotEmpty() }?.let {
                        Text(
                            it,
                            fontFamily = FontFamily.Serif,
                            fontSize = 13.5.sp,
                            lineHeight = 17.sp,
                            color = HL.Ink.copy(alpha = 0.88f)
                        )
                    }
                }
                Icon(
                    Icons.Outlined.Flag,
                    contentDescription = "Report review",
                    tint = HL.Muted.copy(alpha = 0.6f),
                    modifier = Modifier
                        .size(16.dp)
                        .clickable { reportTarget = review }
                )
            }
        }
    }
    reportTarget?.let { target ->
        ReportDialog(
            title = "Report review",
            onDismiss = { reportTarget = null },
            onSubmit = { reason -> onReport(target, reason) }
        )
    }
}

private fun starsString(rating: Int): String {
    val n = rating.coerceIn(0, 5)
    return "★".repeat(n) + "☆".repeat(5 - n)
}

// ---- Bottom dock ----

@Composable
private fun BottomDock(
    currentShelf: LibraryShelf?,
    isLiked: Boolean,
    onAdd: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    val label = when (currentShelf) {
        LibraryShelf.Bookshelf -> "On Bookshelf"
        LibraryShelf.Tbr -> "On TBR"
        LibraryShelf.Read -> "Read"
        null -> "Add to Library"
    }
    // Pop fires only on like-on, mirroring iOS `if !isLiked { likePulse += 1 }`.
    var likePulse by remember { mutableIntStateOf(0) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(Line, size = Size(size.width, 3.dp.toPx()))
            }
            .background(HL.Paper)
            .padding(horizontal = 14.dp)
            .padding(top = 12.dp)
            .navigationBarsPadding()
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
                .brutalButton(
                    onClick = onAdd,
                    fill = if (currentShelf == null) HL.Ink else HL.Accent,
                    borderWidth = 2.dp,
                    offset = 4.dp,
                    shadow = HL.Accent
                ),
            horizontalArrangement = Arrangement.spacedBy(9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.weight(1f))
            Icon(
                if (currentShelf == null) Icons.Filled.Add else Icons.Filled.Check,
                null,
                tint = HL.Paper,
                modifier = Modifier.size(15.dp)
            )
            Text(label.uppercase(), fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 0.5.sp, color = HL.Paper, maxLines = 1)
            Spacer(Modifier.weight(1f))
        }
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 52.dp)
                .brutalButton(
                    onClick = {
                        if (!isLiked) likePulse++
                        onLike()
                    },
                    fill = HL.Paper,
                    borderWidth = 2.dp,
                    offset = 4.dp,
                    shadow = Line
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                null,
                tint = if (isLiked) LikeRed else HL.Ink,
                modifier = Modifier.size(20.dp).popEffect(trigger = likePulse)
            )
        }
    }
}

// ---- Add to Library sheet ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddToLibrarySheet(
    current: LibraryShelf?,
    onSelect: (LibraryShelf?) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = HL.Paper,
        dragHandle = null
    ) {
        Column(modifier = Modifier.navigationBarsPadding()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        drawRect(Line, topLeft = Offset(0f, size.height - 2.dp.toPx()), size = Size(size.width, 2.dp.toPx()))
                    }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ADD TO LIBRARY", fontWeight = FontWeight.Black, fontSize = 15.sp, letterSpacing = 0.5.sp, color = HL.Ink)
                Spacer(Modifier.weight(1f))
                SquareIcon(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, "close", tint = HL.Ink, modifier = Modifier.size(13.dp))
                }
            }
            LibraryOption(LibraryShelf.Bookshelf, Icons.Outlined.LibraryBooks, "Add to Bookshelf", "Books you own — lands on your bookshelf", current, onSelect)
            LibraryOption(LibraryShelf.Tbr, Icons.Outlined.AccessTime, "Add to TBR", "To-be-read — your want-to-read queue", current, onSelect)
            LibraryOption(LibraryShelf.Read, Icons.Outlined.CheckCircle, "Mark as Read", "Finished — 100% progress on your bookshelf", current, onSelect)
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun LibraryOption(
    shelf: LibraryShelf,
    icon: ImageVector,
    title: String,
    sub: String,
    current: LibraryShelf?,
    onSelect: (LibraryShelf?) -> Unit
) {
    val on = current == shelf
    val fg = if (on) HL.Paper else HL.Ink
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (on) HL.Accent else HL.Paper)
            .drawBehind {
                drawRect(Line, topLeft = Offset(0f, size.height - 2.dp.toPx()), size = Size(size.width, 2.dp.toPx()))
            }
            .clickable { onSelect(if (on) null else shelf) }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        Box(
            modifier = Modifier.size(36.dp).border(2.dp, if (on) HL.Paper else Line, RectangleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = fg, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(title.uppercase(), fontWeight = FontWeight.Bold, fontSize = 15.sp, letterSpacing = 0.3.sp, color = fg)
            Text(
                sub,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                color = if (on) HL.Paper.copy(alpha = 0.85f) else HL.Muted
            )
        }
        if (on) Icon(Icons.Filled.Check, null, tint = HL.Paper, modifier = Modifier.size(15.dp))
    }
}

// ---- Toast ----

@Composable
private fun ToastBar(message: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(HL.Ink)
            .border(2.dp, HL.Accent, RectangleShape)
            .padding(horizontal = 15.dp, vertical = 9.dp)
    ) {
        Text(
            message.uppercase(),
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.SemiBold,
            fontSize = 10.sp,
            letterSpacing = 1.sp,
            color = HL.Paper
        )
    }
}
