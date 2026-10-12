package `in`.paperboxd.app.ui.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import `in`.paperboxd.app.domain.model.AttachKind
import `in`.paperboxd.app.domain.model.MessageAttachment
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import kotlinx.coroutines.delay
import java.time.Duration
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Runs [block] every [intervalMs] while the screen is resumed — the stand-in
 * for push until the store accounts exist. Stops in the background.
 */
@Composable
fun PollWhileResumed(intervalMs: Long, block: suspend () -> Unit) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val current = rememberUpdatedState(block)
    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            while (true) {
                current.value()
                delay(intervalMs)
            }
        }
    }
}

private fun parseLocal(iso: String) =
    runCatching { OffsetDateTime.parse(iso).atZoneSameInstant(ZoneId.systemDefault()) }.getOrNull()

/** Chat day divider: "Today", "Yesterday", "Mon", "12 Sep 2025". */
fun dayLabel(iso: String): String {
    val d = parseLocal(iso)?.toLocalDate() ?: return ""
    val today = java.time.LocalDate.now()
    return when {
        d == today -> "Today"
        d == today.minusDays(1) -> "Yesterday"
        d.isAfter(today.minusDays(7)) -> d.format(DateTimeFormatter.ofPattern("EEEE"))
        d.year == today.year -> d.format(DateTimeFormatter.ofPattern("d MMM"))
        else -> d.format(DateTimeFormatter.ofPattern("d MMM yyyy"))
    }
}

/** "10:42" in the device's locale. */
fun timeLabel(iso: String): String =
    parseLocal(iso)?.format(DateTimeFormatter.ofLocalizedTime(java.time.format.FormatStyle.SHORT)).orEmpty()

fun sameDay(a: String, b: String): Boolean = parseLocal(a)?.toLocalDate() == parseLocal(b)?.toLocalDate()

fun minutesApart(a: String, b: String): Long {
    val x = parseLocal(a) ?: return Long.MAX_VALUE
    val y = parseLocal(b) ?: return Long.MAX_VALUE
    return kotlin.math.abs(Duration.between(x, y).toMinutes())
}

private val fusionLink = Regex("""https://(?:www\.)?paperboxd\.in/fusion/([a-km-np-z2-9]{12})""")

/** The Fusion invite token in a message body, if it carries one. */
fun fusionToken(body: String): String? = fusionLink.find(body)?.groupValues?.get(1)

/** "now", "5m", "3h", "Yesterday", "Mon", "12 Sep". */
fun shortAgo(iso: String): String {
    val t = runCatching { OffsetDateTime.parse(iso) }.getOrNull() ?: return ""
    val now = OffsetDateTime.now()
    val d = Duration.between(t, now)
    val local = t.atZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
    val today = now.atZoneSameInstant(ZoneId.systemDefault()).toLocalDate()
    return when {
        d.toMinutes() < 1 -> "now"
        d.toHours() < 1 -> "${d.toMinutes()}m"
        local == today -> "${d.toHours()}h"
        local == today.minusDays(1) -> "Yesterday"
        d.toDays() < 7 -> local.format(DateTimeFormatter.ofPattern("EEE"))
        else -> local.format(DateTimeFormatter.ofPattern("d MMM"))
    }
}

/**
 * A Paperboxd object inside a message, drawn from its send-time snapshot so it
 * still renders after the object changes. Tapping opens the live object.
 */
