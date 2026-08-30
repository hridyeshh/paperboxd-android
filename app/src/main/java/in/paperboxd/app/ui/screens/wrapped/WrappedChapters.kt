package `in`.paperboxd.app.ui.screens.wrapped

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.domain.model.Wrapped
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToInt

// The chapters of Monthly Wrapped, laid out against the 402dp design grid that
// WrappedStoryScreen scales to the device. Twin of iOS WrappedChapters.swift.

/** One page of the story. [isLight] chapters are dark-on-light: the chrome flips. */
data class WrappedChapter(
    val id: String,
    val label: String,
    val isLight: Boolean,
    val content: @Composable (Wrapped) -> Unit
)

/**
 * The story skips chapters the month cannot fill — a reader who finished
 * nothing gets no best-book page rather than an empty one.
 */
fun wrappedChapters(w: Wrapped): List<WrappedChapter> = buildList {
    add(WrappedChapter("cover", "Cover", false) { CoverChapter(it) })
    add(WrappedChapter("pages", "Pages", false) { PagesChapter(it) })
    if (w.books.isNotEmpty()) add(WrappedChapter("books", "Top books", true) { BooksChapter(it) })
    if (w.authors.isNotEmpty()) add(WrappedChapter("authors", "Authors", false) { AuthorsChapter(it) })
    if (w.genres.isNotEmpty()) add(WrappedChapter("genres", "Genres", true) { GenresChapter(it) })
    add(WrappedChapter("rhythm", "Rhythm", false) { RhythmChapter(it) })
    if (w.streak.days > 0) add(WrappedChapter("streak", "Streak", false) { StreakChapter(it) })
    if (w.topRated != null) add(WrappedChapter("top-rated", "Best book", true) { TopRatedChapter(it) })
    if (w.abandoned != null) add(WrappedChapter("abandoned", "Abandoned", false) { AbandonedChapter(it) })
    add(WrappedChapter("rank", "Rank", true) { RankChapter(it) })
    add(WrappedChapter("type", "Your type", false) { ArchetypeChapter(it) })
    add(WrappedChapter("dare", "The dare", true) { DareChapter(it) })
    add(WrappedChapter("card", "The card", false) { CardChapter(it) })
    add(WrappedChapter("outro", "Outro", false) { OutroChapter(it) })
}

// MARK: - 01 Cover

@Composable
private fun CoverChapter(w: Wrapped) {
    Box(Modifier.fillMaxSize().background(PBW.Ink).clipToBounds()) {
        PbStamp(delayMillis = 80, durationMillis = 620, modifier = Modifier.offset((-70).dp, 168.dp)) {
            Box(Modifier.size(268.dp).clip(CircleShape).background(PBW.Terra))
        }
        PbStamp(delayMillis = 240, durationMillis = 620, modifier = Modifier.offset(284.dp, 372.dp)) {
            Box(Modifier.size(152.dp).clip(CircleShape).background(PBW.Amber))
        }
        Column(
            Modifier.fillMaxSize().padding(top = 76.dp, bottom = 112.dp).padding(horizontal = 30.dp)
        ) {
            PbFade { Text("PaperBoxd", fontFamily = PBW.Script, fontSize = 34.sp, color = PBW.Cream) }
            PbKicker("Monthly Wrapped", delayMillis = 160, modifier = Modifier.padding(top = 6.dp))
            Spacer(Modifier.weight(1f))
            PbRise(delayMillis = 420) {
                Text(
                    w.month.uppercase(),
                    fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                    fontSize = 72.sp, lineHeight = 62.sp, letterSpacing = (-2).sp, color = PBW.Cream
                )
            }
            PbFade(delayMillis = 620, modifier = Modifier.padding(top = 14.dp)) {
                Text(w.year, fontFamily = PBW.Mono, fontSize = 15.sp, letterSpacing = 6.3.sp, color = PBW.Amber)
            }
            PbRule(delayMillis = 760, color = PBW.Cream.copy(alpha = 0.35f), thickness = 1.5.dp,
                modifier = Modifier.padding(top = 22.dp))
            PbFade(delayMillis = 860, modifier = Modifier.padding(top = 16.dp)) {
                Column {
                    Text(
                        if (w.totals.books > 0) "${w.totals.books} books. One very specific mood."
                        else "${w.totals.pages.formatted()} pages. One very specific mood.",
                        fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                        fontSize = 19.sp, lineHeight = 27.sp, color = PBW.Cream
                    )
                    Text(
                        w.reader.handle.uppercase(),
                        fontFamily = PBW.Mono, fontSize = 11.sp, letterSpacing = 1.5.sp,
                        color = PBW.Muted, modifier = Modifier.padding(top = 12.dp)
                    )
                }
            }
        }
    }
}

// MARK: - 02 Pages

