# Android ↔ iOS Parity — Work Handoff

_Session of 2026-08-09. Goal: bring paperboxd-android to feature parity with paperboxd-ios._

## Repos

| | Path |
|---|---|
| Android (Kotlin/Compose) | `~/Desktop/everything/Development/Projects/paperboxd-android` |
| iOS (Swift/SwiftUI) | `~/Desktop/everything/Development/Projects/paperboxd-ios` |
| Backend (Go) | `~/Desktop/everything/Development/Projects/paperboxd-backend` |

Build/verify: `cd paperboxd-android && ./gradlew assembleDebug --offline`

**Constraint: never `git commit` or `git push`.** Leave finished work in the working tree and report it.

## Headline finding

Android is **not** far behind. 100 Kotlin files / 22.1k LOC vs iOS 114 Swift / 21.6k LOC, and Android
matches or exceeds iOS in Jazy, Scan, Settings, Book Share, and Home. Eight items were audited:
**six are fixed, two were bogus** (Phase 3 and Phase 6 — both dead code on iOS).

All phases are compile-verified only (`./gradlew assembleDebug --offline`). No emulator or device
has run any of this; `adb` is not installed on this machine.

Android is **ahead** of iOS in places — don't "fix" these:
- `BookShareSheet.kt` (528 LOC vs iOS 318) — has Instagram Stories + save-to-gallery
- Settings — adds Change Password; delete-account exit survey at parity
- Home ships a live currently-reading card that iOS does not

---

## Phase status

### ✅ Phase 1+2 — Page progress (DONE, builds)

`ui/screens/bookdetail/BookDetailScreen.kt`, `PageProgressCard` (~line 812).
iOS reference: `Features/BookDetail/PageProgressView.swift`.

Four changes:
1. **Tap-to-type page.** Was `±` steppers only — logging page 247 took 247 taps. New private
   `NumberField` composable (digit-filtered, 5-char cap, accent underline, IME Done).
2. **`/ SET TOTAL`.** `total` is now `var` state, not a `val`. Previously a book with no
   `pageCount` could *never* save progress — the save button was gated on `total > 0` with no
   way to set one.
3. **Long-press ± = ±10.** `StepButton` gained `onLongStep`, uses `combinedClickable`
   (`@OptIn(ExperimentalFoundationApi::class)`). Mirrors iOS `longDelta`.
4. **Finish-date footer.** `finishLine()` parses `progress.estimated_finish_date` →
   "AT THIS PACE, FINISHED BY TUESDAY". Uses `java.time` (fine, minSdk 26).

**Deliberate divergence from iOS:** kept the `hasChanges && total > 0` save gate. iOS will
`PUT totalPages: 0` if you save before setting a total; SET TOTAL is the intended unblock, so
the guard costs nothing and prevents a junk write.

### ✅ Phase 4 — ImageCropper component (DONE, builds)

New file `ui/components/ImageCropper.kt` (~250 LOC, no new dependency).

```kotlin
ImageCropper(uri, ratio = 1f, maxDimension = 1024, onCancel = {...}, onCrop = { bytes -> ... })
```

- Renders as full-screen `Dialog(usePlatformDefaultWidth = false)` — call sites need no layout surgery
- Pan + pinch-zoom (`detectTransformGestures`), zoom floors at 1 so the image always covers the window
- `renderCrop()` is a direct port of iOS `renderCrop` math **plus bounds clamping** Android needs
  (`Bitmap.createBitmap` throws where `CGImage.cropping` returns nil)
- Decodes via **Coil**, not `BitmapFactory` — Coil applies EXIF orientation

Reference is UIKit's built-in cropper (`allowsEditing: true`), **not** iOS `Components/ImageCropper.swift`
— that Swift file is dead code (see Phase 3 below).

### ✅ Phase 4b — Wire avatar cropping (DONE, builds)

