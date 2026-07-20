# PaperBoxd for Android

> *Your reading universe, in your pocket.*

The native Jetpack Compose client for [PaperBoxd](https://paperboxd.in) — a social book-tracking platform inspired by the simplicity and community spirit of Letterboxd, but built exclusively for books.

**Website:** [paperboxd.in](https://paperboxd.in) · **API:** [api.paperboxd.com](https://api.paperboxd.com) · **Contact:** paperboxd@gmail.com

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
- [Scan & Know](#scan--know)
- [Design System](#design-system)
- [Domain Models](#domain-models)
- [Endpoints Reference](#endpoints-reference)
- [Manifest, Permissions & Deep Links](#manifest-permissions--deep-links)
- [Dependencies](#dependencies)
- [Conventions](#conventions)
- [Parity With iOS](#parity-with-ios)
- [Troubleshooting](#troubleshooting)
- [Related Repositories](#related-repositories)

---

## Overview

This is the Android surface of PaperBoxd. It's a port of the SwiftUI app, not an independent design — screen-for-screen parity is a goal, and the source is full of "iOS twin" comments naming the counterpart. Where a platform convention argues otherwise, Android wins and the divergence is documented in a comment (the dock is the main example).

It talks to the Go backend over REST/JSON with a long-lived JWT — no cookies, no session state, no local database. Everything on screen is fetched live or read from an encrypted preferences file holding two keys: the JWT and the session `User`.

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

### 2. Build and run

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
│   ├── drawable/                  # app_icon, google_logo
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
    │   │   ├── ApiService.kt      # ~62 Retrofit endpoints
    │   │   ├── ApiError.kt        # Typed errors + envelope parsing
    │   │   └── SafeCall.kt        # suspend block → Result<T>
    │   └── repository/            # Auth · Book · Diary · Recommendation · User
    ├── di/AppModule.kt            # App-wide Hilt bindings
    ├── domain/model/              # 13 Gson wire models
    └── ui/
        ├── UiState.kt
        ├── components/            # Shared composables (14 files)
        ├── navigation/            # NavGraph, Screen, BottomNavBar, PipScanButton
        ├── screens/               # auth · bookdetail · diary · home · leaderboard
        │                          # onboarding · profile · scan · search · settings
        │                          # splash · write
        └── theme/                 # Color, Type, Fonts, Theme
```

### Repositories

| Repository | Suspend functions | Covers |
|---|---|---|
| `AuthRepository` | 13 | Login, register, OTP, Google, refresh, health, session persistence, username check, password reset |
| `BookRepository` | 18 | Search, detail, like, shelve, status, progress, reviews, friends-reading, vibe search, scan analyze |
| `UserRepository` | 21 | Profile, follow graph, bookshelf, TBR, likes, favorites, authors, streak, lists, reading activity, onboarding, avatar/banner upload, leaderboard stats |
| `DiaryRepository` | 6 | List, create, detail, delete, like/unlike |
| `RecommendationRepository` | 3 | Home feed, similar books, event tracking |

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
5. **Hold the splash** for a minimum of 2 500 ms so a warm start doesn't flash three screens.

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
| `edit-profile` | `EditProfileScreen` |
| `diary-entry/{username}/{entryId}` | `DiaryEntryDetailScreen` |

**The dock diverges from iOS on purpose.** `BottomNavBar` is a solid white rounded pill with side margins, icon-only tabs, and a Sienna tonal-pill active indicator — variant "A · tonal pill" from *Bottom Dock.html · 03 Android redesign*. It is not a glass dock. The dock and the floating `PipScanButton` render only on tab roots; detail screens are full-bleed.

**Modal overlays** live in the same `Box` as the `NavHost`, gated by `rememberSaveable` booleans rather than nav routes — they survive rotation but never enter the back stack:

- `WriteScreen` — diary composer
- `ScanFlowScreen` — Scan & Know
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

`ApiService` declares ~62 endpoints across health, mobile auth, onboarding, books, bookshelf, profile, diary, activity, leaderboard, recommendations, vibe search, events, and scan.

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
Brutalist wall — book grid, currently-reading card, activity from people you follow, and a notifications bottom sheet.

### Search
Debounced book search (backend-first, ~10–50 ms from PostgreSQL, falling back to Google Books) plus user search, in one screen.

### Book Detail
Cover, metadata, and the action row: shelve / like / rate / review / track progress. Below: friends reading, friends' reviews, all reviews, and similar books. Rating and review write through a single `PATCH …/bookshelf/{bookId}`; page progress through `PUT …/bookshelf/{bookId}/progress`. `BookShareSheet` renders a share card.

### Profile
Header with avatar and banner, stats, follow button, and section grids for Bookshelf, Favorites, TBR, Likes, Authors, Lists, and Diary. `ReadingHeatmap` draws a year of `…/reading/activity?year=` on a Compose `Canvas`. `FollowListSheet` shows followers/following. `ShareProfileSheet` generates a QR code (ZXing) to the public profile URL.

### Diary
Entry list, detail, and likes. `WriteScreen` composes an entry — pick a book, pick a date, set a rating, write.

### Leaderboard
Global, friends-only, and per-dimension boards, plus the viewer's own stats.

### Onboarding
Post-registration flow: claim a username (live availability check), pick genres, upload an avatar, then an "aha" stage that loads real recommendations before entering the app.

### Settings
Free-scan quota, share the app, legal (privacy policy and terms rendered from `assets/legal/*.md` in `LegalBottomSheet`), Goodreads import, sign out, and account deletion.

### Goodreads import
`GoodreadsImportSheet` + VM parse a Goodreads CSV export on-device: for each row, look the book up by ISBN first, then title + author, then add the match to the shelf with the mapped status.

---

## Scan & Know

Point the camera at a book's barcode; get a personalized 0–100 compatibility score.

**Flow** (`ScanFlowScreen`, a full-screen overlay from the Pip button):

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

**Quota.** Scans are limited; the remaining count is persisted by `ScanPrefs` and shown in Settings. The endpoint's only 403 is `scans_exhausted`, which drives a dedicated exhausted layout.

**Camera stack:** CameraX 1.4.2 (`camera-camera2`, `camera-lifecycle`, `camera-view`) + ML Kit barcode-scanning 17.3.0. Those exact versions matter — 1.4.0+ / 17.3.0+ are the first to ship **16 KB page-size-aligned `.so` files**, which Google Play now requires. Permission is requested at point of use via Accompanist Permissions; `CAMERA` is declared `required="false"` so the app still installs on camera-less devices.

---

## Design System

Dark only — no light variant, no Material You dynamic color. `PaperBoxdTheme` wraps a single `darkColorScheme`.

| Token | Hex | Role |
|---|---|---|
| `Background` | `#0A0A0A` | App background |
| `Surface` | `#141414` | Cards, elevated surfaces |
| `Border` | `#2A2A2A` | Hairlines, dividers |
| `TextPrimary` | `#F5F5F5` | Body and headings |
| `TextSecondary` | `#A0A0A0` | Muted / eyebrow text |
| `Accent` | `#E8D5B7` | Warm paper accent |
| `Error` | `#E05252` | Destructive |
| `Terracotta` → `TerracottaDeep` | `#D97757` → `#6B3520` | `AvatarGradient` ring |
| `LikeRed` | `#D72830` | Active "liked" heart |

These mirror the iOS asset catalog exactly — change one, change both.

### Typography

| Family | Font | Use |
|---|---|---|
| `PBSans` | **Space Grotesk** (Light → Bold, 5 weights) | All UI — headings and body |
| `PBScript` | **Pinyon Script** | The wordmark only, never body text (stands in for iOS `SnellRoundhand-Black`) |
| `FontFamily.Serif` | system | Editorial display / headline styles |
| `FontFamily.Monospace` | system | Eyebrows and numerics — Space Grotesk is proportional, not mono |

`PaperBoxdTypography` maps these onto Material 3 slots: `displaySmall` 34sp serif, `headlineMedium` 26sp serif, `headlineSmall` 19sp serif, `titleMedium` 16sp `PBSans`, body 16/14/12sp `PBSans`, and eyebrow labels at 10sp mono with 2sp tracking (uppercased at the call site).

### Shared components

`AuthChrome`, `AvatarImage`, `BookCoverImage`, `BookCoverColumns`, `BrutalLightKit` (light-mode token kit for the brutalist screens), `CelebrationOverlay`, `DarkTextField`, `HorizontalCarousel`, `LegalBottomSheet`, `PrimaryButton`, `RatingPicker`, `ShimmerBox`, `SignalPill`.

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
| `ReadingActivity.kt` | Per-day reading counts (heatmap) |
| `ActivityItem.kt` | Activity-feed event |
| `LeaderboardEntry.kt` | Rank, user, score |
| `Onboarding.kt` | Genres, preferences |
| `Recommendation.kt` | Recommended book + reason |
| `Scan.kt` | `ScanAnalyzeResponse`, `ScanBook`, `ScanScore`, `ScanDimensions`, `ScanSources` |

---

## Endpoints Reference

Declared in `data/remote/ApiService.kt` — one file to diff against `MOBILE_API.md`.

| Group | Paths |
|---|---|
| Health | `GET /api/health` |
| Mobile auth | `POST /api/mobile/auth/{login,register,otp/send,otp/verify,google,refresh}` |
| Mobile user | `PATCH /api/mobile/users/me` |
| Auth utilities | `GET /api/v1/auth/check-username`, `POST /api/v1/auth/forgot-password` |
| Onboarding | `POST /api/v1/users/me/onboarding`, multipart `…/avatar/upload`, `…/banner/upload` |
| Books | `GET /api/v1/books/{id}`, `/search`, `/latest`, `/random`, `POST\|DELETE …/like`, `…/reviews`, `…/reviews/friends`, `…/friends-reading` |
| Vibe search | `POST /api/v1/search/vibe` |
| Bookshelf | `POST\|DELETE\|PATCH /api/v1/users/{u}/bookshelf[/{bookId}]`, `…/status`, `GET\|PUT …/progress` |
| Profile | `GET\|PUT /api/v1/users/{u}` and `/followers`, `/following`, `POST\|DELETE /follow`, `/likes`, `/tbr`, `/authors`, `/favorites`, `/streak`, `/lists`, `/reading/last`, `/reading/activity` |
| User search | `GET /api/v1/users/search` |
| Diary | `GET\|POST /api/v1/users/{u}/diary`, `GET\|DELETE …/{entryId}`, `POST\|DELETE …/{entryId}/like` |
| Activity | `GET /api/v1/activities/following` |
| Leaderboard | `GET /api/v1/leaderboard/{global,friends,dimension/{d}}`, `/api/v1/users/me/leaderboard-stats` |
| Recommendations | `GET /api/v1/recommendations/home`, `…/similar/{bookId}` |
| Scan | `POST /api/v1/scan/analyze` |
| Events | `POST /api/v1/events` |

Full contract: `MOBILE_API.md` in the backend repo.

---

## Manifest, Permissions & Deep Links

**Permissions:** `INTERNET`, `CAMERA`. `android.hardware.camera` is declared `required="false"`.

**Deep links:** `MainActivity` claims `https://paperboxd.in` with `android:autoVerify="true"` — book and profile pages open in the app when the Digital Asset Links file on the domain matches the release signing certificate.

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

---

## Troubleshooting

| Symptom | Cause / fix |
|---|---|
| `GOOGLE_WEB_CLIENT_ID` is empty at runtime | `local.properties` is missing the key, or Gradle wasn't re-synced after adding it. The build falls back to `""` rather than failing. |
| Google Sign-In returns `Failure` immediately | No Play Services on the device/emulator, or the client ID is an Android-type instead of Web-type. |
| Backend rejects a valid Google token | The Web client ID isn't in the backend's `GOOGLE_OAUTH_ALLOWED_AUDIENCES`. |
| Hilt errors after adding an `@Inject` | kapt stub staleness — `./gradlew clean` then rebuild. |
| Stuck on the splash | The 2 500 ms floor is intentional. If it never resolves, the health probe is hanging — check `Config.BASE_URL`. |
| Immediately bounced to login | `/api/mobile/auth/refresh` returned 401; the token expired or `JWT_SECRET` changed on the backend. |
| Play rejects the bundle for page alignment | A CameraX or ML Kit dependency got downgraded below 1.4.0 / 17.3.0. |
| Scan returns 403 | `scans_exhausted` — the free quota is used up. Expected, and has its own layout. |
| Deep links open the browser instead of the app | Digital Asset Links verification failed — the release signing cert fingerprint doesn't match the one published on `paperboxd.in`. |

---

## Related Repositories

| Repository | Description | Stack |
|---|---|---|
| `paperboxd-backend` | REST API server | Go 1.25, PostgreSQL 16, Redis 7 |
| `paperboxd` | Web frontend | Next.js 15, React 19, TypeScript 5 |
| `paperboxd-ios` | Native iOS app | SwiftUI, Swift 5 |
| `Paperboxd design elements` | Design system & UI specs | CSS tokens, HTML prototypes |

---

## Contact

**Developer:** Hridyesh
**Email:** paperboxd@gmail.com
**Website:** [paperboxd.in](https://paperboxd.in)
