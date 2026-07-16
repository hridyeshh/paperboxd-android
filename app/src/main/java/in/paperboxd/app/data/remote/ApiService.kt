package `in`.paperboxd.app.data.remote

import `in`.paperboxd.app.domain.model.AddToBookshelfBody
import `in`.paperboxd.app.domain.model.AuthResponse
import `in`.paperboxd.app.domain.model.AuthorSummary
import `in`.paperboxd.app.domain.model.AvatarUploadResponse
import `in`.paperboxd.app.domain.model.ReadingActivity
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.BookListResponse
import `in`.paperboxd.app.domain.model.BookReviewsResponse
import `in`.paperboxd.app.domain.model.BookStatusResponse
import `in`.paperboxd.app.domain.model.BookshelfResponse
import `in`.paperboxd.app.domain.model.CheckUsernameResponse
import `in`.paperboxd.app.domain.model.DiaryCreateBody
import `in`.paperboxd.app.domain.model.DiaryEntriesResponse
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.domain.model.FavoriteBook
import `in`.paperboxd.app.domain.model.FollowResponse
import `in`.paperboxd.app.domain.model.FollowingActivitiesResponse
import `in`.paperboxd.app.domain.model.FriendReviewsResponse
import `in`.paperboxd.app.domain.model.FriendsReadingResponse
import `in`.paperboxd.app.domain.model.GoogleAuthResponse
import `in`.paperboxd.app.domain.model.HealthResponse
import `in`.paperboxd.app.domain.model.HomeRecommendationsResponse
import `in`.paperboxd.app.domain.model.LastLoggedBookResponse
import `in`.paperboxd.app.domain.model.LeaderboardEntry
import `in`.paperboxd.app.domain.model.LeaderboardResponse
import `in`.paperboxd.app.domain.model.LikesResponse
import `in`.paperboxd.app.domain.model.MobileUserResponse
import `in`.paperboxd.app.domain.model.OnboardingBody
import `in`.paperboxd.app.domain.model.OtpSendResponse
import `in`.paperboxd.app.domain.model.ProgressBody
import `in`.paperboxd.app.domain.model.ProgressUpdateResponse
import `in`.paperboxd.app.domain.model.ReadingProgress
import `in`.paperboxd.app.domain.model.RefreshResponse
import `in`.paperboxd.app.domain.model.ReviewBody
import `in`.paperboxd.app.domain.model.ReviewUpdateResponse
import `in`.paperboxd.app.domain.model.ScanAnalyzeBody
import `in`.paperboxd.app.domain.model.ScanAnalyzeResponse
import `in`.paperboxd.app.domain.model.SimilarBooksResponse
import `in`.paperboxd.app.domain.model.StreakResponse
import `in`.paperboxd.app.domain.model.TbrItem
import `in`.paperboxd.app.domain.model.TrackEventBody
import `in`.paperboxd.app.domain.model.UserListResponse
import `in`.paperboxd.app.domain.model.UserListsResponse
import `in`.paperboxd.app.domain.model.UserProfile
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Every endpoint the app touches, mirroring iOS Endpoints.swift against
 * paperboxd-backend MOBILE_API.md.
 */
interface ApiService {

    // Connectivity
    @GET("api/health")
    suspend fun health(): HealthResponse

    // Mobile auth
    @POST("api/mobile/auth/login")
    suspend fun login(@Body body: Map<String, String>): AuthResponse

    @POST("api/mobile/auth/register")
    suspend fun register(@Body body: Map<String, String>): AuthResponse

    @POST("api/mobile/auth/otp/send")
    suspend fun sendOtp(@Body body: Map<String, String>): OtpSendResponse

    @POST("api/mobile/auth/otp/verify")
    suspend fun verifyOtp(@Body body: Map<String, String>): AuthResponse

    @POST("api/mobile/auth/google")
    suspend fun googleAuth(@Body body: Map<String, String>): GoogleAuthResponse

    @POST("api/mobile/auth/refresh")
    suspend fun refresh(): RefreshResponse

