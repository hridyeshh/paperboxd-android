package in.paperboxd.app.ui.screens.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003\u001a$\u0010\u0004\u001a\u00020\u00012\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00030\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0007\u001a\u000e\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n\u00a8\u0006\f"}, d2 = {"ActivityRow", "", "activity", "Lin/paperboxd/app/domain/model/ActivityItem;", "NotificationsSheet", "activities", "", "onDismiss", "Lkotlin/Function0;", "relativeTime", "", "iso", "app_debug"})
public final class NotificationsSheetKt {
    
    /**
     * Friends activity feed sheet — the bell target on Home (iOS NotificationsView).
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void NotificationsSheet(@org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.ActivityItem> activities, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ActivityRow(in.paperboxd.app.domain.model.ActivityItem activity) {
    }
    
    /**
     * "now / 5m / 3h / 2d" — iOS FriendActivity.relativeTime twin.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String relativeTime(@org.jetbrains.annotations.NotNull()
    java.lang.String iso) {
        return null;
    }
}