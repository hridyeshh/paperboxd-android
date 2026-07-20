package in.paperboxd.app.data.remote;

/**
 * On any 401: clear credentials and emit the session-expired event so the app
 * routes back to auth from anywhere.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2 = {"Lin/paperboxd/app/data/remote/UnauthorizedInterceptor;", "Lokhttp3/Interceptor;", "securePrefs", "Lin/paperboxd/app/data/local/SecurePrefs;", "sessionEvents", "Lin/paperboxd/app/data/remote/SessionEvents;", "(Lin/paperboxd/app/data/local/SecurePrefs;Lin/paperboxd/app/data/remote/SessionEvents;)V", "intercept", "Lokhttp3/Response;", "chain", "Lokhttp3/Interceptor$Chain;", "app_release"})
public final class UnauthorizedInterceptor implements okhttp3.Interceptor {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.local.SecurePrefs securePrefs = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.remote.SessionEvents sessionEvents = null;
    
    @javax.inject.Inject()
    public UnauthorizedInterceptor(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.local.SecurePrefs securePrefs, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.remote.SessionEvents sessionEvents) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public okhttp3.Response intercept(@org.jetbrains.annotations.NotNull()
    okhttp3.Interceptor.Chain chain) {
        return null;
    }
}