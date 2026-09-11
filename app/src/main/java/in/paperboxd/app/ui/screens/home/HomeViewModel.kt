package `in`.paperboxd.app.ui.screens.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import `in`.paperboxd.app.data.repository.BookRepository
import `in`.paperboxd.app.data.repository.RecommendationRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.ActivityItem
import `in`.paperboxd.app.domain.model.FollowRequestUser
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.CommunityShelfBook
import `in`.paperboxd.app.domain.model.FeedModule
import `in`.paperboxd.app.domain.model.LastLoggedBook
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.util.TimeZone
import javax.inject.Inject

data class HomeUiState(
    val recommendations: List<RecommendationItem> = emptyList(),
    /**
     * Server-assembled modules, each with its own title and reason. Rendered
     * above the legacy rails; books in them are removed from those rails so
     * nothing appears twice.
     */
    val feedModules: List<FeedModule> = emptyList(),
    /** "Good evening." — written server-side in the reader's time zone. */
    val greeting: String? = null,
    val lastLoggedBook: LastLoggedBook? = null,
    val latestBooks: List<Book> = emptyList(),
    val friendsActivities: List<ActivityItem> = emptyList(),
    val trendingBooks: List<Book> = emptyList(),
    val risingBooks: List<CommunityShelfBook> = emptyList(),
    val mostTbrBooks: List<CommunityShelfBook> = emptyList(),
    val communityActivities: List<ActivityItem> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val hasNewActivities: Boolean = false,
    val followRequests: List<FollowRequestUser> = emptyList()
) {
    /** Ids already shown inside a feed module. */
    private val usedIds: Set<String> get() = feedModules.flatMap { m -> m.books.map { it.id } }.toSet()

    val pickedForYou: List<RecommendationItem> get() = recommendations.filterNot { it.id in usedIds }.take(6)
    val freshShelves: List<Book> get() = latestBooks.filterNot { it.id in usedIds }.take(8)

    /** Needs a few titles to read as a trend rather than one person's week. */
    val trending: List<Book>
        get() = if (trendingBooks.size >= 4) trendingBooks.filterNot { it.id in usedIds }.take(10) else emptyList()

    /**
     * Same threshold for the discovery shelves; the server's label rides along
     * as the card's reason line so the claim stays the server's.
     */
    val rising: List<RecommendationItem> get() = shelf(risingBooks)
    val mostTbr: List<RecommendationItem> get() = shelf(mostTbrBooks)

    private fun shelf(books: List<CommunityShelfBook>): List<RecommendationItem> =
        if (books.size < 4) emptyList()
        else books.filterNot { it.id in usedIds }.take(10).map {
            RecommendationItem(
                id = it.id, title = it.title, authors = it.authors,
                coverUrl = it.coverUrl, reason = it.label
            )
        }

    /** Public activity fills the rail while the follow graph is empty. */
    val showsCommunityRail: Boolean get() = friendsActivities.isEmpty() && communityActivities.isNotEmpty()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    @ApplicationContext context: Context,
    private val recommendationRepository: RecommendationRepository,
    private val bookRepository: BookRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    // Mirrors the web/iOS "activity_last_viewed" unread-dot marker.
    private val prefs = context.getSharedPreferences("pb_home", Context.MODE_PRIVATE)

    // Backs the "Between covers." card popup. The activity payload has only the
    // book's id and title, so cover/author/rating are fetched when one is opened.
    private val _previewBook = MutableStateFlow<Book?>(null)
    val previewBook: StateFlow<Book?> = _previewBook.asStateFlow()
    private var previewJob: Job? = null

    fun loadPreview(bookId: String?) {
        previewJob?.cancel()
        _previewBook.value = null
        if (bookId == null) return
        previewJob = viewModelScope.launch {
            _previewBook.value = bookRepository.book(bookId).getOrNull()
        }
    }

    var user: User? = null
        set(value) {
            val wasNull = field == null
            field = value
            if (wasNull && value != null) load()
        }

    private fun lastViewedKey() = "activity_last_viewed_${user?.username ?: user?.id}"

    fun respondToFollowRequest(username: String, accept: Boolean) {
        viewModelScope.launch {
            val result = if (accept) {
                userRepository.acceptFollowRequest(username).map { }
            } else {
                userRepository.rejectFollowRequest(username)
            }
            result.onSuccess {
                _state.update { state ->
                    state.copy(followRequests = state.followRequests.filterNot { it.username == username })
                }
            }
        }
    }

    fun load(refreshing: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = !refreshing, isRefreshing = refreshing, errorMessage = null) }

            // Pending follow requests ride the same load: they belong in the bell
            // beside the feed, and they are the only item there that needs an answer.
            val followRequestsTask = async {
                userRepository.followRequests().getOrNull()?.requests.orEmpty()
            }
            val recsTask = async { recommendationRepository.home() }
            // Best-effort: a missing feed leaves the legacy rails, never a blank home.
            val feedTask = async { recommendationRepository.feed(TimeZone.getDefault().id).getOrNull() }
            val lastBookTask = async {
                user?.username?.let { userRepository.lastLoggedBook(it).getOrNull()?.lastBook }
            }
            val latestTask = async { bookRepository.latestBooks(pageSize = 12).getOrNull()?.items.orEmpty() }
            val communityTask = async { recommendationRepository.community().getOrNull() }
            val activitiesTask = async {
                // Friends feed, never your own actions — iOS fetchFriendActivities twin.
                recommendationRepository.followingActivities(pageSize = 10).getOrNull()
                    ?.activities.orEmpty().filter { it.userId != user?.id }
            }

            recsTask.await().fold(
                onSuccess = { resp -> _state.update { it.copy(recommendations = resp.recommendations) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            val activities = activitiesTask.await()
            val community = communityTask.await()
            val feed = feedTask.await()
            _state.update {
                it.copy(
                    feedModules = feed?.modules.orEmpty().filter { m -> m.books.isNotEmpty() },
                    greeting = feed?.greeting ?: it.greeting,
                    lastLoggedBook = lastBookTask.await(),
                    latestBooks = latestTask.await(),
                    friendsActivities = activities,
                    trendingBooks = community?.trendingBooks.orEmpty().filter { b -> b.coverUrl != null },
                    risingBooks = community?.rising.orEmpty().filter { b -> b.coverUrl != null },
                    mostTbrBooks = community?.mostTbr.orEmpty().filter { b -> b.coverUrl != null },
                    communityActivities = community?.activity.orEmpty()
                        .filter { a -> a.userId != user?.id && a.bookTitle != null },
                    followRequests = followRequestsTask.await(),
                    hasNewActivities = computeUnread(activities) || followRequestsTask.await().isNotEmpty(),
                    isLoading = false,
                    isRefreshing = false
                )
            }
        }
    }

    private fun computeUnread(activities: List<ActivityItem>): Boolean {
        val newest = activities.mapNotNull { parseInstant(it.createdAt) }.maxOrNull() ?: return false
        val lastViewed = prefs.getLong(lastViewedKey(), 0L)
        return newest.toEpochMilli() > lastViewed
    }

    fun markActivitiesViewed() {
        prefs.edit().putLong(lastViewedKey(), System.currentTimeMillis()).apply()
        _state.update { it.copy(hasNewActivities = it.followRequests.isNotEmpty()) }
    }

    /**
     * Records a verdict and removes the card from every rail. Removal is the
     * confirmation — a card that stays after "not for me" reads as the button
     * doing nothing. "loved" keeps it: that is a reason to leave the book in
     * front of them, not to take it away.
     */
    fun giveVerdict(item: RecommendationItem, verdict: String) {
        if (verdict != "loved") {
            _state.update { st ->
                st.copy(
                    recommendations = st.recommendations.filterNot { it.id == item.id }
                )
            }
        }
        recommendationRepository.giveVerdict(item.id, verdict, item.reasonType)
    }

    fun trackImpression(bookId: String) {
        recommendationRepository.trackImpression(bookId, source = "home_feed")
    }

    private fun parseInstant(iso: String): Instant? =
        runCatching { Instant.parse(iso) }.getOrNull()
}
