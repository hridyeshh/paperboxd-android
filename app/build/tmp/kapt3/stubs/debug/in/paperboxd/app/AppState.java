package in.paperboxd.app;

/**
 * App-level session state machine. Mirrors iOS AppState: bootstrap
 * (token → health → refresh), sign-in/out routing, onboarding gate,
 * and global 401 handling via [SessionEvents].
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\b\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017J\u000e\u0010\u0018\u001a\u00020\u0015H\u0086@\u00a2\u0006\u0002\u0010\u0019J\u0006\u0010\u001a\u001a\u00020\u0015J \u0010\u001b\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u001d2\b\b\u0002\u0010\u001e\u001a\u00020\u001dH\u0082@\u00a2\u0006\u0002\u0010\u001fJ\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\tH\u0002J\u0010\u0010#\u001a\u00020\u000b2\u0006\u0010\"\u001a\u00020\tH\u0002J\u000e\u0010$\u001a\u00020\u00152\u0006\u0010%\u001a\u00020\u0017J\u0006\u0010&\u001a\u00020\u0015J\u0016\u0010\'\u001a\u00020\u00152\u0006\u0010(\u001a\u00020\u00172\u0006\u0010\"\u001a\u00020\tR\u0016\u0010\u0007\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u000b0\bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\t0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006)"}, d2 = {"Lin/paperboxd/app/AppState;", "", "authRepository", "Lin/paperboxd/app/data/repository/AuthRepository;", "sessionEvents", "Lin/paperboxd/app/data/remote/SessionEvents;", "(Lin/paperboxd/app/data/repository/AuthRepository;Lin/paperboxd/app/data/remote/SessionEvents;)V", "_currentUser", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/domain/model/User;", "_destination", "Lin/paperboxd/app/AppDestination;", "currentUser", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrentUser", "()Lkotlinx/coroutines/flow/StateFlow;", "destination", "getDestination", "scope", "Lkotlinx/coroutines/CoroutineScope;", "avatarUpdated", "", "url", "", "bootstrap", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "finishOnboarding", "holdSplash", "startMillis", "", "minimumMillis", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "needsOnboarding", "", "user", "route", "setOnboardingUsername", "username", "signOut", "signedIn", "token", "app_debug"})
public final class AppState {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.AppDestination> _destination = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.AppDestination> destination = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.domain.model.User> _currentUser = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.domain.model.User> currentUser = null;
    
    @javax.inject.Inject()
    public AppState(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.remote.SessionEvents sessionEvents) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.AppDestination> getDestination() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.domain.model.User> getCurrentUser() {
        return null;
    }
    
    /**
     * Boots the app: prefs → health → refresh. Minimum splash hold 2.5s.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object bootstrap(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final java.lang.Object holdSplash(long startMillis, long minimumMillis, kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final in.paperboxd.app.AppDestination route(in.paperboxd.app.domain.model.User user) {
        return null;
    }
    
    /**
     * onboarding_completed missing (legacy payload) → fall back to username presence.
     */
    private final boolean needsOnboarding(in.paperboxd.app.domain.model.User user) {
        return false;
    }
    
    public final void signedIn(@org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user) {
    }
    
    /**
     * Mid-onboarding username claim — updates cache without routing.
     */
    public final void setOnboardingUsername(@org.jetbrains.annotations.NotNull()
    java.lang.String username) {
    }
    
    /**
     * Keeps the cached avatar in sync after an edit-profile upload.
     */
    public final void avatarUpdated(@org.jetbrains.annotations.Nullable()
    java.lang.String url) {
    }
    
    public final void finishOnboarding() {
    }
    
    public final void signOut() {
    }
}