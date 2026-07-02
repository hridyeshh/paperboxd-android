package in.paperboxd.app.ui.screens.leaderboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\f\u001a\u00020\rH\u0082@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u000f\u001a\u00020\rH\u0082@\u00a2\u0006\u0002\u0010\u000eJ\u000e\u0010\u0010\u001a\u00020\r2\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\rR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardViewModel;", "Landroidx/lifecycle/ViewModel;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "(Lin/paperboxd/app/data/repository/UserRepository;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardUiState;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "loadEntries", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadMyStats", "onTabSelected", "tab", "Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardTab;", "refresh", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class LeaderboardViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.leaderboard.LeaderboardUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.leaderboard.LeaderboardUiState> state = null;
    
    @javax.inject.Inject()
    public LeaderboardViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository) {
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