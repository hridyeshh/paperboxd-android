package in.paperboxd.app.ui.navigation;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000N\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\u001a \u0010\b\u001a\u00020\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\t0\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0007\u001a\u0018\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u0002\u001a.\u0010\u0012\u001a\u00020\t*\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0015H\u0002\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u0018\u0010\u0019\u001a,\u0010\u001a\u001a\u00020\t*\u00020\u001b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u00012\u0006\u0010\u001f\u001a\u00020\u001dH\u0002\u001a&\u0010 \u001a\u00020\t*\u00020\u00132\u0006\u0010!\u001a\u00020\u00152\u0006\u0010\u0017\u001a\u00020\u0015H\u0002\u00f8\u0001\u0000\u00a2\u0006\u0004\b\"\u0010#\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0003X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0082T\u00a2\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006$"}, d2 = {"AMP", "", "BLINK_EVERY", "", "BOIL_MS", "", "SWAY_DEG", "WOBBLE", "PipScanButton", "", "onClick", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "jitter", "seed", "", "salt", "cubic", "Landroidx/compose/ui/graphics/Path;", "c1", "Landroidx/compose/ui/geometry/Offset;", "c2", "end", "cubic-CDGqFxY", "(Landroidx/compose/ui/graphics/Path;JJJ)V", "drawPip", "Landroidx/compose/ui/graphics/drawscope/DrawScope;", "blink", "", "gaze", "excited", "quad", "c", "quad-2x9bVx0", "(Landroidx/compose/ui/graphics/Path;JJ)V", "app_debug"})
public final class PipScanButtonKt {
    
    /**
     * Pip — the scan buddy. iOS PipScanButton + web design-system twin.
     *
     * Three independent motion systems layer on top of each other, exactly like
     * the web reference:
     * 1. Line-boil — every path point is re-jittered off a seeded RNG that
     *    re-rolls every [BOIL_MS] (~6.7fps at speed 1). This deliberately low,
     *    stepped rate is what makes the ink read as hand-drawn instead of glassy.
     *    Driven off withFrameMillis (not a coalescible timer). amp = 0.55*wobble.
     * 2. Sway — whole-body rotate ±SWAY_DEG over 3.4s, origin at 50%/78%.
     * 3. Pose machine — idle blink/glance loop + tap startle.
     */
    private static final long BOIL_MS = 150L;
    private static final float WOBBLE = 0.5F;
    private static final float SWAY_DEG = 0.9F;
    private static final double BLINK_EVERY = 3.4;
    private static final float AMP = 0.275F;
    
    @androidx.compose.runtime.Composable()
    public static final void PipScanButton(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClick, @org.jetbrains.annotations.NotNull()
    androidx.compose.ui.Modifier modifier) {
    }
    
    /**
     * Deterministic per-(frame,point) noise in -1..1 — cheap hash, no state.
     */
    private static final float jitter(int seed, int salt) {
        return 0.0F;
    }
    
    private static final void drawPip(androidx.compose.ui.graphics.drawscope.DrawScope $this$drawPip, int seed, boolean blink, float gaze, boolean excited) {
    }
}