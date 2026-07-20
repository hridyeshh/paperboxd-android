package in.paperboxd.app.ui.screens.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\u001a \u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0001\u0018\u00010\u0005H\u0003\u001a\b\u0010\u0006\u001a\u00020\u0001H\u0003\u001a8\u0010\u0007\u001a\u00020\u00012\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00010\u000b2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0007\u001a\u000e\u0010\u000e\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\f\u00a8\u0006\u0010"}, d2 = {"ActivityRow", "", "activity", "Lin/paperboxd/app/domain/model/ActivityItem;", "onClick", "Lkotlin/Function0;", "EmptyUpdates", "NotificationsSheet", "activities", "", "onOpenBook", "Lkotlin/Function1;", "", "onDismiss", "relativeTime", "iso", "app_release"})
public final class NotificationsSheetKt {
    
    /**
     * Friends activity feed — the bell target on Home. iOS NotificationsView twin:
     * light paper sheet, "@user verb title" rows on brutalist cards, accent dot.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void NotificationsSheet(@org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.ActivityItem> activities, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ActivityRow(in.paperboxd.app.domain.model.ActivityItem activity, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyUpdates() {
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