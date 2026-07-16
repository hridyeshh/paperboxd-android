package in.paperboxd.app.ui.screens.leaderboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u000e\u0010\u000e\u001a\u00020\u000fH\u0082@\u00a2\u0006\u0002\u0010\u0010J\u000e\u0010\u0011\u001a\u00020\u000fH\u0082@\u00a2\u0006\u0002\u0010\u0010J\u000e\u0010\u0012\u001a\u00020\u000f2\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010\u0015\u001a\u00020\u000fR\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardViewModel;", "Landroidx/lifecycle/ViewModel;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "celebrationCenter", "Lin/paperboxd/app/ui/components/CelebrationCenter;", "(Lin/paperboxd/app/data/repository/UserRepository;Lin/paperboxd/app/ui/components/CelebrationCenter;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardUiState;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadEntries", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadMyStats", "onTabSelected", "tab", "Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardTab;", "refresh", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class LeaderboardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.ui.components.CelebrationCenter celebrationCenter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.leaderboard.LeaderboardUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.leaderboard.LeaderboardUiState> state = null;
    
    @javax.inject.Inject()
    public LeaderboardViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.components.CelebrationCenter celebrationCenter) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.leaderboard.LeaderboardUiState> getState() {
        return null;
    }
    
    public final void onTabSelected(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.leaderboard.LeaderboardTab tab) {
    }
    
    public final void refresh() {
    }
    
    private final java.lang.Object loadEntries(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object loadMyStats(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
}