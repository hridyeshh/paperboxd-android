package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** Maps types.ListResponse. */
data class ReadingList(
    val id: String,
    @SerializedName("user_id") val userId: String,
    val username: String,
    val title: String,
    val description: String? = null,
    @SerializedName("is_private") val isPrivate: Boolean = false,
    @SerializedName("book_count") val bookCount: Long = 0,
    @SerializedName("save_count") val saveCount: Long = 0,
    @SerializedName("is_saved") val isSaved: Boolean = false,
    @SerializedName("can_edit") val canEdit: Boolean = false,
    @SerializedName("can_view") val canView: Boolean = true,
    @SerializedName("cover_urls") val coverUrls: List<String> = emptyList(),
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("updated_at") val updatedAt: String = ""
)

data class UserListsResponse(
    @SerializedName("own_lists") val ownLists: List<ReadingList> = emptyList(),
    @SerializedName("saved_lists") val savedLists: List<ReadingList> = emptyList()
)

/** Payload for POST /users/{username}/lists. */
data class CreateListRequest(
    val title: String,
    val description: String?,
    @SerializedName("is_private") val isPrivate: Boolean
)

/** Maps types.ListWithBooksResponse (ListResponse fields + books). */
data class ListWithBooksResponse(
    val id: String = "",
    val username: String = "",
    val title: String = "",
    val description: String? = null,
    @SerializedName("is_private") val isPrivate: Boolean = false,
    @SerializedName("book_count") val bookCount: Long = 0,
    @SerializedName("save_count") val saveCount: Long = 0,
    @SerializedName("can_edit") val canEdit: Boolean = false,
    val books: List<Book> = emptyList()
)

/** Shelf book: BookResponse + bookshelf fields (flattened by the backend). */
data class BookWithStatus(
    val id: String,
    val volumeInfo: VolumeInfo,
    val paperboxdStats: PaperboxdStats? = null,
    val googleBooksId: String? = null,
    val slug: String? = null,
    val status: String = "",
    val rating: Int? = null,
    @SerializedName("finished_at") val finishedAt: String? = null,
    @SerializedName("added_at") val addedAt: String = ""
) {
    val title: String get() = volumeInfo.title
    val authors: List<String> get() = volumeInfo.authors ?: emptyList()
    val coverUrl: String?
        get() {
            val links = volumeInfo.imageLinks
            val raw = links?.large ?: links?.medium ?: links?.thumbnail ?: links?.smallThumbnail
            return raw?.replace("http://", "https://")
        }
}

data class BookshelfResponse(
    val books: List<BookWithStatus> = emptyList(),
    @SerializedName("total_count") val totalCount: Long = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0,
    val pagination: PaginationMeta? = null
)

data class BookWithLikedAt(
    val id: String,
    val volumeInfo: VolumeInfo,
    val paperboxdStats: PaperboxdStats? = null,
    val googleBooksId: String? = null,
    val slug: String? = null,
    @SerializedName("liked_at") val likedAt: String = ""
) {
    val title: String get() = volumeInfo.title
    val coverUrl: String?
        get() {
            val links = volumeInfo.imageLinks
            val raw = links?.large ?: links?.medium ?: links?.thumbnail ?: links?.smallThumbnail
            return raw?.replace("http://", "https://")
        }
}

data class LikesResponse(
    val books: List<BookWithLikedAt> = emptyList(),
    @SerializedName("total_count") val totalCount: Long = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0,
    val pagination: PaginationMeta? = null
)

/** Maps types.TBRResponse. */
data class TbrItem(
    val id: String,
    @SerializedName("book_id") val bookId: String,
    val book: Book,
    val status: String = "",
    @SerializedName("tbr_notes") val tbrNotes: String? = null,
    @SerializedName("tbr_priority") val tbrPriority: String? = null,
    @SerializedName("tbr_added_at") val tbrAddedAt: String? = null,
    @SerializedName("created_at") val createdAt: String = ""
)
