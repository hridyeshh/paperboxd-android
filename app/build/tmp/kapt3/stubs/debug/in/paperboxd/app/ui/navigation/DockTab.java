package in.paperboxd.app.ui.navigation;

/**
 * Dock tabs. Mirrors the shipped iOS MainTabView: Home | Search | Scan
 * (special center action) | Leaderboard | Profile. Scan is not a destination —
 * selecting it fires [onScan] and the selection stays put.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u00048F\u00a2\u0006\u0006\u001a\u0004\b\u0003\u0010\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n\u00a8\u0006\u000b"}, d2 = {"Lin/paperboxd/app/ui/navigation/DockTab;", "", "(Ljava/lang/String;I)V", "isSpecial", "", "()Z", "Home", "Search", "Scan", "Leaderboard", "Profile", "app_debug"})
public enum DockTab {
    /*public static final*/ Home /* = new Home() */,
    /*public static final*/ Search /* = new Search() */,
    /*public static final*/ Scan /* = new Scan() */,
    /*public static final*/ Leaderboard /* = new Leaderboard() */,
    /*public static final*/ Profile /* = new Profile() */;
    
    DockTab() {
    }
    
    public final boolean isSpecial() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<in.paperboxd.app.ui.navigation.DockTab> getEntries() {
        return null;
    }
}