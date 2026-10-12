package `in`.paperboxd.app.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
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
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import `in`.paperboxd.app.AppDestination
import `in`.paperboxd.app.AppState
import `in`.paperboxd.app.domain.model.User
import `in`.paperboxd.app.ui.screens.auth.AuthScreen
import `in`.paperboxd.app.ui.screens.author.AuthorDetailScreen
import `in`.paperboxd.app.ui.screens.bookdetail.BookDetailScreen
import `in`.paperboxd.app.ui.screens.bookdetail.GutenbergReaderScreen
import `in`.paperboxd.app.ui.screens.diary.DiaryEntryDetailScreen
import `in`.paperboxd.app.ui.screens.plus.SmartTbrScreen
import `in`.paperboxd.app.ui.screens.wrapped.WrappedRoute
import `in`.paperboxd.app.ui.screens.fusion.FusionRootScreen
import `in`.paperboxd.app.ui.screens.fusion.FusionRouting
import `in`.paperboxd.app.ui.screens.fusion.LocalFusionRouting
import `in`.paperboxd.app.ui.screens.write.WriteScreen
import `in`.paperboxd.app.domain.model.AttachKind
import `in`.paperboxd.app.FusionRoute
import `in`.paperboxd.app.ui.screens.messages.ChatScreen
import `in`.paperboxd.app.ui.screens.messages.InboxScreen
import `in`.paperboxd.app.ui.screens.messages.InboxViewModel
import `in`.paperboxd.app.ui.screens.messages.PollWhileResumed
import `in`.paperboxd.app.ui.screens.home.HomeScreen
import `in`.paperboxd.app.ui.screens.leaderboard.LeaderboardScreen
import `in`.paperboxd.app.ui.screens.onboarding.OnboardingScreen
import `in`.paperboxd.app.ui.screens.onboarding.TempoPromptSheet
import `in`.paperboxd.app.ui.screens.profile.EditProfileScreen
import `in`.paperboxd.app.ui.screens.profile.ProfileScreen
import `in`.paperboxd.app.ui.screens.search.SearchScreen
import `in`.paperboxd.app.ui.screens.splash.SplashScreen
import `in`.paperboxd.app.ui.components.CelebrationOverlayHost
import `in`.paperboxd.app.ui.screens.jazy.JazyScreen
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.TextSecondary

/**
 * Root switch on the AppState destination — splash / auth / onboarding / main.
 *
 * Cross-faded rather than swapped, so the splash hands off to the next screen
 * as one movement instead of a cut (matches the iOS root transition).
 */
