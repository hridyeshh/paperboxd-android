package `in`.paperboxd.app.ui.navigation

/** All in-tab navigation routes. Root states (splash/auth/onboarding/main) live in AppState. */
sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Search : Screen("search")
    data object Leaderboard : Screen("leaderboard")
    data object ProfileTab : Screen("profile-tab")

    data object BookDetail : Screen("book/{bookId}") {
        fun route(bookId: String) = "book/$bookId"
    }

    data object Profile : Screen("profile/{username}") {
        fun route(username: String) = "profile/$username"
    }
}