- `ui/screens/profile/EditProfileScreen.kt:~234` — picker → `cropUri` → `ImageCropper(ratio = 1f)` →
  `uploadAvatar(bytes, bytes)`. `EditProfileState.pickedAvatar` widened `Uri?` → `Any?`.
- `ui/screens/onboarding/OnboardingScreen.kt:~153` — same shape. Local var renamed
  `pickedUri: Uri?` → `pickedAvatar: Any?`.
- Preview shows the **cropped bytes** (Coil renders `ByteArray`), not the original photo.

**Deleted `readAndDownscale()`** (was `OnboardingScreen.kt:963`) — zero callers once both sites routed
through the cropper. It used `BitmapFactory.decodeByteArray`, which **ignores EXIF**: portrait photos
had been uploading sideways on Android. A comment marks the old site.

iOS evidence this gap was real:
```
EditProfileView.swift:108   ImagePicker(source: .photoLibrary) { ... }   ← allowsEditing defaults true
OnboardingSteps.swift:57    ImagePicker(source: .photoLibrary) { ... }   ← same
ImagePicker.swift:13        var allowsEditing: Bool = true
ImagePicker.swift:46        info[.editedImage] ?? info[.originalImage]
```

**Not verified on device.** All three phases above are compile-verified only — no emulator ran.
Untested: drag/pinch feel, crop framing vs preview on a real photo, Dialog presentation.

### ❌ Phase 3 — Profile banner (DROPPED — not a gap)

Originally listed as "banner upload unreachable on Android". **Wrong.** iOS has no banner either:

```
ProfileHeaderView.swift:9    let bannerCovers: [String]        ← favourite BOOK COVERS, not an image
ProfileView.swift:294        bannerCoverURLs = vm.favoriteBooks.compactMap { $0.coverURL }
ProfileHeaderView.swift:16   var onEditBanner: (() -> Void)? = nil   ← only occurrence; never invoked
```

`showBannerPicker` can never become true → the `.sheet` never presents → `ImageCropper.swift` and
`uploadBanner` are dead on iOS. `UserProfile.bannerURL` is decoded but never rendered.

Android's unwired `ProfileViewModel.uploadBanner()` is therefore **matching** iOS, not lagging it.
Leave it alone.

### ✅ Phase 5 — Share Profile: save-to-gallery (DONE, builds)

Reachability confirmed on iOS first — the Download button is live:
`ProfileHeaderView onShare` → `ProfileView.swift:234` → `shareProfile()` → `:137 showShareProfile = true`
→ `:81 .sheet` → `ShareProfileSheet` Download tile → `saveCardToPhotos()`. Real gap.

`ui/screens/profile/ShareProfileSheet.kt`:
- `rememberGraphicsLayer()` + `drawWithContent { record; drawLayer }` on the `QrCard` call —
  same rasterize-what-you-see trick as `BookShareSheet`, so no offscreen re-render is needed
- Third `ActionTile` (`Icons.Outlined.Download`) → `renderCard()` → `saveToGallery()`.
  Toasts match iOS: "Saved to Photos" / "Couldn't render card", plus "Couldn't save image"
- `saveToGallery` in `BookShareSheet.kt` widened `private` → `internal` and reused as-is —
  no new file, no second MediaStore implementation

`renderCard()` flattens the layer onto `QrBg` before saving. Two reasons, both non-obvious:
the layer is transparent outside the card's rounded corners and offset shadow (gallery apps
render that on black, swallowing the dark ink artwork), and a HARDWARE-config bitmap — which
`toImageBitmap()` may return — cannot be drawn into a software `Canvas`, so it is copied to
ARGB_8888 first.

Skipped "Customise card" (iOS only shows a "coming soon" toast).

### ❌ Phase 6 — SignalPill reason colours (DROPPED — not a gap)

Same mis-scope as Phase 3. **The pill is dead on both platforms.**

