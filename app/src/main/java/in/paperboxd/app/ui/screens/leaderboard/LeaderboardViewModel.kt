package `in`.paperboxd.app.ui.screens.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.LeaderboardEntry
import `in`.paperboxd.app.ui.components.CelebrationCenter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Mirrors iOS LeaderboardTab: global/friends use dedicated routes, others a dimension. */
enum class LeaderboardTab(val dimension: String?) {
    Global(null), Books("books"), Pages("pages"), Streak("streak"), Diary("diary"), Friends(null);

    fun value(e: LeaderboardEntry): Int = when (this) {
        Global, Friends -> e.totalXp
        Books -> e.booksRead
        Pages -> e.pagesRead
        Streak -> e.currentStreak
        Diary -> e.diaryEntries
    }

    fun rank(e: LeaderboardEntry): Int? = when (this) {
        Global, Friends -> e.xpRank
        Books -> e.booksRank
        Pages -> e.pagesRank
        Streak -> e.streakRank
        Diary -> e.diaryRank
    }
}

data class LeaderboardUiState(
    val tab: LeaderboardTab = LeaderboardTab.Global,
    val entries: List<LeaderboardEntry> = emptyList(),
    val myStats: LeaderboardEntry? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val myRank: Int? get() = myStats?.let { tab.rank(it) }
}

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val celebrationCenter: CelebrationCenter
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardUiState())
    val state: StateFlow<LeaderboardUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            loadMyStats()
            loadEntries()
        }
    }

    fun onTabSelected(tab: LeaderboardTab) {
        if (_state.value.tab == tab) return
        _state.update { it.copy(tab = tab) }
        viewModelScope.launch { loadEntries() }
    }

    fun refresh() {
        viewModelScope.launch {
            loadMyStats()
            loadEntries()
        }
    }

    private suspend fun loadEntries() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        val result = when (val tab = _state.value.tab) {
            LeaderboardTab.Global -> userRepository.globalLeaderboard()
            LeaderboardTab.Friends -> userRepository.friendsLeaderboard()
            else -> userRepository.leaderboardByDimension(tab.dimension ?: "xp")
        }
        result.fold(
            onSuccess = { resp -> _state.update { it.copy(entries = resp.leaderboard, isLoading = false) } },
            onFailure = { e ->
                _state.update { it.copy(entries = emptyList(), errorMessage = e.message, isLoading = false) }
            }
        )
    }

    private suspend fun loadMyStats() {
        userRepository.myLeaderboardStats().onSuccess { stats ->
            _state.update { it.copy(myStats = stats) }
            // Celebrate when the reader's tier rises past the last one seen.
            celebrationCenter.checkLevel(stats.level)
        }
    }
}
