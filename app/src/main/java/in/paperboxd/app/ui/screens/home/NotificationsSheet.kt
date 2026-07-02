package `in`.paperboxd.app.ui.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.R
import `in`.paperboxd.app.domain.model.ActivityItem
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.theme.Surface
import `in`.paperboxd.app.ui.theme.TextPrimary
import `in`.paperboxd.app.ui.theme.TextSecondary
import java.time.Instant

/** Friends activity feed sheet — the bell target on Home (iOS NotificationsView). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsSheet(
    activities: List<ActivityItem>,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = Surface) {
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = stringResource(R.string.home_notifications),
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary
            )
            Spacer(Modifier.padding(top = 8.dp))
            if (activities.isEmpty()) {
                Text(
                    text = stringResource(R.string.home_no_activity),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    modifier = Modifier.padding(vertical = 32.dp)
                )
            } else {
                LazyColumn {
                    items(activities, key = { it.id }) { activity ->
                        ActivityRow(activity)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(activity: ActivityItem) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AvatarImage(url = activity.avatarUrl, name = activity.displayName, size = 36.dp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(
                    activity.displayName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    activity.verbPhrase,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            activity.objectTitle?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
                )
            }
        }
        Text(
            relativeTime(activity.createdAt),
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
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
