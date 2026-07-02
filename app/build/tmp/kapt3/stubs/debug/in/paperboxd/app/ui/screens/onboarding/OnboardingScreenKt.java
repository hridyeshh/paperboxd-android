package in.paperboxd.app.ui.screens.onboarding;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000D\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0003\u001a\u0018\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a\u0018\u0010\u0007\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a<\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\n2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u000f2\b\b\u0002\u0010\u0005\u001a\u00020\u0006H\u0007\u001a\u0018\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0003\u001a&\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00010\u000fH\u0003\u001a \u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0082@\u00a2\u0006\u0002\u0010\u0019\u00a8\u0006\u001a"}, d2 = {"AhaLoadingStep", "", "AhaRevealStep", "state", "Lin/paperboxd/app/ui/screens/onboarding/OnboardingUiState;", "viewModel", "Lin/paperboxd/app/ui/screens/onboarding/OnboardingViewModel;", "GenresStep", "OnboardingScreen", "user", "Lin/paperboxd/app/domain/model/User;", "onUsernameSet", "Lkotlin/Function1;", "", "onFinished", "Lkotlin/Function0;", "TempoStep", "UsernameStep", "onPickAvatar", "readAndDownscale", "", "context", "Landroid/content/Context;", "uri", "Landroid/net/Uri;", "(Landroid/content/Context;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class OnboardingScreenKt {
    
    /**
     * Multi-step onboarding: username → genres → tempo → aha loading → aha reveal.
     */
    @androidx.compose.runtime.Composable()
    public static final void OnboardingScreen(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onUsernameSet, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFinished, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel viewModel) {
    }
    
    /**
     * Reads + downscales the picked image off the main thread (max 1024px, JPEG 85).
     */
    private static final java.lang.Object readAndDownscale(android.content.Context context, android.net.Uri uri, kotlin.coroutines.Continuation<? super byte[]> $completion) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void UsernameStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel viewModel, kotlin.jvm.functions.Function0<kotlin.Unit> onPickAvatar) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.layout.ExperimentalLayoutApi.class})
    @androidx.compose.runtime.Composable()
    private static final void GenresStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TempoStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AhaLoadingStep() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AhaRevealStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel viewModel) {
    }
}