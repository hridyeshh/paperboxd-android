package `in`.paperboxd.app.ui.screens.diary

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AddComment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.theme.LikeRed
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val RepostGreen = Color(0xFF218C5C)

fun relativeTime(iso: String): String {
    val then = runCatching { Instant.parse(iso) }.getOrNull() ?: return ""
    val s = Duration.between(then, Instant.now()).seconds.coerceAtLeast(0)
    return when {
        s < 60 -> "now"
        s < 3600 -> "${s / 60}m"
        s < 86_400 -> "${s / 3600}h"
        s < 7 * 86_400 -> "${s / 86_400}d"
        else -> {
            val date = then.atZone(ZoneId.systemDefault())
            val pattern = if (date.year == java.time.Year.now().value) "MMM d" else "MMM d, yyyy"
            DateTimeFormatter.ofPattern(pattern).format(date)
        }
    }
}

// MARK: - Pieces

@Composable
fun ThoughtHeader(entry: DiaryEntry, modifier: Modifier = Modifier) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            entry.displayName,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.5.sp,
            color = HL.Ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Text(
            " @${entry.username}",
            fontSize = 13.5.sp,
            color = HL.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        Text(" · ${relativeTime(entry.createdAt)}", fontSize = 13.5.sp, color = HL.Muted, maxLines = 1)
        if (entry.isPrivate) {
            Spacer(Modifier.weight(1f))
            Icon(
                Icons.Filled.Lock, "Private", tint = HL.Muted,
                modifier = Modifier.padding(start = 6.dp).size(12.dp)
            )
        }
    }
}

@Composable
fun ThoughtBookChip(entry: DiaryEntry, onOpenBook: ((String) -> Unit)? = null) {
    val book = entry.book ?: return
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(HL.Card)
            .border(1.dp, HL.Ink.copy(alpha = 0.12f), RoundedCornerShape(10.dp))
            .then(if (onOpenBook != null && entry.bookId != null) Modifier.clickable { onOpenBook(entry.bookId) } else Modifier)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        BookCoverImage(
            url = book.coverUrl,
            title = book.title,
            modifier = Modifier.width(30.dp).aspectRatio(2f / 3f),
            cornerRadius = 3.dp
        )
        Column(Modifier.weight(1f)) {
            Text(
                book.title, fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold,
                fontSize = 13.5.sp, color = HL.Ink, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
            if (book.authorLine.isNotEmpty()) {
                Text(book.authorLine, fontSize = 12.sp, color = HL.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        entry.rating?.takeIf { it > 0 }?.let { r ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(1.5.dp),
                modifier = Modifier.semantics { contentDescription = "$r out of 5 stars" }
            ) {
                for (i in 1..5) {
                    Icon(
                        if (i <= r) Icons.Filled.Star else Icons.Outlined.StarOutline, null,
                        tint = if (i <= r) HL.Accent else HL.Muted.copy(alpha = 0.4f),
                        modifier = Modifier.size(10.dp)
                    )
                }
            }
        }
    }
}

/** Continue (own thoughts), repost, like, share. */
@Composable
fun ThoughtActionBar(
    entry: DiaryEntry,
    isOwn: Boolean,
    onToggleRepost: () -> Unit,
    onToggleLike: () -> Unit,
    onShare: () -> Unit,
    onContinue: (() -> Unit)? = null
) {
    Row(Modifier.fillMaxWidth().padding(end = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (isOwn && onContinue != null) {
            ActionButton(Icons.Outlined.AddComment, "Add to thread", onClick = onContinue)
        } else {
            Spacer(Modifier.width(48.dp))
        }
        Spacer(Modifier.weight(1f))
        ActionButton(
            Icons.Outlined.Repeat,
            if (entry.isReposted) "Undo repost" else "Repost",
            count = entry.repostsCount,
            tint = if (entry.isReposted) RepostGreen else null,
            enabled = !isOwn && !entry.isPrivate,
            onClick = onToggleRepost
        )
        Spacer(Modifier.weight(1f))
        ActionButton(
            if (entry.isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            if (entry.isLiked) "Unlike" else "Like",
            count = entry.likesCount,
            tint = if (entry.isLiked) LikeRed else null,
            onClick = onToggleLike
        )
        Spacer(Modifier.weight(1f))
        ActionButton(Icons.Outlined.IosShare, "Share to story", onClick = onShare)
    }
}

@Composable
private fun ActionButton(
    icon: ImageVector,
    label: String,
    count: Long? = null,
    tint: Color? = null,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val color = tint ?: HL.Muted
    Row(
        Modifier
            .heightIn(min = 40.dp)
            .widthIn(min = 48.dp)
            .clip(RoundedCornerShape(50))
            .clickable(enabled = enabled, onClickLabel = label, onClick = onClick)
            .alpha(if (enabled) 1f else 0.35f)
            .semantics { contentDescription = label + (count?.takeIf { it > 0 }?.let { ", $it" } ?: "") }
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        if (count != null) {
            Text(
                if (count > 0) count.toString() else "",
                fontFamily = FontFamily.Monospace, fontSize = 12.sp, color = color,
                modifier = Modifier.widthIn(min = 12.dp)
            )
        }
    }
}

// MARK: - Row

/** One thought in a profile's Thoughts tab, Twitter-style. */
@Composable
fun ThoughtRow(
    entry: DiaryEntry,
    viewerUsername: String?,
    onOpen: () -> Unit,
    onContinue: () -> Unit,
    onToggleLike: () -> Unit,
    onToggleRepost: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOwn = viewerUsername.equals(entry.username, ignoreCase = true)
    Column(
        modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        entry.repostedBy?.let { by ->
            Row(
                Modifier.padding(start = 22.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(Icons.Outlined.Repeat, null, tint = HL.Muted, modifier = Modifier.size(13.dp))
                Text(
                    if (by.username.equals(viewerUsername, ignoreCase = true)) "You reposted"
                    else "${by.name.ifBlank { by.username }} reposted",
                    fontSize = 12.sp, fontWeight = FontWeight.Medium, color = HL.Muted
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            AvatarImage(url = entry.avatarUrl, name = entry.displayName, size = 40.dp)
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                ThoughtHeader(entry)
                if (entry.book == null && !entry.title.isNullOrBlank()) {
                    Text(entry.title, fontWeight = FontWeight.SemiBold, fontSize = 14.5.sp, color = HL.Ink)
                }
                Text(
                    entry.plainTextPreview,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.5.sp,
                    lineHeight = 22.sp,
                    color = HL.Ink,
                    maxLines = 8,
                    overflow = TextOverflow.Ellipsis
                )
                if (entry.book != null) {
                    Box(Modifier.padding(top = 5.dp)) { ThoughtBookChip(entry) }
                }
                if (entry.threadCount > 0) {
                    Text(
                        "Show this thread · ${entry.threadCount} more",
                        fontSize = 13.sp, fontWeight = FontWeight.Medium, color = HL.Accent,
                        modifier = Modifier.padding(top = 3.dp)
                    )
                }
                ThoughtActionBar(
                    entry = entry,
                    isOwn = isOwn,
                    onToggleRepost = onToggleRepost,
                    onToggleLike = onToggleLike,
                    onShare = onShare,
                    onContinue = onContinue
                )
            }
        }
    }
}
