package `in`.paperboxd.app.ui.navigation

import android.net.Uri

/** All in-tab navigation routes. Root states (splash/auth/onboarding/main) live in AppState. */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Leaderboard : Screen("leaderboard")
    data object ProfileTab : Screen("profile-tab")

    data object BookDetail : Screen("book/{bookId}") {
        fun route(bookId: String) = "book/$bookId"

        /**
         * Web book pages are addressed by *slug*, not UUID (`app/b/[slug]` on the site,
         * `GET /books/by-slug/{slug}` on the API). BookDetailViewModel resolves a
         * non-UUID `bookId` before loading, so both forms land here.
         */
        const val DEEP_LINK = "https://paperboxd.in/b/{bookId}"
    }

    data object Author : Screen("author/{name}") {
        fun route(name: String) = "author/" + Uri.encode(name)
    }

    data object Profile : Screen("profile/{username}") {
        fun route(username: String) = "profile/$username"

        const val DEEP_LINK = "https://paperboxd.in/u/{username}"
    }
}