```
Android  ui/components/SignalPill.kt        ← zero call sites in the whole app
iOS      Components/SignalPillView.swift    ← one consumer: BookCardView.swift:30
iOS      Features/Home/BookCardView.swift   ← zero references outside its own file
```

`BookCardView` is already on this doc's confirmed-dead list, so its use of `SignalPillView`
proves nothing. The **live** reason renderer on both platforms is the onboarding aha screen, and
it is already at parity — plain 13.5pt grey centred text, no pill, no colour:
`OnboardingAhaViews.swift:124` vs `OnboardingScreen.kt:929`.

Adding `reasonType` to `SignalPill` would have added a parameter to a composable nobody calls.

### ✅ Phase 7 — Network reachability gate on Scan (DONE, builds)

`ui/screens/scan/ScanScreen.kt` — private `rememberIsOnline()` composable, plus
`ACCESS_NETWORK_STATE` in the manifest (required for network callbacks).

- Camera is gated: `if (cameraPermission.status.isGranted && isOnline)`, and `showBrackets`
  gains `&& isOnline`
- Offline `FallbackCard` (`Icons.Outlined.WifiOff`) takes priority over the ISBN card, matching
  the iOS branch order at `ScanScreen.swift:55`. Copy copied verbatim from iOS
- `!isOnline` is checked **first** in the `when`, so a scan can't be confirmed offline

Three deliberate divergences from a naive `NWPathMonitor` port:
1. **Every callback edge re-reads `cm.activeNetwork`** rather than tracking `Network` handles.
   A Wi-Fi→cellular handover fires `onLost` for a network that is no longer the default; handle
   tracking would read that as offline while traffic is flowing.
2. **Requires `NET_CAPABILITY_VALIDATED` as well as `NET_CAPABILITY_INTERNET`** — a captive
   portal is exactly the case where `/scan/analyze` fails, so it should count as offline.
3. **Fails open.** Null `ConnectivityManager` or a throwing `registerDefaultNetworkCallback`
   leaves `online = true`. Never block the camera because reachability itself broke.

iOS assumes `isOnline = true` until the first path callback lands; Android reads
`getNetworkCapabilities` synchronously at registration, so the first frame is already accurate.

It is a composable, not a singleton, because Scan is the only consumer on **either** platform.

### ✅ Phase 8 — Deep links declared but never handled (DONE, builds)

**The interesting part is not the wiring — it is that `/b/{slug}` is not a book ID.**

Web book pages are addressed by slug (`app/b/[slug]/page.tsx`), and the backend keeps the two
lookups on separate routes:

```
internal/handler/books.go:265   GetByID    → uuid.Parse(idStr); 400 "Invalid book ID" on failure
cmd/api/main.go:336             GetBySlug  → /books/by-slug/{slug}
```

So routing `paperboxd.in/b/the-hobbit` straight into `book/{bookId}` would have 400'd — and not
only on the book fetch: `fetchAll()` fans out seven calls (status, similar, friends, reviews,
progress…) that all take the same ID.

Fix, in dependency order:
- `AndroidManifest.xml` — filter path-scoped to `pathPrefix="/b/"` and `pathPrefix="/u/"`.
  It previously claimed *every* paperboxd.in URL, including `/privacy` and `/terms`
- `Screen.kt` — `BookDetail.DEEP_LINK` / `Profile.DEEP_LINK` next to the routes they mirror
- `NavGraph.kt` — `deepLinks = listOf(navDeepLink { ... })` on both composables. No
  `MainActivity` change: `NavController.setGraph` reads the launching activity intent itself,
  and marks it handled, so re-mounting `MainScaffold` cannot re-navigate. A deep link that
  arrives while signed out also survives the auth detour — the `NavHost` is only built once
  `AppState` reaches `Main`, and the intent is still there
- `ApiService.kt` + `BookRepository.kt` — `bookBySlug()` on `GET /books/by-slug/{slug}`
- `BookDetailViewModel.kt` — `bookId` is now `var … private set`. `fetchAll()` resolves a
  non-UUID param to the real ID **before** fanning out, so one guard covers all seven calls
  and every later mutation (`like`, `updateRating`, `toggleBookshelf`, `reportReview`)

