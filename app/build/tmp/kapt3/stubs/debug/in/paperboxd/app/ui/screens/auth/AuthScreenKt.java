package in.paperboxd.app.ui.screens.auth;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u001aJ\u0010\u0000\u001a\u00020\u000126\u0010\u0002\u001a2\u0012\u0013\u0012\u00110\u0004\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\u0007\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\u0005\u0012\b\b\u0006\u0012\u0004\b\b(\t\u0012\u0004\u0012\u00020\u00010\u00032\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0007\u001a\u0018\u0010\f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0018\u0010\u000f\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u001a\u0018\u0010\u0010\u001a\u00020\u00012\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u000bH\u0003\u00a8\u0006\u0011"}, d2 = {"AuthScreen", "", "onSignedIn", "Lkotlin/Function2;", "", "Lkotlin/ParameterName;", "name", "token", "Lin/paperboxd/app/domain/model/User;", "user", "viewModel", "Lin/paperboxd/app/ui/screens/auth/AuthViewModel;", "LoginForm", "state", "Lin/paperboxd/app/ui/screens/auth/AuthUiState;", "OtpForm", "RegisterForm", "app_debug"})
public final class AuthScreenKt {
    
    /**
     * Auth container: Login / Register / OTP modes switch inline, mirroring iOS.
     */
    @androidx.compose.runtime.Composable()
    public static final void AuthScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super in.paperboxd.app.domain.model.User, kotlin.Unit> onSignedIn, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.auth.AuthViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LoginForm(in.paperboxd.app.ui.screens.auth.AuthUiState state, in.paperboxd.app.ui.screens.auth.AuthViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RegisterForm(in.paperboxd.app.ui.screens.auth.AuthUiState state, in.paperboxd.app.ui.screens.auth.AuthViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OtpForm(in.paperboxd.app.ui.screens.auth.AuthUiState state, in.paperboxd.app.ui.screens.auth.AuthViewModel viewModel) {
    }
}