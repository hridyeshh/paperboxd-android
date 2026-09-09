package `in`.paperboxd.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.paperboxd.app.domain.model.ReaderProgress
import `in`.paperboxd.app.domain.model.XpAction
import `in`.paperboxd.app.ui.components.AvatarImage
import `in`.paperboxd.app.ui.components.HL
import `in`.paperboxd.app.ui.components.brutalPlate

// #B85C38 → #A8893F, lifted from the web profile's xpRingGrad so a reader sees
// the same ring on either surface.
private val ArcStart = Color(0xFFB85C38)
private val ArcEnd = Color(0xFFA8893F)
private val Line = Color(0xFFE6DFD0)

private fun Int.grouped(): String = "%,d".format(this)

/**
 * The profile avatar wrapped in an XP arc, with the level name plated beneath.
 *
 * Everything but the arc is repainted for the light brutalist profile (ink
 * border, hard-shadowed plate) rather than web's rounded pill on a dark ground.
 *
 * No XP figure appears out here — the total, the remainder and the bar all live
 * in [LevelSheet]. Out here the arc is the whole story.
 *
 * [progress] is null while the stats request is in flight, and on profiles the
 * server won't give stats for. Then: plain avatar, no ring, no plate, no tap.
 */
@Composable
fun XPRingAvatar(
    avatarUrl: String?,
    displayName: String,
    progress: ReaderProgress?,
    onTap: () -> Unit = {}
) {
    val block = 112.dp
    val ringWidth = 5.dp

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(block)
            .then(
                if (progress != null) {
                    Modifier
                        .clickable(onClick = onTap)
                        .semantics {
                            contentDescription = "Level ${progress.level}, ${progress.name}, " +
                                "${progress.totalXp.grouped()} XP, " +
                                "${progress.xpToNext.grouped()} to level ${progress.level + 1}. " +
                                "Shows level details"
                        }
                } else Modifier
            )
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(block)) {
            if (progress != null) {
                val fill = progress.ringFill
                Box(
                    Modifier.size(block).drawBehind {
                        val w = ringWidth.toPx()
                        val inset = w / 2
                        val d = size.minDimension - w
                        drawArc(
                            color = HL.Paper2,
                            startAngle = 0f, sweepAngle = 360f, useCenter = false,
                            topLeft = Offset(inset, inset), size = Size(d, d),
                            style = Stroke(width = w)
                        )
                        drawArc(
                            brush = Brush.linearGradient(listOf(ArcStart, ArcEnd)),
                            startAngle = -90f, sweepAngle = 360f * fill, useCenter = false,
                            topLeft = Offset(inset, inset), size = Size(d, d),
                            style = Stroke(width = w, cap = StrokeCap.Round)
                        )
                    }
                )
            }
            Box(Modifier.border(2.dp, HL.Ink, CircleShape)) {
                AvatarImage(url = avatarUrl, name = displayName, size = 88.dp)
            }
        }

        if (progress != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .offset(y = (-14).dp)
                    .brutalPlate(fill = HL.Card, borderWidth = 2.dp, offset = 3.dp)
                    .padding(start = 5.dp, end = 8.dp, top = 4.dp, bottom = 4.dp)
            ) {
                Box(
                    Modifier.size(18.dp).background(HL.Ink),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "${progress.level}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = HL.Cream
                    )
                }
                Text(
                    progress.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HL.Ink
                )
            }
        }
    }
}

/**
 * Tapping the avatar opens this. Every XP figure in the app lives here.
 *
 * Web lists all 35 levels; this shows the seven named tiers with the current one
 * plated, because 35 rows is a scroll nobody finishes on a phone.
 *
 * [isOwnProfile] false keeps the tiers and the reader's standing but drops the
 * "to go" figures and the earn table — neither is actionable for a viewer.
 */
