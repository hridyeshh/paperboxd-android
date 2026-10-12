package `in`.paperboxd.app.domain.model

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

// Messages (DMs). Shapes mirror internal/service/messages.go in the backend
// (MOBILE_API.md §3.13) and Models/Message.swift on iOS.

object AttachKind {
    const val BOOK = "book"
    const val LIST = "list"
    const val THOUGHT = "thought"
    const val PROFILE = "profile"
    const val FUSION = "fusion"
    // ref_id is a MediaUpload id.
    const val PHOTO = "photo"
    const val VIDEO = "video"
    // ref_id is a GIPHY id.
    const val GIF = "gif"
    const val STICKER = "sticker"

    val MEDIA = setOf(PHOTO, VIDEO, GIF, STICKER)
}

data class MessagePerson(
    @SerializedName("user_id") val userId: String = "",
    val username: String = "",
    val name: String = "",
    @SerializedName("avatar_url") val avatarUrl: String? = null
) {
    val displayName: String get() = name.ifBlank { username }
}

/** What a client sends: the object's kind and id. The server builds the card. */
data class AttachmentRef(
    val kind: String,
    @SerializedName("ref_id") val refId: String
)

/** An attachment as stored: [snapshot] is the card as it looked when sent. */
data class MessageAttachment(
    val kind: String = "",
    @SerializedName("ref_id") val refId: String = "",
    val snapshot: JsonObject = JsonObject()
) {
    fun str(key: String): String =
        snapshot.get(key)?.takeUnless { it.isJsonNull }?.let { runCatching { it.asString }.getOrNull() }.orEmpty()

    fun int(key: String): Int? =
        snapshot.get(key)?.takeUnless { it.isJsonNull }?.let { runCatching { it.asInt }.getOrNull() }

    fun double(key: String): Double? =
        snapshot.get(key)?.takeUnless { it.isJsonNull }?.let { runCatching { it.asDouble }.getOrNull() }

    val isMedia: Boolean get() = kind in AttachKind.MEDIA

    fun strings(key: String): List<String> =
        snapshot.get(key)?.takeIf { it.isJsonArray }?.asJsonArray
            ?.mapNotNull { e -> runCatching { e.asString }.getOrNull() }.orEmpty()

    /** Fusion snapshot: the two readers. */
    fun readers(): List<MessagePerson> =
        snapshot.get("readers")?.takeIf { it.isJsonArray }?.asJsonArray?.mapNotNull { e ->
            val o = e.takeIf { it.isJsonObject }?.asJsonObject ?: return@mapNotNull null
            fun s(k: String) = o.get(k)?.takeUnless { it.isJsonNull }?.asString.orEmpty()
            MessagePerson(username = s("username"), name = s("name"), avatarUrl = s("avatar_url").ifBlank { null })
        }.orEmpty()
}

data class ChatMessage(
    val id: Long = 0,
    @SerializedName("conversation_id") val conversationId: String = "",
    @SerializedName("sender_id") val senderId: String = "",
    @SerializedName("from_me") val fromMe: Boolean = false,
    val body: String = "",
    @SerializedName("created_at") val createdAt: String = "",
    val unsent: Boolean = false,
    val attachments: List<MessageAttachment> = emptyList()
)

data class MessagePreview(
    val text: String = "",
    @SerializedName("from_me") val fromMe: Boolean = false,
    @SerializedName("created_at") val createdAt: String = ""
)

data class ConversationSummary(
    val id: String = "",
    /** accepted | request — the viewer's side. */
    val state: String = "accepted",
    val muted: Boolean = false,
    val them: MessagePerson = MessagePerson(),
    @SerializedName("last_message") val lastMessage: MessagePreview = MessagePreview(),
    @SerializedName("unread_count") val unreadCount: Int = 0
)

data class ConversationList(
    val conversations: List<ConversationSummary> = emptyList(),
    @SerializedName("next_cursor") val nextCursor: String = ""
)

data class ConversationPage(
    val id: String = "",
    val state: String = "accepted",
    val muted: Boolean = false,
    val them: MessagePerson = MessagePerson(),
    @SerializedName("their_last_read_id") val theirLastReadId: Long = 0,
    /** Oldest first. */
    val messages: List<ChatMessage> = emptyList(),
    @SerializedName("has_more") val hasMore: Boolean = false
)

data class ConversationWith(
    /** Empty when no conversation exists yet — the first send creates it. */
    @SerializedName("conversation_id") val conversationId: String = "",
    val them: MessagePerson = MessagePerson()
)

data class SendMessageRequest(
    @SerializedName("conversation_id") val conversationId: String? = null,
    val to: List<String>? = null,
    val body: String = "",
    val attachments: List<AttachmentRef> = emptyList(),
    @SerializedName("client_id") val clientId: String
)

data class SendFailure(val username: String = "", val error: String = "")

data class SendResult(
    val sent: List<ChatMessage> = emptyList(),
    val failed: List<SendFailure> = emptyList()
)

data class SharedItem(
    @SerializedName("message_id") val messageId: Long = 0,
    val kind: String = "",
    @SerializedName("ref_id") val refId: String = "",
    val snapshot: JsonObject = JsonObject(),
    @SerializedName("from_me") val fromMe: Boolean = false,
    @SerializedName("created_at") val createdAt: String = ""
) {
    val attachment: MessageAttachment get() = MessageAttachment(kind, refId, snapshot)
}

data class SharedList(
    val counts: Map<String, Int> = emptyMap(),
    val items: List<SharedItem> = emptyList(),
    @SerializedName("next_cursor") val nextCursor: String = ""
)

data class InboxBadge(val unread: Int = 0, val requests: Int = 0) {
    val total: Int get() = unread + requests
}

/** POST /messages/media → attach as AttachmentRef(kind, refId). */
data class MediaUpload(
    @SerializedName("ref_id") val refId: String = "",
    val kind: String = "",
    val url: String = "",
    @SerializedName("poster_url") val posterUrl: String = "",
    val width: Int = 0,
    val height: Int = 0,
    @SerializedName("duration_ms") val durationMs: Int = 0
)

/** A GIPHY GIF or sticker from GET /messages/gifs. */
data class GifItem(
    val id: String = "",
    val url: String = "",
    @SerializedName("preview_url") val previewUrl: String = "",
    val width: Int = 0,
    val height: Int = 0
)

data class GifList(val items: List<GifItem> = emptyList())
