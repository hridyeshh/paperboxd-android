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
import `in`.paperboxd.app.domain.model.ReadingActivity
import `in`.paperboxd.app.domain.model.ReadingList
import `in`.paperboxd.app.domain.model.CreateListRequest
import `in`.paperboxd.app.domain.model.ListWithBooksResponse
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
    val isRefreshing: Boolean = false,
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
    val isFollowLoading: Boolean = false,
    val activity: ReadingActivity? = null,
    val activityYear: Int = java.time.Year.now().value
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

    // Shelf — the profile bookshelf tab shows 'read' + 'reading' merged
    // client-side (reading first, deduped) so a freshly added book shows up.
    // Backend has no "shelf" status; valid ones are read / reading / to-read.
    private var shelfPage = 1
    private var shelfHasMore = true
    private var isLoadingShelf = false
    private var didLoadReading = false
    private var readingBooks: List<BookWithStatus> = emptyList()
    private var readBooks: List<BookWithStatus> = emptyList()
    private var readTotal = 0
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

    /** Pull-to-refresh: re-run fetchAll keeping current content on screen (spinner, not full-screen loader). */
    fun refresh() = fetchAll(refreshing = true)

    fun fetchAll(refreshing: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = !refreshing, isRefreshing = refreshing, errorMessage = null) }
            shelfPage = 1; shelfHasMore = true; didLoadReading = false
            readingBooks = emptyList(); readBooks = emptyList(); readTotal = 0
            diaryPage = 1; diaryHasMore = true

            val profileTask = async { userRepository.profile(profileUsername) }
            val lastBookTask = async { userRepository.lastLoggedBook(profileUsername).getOrNull()?.lastBook }
            val favoritesTask = async { userRepository.favorites(profileUsername).getOrNull().orEmpty() }
            val streakTask = async { userRepository.streak(profileUsername).getOrNull()?.streak }
            val activityTask = async {
                userRepository.readingActivity(profileUsername, _state.value.activityYear).getOrNull()
            }

            profileTask.await().fold(
                onSuccess = { p -> _state.update { it.copy(profile = p) } },
                onFailure = { e -> _state.update { it.copy(errorMessage = e.message) } }
            )
            _state.update {
                it.copy(
                    lastLoggedBook = lastBookTask.await(),
                    favoriteBooks = favoritesTask.await(),
                    streak = streakTask.await(),
                    activity = activityTask.await(),
                    shelfBooks = emptyList(),
                    diaryEntries = emptyList(),
                    isLoading = false,
                    isRefreshing = false
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
        if ((!shelfHasMore && didLoadReading) || isLoadingShelf) return
        isLoadingShelf = true
        viewModelScope.launch {
            // Currently-reading books have no finished_at and never come back
            // under status=read, so fetch them once and pin them to the top.
            if (!didLoadReading) {
                didLoadReading = true
                userRepository.bookshelf(profileUsername, status = "reading", page = 1, pageSize = 40)
                    .onSuccess { readingBooks = it.books }
            }
            if (shelfHasMore) {
                userRepository.bookshelf(profileUsername, status = "read", page = shelfPage, pageSize = 20)
                    .onSuccess { resp ->
                        readBooks = readBooks + resp.books
                        readTotal = resp.totalCount.toInt()
                        shelfPage += 1
                        shelfHasMore = resp.books.size == 20
                    }
            }
            rebuildShelf()
            isLoadingShelf = false
        }
    }

    /** Reading books first, then read books, de-duplicated by id — iOS twin. */
    private fun rebuildShelf() {
        val seen = HashSet<String>()
        val merged = (readingBooks + readBooks).filter { seen.add(it.id) }
        _state.update {
            it.copy(shelfBooks = merged, shelfTotal = readingBooks.size + readTotal)
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

    /** Creates a list on the current profile. Returns true on success and prepends
     *  the new list to the "Made" rail so the tab updates without a refetch. */
    suspend fun createList(title: String, description: String?, isPrivate: Boolean): Boolean =
        userRepository.createList(profileUsername, CreateListRequest(title, description, isPrivate))
            .map { created ->
                _state.update { it.copy(ownLists = listOf(created) + it.ownLists) }
                true
            }.getOrDefault(false)

    suspend fun loadListDetail(listId: String): ListWithBooksResponse? =
        userRepository.listDetail(profileUsername, listId).getOrNull()

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

    /** Switches the heatmap year and reloads just the activity payload — iOS twin. */
    fun selectActivityYear(year: Int) {
        if (year == _state.value.activityYear) return
        _state.update { it.copy(activityYear = year) }
        viewModelScope.launch {
            val act = userRepository.readingActivity(profileUsername, year).getOrNull()
            _state.update { it.copy(activity = act) }
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

    // MARK: - Moderation

    /** Blocks the profile user, then invokes [onBlocked] (e.g. to pop back). */
    fun blockUser(onBlocked: () -> Unit) {
        if (isOwnProfile) return
        viewModelScope.launch {
            userRepository.block(profileUsername).fold(
                onSuccess = {
                    _toast.tryEmit("Blocked @$profileUsername")
                    onBlocked()
                },
                onFailure = { e -> _toast.tryEmit(e.message ?: "Couldn't block") }
            )
        }
    }

    fun reportUser(reason: String) {
        viewModelScope.launch {
            userRepository.report("user", profileUsername, reason).fold(
                onSuccess = { _toast.tryEmit("Report received — we review within 24 hours") },
                onFailure = { e -> _toast.tryEmit(e.message ?: "Couldn't send report") }
            )
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
