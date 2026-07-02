package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Full book detail. Maps types.BookResponse from the Go backend; metadata is
 * nested in `volumeInfo` mirroring the Google Books API shape.
 */
data class Book(
    val id: String,
    val slug: String? = null,
    val volumeInfo: VolumeInfo,
    val paperboxdStats: PaperboxdStats? = null,
    val googleBooksId: String? = null,
    val apiSource: String? = null
) {
    val title: String get() = volumeInfo.title
    val authors: List<String> get() = volumeInfo.authors ?: emptyList()
    val authorLine: String get() = authors.joinToString(", ")
    val description: String? get() = volumeInfo.description?.takeIf { it.isNotEmpty() }
    val pageCount: Int? get() = volumeInfo.pageCount?.takeIf { it > 0 }
    val publishedYear: String? get() = volumeInfo.publishedDate?.takeIf { it.length >= 4 }?.take(4)
    val categories: List<String> get() = volumeInfo.categories ?: emptyList()
    val averageRating: Double? get() = volumeInfo.averageRating

    /** Best ISBN — prefers ISBN-13, falls back to ISBN-10. */
    val isbn: String?
        get() {
            val ids = volumeInfo.industryIdentifiers ?: return null
            return ids.firstOrNull { it.type == "ISBN_13" }?.identifier
                ?: ids.firstOrNull { it.type == "ISBN_10" }?.identifier
                ?: ids.firstOrNull()?.identifier
        }

    /** Best cover URL, largest first. Forces HTTPS (Google Books returns http://). */
    val coverUrl: String?
        get() {
            val links = volumeInfo.imageLinks
            val raw = links?.large ?: links?.medium ?: links?.thumbnail
                ?: links?.small ?: links?.smallThumbnail
            return raw?.replace("http://", "https://")
        }
}

data class VolumeInfo(
    val title: String,
    val authors: List<String>? = null,
    val publishedDate: String? = null,
    val description: String? = null,
    val pageCount: Int? = null,
    val categories: List<String>? = null,
    val imageLinks: ImageLinks? = null,
    val averageRating: Double? = null,
    val publisher: String? = null,
    val language: String? = null,
    val industryIdentifiers: List<IndustryIdentifier>? = null
)

data class IndustryIdentifier(
    val type: String,
    val identifier: String
)

data class ImageLinks(
    val smallThumbnail: String? = null,
    val thumbnail: String? = null,
    val small: String? = null,
    val medium: String? = null,
    val large: String? = null,
    val extraLarge: String? = null
)

data class PaperboxdStats(
    val rating: Double? = null,
    val ratingsCount: Int? = null,
    val totalReads: Int? = null,
    val totalLikes: Int? = null,
    val totalTBR: Int? = null
)

/** Google-Books-style list shape from /books/search, /books/latest, /books/random. */
data class BookListResponse(
    val items: List<Book>? = null,
    val totalItems: Int = 0,
    val page: Int? = null,
    val pageSize: Int? = null,
    val pagination: PaginationMeta? = null
)

data class PaginationMeta(
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    val total: Long,
    @SerializedName("total_pages") val totalPages: Int
)