@Composable
fun AttachmentCard(att: MessageAttachment, onOpen: (MessageAttachment) -> Unit, modifier: Modifier = Modifier) {
    if (att.kind == AttachKind.BOOK) {
        BookCard(att, onOpen, modifier)
        return
    }
    Row(
        modifier
            .width(260.dp)
            .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 3.dp)
            .clickable { onOpen(att) }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        when (att.kind) {
            AttachKind.LIST -> {
                val covers = att.strings("covers")
                Box(Modifier.width(56.dp)) {
                    covers.take(3).forEachIndexed { i, url ->
                        BookCoverImage(url, "", Modifier.width(36.dp).aspectRatio(2f / 3f).offset(x = (i * 10).dp), 3.dp)
                    }
                    if (covers.isEmpty()) BookCoverImage(null, att.str("title"), Modifier.width(36.dp).aspectRatio(2f / 3f), 3.dp)
                }
                val n = att.int("book_count") ?: 0
                CardText("LIST · $n ${if (n == 1) "BOOK" else "BOOKS"}", att.str("title"), "by @${att.str("owner_username")}")
            }
            AttachKind.THOUGHT -> {
                if (att.str("book_cover_url").isNotBlank()) {
                    BookCoverImage(att.str("book_cover_url"), att.str("book_title"), Modifier.width(40.dp).aspectRatio(2f / 3f), 3.dp)
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Eyebrow("THOUGHT · @${att.str("author_username")}")
                    Text(
                        "“${att.str("excerpt")}”",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 14.sp,
                        lineHeight = 19.sp,
                        color = HL.Ink,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                    att.str("book_title").takeIf { it.isNotBlank() }?.let {
                        Text(it, fontSize = 11.sp, color = HL.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
            AttachKind.PROFILE -> {
                AvatarImage(att.str("avatar_url").ifBlank { null }, att.str("name").ifBlank { att.str("username") }, 44.dp)
                CardText("READER", att.str("name").ifBlank { att.str("username") }, "@${att.str("username")}")
            }
            AttachKind.FUSION -> {
                val readers = att.readers()
                Box(Modifier.width(60.dp)) {
                    readers.take(2).forEachIndexed { i, r ->
                        AvatarImage(r.avatarUrl, r.displayName, 36.dp, Modifier.offset(x = (i * 22).dp))
                    }
                }
                val score = att.int("score")
                CardText(
                    "FUSION",
                    readers.joinToString(" + ") { it.displayName.substringBefore(' ') },
                    if (score != null) "$score% taste match" else "Taste Fusion"
                )
            }
            else -> CardText("PAPERBOXD", "Unsupported card", "Update the app to see this")
        }
    }
}

/**
 * A book gets the full treatment: cover, meta line, and a short blurb so the
 * recipient knows what it is before tapping. Older cards have no blurb.
 */
@Composable
private fun BookCard(att: MessageAttachment, onOpen: (MessageAttachment) -> Unit, modifier: Modifier) {
    val meta = listOfNotNull(
        "BOOK",
        att.int("year")?.toString(),
        att.int("page_count")?.takeIf { it > 0 }?.let { "$it PP" }
    ).joinToString(" · ")
    val blurb = att.str("description")
    val authors = att.strings("authors").joinToString(", ")
    Column(
        modifier
            .width(272.dp)
            .brutalPlate(fill = HL.Card, borderWidth = 1.5.dp, offset = 3.dp)
            .clickable { onOpen(att) }
            .padding(13.dp),
        verticalArrangement = Arrangement.spacedBy(11.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.width(64.dp)) {
                // Hard offset shadow, in keeping with the brutal plate.
                Box(Modifier.matchParentSize().offset(2.dp, 2.dp).background(HL.Ink.copy(alpha = 0.22f), RoundedCornerShape(4.dp)))
                BookCoverImage(att.str("cover_url"), att.str("title"), Modifier.width(64.dp).aspectRatio(2f / 3f), 4.dp)
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Eyebrow(meta)
                Text(
                    att.str("title"), fontFamily = FontFamily.Serif, fontWeight = FontWeight.SemiBold, fontSize = 16.sp,
                    lineHeight = 20.sp, color = HL.Ink, maxLines = 3, overflow = TextOverflow.Ellipsis
                )
                if (authors.isNotBlank()) {
                    Text("by $authors", fontSize = 12.sp, color = HL.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                att.double("rating")?.takeIf { it > 0 }?.let {
                    Text(
                        "★ %.1f".format(it), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp, color = HL.Ink, modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
        if (blurb.isNotBlank()) {
            Box(Modifier.fillMaxWidth().height(1.dp).background(HL.Ink.copy(alpha = 0.12f)))
            Text(
                blurb, fontFamily = FontFamily.Serif, fontSize = 13.sp, lineHeight = 18.sp,
                color = HL.Ink.copy(alpha = 0.78f), maxLines = 4, overflow = TextOverflow.Ellipsis
            )
        }
        Text(
            "OPEN BOOK →", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 10.sp,
            letterSpacing = 1.4.sp, color = HL.Ink, modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
private fun CardText(eyebrow: String, title: String, sub: String) {
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Eyebrow(eyebrow)
        Text(
            title,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            lineHeight = 19.sp,
            color = HL.Ink,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (sub.isNotBlank()) Text(sub, fontSize = 12.sp, color = HL.Muted, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun Eyebrow(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier,
        fontFamily = FontFamily.Monospace,
        fontSize = 9.5.sp,
        letterSpacing = 1.6.sp,
        color = HL.Muted,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

/** Friendly copy for a send failure; falls back to the server's message. */
fun sendErrorText(t: Throwable?): String = t?.message?.takeIf { it.isNotBlank() } ?: "Couldn't send — try again"
