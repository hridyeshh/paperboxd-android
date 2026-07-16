package `in`.paperboxd.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

/**
 * Android-native floating dock — a solid white rounded pill with side margins,
 * icon-only tabs, and a Sienna tonal-pill active indicator. Diverges on purpose
 * from the iOS glass dock (see "Bottom Dock.html · 03 Android redesign", variant
 * "A · tonal pill"). Four tabs (Home | Search | Leaderboard | Profile); Scan/Buddy
 * stay separate as the floating [PipScanButton], not part of the dock.
 */
enum class DockTab { Home, Search, Leaderboard, Profile }

// PaperBoxd Sienna — brand accent for the active tab.
private val DockAccent = Color(0xFFBE5B37)
private val DockActivePill = Color(0x24BE5B37)   // ~14% sienna tint behind active icon
private val DockInactive = Color(0xFF9C948A)     // muted ink for idle icons
private val DockShadow = Color(0x33151513)

@Composable
fun BottomNavBar(
    selected: DockTab,
    onSelect: (DockTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(horizontal = 22.dp, vertical = 10.dp)
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(30.dp),
                clip = false,
                ambientColor = DockShadow,
                spotColor = DockShadow
            )
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .height(60.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DockTab.entries.forEach { tab ->
            TabButton(
                tab = tab,
                active = selected == tab,
                onClick = { onSelect(tab) },
                modifier = Modifier.weight(1f)
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
        DockTab.Search -> if (active) Icons.Filled.Search else Icons.Outlined.Search
        DockTab.Leaderboard -> if (active) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents
        DockTab.Profile -> if (active) Icons.Filled.Person else Icons.Outlined.Person
    }
    val tint by animateColorAsState(if (active) DockAccent else DockInactive, label = "dockTint")
    Box(
        modifier = modifier
            .height(44.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (active) {
            Box(
                Modifier
                    .size(width = 52.dp, height = 36.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(DockActivePill)
            )
        }
        Icon(
            imageVector = icon,
            contentDescription = tab.name,
            tint = tint,
            modifier = Modifier.size(24.dp)
        )
    }
}
