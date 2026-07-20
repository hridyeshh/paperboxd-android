package in.paperboxd.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0003\u001a*\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0003\u00f8\u0001\u0000\u00a2\u0006\u0004\b\b\u0010\t\u001a\u001e\u0010\n\u001a\u00020\u00012\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eH\u0007\u001a\u0010\u0010\u000f\u001a\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u0003H\u0003\u001a\u0010\u0010\u0011\u001a\u00020\u00032\u0006\u0010\u0012\u001a\u00020\u0003H\u0002\u001a\u0015\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0016H\u0002\u00a2\u0006\u0002\u0010\u0017\u001a\u0010\u0010\u0018\u001a\u00020\u00032\u0006\u0010\u0010\u001a\u00020\u0003H\u0002\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0019"}, d2 = {"Header", "", "text", "", "size", "Landroidx/compose/ui/unit/TextUnit;", "top", "", "Header-Rk4xWKU", "(Ljava/lang/String;JI)V", "LegalBottomSheet", "doc", "Lin/paperboxd/app/ui/components/LegalDoc;", "onDismiss", "Lkotlin/Function0;", "LegalLine", "raw", "clean", "s", "lw", "Landroidx/compose/ui/graphics/Color;", "a", "", "(F)J", "sanitizeLegal", "app_debug"})
public final class LegalBottomSheetKt {
    
    private static final long lw(float a) {
        return 0L;
    }
    
    /**
     * Full legal document in a modal bottom sheet at signup. Content is bundled in
     * assets (works offline). Tables render as plain monospaced rows — the
     * bundled-native path has no table layout engine.
     */
    @kotlin.OptIn(markerClass = {androidx.compose.material3.ExperimentalMaterial3Api.class})
    @androidx.compose.runtime.Composable()
    public static final void LegalBottomSheet(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.components.LegalDoc doc, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDismiss) {
    }
    
    /**
     * Drop the reviewer preamble (before the first `---`) and the internal
     * "Outstanding placeholders" checklist (from that heading on).
     */
    private static final java.lang.String sanitizeLegal(java.lang.String raw) {
        return null;
    }
    
    @androidx.compose.runtime.Composable()
    private static final void LegalLine(java.lang.String raw) {
    }
    
    /**
     * Strip inline markdown the line renderer doesn't handle: `**bold**` and `[label](url)` → `label`.
     */
    private static final java.lang.String clean(java.lang.String s) {
        return null;
    }
}