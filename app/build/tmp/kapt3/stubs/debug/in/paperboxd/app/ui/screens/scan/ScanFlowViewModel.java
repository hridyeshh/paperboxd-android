package in.paperboxd.app.ui.screens.scan;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011J\u0006\u0010\u0012\u001a\u00020\u000fJ\u0010\u0010\u0013\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0015H\u0002J\u000e\u0010\u0016\u001a\u00020\u000f2\u0006\u0010\u0017\u001a\u00020\u0018J\u0018\u0010\u0019\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u001a\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0006\u0010\u001c\u001a\u00020\u000fJ\u0006\u0010\u001d\u001a\u00020\u000fJ\u001c\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001f2\u0006\u0010!\u001a\u00020\u0011H\u0086@\u00a2\u0006\u0002\u0010\u001bJ\u0018\u0010\"\u001a\u00020\u000f2\u0006\u0010\u001a\u001a\u00020\u00112\b\u0010#\u001a\u0004\u0018\u00010\u0011R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r\u00a8\u0006$"}, d2 = {"Lin/paperboxd/app/ui/screens/scan/ScanFlowViewModel;", "Landroidx/lifecycle/ViewModel;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "context", "Landroid/content/Context;", "(Lin/paperboxd/app/data/repository/BookRepository;Landroid/content/Context;)V", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/scan/ScanFlowUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "addToTbr", "", "username", "", "clearToast", "friendlyMessage", "e", "", "go", "stage", "Lin/paperboxd/app/ui/screens/scan/ScanStage;", "lookupTitle", "isbn", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "reset", "retry", "search", "", "Lin/paperboxd/app/ui/screens/scan/ScanSearchHit;", "query", "startScan", "title", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ScanFlowViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.scan.ScanFlowUiState> _uiState = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.scan.ScanFlowUiState> uiState = null;
    
    @javax.inject.Inject()
    public ScanFlowViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository, @dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.scan.ScanFlowUiState> getUiState() {
        return null;
    }
    
    public final void go(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.scan.ScanStage stage) {
    }
    
    public final void startScan(@org.jetbrains.annotations.NotNull()
    java.lang.String isbn, @org.jetbrains.annotations.Nullable()
    java.lang.String title) {
    }
    
    public final void retry() {
    }
    
    private final java.lang.String friendlyMessage(java.lang.Throwable e) {
        return null;
    }
    
    /**
     * Title (or ISBN) search via `/books/search` — iOS `ScanService.search` twin.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object search(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<in.paperboxd.app.ui.screens.scan.ScanSearchHit>> $completion) {
        return null;
    }
    
    /**
     * Best-effort title for a scanned ISBN, to show on the confirmation card.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object lookupTitle(@org.jetbrains.annotations.NotNull()
    java.lang.String isbn, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.String> $completion) {
        return null;
    }
    
    /**
     * Adds the scanned book to the reader's TBR. The backend resolves the book
     * by ISBN and auto-creates it if it isn't cached yet.
     */
    public final void addToTbr(@org.jetbrains.annotations.Nullable()
    java.lang.String username) {
    }
    
    public final void clearToast() {
    }
    
    /**
     * Wipes the flow back to a fresh scanner. The VM is Activity-scoped (the flow
     * isn't a nav destination), so it survives close/reopen — without this, the
     * next open would re-render the previous book's breakdown instead of the camera.
     */
    public final void reset() {
    }
}