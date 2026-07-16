package in.paperboxd.app.ui.screens.scan;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000$\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001aB\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00052\b\b\u0002\u0010\t\u001a\u00020\nH\u0007\u00a8\u0006\u000b"}, d2 = {"CameraScannerView", "", "torchOn", "", "onCode", "Lkotlin/Function1;", "", "onStatus", "Lin/paperboxd/app/ui/screens/scan/ScannerStatus;", "modifier", "Landroidx/compose/ui/Modifier;", "app_debug"})
public final class CameraScannerKt {
    
    /**
     * Live camera barcode scanner — iOS `BarcodeScannerView` twin. CameraX preview +
     * ML Kit analyzer; reports the first EAN-13 / EAN-8 / UPC-E code it reads (book
     * barcodes are EAN-13 ISBNs). Permission is requested by the host screen —
     * this view reports Denied if composed without it.
     */
    @kotlin.OptIn(markerClass = {androidx.camera.core.ExperimentalGetImage.class})
    @androidx.compose.runtime.Composable()
    public static final void CameraScannerView(boolean torchOn, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onCode, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super in.paperboxd.app.ui.screens.scan.ScannerStatus, kotlin.Unit> onStatus, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
}