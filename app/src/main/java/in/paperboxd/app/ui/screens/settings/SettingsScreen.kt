package `in`.paperboxd.app.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalButton
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.components.hatchBrush
import `in`.paperboxd.app.ui.screens.scan.ScanPrefs
import kotlinx.coroutines.launch
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow
import `in`.paperboxd.app.ui.theme.AvatarGradient
import java.util.Locale

/**
 * iOS SettingsView twin — same sections/rows, restyled to the light brutalist
 * paper aesthetic used across the ported app. Opened from the profile hamburger
 * as a slide-up sheet, matching iOS `.sheet(isPresented: $showSettings)`.
 *
 * Rows that iOS pushes onto its NavigationStack (change password, the legal
 * pages, Goodreads import) swap in place here behind a back chip — see
 * [SettingsPage] — because a bottom sheet has no nav host of its own.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    email: String,
    onDismiss: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = HL.Paper,
        shape = RectangleShape,
        dragHandle = {
            Box(Modifier.fillMaxWidth().padding(top = 9.dp, bottom = 7.dp), Alignment.Center) {
                Box(Modifier.size(width = 44.dp, height = 4.dp).background(HL.Ink))
            }
        }
    ) {
        SettingsBody(
            email = email,
            onSignOut = onSignOut,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f),
            header = {
                Spacer(Modifier.width(62.dp))
                Spacer(Modifier.weight(1f))
                SettingsTitle()
                Spacer(Modifier.weight(1f))
                Box(Modifier.width(62.dp), contentAlignment = Alignment.CenterEnd) {
                    Box(
                        Modifier.brutalButton(onDismiss, fill = HL.Ink, borderWidth = 2.dp, offset = 3.dp)
                            .padding(horizontal = 11.dp, vertical = 6.dp)
                    ) {
                        Text(
                            "DONE",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp,
                            color = HL.Paper
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun SettingsTitle() {
    Text(
        "Settings",
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 19.sp,
        color = HL.Ink
    )
}

@Composable
private fun SettingsBody(
    email: String,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel,
    header: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var dialog by remember { mutableStateOf<SettingsDialog?>(null) }

    // iOS pushes these onto the settings NavigationStack; the sheet has no nav
    // host, so the page swaps in place behind the same back chip.
    var page by remember { mutableStateOf<SettingsPage?>(null) }
    val openPage = page
    BackHandler(enabled = openPage != null) { page = null }

    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    Box(modifier.background(HL.Paper)) {
        Column(Modifier.fillMaxSize()) {
            // Nav bar: serif title between the chips, closed by a hard ink rule.
            Column {
                Row(
                    Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (openPage == null) header() else SubPageHeader(openPage.title) { page = null }
                }
                Box(Modifier.fillMaxWidth().height(2.dp).background(HL.Ink))
            }

            if (openPage == null) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 24.dp, bottom = 44.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Section("Privacy") {
                        val isPublic by viewModel.isPublic.collectAsState()
                        val requests by viewModel.followRequests.collectAsState()

                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Outlined.Lock, null,
                                tint = HL.Ink, modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Private account",
                                    fontSize = 15.sp, fontWeight = FontWeight.Medium, color = HL.Ink
                                )
                                Text(
                                    if (isPublic) {
                                        "Anyone can see your shelves, diary and lists."
                                    } else {
                                        "Only followers you approve can see your shelves."
                                    },
                                    fontSize = 12.sp,
                                    color = HL.Muted
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            BrutalSwitch(checked = !isPublic) { viewModel.setPublic(!it) }
                        }

                        // Only meaningful while the account is private; the backend
                        // auto-accepts everyone waiting the moment it goes public.
                        if (!isPublic && requests.isNotEmpty()) {
                            Box(Modifier.fillMaxWidth().height(2.dp).background(HL.Ink))
                            Text(
                                if (requests.size == 1) "1 FOLLOW REQUEST" else "${requests.size} FOLLOW REQUESTS",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                fontSize = 9.5.sp,
                                letterSpacing = 1.8.sp,
                                color = HL.Paper,
                                modifier = Modifier.fillMaxWidth().background(HL.Ink)
                                    .padding(horizontal = 16.dp, vertical = 7.dp)
                            )
                            requests.forEach { request ->
                                RowDivider()
                                Row(
                                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 11.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        Modifier
                                            .size(32.dp)
                                            .background(AvatarGradient)
                                            .border(2.dp, HL.Ink)
                                    )
                                    Spacer(Modifier.width(11.dp))
                                    Column(Modifier.weight(1f)) {
                                        Text(
                                            request.name.ifEmpty { request.username },
                                            fontSize = 14.sp, color = HL.Ink
                                        )
                                        Text(
                                            "@${request.username}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 11.sp, color = HL.Muted
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    MiniButton("Confirm", fill = HL.Ink, label = HL.Paper) {
                                        viewModel.respondToRequest(request.username, true)
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    MiniButton("Decline", fill = HL.Paper, label = HL.Ink) {
                                        viewModel.respondToRequest(request.username, false)
                                    }
                                }
                            }
                        }
                    }

                    Section("Account") {
                        SettingsRow(Icons.Outlined.Lock, "Change Password") {
                            page = SettingsPage.ChangePassword
                        }
                    }

                    Section("Scan & Know") {
                        // Re-read whenever the sheet reopens; the scan flow writes
                        // this back to prefs. iOS gets that free from @AppStorage.
                        val remaining = remember(page) { ScanPrefs.scansRemaining(context) }
                        InfoRow(Icons.Outlined.QrCodeScanner, "Free Scans Remaining") {
                            Text(
                                if (remaining == 0) "NONE LEFT" else "$remaining LEFT",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                color = HL.Ink,
                                modifier = Modifier
                                    .background(HL.Paper2)
                                    .border(2.dp, HL.Ink)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Section("Your Data") {
                        SettingsRow(Icons.Outlined.Download, "Import from Goodreads") {
                            page = SettingsPage.Goodreads
                        }
                    }

                    Section("Discover") {
                        SettingsRow(Icons.Outlined.PersonAddAlt, "Invite Friends") {
                            val send = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, "https://paperboxd.in")
                            }
                            context.startActivity(Intent.createChooser(send, "Invite Friends"))
                        }
                        RowDivider()
                        SettingsRow(Icons.Outlined.StarBorder, "Rate PaperBoxd") {
                            dialog = SettingsDialog.Rate
                        }
                    }

                    Section("About") {
                        SettingsRow(Icons.Outlined.Policy, "Privacy Policy") { page = SettingsPage.Privacy }
                        RowDivider()
                        SettingsRow(Icons.Outlined.Description, "Terms of Service") { page = SettingsPage.Terms }
                        RowDivider()
                        InfoRow(Icons.Outlined.Info, "Version") {
                            Text(
                                version,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = HL.Muted
                            )
                        }
                    }

                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .brutalButton(onSignOut, fill = HL.Ink, borderWidth = 2.dp, offset = 5.dp)
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "SIGN OUT",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            letterSpacing = 1.4.sp,
                            color = HL.Paper
                        )
                    }

                    // Hatched, not red — the hazard reads from the stripe pattern so
                    // the palette stays monochrome.
                    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .brutalButton(
                                    { dialog = SettingsDialog.Delete },
                                    fill = HL.Paper, borderWidth = 2.dp, offset = 5.dp
                                )
                                .height(46.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Hatch(base = HL.Paper, stripe = HL.Paper2)
                            Text(
                                "DELETE ACCOUNT",
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                fontSize = 12.sp,
                                letterSpacing = 1.2.sp,
                                color = HL.Ink
                            )
                        }
                    }
                }
            } else {
                SettingsSubPage(page = openPage, email = email)
            }
        }
    }

    when (dialog) {
        SettingsDialog.Rate -> InfoDialog(
            "Rate PaperBoxd",
            "We'll enable ratings once we're live on the Play Store. Thank you for your support!"
        ) { dialog = null }
        SettingsDialog.Delete -> DeleteAccountDialog(
            onDismiss = { dialog = null },
            onDeleted = { dialog = null; onSignOut() },
            onSubmit = viewModel::deleteAccount
        )
        null -> {}
    }

}

/**
 * Three-step delete-account flow mirroring iOS DeleteAccountSheet: exit-reason
 * survey → confirm warning → goodbye. Reasons post as {reasons:[...]} to the same
 * DELETE /api/v1/users/me endpoint; sign-out fires when goodbye is acknowledged.
 */
