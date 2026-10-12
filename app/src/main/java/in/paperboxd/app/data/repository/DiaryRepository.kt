package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.DiaryCreateBody
import `in`.paperboxd.app.domain.model.DiaryEntriesResponse
import `in`.paperboxd.app.domain.model.DiaryEntry
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/** Thoughts (the backend's name). `username` is always the thought's author. */
@Singleton
class DiaryRepository @Inject constructor(
    private val api: ApiService
) {
    // In-app events replacing iOS NotificationCenter diaryEntryCreated/Deleted.
    private val _entryCreated = MutableSharedFlow<DiaryEntry>(extraBufferCapacity = 1)
    val entryCreated: SharedFlow<DiaryEntry> = _entryCreated.asSharedFlow()

    private val _entryDeleted = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val entryDeleted: SharedFlow<String> = _entryDeleted.asSharedFlow()

    suspend fun entries(username: String, page: Int, pageSize: Int = 20): Result<DiaryEntriesResponse> =
        safeApiCall { api.userThoughts(username, page, pageSize) }

    suspend fun entry(username: String, thoughtId: String): Result<DiaryEntry> =
        safeApiCall { api.getThought(username, thoughtId) }

    suspend fun thread(username: String, thoughtId: String): Result<List<DiaryEntry>> =
        safeApiCall { api.getThoughtThread(username, thoughtId).thoughts }

    suspend fun create(username: String, body: DiaryCreateBody): Result<DiaryEntry> =
        safeApiCall { api.createThought(username, body) }
            .onSuccess { if (it.threadRootId == null) _entryCreated.tryEmit(it) }

    /** Continues [parent]'s thread with [content]. */
    suspend fun continueThread(parent: DiaryEntry, content: String): Result<DiaryEntry> =
        create(parent.username, DiaryCreateBody(content = content, threadParentId = parent.id))

    suspend fun delete(username: String, thoughtId: String): Result<Unit> =
        safeApiCall { api.deleteThought(username, thoughtId); Unit }
            .onSuccess { _entryDeleted.tryEmit(thoughtId) }

    /** Flips [entry]'s like; the result carries the server's count. */
    suspend fun toggleLike(entry: DiaryEntry): Result<DiaryEntry> =
        setLiked(entry.username, entry.id, !entry.isLiked).map { entry.withLike(!entry.isLiked, it) }
            .recoverAlreadyInState(entry.withLike(!entry.isLiked))

    /** Flips [entry]'s repost; the result carries the server's count. */
    suspend fun toggleRepost(entry: DiaryEntry): Result<DiaryEntry> =
        setReposted(entry.username, entry.id, !entry.isReposted).map { entry.withRepost(!entry.isReposted, it) }
            .recoverAlreadyInState(entry.withRepost(!entry.isReposted))

    /**
     * The backend answers 409 "Already liked/reposted" when a double tap races
     * the first request — the server already holds the state that was asked for.
     */
    private fun Result<DiaryEntry>.recoverAlreadyInState(target: DiaryEntry): Result<DiaryEntry> =
        recoverCatching { e ->
            if (e is ApiError.ValidationError && e.message.startsWith("Already")) target else throw e
        }

    /** Returns the fresh likes count, when the backend sent one. */
    suspend fun setLiked(username: String, thoughtId: String, liked: Boolean): Result<Long?> =
        safeApiCall {
            (if (liked) api.likeThought(username, thoughtId) else api.unlikeThought(username, thoughtId)).likesCount
        }

    /** Returns the fresh reposts count, when the backend sent one. */
    suspend fun setReposted(username: String, thoughtId: String, reposted: Boolean): Result<Long?> =
        safeApiCall {
            (if (reposted) api.repostThought(username, thoughtId) else api.unrepostThought(username, thoughtId)).repostsCount
        }
}
