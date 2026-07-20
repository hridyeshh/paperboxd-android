package in.paperboxd.app.ui.screens.jazy;

@kotlin.Metadata(mv = {1, 9, 0}, k = 2, xi = 48, d1 = {"\u00006\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a<\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u001a@\u0010\u000b\u001a\u00020\u00012\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\u0012\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\r\u0012\u0004\u0012\u00020\u00010\u0012H\u0007\u00a8\u0006\u0013"}, d2 = {"JazyMatchCard", "", "match", "Lin/paperboxd/app/domain/model/VibeMatch;", "depth", "", "leaving", "", "onSkip", "Lkotlin/Function0;", "onOpen", "JazyResultsDeck", "query", "", "matches", "", "onClose", "onOpenBook", "Lkotlin/Function1;", "app_debug"})
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
    private static final void JazyMatchCard(in.paperboxd.app.domain.model.VibeMatch match, int depth, boolean leaving, kotlin.jvm.functions.Function0<kotlin.Unit> onSkip, kotlin.jvm.functions.Function0<kotlin.Unit> onOpen) {
    }
}