@Composable
private fun DeleteAccountDialog(
    onDismiss: () -> Unit,
    onDeleted: () -> Unit,
    onSubmit: suspend (List<String>) -> Result<Unit>
) {
    val allReasons = remember {
        listOf(
            "I'm not using this account anymore",
            "I have privacy concerns",
            "I found a better alternative",
            "The service doesn't meet my needs",
            "I'm receiving too many notifications",
            "I want to start fresh with a new account",
            "Other",
        )
    }
    val scope = rememberCoroutineScope()
    var step by remember { mutableStateOf(0) } // 0 reason · 1 confirm · 2 goodbye
    val selected = remember { mutableStateListOf<String>() }
    var other by remember { mutableStateOf("") }
    var deleting by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val formValid = selected.isNotEmpty() &&
        !(selected.contains("Other") && other.isBlank())

    when (step) {
        0 -> BrutalDialog(
            onDismissRequest = onDismiss,
            title = "Delete Account",
            eyebrow = "Danger Zone",
            body = {
                Text(
                    "We're sorry to see you go. Please let us know why you're deleting your account.",
                    color = HL.Muted, fontSize = 13.sp
                )
                Spacer(Modifier.height(14.dp))
                Column(
                    Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    allReasons.forEach { r ->
                        val on = selected.contains(r)
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .brutalButton(
                                    {
                                        if (on) { selected.remove(r); if (r == "Other") other = "" }
                                        else selected.add(r)
                                    },
                                    fill = if (on) HL.Paper2 else HL.Paper,
                                    borderWidth = 2.dp,
                                    offset = 4.dp
                                )
                                .padding(horizontal = 13.dp, vertical = 11.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                Modifier.size(20.dp)
                                    .background(if (on) HL.Ink else HL.Paper)
                                    .border(2.dp, HL.Ink),
                                contentAlignment = Alignment.Center
                            ) {
                                if (on) Icon(
                                    Icons.Outlined.Check, null,
                                    tint = HL.Paper, modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Text(r, color = HL.Ink, fontSize = 14.5.sp)
                        }
                        if (r == "Other" && on) {
                            BasicTextField(
                                value = other,
                                onValueChange = { other = it },
                                singleLine = true,
                                textStyle = TextStyle(fontSize = 14.sp, color = HL.Ink),
                                modifier = Modifier.fillMaxWidth().padding(start = 14.dp)
                                    .background(HL.Paper2).border(2.dp, HL.Ink)
                                    .padding(horizontal = 13.dp, vertical = 11.dp),
                                decorationBox = { inner ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (other.isEmpty()) Text(
                                            "Please specify…", color = HL.Muted, fontSize = 14.sp
                                        )
                                        inner()
                                    }
                                }
                            )
                        }
                    }
                }
            },
            actions = {
                DialogButton("CANCEL", Modifier.weight(1f), fill = HL.Paper, label = HL.Ink, onClick = onDismiss)
                Spacer(Modifier.width(12.dp))
                DialogButton(
                    "CONTINUE", Modifier.weight(1f), fill = HL.Ink, label = HL.Paper,
                    enabled = formValid, hatched = true
                ) { step = 1 }
            }
        )

        1 -> BrutalDialog(
            onDismissRequest = { if (!deleting) onDismiss() },
            title = "Are you sure?",
            eyebrow = "This cannot be undone",
            body = {
                Text(
                    "Deleting your account permanently removes:",
                    color = HL.Paper, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                        .background(hatchBrush())
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                )
                Column(
                    Modifier.fillMaxWidth().border(2.dp, HL.Ink).padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(9.dp)
                ) {
                    listOf(
                        "Your profile and all personal information",
                        "All your books, lists, and reading data",
                        "Your followers and following relationships",
                        "All your activities and reviews",
                    ).forEach {
                        Row(verticalAlignment = Alignment.Top) {
                            Box(Modifier.padding(top = 5.dp).size(6.dp).background(HL.Ink))
                            Spacer(Modifier.width(10.dp))
                            Text(it, color = HL.Ink, fontSize = 13.5.sp)
                        }
                    }
                }
                error?.let {
                    Spacer(Modifier.height(10.dp))
                    Text(it, color = HL.Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            },
            actions = {
                DialogButton(
                    "GO BACK", Modifier.weight(1f), fill = HL.Paper, label = HL.Ink,
                    enabled = !deleting
                ) { step = 0 }
                Spacer(Modifier.width(12.dp))
                DialogButton(
                    if (deleting) "DELETING…" else "DELETE MY ACCOUNT",
                    Modifier.weight(1f), fill = HL.Ink, label = HL.Paper,
                    enabled = !deleting, hatched = true
                ) {
                    deleting = true; error = null
                    scope.launch {
                        onSubmit(buildDeleteReasons(allReasons, selected, other)).fold(
                            onSuccess = { deleting = false; step = 2 },
                            onFailure = {
                                deleting = false
                                error = "Couldn’t delete account. Try again."
                            }
                        )
                    }
                }
            }
        )

        else -> BrutalDialog(
            onDismissRequest = {}, // must acknowledge — account already gone
            title = "We're sorry to see you go",
            eyebrow = null,
            body = {
                Text(
                    "Your account has been deleted. Thank you for being part of our community.",
                    color = HL.Muted, fontSize = 14.sp
                )
            },
            actions = {
                DialogButton("OKAY", Modifier.weight(1f), fill = HL.Ink, label = HL.Paper, onClick = onDeleted)
            }
        )
    }
}

/** Selected reasons in display order; "Other" carries the free-text, matching iOS. */
private fun buildDeleteReasons(
    all: List<String>, selected: List<String>, other: String
): List<String> = all.filter { selected.contains(it) }.map {
    if (it == "Other") "Other: ${other.trim()}" else it
}

private enum class SettingsDialog { Rate, Delete }

/** Pages pushed from a settings row — twins of the iOS NavigationLink destinations. */
private enum class SettingsPage(val title: String) {
    ChangePassword("Change Password"),
    Privacy("Privacy Policy"),
    Terms("Terms of Service"),
    Goodreads("Import from Goodreads")
}

/** Nav bar for a pushed page: back chip, title, and a matching right-hand gap. */
@Composable
private fun RowScope.SubPageHeader(title: String, onBack: () -> Unit) {
    Box(
        Modifier
            .size(width = 37.dp, height = 33.dp)
            .brutalButton(onBack, fill = HL.Paper, borderWidth = 2.dp, offset = 3.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.ArrowBack, "Back",
            tint = HL.Ink, modifier = Modifier.size(16.dp)
        )
    }
    Spacer(Modifier.weight(1f))
    Text(
        title,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 17.sp,
        color = HL.Ink
    )
    Spacer(Modifier.weight(1f))
    Spacer(Modifier.width(41.dp))
}

@Composable
private fun ColumnScope.SettingsSubPage(page: SettingsPage, email: String) {
    when (page) {
        SettingsPage.ChangePassword -> ChangePasswordPage(email)
        SettingsPage.Privacy -> LegalPage(page.title, PRIVACY_TEXT, "https://paperboxd.in/privacy")
        SettingsPage.Terms -> LegalPage(page.title, TERMS_TEXT, "https://paperboxd.in/terms")
        SettingsPage.Goodreads -> GoodreadsImportBody(Modifier.weight(1f))
    }
}

/**
 * Legal text on a plate with a link out to the canonical copy — iOS `LegalView`
 * twin. Replaces the dialog these used to open, which truncated the text.
 */
@Composable
private fun LegalPage(title: String, body: String, url: String) {
    val uriHandler = LocalUriHandler.current
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            body,
            fontSize = 15.sp,
            lineHeight = 25.sp,
            color = HL.Ink,
            modifier = Modifier
                .fillMaxWidth()
                .brutalPlate(fill = HL.Paper, borderWidth = 2.dp, offset = 5.dp)
                .padding(20.dp)
        )
        Box(
            Modifier
                .fillMaxWidth()
                .brutalButton({ uriHandler.openUri(url) }, fill = HL.Ink, borderWidth = 2.dp, offset = 5.dp)
                .height(50.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "READ THE FULL ${title.uppercase(Locale.US)} \u2192",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp,
                letterSpacing = 1.2.sp,
                color = HL.Paper
            )
        }
    }
}

