package in.paperboxd.app.ui.screens.jazy;

/**
 * Ask Jazy's vibe search. iOS twin: the search half of `JazyView`.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0006\b\u0007\u0018\u0000 \u001a2\u00020\u0001:\u0001\u001aB\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011J\u0006\u0010\u0012\u001a\u00020\u000fJ\u0016\u0010\u0013\u001a\u00020\u000f2\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@\u00a2\u0006\u0002\u0010\u0016J\u000e\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u0011J\u0006\u0010\u0019\u001a\u00020\u000fR\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006\u001b"}, d2 = {"Lin/paperboxd/app/ui/screens/jazy/JazyViewModel;", "Landroidx/lifecycle/ViewModel;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "(Lin/paperboxd/app/data/repository/BookRepository;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/jazy/JazyUiState;", "searchJob", "Lkotlinx/coroutines/Job;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "appendChip", "", "chip", "", "closeResults", "holdSearchingScreen", "startedAt", "", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "onQueryChanged", "raw", "submit", "Companion", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class JazyViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.jazy.JazyUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.jazy.JazyUiState> state = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    @java.lang.Deprecated()
    public static final long MIN_SEARCH_DISPLAY_MS = 2000L;
    @org.jetbrains.annotations.NotNull()
    private static final in.paperboxd.app.ui.screens.jazy.JazyViewModel.Companion Companion = null;
    
    @javax.inject.Inject()
    public JazyViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.jazy.JazyUiState> getState() {
        return null;
    }
    
    public final void onQueryChanged(@org.jetbrains.annotations.NotNull()
    java.lang.String raw) {
    }
    
    public final void appendChip(@org.jetbrains.annotations.NotNull()
    java.lang.String chip) {
    }
    
    public final void submit() {
    }
    
    /**
     * The searching screen holds for at least [MIN_SEARCH_DISPLAY_MS] even when
     * the backend answers sooner — a 300ms flash of book covers reads as a glitch.
     */
    private final java.lang.Object holdSearchingScreen(long startedAt, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void closeResults() {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lin/paperboxd/app/ui/screens/jazy/JazyViewModel$Companion;", "", "()V", "MIN_SEARCH_DISPLAY_MS", "", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}