package `in`.paperboxd.app.ui.screens.fusion

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.domain.model.FusionBook
import `in`.paperboxd.app.domain.model.FusionStory
import `in`.paperboxd.app.ui.screens.wrapped.LocalWrappedStill
import `in`.paperboxd.app.ui.screens.wrapped.PbFade
import `in`.paperboxd.app.ui.screens.wrapped.PbRise
import `in`.paperboxd.app.ui.screens.wrapped.PbSlide
import `in`.paperboxd.app.ui.screens.wrapped.SlideFrom

// The Fusion story. Twin of iOS FusionChapters.swift and the design prototype's
// fusion2/stories-a.jsx / stories-b.jsx, laid out against the 402dp grid the
// player scales. Pages the two shelves cannot fill honestly are skipped.

class FusionActions(
    val toast: (String) -> Unit = {},
    val share: () -> Unit = {},
    val explore: () -> Unit = {},
    val next: () -> Unit = {},
    val addToTbr: (List<FusionBook>) -> Unit = {}
)

class FusionChapter(
    val id: String,
    val isDark: Boolean,
    val content: @Composable (s: FusionStory, n: Int, of: Int, act: FusionActions) -> Unit
)

fun fusionChapters(s: FusionStory): List<FusionChapter> = buildList {
    add(FusionChapter("score", false) { st, n, of, _ -> ScorePage(st, n, of) })
    add(FusionChapter("numbers", false) { st, n, of, _ -> NumbersPage(st, n, of) })
    if (s.sharedLoved.isNotEmpty()) add(FusionChapter("loved", false) { st, n, of, _ -> LovedPage(st, n, of) })
    if (s.agree.isNotEmpty()) add(FusionChapter("agree", false) { st, n, of, _ -> AgreePage(st, n, of) })
    if (s.splits.isNotEmpty() || s.disagreement != null) add(FusionChapter("split", false) { st, n, of, _ -> SplitPage(st, n, of) })
    if (s.forYou.isNotEmpty()) add(FusionChapter("their-shelf", false) { st, n, of, a -> TheirShelfPage(st, n, of, a) })
    if (s.forThem.isNotEmpty()) add(FusionChapter("your-shelf", false) { st, n, of, _ -> YourShelfPage(st, n, of) })
    if (s.pick != null) add(FusionChapter("pick", true) { st, n, of, a -> PickPage(st, n, of, a) })
    if (s.wildcard != null) add(FusionChapter("wildcard", true) { st, n, of, a -> WildcardPage(st, n, of, a) })
    add(FusionChapter("verdict", false) { st, n, of, a -> VerdictPage(st, n, of, a) })
}

@Composable
private fun RunHead(n: Int, of: Int, dark: Boolean = false) {
    val c = if (dark) FZ.MuteOnDark else FZ.Grey
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            FzLabel("PaperBoxd Fusion", color = c)
            Spacer(Modifier.weight(1f))
            FzNum("${fzIndex(n)} / ${fzIndex(of)}", 10f, c, bold = false)
        }
        FzRule(200, dark)
    }
}

@Composable
private fun ColumnScope.Grow() = Spacer(Modifier.weight(1f))

// MARK: - 01 Score

@Composable
private fun ScorePage(s: FusionStory, n: Int, of: Int) = FzScreen {
    RunHead(n, of)
    Grow()
    PbRise(300) {
        Row(verticalAlignment = Alignment.Bottom) {
            FzCountText(s.score, 380, 1100, 176f, FZ.Ink)
            Text("%", color = FZ.Accent, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = 56.sp, modifier = Modifier.padding(bottom = 22.dp))
        }
    }
    PbFade(760, modifier = Modifier.padding(top = 18.dp)) { FzLabel("Taste Fusion", 12f) }
    Box(Modifier.padding(top = 34.dp)) { FzOverprint(s.me.first.uppercase(), s.them.first.uppercase(), 44f, 900) }
    Grow()
    FzRule(1200)
    PbFade(1280, modifier = Modifier.padding(top = 16.dp)) { FzBody(s.scoreLine, 17f, alpha = 1f) }
    Row(Modifier.padding(top = 20.dp)) {
        listOf("${s.sharedCount}" to "Shared", "${s.agreement}%" to "Agree", "${s.sharedAuthors.size}" to "Authors")
            .forEachIndexed { i, (v, l) ->
                PbFade(1380 + i * 90, modifier = Modifier.weight(1f)) {
                    Column(verticalArrangement = Arrangement.spacedBy(5.dp)) { FzNum(v, 22f); FzLabel(l, 9f) }
                }
            }
    }
}

