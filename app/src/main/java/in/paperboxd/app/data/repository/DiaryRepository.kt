package `in`.paperboxd.app.data.repository

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
        safeApiCall { api.userDiary(username, page, pageSize) }

    suspend fun entry(username: String, entryId: String): Result<DiaryEntry> =
        safeApiCall { api.getDiaryEntry(username, entryId) }

    suspend fun create(username: String, body: DiaryCreateBody): Result<DiaryEntry> =
        safeApiCall { api.createDiaryEntry(username, body) }
            .onSuccess { _entryCreated.tryEmit(it) }

    suspend fun delete(username: String, entryId: String): Result<Unit> =
        safeApiCall { api.deleteDiaryEntry(username, entryId); Unit }
            .onSuccess { _entryDeleted.tryEmit(entryId) }

    suspend fun like(username: String, entryId: String): Result<Unit> =
        safeApiCall { api.likeDiaryEntry(username, entryId); Unit }

    suspend fun unlike(username: String, entryId: String): Result<Unit> =
        safeApiCall { api.unlikeDiaryEntry(username, entryId); Unit }
}
