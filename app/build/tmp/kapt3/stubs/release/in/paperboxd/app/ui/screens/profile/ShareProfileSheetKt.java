package in.paperboxd.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00008\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\u001a0\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000b2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00050\rH\u0003\u001a\"\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\t2\u0006\u0010\u0010\u001a\u00020\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0003\u001a\u001e\u0010\u0011\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\t2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\rH\u0007\u001a\u001c\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\t2\b\b\u0002\u0010\u0016\u001a\u00020\u0017H\u0002\"\u0010\u0010\u0000\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\"\u0010\u0010\u0003\u001a\u00020\u0001X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0002\u00a8\u0006\u0018"}, d2 = {"Ink", "Landroidx/compose/ui/graphics/Color;", "J", "QrBg", "ActionTile", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "label", "", "modifier", "Landroidx/compose/ui/Modifier;", "onClick", "Lkotlin/Function0;", "QrCard", "username", "profileUrl", "ShareProfileSheet", "onDismiss", "qrBitmap", "Landroid/graphics/Bitmap;", "content", "size", "", "app_release"})
public final class ShareProfileSheetKt {
    private static final long Ink = 0L;
    private static final long QrBg = 0L;
    
    /**
     * Brutalist "Share Profile" sheet — hard-edged offset-shadow QR card with the
     * PaperBoxd mark punched into the centre, plus Share / Copy tiles. Mirrors
     * "Share Profile - Brutalist Mobile.html" + iOS ShareProfileSheet.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void ShareProfileSheet(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void QrCard(java.lang.String username, java.lang.String profileUrl, androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void ActionTile(androidx.compose.ui.graphics.vector.ImageVector icon, java.lang.String label, androidx.compose.ui.Modifier modifier, kotlin.jvm.functions.Function0<kotlin.Unit> onClick) {
    }
    
    /**
     * Ink-on-paper QR bitmap via zxing, error correction H (matches iOS).
     */
    private static final android.graphics.Bitmap qrBitmap(java.lang.String content, int size) {
        return null;
    }
}