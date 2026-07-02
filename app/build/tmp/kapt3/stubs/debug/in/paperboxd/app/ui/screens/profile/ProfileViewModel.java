package in.paperboxd.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000v\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0012\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0006\u0010\"\u001a\u00020#J\u000e\u0010$\u001a\u00020#H\u0082@\u00a2\u0006\u0002\u0010%J\u0006\u0010&\u001a\u00020#J\u000e\u0010\'\u001a\u00020#2\u0006\u0010(\u001a\u00020)J\u000e\u0010*\u001a\u00020#H\u0082@\u00a2\u0006\u0002\u0010%J\u0006\u0010+\u001a\u00020#J\u000e\u0010,\u001a\u00020#2\u0006\u0010(\u001a\u00020-J\u000e\u0010.\u001a\u00020#H\u0082@\u00a2\u0006\u0002\u0010%J\u000e\u0010/\u001a\u00020#2\u0006\u00100\u001a\u000201J\u0016\u00102\u001a\u00020#2\u0006\u00103\u001a\u00020\f2\u0006\u00104\u001a\u00020!J\u0006\u00105\u001a\u00020#J\u000e\u00106\u001a\u00020#2\u0006\u00107\u001a\u000208R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0013\u001a\u00020\u000e8F\u00a2\u0006\u0006\u001a\u0004\b\u0013\u0010\u0014R\u000e\u0010\u0015\u001a\u00020\fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u000eX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\t0\u0019\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\f0\u001d\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u00069"}, d2 = {"Lin/paperboxd/app/ui/screens/profile/ProfileViewModel;", "Landroidx/lifecycle/ViewModel;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "diaryRepository", "Lin/paperboxd/app/data/repository/DiaryRepository;", "(Lin/paperboxd/app/data/repository/UserRepository;Lin/paperboxd/app/data/repository/DiaryRepository;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/profile/ProfileUiState;", "_toast", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "", "diaryHasMore", "", "diaryPage", "", "isLoadingDiary", "isLoadingShelf", "isOwnProfile", "()Z", "profileUsername", "shelfHasMore", "shelfPage", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "toast", "Lkotlinx/coroutines/flow/SharedFlow;", "getToast", "()Lkotlinx/coroutines/flow/SharedFlow;", "viewer", "Lin/paperboxd/app/domain/model/User;", "fetchAll", "", "fetchAuthors", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchDiary", "fetchDiaryIfNeeded", "item", "Lin/paperboxd/app/domain/model/DiaryEntry;", "fetchLists", "fetchShelf", "fetchShelfIfNeeded", "Lin/paperboxd/app/domain/model/BookWithStatus;", "fetchTbr", "onTabSelected", "tab", "Lin/paperboxd/app/ui/screens/profile/ProfileTab;", "start", "username", "viewerUser", "toggleFollow", "uploadBanner", "bytes", "", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class ProfileViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.DiaryRepository diaryRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.profile.ProfileUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.profile.ProfileUiState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<java.lang.String> _toast = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<java.lang.String> toast = null;
    @org.jetbrains.annotations.NotNull()
    private java.lang.String profileUsername = "";
    @org.jetbrains.annotations.Nullable()
    private in.paperboxd.app.domain.model.User viewer;
    private int shelfPage = 1;
    private boolean shelfHasMore = true;
    private boolean isLoadingShelf = false;
    private int diaryPage = 1;
    private boolean diaryHasMore = true;
    private boolean isLoadingDiary = false;
    
    @javax.inject.Inject()
    public ProfileViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.DiaryRepository diaryRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.profile.ProfileUiState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<java.lang.String> getToast() {
        return null;
    }
    
    public final boolean isOwnProfile() {
        return false;
    }
    
    public final void start(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User viewerUser) {
    }
    
    public final void fetchAll() {
    }
    
    public final void onTabSelected(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.profile.ProfileTab tab) {
    }
    
    public final void fetchShelf() {
    }
    
    public final void fetchShelfIfNeeded(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.BookWithStatus item) {
    }
    
    public final void fetchDiary() {
    }
    
    public final void fetchDiaryIfNeeded(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.DiaryEntry item) {
    }
    
    private final java.lang.Object fetchLists(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object fetchTbr(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object fetchAuthors(kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void toggleFollow() {
    }
    
    public final void uploadBanner(@org.jetbrains.annotations.NotNull()
    byte[] bytes) {
    }
}