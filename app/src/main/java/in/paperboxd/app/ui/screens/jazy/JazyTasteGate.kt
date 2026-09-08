package `in`.paperboxd.app.ui.screens.jazy

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.ui.navigation.PipFace
import `in`.paperboxd.app.ui.screens.scan.MonoLabel
import `in`.paperboxd.app.ui.screens.scan.SK

/**
 * Scan & Know is gated on knowing the reader.
 *
 * The scan's whole claim is "how well does this book fit *you*" — against an
 * empty shelf it has nothing to score with, so it reads as a random number.
 * Under [MINIMUM_SHELF] books (bookshelf + TBR) the camera asks for a few books
 * first instead of producing a confident-looking guess.
 * iOS twin: `JazyTaste`.
 */
object JazyTaste {
    const val MINIMUM_SHELF = 5
}

@Composable
fun JazyTasteGateSheet(shelfSize: Int, onDismiss: () -> Unit) {
    val remaining = (JazyTaste.MINIMUM_SHELF - shelfSize).coerceAtLeast(0)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.34f))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 28.dp)
                .padding(end = 5.dp, bottom = 5.dp)
                .hardShadow(SK.ink, 5.dp)
                .background(SK.panel)
                .border(2.dp, SK.ink)
                .padding(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                MonoLabel("Scan locked", size = 9.5f, tracking = 2.2f, color = SK.faint)
                Spacer(Modifier.weight(1f))
                PipFace(modifier = Modifier.size(30.dp, 32.dp).offset(y = (-4).dp))
            }

            Text(
                "Jazy works best when it knows your taste.",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 23.sp,
                lineHeight = 28.sp,
                color = SK.ink,
                modifier = Modifier.padding(top = 10.dp)
            )

            Text(
                "Scanning a cover scores it against what you already read. With " +
                    "$shelfSize book${if (shelfSize == 1) "" else "s"} on your shelf there is " +
                    "nothing to score against yet.",
                fontSize = 13.5.sp,
                lineHeight = 20.sp,
                color = SK.sub,
                modifier = Modifier.padding(top = 12.dp)
            )

            Box(
                Modifier
                    .padding(vertical = 16.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SK.ink.copy(alpha = 0.14f))
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                MonoLabel(
                    "Add $remaining more book${if (remaining == 1) "" else "s"}",
                    size = 10f, tracking = 1.6f, color = SK.ink
                )
                Spacer(Modifier.weight(1f))
                MonoLabel("$shelfSize / ${JazyTaste.MINIMUM_SHELF}",
                          size = 10f, tracking = 1.6f, color = SK.faint)
            }

            // Shelf progress as spines, in the feature's own vocabulary.
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
            ) {
                repeat(JazyTaste.MINIMUM_SHELF) { i ->
                    val filled = i < shelfSize
                    Box(
                        Modifier
                            .weight(1f)
                            .height(26.dp)
                            .background(if (filled) SK.spines[i % SK.spines.size] else Color.Transparent)
                            .border(
                                if (filled) 2.dp else 1.5.dp,
                                if (filled) SK.ink else SK.ink.copy(alpha = 0.22f)
                            )
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(top = 20.dp, end = 4.dp, bottom = 4.dp)
                    .fillMaxWidth()
                    .hardShadow(SK.ink.copy(alpha = 0.78f))
                    .background(SK.ink)
                    .border(2.dp, SK.ink)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss
                    )
                    .height(52.dp)
            ) {
                MonoLabel("Ask Jazy instead", size = 12f, tracking = 1.4f,
                          color = SK.panel, weight = FontWeight.SemiBold)
            }

            MonoLabel("Vibe search needs no shelf — that one is open",
                      size = 9f, tracking = 1.4f, color = SK.faint,
                      modifier = Modifier.padding(top = 4.dp))
        }
    }
}
