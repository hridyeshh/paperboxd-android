package `in`.paperboxd.app.domain.model

/**
 * A reader's XP standing, as shown by the profile avatar ring and level sheet.
 *
 * [level] and [name] come from the server (`GET /api/v1/users/me/leaderboard-stats`)
 * and are never computed here — the backend owns the naming. The thresholds
 * below mirror `CalculateLevel` in `internal/service/xp_service.go` and exist
 * only to draw the ring and say how far the next level is; if they ever drift,
 * the server's `level` still wins and only the arc is slightly off.
 *
 * Twin of iOS `ReaderProgress`.
 */
data class ReaderProgress(
    val level: Int,
    val name: String,
    val totalXp: Int
) {
    /** XP at which [level] began. */
    val levelStart: Int get() = xpAtStart(level)

    /** XP at which the next level begins. */
    val levelEnd: Int get() = xpAtStart(level + 1)

    val xpIntoLevel: Int get() = (totalXp - levelStart).coerceAtLeast(0)

    val xpToNext: Int get() = (levelEnd - totalXp).coerceAtLeast(0)

    /**
     * 0..1 through the current level. Floored at 6% — a true zero-length arc
     * reads as a rendering failure rather than a fresh start.
     */
    val ringFill: Float
        get() {
            val span = levelEnd - levelStart
            if (span <= 0) return 1f
            return (xpIntoLevel.toFloat() / span).coerceIn(0.06f, 1f)
        }

    val nextLevelName: String get() = nameFor(level + 1)

    companion object {
        fun from(entry: LeaderboardEntry) =
            ReaderProgress(level = entry.level, name = entry.levelName, totalXp = entry.totalXp)

        fun xpAtStart(level: Int): Int = when {
            level >= 31 -> 12000 + (level - 31) * 500
            level >= 26 -> 8000 + (level - 26) * 800
            level >= 21 -> 5000 + (level - 21) * 600
            level >= 16 -> 3000 + (level - 16) * 400
            level >= 11 -> 1500 + (level - 11) * 300
            level >= 6 -> 500 + (level - 6) * 200
            else -> ((level - 1) * 100).coerceAtLeast(0)
        }

        /**
         * Only for levels the user has not reached — the server supplies the
         * name of the level they are actually on.
         */
        fun nameFor(level: Int): String = when {
            level >= 31 -> "Living Library"
            level >= 26 -> "Legendary Curator"
            level >= 21 -> "Grand Archivist"
            level >= 16 -> "Literary Scholar"
            level >= 11 -> "Bibliophile"
            level >= 6 -> "Avid Reader"
            else -> "Beginner Reader"
        }

        /** The seven named tiers and the level each begins at. */
        val tiers: List<Pair<Int, String>> = listOf(
            1 to "Beginner Reader",
            6 to "Avid Reader",
            11 to "Bibliophile",
            16 to "Literary Scholar",
            21 to "Grand Archivist",
            26 to "Legendary Curator",
            31 to "Living Library"
        )
    }
}

/**
 * What each action is worth, straight from `xp_service.go`. Shown in the level
 * sheet so "why did my XP stop" is answerable without leaving the app.
 */
object XpAction {
    val all: List<Pair<String, Int>> = listOf(
        "Finish a book" to 25,
        "7-day streak" to 50,
        "Thought on a book" to 20,
        "30-day streak" to 200,
        "Write a thought" to 15,
        "100-day streak" to 1000,
        "Your first list" to 15,
        "Another list" to 5,
        "Log progress" to 5,
        "Gain a follower" to 5,
        "Open the app" to 3,
        "Add to TBR" to 2
    )

    /** `MaxDailyXP` — streak and referral bonuses are exempt. */
    const val DAILY_CAP = 150
}
