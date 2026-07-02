package in.paperboxd.app.ui.screens.auth;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0006\u0010\u0015\u001a\u00020\u0016J\u0006\u0010\u0017\u001a\u00020\u0016J\u0006\u0010\u0018\u001a\u00020\u0016J\b\u0010\u0019\u001a\u00020\u0016H\u0014J\u000e\u0010\u001a\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001e\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u001cJ\u000e\u0010\u001f\u001a\u00020\u00162\u0006\u0010\u001b\u001a\u00020\u001cJ\u0006\u0010 \u001a\u00020\u0016J\u0006\u0010!\u001a\u00020\u0016J\u0010\u0010\"\u001a\u00020\u00162\u0006\u0010#\u001a\u00020$H\u0002J\b\u0010%\u001a\u00020\u0016H\u0002J\u000e\u0010&\u001a\u00020\u00162\u0006\u0010\'\u001a\u00020(J\u0006\u0010)\u001a\u00020\u0016J+\u0010*\u001a\u00020\u00162\u001c\u0010+\u001a\u0018\b\u0001\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160-\u0012\u0006\u0012\u0004\u0018\u00010.0,H\u0002\u00a2\u0006\u0002\u0010/R\u0014\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00070\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\n0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014\u00a8\u00060"}, d2 = {"Lin/paperboxd/app/ui/screens/auth/AuthViewModel;", "Landroidx/lifecycle/ViewModel;", "authRepository", "Lin/paperboxd/app/data/repository/AuthRepository;", "(Lin/paperboxd/app/data/repository/AuthRepository;)V", "_authSuccess", "Lkotlinx/coroutines/flow/MutableSharedFlow;", "Lin/paperboxd/app/ui/screens/auth/AuthSuccess;", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/auth/AuthUiState;", "authSuccess", "Lkotlinx/coroutines/flow/SharedFlow;", "getAuthSuccess", "()Lkotlinx/coroutines/flow/SharedFlow;", "countdownJob", "Lkotlinx/coroutines/Job;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "forgotPassword", "", "login", "loginWithGoogle", "onCleared", "onConfirmPasswordChange", "v", "", "onEmailChange", "onOtpChange", "onPasswordChange", "register", "sendOtp", "startCountdown", "seconds", "", "stopCountdown", "switchTo", "next", "Lin/paperboxd/app/ui/screens/auth/AuthMode;", "verifyOtp", "withLoading", "block", "Lkotlin/Function1;", "Lkotlin/coroutines/Continuation;", "", "(Lkotlin/jvm/functions/Function1;)V", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class AuthViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.auth.AuthUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.auth.AuthUiState> state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableSharedFlow<in.paperboxd.app.ui.screens.auth.AuthSuccess> _authSuccess = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.SharedFlow<in.paperboxd.app.ui.screens.auth.AuthSuccess> authSuccess = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job countdownJob;
    
    @javax.inject.Inject()
    public AuthViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.AuthRepository authRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.auth.AuthUiState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.SharedFlow<in.paperboxd.app.ui.screens.auth.AuthSuccess> getAuthSuccess() {
        return null;
    }
    
    public final void onEmailChange(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void onPasswordChange(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void onConfirmPasswordChange(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void onOtpChange(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void switchTo(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.auth.AuthMode next) {
    }
    
    public final void login() {
    }
    
    public final void register() {
    }
    
    public final void sendOtp() {
    }
    
    public final void verifyOtp() {
    }
    
    public final void forgotPassword() {
    }
    
    public final void loginWithGoogle() {
    }
    
    private final void startCountdown(int seconds) {
    }
    
    private final void stopCountdown() {
    }
    
    private final void withLoading(kotlin.jvm.functions.Function1<? super kotlin.coroutines.Continuation<? super kotlin.Unit>, ? extends java.lang.Object> block) {
    }
    
    @java.lang.Override()
    protected void onCleared() {
    }
}