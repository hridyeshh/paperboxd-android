package in.paperboxd.app.ui.screens.jazy;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u0000N\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\u001an\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\t2\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\u000eH\u0003\u001a@\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00030\u00152\f\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00010\u000e2\u0012\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\fH\u0007\u001a2\u0010\u0018\u001a\u00020\u00012\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001dH\u0003\u00f8\u0001\u0000\u00a2\u0006\u0004\b\u001f\u0010 \u0082\u0002\u0007\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006!"}, d2 = {"JazyMatchCard", "", "match", "Lin/paperboxd/app/domain/model/VibeMatch;", "depth", "", "leaving", "", "leavingDirection", "", "dragX", "onDrag", "Lkotlin/Function1;", "onDragEnd", "Lkotlin/Function0;", "onNext", "onOpen", "JazyResultsDeck", "query", "", "matches", "", "onClose", "onOpenBook", "ReasonLine", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "text", "tint", "Landroidx/compose/ui/graphics/Color;", "ink", "ReasonLine-0YGnOg8", "(Landroidx/compose/ui/graphics/vector/ImageVector;Ljava/lang/String;JJ)V", "app_debug"})
public final class JazyResultsDeckKt {
    
    /**
     * Jazy's vibe results — one match card at a time, skip or open.
     * The top card flicks off to the left; the two behind it sit stacked and scaled.
     * iOS twin: `JazyResultsDeck`.
     */
    @androidx.compose.runtime.Composable()
    public static final void JazyResultsDeck(@org.jetbrains.annotations.NotNull()
    java.lang.String query, @org.jetbrains.annotations.NotNull()
    java.util.List<in.paperboxd.app.domain.model.VibeMatch> matches, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onClose, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.String, kotlin.Unit> onOpenBook) {
    }
    
    @androidx.compose.runtime.Composable()
    private static final void JazyMatchCard(in.paperboxd.app.domain.model.VibeMatch match, int depth, boolean leaving, float leavingDirection, float dragX, kotlin.jvm.functions.Function1<? super java.lang.Float, kotlin.Unit> onDrag, kotlin.jvm.functions.Function0<kotlin.Unit> onDragEnd, kotlin.jvm.functions.Function0<kotlin.Unit> onNext, kotlin.jvm.functions.Function0<kotlin.Unit> onOpen) {
    }
}