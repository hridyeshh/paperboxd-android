package `in`.paperboxd.app.ui.screens.search

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.UserProfile
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.ShimmerBox

/** Hairline #e6dfd0 from "Search - Brutalist Mobile.html". */
private val Line = Color(0xFFE6DFD0)

/**
 * Search — light-mode-only brutalist paper screen, iOS SearchView twin.
 * Idle: "Picked for you" rail + trending wall. Focused: recents / typed results
 * with Books | Readers chips.
 */
@Composable
fun SearchScreen(
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var focused by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    // Search mode is "there is an active search", not "the field has focus".
    // Opening a book takes this screen out of composition and drops keyboard
    // focus, so gating on `focused` alone dumped you back on the wall instead
    // of your results. The query itself survives in the ViewModel.
    val inSearchMode = focused || state.hasQuery

    LaunchedEffect(Unit) {
        viewModel.loadWallIfNeeded()
        viewModel.loadRecommendationsIfNeeded()
    }

    val view = LocalView.current
    DisposableEffect(Unit) {
        val activity = view.context as? android.app.Activity ?: return@DisposableEffect onDispose {}
        val controller = WindowCompat.getInsetsController(activity.window, view)
        val wasLight = controller.isAppearanceLightStatusBars
        controller.isAppearanceLightStatusBars = true
        onDispose { controller.isAppearanceLightStatusBars = wasLight }
    }

    Column(modifier = Modifier.fillMaxSize().background(HL.Paper).statusBarsPadding()) {
        SearchHeader(
            state = state,
            focused = inSearchMode,
            focusRequester = focusRequester,
            onFocusChanged = { focused = it },
            onQueryChanged = viewModel::onQueryChanged,
            onTypeChanged = viewModel::onTypeChanged,
            onBack = {
                focusManager.clearFocus()
                viewModel.onQueryChanged("")
            },
            onClear = { viewModel.onQueryChanged("") }
        )

        if (inSearchMode) {
            if (state.hasQuery) {
                ResultsOverlay(state, onOpenBook, onOpenProfile)
            } else {
                RecentsOverlay(
                    recents = state.recentSearches,
                    onPick = viewModel::onQueryChanged,
                    onRemove = viewModel::removeFromHistory
                )
            }
        } else {
            WallScroll(state, viewModel, onOpenBook)
        }
    }
}

// ---- Header: brutal search bar + type chips ----

@Composable
private fun SearchHeader(
    state: SearchUiState,
    focused: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onQueryChanged: (String) -> Unit,
    onTypeChanged: (SearchType) -> Unit,
    onBack: () -> Unit,
    onClear: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(HL.Paper.copy(alpha = 0.96f))
            .drawBehind {
                drawRect(Line, topLeft = Offset(0f, size.height - 1.dp.toPx()))
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp)
                .padding(top = 14.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (focused) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Back",
                    tint = HL.Ink,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable(onClick = onBack)
                        .padding(6.dp)
                )
            }
            SearchBarField(
                state = state,
                pressed = focused,
                focusRequester = focusRequester,
                onFocusChanged = onFocusChanged,
                onQueryChanged = onQueryChanged,
                onClear = onClear,
                modifier = Modifier.weight(1f)
            )
        }

        if (focused) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                SearchType.entries.forEach { type ->
                    TypeChip(type, selected = state.searchType == type) { onTypeChanged(type) }
                }
            }
        }
    }
}

/**
 * Brutalist search bar. Idle: hard 5dp ink shadow behind the face. Focused: the
 * face presses into its own shadow — instant, no easing (iOS BrutalButtonStyle).
 */
