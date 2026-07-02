package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** One leaderboard row. Maps the backend statToMap shape. */
data class LeaderboardEntry(
    @SerializedName("user_id") val userId: String,
    val username: String,
    @SerializedName("books_read") val booksRead: Int = 0,
    @SerializedName("pages_read") val pagesRead: Int = 0,
    @SerializedName("diary_entries") val diaryEntries: Int = 0,
    @SerializedName("genres_explored") val genresExplored: Int = 0,
    @SerializedName("total_xp") val totalXp: Int = 0,
    val level: Int = 1,
    @SerializedName("current_streak") val currentStreak: Int = 0,
    @SerializedName("books_rank") val booksRank: Int? = null,
    @SerializedName("pages_rank") val pagesRank: Int? = null,
    @SerializedName("diary_rank") val diaryRank: Int? = null,
    @SerializedName("genres_rank") val genresRank: Int? = null,
    @SerializedName("xp_rank") val xpRank: Int? = null,
    @SerializedName("streak_rank") val streakRank: Int? = null,
    @SerializedName("level_name") val levelName: String = "",
    @SerializedName("level_badge") val levelBadge: String = ""
)

data class LeaderboardResponse(
    val leaderboard: List<LeaderboardEntry> = emptyList(),
    val count: Int = 0
)
