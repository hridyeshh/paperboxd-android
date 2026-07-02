package in.paperboxd.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000`\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0003\u001al\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\r2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\u0014H\u0003\u001a\u0018\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0017\u001a\u00020\b2\u0006\u0010\u0018\u001a\u00020\u0019H\u0003\u001a\u0010\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\bH\u0003\u001a\u0090\u0001\u0010\u001c\u001a\u00020\u00012\u0006\u0010\u001d\u001a\u00020\b2\u0006\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u000e\u001a\u00020\r2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010 \u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00142\u0012\u0010!\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00142\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\"\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00142\b\b\u0002\u0010#\u001a\u00020$H\u0007\u001a&\u0010%\u001a\u00020\u00012\u0006\u0010&\u001a\u00020\'2\u0006\u0010(\u001a\u00020\b2\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a$\u0010)\u001a\u00020\u00012\u0006\u0010*\u001a\u00020+2\u0012\u0010,\u001a\u000e\u0012\u0004\u0012\u00020+\u0012\u0004\u0012\u00020\u00010\u0014H\u0003\u00a8\u0006-"}, d2 = {"DiaryRow", "", "entry", "Lin/paperboxd/app/domain/model/DiaryEntry;", "onClick", "Lkotlin/Function0;", "EmptyTab", "message", "", "Header", "state", "Lin/paperboxd/app/ui/screens/profile/ProfileUiState;", "isOwn", "", "showBack", "onBack", "onFollow", "onOpenSettings", "onOpenEditProfile", "onStats", "Lkotlin/Function1;", "Lin/paperboxd/app/ui/screens/profile/FollowListMode;", "ListRow", "title", "bookCount", "", "Pill", "text", "ProfileScreen", "username", "viewer", "Lin/paperboxd/app/domain/model/User;", "onOpenBook", "onOpenProfile", "onOpenDiaryEntry", "viewModel", "Lin/paperboxd/app/ui/screens/profile/ProfileViewModel;", "Stat", "count", "", "label", "TabDock", "selected", "Lin/paperboxd/app/ui/screens/profile/ProfileTab;", "onSelect", "app_debug"})
public final class ProfileScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void ProfileScreen(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User viewer, boolean showBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onBack, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenProfile, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenSettings, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onOpenEditProfile, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenDiaryEntry, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.profile.ProfileViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Header(in.paperboxd.app.ui.screens.profile.ProfileUiState state, boolean isOwn, boolean showBack, kotlin.jvm.functions.Function0<kotlin.Unit> onBack, kotlin.jvm.functions.Function0<kotlin.Unit> onFollow, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenSettings, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenEditProfile, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.profile.FollowListMode, kotlin.Unit> onStats) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Stat(int count, java.lang.String label, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Pill(java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TabDock(in.paperboxd.app.ui.screens.profile.ProfileTab selected, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.profile.ProfileTab, kotlin.Unit> onSelect) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DiaryRow(in.paperboxd.app.domain.model.DiaryEntry entry, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ListRow(java.lang.String title, long bookCount) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyTab(java.lang.String message) {
    }
}