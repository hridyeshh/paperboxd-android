package `in`.paperboxd.app.ui.screens.search

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.RecommendationRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.UserProfile
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class SearchType { Books, Readers, Vibe }

data class SearchUiState(
    val query: String = "",
    val searchType: SearchType = SearchType.Books,
    val books: List<Book> = emptyList(),
    val users: List<UserProfile> = emptyList(),
    val vibeBooks: List<Book> = emptyList(),
    val isSearching: Boolean = false,
    val errorMessage: String? = null,
    val wallBooks: List<Book> = emptyList(),
    val isLoadingWall: Boolean = false,
    val isLoadingMoreWall: Boolean = false,
    val suggestedReaders: List<UserProfile> = emptyList(),
    val recommendations: List<RecommendationItem> = emptyList(),
    val recentSearches: List<String> = emptyList()
) {
    val currentBooks: List<Book>
        get() = when (searchType) {
            SearchType.Books -> books
            SearchType.Vibe -> vibeBooks
            SearchType.Readers -> emptyList()
        }
    val hasQuery: Boolean get() = query.trim().isNotEmpty()
}

@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository,
    private val recommendationRepository: RecommendationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchUiState())
    val state: StateFlow<SearchUiState> = _state.asStateFlow()

    private val prefs = context.getSharedPreferences("pb_search", Context.MODE_PRIVATE)
    private val gson = Gson()

    private var searchJob: Job? = null
    private var wallPage = 1
    private var wallHasMore = true
    private var suggestedLoaded = false

    init {
        _state.update { it.copy(recentSearches = loadHistory()) }
        loadWallIfNeeded()
    }

    // MARK: - Query / type

    fun onQueryChanged(raw: String) {
        _state.update { it.copy(query = raw) }
        searchJob?.cancel()
        val q = raw.trim()
        if (q.isEmpty()) {
            _state.update { it.copy(books = emptyList(), users = emptyList(), vibeBooks = emptyList(), errorMessage = null) }
            return
        }
        val debounce = if (_state.value.searchType == SearchType.Vibe) 600L else 400L
        searchJob = viewModelScope.launch {
            delay(debounce)
            search(q)
        }
    }

    fun onTypeChanged(type: SearchType) {
        searchJob?.cancel()
        _state.update {
            it.copy(searchType = type, books = emptyList(), users = emptyList(), vibeBooks = emptyList(), errorMessage = null)
        }
        val q = _state.value.query.trim()
        if (q.isNotEmpty()) {
            searchJob = viewModelScope.launch { search(q) }
        }
        if (type == SearchType.Readers) loadSuggestedReaders()
    }

    private suspend fun search(q: String) {
        _state.update { it.copy(isSearching = true, errorMessage = null) }
        when (_state.value.searchType) {
            SearchType.Books -> {
                val items = bookRepository.searchBooks(q, pageSize = 20).getOrNull()?.items.orEmpty()
                _state.update { it.copy(books = items) }
                if (items.isNotEmpty()) addToHistory(q)
            }
            SearchType.Readers -> {
                val users = userRepository.searchUsers(q, pageSize = 20).getOrNull()?.users.orEmpty()
                _state.update { it.copy(users = users) }
                if (users.isNotEmpty()) addToHistory(q)
            }
            SearchType.Vibe -> {
                val items = bookRepository.vibeSearch(q, limit = 10).getOrNull()?.items.orEmpty()
                _state.update { it.copy(vibeBooks = items) }
                if (items.isNotEmpty()) addToHistory(q)
            }
        }
        _state.update { it.copy(isSearching = false) }
    }

    // MARK: - Trending wall

    fun loadWallIfNeeded() {
        if (_state.value.wallBooks.isNotEmpty() || _state.value.isLoadingWall) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingWall = true) }
            val items = bookRepository.latestBooks(page = 1, pageSize = 24).getOrNull()?.items.orEmpty()
            wallPage = 1
            wallHasMore = items.size == 24
            _state.update { it.copy(wallBooks = items, isLoadingWall = false) }
        }
    }

    fun loadMoreWall() {
        if (!wallHasMore || _state.value.isLoadingMoreWall || _state.value.isLoadingWall) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingMoreWall = true) }
            val next = wallPage + 1
            val items = bookRepository.latestBooks(page = next, pageSize = 24).getOrNull()?.items.orEmpty()
            wallPage = next
            wallHasMore = items.size == 24
            _state.update { it.copy(wallBooks = it.wallBooks + items, isLoadingMoreWall = false) }
        }
    }

    /** Pull-to-refresh: `/books/random` returns a fresh random slice server-side. */
    fun shuffleWall() {
        if (_state.value.isLoadingWall) return
        viewModelScope.launch {
            _state.update { it.copy(isLoadingWall = true) }
            val items = bookRepository.randomBooks(pageSize = 24).getOrNull()?.items.orEmpty()
            if (items.isNotEmpty()) {
                wallPage = 1
                wallHasMore = false // random feed isn't paginated
                _state.update { it.copy(wallBooks = items) }
            }
            _state.update { it.copy(isLoadingWall = false) }
        }
    }

    /** "Picked for you" rail above the wall — iOS loadRecommendationsIfNeeded twin. */
    fun loadRecommendationsIfNeeded() {
        if (recsLoaded) return
        recsLoaded = true
        viewModelScope.launch {
            val recs = recommendationRepository.home().getOrNull()?.recommendations.orEmpty()
            _state.update { it.copy(recommendations = recs.take(8)) }
        }
    }

    private var recsLoaded = false

    private fun loadSuggestedReaders() {
        if (suggestedLoaded) return
        suggestedLoaded = true
        viewModelScope.launch {
            val users = userRepository.searchUsers("reader", pageSize = 8).getOrNull()?.users.orEmpty()
            _state.update { it.copy(suggestedReaders = users) }
        }
    }

    // MARK: - History

    fun addToHistory(term: String) {
        val t = term.trim()
        if (t.isEmpty()) return
        val next = (listOf(t) + _state.value.recentSearches.filter { it != t }).take(10)
        _state.update { it.copy(recentSearches = next) }
        prefs.edit().putString(HISTORY_KEY, gson.toJson(next)).apply()
    }

    fun removeFromHistory(term: String) {
        val next = _state.value.recentSearches.filter { it != term }
        _state.update { it.copy(recentSearches = next) }
        prefs.edit().putString(HISTORY_KEY, gson.toJson(next)).apply()
    }

    private fun loadHistory(): List<String> {
        val json = prefs.getString(HISTORY_KEY, null) ?: return emptyList()
        return runCatching {
            gson.fromJson<List<String>>(json, object : TypeToken<List<String>>() {}.type)
        }.getOrNull().orEmpty()
    }

    private companion object {
        const val HISTORY_KEY = "pb_search_history"
    }
}
