package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/**
 * GET /api/v1/users/{username}/reading/activity?year= — heatmap payload.
 * Mirrors iOS Models/ReadingActivity.swift. Dates are yyyy-MM-dd (UTC).
 */
data class ReadingActivity(
    val year: Int,
    val start: String,
    val end: String,
    val days: List<Day> = emptyList(),
    @SerializedName("total_pages") val totalPages: Int = 0,
    @SerializedName("days_read") val daysRead: Int = 0,
    @SerializedName("best_day") val bestDay: Int = 0,
    @SerializedName("longest_streak") val longestStreak: Int = 0,
    @SerializedName("current_streak") val currentStreak: Int = 0
) {
    data class Day(
        val date: String,
        val pages: Int = 0,
        val books: Int = 0
    )
}
