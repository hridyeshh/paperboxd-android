package in.paperboxd.app.ui.screens.onboarding;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000h\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\b\u0010\u0000\u001a\u00020\u0001H\u0003\u001a,\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a\u0010\u0010\b\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u0004H\u0003\u001a(\u0010\t\u001a\u00020\u00012\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a$\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u0013H\u0003\u001a\u0010\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u0011H\u0003\u001a2\u0010\u0016\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a&\u0010\u0019\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u00112\u0006\u0010\u001b\u001a\u00020\r2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a<\u0010\u001d\u001a\u00020\u00012\u0006\u0010\u001e\u001a\u00020\u00112\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u0010\u001f\u001a\u00020 2\b\b\u0002\u0010!\u001a\u00020\r2\b\b\u0002\u0010\"\u001a\u00020\rH\u0003\u001a\u0010\u0010#\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u0011H\u0003\u001a\u0010\u0010$\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u0011H\u0003\u001a\u00be\u0001\u0010%\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\'\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a<\u0010,\u001a\u00020\u00012\u0006\u0010-\u001a\u00020.2\u0012\u0010/\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\f\u00100\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\b\b\u0002\u00101\u001a\u000202H\u0007\u001a\u001a\u00103\u001a\u00020\u00012\u0006\u00104\u001a\u0002052\b\b\u0002\u0010\u001f\u001a\u00020 H\u0003\u001a \u00106\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u00112\u0006\u00107\u001a\u00020\r2\u0006\u00108\u001a\u00020\rH\u0003\u001a.\u00109\u001a\u00020\u00012\u0006\u0010\u001a\u001a\u00020\u00112\u0006\u0010:\u001a\u00020\u00112\u0006\u0010;\u001a\u00020\r2\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a2\u0010<\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010*\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010+\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a$\u0010=\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u0013H\u0003\u001a^\u0010>\u001a\u00020\u00012\u0006\u0010\u0003\u001a\u00020\u00042\b\u0010\n\u001a\u0004\u0018\u00010\u000b2\f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00010\u00062\u0012\u0010\'\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00010\u00132\f\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00010\u0006H\u0003\u001a \u0010?\u001a\u0004\u0018\u00010@2\u0006\u0010A\u001a\u00020B2\u0006\u0010C\u001a\u00020DH\u0080@\u00a2\u0006\u0002\u0010E\u00a8\u0006F"}, d2 = {"AhaLoadingStep", "", "AhaRevealStep", "state", "Lin/paperboxd/app/ui/screens/onboarding/OnboardingUiState;", "onSaveToShelf", "Lkotlin/Function0;", "onShowAnother", "AvailabilityHint", "AvatarRow", "avatarModel", "", "uploading", "", "onPick", "DisplayNameField", "value", "", "onChange", "Lkotlin/Function1;", "FieldLabel", "text", "GenresStep", "onToggleGenre", "onContinueFromGenres", "OnbChip", "label", "isOn", "onClick", "OnbPrimaryButton", "title", "modifier", "Landroidx/compose/ui/Modifier;", "loading", "enabled", "OnbSubtitle", "OnbTitle", "OnboardingContent", "onPickAvatar", "onUsernameChange", "onDisplayNameChange", "onSubmitUsername", "onSelectTempo", "onContinueFromTempo", "OnboardingScreen", "user", "Lin/paperboxd/app/domain/model/User;", "onUsernameSet", "onFinished", "viewModel", "Lin/paperboxd/app/ui/screens/onboarding/OnboardingViewModel;", "StageHeader", "step", "Lin/paperboxd/app/ui/screens/onboarding/OnboardingStep;", "StagePill", "done", "current", "TempoRow", "sub", "active", "TempoStep", "UsernameField", "UsernameStep", "readAndDownscale", "", "context", "Landroid/content/Context;", "uri", "Landroid/net/Uri;", "(Landroid/content/Context;Landroid/net/Uri;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_release"})
public final class OnboardingScreenKt {
    
    @androidx.compose.runtime.Composable()
    public static final void OnboardingScreen(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onUsernameSet, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onFinished, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OnboardingContent(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, java.lang.Object avatarModel, kotlin.jvm.functions.Function0<kotlin.Unit> onPickAvatar, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onUsernameChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onDisplayNameChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSubmitUsername, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onToggleGenre, kotlin.jvm.functions.Function0<kotlin.Unit> onContinueFromGenres, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSelectTempo, kotlin.jvm.functions.Function0<kotlin.Unit> onContinueFromTempo, kotlin.jvm.functions.Function0<kotlin.Unit> onSaveToShelf, kotlin.jvm.functions.Function0<kotlin.Unit> onShowAnother) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StageHeader(in.paperboxd.app.ui.screens.onboarding.OnboardingStep step, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void StagePill(java.lang.String label, boolean done, boolean current) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OnbTitle(java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void OnbSubtitle(java.lang.String text) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FieldLabel(java.lang.String text) {
    }
    
    /**
     * Dark pill CTA on the light onboarding background — iOS `OnboardingPrimaryButton`.
     */
    @androidx.compose.runtime.Composable()
    private static final void OnbPrimaryButton(java.lang.String title, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier, boolean loading, boolean enabled) {
    }
    
    /**
     * Selectable genre chip — iOS `OnboardingChip`.
     */
    @androidx.compose.runtime.Composable()
    private static final void OnbChip(java.lang.String label, boolean isOn, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void UsernameStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, java.lang.Object avatarModel, kotlin.jvm.functions.Function0<kotlin.Unit> onPickAvatar, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onUsernameChange, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onDisplayNameChange, kotlin.jvm.functions.Function0<kotlin.Unit> onSubmitUsername) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AvatarRow(java.lang.Object avatarModel, boolean uploading, kotlin.jvm.functions.Function0<kotlin.Unit> onPick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void UsernameField(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onChange) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AvailabilityHint(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void DisplayNameField(java.lang.String value, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onChange) {
    }
    
    @kotlin.OptIn(markerClass = {androidx.compose.foundation.layout.ExperimentalLayoutApi.class})
    @androidx.compose.runtime.Composable()
    private static final void GenresStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onToggleGenre, kotlin.jvm.functions.Function0<kotlin.Unit> onContinueFromGenres) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TempoStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onSelectTempo, kotlin.jvm.functions.Function0<kotlin.Unit> onContinueFromTempo) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void TempoRow(java.lang.String label, java.lang.String sub, boolean active, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AhaLoadingStep() {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void AhaRevealStep(in.paperboxd.app.ui.screens.onboarding.OnboardingUiState state, kotlin.jvm.functions.Function0<kotlin.Unit> onSaveToShelf, kotlin.jvm.functions.Function0<kotlin.Unit> onShowAnother) {
    }
    
    /**
     * Reads + downscales the picked image off the main thread (max 1024px, JPEG 85).
     */
    @org.jetbrains.annotations.Nullable()
    public static final java.lang.Object readAndDownscale(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    android.net.Uri uri, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super byte[]> $completion) {
        return null;
    }
}