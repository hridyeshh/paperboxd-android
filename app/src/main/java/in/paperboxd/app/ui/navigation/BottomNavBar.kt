package `in`.paperboxd.app.ui.navigation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.outlined.Send
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.ui.components.HL

/**
 * Brutal-block dock — iOS `CustomDock` twin: a flat card with a 2dp ink border and a hard
 * offset shadow, like the onboarding buttons. The active tab is an ink square with the
 * icon knocked out in paper; the unread badge is a crimson square. Five tabs
 * (Home | Search | Messages | Leaderboard | Profile); Scan/Buddy stay separate as the
 * floating [PipScanButton], not part of the dock.
 */
enum class DockTab { Home, Search, Inbox, Leaderboard, Profile }

private val DockShape = RoundedCornerShape(4.dp)

@Composable
fun BottomNavBar(
    selected: DockTab,
    onSelect: (DockTab) -> Unit,
    modifier: Modifier = Modifier,
    inboxBadge: Int = 0
) {
    val haptics = LocalHapticFeedback.current
    Row(
        modifier = modifier
            .padding(start = 16.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
            .fillMaxWidth()
            .height(58.dp)
            .drawBehind {
                val o = 4.dp.toPx()
                drawRoundRect(HL.Ink, topLeft = Offset(o, o), size = size, cornerRadius = CornerRadius(4.dp.toPx()))
            }
            .clip(DockShape)
            .background(HL.Card)
            .border(2.dp, HL.Ink, DockShape)
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DockTab.entries.forEach { tab ->
            TabButton(
                tab = tab,
                active = selected == tab,
                badge = tab == DockTab.Inbox && inboxBadge > 0,
                onClick = {
                    if (tab != selected) haptics.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onSelect(tab)
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabButton(
    tab: DockTab,
    active: Boolean,
    badge: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when (tab) {
        DockTab.Home -> if (active) Icons.Filled.Home else Icons.Outlined.Home
        DockTab.Search -> if (active) Icons.Filled.Search else Icons.Outlined.Search
        DockTab.Inbox -> if (active) Icons.AutoMirrored.Filled.Send else Icons.AutoMirrored.Outlined.Send
        DockTab.Leaderboard -> if (active) Icons.Filled.EmojiEvents else Icons.Outlined.EmojiEvents
        DockTab.Profile -> if (active) Icons.Filled.Person else Icons.Outlined.Person
    }
    // The ink square pops in on the newly selected tab.
    val on by animateFloatAsState(if (active) 1f else 0f, spring(dampingRatio = 0.65f), label = "dockIndicator")
    Box(
        modifier = modifier
            .height(48.dp)
            .semantics { selected = active }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(42.dp)
                .graphicsLayer { scaleX = 0.6f + 0.4f * on; scaleY = 0.6f + 0.4f * on; alpha = on }
                .clip(RoundedCornerShape(2.dp))
                .background(HL.Ink)
        )
        Icon(
            imageVector = icon,
            contentDescription = if (tab == DockTab.Inbox) "Messages" else tab.name,
            tint = if (active) HL.Card else HL.Muted,
            modifier = Modifier.size(if (tab == DockTab.Inbox) 19.dp else 20.dp)
        )
        if (badge) {
            Box(
                Modifier
                    .offset(x = 11.dp, y = (-10).dp)
                    .size(8.dp)
                    .background(HL.Crimson)
                    .border(1.5.dp, HL.Ink)
            )
        }
    }
}