/** The counting number, in the Fusion face. */
@Composable
private fun FzCountText(value: Int, delayMillis: Int, duration: Int, size: Float, color: Color) {
    val still = LocalWrappedStill.current
    var shown by remember(value) { mutableIntStateOf(if (still) value else 0) }
    LaunchedEffect(value, still) {
        if (still) { shown = value; return@LaunchedEffect }
        delay(delayMillis.toLong())
        val steps = maxOf(1, duration / 40)
        for (step in 1..steps) {
            val p = step.toFloat() / steps
            shown = (value * (1f - Math.pow((1f - p).toDouble(), 3.0).toFloat())).toInt()
            delay(40)
        }
        shown = value
    }
    Text(
        "$shown", color = color, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = size.sp,
        lineHeight = (size * 0.86f).sp, letterSpacing = FZ.tracking(size), style = TextStyle(fontFeatureSettings = "tnum")
    )
}

// MARK: - 02 Numbers

@Composable
private fun NumbersPage(s: FusionStory, n: Int, of: Int) = FzScreen {
    RunHead(n, of)
    PbFade(260, modifier = Modifier.padding(top = 26.dp)) { FzLabel("What you share") }
    PbRise(360, modifier = Modifier.padding(top = 12.dp)) { FzCountText(s.lovedCount, 440, 1200, 128f, FZ.Accent) }
    Box(Modifier.padding(top = 8.dp)) {
        FzStack(listOf(if (s.lovedCount == 1) "book you" else "books you", "both loved"), 30f, delayMillis = 520)
    }
    Grow()
    val rest = listOf(
        s.sharedAuthors.size to "Authors in common",
        s.sharedGenres.size to "Genres you both obsess over",
        s.waitingCount to "Books waiting for both of you",
    ).filter { it.first > 0 }
    rest.forEachIndexed { i, (count, label) ->
        FzRule(760 + i * 120)
        PbSlide(820 + i * 120, distance = 18.dp) {
            Row(Modifier.padding(vertical = 15.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(Modifier.width(74.dp)) { FzCountText(count, 880 + i * 120, 900, 40f, FZ.Ink) }
                FzBody(label, 14f, modifier = Modifier.padding(bottom = 6.dp))
            }
        }
    }
    if (rest.isNotEmpty()) FzRule(1140)
    if (s.sharedCount > 0) {
        PbFade(1220, modifier = Modifier.padding(top = 18.dp)) {
            FzBody("You have both read ${s.sharedCount} of the same books, and agreed about ${s.agreement}% of them.", 13.5f)
        }
    }
}

// MARK: - 03 Both loved

@Composable
private fun LovedPage(s: FusionStory, n: Int, of: Int) = FzScreen {
    RunHead(n, of)
    PbRise(280, modifier = Modifier.padding(top = 24.dp)) {
        Text(
            buildAnnotatedString {
                withStyle(SpanStyle(color = FZ.Accent)) { append("${s.lovedCount}") }
                append(if (s.lovedCount == 1) " book you" else " books you")
            },
            color = FZ.Ink, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = 38.sp, letterSpacing = FZ.tracking(38f)
        )
    }
    PbRise(360) { FzHeading("both loved", 38f) }
    Column(Modifier.padding(top = 26.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        s.sharedLoved.chunked(3).forEachIndexed { row, books ->
            Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                books.forEachIndexed { col, b ->
                    val i = row * 3 + col
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        FzCover(b, 100.dp, CoverOwner.Both, delayMillis = 480 + i * 85)
                        PbFade(760 + i * 85) {
                            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                FzNum("${b.you ?: 0}", 12f, FZ.Accent)
                                FzNum("/", 12f, FZ.Grey, bold = false)
                                FzNum("${b.them ?: 0}", 12f)
                            }
                        }
                    }
                }
            }
        }
    }
    Grow()
    FzRule(1200)
    val top = s.sharedLoved.first()
    val note = if (top.you == 5 && top.them == 5) "Both of you gave ${top.title} five stars."
    else "You gave ${top.title} ${top.you}. ${s.them.first} gave it ${top.them}."
    PbFade(1260, modifier = Modifier.padding(top = 14.dp)) {
        Row(verticalAlignment = Alignment.Bottom) {
            FzBody(note, 13.5f, modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                FzLabel("You", 9f, FZ.Accent)
                FzLabel(s.them.first, 9f)
            }
        }
    }
}

