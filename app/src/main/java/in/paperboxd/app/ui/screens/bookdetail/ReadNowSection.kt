package `in`.paperboxd.app.ui.screens.bookdetail

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import `in`.paperboxd.app.domain.model.BookReadLinks
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalButton

/**
 * "Read now" — store links out of the app, one brutalist button per store the
 * book is on. Caller supplies the section header so numbering stays with its
 * siblings in BookDetailScreen.
 *
 * iOS twin: `ReadNowSection.swift`.
 */
@Composable
fun ReadNowSection(
    links: BookReadLinks,
    header: @Composable () -> Unit,
    /** Opens the in-app reader; non-null only when the book is public domain. */
    onReadFree: (() -> Unit)? = null
) {
    val context = LocalContext.current
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        header()
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            if (onReadFree != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .brutalButton(onClick = onReadFree, fill = HL.Accent, offset = 3.dp)
                        .height(46.dp)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = HL.Paper, modifier = Modifier.size(16.dp))
                    Text(
                        "READ FREE",
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        color = HL.Paper,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        "PUBLIC DOMAIN",
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 9.sp,
                        letterSpacing = 1.sp,
                        color = HL.Paper.copy(alpha = 0.8f)
                    )
                }
            }
            links.stores.forEach { (label, url) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .brutalButton(
                            onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, url.toUri())) },
                            fill = HL.Paper,
                            offset = 3.dp
                        )
                        .height(46.dp)
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.MenuBook, null, tint = HL.Accent, modifier = Modifier.size(16.dp))
                    Text(
                        label.uppercase(),
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp,
                        color = HL.Ink,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(Icons.AutoMirrored.Filled.OpenInNew, null, tint = HL.Muted, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}
