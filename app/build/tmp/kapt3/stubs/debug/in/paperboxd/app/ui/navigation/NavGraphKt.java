package in.paperboxd.app.ui.navigation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000&\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u001a\u0018\u0010\b\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0006\u001a\u00020\u0007H\u0007\u001a\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\f\u001a\u00020\u0003H\u0007\"\u001a\u0010\u0000\u001a\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"tabRoutes", "", "Lin/paperboxd/app/ui/navigation/DockTab;", "", "AppRoot", "", "appState", "Lin/paperboxd/app/AppState;", "MainScaffold", "user", "Lin/paperboxd/app/domain/model/User;", "PlaceholderScreen", "name", "app_debug"})
public final class NavGraphKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.Map<in.paperboxd.app.ui.navigation.DockTab, java.lang.String> tabRoutes = null;
    
    /**
     * Root switch on the AppState destination — splash / auth / onboarding / main.
     */
    @androidx.compose.runtime.Composable()
    public static final void AppRoot(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.AppState appState) {
    }
    
    /**
     * Main tab container: one NavHost, per-tab back stacks preserved via
     * saveState/restoreState (the Android twin of iOS per-tab NavigationStacks).
     */
    @androidx.compose.runtime.Composable()
    public static final void MainScaffold(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.AppState appState) {
    }
    
    /**
     * Temporary stand-in until each screen lands.
     */
    @androidx.compose.runtime.Composable()
    public static final void PlaceholderScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String name) {
    }
}