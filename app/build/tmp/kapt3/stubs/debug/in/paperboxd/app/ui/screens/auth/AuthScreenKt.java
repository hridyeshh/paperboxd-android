package in.paperboxd.app.ui.screens.auth;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000~\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\u001a\u00ba\u0001\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\u0012\u0010\u0016\u001a\u000e\u0012\u0004\u0012\u00020\u0017\u0012\u0004\u0012\u00020\u00070\u000bH\u0007\u001a\b\u0010\u0018\u001a\u00020\u0007H\u0003\u001aJ\u0010\u0019\u001a\u00020\u000726\u0010\u001a\u001a2\u0012\u0013\u0012\u00110\f\u00a2\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b(\u001e\u0012\u0013\u0012\u00110\u001f\u00a2\u0006\f\b\u001c\u0012\b\b\u001d\u0012\u0004\b\b( \u0012\u0004\u0012\u00020\u00070\u001b2\b\b\u0002\u0010!\u001a\u00020\"H\u0007\u001a\u0018\u0010#\u001a\u00020\u00072\u0006\u0010$\u001a\u00020\f2\u0006\u0010%\u001a\u00020\fH\u0003\u001a\u0010\u0010&\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0003\u001a0\u0010\'\u001a\u00020\u00072\u0006\u0010(\u001a\u00020\f2\u0006\u0010)\u001a\u00020\f2\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\b\b\u0002\u0010+\u001a\u00020,H\u0003\u001ap\u0010-\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010.\u001a\b\u0012\u0004\u0012\u00020\u00070\u0011H\u0003\u001a@\u0010/\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\u000f\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00070\u0011H\u0003\u001av\u00102\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0012\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\u0012\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00070\u000b2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u00101\u001a\b\u0012\u0004\u0012\u00020\u00070\u0011H\u0003\u001a\u0010\u00103\u001a\u00020\u00072\u0006\u00104\u001a\u000205H\u0003\u001a@\u00106\u001a\u00020\u00072\u0006\u00107\u001a\u0002082\u0012\u00109\u001a\u000e\u0012\u0004\u0012\u000208\u0012\u0004\u0012\u00020\u00070\u000b2\f\u0010:\u001a\b\u0012\u0004\u0012\u00020\u00070\u00112\f\u0010;\u001a\b\u0012\u0004\u0012\u00020\u00070\u0011H\u0003\u001a\u001c\u0010<\u001a\u00020\u00072\b\u0010=\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010+\u001a\u00020,H\u0003\u001a\u0010\u0010>\u001a\u00020\f2\u0006\u0010?\u001a\u00020@H\u0002\u001a\u0015\u0010A\u001a\u00020\u00042\u0006\u0010B\u001a\u00020CH\u0002\u00a2\u0006\u0002\u0010D\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0005\u00a8\u0006E"}, d2 = {"CardHorizontal", "Landroidx/compose/ui/unit/Dp;", "F", "SuccessGreen", "Landroidx/compose/ui/graphics/Color;", "J", "AuthContent", "", "state", "Lin/paperboxd/app/ui/screens/auth/AuthUiState;", "onEmailChange", "Lkotlin/Function1;", "", "onPasswordChange", "onConfirmPasswordChange", "onOtpChange", "onLogin", "Lkotlin/Function0;", "onRegister", "onSendOtp", "onForgotPassword", "onLoginWithGoogle", "onSwitchMode", "Lin/paperboxd/app/ui/screens/auth/AuthMode;", "AuthPreview", "AuthScreen", "onSignedIn", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "name", "token", "Lin/paperboxd/app/domain/model/User;", "user", "viewModel", "Lin/paperboxd/app/ui/screens/auth/AuthViewModel;", "CardHeader", "title", "subtitle", "FeedbackRow", "LinkText", "prefix", "action", "onClick", "modifier", "Landroidx/compose/ui/Modifier;", "LoginForm", "onSwitchToRegister", "OtpForm", "onResendOtp", "onSwitchToLogin", "RegisterForm", "StrengthBar", "strength", "Lin/paperboxd/app/ui/screens/auth/PasswordStrength;", "TermsConsent", "checked", "", "onCheckedChange", "onOpenTerms", "onOpenPrivacy", "TopBar", "eyebrow", "formatCountdown", "totalSeconds", "", "w", "a", "", "(F)J", "app_debug"})
public final class AuthScreenKt {
    private static final float CardHorizontal = 0.0F;
    private static final long SuccessGreen = 0L;
    
    private static final long w(float a) {
        return 0L;
    }
    
    @androidx.compose.runtime.Composable()
    public static final void AuthScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super in.paperboxd.app.domain.model.User, kotlin.Unit> onSignedIn, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.auth.AuthViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void AuthContent(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.auth.AuthUiState state, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onEmailChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPasswordChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onConfirmPasswordChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOtpChange, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLogin, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onRegister, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onSendOtp, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onForgotPassword, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onLoginWithGoogle, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.auth.AuthMode, kotlin.Unit> onSwitchMode) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TopBar(java.lang.String eyebrow, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CardHeader(java.lang.String title, java.lang.String subtitle) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FeedbackRow(in.paperboxd.app.ui.screens.auth.AuthUiState state) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LinkText(java.lang.String prefix, java.lang.String action, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LoginForm(in.paperboxd.app.ui.screens.auth.AuthUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onEmailChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPasswordChange, kotlin.jvm.functions.Function0<kotlin.Unit> onLogin, kotlin.jvm.functions.Function0<kotlin.Unit> onLoginWithGoogle, kotlin.jvm.functions.Function0<kotlin.Unit> onForgotPassword, kotlin.jvm.functions.Function0<kotlin.Unit> onSwitchToRegister) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void RegisterForm(in.paperboxd.app.ui.screens.auth.AuthUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onEmailChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onPasswordChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onConfirmPasswordChange, kotlin.jvm.functions.Function0<kotlin.Unit> onRegister, kotlin.jvm.functions.Function0<kotlin.Unit> onLoginWithGoogle, kotlin.jvm.functions.Function0<kotlin.Unit> onSwitchToLogin) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TermsConsent(boolean checked, kotlin.jvm.functions.Function1<? super java.lang.Boolean, kotlin.Unit> onCheckedChange, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenTerms, kotlin.jvm.functions.Function0<kotlin.Unit> onOpenPrivacy) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StrengthBar(in.paperboxd.app.ui.screens.auth.PasswordStrength strength) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OtpForm(in.paperboxd.app.ui.screens.auth.AuthUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOtpChange, kotlin.jvm.functions.Function0<kotlin.Unit> onResendOtp, kotlin.jvm.functions.Function0<kotlin.Unit> onSwitchToLogin) {
    }
    
    private static final java.lang.String formatCountdown(int totalSeconds) {
        return null;
    }
    
    @androidx.compose.ui.tooling.preview.Preview()
    @androidx.compose.runtime.Composable()
    private static final void AuthPreview() {
    }
}