// MARK: - 04 Agree

@Composable
private fun AgreePage(s: FusionStory, n: Int, of: Int) = FzScreen {
    val axes = s.agree.firstOrNull()?.kind == "axis"
    val sizes = listOf(34f, 28f, 23f, 19f)
    RunHead(n, of)
    PbFade(260, modifier = Modifier.padding(top = 26.dp)) { FzLabel("Where you agree") }
    Box(Modifier.padding(top = 12.dp)) {
        FzStack(if (axes) listOf("You want the same", "shape of book") else listOf("You read the same", "kinds of book"), 34f, delayMillis = 340)
    }
    Column(Modifier.padding(top = 30.dp)) {
        s.agree.forEachIndexed { i, a ->
            FzRule(520 + i * 150)
            PbSlide(580 + i * 150, distance = 20.dp) {
                Row(Modifier.padding(vertical = 16.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FzHeading(a.phrase, sizes[i.coerceAtMost(sizes.lastIndex)], modifier = Modifier.weight(1f))
                    Row(Modifier.padding(bottom = 3.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FzNum("${a.you}", 13f, FZ.Accent); FzNum("/", 13f, FZ.Grey, bold = false); FzNum("${a.them}", 13f)
                    }
                }
            }
        }
        FzRule(1120)
    }
    Grow()
    PbFade(1200) {
        FzLabel(if (axes) "${s.agree.size} of nine taste axes · both confident" else "Share of each shelf, you / ${s.them.first}", 9f)
    }
}

// MARK: - 05 Split

@Composable
private fun SplitPage(s: FusionStory, n: Int, of: Int) = FzScreen {
    RunHead(n, of)
    Box(Modifier.padding(top = 24.dp)) { FzStack(listOf("But you're not", "the same reader"), 36f, delayMillis = 280) }
    if (s.splits.isNotEmpty()) {
        Row(Modifier.padding(top = 26.dp)) {
            PbFade(500, modifier = Modifier.weight(1f)) { FzLabel(s.me.first, 9.5f, FZ.Accent) }
            PbFade(540, modifier = Modifier.weight(1f).padding(start = 16.dp)) { FzLabel(s.them.first, 9.5f) }
        }
        Column(Modifier.padding(top = 10.dp)) {
            s.splits.forEachIndexed { i, sp ->
                FzRule(600 + i * 150)
                Row(Modifier.height(IntrinsicSize.Min).padding(vertical = 14.dp)) {
                    PbSlide(660 + i * 150, distance = 16.dp, modifier = Modifier.weight(1f).padding(end = 14.dp)) {
                        FzHeading(sp.you, 19f, FZ.Accent)
                    }
                    Box(Modifier.width(1.dp).fillMaxHeight().background(FZ.Line))
                    PbSlide(660 + i * 150, from = SlideFrom.End, distance = 16.dp, modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                        FzHeading(sp.them, 19f)
                    }
                }
                PbFade(760 + i * 150, modifier = Modifier.padding(bottom = 14.dp)) { FzLabel(sp.label, 9f) }
            }
            FzRule(1080, color = FZ.Ink, thickness = 1.5.dp)
        }
    }
    Grow()
    val d = s.disagreement
    if (d?.you != null && d.them != null) {
        PbFade(1160) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FzLabel("Your biggest disagreement", 9f)
                FzHeading(d.title, 24f)
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    FzRate(s.me.first, d.you, FZ.Accent, 15f)
                    FzRate(s.them.first, d.them, size = 15f)
                }
            }
        }
    }
}

// MARK: - 06 Their shelf

