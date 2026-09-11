package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** A selectable genre. `id` is the slug stored in users.favorite_genres. */
data class Genre(val id: String, val label: String) {
    companion object {
        val all: List<Genre> = listOf(
            Genre("fiction", "Fiction"),
            Genre("mystery", "Mystery"),
            Genre("thriller", "Thriller"),
            Genre("romance", "Romance"),
            Genre("science-fiction", "Sci-Fi"),
            Genre("fantasy", "Fantasy"),
            Genre("horror", "Horror"),
            Genre("historical", "Historical"),
            Genre("biography", "Biography"),
            Genre("self-help", "Self-Help"),
            Genre("business", "Business"),
            Genre("non-fiction", "Non-Fiction"),
            Genre("young-adult", "Young Adult"),
            Genre("classics", "Classics"),
            Genre("poetry", "Poetry")
        )
    }
}

/** Backend cap on favourites (Top 4); the "books you love" step reuses it. */
const val ONBOARDING_MAX_PICKS = 4

/** One row of GET /api/v1/users/suggested. `reason` is server-authored. */
data class SuggestedReader(
    val id: String,
    val username: String,
    val name: String? = null,
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    val bio: String? = null,
    @SerializedName("books_read_count") val booksReadCount: Int = 0,
    @SerializedName("followers_count") val followersCount: Int = 0,
    @SerializedName("shared_genres") val sharedGenres: List<String> = emptyList(),
    val reason: String = ""
) {
    val displayName: String get() = name?.takeIf { it.isNotBlank() } ?: username
}

data class SuggestedReadersResponse(val users: List<SuggestedReader> = emptyList())

/** POST /users/me/avatar/upload — full user response; only the URL is needed. */
data class AvatarUploadResponse(
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

/** Body for POST /users/me/onboarding. */
data class OnboardingBody(
    val genres: List<String>,
    val authors: List<String> = emptyList()
)
