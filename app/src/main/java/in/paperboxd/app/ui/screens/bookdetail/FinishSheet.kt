package `in`.paperboxd.app.ui.screens.bookdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.ui.components.BookCoverImage
import `in`.paperboxd.app.ui.components.EyebrowText
import `in`.paperboxd.app.ui.components.HL

/**
 * The finish moment: rate → note → who else loved it → what next. Finishing is
 * the most rewarding action in the product, so it connects the reader to other
 * readers instead of ending at a poster.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinishSheet(
    state: BookDetailUiState,
    initialRating: Int,
    onRate: (Int) -> Unit,
    onWriteNote: () -> Unit,
    onOpenBook: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var rating by remember { mutableIntStateOf(initialRating) }
    val lovedBy = state.socialFriends.filter { it.status == "read" && (it.rating ?: 0) >= 4 }

    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = HL.Paper) {
        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                EyebrowText(text = "Finished")
                Text(
                    text = state.book?.title.orEmpty(),
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = HL.Ink
                )
                Text(
                    text = "What did you think? Your followers will see that you finished it.",
                    fontSize = 14.sp,
                    color = HL.Muted
                )
            }

            Row {
                (1..5).forEach { star ->
                    IconButton(onClick = { rating = star; onRate(star) }) {
                        Icon(
                            imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = "$star star${if (star == 1) "" else "s"}",
                            tint = HL.Ink
                        )
                    }
                }
            }

            Text(
                text = "Add a note",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = HL.Ink,
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, HL.Muted.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .clickable(onClick = onWriteNote)
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            )

            lovedBy.firstOrNull()?.let { first ->
                val others = lovedBy.size - 1
                val suffix = if (others > 0) " and $others other${if (others == 1) "" else "s"} you follow" else ""
                Text(
                    text = "${first.name ?: first.username}$suffix also loved this.",
                    fontSize = 14.sp,
                    color = HL.Ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(HL.Muted.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                )
            }

            if (state.similarBooks.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    EyebrowText(text = "Readers like you also loved")
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.similarBooks.take(3).forEach { item ->
                            Column(
                                modifier = Modifier.width(96.dp).clickable { onOpenBook(item.id) },
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                BookCoverImage(
                                    url = item.coverUrl,
                                    title = item.title,
                                    modifier = Modifier.width(96.dp).height(144.dp),
                                    cornerRadius = 4.dp
                                )
                                Text(text = item.title, fontSize = 12.sp, color = HL.Ink, maxLines = 2)
                            }
                        }
                    }
                }
            }
        }
    }
}