/**
 * The backend exposes password *reset* (email link) but not an in-app change, so
 * this triggers the same forgot-password flow the auth screen uses. iOS
 * `ChangePasswordView` twin — Android used to fire the mail off from the row
 * itself, with no screen explaining what was about to happen.
 */
@Composable
private fun ChangePasswordPage(email: String) {
    val scope = rememberCoroutineScope()
    val viewModel: SettingsViewModel = hiltViewModel()
    var isSending by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isError by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 32.dp, bottom = 30.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .brutalPlate(fill = HL.Paper, borderWidth = 2.dp, offset = 5.dp)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                Modifier.size(52.dp).background(HL.Ink).border(2.dp, HL.Ink),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.LockReset, null,
                    tint = HL.Paper, modifier = Modifier.size(26.dp)
                )
            }
            Text(
                "Reset your password",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
                color = HL.Ink
            )
            Text(
                "We'll email a reset link to the address on your account. It works once and expires in 30 minutes.",
                fontSize = 14.sp,
                lineHeight = 21.sp,
                color = HL.Muted
            )
            if (email.isNotEmpty()) {
                Text(
                    email,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = HL.Ink,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HL.Paper2)
                        .border(2.dp, HL.Ink)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )
            }
        }

        Box(
            Modifier
                .fillMaxWidth()
                .brutalButton(
                    {
                        if (isSending || email.isEmpty()) return@brutalButton
                        isSending = true
                        message = null
                        scope.launch {
                            viewModel.sendPasswordReset(email).fold(
                                onSuccess = { message = "Reset link sent — check your inbox"; isError = false },
                                onFailure = { message = "Couldn't send the reset link"; isError = true }
                            )
                            isSending = false
                        }
                    },
                    fill = HL.Ink, borderWidth = 2.dp, offset = 5.dp,
                    enabled = !isSending && email.isNotEmpty()
                )
                .height(52.dp)
                .alpha(if (email.isEmpty()) 0.5f else 1f),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                if (isSending) {
                    CircularProgressIndicator(
                        color = HL.Paper,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    if (isSending) "SENDING…" else "SEND RESET LINK",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    letterSpacing = 1.4.sp,
                    color = HL.Paper
                )
            }
        }

        message?.let {
            Row(
                Modifier
                    .fillMaxWidth()
                    .background(HL.Ink)
                    .border(2.dp, HL.Ink)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    if (isError) Icons.Outlined.WarningAmber else Icons.Outlined.Check,
                    null, tint = HL.Paper, modifier = Modifier.size(14.dp)
                )
                Text(it, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = HL.Paper)
            }
        }

        Text(
            "NO IN-APP CHANGE · RESET BY EMAIL",
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            letterSpacing = 2.sp,
            color = HL.Muted
        )
    }
}

