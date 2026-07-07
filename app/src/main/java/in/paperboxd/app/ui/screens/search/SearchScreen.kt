package `in`.paperboxd.app.ui.screens.search

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Search
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.UserProfile
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.ShimmerBox
import `in`.paperboxd.app.ui.theme.Accent
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import androidx.compose.ui.tooling.preview.Preview
import `in`.paperboxd.app.ui.theme.PaperBoxdTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadWallIfNeeded() }

    SearchContent(
        state = state,
        onQueryChange = viewModel::onQueryChanged,
        onTypeSelect = viewModel::onTypeChanged,
        onShuffleWall = viewModel::shuffleWall,
        onLoadMoreWall = viewModel::loadMoreWall,
        onRemoveFromHistory = viewModel::removeFromHistory,
        onOpenBook = onOpenBook,
        onOpenProfile = onOpenProfile
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchContent(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onTypeSelect: (SearchType) -> Unit,
    onShuffleWall: () -> Unit,
    onLoadMoreWall: () -> Unit,
    onRemoveFromHistory: (String) -> Unit,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .statusBarsPadding()
    ) {
        SearchField(
            query = state.query,
            onQueryChange = onQueryChange,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
        )
        TypeTabs(selected = state.searchType, onSelect = onTypeSelect)
        Spacer(Modifier.height(8.dp))

        when {
            state.isSearching -> ShimmerRows()
            state.hasQuery -> ResultsList(state, onOpenBook, onOpenProfile)
            state.searchType == SearchType.Readers -> ReadersIdle(
                state = state,
                onOpenProfile = onOpenProfile,
                onTermClick = onQueryChange,
                onRemoveFromHistory = onRemoveFromHistory
            )
            else -> TrendingWall(
                state = state,
                onShuffleWall = onShuffleWall,
                onLoadMoreWall = onLoadMoreWall,
                onOpenBook = onOpenBook
            )
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    stringResource(R.string.search_hint),
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(color = TextPrimary),
                cursorBrush = SolidColor(Accent),
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (query.isNotEmpty()) {
            IconButton(onClick = { onQueryChange("") }, modifier = Modifier.size(22.dp)) {
                Icon(Icons.Outlined.Close, contentDescription = null, tint = TextSecondary)
            }
        }
    }
}

@Composable
private fun TypeTabs(selected: SearchType, onSelect: (SearchType) -> Unit) {
    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SearchType.entries.forEach { type ->
            val active = type == selected
            Text(
                text = when (type) {
                    SearchType.Books -> stringResource(R.string.search_books)
                    SearchType.Readers -> stringResource(R.string.search_readers)
                    SearchType.Vibe -> stringResource(R.string.search_vibe)
                },
                style = MaterialTheme.typography.labelMedium,
                color = if (active) Background else TextSecondary,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (active) Accent else Surface)
                    .clickable { onSelect(type) }
                    .padding(horizontal = 14.dp, vertical = 7.dp)
            )
        }
    }
}

@Composable
private fun ResultsList(
    state: SearchUiState,
    onOpenBook: (String) -> Unit,
    onOpenProfile: (String) -> Unit
) {
    if (state.searchType == SearchType.Readers) {
        if (state.users.isEmpty()) {
            EmptyState(stringResource(R.string.search_no_results))
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 110.dp)) {
                items(state.users, key = { it.id }) { user ->
                    UserRow(user, onClick = { onOpenProfile(user.username) })
                }
            }
        }
    } else {
        if (state.currentBooks.isEmpty()) {
            EmptyState(stringResource(R.string.search_no_results))
        } else {
            LazyColumn(contentPadding = PaddingValues(bottom = 110.dp)) {
                items(state.currentBooks, key = { it.id }) { book ->
                    BookRow(book, onClick = { onOpenBook(book.id) })
                }
            }
        }
    }
}

@Composable
private fun BookRow(book: Book, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BookCoverImage(
            url = book.coverUrl,
            title = book.title,
            modifier = Modifier.width(44.dp).aspectRatio(2f / 3f),
            cornerRadius = 4.dp
        )
        Spacer(Modifier.width(12.dp))
        Column {
            Text(book.title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1)
            val sub = listOfNotNull(book.authorLine.ifEmpty { null }, book.publishedYear).joinToString(" · ")
            Text(sub, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
private fun UserRow(user: UserProfile, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarImage(url = user.avatarUrl, name = user.displayName, size = 44.dp)
        Spacer(Modifier.width(12.dp))
        Column {
            Text(user.displayName, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1)
            Text("@${user.username}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
        }
    }
}

@Composable
private fun ReadersIdle(
    state: SearchUiState,
    onOpenProfile: (String) -> Unit,
    onTermClick: (String) -> Unit,
    onRemoveFromHistory: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 110.dp)) {
        if (state.recentSearches.isNotEmpty()) {
            items(state.recentSearches, key = { "h_$it" }) { term ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTermClick(term) }
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.History, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(term, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemoveFromHistory(term) }, modifier = Modifier.size(20.dp)) {
                        Icon(Icons.Outlined.Close, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
        }
        if (state.suggestedReaders.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.search_suggested),
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }
            items(state.suggestedReaders, key = { it.id }) { user ->
                UserRow(user, onClick = { onOpenProfile(user.username) })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TrendingWall(
    state: SearchUiState,
    onShuffleWall: () -> Unit,
    onLoadMoreWall: () -> Unit,
    onOpenBook: (String) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = state.isLoadingWall,
        onRefresh = onShuffleWall
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, bottom = 110.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(state.wallBooks, key = { _, b -> b.id }) { index, book ->
                if (index >= state.wallBooks.size - 4) {
                    LaunchedEffect(book.id) { onLoadMoreWall() }
                }
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
}

@Composable
private fun ShimmerRows() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        repeat(6) {
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                ShimmerBox(modifier = Modifier.width(44.dp).aspectRatio(2f / 3f).clip(RoundedCornerShape(4.dp)))
                Spacer(Modifier.width(12.dp))
                Column {
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.7f).height(16.dp).clip(RoundedCornerShape(3.dp)))
                    Spacer(Modifier.height(6.dp))
                    ShimmerBox(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp).clip(RoundedCornerShape(3.dp)))
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
    }
}

@Preview
@Composable
private fun SearchPreview() {
    PaperBoxdTheme {
        SearchContent(
            state = SearchUiState(searchType = SearchType.Books),
            onQueryChange = {},
            onTypeSelect = {},
            onShuffleWall = {},
            onLoadMoreWall = {},
            onRemoveFromHistory = {},
            onOpenBook = {},
            onOpenProfile = {}
        )
    }
}
