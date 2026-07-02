package in.paperboxd.app.ui.screens.leaderboard;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0011\b\u0087\b\u0018\u00002\u00020\u0001BA\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\u000f\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u00c6\u0003J\u000b\u0010\u001c\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\tH\u00c6\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\u000bH\u00c6\u0003JE\u0010\u001f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\b\u001a\u00020\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000bH\u00c6\u0001J\u0013\u0010 \u001a\u00020\t2\b\u0010!\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\"\u001a\u00020\u0013H\u00d6\u0001J\t\u0010#\u001a\u00020\u000bH\u00d6\u0001R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0011R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u00138F\u00a2\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019\u00a8\u0006$"}, d2 = {"Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardUiState;", "", "tab", "Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardTab;", "entries", "", "Lin/paperboxd/app/domain/model/LeaderboardEntry;", "myStats", "isLoading", "", "errorMessage", "", "(Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardTab;Ljava/util/List;Lin/paperboxd/app/domain/model/LeaderboardEntry;ZLjava/lang/String;)V", "getEntries", "()Ljava/util/List;", "getErrorMessage", "()Ljava/lang/String;", "()Z", "myRank", "", "getMyRank", "()Ljava/lang/Integer;", "getMyStats", "()Lin/paperboxd/app/domain/model/LeaderboardEntry;", "getTab", "()Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardTab;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
public final class LeaderboardUiState {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.ui.screens.leaderboard.LeaderboardTab tab = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.List<in.paperboxd.app.domain.model.LeaderboardEntry> entries = null;
    @org.jetbrains.annotations.Nullable()
    private final in.paperboxd.app.domain.model.LeaderboardEntry myStats = null;
    private final boolean isLoading = false;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String errorMessage = null;
    
    public LeaderboardUiState(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.leaderboard.LeaderboardTab tab, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.LeaderboardEntry> entries, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.LeaderboardEntry myStats, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.leaderboard.LeaderboardTab getTab() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.LeaderboardEntry> getEntries() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.LeaderboardEntry getMyStats() {
        return null;
    }
    
    public final boolean isLoading() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getErrorMessage() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getMyRank() {
        return null;
    }
    
    public LeaderboardUiState() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.leaderboard.LeaderboardTab component1() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.LeaderboardEntry> component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.LeaderboardEntry component3() {
        return null;
    }
    
    public final boolean component4() {
        return false;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component5() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final in.paperboxd.app.ui.screens.leaderboard.LeaderboardUiState copy(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.leaderboard.LeaderboardTab tab, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.LeaderboardEntry> entries, @org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.LeaderboardEntry myStats, boolean isLoading, @org.jetbrains.annotations.Nullable()
    java.lang.String errorMessage) {
        return null;
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public java.lang.String toString() {
        return null;
    }
}