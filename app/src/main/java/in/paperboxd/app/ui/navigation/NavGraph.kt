package `in`.paperboxd.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import `in`.paperboxd.app.AppDestination
import `in`.paperboxd.app.AppState
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.screens.auth.AuthScreen
import `in`.paperboxd.app.ui.screens.bookdetail.BookDetailScreen
import `in`.paperboxd.app.ui.screens.diary.DiaryEntryDetailScreen
import `in`.paperboxd.app.ui.screens.wrapped.WrappedRoute
import `in`.paperboxd.app.ui.screens.write.WriteScreen
import `in`.paperboxd.app.ui.screens.home.HomeScreen
import `in`.paperboxd.app.ui.screens.leaderboard.LeaderboardScreen
import `in`.paperboxd.app.ui.screens.onboarding.OnboardingScreen
import `in`.paperboxd.app.ui.screens.profile.EditProfileScreen
import `in`.paperboxd.app.ui.screens.profile.ProfileScreen
import `in`.paperboxd.app.ui.screens.search.SearchScreen
import `in`.paperboxd.app.ui.screens.splash.SplashScreen
import `in`.paperboxd.app.ui.components.CelebrationOverlayHost
import `in`.paperboxd.app.ui.screens.jazy.JazyScreen
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.TextSecondary

/** Root switch on the AppState destination — splash / auth / onboarding / main. */
@Composable
fun AppRoot(appState: AppState) {
    val destination by appState.destination.collectAsState()
    when (val dest = destination) {
        is AppDestination.Splash -> SplashScreen(onBootstrap = { appState.bootstrap() })
        is AppDestination.Auth -> AuthScreen(
            onSignedIn = { token, user, refreshToken -> appState.signedIn(token, user, refreshToken) }
        )
        is AppDestination.Onboarding -> OnboardingScreen(
            user = dest.user,
            onUsernameSet = { appState.setOnboardingUsername(it) },
            onFinished = { appState.finishOnboarding() }
        )
        is AppDestination.Main -> MainScaffold(user = dest.user, appState = appState)
    }
}

private val tabRoutes = mapOf(
    DockTab.Home to Screen.Home.route,
    DockTab.Search to Screen.Search.route,
    DockTab.Leaderboard to Screen.Leaderboard.route,
    DockTab.Profile to Screen.ProfileTab.route
)

/**
 * Main tab container: one NavHost, per-tab back stacks preserved via
 * saveState/restoreState (the Android twin of iOS per-tab NavigationStacks).
 */
@Composable
fun MainScaffold(user: User, appState: AppState) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    var showScan by rememberSaveable { mutableStateOf(false) }
    var showWrite by rememberSaveable { mutableStateOf(false) }

    val currentUser by appState.currentUser.collectAsState()
    val sessionUser = currentUser ?: user

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    user = sessionUser,
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) },
                    onWrite = { showWrite = true }
                )
            }
            composable(Screen.Search.route) {
                SearchScreen(
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) },
                    onOpenProfile = { navController.navigate(Screen.Profile.route(it)) }
                )
            }
            composable(Screen.Leaderboard.route) {
                LeaderboardScreen(
                    viewer = sessionUser,
                    onOpenProfile = { navController.navigate(Screen.Profile.route(it)) }
                )
            }
            composable(Screen.ProfileTab.route) { entry ->
                val reloadKey by entry.savedStateHandle
                    .getStateFlow("profileUpdated", 0).collectAsState()
                ProfileScreen(
                    username = sessionUser.username.orEmpty(),
                    viewer = sessionUser,
                    showBack = false,
                    onBack = {},
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) },
                    onOpenProfile = { navController.navigate(Screen.Profile.route(it)) },
                    onSignOut = { appState.signOut() },
                    onOpenEditProfile = { navController.navigate("edit-profile") },
                    onOpenWrapped = { navController.navigate("wrapped") },
                    onOpenDiaryEntry = {
                        navController.navigate("diary-entry/${sessionUser.username.orEmpty()}/$it")
                    },
                    reloadKey = reloadKey
                )
            }
            composable(
                Screen.BookDetail.route,
                deepLinks = listOf(navDeepLink { uriPattern = Screen.BookDetail.DEEP_LINK })
            ) {
                BookDetailScreen(
                    user = sessionUser,
                    onBack = { navController.popBackStack() },
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) },
                    onOpenProfile = { navController.navigate(Screen.Profile.route(it)) }
                )
            }
            composable(
                Screen.Profile.route,
                deepLinks = listOf(navDeepLink { uriPattern = Screen.Profile.DEEP_LINK })
            ) { entry ->
                val username = entry.arguments?.getString("username").orEmpty()
                val reloadKey by entry.savedStateHandle
                    .getStateFlow("profileUpdated", 0).collectAsState()
                ProfileScreen(
                    username = username,
                    viewer = sessionUser,
                    showBack = true,
                    onBack = { navController.popBackStack() },
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) },
                    onOpenProfile = { navController.navigate(Screen.Profile.route(it)) },
                    onSignOut = {},
                    onOpenEditProfile = { navController.navigate("edit-profile") },
                    onOpenWrapped = { navController.navigate("wrapped") },
                    onOpenDiaryEntry = { navController.navigate("diary-entry/$username/$it") },
                    reloadKey = reloadKey
                )
            }
            composable("wrapped") {
                WrappedRoute(onClose = { navController.popBackStack() })
            }
            composable("edit-profile") {
                EditProfileScreen(
                    username = sessionUser.username.orEmpty(),
                    onClose = { navController.popBackStack() },
                    onSaved = {
                        navController.previousBackStackEntry?.savedStateHandle?.let { h ->
                            h["profileUpdated"] = (h.get<Int>("profileUpdated") ?: 0) + 1
                        }
                        navController.popBackStack()
                    }
                )
            }
            composable("diary-entry/{username}/{entryId}") {
                DiaryEntryDetailScreen(
                    onBack = { navController.popBackStack() },
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) }
                )
            }
        }

        // Dock + floating Pip only on tab roots — detail screens are full-bleed.
        if (currentRoute in tabRoutes.values) {
            // Pip scan button floats bottom-right above the dock (iOS twin).
            PipScanButton(
                onClick = { showScan = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 18.dp, bottom = 84.dp)
            )
            BottomNavBar(
                selected = tabRoutes.entries.firstOrNull { it.value == currentRoute }?.key
                    ?: DockTab.Home,
                onSelect = { tab ->
                    val route = tabRoutes.getValue(tab)
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        if (showWrite) {
            WriteScreen(
                user = sessionUser,
                onDismiss = { showWrite = false }
            )
        }
        // Pip opens Ask Jazy; the scanner rises from inside it on the camera tap.
        if (showScan) {
            JazyScreen(
                user = sessionUser,
                onDismiss = { showScan = false },
                onOpenBook = { bookId ->
                    showScan = false
                    navController.navigate(Screen.BookDetail.route(bookId))
                }
            )
        }

        // Full-screen celebration takeovers (shelved / streak / level-up),
        // above the dock and the write sheet. iOS twin: MainTabView overlay.
        CelebrationOverlayHost()
    }
}

/** Temporary stand-in until each screen lands. */
@Composable
fun PlaceholderScreen(name: String) {
    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            color = TextSecondary
        )
    }
}