@Composable
fun AppRoot(appState: AppState) {
    val destination by appState.destination.collectAsState()
    AnimatedContent(
        targetState = destination,
        transitionSpec = { fadeIn(tween(450)) togetherWith fadeOut(tween(450)) },
        // Key on the destination kind: a same-kind update (a refreshed user on
        // Main) must not tear the nav host down and lose its back stacks.
        contentKey = { it::class },
        label = "root",
    ) { dest ->
        when (dest) {
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
}

private val tabRoutes = mapOf(
    DockTab.Home to Screen.Home.route,
    DockTab.Search to Screen.Search.route,
    DockTab.Inbox to Screen.Messages.route,
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
    val fusionRoute by appState.fusionRoute.collectAsState()
    // Messages badge on the dock. No push yet: polled while the app is open,
    // and on every tab/screen change.
    val inbox: InboxViewModel = hiltViewModel()
    val inboxState by inbox.state.collectAsState()
    PollWhileResumed(30_000) { inbox.refreshBadge() }
    LaunchedEffect(currentRoute) { inbox.refreshBadge() }

    CompositionLocalProvider(LocalFusionRouting provides FusionRouting(fusionRoute, appState::openFusion)) {
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
                    onOpenSmartTbr = { navController.navigate("smart-tbr") },
                    onOpenDiaryEntry = { entry, compose ->
                        navController.navigate("diary-entry/${entry.username}/${entry.id}?compose=$compose")
                    },
                    reloadKey = reloadKey,
                    onOpenChat = { navController.navigate(Screen.Chat.route(username = it)) }
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
                    onOpenProfile = { navController.navigate(Screen.Profile.route(it)) },
                    onOpenAuthor = { navController.navigate(Screen.Author.route(it)) },
                    onReadFree = { title, url -> navController.navigate(Screen.GutenbergReader.route(title, url)) },
                    onWrite = { showWrite = true }
                )
            }
            composable(Screen.GutenbergReader.route) { entry ->
                GutenbergReaderScreen(
                    title = entry.arguments?.getString("title").orEmpty(),
                    url = entry.arguments?.getString("url").orEmpty(),
                    onClose = { navController.popBackStack() }
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
                    onOpenSmartTbr = { navController.navigate("smart-tbr") },
                    onOpenDiaryEntry = { entry, compose ->
                        navController.navigate("diary-entry/${entry.username}/${entry.id}?compose=$compose")
                    },
                    reloadKey = reloadKey,
                    onOpenChat = { navController.navigate(Screen.Chat.route(username = it)) }
                )
            }
            composable(Screen.Author.route) {
                AuthorDetailScreen(
                    onBack = { navController.popBackStack() },
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) }
                )
            }
            composable("wrapped") {
                WrappedRoute(onClose = { navController.popBackStack() })
            }
            composable("smart-tbr") {
                SmartTbrScreen(
                    user = sessionUser,
                    onClose = { navController.popBackStack() },
                    onOpenBook = { navController.navigate(Screen.BookDetail.route(it)) }
                )
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
            composable(Screen.Messages.route) {
                // A dock tab: no back arrow.
                InboxScreen(
                    onBack = null,
                    onOpenChat = { navController.navigate(Screen.Chat.route(conversationId = it)) }
                )
            }
            composable(
                Screen.Chat.route,
                arguments = listOf(
                    navArgument("conversationId") { type = NavType.StringType; defaultValue = "" },
                    navArgument("username") { type = NavType.StringType; defaultValue = "" }
                )
            ) {
                ChatScreen(
                    onBack = { navController.popBackStack() },
                    onOpenProfile = { navController.navigate(Screen.Profile.route(it)) },
                    onOpenFusionLink = { appState.openFusion(FusionRoute.Link(it)) },
                    // Lists have no screen of their own on Android; they open from the owner's profile.
                    onOpenAttachment = { a ->
                        when (a.kind) {
                            AttachKind.BOOK -> navController.navigate(Screen.BookDetail.route(a.refId))
                            AttachKind.PROFILE -> navController.navigate(Screen.Profile.route(a.str("username")))
                            AttachKind.LIST -> navController.navigate(Screen.Profile.route(a.str("owner_username")))
                            AttachKind.THOUGHT -> navController.navigate("diary-entry/${a.str("author_username")}/${a.refId}")
                            AttachKind.FUSION -> appState.openFusion(FusionRoute.Open(a.refId, null))
                        }
                    }
                )
            }
            // {username} is the thought's author, not the profile it was opened from.
            composable(
                "diary-entry/{username}/{entryId}?compose={compose}",
                arguments = listOf(navArgument("compose") { type = NavType.BoolType; defaultValue = false })
            ) {
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
                inboxBadge = inboxState.badge.total,
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

        // Fusion links, notification taps and the profile list all land here.
        fusionRoute?.let { route ->
            FusionRootScreen(route = route, user = sessionUser, onClose = { appState.openFusion(null) })
        }

        // One-shot reading-pace prompt right after onboarding finishes.
        val showTempoPrompt by appState.showTempoPrompt.collectAsState()
        if (showTempoPrompt) {
            TempoPromptSheet(onDismiss = appState::dismissTempoPrompt)
        }
    }
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
