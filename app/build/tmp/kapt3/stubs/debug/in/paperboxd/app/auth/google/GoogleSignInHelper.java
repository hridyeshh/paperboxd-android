package in.paperboxd.app.auth.google;

/**
 * Front half of Google Sign-In: drives the Credential Manager picker and returns
 * a Google ID token. The back half (exchanging that token for a session) already
 * lives in AuthRepository.googleAuth — this class does not touch the network or
 * token storage. Mirrors iOS GoogleOAuth: cancellation is a silent no-op, only a
 * genuine failure surfaces an error.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0007\b\u0007\u00a2\u0006\u0002\u0010\u0002J\u0016\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0086@\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\b"}, d2 = {"Lin/paperboxd/app/auth/google/GoogleSignInHelper;", "", "()V", "getIdToken", "Lin/paperboxd/app/auth/google/GoogleSignInResult;", "activityContext", "Landroid/content/Context;", "(Landroid/content/Context;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class GoogleSignInHelper {
    
    @javax.inject.Inject()
    public GoogleSignInHelper() {
        super();
    }
    
    /**
     * Launches the Google account picker and returns the resulting ID token.
     * [activityContext] must be an Activity context so the picker UI can show.
     */
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getIdToken(@org.jetbrains.annotations.NotNull()
    android.content.Context activityContext, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super in.paperboxd.app.auth.google.GoogleSignInResult> $completion) {
        return null;
    }
}