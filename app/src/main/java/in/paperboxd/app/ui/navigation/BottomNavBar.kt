package `in`.paperboxd.app.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.ui.theme.Background
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary

/**
 * Dock tabs. Mirrors the shipped iOS MainTabView: Home | Search | Scan
 * (special center action) | Leaderboard | Profile. Scan is not a destination —
 * selecting it fires [onScan] and the selection stays put.
 */
enum class DockTab {
    Home, Search, Scan, Leaderboard, Profile;

    val isSpecial: Boolean get() = this == Scan
}

@Composable
fun BottomNavBar(
    selected: DockTab,
    onSelect: (DockTab) -> Unit,
    onScan: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clip(CircleShape)
            .background(Surface.copy(alpha = 0.92f))
            .border(0.8.dp, TextPrimary.copy(alpha = 0.10f), CircleShape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DockTab.entries.forEach { tab ->
            if (tab.isSpecial) {
                ScanButton(onClick = onScan, modifier = Modifier.weight(1f))
            } else {
                TabButton(
                    tab = tab,
                    active = selected == tab,
                    onClick = { onSelect(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ScanButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(modifier = modifier.heightIn(min = 56.dp), contentAlignment = Alignment.Center) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(TextPrimary)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.QrCodeScanner,
                contentDescription = null,
                tint = Background,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun TabButton(
    tab: DockTab,
    active: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (tab) {
        DockTab.Home -> if (active) Icons.Filled.Home else Icons.Outlined.Home
        DockTab.Search -> Icons.Outlined.Search
        DockTab.Leaderboard -> Icons.Outlined.EmojiEvents
        DockTab.Profile -> if (active) Icons.Filled.Person else Icons.Outlined.Person
        DockTab.Scan -> Icons.Outlined.QrCodeScanner
    }
    Box(
        modifier = modifier
            .heightIn(min = 56.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (active) TextPrimary else TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.size(24.dp)
        )
    }
}
