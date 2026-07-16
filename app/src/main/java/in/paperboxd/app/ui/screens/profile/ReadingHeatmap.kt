package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.domain.model.ReadingActivity
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate
import java.time.LocalDate
import java.time.Year

// Terracotta ramp (empty → most) — iOS ReadingHeatmapView.ramp twin.
private val Ramp = listOf(
    Color(0xFFECE7E1), // 0 · empty
    Color(0xFFF4D2BD), // 1
    Color(0xFFE7A87D), // 2
    Color(0xFFD97A4A), // 3
    Color(0xFFBF5526), // 4
)

private fun level(pages: Int): Int = when {
    pages < 1 -> 0
    pages <= 12 -> 1
    pages <= 30 -> 2
    pages <= 55 -> 3
    else -> 4
}

private fun grouped(n: Int): String = "%,d".format(n)

/**
 * GitHub-style reading heatmap on a brutalist plate — iOS ReadingHeatmapView twin.
 * Mono eyebrow + year tabs, big page count, month-labelled pages-per-day grid,
 * LESS→MORE legend with the streak line.
 */
@Composable
fun ReadingHeatmap(
    activity: ReadingActivity,
    selectedYear: Int,
    onSelectYear: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cell = 9.dp
    val gap = 3.dp

    // Week columns of 7 day-levels (Sun→Sat); null = pad cell outside the year.
    val grid = remember(activity) { buildGrid(activity) }
    val months = remember(activity) { monthMarkers(activity) }

    val scroll = rememberScrollState()
    // Server caps the current year at today — land on the current week, not January.
    LaunchedEffect(activity) { scroll.scrollTo(scroll.maxValue) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .brutalPlate(fill = HL.Paper, borderWidth = 2.dp, offset = 5.dp)
            .padding(16.dp)
    ) {
        // header: eyebrow + year tabs
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "READING ACTIVITY",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = HL.Muted
            )
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                val now = Year.now().value
                (now - 2..now).forEach { yr ->
                    val active = yr == selectedYear
                    Column(
                        modifier = Modifier.clickable { onSelectYear(yr) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "'%02d".format(yr % 100),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (active) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 11.sp,
                            color = if (active) HL.Ink else HL.Muted
                        )
                        Box(
                            Modifier
                                .width(16.dp)
                                .height(2.dp)
                                .background(if (active) Ramp[4] else Color.Transparent)
                        )
                    }
                }
            }
        }

        // page count
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                grouped(activity.totalPages),
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 34.sp,
                color = HL.Ink
            )
            Text(
                "PAGES",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = HL.Muted,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        // month header + grid, one horizontal scroller
        Column(
            modifier = Modifier
                .padding(top = 14.dp)
                .horizontalScroll(scroll),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                months.forEach { label ->
                    Box(Modifier.width(cell).height(12.dp)) {
                        if (label.isNotEmpty()) {
                            Text(
                                label,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 8.5.sp,
                                color = HL.Muted,
                                maxLines = 1,
                                softWrap = false,
                                // let the 3-letter label spill over the next cells
                                modifier = Modifier.wrapContentWidth(
                                    align = Alignment.Start,
                                    unbounded = true
                                )
                            )
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                grid.forEach { col ->
                    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
                        col.forEach { lvl ->
                            Box(
                                Modifier
                                    .size(cell)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(lvl?.let { Ramp[it] } ?: Color.Transparent)
                            )
                        }
                    }
                }
            }
        }

        // legend + streak line
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(top = 14.dp)
        ) {
            Text("LESS", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = HL.Muted)
            Ramp.forEach { c ->
                Box(Modifier.size(cell).clip(RoundedCornerShape(2.dp)).background(c))
            }
            Text("MORE", fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = HL.Muted)
            Spacer(Modifier.weight(1f))
            Text(
                "${activity.daysRead} DAYS · ",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 9.5.sp,
                color = HL.Muted,
                maxLines = 1
            )
            Text(
                "${activity.longestStreak}-DAY STREAK",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Medium,
                fontSize = 9.5.sp,
                color = HL.Ink,
                maxLines = 1
            )
        }
    }
}

private fun buildGrid(activity: ReadingActivity): List<List<Int?>> {
    val start = runCatching { LocalDate.parse(activity.start) }.getOrNull() ?: return emptyList()
    val end = runCatching { LocalDate.parse(activity.end) }.getOrNull() ?: return emptyList()
    val pagesByDate = activity.days.associate { it.date to it.pages }

    // Sunday-first grid, same as iOS (Calendar weekday 1 = Sunday).
    val startPad = start.dayOfWeek.value % 7           // Sun=0 … Sat=6
    val gridStart = start.minusDays(startPad.toLong())
    val endPad = 6 - (end.dayOfWeek.value % 7)
    val gridEnd = end.plusDays(endPad.toLong())

    val cols = mutableListOf<List<Int?>>()
    var cur = gridStart
    while (!cur.isAfter(gridEnd)) {
        val col = (0 until 7).map { i ->
            val d = cur.plusDays(i.toLong())
            if (!d.isBefore(start) && !d.isAfter(end)) level(pagesByDate[d.toString()] ?: 0) else null
        }
        cols.add(col)
        cur = cur.plusDays(7)
    }
    return cols
}

private fun monthMarkers(activity: ReadingActivity): List<String> {
    val start = runCatching { LocalDate.parse(activity.start) }.getOrNull() ?: return emptyList()
    val end = runCatching { LocalDate.parse(activity.end) }.getOrNull() ?: return emptyList()
    val names = listOf("JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC")

    val startPad = start.dayOfWeek.value % 7
    val gridStart = start.minusDays(startPad.toLong())
    val endPad = 6 - (end.dayOfWeek.value % 7)
    val gridEnd = end.plusDays(endPad.toLong())

    val markers = mutableListOf<String>()
    var lastMonth = -1
    var cur = gridStart
    while (!cur.isAfter(gridEnd)) {
        // month of the first in-year day of this column
        var repMonth = -1
        for (i in 0 until 7) {
            val d = cur.plusDays(i.toLong())
            if (!d.isBefore(start) && !d.isAfter(end)) {
                repMonth = d.monthValue - 1
                break
            }
        }
        if (repMonth >= 0 && repMonth != lastMonth) {
            markers.add(names[repMonth])
            lastMonth = repMonth
        } else {
            markers.add("")
        }
        cur = cur.plusDays(7)
    }
    return markers
}
