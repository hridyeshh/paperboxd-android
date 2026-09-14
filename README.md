# PaperBoxd for Android

> *Your reading universe, in your pocket.*

The native Jetpack Compose client for [PaperBoxd](https://paperboxd.in) — a social book-tracking platform inspired by the simplicity and community spirit of Letterboxd, but built exclusively for books.

**Website:** [paperboxd.in](https://paperboxd.in) · **API:** [api.paperboxd.com](https://api.paperboxd.com) · **Contact:** contact@paperboxd.in

---

## Table of Contents

- [Overview](#overview)
- [Requirements](#requirements)
- [Getting Started](#getting-started)
- [Architecture](#architecture)
- [Project Layout](#project-layout)
- [App State Machine](#app-state-machine)
- [Navigation & The Dock](#navigation--the-dock)
- [Networking Layer](#networking-layer)
- [Error Handling](#error-handling)
- [Authentication & Security](#authentication--security)
- [Features](#features)
- [Ask Jazy](#ask-jazy)
- [Scan & Know](#scan--know)
- [Monthly Wrapped](#monthly-wrapped)
- [Fusion](#fusion)
- [Design System](#design-system)
- [Domain Models](#domain-models)
- [Endpoints Reference](#endpoints-reference)
- [Manifest, Permissions & Deep Links](#manifest-permissions--deep-links)
- [Dependencies](#dependencies)
- [Conventions](#conventions)
- [Parity With iOS](#parity-with-ios)
- [Known Gaps](#known-gaps)
- [Troubleshooting](#troubleshooting)
- [Related Repositories](#related-repositories)

---

## Overview

This is the Android surface of PaperBoxd. It's a port of the SwiftUI app, not an independent design — screen-for-screen parity is a goal, and the source is full of "iOS twin" comments naming the counterpart. Where a platform convention argues otherwise, Android wins and the divergence is documented in a comment (the dock is the main example).

It talks to the Go backend over REST/JSON with a long-lived JWT — no cookies, no session state, no local database. Everything on screen is fetched live or read from an encrypted preferences file holding two keys: the JWT and the session `User`.

**Design stance:** paper and ink on every page. One light theme, no dark variant, no dynamic colour, and the theme never consults `isSystemInDarkTheme()` — a phone in dark mode still gets the brutalist paper kit, same as iOS.

**At a glance:**

| | |
|---|---|
| Language | Kotlin 2.0.21 |
| UI | Jetpack Compose (Material 3) — single Activity, zero XML layouts |
| `minSdk` / `targetSdk` / `compileSdk` | 26 / 36 / 36 |
| Architecture | MVVM + repositories + a root state machine |
| DI | Hilt 2.50 (kapt) |
| Networking | Retrofit 2.9 + OkHttp + Gson, coroutines on `Dispatchers.IO` |
| Persistence | `EncryptedSharedPreferences` (JWT + cached user) |
| Application ID | `in.paperboxd.app` |
| JVM target | 17 |

---

## Requirements

| Tool | Version |
|---|---|
| Android Studio | Ladybug (2024.2) or newer |
| JDK | 17 |
| Gradle | 8.11.1 (wrapper committed) |
| Android Gradle Plugin | 8.10.1 |
| Android SDK Platform | 36 |
| Test device | API 26+ (Android 8.0), with Google Play Services for Google Sign-In |

---

## Getting Started

```bash
git clone git@github.com:hridyeshh/paperboxd-android.git
cd paperboxd-android
```

### 1. Configure `local.properties`

`local.properties` is git-ignored and holds both the SDK path and the Google OAuth client ID:

```properties
sdk.dir=/Users/<you>/Library/Android/sdk
GOOGLE_WEB_CLIENT_ID=<your-web-type-oauth-client-id>.apps.googleusercontent.com
```

`app/build.gradle.kts` reads that property at configure time and emits it as `BuildConfig.GOOGLE_WEB_CLIENT_ID`, which `Config.GOOGLE_WEB_CLIENT_ID` re-exports. Same pattern as the backend reading its allowlist from the environment — the value never enters version control.

> It must be a **Web**-type client ID, not an Android one. Credential Manager passes it as `serverClientId`, and the resulting ID token's `aud` claim carries it — so the same value must appear in the backend's `GOOGLE_OAUTH_ALLOWED_AUDIENCES` allowlist or `/api/mobile/auth/google` rejects the token.

### 2. Release signing (optional)

Release builds sign from a git-ignored `keystore.properties` at the project root:

```properties
storeFile=<path to .jks>
storePassword=…
keyAlias=…
keyPassword=…
```

When the file is absent or empty, `assembleRelease` still builds — unsigned. Debug builds never need it.

### 3. Build and run

```bash
./gradlew assembleDebug          # build
./gradlew installDebug           # build + install on the connected device
./gradlew clean                  # when kapt/Hilt stubs get confused
```

Or open the project in Android Studio and hit **Run**.

### Pointing at a local backend

Both debug and release hit the Railway production backend. For local backend work, change `Config.BASE_URL` in `config/Config.kt`. Plaintext HTTP requires a network-security config (or `usesCleartextTraffic`) on API 28+ — the manifest currently has neither, by design.

---

## Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                       MainActivity                           │
│              @AndroidEntryPoint · @Inject AppState           │
└───────────────────────────┬──────────────────────────────────┘
                            │ AppRoot(appState)
                            │ collectAsState() on AppDestination
      ┌───────────┬─────────┴────────┬──────────────┐
      ▼           ▼                  ▼              ▼
 SplashScreen  AuthScreen    OnboardingScreen   MainScaffold
                                                     │
                            ┌────────────────────────┴─────────┐
                            │  one NavHost · 4 dock tabs       │
                            │  + Pip · Write · Scan overlays   │
                            └────────────────────────┬─────────┘
                                                     │
                            ┌────────────────────────▼─────────┐
                            │  @HiltViewModel ViewModels       │
                            │  StateFlow<UiState<T>>           │
                            └────────────────────────┬─────────┘
                                                     │ suspend
                            ┌────────────────────────▼─────────┐
                            │  Repositories → Result<T>        │
                            └────────────────────────┬─────────┘
                                                     │ safeApiCall
                            ┌────────────────────────▼─────────┐
                            │  ApiService (Retrofit)           │
                            │  OkHttp · Bearer JWT interceptor │
                            └────────────────────────┬─────────┘
                                                     │ HTTPS / REST JSON
                            ┌────────────────────────▼─────────┐
                            │  Go backend (Railway)            │
                            └──────────────────────────────────┘
```

**Layer rules:**

- Composables never touch `ApiService`. A `@HiltViewModel` owns the call and exposes `StateFlow<UiState<T>>`.
- Repositories return `Result<T>` and **never throw to the UI layer** — `safeApiCall` catches `HttpException`, `IOException`, and `JsonParseException` and maps them to typed `ApiError`s.
- `AppState` is a `@Singleton` injected into `MainActivity`; it survives configuration changes because Hilt owns it, not the Activity.
- One Activity, one `NavHost`. No fragments.

```kotlin
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String, val code: String? = null) : UiState<Nothing>()
}
```

---

## Project Layout

```
app/src/main/
├── AndroidManifest.xml
├── assets/legal/                  # privacy-policy.md, terms-of-service.md (rendered in-app)
├── res/
│   ├── font/                      # Space Grotesk (5 weights) + Pinyon Script
│   ├── drawable/                  # app_icon (+ adaptive foreground), google_logo
│   ├── values/                    # strings, themes
│   └── xml/file_paths.xml         # FileProvider paths for share cards
└── java/in/paperboxd/app/
    ├── PaperBoxdApp.kt            # @HiltAndroidApp
    ├── MainActivity.kt            # @AndroidEntryPoint, edge-to-edge, hosts AppRoot
    ├── AppState.kt                # Root state machine (AppDestination)
    ├── config/Config.kt           # Base URL, user agent, prefs name, OAuth client ID
    ├── auth/google/
    │   └── GoogleSignInHelper.kt  # Credential Manager → Google ID token
    ├── data/
    │   ├── local/SecurePrefs.kt   # EncryptedSharedPreferences (token + user)
    │   ├── remote/
    │   │   ├── ApiClient.kt       # OkHttp/Retrofit providers, interceptors, SessionEvents
    │   │   ├── ApiService.kt      # 85 Retrofit endpoints
    │   │   ├── ApiError.kt        # Typed errors + envelope parsing
    │   │   └── SafeCall.kt        # suspend block → Result<T>
    │   └── repository/            # Analytics · Auth · Book · Diary · Fusion · Recommendation · User · Wrapped
    ├── di/AppModule.kt            # App-wide Hilt bindings
    ├── domain/model/              # 17 Gson wire models
    └── ui/
        ├── UiState.kt
        ├── components/            # Shared composables (16 files)
        ├── navigation/            # NavGraph, Screen, BottomNavBar, PipScanButton
        ├── screens/               # auth · author · bookdetail · diary · fusion · home
        │                          # jazy · leaderboard · onboarding · profile · scan
        │                          # search · settings · splash · wrapped · write
        └── theme/                 # Color, Type, Fonts, Theme
```

### Repositories

| Repository | Suspend functions | Covers |
|---|---|---|
| `AuthRepository` | 13 | Login, register, OTP, Google, refresh, health, session persistence, username check, password reset |
| `BookRepository` | 24 | Search, detail, like, shelve, status, progress, reviews, social proof, friends-reading, author info, books by author, vibe search, Jazy, scan analyze |
| `UserRepository` | 31 | Profile, follow graph, follow requests, visibility, block / report, bookshelf, TBR, likes, favorites, authors, streak, lists, reading activity, onboarding, suggested readers, daily open, avatar/banner upload, leaderboard stats |
| `DiaryRepository` | 6 | List, create, detail, delete, like/unlike |
| `RecommendationRepository` | 5 | Home recs, feed, similar books, following activity, community — plus fire-and-forget verdicts and `rec_impression` |
| `WrappedRepository` | 1 | Monthly Wrapped, sent with the device time zone |
| `FusionRepository` | 7 | Invite create / cancel / preview / accept, list, story, delete |
| `AnalyticsRepository` | — | Fire-and-forget `track()` → `POST /api/v1/events`; never blocks, never surfaces errors |

---

## App State Machine

`AppState` is the only global navigation authority.

```kotlin
sealed class AppDestination {
    data object Splash : AppDestination()
    data object Auth : AppDestination()
    data class Onboarding(val user: User) : AppDestination()
    data class Main(val user: User) : AppDestination()
}
```

### Bootstrap flow

`AppState.bootstrap()`, kicked off by `SplashScreen`:

1. **Read the JWT from `SecurePrefs`.** Null or empty → `Auth`.
2. **Probe `GET /api/health`.** Unreachable → keep the token (a flaky network is not a logout) and route from the cached `User`; no cached user → `Auth`.
3. **`POST /api/mobile/auth/refresh`.** Success re-mints and persists the token. Failure clears the session and routes to `Auth`.
4. **Route** on the cached user: `needsOnboarding(user)` → `Onboarding(user)`, else `Main(user)`.
5. **Hold the splash** for a minimum of 4 600 ms — the same floor as iOS, sized to the splash animation — so a warm start doesn't flash three screens.

`AppState` also owns `fusionRoute`: `handleIncomingUri` turns a verified `https://paperboxd.in/fusion/<token>` link or a `paperboxd://fusion/<token>` URI into a route (`MainActivity.onNewIntent` handles warm links), and `MainScaffold` renders the Fusion flow above the dock. A link opened while signed out waits in `SecurePrefs` until sign-in.

Once the destination is `Main`, `AppState.recordDailyOpen()` posts `/api/v1/users/me/daily-open` at most once per UTC day. Streaks count **activity days**, so opening the app is enough to keep one alive.

### Onboarding gate

```kotlin
private fun needsOnboarding(user: User): Boolean =
    user.onboardingCompleted?.let { !it } ?: user.username.isNullOrEmpty()
```

`onboardingCompleted` is null for users cached by an older build; falling back to username presence means existing users never get re-onboarded after an update.

### Session expiry

`UnauthorizedInterceptor` sees any 401, clears `SecurePrefs`, and emits on `SessionEvents.expired` (a `MutableSharedFlow`). `AppState` collects it in its init block, nils the user, and flips the destination to `Auth`. No screen handles logout itself.

---

## Navigation & The Dock

`MainScaffold` hosts **one** `NavHost` with per-tab back stacks preserved via `saveState` / `restoreState` on `navigate` — the Android twin of iOS per-tab `NavigationStack`s.

| Route | Screen |
|---|---|
| `home` | `HomeScreen` |
| `search` | `SearchScreen` |
| `leaderboard` | `LeaderboardScreen` |
| `profile-tab` | `ProfileScreen` (own profile, no back button) |
| `book/{bookId}` | `BookDetailScreen` |
| `profile/{username}` | `ProfileScreen` (other user, with back) |
| `author/{name}` | `AuthorDetailScreen` |
| `wrapped` | `WrappedStoryScreen` |
| `edit-profile` | `EditProfileScreen` |
| `diary-entry/{username}/{entryId}` | `DiaryEntryDetailScreen` |

**The dock diverges from iOS on purpose.** `BottomNavBar` is a solid white rounded pill with side margins, icon-only tabs, and a Sienna tonal-pill active indicator — variant "A · tonal pill" from *Bottom Dock.html · 03 Android redesign*. It is not a glass dock. The dock and the floating `PipScanButton` (which opens Ask Jazy) render only on tab roots; detail screens are full-bleed.

**Modal overlays** live in the same `Box` as the `NavHost`, gated by `rememberSaveable` booleans rather than nav routes — they survive rotation but never enter the back stack:

- `WriteScreen` — diary composer
- `JazyScreen` — opened by the Pip button; Scan & Know (`ScanFlowScreen`) rises from inside it on the camera tap
- `CelebrationOverlayHost` — full-screen shelved / streak / level-up takeovers, drawn above everything

**Cross-screen refresh** uses `savedStateHandle`: `EditProfileScreen` increments a `profileUpdated` counter on the previous back-stack entry before popping, and `ProfileScreen` collects it as `reloadKey`. No event bus, no shared ViewModel.

---

## Networking Layer

Retrofit over OkHttp, both provided as `@Singleton` by `NetworkModule`.

**OkHttp client:**

| Setting | Value |
|---|---|
| Connect timeout | 30 s |
| Read timeout | 30 s |
| `AuthInterceptor` | Adds `User-Agent: PaperBoxd-Android/<versionName> (Android)`, `Accept: application/json`, and `Authorization: Bearer <jwt>` when a token exists |
| `UnauthorizedInterceptor` | On any 401: clears `SecurePrefs`, emits `SessionEvents.expired` |
| `HttpLoggingInterceptor` | `Level.BASIC`, **debug builds only** |

**Calling convention** — everything goes through `safeApiCall`, which runs on `Dispatchers.IO` and converts exceptions into a `Result`:

```kotlin
suspend fun profile(username: String): Result<UserProfile> =
    safeApiCall { api.getProfile(username) }
```

`ApiService` declares 85 endpoints across health, mobile auth, onboarding, books, authors, bookshelf, profile, privacy and moderation, diary, activity and community, leaderboard, recommendations and the feed, vibe search, Jazy, Wrapped, Fusion, events, and scan.

---

## Error Handling

The backend returns:

```json
{ "error": "Human readable message", "code": "SNAKE_CASE_CODE" }
```

`ApiError.parseEnvelope` tolerates both this flat shape and the older nested `{"error": {"code": …, "message": …}}` form, because both are still in circulation.

`ApiError` is a sealed class of `Exception`:

| Backend code | HTTP | `ApiError` |
|---|---|---|
| `UNAUTHORIZED` / `INVALID_TOKEN` / `EXPIRED_TOKEN` | 401 | `Unauthorized` |
| `FORBIDDEN` | 403 | `Forbidden` |
| `NOT_FOUND` | 404 | `NotFound` |
| `VALIDATION_ERROR` | 400 | `ValidationError` |
| `RATE_LIMITED` | 429 | `RateLimited` |
| `INTERNAL_ERROR` | 5xx | `ServerError` |
| — | any | status-code fallback, then `Unknown` |

Plus `NetworkError` (an `IOException` — no connectivity, DNS, timeout) and `DecodingError` (Gson).

**Display-string precedence:** explicit `message` → friendly copy for a known machine code → the raw `error` string → `"Something went wrong — please try again"`.

Machine codes get hand-written copy instead of being shown raw:

| Code | Shown to the reader |
|---|---|
| `book_not_found` | "Couldn't find this book — try searching by title" |
| `scans_exhausted` | "You've used all your free scans" |
| `scoring_failed` | "Something took too long — your scan hasn't been used" |

Errors are surfaced to the UI, never silently swallowed — this matters most in the scan and bookshelf flows, where a dropped write looks identical to a successful one.

---

## Authentication & Security

| Concern | Implementation |
|---|---|
| Token storage | `EncryptedSharedPreferences` (`SecurePrefs`) — AES256-SIV keys, AES256-GCM values, backed by an AES256-GCM `MasterKey` in the Android Keystore |
| Cached user | Same file, Gson-serialized under `auth.user` |
| Prefs file | `in.paperboxd.app.secure` — name is stable across builds |
| Transport | Pure `Authorization: Bearer <jwt>`. No cookies, ever. |
| Token lifetime | 30 days (mobile default), re-minted on every launch via `/api/mobile/auth/refresh` |
| 401 handling | Interceptor clears credentials and emits `SessionEvents.expired` → forced logout from anywhere |
| Secrets in VCS | None. The OAuth client ID comes from git-ignored `local.properties` via `BuildConfig`. |

### Google Sign-In

`GoogleSignInHelper` uses **Credential Manager** with `GetSignInWithGoogleOption` (`androidx.credentials` 1.3.0 + `googleid` 1.1.1) — the current API, not the deprecated `GoogleSignInClient`. It returns a small sealed result:

```kotlin
sealed interface GoogleSignInResult {
    data class Success(val idToken: String) : GoogleSignInResult
    data object Cancelled : GoogleSignInResult                     // user dismissed — stay silent
    data class Failure(val message: String) : GoogleSignInResult   // real error — surface it
}
```

The distinction matters: a dismissed picker must not raise an error banner, but "no Play Services" or "misconfigured client" must.

The ID token then goes to `POST /api/mobile/auth/google`, which verifies the `aud` claim against its allowlist and returns a PaperBoxd JWT.

**Auth methods available:** email + password, 6-digit email OTP, Google.

---

## Features

### Home
Brutalist wall, top to bottom — the same order as iOS:

- **Greeting** — the server's time-of-day line for the device zone ("Good evening, maya"), falling back to "Hello".
- **Currently reading** card.
- **"Between covers."** — friends' activity; a tapped card opens a book preview that fetches cover and rating on open. Falls back to **"What readers are into."** from `/api/v1/community`.
- **Feed modules** from `GET /api/v1/recommendations/feed?tz=<device zone>`, server-titled, above the legacy rails. A failed feed leaves the legacy home untouched.
- **Picked for you** — reason line + confidence tier per card. **Long-press** opens a `DropdownMenu` verdict posted to `/api/v1/recommendations/feedback` — verdict only; reason chips live on web.
- **Discovery shelves** — trending, rising, most saved, just added — from real shelf data.

Every legacy rail is deduped against feed module books (`usedIds`). Cards emit `rec_impression` with `reason_type`. Notifications open as a bottom sheet.

### Search
Debounced book search (backend-first, ~10–50 ms from PostgreSQL, falling back to Google Books) plus user search, in one screen.

### Book Detail
Cover, metadata, and the action row: shelve / like / rate / review / track progress. Below: **social proof** (`/books/{id}/social` — Paperboxd readers, friends, lists), friends reading, friends' reviews, all reviews, and similar books. The author name opens `AuthorDetailScreen`. Rating and review write through a single `PATCH …/bookshelf/{bookId}`; page progress through `PUT …/bookshelf/{bookId}/progress`. Rating needs 20 pages read or a finish — the backend refuses unearned ratings. `BookShareSheet` renders a share card that can go to the share sheet or be saved to the gallery (`GallerySaver`). Reviews can be reported.

### Profile
Header with avatar inside an XP ring (`ProfileLevel` — tap for the level sheet), banner (in-house pan-only `ImageCropper`, no dependency), stats, follow button, and section grids for Bookshelf, Favorites, TBR, Likes, Authors, Lists (`ListSheets`), and Diary, switched by hand-drawn trimmable icons (`ProfileTabMarks`). `ReadingHeatmap` draws a year of `…/reading/activity?year=` on a Compose `Canvas`. `FollowListSheet` shows followers/following. `ShareProfileSheet` generates a QR code (ZXing) to the public profile URL. Your own profile carries the **Monthly Wrapped** entry card and the **Fusions** section (`FusionsSection` — your Fusions, Create sheet, Invite); another reader's offers Report (`ReportDialog`) and Block. Following a private account sends a request.

### Diary
Entry list, detail, and likes; entries can be reported. `WriteScreen` composes an entry — pick a book, pick a date, set a rating, write.

### Leaderboard
Global, friends-only, and per-dimension boards, plus the viewer's own stats.

### Onboarding
Post-registration flow: claim a username (live availability check) → pick genres → **books you love** → **readers to follow** (`/api/v1/users/suggested`) → an "aha" stage that loads real recommendations before entering the app. Steps emit the same onboarding events as iOS and web.

### Settings
The same sections as iOS: **Privacy** (private account toggle, pending follow requests), **Account**, **Scan & Know** (free-scan quota), **Your Data** (Goodreads import), **Discover** (invite friends, rate PaperBoxd), **About** (privacy policy and terms rendered from `assets/legal/*.md` in `LegalBottomSheet`), then sign out and account deletion.

### Goodreads import
`GoodreadsImportSheet` + VM parse a Goodreads CSV export on-device: for each row, look the book up by ISBN first, then title + author, then add the match to the shelf with the mapped status.

---

## Ask Jazy

The Pip button's destination — the Android twin of iOS `JazyView`. Describe a feeling (or tap a preset tab), and `JazyViewModel` posts to `POST /api/v1/jazy` with a session id and an anonymous id:

- A **`jazy#question`** response renders one clarifying question with tappable options; the answer goes back on the same session.
- A **`jazy#deck`** renders `JazyResultsDeck` — `% match`, a reason, and one honest caveat per book. On a refinement ("shorter", "less weird") the deck header quotes the accumulated ask the server `understood`, and Jazy's `intro` sits on the first card.

**The camera lives here.** Scan & Know opens from Jazy's camera tap, gated by `JazyTasteGate`: under five books on shelf + TBR the camera asks for a few books first, because a compatibility score against an empty shelf is noise. The gate fails **open** when the count can't be fetched.

---

## Scan & Know

Point the camera at a book's barcode; get a personalized 0–100 compatibility score.

**Flow** (`ScanFlowScreen`, opened from Ask Jazy's camera tap):

```
scan  →  analyzing  →  reveal  →  breakdown
 │           │            │           │
 │           │            │           └─ 5-axis radar + for-you / against-you
 │           │            └─ count-up animation to the final score
 │           └─ POST /api/v1/scan/analyze runs while you play a game
 └─ CameraScanner (CameraX + ML Kit) or ScanManualSearch by title
```

**The scoring is real.** `POST /api/v1/scan/analyze` returns the book, a `dimensions` block (genre fit, writing style, length/complexity, community love, personal fit — each scored out of 20, so normalize by 20 for the radar axes), a verdict, for/against bullets, a one-liner, and live source counts (readers, ratings, shelf, friends) shown on the analyzing screen.

**The games.** The analyze call takes a few seconds, so `ScanGameHost` fills the wait with one of three endless games — **Breakout**, **Catch**, and **Stack** — written as **Compose `Canvas` ports of the iOS SpriteKit scenes**. They run a fixed 60 Hz timestep specifically so the per-frame tuning constants carry over from iOS unchanged. Light brutalist: paper canvas `#FCFBF7`, ink `#141414`, muted book-spine tones as the only color.

**Quota.** Scans are limited; the remaining count is persisted by `ScanPrefs` and shown in Settings. The endpoint's only 403 is `scans_exhausted`, which drives a dedicated exhausted layout. The backend reserves a scan before calling Claude and refunds it on failure. The app emits `scan_started` (via `AnalyticsRepository`) when the camera hands over an ISBN; the backend records success and failure.

**Camera stack:** CameraX 1.4.2 (`camera-camera2`, `camera-lifecycle`, `camera-view`) + ML Kit barcode-scanning 17.3.0. Those exact versions matter — 1.4.0+ / 17.3.0+ are the first to ship **16 KB page-size-aligned `.so` files**, which Google Play now requires. Permission is requested at point of use via Accompanist Permissions; `CAMERA` is declared `required="false"` so the app still installs on camera-less devices.

---

## Monthly Wrapped

`WrappedStoryScreen` (route `wrapped`) is the port of the iOS fourteen-chapter story player: `WrappedChapters` draws each chapter, `WrappedKit` holds its palette and type, `WrappedViewModel` loads one `GET /api/v1/users/me/wrapped` through `WrappedRepository`, which always sends the device time zone so the month boundary is the reader's, not the server's. Reading time is labelled an estimate — it's derived from pages. `has_data: false` shows an empty state. Entry: `WrappedEntryCard` on your own profile.

---

## Fusion

Two readers' shelves side by side, from a one-time link — twin of the iOS feature, page for page. Create a Fusion on your profile, share the link; when the other reader opens it and taps **Fuse**, a ten-page story is built from both profiles.

- **`FusionFlow`** holds the view model and root, routing on the link's state: join, analyzing, "they joined", story, or `own` / `used` / `expired` / `unavailable`.
- **`FusionStoryScreen`** is the player — Score · Numbers · Both loved · Agree · Split · Their shelf · Your shelf · The pick · Wildcard · Verdict — plus Explore and a share card. `FusionChapters` draws the pages; `FusionKit` is the Swiss-style kit over the Wrapped motion primitives. Pages the server sends empty are skipped, never shown blank.
- **`LocalFusionRouting`** lets the profile section and the notifications sheet open a Fusion without threading callbacks through the NavHost; a `fusion_joined` update opens it with the "X fused with you" intro.
- **Links:** `/fusion/` joins `/b/` and `/u/` in the verified `paperboxd.in` intent filter, plus a `paperboxd://fusion` filter for the web join page's "Open in the app" button.

Not screenshot-verified — the only AVD has no system image. It compiles clean and the layouts are line-for-line twins of the iOS pages, which were verified in the simulator.

---

## Design System

Light only — no dark variant, no Material You dynamic colour. `PaperBoxdTheme` wraps a single **`lightColorScheme`**. That builder matters: Material derives elevation tints and default on-colours from which one you call, so the old dark scheme holding light values tinted dialogs, sheets, switches and ripples the wrong way against paper.

| Token | Hex | Role |
|---|---|---|
| `Background` | `#F2EDE1` | Paper — app background |
| `Surface` | `#FDFBF6` | Card — elevated surfaces |
| `Border` | `#E6DFD0` | Hairlines, dividers |
| `TextPrimary` | `#151513` | Ink — body, headings, and `primary` (actions are ink plates) |
| `TextSecondary` | `#6A6456` | Muted / eyebrow text |
| `Accent` | `#D23B26` | On `secondary` — progress fills and marks, not buttons |
| `Error` | `#C0271C` | Crimson — reads on paper |
| `Terracotta` → `TerracottaDeep` | `#D97757` → `#6B3520` | `AvatarGradient` ring |
| `LikeRed` | `#D72830` | Active "liked" heart |

These are the same hexes as the brutalist kit (`BrutalLightKit`) and the light appearance of the iOS asset catalog — change one, change all three.

### Typography

| Family | Font | Use |
|---|---|---|
| `PBSans` | **Space Grotesk** (Light → Bold, 5 weights) | All UI — headings and body |
| `PBScript` | **Pinyon Script** | The wordmark only, never body text (stands in for iOS `SnellRoundhand-Black`) |
| `FontFamily.Serif` | system | Editorial display / headline styles |
| `FontFamily.Monospace` | system | Eyebrows and numerics — Space Grotesk is proportional, not mono |

`PaperBoxdTypography` maps these onto Material 3 slots: `displaySmall` 34sp serif, `headlineMedium` 26sp serif, `headlineSmall` 19sp serif, `titleMedium` 16sp `PBSans`, body 16/14/12sp `PBSans`, and eyebrow labels at 10sp mono with 2sp tracking (uppercased at the call site).

### Shared components

`AuthChrome`, `AvatarImage`, `BookCoverImage`, `BookCoverColumns`, `BookShelfHero` (the auth screens' shelf of cloth-and-foil spines), `BrutalLightKit` (the paper-and-ink kit every screen draws from), `CelebrationOverlay`, `DarkTextField`, `GallerySaver`, `HorizontalCarousel`, `ImageCropper`, `LegalBottomSheet`, `PrimaryButton`, `RatingPicker`, `ReportDialog`, `ShimmerBox`, `SignalPill`.

---

## Domain Models

Gson data classes in `domain/model/`, with `@SerializedName` for every snake_case wire field:

| File | Represents |
|---|---|
| `User.kt` | Auth user — id, username, email, avatarUrl, level, xp, onboardingCompleted |
| `UserProfile.kt` | Full public profile — bio, stats, pronouns, links |
| `Book.kt` | Book metadata — title, authors, cover, isbn, genres, pages |
| `BookDetailExtras.kt` | Friends reading, reviews |
| `CurrentlyReading.kt` | Progress — current page, percentage |
| `DiaryEntry.kt` | Diary entry — content, book ref, date, likes |
| `ReadingList.kt` | List — id, title, books, privacy |
| `ReaderProgress.kt` | XP standing for the avatar ring and level sheet |
| `ReadingActivity.kt` | Per-day reading counts (heatmap) |
| `ActivityItem.kt` | Activity-feed event |
| `LeaderboardEntry.kt` | Rank, user, score |
| `Onboarding.kt` | Genres, preferences |
| `Recommendation.kt` | Recommended book + reason, `reason_type`, confidence; `FeedModule` / `FeedResponse` |
| `Scan.kt` | `ScanAnalyzeResponse`, `ScanBook`, `ScanScore`, `ScanDimensions`, `ScanSources` |
| `Vibe.kt` | Vibe matches and the Jazy concierge response (question or deck) |
| `Wrapped.kt` | The monthly Wrapped payload |
| `Fusion.kt` | `FusionInvitePreview`, `FusionList`, `FusionStory` — mirrors the Go JSON |

---

## Endpoints Reference

Declared in `data/remote/ApiService.kt` — one file to diff against `MOBILE_API.md`.

| Group | Paths |
|---|---|
| Health | `GET /api/health` |
| Mobile auth | `POST /api/mobile/auth/{login,register,otp/send,otp/verify,google,refresh}` — no Apple; Android doesn't need it |
| Mobile user | `PATCH /api/mobile/users/me` |
| Auth utilities | `GET /api/v1/auth/check-username`, `POST /api/v1/auth/forgot-password` |
| Onboarding | `POST /api/v1/users/me/onboarding`, `GET /api/v1/users/suggested`, multipart `…/avatar/upload`, `…/banner/upload` |
| App open | `POST /api/v1/users/me/daily-open` |
| Books | `GET /api/v1/books/{id}`, `/search`, `/latest`, `/random`, `/by-author`, `POST\|DELETE …/like`, `…/social`, `…/reviews`, `…/reviews/friends`, `…/friends-reading` |
| Authors | `GET /api/v1/authors/info` |
| Vibe search & Jazy | `POST /api/v1/search/vibe`, `POST /api/v1/jazy` |
| Bookshelf | `POST\|DELETE\|PATCH /api/v1/users/{u}/bookshelf[/{bookId}]`, `…/status`, `GET\|PUT …/progress` |
| Profile | `GET\|PUT /api/v1/users/{u}` and `/followers`, `/following`, `POST\|DELETE /follow`, `/likes`, `/tbr`, `/authors`, `/favorites`, `/streak`, `/lists`, `/reading/last`, `/reading/activity` |
| User search | `GET /api/v1/users/search` |
| Privacy & moderation | `PATCH /api/v1/users/me/visibility`, `GET\|POST\|DELETE /api/v1/users/me/follow-requests[/{u}]`, `POST\|DELETE /api/v1/users/{u}/block`, `POST /api/v1/reports` |
| Diary | `GET\|POST /api/v1/users/{u}/diary`, `GET\|DELETE …/{entryId}`, `POST\|DELETE …/{entryId}/like` |
| Activity | `GET /api/v1/activities/following`, `GET /api/v1/community` |
| Leaderboard | `GET /api/v1/leaderboard/{global,friends,dimension/{d}}`, `/api/v1/users/me/leaderboard-stats` |
| Recommendations | `GET /api/v1/recommendations/home`, `…/feed?tz=`, `…/similar/{bookId}`, `POST …/feedback` |
| Scan | `POST /api/v1/scan/analyze` |
| Wrapped | `GET /api/v1/users/me/wrapped?month=&tz=` |
| Fusion | `GET /api/v1/fusions`, `POST\|DELETE\|GET /api/v1/fusions/invites[/{token}]`, `POST …/{token}/accept`, `GET\|DELETE /api/v1/fusions/{id}` |
| Events | `POST /api/v1/events` |

Full contract: `MOBILE_API.md` in the backend repo.

---

## Manifest, Permissions & Deep Links

**Permissions:** `INTERNET`, `ACCESS_NETWORK_STATE`, `CAMERA`, and `WRITE_EXTERNAL_STORAGE` capped at `maxSdkVersion="28"` — saving a share card to `MediaStore` is permission-free from API 29, so only older devices ever see it. `android.hardware.camera` is declared `required="false"`.

**Deep links:** `MainActivity` claims `https://paperboxd.in/b/…` (books), `/u/…` (profiles) and `/fusion/…` (Fusion invites) with `android:autoVerify="true"` — they open in the app when the Digital Asset Links file on the domain matches the release signing certificate. A second filter claims `paperboxd://fusion/…` for the web join page's "Open in the app" button, which works even while App Links are unverified.

**FileProvider:** `${applicationId}.fileprovider`, paths in `res/xml/file_paths.xml`, serving rendered share cards from `cacheDir/shares` to the system share sheet.

**Window:** `enableEdgeToEdge()` in `MainActivity`, `windowSoftInputMode="adjustResize"` so the diary composer isn't covered by the IME.

---

## Dependencies

| Area | Library | Version |
|---|---|---|
| UI | Compose BOM | `2024.12.01` |
| | Material 3, `material-icons-extended` | via BOM |
| | `androidx.activity:activity-compose` | 1.9.3 |
| | `androidx.core:core-ktx` | 1.15.0 |
| Navigation | `navigation-compose` | 2.7.6 |
| Lifecycle | `lifecycle-viewmodel-compose`, `lifecycle-runtime-compose` | 2.7.0 |
| DI | Hilt (`hilt-android`, `hilt-compiler` via kapt) | 2.50 |
| | `androidx.hilt:hilt-navigation-compose` | 1.1.0 |
| Networking | Retrofit + `converter-gson` | 2.9.0 |
| | OkHttp `logging-interceptor` | 4.12.0 |
| Images | Coil `coil-compose` | 2.5.0 |
| Secure storage | `androidx.security:security-crypto` | 1.1.0-alpha06 |
| Google auth | `androidx.credentials` + `credentials-play-services-auth` | 1.3.0 |
| | `com.google.android.libraries.identity.googleid:googleid` | 1.1.1 |
| Concurrency | `kotlinx-coroutines-android` | 1.7.3 |
| Prefs | `datastore-preferences` | 1.0.0 |
| Accompanist | `pager`, `permissions` | 0.32.0 |
| QR | ZXing `core` | 3.5.3 |
| Camera | CameraX `camera2` / `lifecycle` / `view` | 1.4.2 |
| Barcode | ML Kit `barcode-scanning` | 17.3.0 |

> CameraX and ML Kit versions are pinned at or above the first releases shipping 16 KB page-size-aligned native libraries. **Do not downgrade** — Play will reject the bundle.

`security-crypto` is on an alpha because it's the only line with a `MasterKey` API that works on current AGP; revisit when 1.1.0 stabilizes.

---

## Conventions

- **Package name needs backticks.** `in` is a Kotlin keyword, so every file starts with a backticked `in` in its package declaration, and imports use `` `in` `` too. It's ugly and it's load-bearing.
- **Repositories return `Result<T>`.** Never let an exception reach a composable.
- **ViewModels expose `StateFlow<UiState<T>>`.** Collected with `collectAsState()`; no `LiveData`, no mutable state escaping the VM.
- **Errors reach the reader.** Critical mobile flows (auth, shelving, scan, diary writes) surface failures in the UI. Best-effort silent `catch` is a bug, not a style choice.
- **Endpoints live in `ApiService`**, never inline at a call site.
- **Comments name the iOS twin.** When a class mirrors a Swift counterpart, the KDoc says so. That's how parity gets maintained across two codebases.
- **Divergences are documented.** The dock, the tonal-pill indicator, and Credential Manager are deliberate Android-native choices, each with a comment explaining why.

---

## Parity With iOS

| Concept | iOS | Android |
|---|---|---|
| Root state | `AppScreen` enum + `AppState` | `AppDestination` sealed class + `AppState` |
| Session expiry | `.paperboxdSessionExpired` notification | `SessionEvents.expired` `SharedFlow` |
| Secure storage | Keychain (`KeychainManager`) | `EncryptedSharedPreferences` (`SecurePrefs`) — same two keys |
| HTTP client | `URLSession` in an `actor` | Retrofit + OkHttp interceptors |
| Error type | `APIError` enum | `ApiError` sealed class — identical code mapping |
| Tab container | `MainTabView` (glass dock / iOS 26 native) | `MainScaffold` (white tonal-pill dock) |
| Per-tab stacks | `NavigationStack` per tab | one `NavHost` + `saveState`/`restoreState` |
| Scan games | SpriteKit scenes | Compose `Canvas`, fixed 60 Hz timestep |
| Celebrations | `CelebrationOverlayView` overlay | `CelebrationOverlayHost` in the root `Box` |
| Google auth | ASWebAuthenticationSession + PKCE | Credential Manager + `GetSignInWithGoogleOption` |
| Pip destination | `JazyView` `fullScreenCover` | `JazyScreen` overlay |
| Rec verdicts | `.contextMenu` on carousel cards | Long-press `DropdownMenu` on "picked for you" |
| Wrapped player | `WrappedView` (SwiftUI) | `WrappedStoryScreen` (Compose) |
| Fusion | `FusionRootView` full-screen cover, `AppState.fusionRoute` | `FusionFlow` above the dock, `AppState.fusionRoute` + `LocalFusionRouting` |
| Fusion links | `applinks:paperboxd.in` entitlement + `paperboxd://` | verified `/fusion/` App Link + `paperboxd://fusion` |
| Moderation | `ModerationService` | `UserRepository.block/unblock/report` + `ReportDialog` |
| Analytics | `Analytics.track` | `AnalyticsRepository.track` |
| Save share card | system share sheet | share sheet + `GallerySaver` |

---

## Known Gaps

| Gap | State |
|---|---|
| **Release keystore** | `keystore.properties` still holds `CHANGE_ME` placeholders — a real upload key must be generated before a Play release, and its SHA-256 published in `assetlinks.json` for deep links. **Ship blocker.** |
| **App Links unverified in prod** | `assetlinks.json` still has placeholder fingerprints, so `https://paperboxd.in/fusion/…` opens the browser on a fresh install; the `paperboxd://` button covers it meanwhile. Fixed by the keystore row above. |
| **Push notifications** | The backend accepts FCM tokens; the app doesn't request `POST_NOTIFICATIONS` or register one, and no sender ships server-side yet. |
| **Taste dashboard** | `GET /api/v1/users/me/taste` is web-only. |
| **Recommendation reason chips** | Verdict only on long-press; reason codes are web-only. |
| **Feed module subtitles** | Not drawn — the carousel header has no slot for a second line. |

---

## Troubleshooting

| Symptom | Cause / fix |
|---|---|
| `GOOGLE_WEB_CLIENT_ID` is empty at runtime | `local.properties` is missing the key, or Gradle wasn't re-synced after adding it. The build falls back to `""` rather than failing. |
| Google Sign-In returns `Failure` immediately | No Play Services on the device/emulator, or the client ID is an Android-type instead of Web-type. |
| Backend rejects a valid Google token | The Web client ID isn't in the backend's `GOOGLE_OAUTH_ALLOWED_AUDIENCES`. |
| Hilt errors after adding an `@Inject` | kapt stub staleness — `./gradlew clean` then rebuild. |
| Stuck on the splash | The 4 600 ms floor is intentional. If it never resolves, the health probe is hanging — check `Config.BASE_URL`. |
| Immediately bounced to login | `/api/mobile/auth/refresh` returned 401; the token expired or `JWT_SECRET` changed on the backend. |
| Play rejects the bundle for page alignment | A CameraX or ML Kit dependency got downgraded below 1.4.0 / 17.3.0. |
| Scan returns 403 | `scans_exhausted` — the free quota is used up. Expected, and has its own layout. |
| Camera tap in Jazy asks for books | `JazyTasteGate` — fewer than five books on shelf + TBR. By design. |
| `assembleRelease` produces an unsigned APK | `keystore.properties` is missing or empty. |
| Dialogs or sheets render with a dark tint | Something built its own `darkColorScheme` or read `isSystemInDarkTheme()`. The app theme is light-only. |
| Deep links open the browser instead of the app | Digital Asset Links verification failed — the release signing cert fingerprint doesn't match the one published on `paperboxd.in`. For Fusion, the join page's "Open in the app" (`paperboxd://`) still works. |
| Fusion Agree/Split pages show genres instead of taste axes | Trait backfill hasn't run on the backend — expected until it does. |

---

## Related Repositories

| Repository | Description | Stack |
|---|---|---|
| `paperboxd-backend` | REST API server | Go 1.25, PostgreSQL 16, Redis 7 |
| `paperboxd` | Web frontend | Next.js 15, React 19, TypeScript 5 |
| `paperboxd-ios` | Native iOS app | SwiftUI, Swift 5 |
| `analytics-paperboxd` | Internal analytics dashboard | Next.js, Tailwind |
| `Paperboxd design elements` | Design system & UI specs | CSS tokens, HTML prototypes |

---

## Contact

**Email:** contact@paperboxd.in  
**Developer:** Hridyesh · hridyesh@paperboxd.in  
**Website:** [paperboxd.in](https://paperboxd.in)