---

## Items found while doing the above — none of these are parity gaps

### ⚠️ `assetlinks.json` still has placeholder fingerprints — blocks App Links verification

`paperboxd/public/.well-known/assetlinks.json` (the **web** repo) ships:

```json
"sha256_cert_fingerprints": [
  "REPLACE_WITH_UPLOAD_KEY_SHA256",
  "REPLACE_WITH_PLAY_APP_SIGNING_SHA256"
]
```

`autoVerify="true"` will therefore fail silently and Android falls back to a disambiguation
chooser. Deep links still *work*, they just are not seamless. Needs the real upload-key and
Play-app-signing SHA-256s before launch. Path scoping was worth doing regardless.

### ✅ Gallery saves failed on API 26–28 (FIXED, builds) — pre-existing, was hitting Book Share too

`minSdk = 26`, but the manifest declared no `WRITE_EXTERNAL_STORAGE`. Inserting into
`MediaStore.Images.Media.EXTERNAL_CONTENT_URI` only became permission-free in API 29 — the
`if (SDK_INT >= Q)` guard in `saveToGallery` handled `RELATIVE_PATH` but not the permission. On
API 26–28 the insert threw, `runCatching` swallowed it, and the user saw "Couldn't save image".
This predated Phase 5; `BookShareSheet`'s SAVE tile had the same bug.

New `ui/components/GallerySaver.kt`:
- `<uses-permission … WRITE_EXTERNAL_STORAGE android:maxSdkVersion="28" />` — capped at 28 so
  API 29+ never carries a permission it does not need, and Play does not flag it
