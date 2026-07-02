package in.paperboxd.app.ui.screens.leaderboard;

/**
 * Mirrors iOS LeaderboardTab: global/friends use dedicated routes, others a dimension.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0002\u0010\u0004J\u0015\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\u000e\u0010\f\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nR\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006j\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012\u00a8\u0006\u0013"}, d2 = {"Lin/paperboxd/app/ui/screens/leaderboard/LeaderboardTab;", "", "dimension", "", "(Ljava/lang/String;ILjava/lang/String;)V", "getDimension", "()Ljava/lang/String;", "rank", "", "e", "Lin/paperboxd/app/domain/model/LeaderboardEntry;", "(Lin/paperboxd/app/domain/model/LeaderboardEntry;)Ljava/lang/Integer;", "value", "Global", "Books", "Pages", "Streak", "Diary", "Friends", "app_debug"})
public enum LeaderboardTab {
    /*public static final*/ Global /* = new Global(null) */,
    /*public static final*/ Books /* = new Books(null) */,
    /*public static final*/ Pages /* = new Pages(null) */,
    /*public static final*/ Streak /* = new Streak(null) */,
    /*public static final*/ Diary /* = new Diary(null) */,
    /*public static final*/ Friends /* = new Friends(null) */;
    @org.jetbrains.annotations.Nullable()
    private final java.lang.String dimension = null;
    
    LeaderboardTab(java.lang.String dimension) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDimension() {
        return null;
    }
    
    public final int value(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.LeaderboardEntry e) {
        return 0;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer rank(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.LeaderboardEntry e) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<in.paperboxd.app.ui.screens.leaderboard.LeaderboardTab> getEntries() {
        return null;
    }
}