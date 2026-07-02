package `in`.paperboxd.app.domain.model

import com.google.gson.annotations.SerializedName

/** Public profile. Maps types.UserResponse. */
data class UserProfile(
    val id: String,
    val username: String,
    val name: String = "",
    @SerializedName("avatar_url") val avatarUrl: String? = null,
    @SerializedName("banner_url") val bannerUrl: String? = null,
    val bio: String? = null,
    val pronouns: List<String> = emptyList(),
    val birthday: String? = null,
    val gender: String? = null,
    val links: List<String> = emptyList(),
    @SerializedName("is_public") val isPublic: Boolean = true,
    @SerializedName("books_read_count") val booksReadCount: Int = 0,
    @SerializedName("total_pages_read") val totalPagesRead: Int = 0,
    @SerializedName("favorites_count") val favoritesCount: Int = 0,
    @SerializedName("lists_count") val listsCount: Int = 0,
    @SerializedName("diary_entries_count") val diaryEntriesCount: Int = 0,
    @SerializedName("followers_count") val followersCount: Int = 0,
    @SerializedName("following_count") val followingCount: Int = 0,
    @SerializedName("favorite_genres") val favoriteGenres: List<String> = emptyList(),
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("is_following") val isFollowing: Boolean? = null
) {
    val displayName: String get() = name.ifEmpty { username }
}

/** One of the "Four books I love" favourites. Maps types.FavoriteResponse. */
data class FavoriteBook(
    val id: String,
    @SerializedName("book_id") val bookId: String,
    @SerializedName("display_order") val displayOrder: Int = 0,
    val note: String? = null,
    val book: Book,
    @SerializedName("created_at") val createdAt: String? = null
) {
    val coverUrl: String? get() = book.coverUrl
    val title: String get() = book.title
}

data class StreakResponse(val streak: Int)

/** Note: backend emits camelCase here, unlike most user routes. */
data class FollowResponse(
    val message: String,
    val isFollowing: Boolean,
    val followersCount: Int,
    val followingCount: Int
)

data class UserListResponse(
    val users: List<UserProfile> = emptyList(),
    @SerializedName("total_count") val totalCount: Long = 0,
    val page: Int = 1,
    @SerializedName("page_size") val pageSize: Int = 0,
    val pagination: PaginationMeta? = null
)
