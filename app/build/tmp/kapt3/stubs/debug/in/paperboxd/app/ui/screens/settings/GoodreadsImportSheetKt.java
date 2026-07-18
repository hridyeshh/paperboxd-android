package in.paperboxd.app.ui.screens.settings;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000&\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\u001a\u001e\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a.\u0010\u0006\u001a\u00020\u00012\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\b2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a \u0010\f\u001a\u00020\u00012\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\u000e\u001a\u00020\u000fH\u0007\u001a\u0016\u0010\u0010\u001a\u00020\u00012\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005H\u0003\u001a\u0018\u0010\u0012\u001a\u00020\u00012\u0006\u0010\u0013\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\bH\u0003\u001a\u0018\u0010\u0014\u001a\u00020\u00012\u0006\u0010\u0015\u001a\u00020\u00032\u0006\u0010\u0016\u001a\u00020\bH\u0003\u00a8\u0006\u0017"}, d2 = {"ErrorView", "", "message", "", "onRetry", "Lkotlin/Function0;", "FinishedView", "imported", "", "skipped", "total", "onAnother", "GoodreadsImportSheet", "onDismiss", "viewModel", "Lin/paperboxd/app/ui/screens/settings/GoodreadsImportViewModel;", "IdleView", "onChoose", "ImportingView", "done", "ResultRow", "label", "value", "app_debug"})
public final class GoodreadsImportSheetKt {
    
    /**
     * Goodreads CSV import, presented as a bottom sheet over Settings.
     * Twin of iOS GoodreadsImportView (idle → importing → finished).
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void GoodreadsImportSheet(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel viewModel) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void IdleView(kotlin.jvm.functions.Function0<kotlin.Unit> onChoose) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ImportingView(int done, int total) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FinishedView(int imported, int skipped, int total, kotlin.jvm.functions.Function0<kotlin.Unit> onAnother) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ErrorView(java.lang.String message, kotlin.jvm.functions.Function0<kotlin.Unit> onRetry) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ResultRow(java.lang.String label, int value) {
    }
}