package in.paperboxd.app.ui.screens.onboarding;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000~\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u0016\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020\u0010H\u0082@\u00a2\u0006\u0002\u0010$J\u0006\u0010%\u001a\u00020\"J\u0006\u0010&\u001a\u00020\"J\u0006\u0010\'\u001a\u00020\"J\u0010\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020\u0010H\u0002J\u000e\u0010+\u001a\u00020\"2\u0006\u0010,\u001a\u00020\u0010J\u000e\u0010-\u001a\u00020\"2\u0006\u0010.\u001a\u00020\u0010J\u0006\u0010/\u001a\u00020\"J\u000e\u00100\u001a\u00020\"2\u0006\u00101\u001a\u00020\u0010J\u0006\u00102\u001a\u00020\"J\u0006\u00103\u001a\u00020\"J\u0006\u00104\u001a\u00020\"J\u000e\u00105\u001a\u00020\"2\u0006\u00101\u001a\u00020\u0010J\u000e\u00106\u001a\u00020\"2\u0006\u00107\u001a\u000208J+\u00109\u001a\u00020\"2\u001c\u0010:\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020\"0<\u0012\u0006\u0012\u0004\u0018\u00010=0;H\u0002\u00a2\u0006\u0002\u0010>J\u000e\u0010?\u001a\u00020)*\u0004\u0018\u00010\u0010H\u0002R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000b0\u0014\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001cR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000e0\u001e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 \u00a8\u0006@"}, d2 = {"Lin/paperboxd/app/ui/screens/onboarding/OnboardingViewModel;", "Landroidx/lifecycle/ViewModel;", "authRepository", "Lin/paperboxd/app/data/repository/AuthRepository;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "recommendationRepository", "Lin/paperboxd/app/data/repository/RecommendationRepository;", "(Lin/paperboxd/app/data/repository/AuthRepository;Lin/paperboxd/app/data/repository/BookRepository;Lin/paperboxd/app/data/repository/RecommendationRepository;)V", "_events", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lin/paperboxd/app/ui/screens/onboarding/OnboardingEvent;", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/onboarding/OnboardingUiState;", "claimedUsername", "", "debounceJob", "Lkotlinx/coroutines/Job;", "events", "Lkotlinx/coroutines/flow/SharedFlow;", "getEvents", "()Lkotlinx/coroutines/flow/SharedFlow;", "initialUser", "Lin/paperboxd/app/domain/model/User;", "getInitialUser", "()Lin/paperboxd/app/domain/model/User;", "setInitialUser", "(Lin/paperboxd/app/domain/model/User;)V", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "check", "", "username", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "continueFromGenres", "continueFromTempo", "finish", "isLocallyValid", "", "s", "onDisplayNameChange", "v", "onUsernameChange", "raw", "saveToShelf", "selectTempo", "id", "showAnother", "skip", "submitUsername", "toggleGenre", "uploadAvatar", "bytes", "", "withSubmitting", "block", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "", "(Lkotlin/jvm/functions/Function1;)V", "isValidCover", "app_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class OnboardingViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.RecommendationRepository recommendationRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.onboarding.OnboardingUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.onboarding.OnboardingUiState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<in.paperboxd.app.ui.screens.onboarding.OnboardingEvent> _events = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<in.paperboxd.app.ui.screens.onboarding.OnboardingEvent> events = null;
    
    /**
     * Set once by the screen from AppState's user (id/email/username fallback).
     */
    @org.jetbrains.annotations.Nullable()
    private in.paperboxd.app.domain.model.User initialUser;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String claimedUsername;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job debounceJob;
    
    @javax.inject.Inject()
    public OnboardingViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.RecommendationRepository recommendationRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.onboarding.OnboardingUiState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<in.paperboxd.app.ui.screens.onboarding.OnboardingEvent> getEvents() {
        return null;
    }
    
    /**
     * Set once by the screen from AppState's user (id/email/username fallback).
     */
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.User getInitialUser() {
        return null;
    }
    
    /**
     * Set once by the screen from AppState's user (id/email/username fallback).
     */
    public final void setInitialUser(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.User p0) {
    }
    
    public final void onUsernameChange(@org.jetbrains.annotations.NotNull()
    java.lang.String raw) {
    }
    
    public final void onDisplayNameChange(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    private final java.lang.Object check(java.lang.String username, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    public final void submitUsername() {
    }
    
    /**
     * Skip the whole flow: claim the fallback username, then finish.
     */
    public final void skip() {
    }
    
    public final void uploadAvatar(@org.jetbrains.annotations.NotNull()
    byte[] bytes) {
    }
    
    public final void toggleGenre(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    public final void continueFromGenres() {
    }
    
    public final void selectTempo(@org.jetbrains.annotations.NotNull()
    java.lang.String id) {
    }
    
    /**
     * Persists genres (tempo is cosmetic), then fetches the aha reveal books.
     */
    public final void continueFromTempo() {
    }
    
    public final void showAnother() {
    }
    
    public final void saveToShelf() {
    }
    
    public final void finish() {
    }
    
    private final boolean isLocallyValid(java.lang.String s) {
        return false;
    }
    
    private final void withSubmitting(kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> block) {
    }
    
    private final boolean isValidCover(java.lang.String $this$isValidCover) {
        return false;
    }
}