package in.paperboxd.app.data.repository;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u0084\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u0017\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u0004\u0018\u00010\bJ\b\u0010\t\u001a\u0004\u0018\u00010\nJ$\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\f2\u0006\u0010\u000e\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000f\u0010\u0010J\u0006\u0010\u0011\u001a\u00020\u0012J\u001c\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00120\fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0014\u0010\u0015J$\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00120\f2\u0006\u0010\u0017\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u0018\u0010\u0010J$\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\f2\u0006\u0010\u001b\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001c\u0010\u0010J\u001c\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u001f\u0010\u0015J,\u0010 \u001a\b\u0012\u0004\u0012\u00020!0\f2\u0006\u0010\u0017\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b#\u0010$J\u0016\u0010%\u001a\u00020\u00122\u0006\u0010&\u001a\u00020\b2\u0006\u0010\'\u001a\u00020\nJ\u000e\u0010(\u001a\u00020\u00122\u0006\u0010&\u001a\u00020\bJ\u000e\u0010)\u001a\u00020\u00122\u0006\u0010\'\u001a\u00020\nJ\u001c\u0010*\u001a\b\u0012\u0004\u0012\u00020+0\fH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b,\u0010\u0015J,\u0010-\u001a\b\u0012\u0004\u0012\u00020!0\f2\u0006\u0010\u0017\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b.\u0010$J*\u0010/\u001a\b\u0012\u0004\u0012\u00020\u00120\f2\f\u00100\u001a\b\u0012\u0004\u0012\u00020\b01H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b2\u00103J$\u00104\u001a\b\u0012\u0004\u0012\u0002050\f2\u0006\u0010\u0017\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b6\u0010\u0010J0\u00107\u001a\b\u0012\u0004\u0012\u0002080\f2\u0012\u00109\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0:H\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b;\u0010<J.\u0010=\u001a\b\u0012\u0004\u0012\u00020>0\f2\u0006\u0010?\u001a\u00020@2\b\b\u0002\u0010A\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bB\u0010CJ,\u0010D\u001a\b\u0012\u0004\u0012\u00020!0\f2\u0006\u0010\u0017\u001a\u00020\b2\u0006\u0010E\u001a\u00020\bH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\bF\u0010$R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006G"}, d2 = {"Lin/paperboxd/app/data/repository/AuthRepository;", "", "api", "Lin/paperboxd/app/data/remote/ApiService;", "securePrefs", "Lin/paperboxd/app/data/local/SecurePrefs;", "(Lin/paperboxd/app/data/remote/ApiService;Lin/paperboxd/app/data/local/SecurePrefs;)V", "cachedToken", "", "cachedUser", "Lin/paperboxd/app/domain/model/User;", "checkUsername", "Lkotlin/Result;", "Lin/paperboxd/app/domain/model/CheckUsernameResponse;", "username", "checkUsername-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearSession", "", "deleteAccount", "deleteAccount-IoAF18A", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "forgotPassword", "email", "forgotPassword-gIAlu-s", "googleAuth", "Lin/paperboxd/app/domain/model/GoogleAuthResponse;", "idToken", "googleAuth-gIAlu-s", "health", "Lin/paperboxd/app/domain/model/HealthResponse;", "health-IoAF18A", "login", "Lin/paperboxd/app/domain/model/AuthResponse;", "password", "login-0E7RQCE", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "persistSession", "token", "user", "persistToken", "persistUser", "refresh", "Lin/paperboxd/app/domain/model/RefreshResponse;", "refresh-IoAF18A", "register", "register-0E7RQCE", "saveOnboarding", "genres", "", "saveOnboarding-gIAlu-s", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendOtp", "Lin/paperboxd/app/domain/model/OtpSendResponse;", "sendOtp-gIAlu-s", "updateMobileMe", "Lin/paperboxd/app/domain/model/MobileUserResponse;", "fields", "", "updateMobileMe-gIAlu-s", "(Ljava/util/Map;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "uploadAvatar", "Lin/paperboxd/app/domain/model/AvatarUploadResponse;", "bytes", "", "fileName", "uploadAvatar-0E7RQCE", "([BLjava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "verifyOtp", "code", "verifyOtp-0E7RQCE", "app_debug"})
public final class AuthRepository {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.remote.ApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.local.SecurePrefs securePrefs = null;
    
    @javax.inject.Inject()
    public AuthRepository(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.remote.ApiService api, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.local.SecurePrefs securePrefs) {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String cachedToken() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.User cachedUser() {
        return null;
    }
    
    public final void persistSession(@org.jetbrains.annotations.NotNull()
    java.lang.String token, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user) {
    }
    
    public final void persistToken(@org.jetbrains.annotations.NotNull()
    java.lang.String token) {
    }
    
    public final void persistUser(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.domain.model.User user) {
    }
    
    public final void clearSession() {
    }
}