// MARK: - Brutalist building blocks

/** Eyebrow + a hard ink plate holding the section's rows. */
@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        SectionEyebrow(title)
        Column(
            Modifier
                .fillMaxWidth()
                .brutalPlate(fill = HL.Paper, borderWidth = 2.dp, offset = 5.dp)
        ) { content() }
    }
}

@Composable
private fun SectionEyebrow(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(7.dp).background(HL.Ink))
        Spacer(Modifier.width(8.dp))
        EyebrowText(title, color = HL.Ink)
    }
}

@Composable
private fun RowDivider() {
    Box(Modifier.fillMaxWidth().height(1.dp).background(HL.Ink.copy(alpha = 0.16f)))
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).height(54.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = HL.Ink, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 15.sp, color = HL.Ink)
        Spacer(Modifier.weight(1f))
        // Mono arrow instead of a chevron — hard, in the type system, no glyph tint.
        Text("→", fontFamily = FontFamily.Monospace, fontSize = 15.sp, color = HL.Ink)
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: @Composable () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(54.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = HL.Ink, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(12.dp))
        Text(label, fontSize = 15.sp, color = HL.Ink)
        Spacer(Modifier.weight(1f))
        value()
    }
}

/** Hard-edged twin of [androidx.compose.material3.Switch] — 52×30, knob steps across. */
@Composable
private fun BrutalSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Box(
        Modifier
            .size(width = 52.dp, height = 30.dp)
            .background(if (checked) HL.Ink else HL.Paper)
            .border(2.dp, HL.Ink)
            .clickable { onCheckedChange(!checked) }
            .padding(2.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(Modifier.size(22.dp).background(if (checked) HL.Paper else HL.Ink))
    }
}