- `rememberGallerySaver(onResult: (String) -> Unit): (Bitmap) -> Unit` — requests the grant only
  when `SDK_INT < Q`, holds the bitmap in `pending` across the dialog (the grant resolves long
  after the tile's click handler returns), and reports the toast string back
- Toast copy moved in with it, since both sheets already used identical strings

**`saveToGallery` is now `private` inside that file.** That is the actual fix: previously it was
callable directly, and both call sites called it directly, so each one had to remember the
permission rule independently — and neither did. There is now no way to reach MediaStore except
through the permission-aware path, so a third save button cannot reintroduce this.

`BookShareSheet.kt` lost its copy of `saveToGallery` (and the `ContentValues` / `Environment` /
`MediaStore` / `Build` imports); both sheets now do `val saveCard = rememberGallerySaver { toast = it }`.

Not handled: a *permanent* denial just re-reports "Storage access needed to save" with no
"Open Settings" escape hatch like `ScanScreen` has. A save tile is retryable, so this seemed the
wrong place to spend UI. Marked with a `ponytail:` comment in the file.

### ℹ️ `/u/{username}/lists/{listId}` is captured but has no Android screen

`pathPrefix="/u/"` also matches the web list route. Android's PatternMatcher glob cannot exclude
a `/`, so the filter cannot be narrowed further. The `navDeepLink` pattern will not match, so
such a link opens the app on Home. The app never generates these URLs itself (`ShareProfileSheet`
emits `/u/{username}`, `BookShareSheet` emits `/b/{slug}`), so this is low-traffic.

---

## Method note — read this before auditing more

Two items on the original list were mis-scoped (Phase 3, and Phase 4's justification) because the
audit trusted a grep hit for `.sheet` / `.fullScreenCover` as proof a feature was live.

**A modifier existing is not proof a feature is reachable.** Trace the `@State` var that triggers it
to a call site that actually sets it. iOS carries a lot of dead code.

Phase 6 then repeated the mistake one level deeper: `SignalPillView` *is* consumed — but only by
`BookCardView`, which was already on the dead list below. **Trace the consumer's consumer too.**

Confirmed-dead on iOS (do **not** port these):
`DiaryView`, `MasonryGridView`, `BookCardView`, `CurrentlyReadingCard`, `BrutalistRefresh`,
`HorizontalCarouselView`, `LikesGridView`, `ImageCropper` (banner path), `SignalPillView`.

Android's own dead code: the `SettingsScreen` composable (the live one is `SettingsSheet`),
`PlaceholderScreen` in `NavGraph.kt:220`, and `ui/components/SignalPill.kt` (zero call sites).

Second lesson, from Phase 8: **a URL is not an ID.** The web, the backend and the app can each
address the same object differently — check the actual handler before routing a deep link
parameter into a call that expects a primary key.

---

## Working tree state (nothing committed)

**Changed by the parity work (phases 1–8):**

| File | Phase |
|---|---|
| `ui/screens/bookdetail/BookDetailScreen.kt` | 1+2 |
| `ui/components/ImageCropper.kt` *(new, untracked)* | 4 |
| `ui/screens/onboarding/OnboardingScreen.kt` | 4b |
| `ui/screens/profile/EditProfileScreen.kt` | 4b |
| `ui/screens/profile/ShareProfileSheet.kt` | 5 |
| `ui/components/GallerySaver.kt` *(new, untracked)* | API 26–28 storage fix |
| `ui/screens/bookdetail/BookShareSheet.kt` | 5 + storage fix (`saveToGallery` moved out) |
| `ui/screens/scan/ScanScreen.kt` | 7 |
| `AndroidManifest.xml` | 7 (`ACCESS_NETWORK_STATE`) + 8 (path scoping) + storage fix |
| `ui/navigation/Screen.kt` | 8 |
| `ui/navigation/NavGraph.kt` | 8 |
| `data/repository/BookRepository.kt` | 8 |
| `ui/screens/bookdetail/BookDetailViewModel.kt` | 8 |
| `data/remote/ApiService.kt` | 8 — **also carries someone else's earlier edits** |
| `PARITY_HANDOFF.md` *(this file, untracked)* | — |

Nothing is committed, per the standing constraint.

**Already modified before this session — not mine, don't attribute or revert:**
`config/Config.kt`, `data/remote/ApiClient.kt`, `data/repository/AuthRepository.kt`,
`ui/screens/settings/SettingsViewModel.kt`, `res/drawable/app_icon.png`,
`play-store-icon.png` (untracked). `data/remote/ApiService.kt` was in this set and now also has
the Phase 8 `bookBySlug` endpoint appended — split it if you commit selectively.

## What to verify on a device first

Nothing below has run outside the compiler:

1. **Phase 5 Download** — that the saved PNG is the card and not a transparent/black rectangle
   (the HARDWARE-bitmap and flatten paths are both untested).
2. **The storage fix, on an API 26–28 emulator specifically** — that is the whole point of it, and
   an API 29+ device exercises none of the new code. Check both tiles (Book Share SAVE, Share
   Profile Download): permission dialog on first tap, card actually lands in Pictures/PaperBoxd,
   and a denial reports the toast rather than hanging on the held bitmap.
3. **Phase 7** — toggle airplane mode with Scan open; the camera should stop and the offline card
   appear, then recover. Check `VALIDATED` does not flap on a normal Wi-Fi network.
4. **Phase 8** — `adb shell am start -a android.intent.action.VIEW -d "https://paperboxd.in/b/<slug>"`
   and the `/u/<username>` twin. Test signed-out too: the link should survive the auth screen.
   (`adb` is not installed on this machine — that is why none of this was run.)
5. **Phases 1–4b** — still unverified from the earlier session: drag/pinch feel in the cropper,
   crop framing vs preview on a real photo, page-progress `NumberField` IME behaviour.

iOS repo also has uncommitted AppIcon work and an untracked `RELEASE_READINESS.md` — that file is a
useful snapshot of iOS's own launch blockers (empty app icon, bundle ID mismatch, placeholder legal text).
