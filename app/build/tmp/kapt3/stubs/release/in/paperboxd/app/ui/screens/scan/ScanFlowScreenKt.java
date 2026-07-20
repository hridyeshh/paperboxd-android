package in.paperboxd.app.ui.screens.scan;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000\u001a\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a(\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u0007\u00a8\u0006\b"}, d2 = {"ScanFlowScreen", "", "user", "Lin/paperboxd/app/domain/model/User;", "onDismiss", "Lkotlin/Function0;", "viewModel", "Lin/paperboxd/app/ui/screens/scan/ScanFlowViewModel;", "app_release"})
public final class ScanFlowScreenKt {
    
    /**
     * Scan & Know flow coordinator — iOS `ScanFlowView` twin. Presented full-screen
     * from the dock's Pip scan button.
     *
     * Stages: Scan → Analyzing (the games fill the wait while the backend scores the
     * scanned ISBN) → Reveal (count-up) → Breakdown. The book + score + breakdown
     * are real — fetched from `POST /api/v1/scan/analyze`, not hardcoded.
     */
    @androidx.compose.runtime.Composable()
    public static final void ScanFlowScreen(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.scan.ScanFlowViewModel viewModel) {
    }
}