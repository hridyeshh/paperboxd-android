package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/**
 * Monthly Wrapped, from GET /api/v1/users/me/wrapped. Mirrors the iOS
 * `Wrapped` model field for field.
 *
 * Anything the backend cannot know for certain is either nullable (`topRated`,
 * `abandoned`) or named as an estimate (`estimatedHours` comes from pages, not
 * from a timer).
 */
data class Wrapped(
    /** False when nothing was logged that month — show the empty state. */
    @SerializedName("has_data") val hasData: Boolean = false,
    val month: String = "",
    @SerializedName("month_short") val monthShort: String = "",
    val year: String = "",
    @SerializedName("next_month") val nextMonth: String = "",
    val reader: Reader = Reader(),
    val totals: Totals = Totals(),
    val books: List<Book> = emptyList(),
    val authors: List<Author> = emptyList(),
    val genres: List<Genre> = emptyList(),
    val rhythm: Rhythm = Rhythm(),
    val streak: Streak = Streak(),
    @SerializedName("top_rated") val topRated: TopRated? = null,
    val abandoned: Abandoned? = null,
    val rank: Rank = Rank(),
    val archetype: Archetype = Archetype(),
    val dare: Dare = Dare()
) {
    data class Reader(
        val name: String = "",
        val handle: String = "",
        val first: String = ""
    )

    data class Totals(
        val books: Int = 0,
        val pages: Int = 0,
        @SerializedName("estimated_hours") val estimatedHours: Int = 0,
        @SerializedName("estimated_minutes") val estimatedMinutes: Int = 0,
        val sessions: Int = 0,
        @SerializedName("active_days") val activeDays: Int = 0,
        @SerializedName("biggest_day_pages") val biggestDayPages: Int = 0,
        @SerializedName("biggest_day") val biggestDay: String = ""
    )

    data class Book(
        val title: String = "",
        val author: String = "",
        val cover: String = "",
        val pages: Int = 0,
        val days: Int = 0,
        val rating: Double = 0.0
    )

    data class Author(
        val name: String = "",
        val books: Int = 0,
        val pages: Int = 0,
        val note: String? = null
    )

    data class Genre(
        val name: String = "",
        val pct: Int = 0
    )

    data class Rhythm(
        val label: String = "",
        val peak: String = "",
        @SerializedName("pct_after_midnight") val pctAfterMidnight: Int = 0,
        val line: String = "",
        /** 24 slots, 0–100, normalised against the busiest hour. */
        val hours: List<Int> = emptyList()
    )

    data class Streak(
        val days: Int = 0,
        val start: String = "",
        val end: String = "",
        val broke: String = "",
        @SerializedName("longest_ever") val longestEver: Int = 0,
        /** One entry per day of the month, in pages. */
        val calendar: List<Int> = emptyList(),
        /** Indices into [calendar]; -1 when there was no streak. */
        @SerializedName("streak_start") val streakStart: Int = -1,
        @SerializedName("streak_end") val streakEnd: Int = -1,
        @SerializedName("broke_index") val brokeIndex: Int = -1
    ) {
        fun isInStreak(index: Int) = streakStart >= 0 && index in streakStart..streakEnd
    }

    data class TopRated(
        val title: String = "",
        val author: String = "",
        val cover: String = "",
        val rating: Double = 0.0,
        val date: String = "",
        val review: String = ""
    )

    data class Abandoned(
        val title: String = "",
        val author: String = "",
        val page: Int = 0,
        val of: Int = 0,
        val started: String = "",
        @SerializedName("last_opened") val lastOpened: String = "",
        val roast: String = ""
    ) {
        val pctRead: Int get() = if (of > 0) Math.round(page.toFloat() / of * 100) else 0
    }

    data class Rank(
        val percentile: Int = 0,
        val label: String = "",
        val readers: Int = 0,
        val beat: Int = 0,
        val line: String = ""
    )

    data class Archetype(
        val name: String = "",
        val kicker: String = "",
        val definition: String = "",
        val traits: List<String> = emptyList(),
        @SerializedName("stat_label") val statLabel: String = "",
        @SerializedName("stat_value") val statValue: String = "",
        val pairs: String = ""
    )

    data class Dare(
        val title: String = "",
        val body: String = "",
        val target: String = "",
        val tag: String = ""
    )
}
