package in.paperboxd.app.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J&\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\f\u0010\rJ\u001c\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0010\u0010\u0011J$\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00130\b2\u0006\u0010\u0014\u001a\u00020\u0015H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0016\u0010\u0017J\u0018\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0014\u001a\u00020\u00152\b\b\u0002\u0010\u001a\u001a\u00020\u0015R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u001b"}, d2 = {"Lin/paperboxd/app/data/repository/RecommendationRepository;", "", "api", "Lin/paperboxd/app/data/remote/ApiService;", "(Lin/paperboxd/app/data/remote/ApiService;)V", "fireAndForgetScope", "Lkotlinx/coroutines/CoroutineScope;", "followingActivities", "Lkotlin/Result;", "Lin/paperboxd/app/domain/model/FollowingActivitiesResponse;", "pageSize", "", "followingActivities-gIAlu-s", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "home", "Lin/paperboxd/app/domain/model/HomeRecommendationsResponse;", "home-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "similar", "Lin/paperboxd/app/domain/model/SimilarBooksResponse;", "bookId", "", "similar-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "trackImpression", "", "source", "app_debug"})
public final class RecommendationRepository {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.remote.ApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope fireAndForgetScope = null;
    
    @javax.inject.Inject()
    public RecommendationRepository(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.remote.ApiService api) {
        super();
    }
    
    /**
     * Fire-and-forget impression tracking — never blocks or surfaces errors.
     */
    public final void trackImpression(@org.jetbrains.annotations.NotNull()
    java.lang.String bookId, @org.jetbrains.annotations.NotNull()
    java.lang.String source) {
    }
}