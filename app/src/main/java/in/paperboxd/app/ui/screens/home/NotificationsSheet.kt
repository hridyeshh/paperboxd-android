package `in`.paperboxd.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.ActivityItem
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import java.time.Instant

/**
 * Friends activity feed — the bell target on Home. iOS NotificationsView twin:
 * light paper sheet, "@user verb title" rows on brutalist cards, accent dot.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    activities: List<ActivityItem>,
    onOpenBook: (String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = HL.Paper) {
        Column {
            Text(
                text = stringResource(R.string.home_updates),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 17.sp,
                color = HL.Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )
            if (activities.isEmpty()) {
                EmptyUpdates()
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 14.dp, bottom = 24.dp)
                ) {
                    items(activities, key = { it.id }) { activity ->
                        ActivityRow(
                            activity = activity,
                            onClick = activity.bookId?.let { id -> { onDismiss(); onOpenBook(id) } }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: ActivityItem, onClick: (() -> Unit)?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .brutalPlate(offset = 3.dp)
            .let { if (onClick != null) it.clickable(onClick = onClick) else it }
            .padding(13.dp),
        horizontalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        AvatarImage(url = activity.avatarUrl, name = activity.displayName, size = 38.dp)

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = HL.Ink)) {
                        append("@${activity.username} ")
                    }
                    withStyle(SpanStyle(color = HL.Muted)) {
                        append(activity.verbPhrase)
                    }
                    activity.objectTitle?.let {
                        withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = HL.Ink)) {
                            append(" $it")
                        }
                    }
                },
                fontSize = 13.sp,
                lineHeight = 18.sp,
                maxLines = 3
            )
            Text(
                text = relativeTime(activity.createdAt).uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                letterSpacing = 0.8.sp,
                color = HL.Muted
            )
        }

        Box(
            modifier = Modifier
                .padding(top = 5.dp)
                .size(7.dp)
                .clip(CircleShape)
                .background(HL.Accent)
        )
    }
}

@Composable
private fun EmptyUpdates() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            Icons.Outlined.NotificationsOff,
            contentDescription = null,
            tint = HL.Muted.copy(alpha = 0.6f),
            modifier = Modifier.size(34.dp)
        )
        Text(
            text = stringResource(R.string.home_no_updates),
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 17.sp,
            color = HL.Ink
        )
        Text(
            text = stringResource(R.string.home_follow_hint),
            fontSize = 13.sp,
            color = HL.Muted,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
        Spacer(Modifier.height(8.dp))
    }
}

/** "now / 5m / 3h / 2d" — iOS FriendActivity.relativeTime twin. */
fun relativeTime(iso: String): String {
    val date = runCatching { Instant.parse(iso) }.getOrNull() ?: return ""
    val diff = (System.currentTimeMillis() - date.toEpochMilli()) / 1_000
    return when {
        diff < 60 -> "now"
        diff < 3_600 -> "${diff / 60}m"
        diff < 86_400 -> "${diff / 3_600}h"
        else -> "${diff / 86_400}d"
    }
}
