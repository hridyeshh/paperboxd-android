package in.paperboxd.app.ui.screens.search;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0007\b\u0007\u0018\u0000 /2\u00020\u0001:\u0001/B!\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\u000e\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001f0!H\u0002J\u0006\u0010\"\u001a\u00020\u001dJ\b\u0010#\u001a\u00020\u001dH\u0002J\u0006\u0010$\u001a\u00020\u001dJ\u000e\u0010%\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u001fJ\u000e\u0010\'\u001a\u00020\u001d2\u0006\u0010(\u001a\u00020)J\u000e\u0010*\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\u0016\u0010+\u001a\u00020\u001d2\u0006\u0010,\u001a\u00020\u001fH\u0082@\u00a2\u0006\u0002\u0010-J\u0006\u0010.\u001a\u00020\u001dR\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n \u0010*\u0004\u0018\u00010\u000f0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u0018X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u001bX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00060"}, d2 = {"Lin/paperboxd/app/ui/screens/search/SearchViewModel;", "Landroidx/lifecycle/ViewModel;", "context", "Landroid/content/Context;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "(Landroid/content/Context;Lin/paperboxd/app/data/repository/BookRepository;Lin/paperboxd/app/data/repository/UserRepository;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/search/SearchUiState;", "gson", "Lcom/google/gson/Gson;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "searchJob", "Lkotlinx/coroutines/Job;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "suggestedLoaded", "", "wallHasMore", "wallPage", "", "addToHistory", "", "term", "", "loadHistory", "", "loadMoreWall", "loadSuggestedReaders", "loadWallIfNeeded", "onQueryChanged", "raw", "onTypeChanged", "type", "Lin/paperboxd/app/ui/screens/search/SearchType;", "removeFromHistory", "search", "q", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "shuffleWall", "Companion", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SearchViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.search.SearchUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.search.SearchUiState> state = null;
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final com.google.gson.Gson gson = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    private int wallPage = 1;
    private boolean wallHasMore = true;
    private boolean suggestedLoaded = false;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String HISTORY_KEY = "pb_search_history";
    @org.jetbrains.annotations.NotNull()
    private static final in.paperboxd.app.ui.screens.search.SearchViewModel.Companion Companion = null;
    
    @javax.inject.Inject()
    public SearchViewModel(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.search.SearchUiState> getState() {
        return null;
    }
    
    public final void onQueryChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String raw) {
    }
    
    public final void onTypeChanged(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.search.SearchType type) {
    }
    
    private final java.lang.Object search(java.lang.String q, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void loadWallIfNeeded() {
    }
    
    public final void loadMoreWall() {
    }
    
    /**
     * Pull-to-refresh: `/books/random` returns a fresh random slice server-side.
     */
    public final void shuffleWall() {
    }
    
    private final void loadSuggestedReaders() {
    }
    
    public final void addToHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String term) {
    }
    
    public final void removeFromHistory(@org.jetbrains.annotations.NotNull()
    java.lang.String term) {
    }
    
    private final java.util.List<java.lang.String> loadHistory() {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/ui/screens/search/SearchViewModel$Companion;", "", "()V", "HISTORY_KEY", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}