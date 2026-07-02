package in.paperboxd.app.ui.screens.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000L\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0002\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0003\u001a*\u0010\u0002\u001a\u00020\u00012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u0007H\u0003\u001a<\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0007\u001a\u001e\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a\u001e\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00162\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a\u0018\u0010\u0017\u001a\u00020\u00012\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\bH\u0007\u001a,\u0010\u001a\u001a\u00020\u00012\u0006\u0010\u001b\u001a\u00020\u001c2\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u00a8\u0006\u001e"}, d2 = {"DotGridBackground", "", "FreshShelvesRow", "books", "", "Lin/paperboxd/app/domain/model/Book;", "onOpenBook", "Lkotlin/Function1;", "", "HomeScreen", "user", "Lin/paperboxd/app/domain/model/User;", "onWrite", "Lkotlin/Function0;", "viewModel", "Lin/paperboxd/app/ui/screens/home/HomeViewModel;", "LastLoggedCard", "book", "Lin/paperboxd/app/domain/model/LastLoggedBook;", "onClick", "RecommendationCard", "rec", "Lin/paperboxd/app/domain/model/RecommendationItem;", "SectionHeader", "eyebrow", "title", "TopBar", "hasUnread", "", "onBell", "app_debug"})
public final class HomeScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void HomeScreen(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onWrite, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.home.HomeViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TopBar(boolean hasUnread, kotlin.jvm.functions.Function0<kotlin.Unit> onBell, kotlin.jvm.functions.Function0<kotlin.Unit> onWrite) {
    }
    
    /**
     * Barely-visible dot grid, iOS Home background twin.
     */
    @androidx.compose.runtime.Composable()
    private static final void DotGridBackground() {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void SectionHeader(@org.jetbrains.annotations.NotNull()
    java.lang.String eyebrow, @org.jetbrains.annotations.NotNull()
    java.lang.String title) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LastLoggedCard(in.paperboxd.app.domain.model.LastLoggedBook book, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RecommendationCard(in.paperboxd.app.domain.model.RecommendationItem rec, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FreshShelvesRow(java.util.List<in.paperboxd.app.domain.model.Book> books, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook) {
    }
}