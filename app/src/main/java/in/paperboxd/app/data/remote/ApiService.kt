package `in`.paperboxd.app.data.remote

import `in`.paperboxd.app.domain.model.AddToBookshelfBody
import `in`.paperboxd.app.domain.model.SuggestedReadersResponse
import `in`.paperboxd.app.domain.model.AddFavoriteBody
import `in`.paperboxd.app.domain.model.AuthResponse
import `in`.paperboxd.app.domain.model.AvatarUploadResponse
import `in`.paperboxd.app.domain.model.ReadingActivity
import `in`.paperboxd.app.domain.model.JazyTurn
import `in`.paperboxd.app.domain.model.SmartTbr
import `in`.paperboxd.app.domain.model.SubscriptionStatus
import `in`.paperboxd.app.domain.model.VibeSearchResponse
import `in`.paperboxd.app.domain.model.Wrapped
import `in`.paperboxd.app.domain.model.FusionAcceptResponse
import `in`.paperboxd.app.domain.model.FusionInvite
import `in`.paperboxd.app.domain.model.FusionInvitePreview
import `in`.paperboxd.app.domain.model.FusionList
import `in`.paperboxd.app.domain.model.FusionStory
import `in`.paperboxd.app.domain.model.ConversationList
import `in`.paperboxd.app.domain.model.ConversationPage
import `in`.paperboxd.app.domain.model.ConversationWith
import `in`.paperboxd.app.domain.model.InboxBadge
import `in`.paperboxd.app.domain.model.SendMessageRequest
import `in`.paperboxd.app.domain.model.SendResult
import `in`.paperboxd.app.domain.model.GifList
import `in`.paperboxd.app.domain.model.MediaUpload
import `in`.paperboxd.app.domain.model.SharedList
import `in`.paperboxd.app.domain.model.Book
import `in`.paperboxd.app.domain.model.BookSocialResponse
import `in`.paperboxd.app.domain.model.CommunityResponse
import `in`.paperboxd.app.domain.model.AuthorInfo
import `in`.paperboxd.app.domain.model.BookListResponse
import `in`.paperboxd.app.domain.model.BookReviewsResponse
import `in`.paperboxd.app.domain.model.BookStatusResponse
import `in`.paperboxd.app.domain.model.BookshelfResponse
import `in`.paperboxd.app.domain.model.CheckUsernameResponse
import `in`.paperboxd.app.domain.model.DiaryCreateBody
import `in`.paperboxd.app.domain.model.DiaryEntriesResponse
import `in`.paperboxd.app.domain.model.DiaryEntry
import `in`.paperboxd.app.domain.model.ThoughtCountResponse
import `in`.paperboxd.app.domain.model.ThoughtThreadResponse
import `in`.paperboxd.app.domain.model.FavoriteBook
import `in`.paperboxd.app.domain.model.FollowResponse
import `in`.paperboxd.app.domain.model.FollowingActivitiesResponse
import `in`.paperboxd.app.domain.model.FriendReviewsResponse
import `in`.paperboxd.app.domain.model.FriendsReadingResponse
import `in`.paperboxd.app.domain.model.GoogleAuthResponse
import `in`.paperboxd.app.domain.model.HealthResponse
import `in`.paperboxd.app.domain.model.FeedResponse
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
import `in`.paperboxd.app.domain.model.CreateListRequest
import `in`.paperboxd.app.domain.model.ListWithBooksResponse
import `in`.paperboxd.app.domain.model.ReadingList
import `in`.paperboxd.app.domain.model.UserProfile
import `in`.paperboxd.app.domain.model.FollowRequestsResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

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

    /**
     * Password reset. Takes an absolute [url] because this one call goes to the
     * Next.js web tier, not the Go backend — the backend route only mints a token
     * and hands it back, the proxy is what emails the link via Resend. Callers
     * pass Config.FORGOT_PASSWORD_URL. iOS twin: Endpoints.forgotPassword.
     *
     * Always 200 with a generic `{message}` so account existence never leaks, and
     * the response carries no reset token.
     */
    @POST
    suspend fun forgotPassword(
        @Url url: String,
        @Body body: Map<String, String>
    ): ResponseBody

    // Current user
    // hasBody DELETE so the exit-survey reasons ride along, matching iOS.
    @HTTP(method = "DELETE", path = "api/v1/users/me", hasBody = true)
    suspend fun deleteMe(@Body body: Map<String, List<String>>): ResponseBody

    @POST("api/v1/users/me/onboarding")
    suspend fun saveOnboarding(@Body body: OnboardingBody): ResponseBody

    @GET("api/v1/users/suggested")
    suspend fun suggestedUsers(@Query("limit") limit: Int = 8): SuggestedReadersResponse

    @POST("api/v1/users/me/daily-open")
    suspend fun dailyOpen(): ResponseBody

    @Multipart
    @POST("api/v1/users/me/avatar/upload")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): AvatarUploadResponse

    @Multipart
    @POST("api/v1/users/me/banner/upload")
    suspend fun uploadBanner(@Part file: MultipartBody.Part): UserProfile

    @GET("api/v1/users/me/leaderboard-stats")
    suspend fun myLeaderboardStats(): LeaderboardEntry

    /**
     * Monthly Wrapped. [month] is YYYY-MM (omit for the current month) and [tz]
     * is an IANA zone so day/hour buckets match the reader's own clock.
     */
    @GET("api/v1/users/me/wrapped")
    suspend fun wrapped(
        @Query("tz") tz: String,
        @Query("month") month: String? = null
    ): Wrapped

    // Books
    @GET("api/v1/books/{id}")
    suspend fun book(@Path("id") id: String): Book

    /** Resolves a web slug (`paperboxd.in/b/{slug}`) to the full book. */
    @GET("api/v1/books/by-slug/{slug}")
    suspend fun bookBySlug(@Path("slug") slug: String): Book

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

    /** Books PaperBoxd holds by one author — the author page's catalogue. */
    @GET("api/v1/books/by-author")
    suspend fun booksByAuthor(
        @Query("author") author: String,
        @Query("page_size") pageSize: Int? = null
    ): BookListResponse

    /** Wikipedia-backed author blurb + portrait, cached 24h server-side. */
    @GET("api/v1/authors/info")
    suspend fun authorInfo(@Query("name") name: String): AuthorInfo

    @POST("api/v1/books/{id}/like")
    suspend fun likeBook(@Path("id") id: String): ResponseBody

    @DELETE("api/v1/books/{id}/like")
    suspend fun unlikeBook(@Path("id") id: String): ResponseBody

    @GET("api/v1/books/{id}/reviews")
    suspend fun bookReviews(@Path("id") id: String): BookReviewsResponse

    @GET("api/v1/books/{id}/reviews/friends")
    suspend fun bookReviewsByFriends(@Path("id") id: String): FriendReviewsResponse

    /** Paperboxd's own reader stats for a book (rating, reads, TBR). */
    @GET("api/v1/books/{id}/social")
    suspend fun bookSocial(@Path("id") id: String): BookSocialResponse

    @GET("api/v1/books/{id}/friends-reading")
    suspend fun bookFriendsReading(@Path("id") id: String): FriendsReadingResponse

    // Vibe (semantic) search — powers Ask Jazy.
    @POST("api/v1/search/vibe")
    suspend fun vibeSearch(@Body body: Map<String, @JvmSuppressWildcards Any>): VibeSearchResponse

    /**
     * Jazy concierge — search with a voice, and permission to ask one question.
     * Plus-only: 402 SUBSCRIPTION_REQUIRED without an active subscription.
     */
    @POST("api/v1/jazy")
    suspend fun jazy(@Body body: Map<String, @JvmSuppressWildcards Any>): JazyTurn

    // Plus subscription: the backend owns the entitlement, Play supplies receipts.
    @GET("api/v1/subscriptions/me")
    suspend fun subscriptionStatus(): SubscriptionStatus

    @POST("api/v1/subscriptions/google")
    suspend fun linkGoogleSubscription(@Body body: Map<String, String>): SubscriptionStatus

    // Smart TBR (Plus): the reader's TBR, ranked.
    @GET("api/v1/users/me/tbr/smart")
    suspend fun smartTbr(): SmartTbr

    @POST("api/v1/users/me/tbr/smart/{bookId}/keep")
    suspend fun keepTbr(@Path("bookId") bookId: String): Response<Unit>

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

    // ── Private profiles ──────────────────────────────────────────────────────

    @PATCH("api/v1/users/me/visibility")
    suspend fun updateVisibility(@Body body: Map<String, Boolean>): UserProfile

    @GET("api/v1/users/me/follow-requests")
    suspend fun followRequests(): FollowRequestsResponse

    @POST("api/v1/users/me/follow-requests/{username}")
    suspend fun acceptFollowRequest(@Path("username") username: String): FollowResponse

    @DELETE("api/v1/users/me/follow-requests/{username}")
    suspend fun rejectFollowRequest(@Path("username") username: String)

    @DELETE("api/v1/users/{username}/follow")
    suspend fun unfollow(@Path("username") username: String): FollowResponse

    @POST("api/v1/users/{username}/block")
    suspend fun blockUser(@Path("username") username: String): ResponseBody

    @DELETE("api/v1/users/{username}/block")
    suspend fun unblockUser(@Path("username") username: String): ResponseBody

    @POST("api/v1/reports")
    suspend fun createReport(@Body body: Map<String, String>): ResponseBody

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

    @POST("api/v1/users/{username}/favorites")
    suspend fun addFavorite(
        @Path("username") username: String,
        @Body body: AddFavoriteBody
    ): ResponseBody

    @GET("api/v1/users/{username}/favorites")
    suspend fun userFavorites(@Path("username") username: String): List<FavoriteBook>

    @GET("api/v1/users/{username}/streak")
    suspend fun userStreak(@Path("username") username: String): StreakResponse

    @GET("api/v1/users/{username}/lists")
    suspend fun userLists(@Path("username") username: String): UserListsResponse

    @POST("api/v1/users/{username}/lists")
    suspend fun createList(
        @Path("username") username: String,
        @Body body: CreateListRequest
    ): ReadingList

    @GET("api/v1/users/{username}/lists/{listId}")
    suspend fun listDetail(
        @Path("username") username: String,
        @Path("listId") listId: String
    ): ListWithBooksResponse

    @GET("api/v1/users/{username}/reading/last")
    suspend fun lastLoggedBook(@Path("username") username: String): LastLoggedBookResponse

    @GET("api/v1/users/{username}/reading/activity")
    suspend fun readingActivity(
        @Path("username") username: String,
        @Query("year") year: Int
    ): ReadingActivity

    // Thoughts. {username} is the author — the thread GET sits behind the
    // author's profile-privacy gate.
    @GET("api/v1/users/{username}/thoughts")
    suspend fun userThoughts(
        @Path("username") username: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): DiaryEntriesResponse

    @POST("api/v1/users/{username}/thoughts")
    suspend fun createThought(
        @Path("username") username: String,
        @Body body: DiaryCreateBody
    ): DiaryEntry

    @GET("api/v1/users/{username}/thoughts/{thoughtId}")
    suspend fun getThought(
        @Path("username") username: String,
        @Path("thoughtId") thoughtId: String
    ): DiaryEntry

    @GET("api/v1/users/{username}/thoughts/{thoughtId}/thread")
    suspend fun getThoughtThread(
        @Path("username") username: String,
        @Path("thoughtId") thoughtId: String
    ): ThoughtThreadResponse

    @DELETE("api/v1/users/{username}/thoughts/{thoughtId}")
    suspend fun deleteThought(
        @Path("username") username: String,
        @Path("thoughtId") thoughtId: String
    ): ResponseBody

    @POST("api/v1/users/{username}/thoughts/{thoughtId}/like")
    suspend fun likeThought(
        @Path("username") username: String,
        @Path("thoughtId") thoughtId: String
    ): ThoughtCountResponse

    @DELETE("api/v1/users/{username}/thoughts/{thoughtId}/like")
    suspend fun unlikeThought(
        @Path("username") username: String,
        @Path("thoughtId") thoughtId: String
    ): ThoughtCountResponse

    @POST("api/v1/users/{username}/thoughts/{thoughtId}/repost")
    suspend fun repostThought(
        @Path("username") username: String,
        @Path("thoughtId") thoughtId: String
    ): ThoughtCountResponse

    @DELETE("api/v1/users/{username}/thoughts/{thoughtId}/repost")
    suspend fun unrepostThought(
        @Path("username") username: String,
        @Path("thoughtId") thoughtId: String
    ): ThoughtCountResponse

    // Activities + events
    @GET("api/v1/activities/following")
    suspend fun followingActivities(
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null
    ): FollowingActivitiesResponse

    @POST("api/v1/events")
    suspend fun trackEvent(@Body body: TrackEventBody): Response<ResponseBody>

    /** Verdict on a recommendation card — see service.RecordFeedback. */
    @POST("api/v1/recommendations/feedback")
    suspend fun recommendationFeedback(@Body body: Map<String, @JvmSuppressWildcards Any>): Response<ResponseBody>

    /** Public community snapshot: trending books + public activity. No auth. */
    @GET("api/v1/community")
    suspend fun community(): CommunityResponse

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

    /** The modular home. [tz] is the reader's IANA zone, for the greeting. */
    @GET("api/v1/recommendations/feed")
    suspend fun feed(@Query("tz") tz: String?): FeedResponse

    @GET("api/v1/recommendations/similar/{bookId}")
    suspend fun similarBooks(@Path("bookId") bookId: String): SimilarBooksResponse

    // Scan & Know
    @POST("api/v1/scan/analyze")
    suspend fun scanAnalyze(@Body body: ScanAnalyzeBody): ScanAnalyzeResponse

    // Fusion — one-time invite links and the two-reader story.
    @GET("api/v1/fusions")
    suspend fun fusions(): FusionList

    @POST("api/v1/fusions/invites")
    suspend fun createFusionInvite(): FusionInvite

    @DELETE("api/v1/fusions/invites/{token}")
    suspend fun cancelFusionInvite(@Path("token") token: String): Response<Unit>

    @GET("api/v1/fusions/invites/{token}")
    suspend fun fusionInvite(@Path("token") token: String): FusionInvitePreview

    @POST("api/v1/fusions/invites/{token}/accept")
    suspend fun acceptFusionInvite(@Path("token") token: String): FusionAcceptResponse

    @GET("api/v1/fusions/{id}")
    suspend fun fusion(@Path("id") id: String): FusionStory

    // Messages (DMs). Polling: a chat asks for ?after=<last id>.
    @GET("api/v1/conversations")
    suspend fun conversations(
        @Query("box") box: String,
        @Query("before") before: String? = null
    ): ConversationList

    @GET("api/v1/conversations/badge")
    suspend fun inboxBadge(): InboxBadge

    @GET("api/v1/conversations/with/{username}")
    suspend fun conversationWith(@Path("username") username: String): ConversationWith

    @GET("api/v1/conversations/{id}/messages")
    suspend fun conversationMessages(
        @Path("id") id: String,
        @Query("before") before: Long? = null,
        @Query("after") after: Long? = null
    ): ConversationPage

    @GET("api/v1/conversations/{id}/shared")
    suspend fun conversationShared(
        @Path("id") id: String,
        @Query("kind") kind: String? = null
    ): SharedList

    @POST("api/v1/messages")
    suspend fun sendMessage(@Body body: SendMessageRequest): SendResult

    /** kind = photo | video. Attach the result as AttachmentRef(kind, refId). */
    @Multipart
    @POST("api/v1/messages/media")
    suspend fun uploadMessageMedia(@Part("kind") kind: RequestBody, @Part file: MultipartBody.Part): MediaUpload

    /** GIPHY via the backend; trending when q is empty. type = gifs | stickers. */
    @GET("api/v1/messages/gifs")
    suspend fun gifs(@Query("type") type: String, @Query("q") q: String): GifList

    @DELETE("api/v1/messages/{id}")
    suspend fun unsendMessage(@Path("id") id: Long): Response<Unit>

    @POST("api/v1/conversations/{id}/read")
    suspend fun markConversationRead(@Path("id") id: String): Response<Unit>

    @POST("api/v1/conversations/{id}/accept")
    suspend fun acceptConversation(@Path("id") id: String): Response<Unit>

    @PATCH("api/v1/conversations/{id}")
    suspend fun updateConversation(@Path("id") id: String, @Body body: Map<String, Boolean>): Response<Unit>

    @DELETE("api/v1/conversations/{id}")
    suspend fun clearConversation(@Path("id") id: String): Response<Unit>
}