@Composable
private fun MiniButton(
    text: String,
    fill: Color,
    label: Color,
    onClick: () -> Unit
) {
    Box(
        Modifier.brutalButton(onClick, fill = fill, borderWidth = 2.dp, offset = 2.dp)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = label)
    }
}

/** Fills the parent with 45° hazard stripes, inset so the ink border still reads. */
@Composable
private fun BoxScope.Hatch(base: Color, stripe: Color) {
    Box(Modifier.matchParentSize().padding(2.dp).background(hatchBrush(base, stripe)))
}

@Composable
private fun RowScope.DialogButton(
    text: String,
    modifier: Modifier = Modifier,
    fill: Color,
    label: Color,
    enabled: Boolean = true,
    hatched: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier
            .brutalButton(onClick, fill = fill, borderWidth = 2.dp, offset = 4.dp, enabled = enabled)
            .height(48.dp),
        contentAlignment = Alignment.Center
    ) {
        if (hatched) Hatch(base = fill, stripe = Color(0xFF33332E))
        Text(
            text,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Medium,
            fontSize = 12.sp,
            letterSpacing = 1.2.sp,
            color = if (enabled) label else label.copy(alpha = 0.45f)
        )
    }
}

/** AlertDialog stripped to a hard ink frame on paper. */
@Composable
private fun BrutalDialog(
    onDismissRequest: () -> Unit,
    title: String,
    eyebrow: String?,
    body: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        shape = RectangleShape,
        containerColor = HL.Paper,
        modifier = Modifier.border(2.dp, HL.Ink),
        title = {
            Column {
                if (eyebrow != null) {
                    SectionEyebrow(eyebrow)
                    Spacer(Modifier.height(6.dp))
                }
                Text(
                    title,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = HL.Ink
                )
            }
        },
        text = { Column { body() } },
        confirmButton = {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { actions() }
        }
    )
}

