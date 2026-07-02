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

/** Reading tempo option. Cosmetic — collected but not persisted, matches web + iOS. */
data class ReadingTempo(val id: String, val label: String, val sub: String) {
    companion object {
        val all: List<ReadingTempo> = listOf(
            ReadingTempo("casual", "Casual", "1–3 books / month"),
            ReadingTempo("regular", "Regular", "About 1 / week"),
            ReadingTempo("voracious", "Voracious", "Multiple per week")
        )
    }
}

/** POST /users/me/avatar/upload — full user response; only the URL is needed. */
data class AvatarUploadResponse(
    @SerializedName("avatar_url") val avatarUrl: String? = null
)

/** Body for POST /users/me/onboarding. */
data class OnboardingBody(
    val genres: List<String>,
    val authors: List<String> = emptyList()
)
