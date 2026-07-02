package in.paperboxd.app.ui.screens.bookdetail;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0006\u0010%\u001a\u00020&J,\u0010\'\u001a\u00020&2\u0006\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010\u000e2\u0012\u0010+\u001a\u000e\u0012\u0004\u0012\u00020-\u0012\u0004\u0012\u00020&0,J\u0006\u0010.\u001a\u00020&J\u0006\u0010/\u001a\u00020&J\u0006\u00100\u001a\u00020&J\u0016\u00101\u001a\u00020&2\u0006\u00102\u001a\u00020)2\u0006\u00103\u001a\u00020)R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u000f\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u00138F\u00a2\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0017\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u000e0\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR(\u0010 \u001a\u0004\u0018\u00010\u001f2\b\u0010\u001e\u001a\u0004\u0018\u00010\u001f@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$\u00a8\u00064"}, d2 = {"Lin/paperboxd/app/ui/screens/bookdetail/BookDetailViewModel;", "Landroidx/lifecycle/ViewModel;", "savedStateHandle", "Landroidx/lifecycle/SavedStateHandle;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "recommendationRepository", "Lin/paperboxd/app/data/repository/RecommendationRepository;", "(Landroidx/lifecycle/SavedStateHandle;Lin/paperboxd/app/data/repository/BookRepository;Lin/paperboxd/app/data/repository/RecommendationRepository;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/bookdetail/BookDetailUiState;", "_toast", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "", "bookId", "getBookId", "()Ljava/lang/String;", "myReview", "Lin/paperboxd/app/domain/model/BookReview;", "getMyReview", "()Lin/paperboxd/app/domain/model/BookReview;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "toast", "Lkotlinx/coroutines/flow/SharedFlow;", "getToast", "()Lkotlinx/coroutines/flow/SharedFlow;", "value", "Lin/paperboxd/app/domain/model/User;", "user", "getUser", "()Lin/paperboxd/app/domain/model/User;", "setUser", "(Lin/paperboxd/app/domain/model/User;)V", "fetchAll", "", "submitReview", "rating", "", "review", "onDone", "Lkotlin/Function1;", "", "toggleBookshelf", "toggleLike", "toggleTbr", "updateProgress", "currentPage", "totalPages", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class BookDetailViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.RecommendationRepository recommendationRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String bookId = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.bookdetail.BookDetailUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.bookdetail.BookDetailUiState> state = null;
    
    /**
     * One-shot snackbar messages ("Review posted", revert errors).
     */
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<java.lang.String> _toast = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<java.lang.String> toast = null;
    @org.jetbrains.annotations.Nullable()
    private in.paperboxd.app.domain.model.User user;
    
    @javax.inject.Inject()
    public BookDetailViewModel(@org.jetbrains.annotations.NotNull()
    androidx.lifecycle.SavedStateHandle savedStateHandle, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.RecommendationRepository recommendationRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getBookId() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.bookdetail.BookDetailUiState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<java.lang.String> getToast() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.User getUser() {
        return null;
    }
    
    public final void setUser(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.User value) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.BookReview getMyReview() {
        return null;
    }
    
    public final void fetchAll() {
    }
    
    public final void toggleBookshelf() {
    }
    
    public final void toggleLike() {
    }
    
    public final void toggleTbr() {
    }
    
    /**
     * Shelves the book first if needed (backend stores reviews on the shelf entry).
     */
    public final void submitReview(int rating, @org.jetbrains.annotations.Nullable()
    java.lang.String review, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onDone) {
    }
    
    public final void updateProgress(int currentPage, int totalPages) {
    }
}