@Composable
private fun InfoDialog(title: String, body: String, url: String? = null, onDismiss: () -> Unit) {
    val uriHandler = LocalUriHandler.current
    BrutalDialog(
        onDismissRequest = onDismiss,
        title = title,
        eyebrow = null,
        body = { Text(body, color = HL.Ink, fontSize = 14.sp, lineHeight = 22.sp) },
        actions = {
            if (url != null) {
                DialogButton("READ FULL TEXT", Modifier.weight(1f), fill = HL.Paper, label = HL.Ink) {
                    uriHandler.openUri(url)
                }
                Spacer(Modifier.width(12.dp))
            }
            DialogButton("OK", Modifier.weight(1f), fill = HL.Ink, label = HL.Paper, onClick = onDismiss)
        }
    )
}

private const val PRIVACY_TEXT =
    "PaperBoxd stores the reading data you give us — the books you log, reviews, " +
    "shelves, and profile details — to run the app and show your activity to people " +
    "you choose to share it with.\n\nWe don't sell your data. Book metadata and ratings " +
    "come from third-party sources (Google Books, Open Library, Hardcover).\n\nYou can " +
    "request deletion of your account and data at any time by emailing paperboxd@gmail.com.\n\n" +
    "For the full, current policy see paperboxd.in/privacy."

private const val TERMS_TEXT =
    "By using PaperBoxd you agree to use it for personal, non-commercial book tracking " +
    "and to respect other readers in the community.\n\nYou own the content you post. You " +
    "grant us a licence to display it within the app so your friends and followers can see " +
    "your activity.\n\nThe Scan & Know score is a recommendation aid, not a guarantee — it's " +
    "generated from community data and your reading history.\n\nWe may update these terms as " +
    "the app evolves. Continued use means you accept the current terms. Full terms at paperboxd.in/terms."
