package in.paperboxd.app.ui.navigation;

/**
 * Glass dock — iOS MainTabView CustomDock twin. Four icon-only tabs
 * (Home | Search | Leaderboard | Profile) in a dark capsule; the active tab
 * gets a soft glow halo + filled glyph. Scan is NOT here — it lives as the
 * floating [PipScanButton] above the dock.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lin/paperboxd/app/ui/navigation/DockTab;", "", "(Ljava/lang/String;I)V", "Home", "Search", "Leaderboard", "Profile", "app_debug"})
public enum DockTab {
    /*public static final*/ Home /* = new Home() */,
    /*public static final*/ Search /* = new Search() */,
    /*public static final*/ Leaderboard /* = new Leaderboard() */,
    /*public static final*/ Profile /* = new Profile() */;
    
    DockTab() {
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<in.paperboxd.app.ui.navigation.DockTab> getEntries() {
        return null;
    }
}