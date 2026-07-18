package `in`.paperboxd.app.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ChevronRight
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import `in`.paperboxd.app.ui.screens.scan.ScanPrefs
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.theme.PBScript
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.outlined.Check
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/** iOS SettingsView twin — same sections/rows, restyled to the light brutalist
 *  paper aesthetic used across the ported app. Opened from the profile hamburger. */
@Composable
fun SettingsScreen(
    email: String,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsBody(
        email = email,
        onSignOut = onSignOut,
        viewModel = viewModel,
        modifier = Modifier.fillMaxSize(),
        header = {
            CircleChip(onBack) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack, "Back",
                    tint = HL.Ink, modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.weight(1f))
            Text("Settings", fontFamily = PBScript, fontSize = 24.sp, color = HL.Ink)
            Spacer(Modifier.weight(1f))
            Spacer(Modifier.size(36.dp))
        }
    )
}

/** Slide-up sheet twin of iOS `.sheet(isPresented: $showSettings)` — same body,
 *  presented as a ModalBottomSheet over the profile instead of a full screen. */
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
        dragHandle = null
    ) {
        SettingsBody(
            email = email,
            onSignOut = onSignOut,
            viewModel = viewModel,
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.92f),
            header = {
                Spacer(Modifier.width(72.dp)) // balances the Done pill so title centres
                Spacer(Modifier.weight(1f))
                Text(
                    "Settings",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp,
                    color = HL.Ink
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "Done",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = HL.Accent,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(HL.Card)
                        .clickable(onClick = onDismiss)
                        .padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        )
    }
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
    val scope = rememberCoroutineScope()

    var dialog by remember { mutableStateOf<SettingsDialog?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }
    var showGoodreads by remember { mutableStateOf(false) }

    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    Box(modifier.background(HL.Paper)) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                content = header
            )

            Section("Account") {
                SettingsRow(Icons.Outlined.Lock, "Change Password") {
                    scope.launch {
                        toast = "Sending reset link…"
                        toast = viewModel.sendPasswordReset(email).fold(
                            onSuccess = { "Reset link sent — check your inbox" },
                            onFailure = { "Couldn’t send the reset link" }
                        )
                    }
                }
            }

            Section("Scan & Know") {
                InfoRow(
                    Icons.Outlined.QrCodeScanner,
                    "Free Scans Remaining",
                    "${ScanPrefs.scansRemaining(LocalContext.current)} remaining"
                )
            }

            Section("Your Data") {
                SettingsRow(Icons.Outlined.Download, "Import from Goodreads") {
                    showGoodreads = true
                }
            }

            Section("Discover") {
                SettingsRow(Icons.Outlined.PersonAddAlt, "Invite Friends") {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, "Track your reading with me on PaperBoxd — https://paperboxd.in")
                    }
                    context.startActivity(Intent.createChooser(send, "Invite Friends"))
                }
                SettingsRow(Icons.Outlined.StarBorder, "Rate PaperBoxd") {
                    dialog = SettingsDialog.Rate
                }
            }

            Section("About") {
                SettingsRow(Icons.Outlined.Policy, "Privacy Policy") { dialog = SettingsDialog.Privacy }
                SettingsRow(Icons.Outlined.Description, "Terms of Service") { dialog = SettingsDialog.Terms }
                InfoRow(Icons.Outlined.Info, "Version", version)
            }

            Spacer(Modifier.height(20.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(HL.Card)
                    .clickable { onSignOut() }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "SIGN OUT",
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.5.sp,
                    color = HL.Accent
                )
            }

            Spacer(Modifier.height(16.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clickable { dialog = SettingsDialog.Delete }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "DELETE ACCOUNT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp,
                    color = HL.Accent.copy(alpha = 0.65f)
                )
            }
        }

        toast?.let { ToastChip(it) { toast = null } }
    }

    when (dialog) {
        SettingsDialog.Rate -> InfoDialog(
            "Rate PaperBoxd",
            "We'll enable ratings once we're live on the Play Store. Thank you for your support!"
        ) { dialog = null }
        SettingsDialog.Privacy -> InfoDialog("Privacy Policy", PRIVACY_TEXT) { dialog = null }
        SettingsDialog.Terms -> InfoDialog("Terms of Service", TERMS_TEXT) { dialog = null }
        SettingsDialog.Delete -> DeleteAccountDialog(
            onDismiss = { dialog = null },
            onDeleted = { dialog = null; onSignOut() },
            onSubmit = viewModel::deleteAccount
        )
        null -> {}
    }

    if (showGoodreads) {
        GoodreadsImportSheet(onDismiss = { showGoodreads = false })
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
        0 -> AlertDialog(
            onDismissRequest = onDismiss,
            containerColor = HL.Card,
            title = { Text("Delete Account", color = HL.Ink, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        "We're sorry to see you go. Please let us know why you're deleting your account.",
                        color = HL.Muted, fontSize = 13.sp
                    )
                    Spacer(Modifier.height(14.dp))
                    Column(
                        Modifier.heightIn(max = 320.dp).verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        allReasons.forEach { r ->
                            val on = selected.contains(r)
                            Row(
                                Modifier.fillMaxWidth().clickable {
                                    if (on) { selected.remove(r); if (r == "Other") other = "" }
                                    else selected.add(r)
                                },
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    Modifier.size(20.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(if (on) HL.Accent else Color.Transparent)
                                        .border(
                                            1.5.dp,
                                            if (on) HL.Accent else HL.Muted,
                                            RoundedCornerShape(5.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (on) Icon(
                                        Icons.Outlined.Check, null,
                                        tint = Color.White, modifier = Modifier.size(14.dp)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Text(r, color = HL.Ink, fontSize = 14.sp)
                            }
                            if (r == "Other" && on) {
                                BasicTextField(
                                    value = other,
                                    onValueChange = { other = it },
                                    singleLine = true,
                                    textStyle = TextStyle(fontSize = 14.sp, color = HL.Ink),
                                    modifier = Modifier.fillMaxWidth().padding(start = 32.dp)
                                        .clip(RoundedCornerShape(8.dp)).background(HL.Paper2)
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
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
                }
            },
            confirmButton = {
                TextButton(enabled = formValid, onClick = { step = 1 }) {
                    Text(
                        "Continue",
                        color = if (formValid) HL.Accent else HL.Muted,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel", color = HL.Muted) } }
        )

        1 -> AlertDialog(
            onDismissRequest = { if (!deleting) onDismiss() },
            containerColor = HL.Card,
            title = { Text("Are you sure?", color = HL.Ink, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("This action cannot be undone.", color = HL.Muted, fontSize = 13.sp)
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Deleting your account will permanently remove:",
                        color = HL.Ink, fontSize = 14.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    listOf(
                        "Your profile and all personal information",
                        "All your books, lists, and reading data",
                        "Your followers and following relationships",
                        "All your activities and reviews",
                    ).forEach {
                        Row(Modifier.padding(top = 4.dp)) {
                            Text("•  ", color = HL.Muted, fontSize = 13.sp)
                            Text(it, color = HL.Muted, fontSize = 13.sp)
                        }
                    }
                    error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = HL.Accent, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                TextButton(enabled = !deleting, onClick = {
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
                }) {
                    Text(
                        if (deleting) "Deleting…" else "Delete my account",
                        color = HL.Accent, fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(enabled = !deleting, onClick = { step = 0 }) {
                    Text("Go back", color = HL.Muted)
                }
            }
        )

        else -> AlertDialog(
            onDismissRequest = {}, // must acknowledge — account already gone
            containerColor = HL.Card,
            title = { Text("We're sorry to see you go", color = HL.Ink, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Your account has been successfully deleted. Thank you for being part of our community.",
                    color = HL.Muted, fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = onDeleted) {
                    Text("Okay", color = HL.Accent, fontWeight = FontWeight.Bold)
                }
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

private enum class SettingsDialog { Rate, Privacy, Terms, Delete }

@Composable
private fun Section(title: String, content: @Composable () -> Unit) {
    Column(Modifier.padding(top = 22.dp)) {
        EyebrowText(title, modifier = Modifier.padding(start = 24.dp, bottom = 10.dp))
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(HL.Card)
        ) { content() }
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = onClick).padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = HL.Ink.copy(alpha = 0.55f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(14.dp))
        Text(label, fontSize = 15.sp, color = HL.Ink)
        Spacer(Modifier.weight(1f))
        Icon(
            Icons.Outlined.ChevronRight, null,
            tint = HL.Muted.copy(alpha = 0.6f), modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun InfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = HL.Ink.copy(alpha = 0.55f), modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(14.dp))
        Text(label, fontSize = 15.sp, color = HL.Ink)
        Spacer(Modifier.weight(1f))
        Text(value, fontSize = 13.sp, color = HL.Muted, fontFamily = FontFamily.Monospace)
    }
}

@Composable
private fun CircleChip(onClick: () -> Unit, content: @Composable () -> Unit) {
    Box(
        Modifier.size(36.dp).clip(CircleShape).background(HL.Ink.copy(alpha = 0.06f)).clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}

@Composable
private fun InfoDialog(title: String, body: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = HL.Card,
        title = { Text(title, color = HL.Ink, fontWeight = FontWeight.Bold) },
        text = { Text(body, color = HL.Muted, fontSize = 14.sp) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("OK", color = HL.Ink) } }
    )
}

@Composable
private fun ToastChip(msg: String, onDone: () -> Unit) {
    LaunchedEffect(msg) {
        delay(2000)
        onDone()
    }
    Box(Modifier.fillMaxSize().padding(bottom = 48.dp), contentAlignment = Alignment.BottomCenter) {
        Text(
            msg,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = HL.Card,
            modifier = Modifier
                .clip(CircleShape)
                .background(HL.Ink.copy(alpha = 0.92f))
                .padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

private const val PRIVACY_TEXT =
    "PaperBoxd stores the reading data you give us — the books you log, reviews, " +
    "shelves, and profile details — to run the app and show your activity to people " +
    "you choose to share it with.\n\nWe don't sell your data. Book metadata and ratings " +
    "come from third-party sources (Google Books, Open Library, Hardcover).\n\nYou can " +
    "request deletion of your account and data at any time by emailing hello@paperboxd.in.\n\n" +
    "For the full, current policy see paperboxd.in/privacy."

private const val TERMS_TEXT =
    "By using PaperBoxd you agree to use it for personal, non-commercial book tracking " +
    "and to respect other readers in the community.\n\nYou own the content you post. You " +
    "grant us a licence to display it within the app so your friends and followers can see " +
    "your activity.\n\nThe Scan & Know score is a recommendation aid, not a guarantee — it's " +
    "generated from community data and your reading history.\n\nWe may update these terms as " +
    "the app evolves. Continued use means you accept the current terms. Full terms at paperboxd.in/terms."