@Composable
fun LevelSheet(progress: ReaderProgress, isOwnProfile: Boolean = true) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier
                    .brutalPlate(fill = HL.Ink, borderWidth = 2.dp, offset = 3.dp, shadow = ArcStart)
                    .size(46.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${progress.level}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = HL.Cream
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Text(
                    "LEVEL ${progress.level} · THE READING ORDER",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 2.sp,
                    color = HL.Muted
                )
                Text(
                    progress.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 24.sp,
                    color = HL.Ink
                )
            }
        }

        if (isOwnProfile) {
            Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        "${progress.totalXp.grouped()} XP",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = HL.Ink
                    )
                    Text(
                        "${progress.xpToNext.grouped()} TO LEVEL ${progress.level + 1}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        color = HL.Muted
                    )
                }
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(HL.Paper2)
                        .border(2.dp, HL.Ink)
                ) {
                    Box(
                        Modifier
                            .fillMaxWidth(progress.ringFill)
                            .height(12.dp)
                            .background(Brush.horizontalGradient(listOf(ArcStart, ArcEnd)))
                    )
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(progress.levelStart.grouped(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = HL.Muted)
                    Text(progress.levelEnd.grouped(), fontFamily = FontFamily.Monospace, fontSize = 9.sp, color = HL.Muted)
                }
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Text(
                "THE READING ORDER",
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                color = HL.Muted
            )
            val nextTier = ReaderProgress.tiers.firstOrNull { it.first > progress.level }?.first
            // 2 here, not 4: each row already carries a 3dp shadow gutter below
            // it, so the visible gap is 5.
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                ReaderProgress.tiers.forEachIndexed { i, (level, name) ->
                    // "Current" is the tier the level falls inside, not one whose
                    // number matches — level 13 is a Bibliophile though the tier
                    // starts at 11.
                    val ceiling = ReaderProgress.tiers.getOrNull(i + 1)?.first ?: Int.MAX_VALUE
                    val isCurrent = progress.level in level until ceiling
                    val isUnlocked = progress.level >= ceiling
                    val isNext = level == nextTier
                    val need = ReaderProgress.xpAtStart(level)

                    TierRow(
                        level = level, name = name, need = need,
                        isCurrent = isCurrent, isUnlocked = isUnlocked, isNext = isNext,
                        toGo = if (isNext && isOwnProfile) need - progress.totalXp else null
                    )
                }
            }
        }

        if (isOwnProfile) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(Modifier.fillMaxWidth().height(1.dp).background(Line))
                Text(
                    "HOW TO EARN XP",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 2.sp,
                    color = HL.Muted
                )
                XpAction.all.chunked(2).forEach { pair ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        pair.forEach { (label, xp) ->
                            Row(
                                Modifier.weight(1f),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, fontSize = 12.sp, color = HL.Muted, maxLines = 1)
                                Text(
                                    "+$xp",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HL.Ink
                                )
                            }
                        }
                        if (pair.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
                Text(
                    "${XpAction.DAILY_CAP} XP A DAY MAX — STREAK AND REFERRAL BONUSES ARE EXEMPT",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    lineHeight = 14.sp,
                    color = HL.Muted,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun TierRow(
    level: Int,
    name: String,
    need: Int,
    isCurrent: Boolean,
    isUnlocked: Boolean,
    isNext: Boolean,
    toGo: Int?
) {
    val body: @Composable () -> Unit = {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 36.dp)
                .padding(horizontal = 8.dp)
        ) {
            Box(
                Modifier
                    .size(22.dp)
                    .background(
                        if (isCurrent) HL.Cream
                        else HL.Ink.copy(alpha = if (isUnlocked) 0.12f else 0.06f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "$level",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HL.Ink.copy(alpha = if (isCurrent || isUnlocked) 1f else 0.7f)
                )
            }
            Text(
                name,
                fontSize = 13.sp,
                fontWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal,
                color = when {
                    isCurrent -> HL.Cream
                    isUnlocked -> HL.Muted
                    else -> HL.Ink
                },
                modifier = Modifier.weight(1f)
            )
            Text(
                "${need.grouped()} XP",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = if (isCurrent) HL.Cream.copy(alpha = 0.7f) else HL.Muted
            )
            when {
                isCurrent -> Text(
                    "NOW",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    letterSpacing = 1.2.sp,
                    color = HL.Cream
                )
                isUnlocked -> Text("done", fontSize = 11.sp, color = HL.Sage)
                toGo != null -> Text(
                    "${toGo.grouped()} to go",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    color = HL.Accent
                )
            }
        }
    }

    // Every row reserves the same shadow gutter, plated or not — brutalPlate
    // supplies it on the current row, the others pad to match — so their edges
    // line up down the column.
    val gutter = Modifier.fillMaxWidth().padding(end = 3.dp, bottom = 3.dp)
    when {
        isCurrent -> Box(
            Modifier
                .fillMaxWidth()
                .brutalPlate(fill = HL.Ink, borderWidth = 2.dp, offset = 3.dp, shadow = ArcStart)
        ) { body() }
        isNext -> Box(gutter.border(1.dp, Line)) { body() }
        isUnlocked -> Box(gutter) { body() }
        else -> Box(gutter.alpha(0.5f)) { body() }
    }
}
