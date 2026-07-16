package in.paperboxd.app.ui.screens.scan;

/**
 * Live free-scan count — iOS `@AppStorage("pb_scans_remaining")` twin.
 * Persisted by the scan flow after each analyze response; read by the
 * Reveal/Breakdown footers and Settings.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002J\u000e\u0010\b\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u0004J\u000e\u0010\n\u001a\u00020\u00042\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u000f\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0010"}, d2 = {"Lin/paperboxd/app/ui/screens/scan/ScanPrefs;", "", "()V", "DEFAULT", "", "FILE", "", "KEY", "footerText", "remaining", "scansRemaining", "context", "Landroid/content/Context;", "setScansRemaining", "", "value", "app_debug"})
public final class ScanPrefs {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String FILE = "pb_scan";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY = "pb_scans_remaining";
    private static final int DEFAULT = 7;
    @org.jetbrains.annotations.NotNull()
    public static final in.paperboxd.app.ui.screens.scan.ScanPrefs INSTANCE = null;
    
    private ScanPrefs() {
        super();
    }
    
    public final int scansRemaining(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return 0;
    }
    
    public final void setScansRemaining(@org.jetbrains.annotations.NotNull()
    android.content.Context context, int value) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String footerText(int remaining) {
        return null;
    }
}