package in.paperboxd.app.ui.screens.bookdetail;

/**
 * Card canvas, in iOS points mapped 1:1 to dp — mirrors iOS BookShareCardView.Format
 * (540×960 story, 540×540 square). The card is always composed at this base size and
 * scaled to fit the preview, so the raster ships at full resolution and every inset
 * below can be copied straight from the Swift.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0082\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u001f\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0007R\u0019\u0010\u0006\u001a\u00020\u0005\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0019\u0010\u0004\u001a\u00020\u0005\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\n\u001a\u0004\b\u000b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rj\u0002\b\u000ej\u0002\b\u000f\u0082\u0002\u000b\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b!\u00a8\u0006\u0010"}, d2 = {"Lin/paperboxd/app/ui/screens/bookdetail/ShareFormat;", "", "label", "", "baseW", "Landroidx/compose/ui/unit/Dp;", "baseH", "(Ljava/lang/String;ILjava/lang/String;FF)V", "getBaseH-D9Ej5fM", "()F", "F", "getBaseW-D9Ej5fM", "getLabel", "()Ljava/lang/String;", "Story", "Square", "app_debug"})
enum ShareFormat {
    /*public static final*/ Story /* = new Story(null, 0.0F, 0.0F) */,
    /*public static final*/ Square /* = new Square(null, 0.0F, 0.0F) */;
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String label = null;
    private final float baseW = 0.0F;
    private final float baseH = 0.0F;
    
    ShareFormat(java.lang.String label, float baseW, float baseH) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getLabel() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public static kotlin.enums.EnumEntries<in.paperboxd.app.ui.screens.bookdetail.ShareFormat> getEntries() {
        return null;
    }
}