@Composable
private fun TheirShelfPage(s: FusionStory, n: Int, of: Int, act: FusionActions) = FzScreen {
    RunHead(n, of)
    PbRise(280, modifier = Modifier.padding(top = 24.dp)) { FzHeading("Books ${s.them.first} loves", 34f) }
    PbRise(350) { FzHeading("that you haven't read", 34f, FZ.Grey) }
    Column(Modifier.padding(top = 26.dp)) {
        s.forYou.forEachIndexed { i, b ->
            FzRule(480 + i * 170)
            PbSlide(540 + i * 170, distance = 20.dp) {
                Row(Modifier.padding(vertical = 16.dp), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Box(Modifier.width(34.dp)) { FzNum(fzIndex(i + 1), 30f, FZ.Grey) }
                    Column(Modifier.weight(1f)) {
                        FzHeading(b.title, 22f)
                        Row(Modifier.padding(top = 8.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FzLabel(b.author, 9f)
                            b.them?.let { FzRate(s.them.first, it) }
                        }
                        b.reason?.let { FzBody(it, 13f, modifier = Modifier.padding(top = 9.dp)) }
                        Row(Modifier.padding(top = 11.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            FzPill("Add to TBR", compact = true, fullWidth = false) { act.addToTbr(listOf(b)) }
                            b.match?.let { FzLabel(it, 9f, FZ.Accent) }
                        }
                    }
                }
            }
        }
        FzRule(1020)
    }
    Grow()
    PbFade(1100) { FzLabel("From ${s.them.first}'s shelf", 9f) }
}

// MARK: - 07 Your shelf

@Composable
private fun YourShelfPage(s: FusionStory, n: Int, of: Int) = FzScreen {
    RunHead(n, of)
    PbRise(280, modifier = Modifier.padding(top = 24.dp)) { FzHeading("Books you should", 34f) }
    PbRise(350) { FzHeading("give ${s.them.first}", 34f, FZ.Accent) }
    Row(Modifier.padding(top = 28.dp), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
        s.forThem.forEachIndexed { i, b ->
            FzCover(b, 100.dp, CoverOwner.You, delayMillis = 480 + i * 110, rotation = if (i == 1) 0f else if (i == 0) -1.5f else 1.5f)
        }
    }
    Column(Modifier.padding(top = 26.dp)) {
        s.forThem.forEachIndexed { i, b ->
            FzRule(760 + i * 140)
            PbSlide(820 + i * 140, distance = 16.dp) {
                Row(Modifier.padding(vertical = 13.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.width(20.dp).padding(top = 3.dp)) { FzNum(fzIndex(i + 1), 11f, FZ.Accent) }
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        FzHeading(b.title, 17f)
                        b.reason?.let { FzBody(it, 12.5f) }
                    }
                    b.you?.let { FzRate("You", it, FZ.Accent) }
                }
            }
        }
        FzRule(1220)
    }
    Grow()
    PbFade(1300) { FzLabel("${s.them.first} sees these in their Fusion too", 9f) }
}

// MARK: - 08 The pick

@Composable
private fun PickPage(s: FusionStory, n: Int, of: Int, act: FusionActions) {
    val p = s.pick ?: return
    FzScreen(dark = true) {
        RunHead(n, of, dark = true)
        PbFade(260, modifier = Modifier.padding(top = 24.dp)) { FzLabel("Your Fusion found these", color = FZ.Accent) }
        Box(Modifier.padding(top = 12.dp)) { FzStack(listOf("Neither of you", "has read this"), 32f, FZ.Bg, delayMillis = 340) }
        Row(Modifier.padding(top = 28.dp), verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
            FzCover(p.book, 116.dp, CoverOwner.Both, dark = true, delayMillis = 480)
            Column(Modifier.weight(1f).padding(bottom = 4.dp)) {
                PbRise(620) { FzHeading(p.book.title, 34f, FZ.Bg) }
                PbFade(720, modifier = Modifier.padding(top = 10.dp)) { FzLabel(p.book.author, 9f, FZ.Bg.copy(alpha = 0.6f)) }
                PbFade(820, modifier = Modifier.padding(top = 14.dp)) {
                    Box(Modifier.border(1.dp, FZ.Accent).padding(horizontal = 10.dp, vertical = 7.dp)) { FzLabel(p.label, 9.5f, FZ.Accent) }
                }
            }
        }
        Column(Modifier.padding(top = 28.dp)) {
            p.reasons.forEachIndexed { i, r ->
                PbSlide(940 + i * 130, distance = 16.dp) {
                    Column {
                        Box(Modifier.fillMaxWidth().height(1.dp).background(FZ.LineOnDark))
                        Row(Modifier.padding(vertical = 11.dp), horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                            Box(Modifier.width(18.dp).padding(top = 3.dp)) {
                                FzNum(fzIndex(i + 1), 10f, if (r.who == "you") FZ.Accent else FZ.Bg.copy(alpha = 0.5f))
                            }
                            FzBody(r.text, 13f, FZ.Bg, 0.86f)
                        }
                    }
                }
            }
        }
        Grow()
        PbFade(1500) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FzPill(if (s.also.isEmpty()) "Add to TBR" else "Add all ${1 + s.also.size}", solid = true, light = true, modifier = Modifier.weight(1f)) {
                    act.addToTbr(listOf(p.book) + s.also)
                }
                if (s.also.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        s.also.forEach { FzLabel(it.title, 9f, FZ.MuteOnDark) }
                    }
                }
            }
        }
    }
}