@Composable
private fun SearchBarField(
    state: SearchUiState,
    pressed: Boolean,
    focusRequester: FocusRequester,
    onFocusChanged: (Boolean) -> Unit,
    onQueryChanged: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shadow = 5.dp
    val push = if (pressed) shadow else 0.dp
    val placeholder = when (state.searchType) {
        SearchType.Books -> "Title, author, or ISBN..."
        SearchType.Readers -> "Find readers by username..."
    }

    Row(
        modifier = modifier
            .padding(end = shadow, bottom = shadow)
            .offset(x = push, y = push)
            .drawBehind {
                if (!pressed) {
                    val off = shadow.toPx()
                    drawRect(HL.Ink, topLeft = Offset(off, off), size = size)
                }
            }
            .background(HL.Card)
            .border(1.5.dp, HL.Ink)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            tint = HL.Ink,
            modifier = Modifier.size(16.dp)
        )
        BasicTextField(
            value = state.query,
            onValueChange = onQueryChanged,
            singleLine = true,
            textStyle = TextStyle(fontSize = 15.sp, color = HL.Ink),
            cursorBrush = SolidColor(HL.Accent),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { }),
            decorationBox = { inner ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (state.query.isEmpty()) {
                        Text(placeholder, fontSize = 15.sp, color = HL.Muted)
                    }
                    inner()
                }
            },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
                .onFocusChanged { onFocusChanged(it.isFocused) }
        )
        if (state.query.isNotEmpty()) {
            Icon(
                Icons.Filled.Cancel,
                contentDescription = "Clear",
                tint = HL.Muted,
                modifier = Modifier.size(16.dp).clickable(onClick = onClear)
            )
        }
    }
}

@Composable
private fun TypeChip(type: SearchType, selected: Boolean, onClick: () -> Unit) {
    Text(
        text = type.name,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        color = if (selected) HL.Paper else HL.Muted,
        modifier = Modifier
            .clip(CircleShape)
            .background(if (selected) HL.Ink else Color.Transparent)
            .let { if (selected) it else it.border(1.dp, Line, CircleShape) }
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

// ---- Idle wall: recs rail + trending grid ----

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WallScroll(state: SearchUiState, viewModel: SearchViewModel, onOpenBook: (String) -> Unit) {
    PullToRefreshBox(
        isRefreshing = state.isLoadingWall && state.wallBooks.isNotEmpty(),
        onRefresh = viewModel::shuffleWall
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, bottom = 112.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            if (state.recommendations.isNotEmpty()) {
                item(span = { GridItemSpan(3) }) {
                    Column(
                        modifier = Modifier.padding(top = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            EyebrowText("Picked for you")
                            Text(
                                "Based on your shelves.",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 17.sp,
                                color = HL.Ink
                            )
                        }
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            lazyItems(state.recommendations, key = { it.id }) { rec ->
                                RecsRailCard(rec, onClick = { onOpenBook(rec.id) })
                            }
                        }
                    }
                }
            }

            item(span = { GridItemSpan(3) }) {
                Column(
                    modifier = Modifier.padding(
                        top = if (state.recommendations.isEmpty()) 16.dp else 8.dp,
                        bottom = 8.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    EyebrowText("The wall")
                    Text(
                        "What everyone is shelving.",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 17.sp,
                        color = HL.Ink
                    )
                }
            }

            if (state.isLoadingWall && state.wallBooks.isEmpty()) {
                items(12) { ShimmerBox(modifier = Modifier.aspectRatio(2f / 3f)) }
            } else {
                items(state.wallBooks, key = { it.id }) { book ->
                    LaunchedEffect(book.id) {
                        if (book.id == state.wallBooks.lastOrNull()?.id) viewModel.loadMoreWall()
                    }
                    BookCoverImage(
                        url = book.coverUrl,
                        title = book.title,
                        modifier = Modifier
                            .aspectRatio(2f / 3f)
                            .clickable { onOpenBook(book.id) },
                        cornerRadius = 5.dp
                    )
                }
            }

            if (state.isLoadingMoreWall) {
                item(span = { GridItemSpan(3) }) {
                    Text(
                        "··· loading more ···",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp,
                        color = HL.Muted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RecsRailCard(item: RecommendationItem, onClick: () -> Unit) {
    Column(
        modifier = Modifier.width(96.dp).clickable(onClick = onClick),
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
                item.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.5.sp,
                lineHeight = 15.sp,
                color = HL.Ink,
                maxLines = 2
            )
            if (item.authorLine.isNotEmpty()) {
                Text(item.authorLine, fontSize = 10.5.sp, color = HL.Muted, maxLines = 1)
            }
        }
    }
}

// ---- Focused: recents ----

@Composable
private fun RecentsOverlay(
    recents: List<String>,
    onPick: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    if (recents.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(top = 40.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                "search anything.",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
                color = HL.Ink
            )
            Text(
                "Books or readers.",
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontSize = 18.sp,
                color = HL.Muted
            )
        }
    } else {
        LazyColumn {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(top = 20.dp, bottom = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    EyebrowText("Recent")
                    Spacer(Modifier.weight(1f))
                }
            }
            lazyItems(recents, key = { it }) { term ->
                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPick(term) }
                            .padding(horizontal = 20.dp, vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Schedule,
                            contentDescription = null,
                            tint = HL.Muted.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(term, fontSize = 14.sp, color = HL.Ink, modifier = Modifier.weight(1f))
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Remove",
                            tint = HL.Muted.copy(alpha = 0.5f),
                            modifier = Modifier.size(12.dp).clickable { onRemove(term) }
                        )
                    }
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 52.dp)
                            .height(1.dp)
                            .background(Line.copy(alpha = 0.4f))
                    )
                }
            }
        }
    }
}

