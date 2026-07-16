package in.paperboxd.app.ui.screens.scan;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u0002\u001aL\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0007\u001a\u0010\u0010\f\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0003\u001a\u0016\u0010\r\u001a\u00020\u00012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fH\u0007\u001a \u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0003\u00a8\u0006\u0016"}, d2 = {"BreakdownScreen", "", "result", "Lin/paperboxd/app/ui/screens/scan/ScanResult;", "tbrState", "Lin/paperboxd/app/ui/screens/scan/TbrState;", "toast", "", "onAddToTbr", "Lkotlin/Function0;", "onToastShown", "onClose", "CompareRow", "RadarChart", "dimensions", "", "Lin/paperboxd/app/ui/screens/scan/ScanResult$Dimension;", "RadarLabel", "text", "xFrac", "", "yFrac", "app_debug"})
public final class BreakdownScreenKt {
    
    /**
     * 04 — Breakdown · MINIMALIST resolution — iOS `BreakdownScreen` twin. Everything
     * exhales: no boxes, no shadows — whitespace, hairline splits, a serif headline,
     * a thin radar, and plain-English reasons.
     */
    @androidx.compose.runtime.Composable()
    public static final void BreakdownScreen(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.scan.ScanResult result, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.scan.TbrState tbrState, @org.jetbrains.annotations.Nullable()
    java.lang.String toast, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onAddToTbr, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onToastShown, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CompareRow(in.paperboxd.app.ui.screens.scan.ScanResult result) {
    }
    
    @androidx.compose.runtime.Composable()
    public static final void RadarChart(@org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.ui.screens.scan.ScanResult.Dimension> dimensions) {
    }
    
    /**
     * Places a small mono label centered at a fractional position of the parent.
     */
    @androidx.compose.runtime.Composable()
    private static final void RadarLabel(java.lang.String text, float xFrac, float yFrac) {
    }
}