// MARK: - 09 Wildcard

@Composable
private fun WildcardPage(s: FusionStory, n: Int, of: Int, act: FusionActions) {
    val w = s.wildcard ?: return
    FzScreen(dark = true) {
        RunHead(n, of, dark = true)
        PbFade(260, modifier = Modifier.padding(top = 26.dp)) { FzLabel("One wildcard", color = FZ.Accent) }
        Grow()
        FzStack(titleLines(w.book.title), 62f, FZ.Bg, delayMillis = 380, stepMillis = 90)
        PbFade(640, modifier = Modifier.padding(top = 16.dp)) {
            Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                Column(Modifier.weight(1f)) {
                    FzLabel(w.book.author, 9f, FZ.Bg.copy(alpha = 0.6f))
                    w.reasons.forEachIndexed { i, r ->
                        Column(Modifier.padding(top = if (i == 0) 20.dp else 11.dp), verticalArrangement = Arrangement.spacedBy(11.dp)) {
                            Box(Modifier.fillMaxWidth().height(1.dp).background(FZ.LineOnDark))
                            FzBody(r.text, 14f, FZ.Bg, 0.84f)
                        }
                    }
                }
                FzCover(w.book, 74.dp, CoverOwner.Both, dark = true, delayMillis = 900, rotation = -3f)
            }
        }
        Grow()
        PbFade(1200) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FzPill("Take the risk", light = true, modifier = Modifier.weight(1f)) { act.addToTbr(listOf(w.book)) }
                FzPill("Not this time", light = true, modifier = Modifier.weight(1f)) { act.next() }
            }
        }
    }
}

/** Titles set as the image: short uppercase lines. */
private fun titleLines(title: String): List<String> {
    val lines = mutableListOf<String>()
    var current = ""
    for (word in title.uppercase().split(" ")) {
        current = if (current.isNotEmpty() && current.length + word.length + 1 > 10) {
            lines += current; word
        } else if (current.isEmpty()) word else "$current $word"
    }
    if (current.isNotEmpty()) lines += current
    return lines.take(4)
}

// MARK: - 10 Verdict

@Composable
private fun VerdictPage(s: FusionStory, n: Int, of: Int, act: FusionActions) = FzScreen {
    RunHead(n, of)
    Grow()
    val words = s.verdict.split(" ")
    PbRise(300) {
        Text(
            buildAnnotatedString {
                append(words.dropLast(1).joinToString(" "))
                if (words.size > 1) append(" ")
                withStyle(SpanStyle(color = FZ.Accent)) { append(words.lastOrNull().orEmpty()) }
            },
            color = FZ.Ink, fontFamily = FZ.Sans, fontWeight = FontWeight.Bold, fontSize = 44.sp,
            lineHeight = 44.sp, letterSpacing = FZ.tracking(44f)
        )
    }
    FzRule(760, modifier = Modifier.padding(top = 26.dp))
    PbFade(840, modifier = Modifier.padding(top = 18.dp)) { FzBody(s.verdictSub, 15f) }
    Grow()
    PbFade(980) {
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            FzPill("Explore your Fusion", solid = true, modifier = Modifier.weight(1f)) { act.explore() }
            FzPill("Share", fullWidth = false) { act.share() }
        }
    }
}
