package in.paperboxd.app.config;

/**
 * Centralised configuration. Mirrors iOS Config.swift: debug + release both hit
 * Railway prod. Flip [BASE_URL] to a local address for backend development.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\b\u00c7\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u00020\u0004X\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2 = {"Lin/paperboxd/app/config/Config;", "", "()V", "BASE_URL", "", "GOOGLE_WEB_CLIENT_ID", "SECURE_PREFS_NAME", "userAgent", "getUserAgent", "()Ljava/lang/String;", "app_release"})
public final class Config {
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String BASE_URL = "https://paperboxd-backend-production-d9e0.up.railway.app";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String userAgent = "PaperBoxd-Android/1.0 (Android)";
    
    /**
     * EncryptedSharedPreferences file name. Stable across builds.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String SECURE_PREFS_NAME = "in.paperboxd.app.secure";
    
    /**
     * Web-type OAuth client ID passed to Credential Manager as the serverClientId
     * for Google Sign-In. Sourced from local.properties via BuildConfig; must be
     * present in the backend's GOOGLE_OAUTH_ALLOWED_AUDIENCES allowlist.
     */
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String GOOGLE_WEB_CLIENT_ID = "893085484645-alusdduovvu1k8kcm16466t72sha1f8b.apps.googleusercontent.com";
    @org.jetbrains.annotations.NotNull()
    public static final in.paperboxd.app.config.Config INSTANCE = null;
    
    private Config() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getUserAgent() {
        return null;
    }
}