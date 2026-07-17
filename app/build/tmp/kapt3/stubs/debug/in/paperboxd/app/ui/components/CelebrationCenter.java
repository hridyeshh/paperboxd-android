package in.paperboxd.app.ui.components;

/**
 * Shared celebration bus + increment detection. Android twin of the iOS
 * CelebrationCenter singleton. Injected into the view models that emit
 * celebrations; observed by [CelebrationOverlayHost] via [CelebrationViewModel].
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\b\u0007\u0018\u0000 \u00182\u00020\u0001:\u0001\u0018B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0015\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012\u00a2\u0006\u0002\u0010\u0013J\u000e\u0010\u0014\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0015\u001a\u00020\u0010J\u000e\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u0007R\u0016\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\b\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00070\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0016\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0019"}, d2 = {"Lin/paperboxd/app/ui/components/CelebrationCenter;", "", "context", "Landroid/content/Context;", "(Landroid/content/Context;)V", "_current", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/components/Celebration;", "current", "Lkotlinx/coroutines/flow/StateFlow;", "getCurrent", "()Lkotlinx/coroutines/flow/StateFlow;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "checkLevel", "", "new", "", "(Ljava/lang/Integer;)V", "checkStreak", "dismiss", "show", "celebration", "Companion", "app_debug"})
public final class CelebrationCenter {
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.components.Celebration> _current = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.components.Celebration> current = null;
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_STREAK_DAY = "celebrated_streak_day";
    @org.jetbrains.annotations.NotNull()
    @java.lang.Deprecated()
    public static final java.lang.String KEY_LEVEL = "celebrated_level";
    @org.jetbrains.annotations.NotNull()
    private static final in.paperboxd.app.ui.components.CelebrationCenter.Companion Companion = null;
    
    @javax.inject.Inject()
    public CelebrationCenter(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.components.Celebration> getCurrent() {
        return null;
    }
    
    public final void show(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.components.Celebration celebration) {
    }
    
    public final void dismiss() {
    }
    
    /**
     * Celebrate the first successful page-log of each UTC day — that's the moment
     * a reading day (and thus the streak) is earned. Keyed on the UTC day so repeat
     * logs the same day don't re-fire, but a fresh day / first-ever log / streak
     * reset all celebrate (a numeric-increase check missed those).
     */
    public final void checkStreak(int p0_54480) {
    }
    
    /**
     * Celebrate when the user's level rises past the last one seen.
     */
    public final void checkLevel(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0_54480) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0082\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2 = {"Lin/paperboxd/app/ui/components/CelebrationCenter$Companion;", "", "()V", "KEY_LEVEL", "", "KEY_STREAK_DAY", "app_debug"})
    static final class Companion {
        
        private Companion() {
            super();
        }
    }
}