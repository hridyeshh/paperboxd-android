package in.paperboxd.app.ui.screens.write;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000`\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\'\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017J\u000e\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u00152\u0006\u0010\u001e\u001a\u00020\u0017J\u0010\u0010\u001f\u001a\u00020\u00152\b\u0010 \u001a\u0004\u0018\u00010!J\u000e\u0010\"\u001a\u00020\u00152\u0006\u0010#\u001a\u00020\u0017R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\r0\u0011\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006$"}, d2 = {"Lin/paperboxd/app/ui/screens/write/WriteViewModel;", "Landroidx/lifecycle/ViewModel;", "diaryRepository", "Lin/paperboxd/app/data/repository/DiaryRepository;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "celebrationCenter", "Lin/paperboxd/app/ui/components/CelebrationCenter;", "(Lin/paperboxd/app/data/repository/DiaryRepository;Lin/paperboxd/app/data/repository/BookRepository;Lin/paperboxd/app/data/repository/UserRepository;Lin/paperboxd/app/ui/components/CelebrationCenter;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/write/WriteUiState;", "searchJob", "Lkotlinx/coroutines/Job;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "onContentChange", "", "v", "", "onDateChange", "millis", "", "onRate", "", "onSearchChange", "q", "selectBook", "book", "Lin/paperboxd/app/domain/model/Book;", "submit", "username", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class WriteViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.DiaryRepository diaryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.ui.components.CelebrationCenter celebrationCenter = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.write.WriteUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.write.WriteUiState> state = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job searchJob;
    
    @javax.inject.Inject()
    public WriteViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.DiaryRepository diaryRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.components.CelebrationCenter celebrationCenter) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.write.WriteUiState> getState() {
        return null;
    }
    
    public final void onContentChange(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void onRate(int v) {
    }
    
    public final void onDateChange(long millis) {
    }
    
    public final void selectBook(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.Book book) {
    }
    
    public final void onSearchChange(@org.jetbrains.annotations.NotNull()
    java.lang.String q) {
    }
    
    public final void submit(@org.jetbrains.annotations.NotNull()
    java.lang.String username) {
    }
}