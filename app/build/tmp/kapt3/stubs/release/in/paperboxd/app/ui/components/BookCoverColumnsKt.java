package in.paperboxd.app.ui.components;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000<\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\u001a\u0012\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u001a\u0010\u0010\u000f\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0004H\u0003\u001a\u0015\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002\u00a2\u0006\u0002\u0010\u0015\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\"\u0010\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0007\"\u0010\u0010\b\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\u0007\"\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2 = {"COVER_RATIO", "", "colSpecs", "", "Lin/paperboxd/app/ui/components/ColSpec;", "coverGap", "Landroidx/compose/ui/unit/Dp;", "F", "coverWidth", "pbCovers", "Landroidx/compose/ui/graphics/Brush;", "BookCoverColumns", "", "modifier", "Landroidx/compose/ui/Modifier;", "CoverColumn", "spec", "hex", "Landroidx/compose/ui/graphics/Color;", "h", "", "(Ljava/lang/String;)J", "app_release"})
public final class BookCoverColumnsKt {
    
    /**
     * Animated tiled book-cover wall. Mirrors the iOS `BookCoverColumns`: five
     * columns of gradient "covers" scroll vertically (alternating up/down at
     * different speeds), the whole grid rotated -8° and over-scaled so it bleeds
     * off every edge. Sits behind [DarkWash]. Purely decorative.
     */
    private static final float coverWidth = 0.0F;
    private static final float coverGap = 0.0F;
    private static final float COVER_RATIO = 1.5F;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<androidx.compose.ui.graphics.Brush> pbCovers = null;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<in.paperboxd.app.ui.components.ColSpec> colSpecs = null;
    
    @androidx.compose.runtime.Composable()
    public static final void BookCoverColumns(@org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void CoverColumn(in.paperboxd.app.ui.components.ColSpec spec) {
    }
    
    private static final long hex(java.lang.String h) {
        return 0L;
    }
}