    @PATCH("api/mobile/users/me")
    suspend fun updateMobileMe(@Body body: Map<String, String>): MobileUserResponse

    // Auth (web v1 routes that accept the mobile bearer)
    @GET("api/v1/auth/check-username")
    suspend fun checkUsername(@Query("username") username: String): CheckUsernameResponse

    @POST("api/v1/auth/forgot-password")
    suspend fun forgotPassword(@Body body: Map<String, String>): ResponseBody

    // Current user
    @DELETE("api/v1/users/me")
    suspend fun deleteMe(): ResponseBody

    @POST("api/v1/users/me/onboarding")
    suspend fun saveOnboarding(@Body body: OnboardingBody): ResponseBody

    @Multipart
    @POST("api/v1/users/me/avatar/upload")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): AvatarUploadResponse

    @Multipart
    @POST("api/v1/users/me/banner/upload")
    suspend fun uploadBanner(@Part file: MultipartBody.Part): UserProfile

    @GET("api/v1/users/me/leaderboard-stats")
    suspend fun myLeaderboardStats(): LeaderboardEntry

    // Books
    @GET("api/v1/books/{id}")
    suspend fun book(@Path("id") id: String): Book

    @GET("api/v1/books/search")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): BookListResponse

    @GET("api/v1/books/latest")
    suspend fun latestBooks(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): BookListResponse

    @GET("api/v1/books/random")
    suspend fun randomBooks(@Query("page_size") pageSize: Int? = null): BookListResponse

    @POST("api/v1/books/{id}/like")
    suspend fun likeBook(@Path("id") id: String): ResponseBody

    @DELETE("api/v1/books/{id}/like")
    suspend fun unlikeBook(@Path("id") id: String): ResponseBody

    @GET("api/v1/books/{id}/reviews")
    suspend fun bookReviews(@Path("id") id: String): BookReviewsResponse

    @GET("api/v1/books/{id}/reviews/friends")
    suspend fun bookReviewsByFriends(@Path("id") id: String): FriendReviewsResponse

    @GET("api/v1/books/{id}/friends-reading")
    suspend fun bookFriendsReading(@Path("id") id: String): FriendsReadingResponse

    // Vibe (semantic) search
    @POST("api/v1/search/vibe")
    suspend fun vibeSearch(@Body body: Map<String, @JvmSuppressWildcards Any>): BookListResponse

    // Bookshelf
    @POST("api/v1/users/{username}/bookshelf")
    suspend fun addToBookshelf(
        @Path("username") username: String,
        @Body body: AddToBookshelfBody
    ): ResponseBody

    @DELETE("api/v1/users/{username}/bookshelf/{bookId}")
    suspend fun removeFromBookshelf(
        @Path("username") username: String,
        @Path("bookId") bookId: String
    ): ResponseBody

    @PATCH("api/v1/users/{username}/bookshelf/{bookId}")
    suspend fun updateBookshelfRating(
        @Path("username") username: String,
        @Path("bookId") bookId: String,
        @Body body: ReviewBody
    ): ReviewUpdateResponse

    @GET("api/v1/users/{username}/bookshelf/{bookId}/status")
    suspend fun bookStatus(
        @Path("username") username: String,
        @Path("bookId") bookId: String
    ): BookStatusResponse

    @GET("api/v1/users/{username}/bookshelf/{bookId}/progress")
    suspend fun readingProgress(
        @Path("username") username: String,
        @Path("bookId") bookId: String
    ): ReadingProgress

    @PUT("api/v1/users/{username}/bookshelf/{bookId}/progress")
    suspend fun updateReadingProgress(
        @Path("username") username: String,
        @Path("bookId") bookId: String,
        @Body body: ProgressBody
    ): ProgressUpdateResponse

    @GET("api/v1/users/{username}/bookshelf")
    suspend fun userBookshelf(
        @Path("username") username: String,
        @Query("status") status: String? = null,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): BookshelfResponse

    // Profile + social
    @GET("api/v1/users/{username}")
    suspend fun profile(@Path("username") username: String): UserProfile

    @PUT("api/v1/users/{username}")
    suspend fun updateProfile(
        @Path("username") username: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): UserProfile

    @POST("api/v1/users/{username}/follow")
    suspend fun follow(@Path("username") username: String): FollowResponse

    @DELETE("api/v1/users/{username}/follow")
    suspend fun unfollow(@Path("username") username: String): FollowResponse

    @GET("api/v1/users/{username}/followers")
    suspend fun followers(
        @Path("username") username: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): UserListResponse

    @GET("api/v1/users/{username}/following")
    suspend fun following(
        @Path("username") username: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): UserListResponse

    @GET("api/v1/users/search")
    suspend fun searchUsers(
        @Query("query") query: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): UserListResponse

    @GET("api/v1/users/{username}/likes")
    suspend fun userLikes(
        @Path("username") username: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): LikesResponse

    @GET("api/v1/users/{username}/tbr")
    suspend fun userTbr(@Path("username") username: String): List<TbrItem>

    @GET("api/v1/users/{username}/authors")
    suspend fun userAuthors(@Path("username") username: String): List<AuthorSummary>

    @GET("api/v1/users/{username}/favorites")
    suspend fun userFavorites(@Path("username") username: String): List<FavoriteBook>

    @GET("api/v1/users/{username}/streak")
    suspend fun userStreak(@Path("username") username: String): StreakResponse

    @GET("api/v1/users/{username}/lists")
    suspend fun userLists(@Path("username") username: String): UserListsResponse

    @GET("api/v1/users/{username}/reading/last")
    suspend fun lastLoggedBook(@Path("username") username: String): LastLoggedBookResponse

    @GET("api/v1/users/{username}/reading/activity")
    suspend fun readingActivity(
        @Path("username") username: String,
        @Query("year") year: Int
    ): ReadingActivity

    // Diary
    @GET("api/v1/users/{username}/diary")
    suspend fun userDiary(
        @Path("username") username: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): DiaryEntriesResponse

    @POST("api/v1/users/{username}/diary")
    suspend fun createDiaryEntry(
        @Path("username") username: String,
        @Body body: DiaryCreateBody
    ): DiaryEntry

    @GET("api/v1/users/{username}/diary/{entryId}")
    suspend fun getDiaryEntry(
        @Path("username") username: String,
        @Path("entryId") entryId: String
    ): DiaryEntry

    @DELETE("api/v1/users/{username}/diary/{entryId}")
    suspend fun deleteDiaryEntry(
        @Path("username") username: String,
        @Path("entryId") entryId: String
    ): ResponseBody

    @POST("api/v1/users/{username}/diary/{entryId}/like")
    suspend fun likeDiaryEntry(
        @Path("username") username: String,
        @Path("entryId") entryId: String
    ): ResponseBody

    @DELETE("api/v1/users/{username}/diary/{entryId}/like")
    suspend fun unlikeDiaryEntry(
        @Path("username") username: String,
        @Path("entryId") entryId: String
    ): ResponseBody

    // Activities + events
    @GET("api/v1/activities/following")
    suspend fun followingActivities(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): FollowingActivitiesResponse

    @POST("api/v1/events")
    suspend fun trackEvent(@Body body: TrackEventBody): Response<ResponseBody>

    // Leaderboard
    @GET("api/v1/leaderboard/global")
    suspend fun globalLeaderboard(): LeaderboardResponse

    @GET("api/v1/leaderboard/friends")
    suspend fun friendsLeaderboard(): LeaderboardResponse

    @GET("api/v1/leaderboard/dimension/{dimension}")
    suspend fun leaderboardByDimension(@Path("dimension") dimension: String): LeaderboardResponse

    // Recommendations
    @GET("api/v1/recommendations/home")
    suspend fun homeRecommendations(): HomeRecommendationsResponse

    @GET("api/v1/recommendations/similar/{bookId}")
    suspend fun similarBooks(@Path("bookId") bookId: String): SimilarBooksResponse

    // Scan & Know
    @POST("api/v1/scan/analyze")
    suspend fun scanAnalyze(@Body body: ScanAnalyzeBody): ScanAnalyzeResponse
}
