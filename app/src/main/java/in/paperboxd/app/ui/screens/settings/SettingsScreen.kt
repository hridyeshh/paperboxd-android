package `in`.paperboxd.app.ui.screens.settings

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import `in`.paperboxd.app.ui.theme.PBScript
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** iOS SettingsView twin — same sections/rows, restyled to the light brutalist
 *  paper aesthetic used across the ported app. Opened from the profile hamburger. */
@Composable
fun SettingsScreen(
    email: String,
    onBack: () -> Unit,
    onSignOut: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var dialog by remember { mutableStateOf<SettingsDialog?>(null) }
    var toast by remember { mutableStateOf<String?>(null) }

    val version = remember {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull() ?: "1.0"
    }

    Box(Modifier.fillMaxSize().background(HL.Paper)) {
        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 40.dp)
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                // ponytail: scan quota not persisted on Android yet — mirrors iOS default of 7.
                InfoRow(Icons.Outlined.QrCodeScanner, "Free Scans Remaining", "7 remaining")
            }

            Section("Your Data") {
                SettingsRow(Icons.Outlined.Download, "Import from Goodreads") {
                    toast = "Goodreads import coming soon"
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
                    .brutalPlate(fill = HL.Card, borderWidth = 2.dp, offset = 3.dp, shadow = Color.Transparent)
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
        SettingsDialog.Delete -> AlertDialog(
            onDismissRequest = { dialog = null },
            containerColor = HL.Card,
            title = { Text("Delete Account", color = HL.Ink, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This permanently deletes your account and all your reading data. This can't be undone.",
                    color = HL.Muted, fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    dialog = null
                    scope.launch {
                        viewModel.deleteAccount().fold(
                            onSuccess = { onSignOut() },
                            onFailure = { toast = "Couldn’t delete account" }
                        )
                    }
                }) { Text("Delete", color = HL.Accent, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { dialog = null }) { Text("Cancel", color = HL.Muted) }
            }
        )
        null -> {}
    }
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
                .brutalPlate(fill = HL.Card, borderWidth = 2.dp, offset = 3.dp, shadow = Color.Transparent)
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
