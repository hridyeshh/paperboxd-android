package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiError
import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.AttachmentRef
import `in`.paperboxd.app.domain.model.ConversationList
import `in`.paperboxd.app.domain.model.ConversationPage
import `in`.paperboxd.app.domain.model.ConversationWith
import `in`.paperboxd.app.domain.model.GifList
import `in`.paperboxd.app.domain.model.MediaUpload
import `in`.paperboxd.app.domain.model.InboxBadge
import `in`.paperboxd.app.domain.model.SendMessageRequest
import `in`.paperboxd.app.domain.model.SendResult
import `in`.paperboxd.app.domain.model.SharedList
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Messages (DMs). No push yet: screens poll — see MOBILE_API.md §3.13. */
@Singleton
class MessageRepository @Inject constructor(
    private val api: ApiService
) {
    /** box = "accepted" (inbox) or "request". */
    suspend fun conversations(box: String): Result<ConversationList> = safeApiCall { api.conversations(box) }

    suspend fun badge(): Result<InboxBadge> = safeApiCall { api.inboxBadge() }

    suspend fun conversationWith(username: String): Result<ConversationWith> =
        safeApiCall { api.conversationWith(username) }

    suspend fun messages(id: String, before: Long? = null, after: Long? = null): Result<ConversationPage> =
        safeApiCall { api.conversationMessages(id, before, after) }

    suspend fun shared(id: String, kind: String? = null): Result<SharedList> =
        safeApiCall { api.conversationShared(id, kind) }

    /**
     * Sends to a conversation, or to up to five usernames when [conversationId]
     * is null. [clientId] makes a retry return the first send instead of a twin.
     */
    suspend fun send(
        conversationId: String?,
        to: List<String>? = null,
        body: String,
        attachments: List<AttachmentRef> = emptyList(),
        clientId: String = UUID.randomUUID().toString()
    ): Result<SendResult> = safeApiCall {
        api.sendMessage(SendMessageRequest(conversationId, to, body, attachments, clientId))
    }

    /** [kind] = photo | video; [body] is the file (a JPEG or the picked video). */
    suspend fun uploadMedia(kind: String, fileName: String, body: RequestBody): Result<MediaUpload> = safeApiCall {
        api.uploadMessageMedia(kind.toRequestBody(), MultipartBody.Part.createFormData("file", fileName, body))
    }

    suspend fun gifs(type: String, query: String): Result<GifList> = safeApiCall { api.gifs(type, query) }

    suspend fun unsend(messageId: Long): Result<Unit> = safeApiCall { api.unsendMessage(messageId).orThrow() }
    suspend fun markRead(id: String): Result<Unit> = safeApiCall { api.markConversationRead(id).orThrow() }
    suspend fun accept(id: String): Result<Unit> = safeApiCall { api.acceptConversation(id).orThrow() }
    suspend fun setMuted(id: String, muted: Boolean): Result<Unit> =
        safeApiCall { api.updateConversation(id, mapOf("muted" to muted)).orThrow() }
    suspend fun clear(id: String): Result<Unit> = safeApiCall { api.clearConversation(id).orThrow() }

    private fun Response<Unit>.orThrow() {
        if (!isSuccessful) throw ApiError.fromResponse(code(), errorBody()?.string())
    }
}
