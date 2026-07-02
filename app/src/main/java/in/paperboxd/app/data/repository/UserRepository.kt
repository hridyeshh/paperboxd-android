package `in`.paperboxd.app.data.repository

import `in`.paperboxd.app.data.remote.ApiService
import `in`.paperboxd.app.data.remote.safeApiCall
import `in`.paperboxd.app.domain.model.AuthorSummary
import `in`.paperboxd.app.domain.model.BookshelfResponse
import `in`.paperboxd.app.domain.model.FavoriteBook
import `in`.paperboxd.app.domain.model.FollowResponse
import `in`.paperboxd.app.domain.model.LastLoggedBookResponse
import `in`.paperboxd.app.domain.model.LeaderboardEntry
import `in`.paperboxd.app.domain.model.LeaderboardResponse
import `in`.paperboxd.app.domain.model.LikesResponse
import `in`.paperboxd.app.domain.model.StreakResponse
import `in`.paperboxd.app.domain.model.TbrItem
import `in`.paperboxd.app.domain.model.UserListResponse
import `in`.paperboxd.app.domain.model.UserListsResponse
import `in`.paperboxd.app.domain.model.UserProfile
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val api: ApiService
) {
    suspend fun profile(username: String): Result<UserProfile> = safeApiCall { api.profile(username) }

    suspend fun updateProfile(username: String, fields: Map<String, Any?>): Result<UserProfile> =
        safeApiCall { api.updateProfile(username, fields) }

    suspend fun follow(username: String): Result<FollowResponse> = safeApiCall { api.follow(username) }
    suspend fun unfollow(username: String): Result<FollowResponse> = safeApiCall { api.unfollow(username) }

    suspend fun followers(username: String, page: Int, pageSize: Int = 30): Result<UserListResponse> =
        safeApiCall { api.followers(username, page, pageSize) }

    suspend fun following(username: String, page: Int, pageSize: Int = 30): Result<UserListResponse> =
        safeApiCall { api.following(username, page, pageSize) }

    suspend fun searchUsers(query: String, page: Int? = null, pageSize: Int? = 20): Result<UserListResponse> =
        safeApiCall { api.searchUsers(query, page, pageSize) }

    suspend fun bookshelf(username: String, status: String?, page: Int, pageSize: Int = 20): Result<BookshelfResponse> =
        safeApiCall { api.userBookshelf(username, status, page, pageSize) }

    suspend fun likes(username: String, page: Int? = null, pageSize: Int? = null): Result<LikesResponse> =
        safeApiCall { api.userLikes(username, page, pageSize) }

    suspend fun tbr(username: String): Result<List<TbrItem>> = safeApiCall { api.userTbr(username) }

    suspend fun authors(username: String): Result<List<AuthorSummary>> =
        safeApiCall { api.userAuthors(username) }

    suspend fun favorites(username: String): Result<List<FavoriteBook>> =
        safeApiCall { api.userFavorites(username).sortedBy { it.displayOrder } }

    suspend fun streak(username: String): Result<StreakResponse> = safeApiCall { api.userStreak(username) }

    suspend fun lists(username: String): Result<UserListsResponse> = safeApiCall { api.userLists(username) }

    suspend fun lastLoggedBook(username: String): Result<LastLoggedBookResponse> =
        safeApiCall { api.lastLoggedBook(username) }

    suspend fun uploadBanner(bytes: ByteArray, fileName: String = "banner.jpg"): Result<UserProfile> =
        safeApiCall {
            val part = MultipartBody.Part.createFormData(
                "file", fileName, bytes.toRequestBody("image/jpeg".toMediaType())
            )
            api.uploadBanner(part)
        }

    // Leaderboard
    suspend fun globalLeaderboard(): Result<LeaderboardResponse> = safeApiCall { api.globalLeaderboard() }
    suspend fun friendsLeaderboard(): Result<LeaderboardResponse> = safeApiCall { api.friendsLeaderboard() }
    suspend fun leaderboardByDimension(dimension: String): Result<LeaderboardResponse> =
        safeApiCall { api.leaderboardByDimension(dimension) }
    suspend fun myLeaderboardStats(): Result<LeaderboardEntry> = safeApiCall { api.myLeaderboardStats() }
}