@Composable
private fun PagesChapter(w: Wrapped) {
    WrappedScreen {
        PbKicker("You turned")
        PbRise(delayMillis = 180, modifier = Modifier.padding(top = 14.dp)) {
            PbCount(w.totals.pages, delayMillis = 260, durationMillis = 1600, fontSize = 92)
        }
        PbRise(delayMillis = 320) {
            Text("pages", fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                fontSize = 44.sp, lineHeight = 46.sp, color = PBW.Cream)
        }

        // The stack of set lines — a page of type, abstracted.
        Column(
            Modifier.padding(top = 34.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(26) { i ->
                PbGrow(delayMillis = 520 + i * 34, durationMillis = 620) {
                    Box(
                        Modifier
                            .fillMaxWidth((34 + ((i * 37) % 66)) / 100f)
                            .height(3.dp)
                            .background(if (i % 5 == 4) PBW.Terra else PBW.Cream.copy(alpha = 0.3f))
                    )
                }
            }
        }

        Spacer(Modifier.weight(1f))
        PbRule(delayMillis = 1500, color = PBW.Cream.copy(alpha = 0.28f), thickness = 1.dp,
            modifier = Modifier.padding(bottom = 18.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(30.dp)) {
            PagesStat("Hours", "${w.totals.estimatedHours}h ${w.totals.estimatedMinutes}m", 1560)
            PagesStat("Sittings", "${w.totals.sessions}", 1670)
            PagesStat("Biggest day", "${w.totals.biggestDayPages}p", 1780)
        }
    }
}

@Composable
private fun PagesStat(label: String, value: String, delayMillis: Int) {
    PbFade(delayMillis) {
        Column {
            Text(label.uppercase(), fontFamily = PBW.Mono, fontSize = 9.5.sp,
                letterSpacing = 1.5.sp, color = PBW.Muted)
            Text(value, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp, color = PBW.Cream, modifier = Modifier.padding(top = 5.dp))
        }
    }
}

// MARK: - 03 Top books

@Composable
private fun BooksChapter(w: Wrapped) {
    WrappedScreen(background = PBW.Cream) {
        PbKicker(if (w.books.size >= 5) "Your top five" else "What you read", color = PBW.TerraDeep)
        PbRise(delayMillis = 140, modifier = Modifier.padding(top = 10.dp)) {
            Text("The books", fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp, letterSpacing = (-1).sp, color = PBW.Ink)
        }
        Column(Modifier.padding(top = 26.dp)) {
            w.books.forEachIndexed { i, book ->
                PbSlide(delayMillis = 340 + i * 130, from = SlideFrom.Start, distance = 26.dp) {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 13.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            "%02d".format(i + 1),
                            fontFamily = PBW.Mono, fontSize = 11.sp,
                            color = if (i == 0) PBW.Terra else PBW.Ink.copy(alpha = 0.4f),
                            modifier = Modifier.width(20.dp)
                        )
                        Box(Modifier.width(26.dp).height(39.dp).background(PBW.spine(i))) {
                            Box(Modifier.offset(x = 2.dp).width(1.dp).fillMaxHeight()
                                .background(Color.Black.copy(alpha = 0.25f)))
                        }
                        Column(Modifier.weight(1f)) {
                            Text(book.title, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                                fontSize = if (i == 0) 21.sp else 17.sp, lineHeight = 24.sp,
                                color = PBW.Ink, maxLines = 2, overflow = TextOverflow.Ellipsis)
                            Text(book.author, fontFamily = PBW.Sans, fontSize = 12.sp,
                                color = PBW.Ink.copy(alpha = 0.52f), maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(top = 3.dp))
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("${book.pages}p", fontFamily = PBW.Mono, fontSize = 10.5.sp,
                                color = PBW.Ink.copy(alpha = 0.45f))
                            Text("${book.days}d", fontFamily = PBW.Mono, fontSize = 10.5.sp,
                                color = PBW.Ink.copy(alpha = 0.3f))
                        }
                    }
                }
                if (i < w.books.lastIndex) {
                    PbGrow(delayMillis = 420 + i * 130, durationMillis = 500) {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(PBW.Ink.copy(alpha = 0.12f)))
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        w.books.firstOrNull()?.let { top ->
            PbFade(delayMillis = 1100) {
                Text(
                    "You spent the most of the month with ${top.title} — ${top.pages} pages across ${top.days} ${if (top.days == 1) "day" else "days"}.",
                    fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                    fontSize = 15.5.sp, lineHeight = 22.sp, color = PBW.Ink.copy(alpha = 0.62f)
                )
            }
        }
    }
}

// MARK: - 04 Authors

@Composable
private fun AuthorsChapter(w: Wrapped) {
    WrappedScreen {
        PbKicker("Most read author")
        w.authors.firstOrNull()?.let { top ->
            Column(Modifier.padding(top = 16.dp)) {
                top.name.split(" ").forEachIndexed { i, word ->
                    PbRise(delayMillis = 200 + i * 110) {
                        Text(word, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                            fontSize = 46.sp, lineHeight = 46.sp, letterSpacing = (-1.3).sp, color = PBW.Amber)
                    }
                }
            }
            PbFade(delayMillis = 520, modifier = Modifier.padding(top = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                    AuthorCount(top.books, "BOOKS")
                    AuthorCount(top.pages, "PAGES")
                }
            }
            top.note?.let { note ->
                PbFade(delayMillis = 660, modifier = Modifier.padding(top = 14.dp)) {
                    Text(
                        note.uppercase(),
                        fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.2.sp, color = PBW.Terra,
                        modifier = Modifier.border(1.dp, PBW.Terra).padding(horizontal = 11.dp, vertical = 6.dp)
                    )
                }
            }
        }
        PbRule(delayMillis = 800, color = PBW.Cream.copy(alpha = 0.25f), thickness = 1.dp,
            modifier = Modifier.padding(top = 30.dp, bottom = 4.dp))
        w.authors.drop(1).forEachIndexed { i, author ->
            PbSlide(delayMillis = 880 + i * 110, from = SlideFrom.End, distance = 22.dp) {
                Column {
                    Row(
                        Modifier.fillMaxWidth().padding(vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("%02d".format(i + 2), fontFamily = PBW.Mono, fontSize = 10.sp,
                            color = PBW.Muted, modifier = Modifier.width(18.dp))
                        Text(author.name, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                            fontSize = 19.sp, color = PBW.Cream, maxLines = 1,
                            overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                        Text("${author.pages}p", fontFamily = PBW.Mono, fontSize = 10.5.sp, color = PBW.Muted)
                    }
                    Box(Modifier.fillMaxWidth().height(1.dp).background(PBW.Cream.copy(alpha = 0.1f)))
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun AuthorCount(n: Int, label: String) {
    Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
        Text("$n", fontFamily = PBW.Display, fontWeight = FontWeight.Black, fontSize = 32.sp, color = PBW.Cream)
        Text(label, fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.4.sp,
            color = PBW.Muted, modifier = Modifier.padding(bottom = 4.dp))
    }
}

// MARK: - 05 Genres

@Composable
private fun GenresChapter(w: Wrapped) {
    WrappedScreen(background = PBW.Terra) {
        PbKicker("What you reached for", color = PBW.Ink.copy(alpha = 0.6f))
        PbRise(delayMillis = 140, modifier = Modifier.padding(top = 10.dp)) {
            Text("Your genres", fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp, letterSpacing = (-1).sp, color = PBW.Ink)
        }
        Column(
            Modifier.padding(vertical = 24.dp).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            w.genres.forEachIndexed { i, genre ->
                Row(
                    Modifier.fillMaxWidth().height(max(44, (genre.pct + 13) * 2).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    PbGrow(delayMillis = 380 + i * 140, durationMillis = 760) {
                        Box(
                            Modifier
                                .width(max(80f, genre.pct * 1.34f / 100f * 342f).dp)
                                .fillMaxHeight()
                                .background(PBW.genre(i))
                        )
                    }
                    PbFade(delayMillis = 620 + i * 140, modifier = Modifier.padding(start = 12.dp)) {
                        Column {
                            Text(genre.name, fontFamily = PBW.Sans, fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp, color = PBW.Ink, maxLines = 2,
                                overflow = TextOverflow.Ellipsis)
                            Text("${genre.pct}%", fontFamily = PBW.Mono, fontSize = 10.5.sp,
                                color = PBW.Ink.copy(alpha = 0.6f), modifier = Modifier.padding(top = 2.dp))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        w.genres.firstOrNull()?.let { top ->
            PbFade(delayMillis = 1240) {
                Text(
                    "${top.pct}% of everything you read this month was ${top.name.lowercase()}.",
                    fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                    fontSize = 16.sp, lineHeight = 23.sp, color = PBW.Ink.copy(alpha = 0.78f)
                )
            }
        }
    }
}

// MARK: - 06 Rhythm

@Composable
private fun RhythmChapter(w: Wrapped) {
    WrappedScreen {
        PbKicker("Your reading rhythm")
        Column(Modifier.padding(top = 12.dp)) {
            w.rhythm.label.split(" ").forEachIndexed { i, word ->
                PbRise(delayMillis = 180 + i * 110) {
                    Text(word.uppercase(), fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                        fontSize = 60.sp, lineHeight = 54.sp, letterSpacing = (-1.8).sp,
                        color = if (i == 0) PBW.Cream else PBW.Amber)
                }
            }
        }
        PbFade(delayMillis = 520, modifier = Modifier.padding(top = 16.dp)) {
            Text(w.rhythm.line, fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                fontSize = 17.sp, lineHeight = 24.sp, color = PBW.Cream.copy(alpha = 0.8f))
        }
        Spacer(Modifier.weight(1f))
        Row(
            Modifier.fillMaxWidth().height(168.dp).padding(bottom = 10.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(2.5.dp)
        ) {
            w.rhythm.hours.forEachIndexed { hour, value ->
                PbGrow(delayMillis = 700 + hour * 32, durationMillis = 640, vertical = true,
                    modifier = Modifier.weight(1f)) {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .height(max(3f, value / 100f * 158f).dp)
                            .background(if (hour >= 22 || hour <= 1) PBW.Amber else PBW.Cream.copy(alpha = 0.34f))
                    )
                }
            }
        }
        PbFade(delayMillis = 1560) {
            Column {
                Box(Modifier.fillMaxWidth().height(1.dp).background(PBW.Cream.copy(alpha = 0.16f)))
                Row(
                    Modifier.fillMaxWidth().padding(top = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("12A", "6A", "12P", "6P", "11P").forEach {
                        Text(it, fontFamily = PBW.Mono, fontSize = 9.sp, letterSpacing = 0.9.sp, color = PBW.Muted)
                    }
                }
            }
        }
        PbFade(delayMillis = 1680, modifier = Modifier.padding(top = 18.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(26.dp)) {
                RhythmStat("PEAK", w.rhythm.peak, PBW.Amber)
                RhythmStat("AFTER MIDNIGHT", "${w.rhythm.pctAfterMidnight}%", PBW.Cream)
            }
        }
    }
}

@Composable
private fun RhythmStat(label: String, value: String, color: Color) {
    Column {
        Text(label, fontFamily = PBW.Mono, fontSize = 9.5.sp, letterSpacing = 1.5.sp, color = PBW.Muted)
        Text(value, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp, color = color, modifier = Modifier.padding(top = 4.dp))
    }
}

// MARK: - 07 Streak

@Composable
private fun StreakChapter(w: Wrapped) {
    val peak = max(w.streak.calendar.maxOrNull() ?: 1, 1)
    WrappedScreen {
        PbKicker("Longest streak")
        Row(
            Modifier.padding(top = 12.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            PbRise(delayMillis = 180) {
                PbCount(w.streak.days, delayMillis = 240, durationMillis = 1100, fontSize = 96)
            }
            PbRise(delayMillis = 340) {
                Text("days", fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                    fontSize = 34.sp, color = PBW.Cream, modifier = Modifier.padding(bottom = 8.dp))
            }
        }
        PbFade(delayMillis = 560, modifier = Modifier.padding(top = 12.dp)) {
            Text(
                "${w.streak.start} to ${w.streak.end}." +
                    if (w.streak.broke.isNotEmpty()) " Then ${w.streak.broke} happened." else "",
                fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                fontSize = 16.5.sp, lineHeight = 23.sp, color = PBW.Cream.copy(alpha = 0.78f)
            )
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            userScrollEnabled = false,
            modifier = Modifier.padding(top = 30.dp).fillMaxWidth().wrapContentHeight()
        ) {
            itemsIndexed(w.streak.calendar) { i, pages ->
                PbStamp(delayMillis = 820 + i * 26, durationMillis = 340) {
                    Box(Modifier.fillMaxWidth().aspectRatio(1f), contentAlignment = Alignment.Center) {
                        when {
                            i == w.streak.brokeIndex -> {
                                Box(Modifier.fillMaxSize().border(1.5.dp, PBW.Terra))
                                Text("×", fontFamily = PBW.Mono, fontSize = 13.sp, color = PBW.Terra)
                            }
                            w.streak.isInStreak(i) -> Box(
                                Modifier.fillMaxSize()
                                    .background(PBW.Amber.copy(alpha = max(0.42f, pages.toFloat() / peak)))
                            )
                            else -> Box(Modifier.fillMaxSize().background(PBW.Cream.copy(alpha = 0.13f)))
                        }
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
        PbFade(delayMillis = 1700) {
            Column {
                Box(Modifier.fillMaxWidth().height(1.dp).background(PBW.Cream.copy(alpha = 0.16f)))
                Row(
                    Modifier.fillMaxWidth().padding(top = 14.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("YOUR RECORD", fontFamily = PBW.Mono, fontSize = 9.5.sp,
                            letterSpacing = 1.5.sp, color = PBW.Muted)
                        Text("${w.streak.longestEver} days", fontFamily = PBW.Display,
                            fontWeight = FontWeight.SemiBold, fontSize = 21.sp, color = PBW.Cream,
                            modifier = Modifier.padding(top = 4.dp))
                    }
                    val gap = w.streak.longestEver - w.streak.days
                    Text(
                        if (gap > 0) "$gap SHORT" else "YOUR BEST YET",
                        fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.sp, color = PBW.Terra
                    )
                }
            }
        }
    }
}

// MARK: - 08 The month's best book

@Composable
private fun TopRatedChapter(w: Wrapped) {
    val f = w.topRated ?: return
    WrappedScreen(background = PBW.Cream) {
        PbKicker(if (f.rating >= 5) "The only five star" else "The best of the month", color = PBW.TerraDeep)
        Row(
            Modifier.padding(top = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            PbBookBlock(f.title, f.author, PBW.spine(1), Color(0xFFE9E2CF), 104.dp, 260, -3f)
            Column(Modifier.weight(1f).padding(top = 4.dp)) {
                PbRise(delayMillis = 420) {
                    Text(f.title, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                        fontSize = 27.sp, lineHeight = 30.sp, color = PBW.Ink)
                }
                PbFade(delayMillis = 540, modifier = Modifier.padding(top = 6.dp)) {
                    Text(f.author, fontFamily = PBW.Sans, fontSize = 13.sp, color = PBW.Ink.copy(alpha = 0.55f))
                }
                Row(
                    Modifier.padding(top = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(f.rating.roundToInt()) { i ->
                        PbStamp(delayMillis = 700 + i * 90, durationMillis = 300) {
                            Text("★", fontSize = 17.sp, color = PBW.Terra)
                        }
                    }
                }
                PbFade(delayMillis = 1180, modifier = Modifier.padding(top = 12.dp)) {
                    Text(f.date.uppercase(), fontFamily = PBW.Mono, fontSize = 10.sp,
                        letterSpacing = 1.2.sp, color = PBW.Ink.copy(alpha = 0.45f))
                }
            }
        }
        Spacer(Modifier.weight(1f))
        PbRule(delayMillis = 1280, color = PBW.Ink.copy(alpha = 0.2f), thickness = 1.dp,
            modifier = Modifier.padding(bottom = 20.dp))
        PbFade(delayMillis = 1360) {
            Column {
                Text(
                    if (f.review.isEmpty()) "No review — some books do not need one." else "“${f.review}”",
                    fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                    fontSize = 20.sp, lineHeight = 29.sp, color = PBW.Ink
                )
                Text(
                    "YOUR REVIEW · ${w.reader.handle.uppercase()}",
                    fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.4.sp,
                    color = PBW.Ink.copy(alpha = 0.45f), modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}

// MARK: - 09 The one left unfinished

@Composable
private fun AbandonedChapter(w: Wrapped) {
    val a = w.abandoned ?: return
    WrappedScreen {
        PbKicker("Left unfinished", color = PBW.Terra)
        Box(Modifier.fillMaxWidth().padding(top = 24.dp), contentAlignment = Alignment.Center) {
            Box {
                PbBookBlock(a.title, a.author, PBW.spine(5), Color(0xFFEFE6F2), 128.dp, 240, 4f)
                // The bookmark still sitting in it.
                PbSlide(
                    delayMillis = 720, durationMillis = 620, from = SlideFrom.Top, distance = 40.dp,
                    modifier = Modifier.align(Alignment.TopEnd).offset(x = (-20).dp, y = (-18).dp)
                ) {
                    Box(Modifier.width(16.dp).height(74.dp).background(PBW.Terra))
                }
            }
        }
        PbFade(delayMillis = 900, modifier = Modifier.padding(top = 26.dp)) {
            Column {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("PAGE ${a.page}", fontFamily = PBW.Mono, fontSize = 10.sp,
                        letterSpacing = 1.2.sp, color = PBW.Muted)
                    Text("${a.of} TOTAL", fontFamily = PBW.Mono, fontSize = 10.sp,
                        letterSpacing = 1.2.sp, color = PBW.Muted)
                }
                Box(
                    Modifier.padding(top = 8.dp).fillMaxWidth().height(5.dp)
                        .background(PBW.Cream.copy(alpha = 0.16f))
                ) {
                    PbGrow(delayMillis = 1020, durationMillis = 900) {
                        Box(
                            Modifier.fillMaxWidth(a.pctRead / 100f).height(5.dp).background(PBW.Terra)
                        )
                    }
                }
                Text("${a.pctRead}% IN", fontFamily = PBW.Mono, fontSize = 10.sp,
                    letterSpacing = 1.2.sp, color = PBW.Terra, modifier = Modifier.padding(top = 8.dp))
            }
        }
        PbFade(delayMillis = 1300, modifier = Modifier.padding(top = 28.dp)) {
            Text(a.roast, fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                fontSize = 21.sp, lineHeight = 30.sp, color = PBW.Cream)
        }
        Spacer(Modifier.weight(1f))
        PbFade(delayMillis = 1560) {
            Column {
                Box(Modifier.fillMaxWidth().height(1.dp).background(PBW.Cream.copy(alpha = 0.16f)))
                Text(
                    "STARTED ${a.started.uppercase()} · LAST OPENED ${a.lastOpened.uppercase()}",
                    fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.2.sp,
                    color = PBW.Muted, modifier = Modifier.padding(top = 13.dp)
                )
            }
        }
    }
}

// MARK: - 10 Rank

@Composable
private fun RankChapter(w: Wrapped) {
    // A reader distribution: most people finish a little, a few finish a lot.
    val curve = remember { (0..29).map { exp(-((it - 9) / 6.4).pow(2)) * 100 } }
    val youAt = (29 - (w.rank.percentile / 100f * 30f).roundToInt()).coerceIn(0, 29)

    WrappedScreen(background = PBW.Amber) {
        PbKicker("Against everybody else", color = PBW.Ink.copy(alpha = 0.6f))
        PbRise(delayMillis = 180, modifier = Modifier.padding(top = 14.dp)) {
            Text("TOP ${w.rank.percentile}%", fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                fontSize = 88.sp, lineHeight = 76.sp, letterSpacing = (-3).sp, color = PBW.Ink)
        }
        PbFade(delayMillis = 520, modifier = Modifier.padding(top = 16.dp)) {
            Text(w.rank.line, fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                fontSize = 18.sp, lineHeight = 26.sp, color = PBW.Ink.copy(alpha = 0.8f))
        }
        Spacer(Modifier.weight(1f))
        Box(Modifier.fillMaxWidth().height(150.dp)) {
            Row(
                Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                curve.forEachIndexed { i, value ->
                    PbGrow(delayMillis = 760 + i * 26, durationMillis = 600, vertical = true,
                        modifier = Modifier.weight(1f)) {
                        Box(
                            Modifier.fillMaxWidth()
                                .height(max(4.0, value / 100 * 150).dp)
                                .background(if (i >= youAt) PBW.Ink else PBW.Ink.copy(alpha = 0.26f))
                        )
                    }
                }
            }
            PbFade(delayMillis = 1620, modifier = Modifier.align(Alignment.TopEnd)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("YOU\nARE\nHERE", fontFamily = PBW.Mono, fontSize = 10.sp,
                        letterSpacing = 1.sp, textAlign = TextAlign.End, color = PBW.Ink)
                    Box(Modifier.width(1.5.dp).height(150.dp).background(PBW.Ink))
                }
            }
        }
        PbFade(delayMillis = 1740, modifier = Modifier.padding(top = 6.dp)) {
            Column {
                Box(Modifier.fillMaxWidth().height(1.dp).background(PBW.Ink.copy(alpha = 0.3f)))
                Row(
                    Modifier.fillMaxWidth().padding(top = 9.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("0 PAGES", fontFamily = PBW.Mono, fontSize = 9.5.sp,
                        letterSpacing = 1.sp, color = PBW.Ink.copy(alpha = 0.6f))
                    Text("OF ${w.rank.readers.formatted()} READERS", fontFamily = PBW.Mono,
                        fontSize = 9.5.sp, letterSpacing = 1.sp, color = PBW.Ink.copy(alpha = 0.6f))
                }
            }
        }
    }
}

// MARK: - 11 Reading personality

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ArchetypeChapter(w: Wrapped) {
    Box(Modifier.fillMaxSize().background(PBW.Ink).clipToBounds()) {
        PbStamp(delayMillis = 300, durationMillis = 900, modifier = Modifier.offset(198.dp, 430.dp)) {
            Box(Modifier.size(300.dp).border(1.5.dp, PBW.Terra.copy(alpha = 0.4f), CircleShape))
        }
        Column(
            Modifier.fillMaxSize().padding(top = 76.dp, bottom = 112.dp).padding(horizontal = 30.dp)
        ) {
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    PbKicker(w.archetype.kicker, color = PBW.Terra)
                    Column(Modifier.padding(top = 16.dp)) {
                        w.archetype.name.split(" ").forEachIndexed { i, word ->
                            PbRise(delayMillis = 220 + i * 130) {
                                Text(
                                    word,
                                    fontFamily = PBW.Display,
                                    fontWeight = if (i == 0) FontWeight.Normal else FontWeight.SemiBold,
                                    fontStyle = if (i == 0) FontStyle.Italic else FontStyle.Normal,
                                    fontSize = if (i == 0) 34.sp else 48.sp,
                                    lineHeight = if (i == 0) 36.sp else 50.sp,
                                    letterSpacing = (-1.5).sp,
                                    color = if (i == 1) PBW.Amber else PBW.Cream
                                )
                            }
                        }
                    }
                }
                PbStamp(delayMillis = 760, rotation = -7f, modifier = Modifier.padding(top = 8.dp)) {
                    Column(
                        Modifier.size(78.dp).border(1.5.dp, PBW.Terra, CircleShape).padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(w.archetype.statLabel.uppercase(), fontFamily = PBW.Mono, fontSize = 7.5.sp,
                            letterSpacing = 1.sp, textAlign = TextAlign.Center, maxLines = 2,
                            overflow = TextOverflow.Ellipsis, color = PBW.Terra)
                        Text(w.archetype.statValue, fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                            fontSize = 20.sp, color = PBW.Terra)
                    }
                }
            }
            PbRule(delayMillis = 900, color = PBW.Terra, thickness = 1.5.dp,
                modifier = Modifier.padding(top = 26.dp, bottom = 20.dp))
            PbFade(delayMillis = 980) {
                Text(w.archetype.definition, fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                    fontSize = 18.5.sp, lineHeight = 27.sp, color = PBW.Cream.copy(alpha = 0.88f))
            }
            androidx.compose.foundation.layout.FlowRow(
                Modifier.padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                w.archetype.traits.forEachIndexed { i, trait ->
                    PbFade(delayMillis = 1220 + i * 110) {
                        Text(
                            trait.uppercase(), fontFamily = PBW.Mono, fontSize = 9.sp,
                            letterSpacing = 0.6.sp, color = PBW.Cream,
                            modifier = Modifier.border(1.dp, PBW.Cream.copy(alpha = 0.28f))
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.weight(1f))
            PbFade(delayMillis = 1700) {
                Column {
                    Box(Modifier.fillMaxWidth().height(1.dp).background(PBW.Cream.copy(alpha = 0.16f)))
                    Row(
                        Modifier.padding(top = 13.dp),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Text("PAIRS WELL WITH", fontFamily = PBW.Mono, fontSize = 10.sp,
                            letterSpacing = 1.4.sp, color = PBW.Muted)
                        Text(w.archetype.pairs.uppercase(), fontFamily = PBW.Mono, fontSize = 10.sp,
                            letterSpacing = 1.4.sp, color = PBW.Amber)
                    }
                }
            }
        }
    }
}

// MARK: - 12 The dare

@Composable
private fun DareChapter(w: Wrapped) {
    WrappedScreen(background = PBW.Cream) {
        PbFade(delayMillis = 80) {
            Text(w.dare.tag, fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.6.sp,
                color = PBW.Cream,
                modifier = Modifier.background(PBW.Ink).padding(horizontal = 12.dp, vertical = 7.dp))
        }
        Column(Modifier.padding(top = 28.dp)) {
            val words = w.dare.title.split(" ")
            words.forEachIndexed { i, word ->
                val last = i == words.lastIndex
                PbRise(delayMillis = 280 + i * 110) {
                    Text(
                        word, fontFamily = PBW.Display,
                        fontWeight = FontWeight.SemiBold,
                        fontStyle = if (last) FontStyle.Italic else FontStyle.Normal,
                        fontSize = 42.sp, lineHeight = 45.sp, letterSpacing = (-1.3).sp,
                        color = if (last) PBW.Terra else PBW.Ink
                    )
                }
            }
        }
        PbRule(delayMillis = 860, color = PBW.Ink.copy(alpha = 0.25f), thickness = 1.dp,
            modifier = Modifier.padding(top = 30.dp, bottom = 22.dp))
        PbFade(delayMillis = 940) {
            Text(w.dare.body, fontFamily = PBW.Sans, fontSize = 15.5.sp, lineHeight = 24.sp,
                color = PBW.Ink.copy(alpha = 0.72f))
        }
        Spacer(Modifier.weight(1f))
        PbFade(delayMillis = 1200) {
            Row(
                Modifier.fillMaxWidth().border(1.5.dp, PBW.Ink)
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("${w.nextMonth.uppercase()} TARGET", fontFamily = PBW.Mono, fontSize = 9.5.sp,
                        letterSpacing = 1.5.sp, color = PBW.Ink.copy(alpha = 0.5f))
                    Text(w.dare.target, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                        fontSize = 23.sp, color = PBW.Ink, modifier = Modifier.padding(top = 4.dp))
                }
                Text("→", fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                    fontSize = 30.sp, color = PBW.Terra)
            }
        }
    }
}

// MARK: - 13 The card

@Composable
private fun CardChapter(w: Wrapped) {
    WrappedScreen(topPadding = 72.dp) {
        PbKicker("Your ${w.month}, in one card")
        Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.TopCenter) {
            PbStamp(delayMillis = 220, durationMillis = 640) {
                WrappedRecapCard(w)
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

// MARK: - 14 Outro

@Composable
private fun OutroChapter(w: Wrapped) {
    Box(Modifier.fillMaxSize().background(PBW.Ink).clipToBounds(), contentAlignment = Alignment.Center) {
        PbStamp(delayMillis = 120, durationMillis = 700, modifier = Modifier.offset(y = (-80).dp)) {
            Box(Modifier.size(216.dp).clip(CircleShape).background(PBW.Brown))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            PbFade(delayMillis = 340) {
                Text("PaperBoxd", fontFamily = PBW.Script, fontSize = 58.sp, color = PBW.Cream)
            }
            PbRise(delayMillis = 560, modifier = Modifier.padding(top = 22.dp)) {
                Text("See you in ${w.nextMonth},", fontFamily = PBW.Display, fontStyle = FontStyle.Italic,
                    fontSize = 25.sp, lineHeight = 33.sp, color = PBW.Cream, textAlign = TextAlign.Center)
            }
            PbRise(delayMillis = 680) {
                Text("${w.reader.first}.", fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                    fontSize = 25.sp, lineHeight = 33.sp, color = PBW.Amber, textAlign = TextAlign.Center)
            }
            PbFade(delayMillis = 960, modifier = Modifier.padding(top = 30.dp)) {
                Text(
                    "${w.totals.books} BOOKS · ${w.totals.pages.formatted()} PAGES · ${w.totals.estimatedHours}H",
                    fontFamily = PBW.Mono, fontSize = 10.sp, letterSpacing = 1.8.sp, color = PBW.Muted
                )
            }
        }
    }
}

// MARK: - The 9:16 object that actually gets shared

@Composable
fun WrappedRecapCard(w: Wrapped, width: androidx.compose.ui.unit.Dp = 300.dp) {
    val k = width.value / 300f
    fun s(v: Float) = (v * k).dp
    fun t(v: Float) = (v * k).sp

    Box(
        Modifier
            .width(width)
            .height(width * 16f / 9f)
            .background(PBW.Ink)
            .clipToBounds()
    ) {
        Box(
            Modifier
                .offset(x = width - s(110f), y = s(264f))
                .size(s(168f))
                .clip(CircleShape)
                .background(PBW.Terra)
        )
        Column(Modifier.fillMaxSize().padding(horizontal = s(26f), vertical = s(30f))) {
            Text("PaperBoxd", fontFamily = PBW.Script, fontSize = t(26f), color = PBW.Cream)
            Text("MONTHLY WRAPPED", fontFamily = PBW.Mono, fontSize = t(8f), letterSpacing = t(1.4f),
                color = PBW.Muted, modifier = Modifier.padding(top = s(4f)))
            Text(w.month.uppercase(), fontFamily = PBW.Display, fontWeight = FontWeight.Black,
                fontSize = t(44f), lineHeight = t(42f), color = PBW.Cream,
                modifier = Modifier.padding(top = s(22f)))
            Text(w.year, fontFamily = PBW.Mono, fontSize = t(11f), letterSpacing = t(4.6f),
                color = PBW.Amber, modifier = Modifier.padding(top = s(9f)))

            Box(
                Modifier.padding(top = s(20f), bottom = s(16f)).fillMaxWidth()
                    .height(1.dp).background(PBW.Cream.copy(alpha = 0.3f))
            )

            Row(horizontalArrangement = Arrangement.spacedBy(s(20f))) {
                RecapStat("BOOKS", "${w.totals.books}", k)
                RecapStat("PAGES", w.totals.pages.formatted(), k)
                RecapStat("HOURS", "${w.totals.estimatedHours}", k)
            }

            if (w.books.isNotEmpty()) {
                Text("TOP BOOKS", fontFamily = PBW.Mono, fontSize = t(7.5f), letterSpacing = t(1.4f),
                    color = PBW.Muted, modifier = Modifier.padding(top = s(20f), bottom = s(8f)))
                w.books.take(4).forEachIndexed { i, book ->
                    Row(
                        Modifier.padding(vertical = s(4f)),
                        horizontalArrangement = Arrangement.spacedBy(s(8f))
                    ) {
                        Text("%02d".format(i + 1), fontFamily = PBW.Mono, fontSize = t(8f), color = PBW.Terra)
                        Text(book.title, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                            fontSize = t(13.5f), color = PBW.Cream, maxLines = 1,
                            overflow = TextOverflow.Ellipsis)
                    }
                }
            }

            Row(
                Modifier.padding(top = s(16f)),
                horizontalArrangement = Arrangement.spacedBy(s(20f))
            ) {
                w.authors.firstOrNull()?.let { RecapSmallStat("TOP AUTHOR", it.name, k, Modifier.weight(1f)) }
                w.genres.firstOrNull()?.let { RecapSmallStat("TOP GENRE", it.name, k, Modifier.weight(1f)) }
            }

            Spacer(Modifier.weight(1f))

            Column(
                Modifier.fillMaxWidth().border(1.dp, PBW.Terra)
                    .padding(horizontal = s(13f), vertical = s(11f))
            ) {
                Text("READING TYPE", fontFamily = PBW.Mono, fontSize = t(7.5f),
                    letterSpacing = t(1.4f), color = PBW.Terra)
                Text(w.archetype.name, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
                    fontSize = t(19f), color = PBW.Cream, maxLines = 1, overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = s(3f)))
            }
            Row(
                Modifier.fillMaxWidth().padding(top = s(14f)),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(w.reader.handle.uppercase(), fontFamily = PBW.Mono, fontSize = t(8f),
                    letterSpacing = t(1.4f), color = PBW.Muted)
                Text("PAPERBOXD.IN", fontFamily = PBW.Mono, fontSize = t(8f),
                    letterSpacing = t(1.4f), color = PBW.Muted)
            }
        }
    }
}

@Composable
private fun RecapStat(label: String, value: String, k: Float) {
    Column {
        Text(label, fontFamily = PBW.Mono, fontSize = (7.5f * k).sp,
            letterSpacing = (1.4f * k).sp, color = PBW.Muted)
        Text(value, fontFamily = PBW.Display, fontWeight = FontWeight.Black,
            fontSize = (26f * k).sp, color = PBW.Cream, modifier = Modifier.padding(top = (2f * k).dp))
    }
}

@Composable
private fun RecapSmallStat(label: String, value: String, k: Float, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(label, fontFamily = PBW.Mono, fontSize = (7.5f * k).sp,
            letterSpacing = (1.4f * k).sp, color = PBW.Muted)
        Text(value, fontFamily = PBW.Display, fontWeight = FontWeight.SemiBold,
            fontSize = (14f * k).sp, color = PBW.Cream, maxLines = 1,
            overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = (3f * k).dp))
    }
}
