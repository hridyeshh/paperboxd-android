package in.paperboxd.app.ui.screens.scan;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000H\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\u001a\u0012\u0010\u0000\u001a\u00020\u00012\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0003\u001a(\u0010\u0004\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00010\b2\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u0003\u001a:\u0010\t\u001a\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\u000e\b\u0002\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0003\u001a0\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u000b2\b\u0010\u0012\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0013\u001a\u00020\u000e2\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0003\u001a\u0087\u0001\u0010\u0015\u001a\u00020\u00012$\u0010\u0016\u001a \b\u0001\u0012\u0004\u0012\u00020\u000b\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\u000b0\u0018\u0012\u0006\u0012\u0004\u0018\u00010\u00190\u00172(\u0010\u001a\u001a$\b\u0001\u0012\u0004\u0012\u00020\u000b\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001c0\u001b0\u0018\u0012\u0006\u0012\u0004\u0018\u00010\u00190\u00172\u001a\u0010\u0014\u001a\u0016\u0012\u0004\u0012\u00020\u000b\u0012\u0006\u0012\u0004\u0018\u00010\u000b\u0012\u0004\u0012\u00020\u00010\u00172\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u00010\bH\u0007\u00a2\u0006\u0002\u0010\u001e\u00a8\u0006\u001f"}, d2 = {"BracketsLayer", "", "modifier", "Landroidx/compose/ui/Modifier;", "CircleIcon", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "onClick", "Lkotlin/Function0;", "FallbackCard", "title", "", "body", "showSettings", "", "onSettings", "IsbnCard", "isbn", "bookTitle", "titleLoading", "onScanIt", "ScanScreen", "lookupTitle", "Lkotlin/Function2;", "Lkotlin/coroutines/Continuation;", "", "search", "", "Lin/paperboxd/app/ui/screens/scan/ScanSearchHit;", "onClose", "(Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function0;)V", "app_release"})
public final class ScanScreenKt {
    
    /**
     * 01 — Scan. Live camera viewfinder with barcode framing — iOS `ScanScreen` twin.
     * When an ISBN barcode is read, a confirmation card slides up. Camera permission
     * is requested on appear; denied / unavailable states show a fallback card.
     */
    @kotlin.OptIn(markerClass = {com.google.accompanist.permissions.ExperimentalPermissionsApi.class})
    @androidx.compose.runtime.Composable()
    public static final void ScanScreen(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super kotlin.coroutines.Continuation<? super java.lang.String>, ? extends java.lang.Object> lookupTitle, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super kotlin.coroutines.Continuation<? super java.util.List<in.paperboxd.app.ui.screens.scan.ScanSearchHit>>, ? extends java.lang.Object> search, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function2<? super java.lang.String, ? super java.lang.String, kotlin.Unit> onScanIt, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClose) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CircleIcon(androidx.compose.ui.graphics.vector.ImageVector icon, kotlin.jvm.functions.Function0<kotlin.Unit> onClick, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void BracketsLayer(androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void IsbnCard(java.lang.String isbn, java.lang.String bookTitle, boolean titleLoading, kotlin.jvm.functions.Function0<kotlin.Unit> onScanIt) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void FallbackCard(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String title, java.lang.String body, boolean showSettings, kotlin.jvm.functions.Function0<kotlin.Unit> onSettings) {
    }
}