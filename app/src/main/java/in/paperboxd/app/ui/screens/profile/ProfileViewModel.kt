package `in`.paperboxd.app.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import `in`.paperboxd.app.data.repository.DiaryRepository
import `in`.paperboxd.app.data.repository.UserRepository
import `in`.paperboxd.app.domain.model.AuthorSummary
import `in`.paperboxd.app.domain.model.BookWithStatus
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.domain.model.FavoriteBook
import `in`.paperboxd.app.domain.model.LastLoggedBook
import `in`.paperboxd.app.domain.model.ReadingList
import `in`.paperboxd.app.domain.model.TbrItem
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.domain.model.UserProfile
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class ProfileTab { Bookshelf, Diary, Lists, Tbr, Authors }

data class ProfileUiState(
    val profile: UserProfile? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedTab: ProfileTab = ProfileTab.Bookshelf,
    val shelfBooks: List<BookWithStatus> = emptyList(),
    val shelfTotal: Int? = null,
    val diaryEntries: List<DiaryEntry> = emptyList(),
    val ownLists: List<ReadingList> = emptyList(),
    val savedLists: List<ReadingList> = emptyList(),
    val tbrItems: List<TbrItem> = emptyList(),
    val authors: List<AuthorSummary> = emptyList(),
    val favoriteBooks: List<FavoriteBook> = emptyList(),
    val lastLoggedBook: LastLoggedBook? = null,
    val streak: Int? = null,
    val isFollowLoading: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val diaryRepository: DiaryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    private val _toast = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val toast: SharedFlow<String> = _toast.asSharedFlow()

    private var profileUsername: String = ""
    private var viewer: User? = null

    private var shelfPage = 1
    private var shelfHasMore = true
    private var isLoadingShelf = false
    private var diaryPage = 1
    private var diaryHasMore = true
    private var isLoadingDiary = false

    val isOwnProfile: Boolean
        get() = viewer?.username?.lowercase() == profileUsername.lowercase()

    fun start(username: String, viewerUser: User) {
        if (profileUsername == username) return
        profileUsername = username
        viewer = viewerUser
        fetchAll()
    }

    fun fetchAll() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            shelfPage = 1; shelfHasMore = true
            diaryPage = 1; diaryHasMore = true

            val profileTask = async { userRepository.profile(profileUsername) }
            val lastBookTask = async { userRepository.lastLoggedBook(profileUsername).getOrNull()?.lastBook }
            val favoritesTask = async { userRepository.favorites(profileUsername).getOrNull().orEmpty() }
            val streakTask = async { userRepository.streak(profileUsername).getOrNull()?.streak }

            profileTask.await().fold(
                onSuccess = { p -> _state.update { it.copy(profile = p) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            _state.update {
                it.copy(
                    lastLoggedBook = lastBookTask.await(),
                    favoriteBooks = favoritesTask.await(),
                    streak = streakTask.await(),
                    shelfBooks = emptyList(),
                    diaryEntries = emptyList(),
                    isLoading = false
                )
            }
            fetchShelf()
        }
    }

    // MARK: - Tabs

    fun onTabSelected(tab: ProfileTab) {
        _state.update { it.copy(selectedTab = tab) }
        viewModelScope.launch {
            when (tab) {
                ProfileTab.Bookshelf -> if (_state.value.shelfBooks.isEmpty()) fetchShelf()
                ProfileTab.Diary -> if (_state.value.diaryEntries.isEmpty()) fetchDiary()
                ProfileTab.Lists -> fetchLists()
                ProfileTab.Tbr -> fetchTbr()
                ProfileTab.Authors -> fetchAuthors()
            }
        }
    }

    fun fetchShelf() {
        if (!shelfHasMore || isLoadingShelf) return
        isLoadingShelf = true
        viewModelScope.launch {
            userRepository.bookshelf(profileUsername, status = "shelf", page = shelfPage, pageSize = 20)
                .onSuccess { resp ->
                    _state.update {
                        it.copy(
                            shelfBooks = it.shelfBooks + resp.books,
                            shelfTotal = resp.totalCount.toInt()
                        )
                    }
                    shelfPage += 1
                    shelfHasMore = resp.books.size == 20
                }
            isLoadingShelf = false
        }
    }

    fun fetchShelfIfNeeded(item: BookWithStatus) {
        if (_state.value.shelfBooks.lastOrNull()?.id == item.id) fetchShelf()
    }

    fun fetchDiary() {
        if (!diaryHasMore || isLoadingDiary) return
        isLoadingDiary = true
        viewModelScope.launch {
            diaryRepository.entries(profileUsername, page = diaryPage, pageSize = 20)
                .onSuccess { resp ->
                    _state.update { it.copy(diaryEntries = it.diaryEntries + resp.entries) }
                    diaryPage += 1
                    diaryHasMore = resp.entries.size == 20
                }
            isLoadingDiary = false
        }
    }

    fun fetchDiaryIfNeeded(item: DiaryEntry) {
        if (_state.value.diaryEntries.lastOrNull()?.id == item.id) fetchDiary()
    }

    private suspend fun fetchLists() {
        if (_state.value.ownLists.isNotEmpty() || _state.value.savedLists.isNotEmpty()) return
        userRepository.lists(profileUsername).onSuccess { resp ->
            _state.update { it.copy(ownLists = resp.ownLists, savedLists = resp.savedLists) }
        }
    }

    private suspend fun fetchTbr() {
        if (_state.value.tbrItems.isNotEmpty()) return
        userRepository.tbr(profileUsername).onSuccess { items ->
            _state.update { it.copy(tbrItems = items) }
        }
    }

    private suspend fun fetchAuthors() {
        if (_state.value.authors.isNotEmpty()) return
        userRepository.authors(profileUsername).onSuccess { items ->
            _state.update { it.copy(authors = items) }
        }
    }

    // MARK: - Follow

    fun toggleFollow() {
        val profile = _state.value.profile ?: return
        if (isOwnProfile) return
        val following = profile.isFollowing ?: false
        viewModelScope.launch {
            _state.update { it.copy(isFollowLoading = true) }
            val result = if (following) userRepository.unfollow(profileUsername)
            else userRepository.follow(profileUsername)
            result.fold(
                onSuccess = { resp ->
                    _state.update {
                        it.copy(
                            profile = profile.copy(
                                isFollowing = resp.isFollowing,
                                followersCount = resp.followersCount
                            )
                        )
                    }
                },
                onFailure = { e -> _toast.tryEmit(e.message ?: "Couldn't update follow") }
            )
            _state.update { it.copy(isFollowLoading = false) }
        }
    }

    // MARK: - Banner upload

    fun uploadBanner(bytes: ByteArray) {
        viewModelScope.launch {
            userRepository.uploadBanner(bytes).fold(
                onSuccess = {
                    fetchAll()
                    _toast.tryEmit("Banner updated")
                },
                onFailure = { e -> _toast.tryEmit(e.message ?: "Couldn't update banner") }
            )
        }
    }
}
