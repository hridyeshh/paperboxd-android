package in.paperboxd.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00004\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\u001a6\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00040\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0007\u001a\u001e\u0010\r\u001a\u0010\u0012\f\u0012\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u00010\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\u001a\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bH\u0002\u001a\u0010\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\bH\u0002\u001a\u0016\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00012\u0006\u0010\u0005\u001a\u00020\u0006H\u0002\"\u0014\u0010\u0000\u001a\b\u0012\u0004\u0012\u00020\u00020\u0001X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Ramp", "", "Landroidx/compose/ui/graphics/Color;", "ReadingHeatmap", "", "activity", "Lin/paperboxd/app/domain/model/ReadingActivity;", "selectedYear", "", "onSelectYear", "Lkotlin/Function1;", "modifier", "Landroidx/compose/ui/Modifier;", "buildGrid", "grouped", "", "n", "level", "pages", "monthMarkers", "app_debug"})
public final class ReadingHeatmapKt {
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<androidx.compose.ui.graphics.Color> Ramp = null;
    
    private static final int level(int pages) {
        return 0;
    }
    
    private static final java.lang.String grouped(int n) {
        return null;
    }
    
    /**
     * GitHub-style reading heatmap on a brutalist plate — iOS ReadingHeatmapView twin.
     * Mono eyebrow + year tabs, big page count, month-labelled pages-per-day grid,
     * LESS→MORE legend with the streak line.
     */
    @androidx.compose.runtime.Composable()
    public static final void ReadingHeatmap(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.ReadingActivity activity, int selectedYear, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Integer, kotlin.Unit> onSelectYear, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    private static final java.util.List<java.util.List<java.lang.Integer>> buildGrid(in.paperboxd.app.domain.model.ReadingActivity activity) {
        return null;
    }
    
    private static final java.util.List<java.lang.String> monthMarkers(in.paperboxd.app.domain.model.ReadingActivity activity) {
        return null;
    }
}