package in.paperboxd.app.data.remote;

/**
 * Every endpoint the app touches, mirroring iOS Endpoints.swift against
 * paperboxd-backend MOBILE_API.md.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00d8\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bf\u0018\u00002\u00020\u0001J\"\u0010\u0002\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0018\u0010\t\u001a\u00020\n2\b\b\u0001\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\r\u001a\u00020\u000e2\b\b\u0001\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\u000f\u001a\u00020\u00102\b\b\u0001\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010\u0011\u001a\u00020\u00122\b\b\u0001\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\"\u0010\u0013\u001a\u00020\u00142\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0015\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u0018\u0010\u0017\u001a\u00020\u00182\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\"\u0010\u0019\u001a\u00020\u001a2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020\u001bH\u00a7@\u00a2\u0006\u0002\u0010\u001cJ\"\u0010\u001d\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u001e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u001f\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010 J\u0018\u0010!\u001a\u00020\"2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ0\u0010#\u001a\u00020$2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010(J0\u0010)\u001a\u00020$2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010(J&\u0010*\u001a\u00020+2\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010,J$\u0010-\u001a\u00020\u00032\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050.H\u00a7@\u00a2\u0006\u0002\u0010/J\u000e\u00100\u001a\u000201H\u00a7@\u00a2\u0006\u0002\u0010 J\"\u00102\u001a\u00020\u001a2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u001e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u000e\u00103\u001a\u000201H\u00a7@\u00a2\u0006\u0002\u0010 J$\u00104\u001a\u0002052\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050.H\u00a7@\u00a2\u0006\u0002\u0010/J\u000e\u00106\u001a\u000207H\u00a7@\u00a2\u0006\u0002\u0010 J\u000e\u00108\u001a\u000209H\u00a7@\u00a2\u0006\u0002\u0010 J\u0018\u0010:\u001a\u00020;2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ&\u0010<\u001a\u00020=2\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010,J\u0018\u0010>\u001a\u0002012\b\b\u0001\u0010?\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010@\u001a\u00020\u00032\b\b\u0001\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\"\u0010A\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u001e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J$\u0010B\u001a\u00020C2\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050.H\u00a7@\u00a2\u0006\u0002\u0010/J\u000e\u0010D\u001a\u00020EH\u00a7@\u00a2\u0006\u0002\u0010 J\u0018\u0010F\u001a\u00020G2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001a\u0010H\u001a\u00020=2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010IJ\"\u0010J\u001a\u00020K2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0015\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010L\u001a\u00020MH\u00a7@\u00a2\u0006\u0002\u0010 J$\u0010N\u001a\u00020C2\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050.H\u00a7@\u00a2\u0006\u0002\u0010/J\"\u0010O\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0015\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u0018\u0010P\u001a\u00020\u00032\b\b\u0001\u0010\u0006\u001a\u00020QH\u00a7@\u00a2\u0006\u0002\u0010RJ\u0018\u0010S\u001a\u00020T2\b\b\u0001\u0010\u0006\u001a\u00020UH\u00a7@\u00a2\u0006\u0002\u0010VJ0\u0010W\u001a\u00020=2\b\b\u0001\u0010X\u001a\u00020\u00052\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010(J0\u0010Y\u001a\u00020$2\b\b\u0001\u0010X\u001a\u00020\u00052\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010(J$\u0010Z\u001a\u00020[2\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050.H\u00a7@\u00a2\u0006\u0002\u0010/J\u0018\u0010\\\u001a\u00020]2\b\b\u0001\u0010\u0015\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001e\u0010^\u001a\b\u0012\u0004\u0012\u00020\u00030_2\b\b\u0001\u0010\u0006\u001a\u00020`H\u00a7@\u00a2\u0006\u0002\u0010aJ\u0018\u0010b\u001a\u00020\"2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u0018\u0010c\u001a\u00020\u00032\b\b\u0001\u0010\u000b\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\"\u0010d\u001a\u00020\u00032\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u001e\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0016J,\u0010e\u001a\u00020f2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0015\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020gH\u00a7@\u00a2\u0006\u0002\u0010hJ$\u0010i\u001a\u00020j2\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050.H\u00a7@\u00a2\u0006\u0002\u0010/J5\u0010k\u001a\u00020G2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\u001b\b\u0001\u0010\u0006\u001a\u0015\u0012\u0004\u0012\u00020\u0005\u0012\u000b\u0012\t\u0018\u00010\u0001\u00a2\u0006\u0002\bl0.H\u00a7@\u00a2\u0006\u0002\u0010mJ,\u0010n\u001a\u00020o2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\b\b\u0001\u0010\u0015\u001a\u00020\u00052\b\b\u0001\u0010\u0006\u001a\u00020pH\u00a7@\u00a2\u0006\u0002\u0010qJ\u0018\u0010r\u001a\u00020s2\b\b\u0001\u0010t\u001a\u00020uH\u00a7@\u00a2\u0006\u0002\u0010vJ\u0018\u0010w\u001a\u00020G2\b\b\u0001\u0010t\u001a\u00020uH\u00a7@\u00a2\u0006\u0002\u0010vJ\u001e\u0010x\u001a\b\u0012\u0004\u0012\u00020z0y2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ<\u0010{\u001a\u00020|2\b\b\u0001\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010}\u001a\u0004\u0018\u00010\u00052\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010~J1\u0010\u007f\u001a\u00030\u0080\u00012\b\b\u0001\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010(J \u0010\u0081\u0001\u001a\t\u0012\u0005\u0012\u00030\u0082\u00010y2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ2\u0010\u0083\u0001\u001a\u00030\u0084\u00012\b\b\u0001\u0010\u0004\u001a\u00020\u00052\n\b\u0003\u0010%\u001a\u0004\u0018\u00010&2\n\b\u0003\u0010\'\u001a\u0004\u0018\u00010&H\u00a7@\u00a2\u0006\u0002\u0010(J\u001a\u0010\u0085\u0001\u001a\u00030\u0086\u00012\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ\u001a\u0010\u0087\u0001\u001a\u00030\u0088\u00012\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ \u0010\u0089\u0001\u001a\t\u0012\u0005\u0012\u00030\u008a\u00010y2\b\b\u0001\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\fJ%\u0010\u008b\u0001\u001a\u00020C2\u0014\b\u0001\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00050.H\u00a7@\u00a2\u0006\u0002\u0010/J*\u0010\u008c\u0001\u001a\u00020=2\u0019\b\u0001\u0010\u0006\u001a\u0013\u0012\u0004\u0012\u00020\u0005\u0012\t\u0012\u00070\u0001\u00a2\u0006\u0002\bl0.H\u00a7@\u00a2\u0006\u0002\u0010/\u00a8\u0006\u008d\u0001"}, d2 = {"Lin/paperboxd/app/data/remote/ApiService;", "", "addToBookshelf", "Lokhttp3/ResponseBody;", "username", "", "body", "Lin/paperboxd/app/domain/model/AddToBookshelfBody;", "(Ljava/lang/String;Lin/paperboxd/app/domain/model/AddToBookshelfBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "book", "Lin/paperboxd/app/domain/model/Book;", "id", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "bookFriendsReading", "Lin/paperboxd/app/domain/model/FriendsReadingResponse;", "bookReviews", "Lin/paperboxd/app/domain/model/BookReviewsResponse;", "bookReviewsByFriends", "Lin/paperboxd/app/domain/model/FriendReviewsResponse;", "bookStatus", "Lin/paperboxd/app/domain/model/BookStatusResponse;", "bookId", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "checkUsername", "Lin/paperboxd/app/domain/model/CheckUsernameResponse;", "createDiaryEntry", "Lin/paperboxd/app/domain/model/DiaryEntry;", "Lin/paperboxd/app/domain/model/DiaryCreateBody;", "(Ljava/lang/String;Lin/paperboxd/app/domain/model/DiaryCreateBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteDiaryEntry", "entryId", "deleteMe", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "follow", "Lin/paperboxd/app/domain/model/FollowResponse;", "followers", "Lin/paperboxd/app/domain/model/UserListResponse;", "page", "", "pageSize", "(Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "following", "followingActivities", "Lin/paperboxd/app/domain/model/FollowingActivitiesResponse;", "(Ljava/lang/Integer;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "forgotPassword", "", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "friendsLeaderboard", "Lin/paperboxd/app/domain/model/LeaderboardResponse;", "getDiaryEntry", "globalLeaderboard", "googleAuth", "Lin/paperboxd/app/domain/model/GoogleAuthResponse;", "health", "Lin/paperboxd/app/domain/model/HealthResponse;", "homeRecommendations", "Lin/paperboxd/app/domain/model/HomeRecommendationsResponse;", "lastLoggedBook", "Lin/paperboxd/app/domain/model/LastLoggedBookResponse;", "latestBooks", "Lin/paperboxd/app/domain/model/BookListResponse;", "leaderboardByDimension", "dimension", "likeBook", "likeDiaryEntry", "login", "Lin/paperboxd/app/domain/model/AuthResponse;", "myLeaderboardStats", "Lin/paperboxd/app/domain/model/LeaderboardEntry;", "profile", "Lin/paperboxd/app/domain/model/UserProfile;", "randomBooks", "(Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "readingProgress", "Lin/paperboxd/app/domain/model/ReadingProgress;", "refresh", "Lin/paperboxd/app/domain/model/RefreshResponse;", "register", "removeFromBookshelf", "saveOnboarding", "Lin/paperboxd/app/domain/model/OnboardingBody;", "(Lin/paperboxd/app/domain/model/OnboardingBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "scanAnalyze", "Lin/paperboxd/app/domain/model/ScanAnalyzeResponse;", "Lin/paperboxd/app/domain/model/ScanAnalyzeBody;", "(Lin/paperboxd/app/domain/model/ScanAnalyzeBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "searchBooks", "query", "searchUsers", "sendOtp", "Lin/paperboxd/app/domain/model/OtpSendResponse;", "similarBooks", "Lin/paperboxd/app/domain/model/SimilarBooksResponse;", "trackEvent", "Lretrofit2/Response;", "Lin/paperboxd/app/domain/model/TrackEventBody;", "(Lin/paperboxd/app/domain/model/TrackEventBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "unfollow", "unlikeBook", "unlikeDiaryEntry", "updateBookshelfRating", "Lin/paperboxd/app/domain/model/ReviewUpdateResponse;", "Lin/paperboxd/app/domain/model/ReviewBody;", "(Ljava/lang/String;Ljava/lang/String;Lin/paperboxd/app/domain/model/ReviewBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateMobileMe", "Lin/paperboxd/app/domain/model/MobileUserResponse;", "updateProfile", "Lkotlin/jvm/JvmSuppressWildcards;", "(Ljava/lang/String;Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateReadingProgress", "Lin/paperboxd/app/domain/model/ProgressUpdateResponse;", "Lin/paperboxd/app/domain/model/ProgressBody;", "(Ljava/lang/String;Ljava/lang/String;Lin/paperboxd/app/domain/model/ProgressBody;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadAvatar", "Lin/paperboxd/app/domain/model/AvatarUploadResponse;", "file", "Lokhttp3/MultipartBody$Part;", "(Lokhttp3/MultipartBody$Part;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadBanner", "userAuthors", "", "Lin/paperboxd/app/domain/model/AuthorSummary;", "userBookshelf", "Lin/paperboxd/app/domain/model/BookshelfResponse;", "status", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/lang/Integer;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "userDiary", "Lin/paperboxd/app/domain/model/DiaryEntriesResponse;", "userFavorites", "Lin/paperboxd/app/domain/model/FavoriteBook;", "userLikes", "Lin/paperboxd/app/domain/model/LikesResponse;", "userLists", "Lin/paperboxd/app/domain/model/UserListsResponse;", "userStreak", "Lin/paperboxd/app/domain/model/StreakResponse;", "userTbr", "Lin/paperboxd/app/domain/model/TbrItem;", "verifyOtp", "vibeSearch", "app_debug"})
public abstract interface ApiService {
    
    @retrofit2.http.GET(value = "api/health")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object health(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.HealthResponse> $completion);
    
    @retrofit2.http.POST(value = "api/mobile/auth/login")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object login(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.AuthResponse> $completion);
    
    @retrofit2.http.POST(value = "api/mobile/auth/register")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object register(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.AuthResponse> $completion);
    
    @retrofit2.http.POST(value = "api/mobile/auth/otp/send")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object sendOtp(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.OtpSendResponse> $completion);
    
    @retrofit2.http.POST(value = "api/mobile/auth/otp/verify")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object verifyOtp(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.AuthResponse> $completion);
    
    @retrofit2.http.POST(value = "api/mobile/auth/google")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object googleAuth(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.GoogleAuthResponse> $completion);
    
    @retrofit2.http.POST(value = "api/mobile/auth/refresh")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object refresh(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.RefreshResponse> $completion);
    
    @retrofit2.http.PATCH(value = "api/mobile/users/me")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMobileMe(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.MobileUserResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/auth/check-username")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object checkUsername(@retrofit2.http.Query(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.CheckUsernameResponse> $completion);
    
    @retrofit2.http.POST(value = "api/v1/auth/forgot-password")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object forgotPassword(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.String> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.DELETE(value = "api/v1/users/me")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMe(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.POST(value = "api/v1/users/me/onboarding")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveOnboarding(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.OnboardingBody body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "api/v1/users/me/avatar/upload")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadAvatar(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part file, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.AvatarUploadResponse> $completion);
    
    @retrofit2.http.Multipart()
    @retrofit2.http.POST(value = "api/v1/users/me/banner/upload")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object uploadBanner(@retrofit2.http.Part()
    @org.jetbrains.annotations.NotNull()
    okhttp3.MultipartBody.Part file, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.UserProfile> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/me/leaderboard-stats")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object myLeaderboardStats(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.LeaderboardEntry> $completion);
    
    @retrofit2.http.GET(value = "api/v1/books/{id}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object book(@retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.Book> $completion);
    
    @retrofit2.http.GET(value = "api/v1/books/search")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchBooks(@retrofit2.http.Query(value = "q")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.BookListResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/books/latest")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object latestBooks(@retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.BookListResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/books/random")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object randomBooks(@retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.BookListResponse> $completion);
    
    @retrofit2.http.POST(value = "api/v1/books/{id}/like")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object likeBook(@retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.DELETE(value = "api/v1/books/{id}/like")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object unlikeBook(@retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.GET(value = "api/v1/books/{id}/reviews")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object bookReviews(@retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.BookReviewsResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/books/{id}/reviews/friends")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object bookReviewsByFriends(@retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.FriendReviewsResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/books/{id}/friends-reading")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object bookFriendsReading(@retrofit2.http.Path(value = "id")
    @org.jetbrains.annotations.NotNull()
    java.lang.String id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.FriendsReadingResponse> $completion);
    
    @retrofit2.http.POST(value = "api/v1/search/vibe")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object vibeSearch(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Object> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.BookListResponse> $completion);
    
    @retrofit2.http.POST(value = "api/v1/users/{username}/bookshelf")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object addToBookshelf(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.AddToBookshelfBody body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.DELETE(value = "api/v1/users/{username}/bookshelf/{bookId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object removeFromBookshelf(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "bookId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String bookId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.PATCH(value = "api/v1/users/{username}/bookshelf/{bookId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateBookshelfRating(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "bookId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String bookId, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ReviewBody body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.ReviewUpdateResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/bookshelf/{bookId}/status")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object bookStatus(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "bookId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String bookId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.BookStatusResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/bookshelf/{bookId}/progress")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object readingProgress(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "bookId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String bookId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.ReadingProgress> $completion);
    
    @retrofit2.http.PUT(value = "api/v1/users/{username}/bookshelf/{bookId}/progress")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateReadingProgress(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "bookId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String bookId, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ProgressBody body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.ProgressUpdateResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/bookshelf")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userBookshelf(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Query(value = "status")
    @org.jetbrains.annotations.Nullable()
    java.lang.String status, @retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.BookshelfResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object profile(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.UserProfile> $completion);
    
    @retrofit2.http.PUT(value = "api/v1/users/{username}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateProfile(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Object> body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.UserProfile> $completion);
    
    @retrofit2.http.POST(value = "api/v1/users/{username}/follow")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object follow(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.FollowResponse> $completion);
    
    @retrofit2.http.DELETE(value = "api/v1/users/{username}/follow")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object unfollow(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.FollowResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/followers")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object followers(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.UserListResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/following")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object following(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.UserListResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/search")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object searchUsers(@retrofit2.http.Query(value = "query")
    @org.jetbrains.annotations.NotNull()
    java.lang.String query, @retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.UserListResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/likes")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userLikes(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.LikesResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/tbr")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userTbr(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<in.paperboxd.app.domain.model.TbrItem>> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/authors")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userAuthors(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<in.paperboxd.app.domain.model.AuthorSummary>> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/favorites")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userFavorites(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<in.paperboxd.app.domain.model.FavoriteBook>> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/streak")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userStreak(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.StreakResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/lists")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userLists(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.UserListsResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/reading/last")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object lastLoggedBook(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.LastLoggedBookResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/diary")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object userDiary(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.DiaryEntriesResponse> $completion);
    
    @retrofit2.http.POST(value = "api/v1/users/{username}/diary")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object createDiaryEntry(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.DiaryCreateBody body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.DiaryEntry> $completion);
    
    @retrofit2.http.GET(value = "api/v1/users/{username}/diary/{entryId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getDiaryEntry(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "entryId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String entryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.DiaryEntry> $completion);
    
    @retrofit2.http.DELETE(value = "api/v1/users/{username}/diary/{entryId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteDiaryEntry(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "entryId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String entryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.POST(value = "api/v1/users/{username}/diary/{entryId}/like")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object likeDiaryEntry(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "entryId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String entryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.DELETE(value = "api/v1/users/{username}/diary/{entryId}/like")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object unlikeDiaryEntry(@retrofit2.http.Path(value = "username")
    @org.jetbrains.annotations.NotNull()
    java.lang.String username, @retrofit2.http.Path(value = "entryId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String entryId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super okhttp3.ResponseBody> $completion);
    
    @retrofit2.http.GET(value = "api/v1/activities/following")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object followingActivities(@retrofit2.http.Query(value = "page")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer page, @retrofit2.http.Query(value = "page_size")
    @org.jetbrains.annotations.Nullable()
    java.lang.Integer pageSize, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.FollowingActivitiesResponse> $completion);
    
    @retrofit2.http.POST(value = "api/v1/events")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object trackEvent(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.TrackEventBody body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super retrofit2.Response<okhttp3.ResponseBody>> $completion);
    
    @retrofit2.http.GET(value = "api/v1/leaderboard/global")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object globalLeaderboard(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.LeaderboardResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/leaderboard/friends")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object friendsLeaderboard(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.LeaderboardResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/leaderboard/dimension/{dimension}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object leaderboardByDimension(@retrofit2.http.Path(value = "dimension")
    @org.jetbrains.annotations.NotNull()
    java.lang.String dimension, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.LeaderboardResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/recommendations/home")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object homeRecommendations(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.HomeRecommendationsResponse> $completion);
    
    @retrofit2.http.GET(value = "api/v1/recommendations/similar/{bookId}")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object similarBooks(@retrofit2.http.Path(value = "bookId")
    @org.jetbrains.annotations.NotNull()
    java.lang.String bookId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.SimilarBooksResponse> $completion);
    
    @retrofit2.http.POST(value = "api/v1/scan/analyze")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object scanAnalyze(@retrofit2.http.Body()
    @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ScanAnalyzeBody body, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.domain.model.ScanAnalyzeResponse> $completion);
    
    /**
     * Every endpoint the app touches, mirroring iOS Endpoints.swift against
     * paperboxd-backend MOBILE_API.md.
     */
    @kotlin.Metadata(mv = {1, 9, 0}, k = 3, xi = 48)
    public static final class DefaultImpls {
    }
}