package `in`.paperboxd.app.ui.screens.wrapped

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * The way into Monthly Wrapped from your own profile. Deliberately quiet — the
 * story behind it is not. Twin of iOS WrappedEntryCard.swift.
 */
@Composable
fun WrappedEntryCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    monthName: String = LocalDate.now().month.getDisplayName(TextStyle.FULL, Locale.getDefault())
) {
    Row(
        modifier
            .fillMaxWidth()
            .background(PBW.Ink)
            .border(1.dp, PBW.Terra.copy(alpha = 0.45f))
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                "MONTHLY WRAPPED",
                fontFamily = PBW.Mono, fontSize = 9.5.sp, letterSpacing = 1.6.sp, color = PBW.Terra
            )
            Text(
                "Your $monthName, in fourteen chapters.",
                fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                fontSize = 19.sp, lineHeight = 24.sp, color = PBW.Cream,
                maxLines = 2, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
        Text("→", fontFamily = PBW.Display, fontWeight = FontWeight.Black, fontSize = 24.sp, color = PBW.Terra)
    }
}
