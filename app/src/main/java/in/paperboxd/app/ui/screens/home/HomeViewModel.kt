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
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.LastLoggedBook
import `in`.paperboxd.app.domain.model.RecommendationItem
import `in`.paperboxd.app.domain.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

data class HomeUiState(
    val recommendations: List<RecommendationItem> = emptyList(),
    val lastLoggedBook: LastLoggedBook? = null,
    val latestBooks: List<Book> = emptyList(),
    val friendsActivities: List<ActivityItem> = emptyList(),
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val errorMessage: String? = null,
    val hasNewActivities: Boolean = false
) {
    val pickedForYou: List<RecommendationItem> get() = recommendations.take(6)
    val freshShelves: List<Book> get() = latestBooks.take(8)
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

    var user: User? = null
        set(value) {
            val wasNull = field == null
            field = value
            if (wasNull && value != null) load()
        }

    private fun lastViewedKey() = "activity_last_viewed_${user?.username ?: user?.id}"

    fun load(refreshing: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = !refreshing, isRefreshing = refreshing, errorMessage = null) }

            val recsTask = async { recommendationRepository.home() }
            val lastBookTask = async {
                user?.username?.let { userRepository.lastLoggedBook(it).getOrNull()?.lastBook }
            }
            val latestTask = async { bookRepository.latestBooks(pageSize = 12).getOrNull()?.items.orEmpty() }
            val activitiesTask = async {
                recommendationRepository.followingActivities(pageSize = 10).getOrNull()?.activities.orEmpty()
            }

            recsTask.await().fold(
                onSuccess = { resp -> _state.update { it.copy(recommendations = resp.recommendations) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            val activities = activitiesTask.await()
            _state.update {
                it.copy(
                    lastLoggedBook = lastBookTask.await(),
                    latestBooks = latestTask.await(),
                    friendsActivities = activities,
                    hasNewActivities = computeUnread(activities),
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
        _state.update { it.copy(hasNewActivities = false) }
    }

    fun trackImpression(bookId: String) {
        recommendationRepository.trackImpression(bookId, source = "home_feed")
    }

    private fun parseInstant(iso: String): Instant? =
        runCatching { Instant.parse(iso) }.getOrNull()
}
