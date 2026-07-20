package in.paperboxd.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0012\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u001f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015J\u000e\u0010\u0016\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0015J\u000e\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0015J\u000e\u0010\u0019\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0015J\u000e\u0010\u001a\u001a\u00020\u00132\u0006\u0010\u0017\u001a\u00020\u0015J\u000e\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u0015J\u0014\u0010\u001d\u001a\u00020\u00132\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00130\u001fJ\u0010\u0010 \u001a\u00020\u00132\b\u0010\u0017\u001a\u0004\u0018\u00010\u0015J\u000e\u0010!\u001a\u00020\u00132\u0006\u0010\"\u001a\u00020\u0015J\u0016\u0010#\u001a\u00020\u00132\u0006\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\'R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u000b0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000b0\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2 = {"Lin/paperboxd/app/ui/screens/profile/EditProfileViewModel;", "Landroidx/lifecycle/ViewModel;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "authRepository", "Lin/paperboxd/app/data/repository/AuthRepository;", "api", "Lin/paperboxd/app/data/remote/ApiService;", "(Lin/paperboxd/app/data/repository/UserRepository;Lin/paperboxd/app/data/repository/AuthRepository;Lin/paperboxd/app/data/remote/ApiService;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/profile/EditProfileState;", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "usernameJob", "Lkotlinx/coroutines/Job;", "load", "", "username", "", "onBio", "v", "onGender", "onLink", "onName", "onUsername", "raw", "save", "onDone", "Lkotlin/Function0;", "setBirthday", "togglePronoun", "opt", "uploadAvatar", "bytes", "", "preview", "Landroid/net/Uri;", "app_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class EditProfileViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.AuthRepository authRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.remote.ApiService api = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.profile.EditProfileState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.profile.EditProfileState> state = null;
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job usernameJob;
    
    @javax.inject.Inject()
    public EditProfileViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.AuthRepository authRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.remote.ApiService api) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.profile.EditProfileState> getState() {
        return null;
    }
    
    public final void load(@org.jetbrains.annotations.NotNull()
    java.lang.String username) {
    }
    
    public final void onName(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void onBio(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void onLink(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void onGender(@org.jetbrains.annotations.NotNull()
    java.lang.String v) {
    }
    
    public final void setBirthday(@org.jetbrains.annotations.Nullable()
    java.lang.String v) {
    }
    
    public final void togglePronoun(@org.jetbrains.annotations.NotNull()
    java.lang.String opt) {
    }
    
    public final void onUsername(@org.jetbrains.annotations.NotNull()
    java.lang.String raw) {
    }
    
    public final void uploadAvatar(@org.jetbrains.annotations.NotNull()
    byte[] bytes, @org.jetbrains.annotations.NotNull()
    android.net.Uri preview) {
    }
    
    /**
     * PUT /users/{originalUsername}; invokes [onDone] on success. Mirrors iOS save().
     */
    public final void save(@org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function0<kotlin.Unit> onDone) {
    }
}