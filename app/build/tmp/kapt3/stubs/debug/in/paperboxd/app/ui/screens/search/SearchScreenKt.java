package in.paperboxd.app.ui.screens.search;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000J\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0010\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\bH\u0003\u001aL\u0010\t\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a8\u0010\u0010\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a\u0090\u0001\u0010\u0012\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\r2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\rH\u0007\u001a.\u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\b2\u0012\u0010\u0013\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\b\b\u0002\u0010\u001a\u001a\u00020\u001bH\u0003\u001a\b\u0010\u001c\u001a\u00020\u0001H\u0003\u001a:\u0010\u001d\u001a\u00020\u00012\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\u0012\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\r2\b\b\u0002\u0010\u001e\u001a\u00020\u001fH\u0007\u001a\b\u0010 \u001a\u00020\u0001H\u0003\u001a@\u0010!\u001a\u00020\u00012\u0006\u0010\n\u001a\u00020\u000b2\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a$\u0010\"\u001a\u00020\u00012\u0006\u0010#\u001a\u00020\u00152\u0012\u0010$\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00010\rH\u0003\u001a\u001e\u0010%\u001a\u00020\u00012\u0006\u0010&\u001a\u00020\'2\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u00a8\u0006("}, d2 = {"BookRow", "", "book", "Lin/paperboxd/app/domain/model/Book;", "onClick", "Lkotlin/Function0;", "EmptyState", "message", "", "ReadersIdle", "state", "Lin/paperboxd/app/ui/screens/search/SearchUiState;", "onOpenProfile", "Lkotlin/Function1;", "onTermClick", "onRemoveFromHistory", "ResultsList", "onOpenBook", "SearchContent", "onQueryChange", "onTypeSelect", "Lin/paperboxd/app/ui/screens/search/SearchType;", "onShuffleWall", "onLoadMoreWall", "SearchField", "query", "modifier", "Landroidx/compose/ui/Modifier;", "SearchPreview", "SearchScreen", "viewModel", "Lin/paperboxd/app/ui/screens/search/SearchViewModel;", "ShimmerRows", "TrendingWall", "TypeTabs", "selected", "onSelect", "UserRow", "user", "Lin/paperboxd/app/domain/model/UserProfile;", "app_debug"})
public final class SearchScreenKt {
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SearchScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenProfile, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.search.SearchViewModel viewModel) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void SearchContent(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.search.SearchUiState state, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onQueryChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.search.SearchType, kotlin.Unit> onTypeSelect, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onShuffleWall, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLoadMoreWall, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onRemoveFromHistory, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenProfile) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void SearchField(java.lang.String query, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onQueryChange, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TypeTabs(in.paperboxd.app.ui.screens.search.SearchType selected, kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.search.SearchType, kotlin.Unit> onSelect) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ResultsList(in.paperboxd.app.ui.screens.search.SearchUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenProfile) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BookRow(in.paperboxd.app.domain.model.Book book, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void UserRow(in.paperboxd.app.domain.model.UserProfile user, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ReadersIdle(in.paperboxd.app.ui.screens.search.SearchUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenProfile, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onTermClick, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onRemoveFromHistory) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    private static final void TrendingWall(in.paperboxd.app.ui.screens.search.SearchUiState state, kotlin.jvm.functions.Function0<kotlin.Unit> onShuffleWall, kotlin.jvm.functions.Function0<kotlin.Unit> onLoadMoreWall, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ShimmerRows() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void EmptyState(java.lang.String message) {
    }
    
    @androidx.compose.ui.tooling.preview.Preview()
    @androidx.compose.runtime.Composable()
    private static final void SearchPreview() {
    }
}