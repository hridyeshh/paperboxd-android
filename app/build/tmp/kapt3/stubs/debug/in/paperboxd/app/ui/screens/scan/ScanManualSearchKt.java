package in.paperboxd.app.ui.screens.scan;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000*\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u001aa\u0010\u0000\u001a\u00020\u00012(\u0010\u0002\u001a$\b\u0001\u0012\u0004\u0012\u00020\u0004\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u00060\u0005\u0012\u0006\u0012\u0004\u0018\u00010\b0\u00032\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\u001a\u0010\u000b\u001a\u0016\u0012\u0004\u0012\u00020\u0004\u0012\u0006\u0012\u0004\u0018\u00010\u0004\u0012\u0004\u0012\u00020\u00010\u0003H\u0007\u00a2\u0006\u0002\u0010\f\u001a\u0010\u0010\r\u001a\u00020\u00012\u0006\u0010\u000e\u001a\u00020\u0004H\u0003\u00a8\u0006\u000f"}, d2 = {"ManualSearchSheet", "", "search", "Lkotlin/Function2;", "", "Lkotlin/coroutines/Continuation;", "", "Lin/paperboxd/app/ui/screens/scan/ScanSearchHit;", "", "onDismiss", "Lkotlin/Function0;", "onPick", "(Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;)V", "Note", "text", "app_debug"})
public final class ScanManualSearchKt {
    
    /**
     * "Type the title instead" — iOS `ManualSearchSheet` twin. Searches
     * `/books/search` and, on tap, feeds the chosen book's ISBN into the scan
     * analysis (the backend scores by ISBN).
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ManualSearchSheet(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super kotlin.coroutines.Continuation<? super java.util.List<in.paperboxd.app.ui.screens.scan.ScanSearchHit>>, ? extends java.lang.Object> search, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onPick) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void Note(java.lang.String text) {
    }
}