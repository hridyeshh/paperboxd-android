package in.paperboxd.app.ui.screens.onboarding;

/**
 * Multi-step onboarding — iOS `OnboardingContainerView` twin: light editorial
 * theme (dark only on the aha-loading step), a staged pill header, serif titles,
 * a dashed avatar ring, availability-coloured username field, genre chips with a
 * live counter, tempo rows, a pulsing dark loading screen, and the first-match
 * reveal. Steps: username → genres → tempo → ahaLoading → ahaReveal.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b&\b\u00c2\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u0011\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0006R\u0019\u0010\u0007\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\t\u0010\nR\u0019\u0010\f\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\r\u0010\nR\u0019\u0010\u000e\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u000f\u0010\nR\u0019\u0010\u0010\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u0011\u0010\nR\u0019\u0010\u0012\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u0013\u0010\nR\u0019\u0010\u0014\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u0015\u0010\nR\u0019\u0010\u0016\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u0017\u0010\nR\u0019\u0010\u0018\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u0019\u0010\nR\u0019\u0010\u001a\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u001b\u0010\nR\u0019\u0010\u001c\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u001d\u0010\nR\u0019\u0010\u001e\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\u001f\u0010\nR\u0019\u0010 \u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b!\u0010\nR\u0019\u0010\"\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b#\u0010\nR\u0019\u0010$\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b%\u0010\nR\u0019\u0010&\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b\'\u0010\nR\u0019\u0010(\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b)\u0010\nR\u0019\u0010*\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b+\u0010\nR\u0019\u0010,\u001a\u00020\b\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\n\n\u0002\u0010\u000b\u001a\u0004\b-\u0010\n\u0082\u0002\u000b\n\u0005\b\u00a1\u001e0\u0001\n\u0002\b!\u00a8\u0006."}, d2 = {"Lin/paperboxd/app/ui/screens/onboarding/OB;", "", "()V", "avatarGradient", "Landroidx/compose/ui/graphics/Brush;", "getAvatarGradient", "()Landroidx/compose/ui/graphics/Brush;", "border88", "Landroidx/compose/ui/graphics/Color;", "getBorder88-0d7_KjU", "()J", "J", "border90", "getBorder90-0d7_KjU", "darkBg", "getDarkBg-0d7_KjU", "fill92", "getFill92-0d7_KjU", "fill96", "getFill96-0d7_KjU", "fill97", "getFill97-0d7_KjU", "green", "getGreen-0d7_KjU", "ink", "getInk-0d7_KjU", "line85", "getLine85-0d7_KjU", "red", "getRed-0d7_KjU", "ring", "getRing-0d7_KjU", "sub25", "getSub25-0d7_KjU", "sub40", "getSub40-0d7_KjU", "sub45", "getSub45-0d7_KjU", "sub50", "getSub50-0d7_KjU", "sub53", "getSub53-0d7_KjU", "sub60", "getSub60-0d7_KjU", "sub65", "getSub65-0d7_KjU", "app_debug"})
final class OB {
    private static final long ink = 0L;
    private static final long sub53 = 0L;
    private static final long sub60 = 0L;
    private static final long sub65 = 0L;
    private static final long sub50 = 0L;
    private static final long sub45 = 0L;
    private static final long sub40 = 0L;
    private static final long sub25 = 0L;
    private static final long border88 = 0L;
    private static final long border90 = 0L;
    private static final long line85 = 0L;
    private static final long fill92 = 0L;
    private static final long fill96 = 0L;
    private static final long fill97 = 0L;
    private static final long ring = 0L;
    private static final long green = 0L;
    private static final long red = 0L;
    private static final long darkBg = 0L;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.compose.ui.graphics.Brush avatarGradient = null;
    @org.jetbrains.annotations.NotNull()
    public static final in.paperboxd.app.ui.screens.onboarding.OB INSTANCE = null;
    
    private OB() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.ui.graphics.Brush getAvatarGradient() {
        return null;
    }
}