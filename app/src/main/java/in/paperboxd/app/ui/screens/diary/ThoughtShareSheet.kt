package `in`.paperboxd.app.ui.screens.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import `in`.paperboxd.app.domain.model.AttachKind
import `in`.paperboxd.app.domain.model.AttachmentRef
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.ui.screens.bookdetail.ShareCard
import `in`.paperboxd.app.ui.screens.bookdetail.ShareCardSheet
import `in`.paperboxd.app.ui.screens.bookdetail.ShareChip
import `in`.paperboxd.app.ui.screens.bookdetail.ShareGround
import `in`.paperboxd.app.ui.screens.bookdetail.ShareLayout
import `in`.paperboxd.app.ui.screens.bookdetail.bookShareCanvas

/**
 * Share sheet for one thought — iOS ThoughtShareSheet twin. The card is the book
 * card's quote layout: the reader's words set large on the chosen ground, the
 * book (real cover board) underneath, the handle at the foot. [threadLabel] is
 * e.g. "2/3".
 */
@Composable
fun ThoughtShareSheet(entry: DiaryEntry, threadLabel: String? = null, onDismiss: () -> Unit) {
    var ground by remember { mutableStateOf(ShareGround.Ink) }

    ShareCardSheet(
        link = "https://paperboxd.in/u/${entry.username}",
        imageName = "thought-share",
        onDismiss = onDismiss,
        // Private thoughts can't be sent; the server refuses them too.
        send = if (entry.isPrivate) null else AttachmentRef(AttachKind.THOUGHT, entry.id),
        canvasOf = ::bookShareCanvas,
        controls = {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ShareGround.entries.forEach { g ->
                    ShareChip(g.label, ground == g, Modifier.weight(1f), swatch = g.bg) { ground = g }
                }
            }
        }
    ) { format, modifier ->
        ShareCard(
            title = entry.book?.title.orEmpty(),
            author = entry.book?.authorLine.orEmpty(),
            year = null,
            coverUrl = entry.book?.coverUrl,
            handle = "@${entry.username}",
            rating = null,
            note = entry.plainText,
            statusLabel = threadLabel?.let { "Thread $it" } ?: "A thought",
            ground = ground,
            layout = ShareLayout.Quote,
            showRating = false,
            showNote = true,
            format = format,
            modifier = modifier
        )
    }
}