// ---- Focused: results ----

@Composable
private fun ResultsOverlay(
    state: SearchUiState,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)) {
        if (state.isSearching) {
            item {
                Box(Modifier.fillMaxWidth().padding(top = 60.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HL.Ink, strokeWidth = 2.dp, modifier = Modifier.size(28.dp))
                }
            }
        } else if (state.searchType == SearchType.Readers) {
            if (state.users.isEmpty()) {
                item { NoResults() }
            } else {
                lazyItems(state.users, key = { it.username }) { user ->
                    UserResultRow(user, onClick = { onOpenProfile(user.username) })
                    HairLine()
                }
            }
        } else {
            val items = state.currentBooks
            if (items.isEmpty()) {
                item { NoResults() }
            } else {
                lazyItems(items, key = { it.id }) { book ->
                    BookResultRow(book, onClick = { onOpenBook(book.id) })
                    HairLine()
                }
            }
        }
    }
}

@Composable
private fun HairLine() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(start = 20.dp)
            .height(1.dp)
            .background(Line.copy(alpha = 0.5f))
    )
}

@Composable
private fun BookResultRow(book: Book, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        BookCoverImage(
            url = book.coverUrl,
            title = book.title,
            modifier = Modifier.width(40.dp).aspectRatio(2f / 3f),
            cornerRadius = 4.dp
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                book.title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.5.sp,
                lineHeight = 18.sp,
                color = HL.Ink,
                maxLines = 2
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    listOfNotNull(
                        book.authorLine.takeIf { it.isNotEmpty() },
                        book.publishedYear
                    ).joinToString(" · "),
                    fontSize = 12.sp,
                    color = HL.Muted,
                    maxLines = 1,
                    modifier = Modifier.weight(1f, fill = false)
                )
                book.averageRating?.let {
                    Text(
                        "★ %.1f".format(it),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = HL.Ink
                    )
                }
            }
            if (book.categories.isNotEmpty()) {
                Text(
                    book.categories.take(2).joinToString(" · "),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.5.sp,
                    letterSpacing = 1.sp,
                    color = HL.Accent.copy(alpha = 0.85f),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun UserResultRow(user: UserProfile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AvatarImage(url = user.avatarUrl, name = user.displayName, size = 44.dp)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                user.displayName,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.5.sp,
                color = HL.Ink,
                maxLines = 1
            )
            Text(
                "@${user.username}",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = HL.Muted,
                maxLines = 1
            )
        }
        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                "${user.booksReadCount}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                color = HL.Ink
            )
            Text(
                "BOOKS",
                fontFamily = FontFamily.Monospace,
                fontSize = 8.5.sp,
                letterSpacing = 1.6.sp,
                color = HL.Muted
            )
        }
    }
}

@Composable
private fun NoResults() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            Icons.Outlined.Search,
            contentDescription = null,
            tint = HL.Muted.copy(alpha = 0.6f),
            modifier = Modifier.size(34.dp)
        )
        Text(
            "Nothing found",
            fontFamily = FontFamily.Serif,
            fontStyle = FontStyle.Italic,
            fontSize = 14.sp,
            color = HL.Muted
        )
    }
}
