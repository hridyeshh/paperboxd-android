package in.paperboxd.app.ui.screens.profile;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u001aJ\u0016\u0010\u001b\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\b2\u0006\u0010\u0019\u001a\u00020\u001aR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R7\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\f\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r8F@BX\u0086\u008e\u0002\u00a2\u0006\u0012\n\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001c"}, d2 = {"Lin/paperboxd/app/ui/screens/profile/FollowListViewModel;", "Landroidx/lifecycle/ViewModel;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "(Lin/paperboxd/app/data/repository/UserRepository;)V", "hasMore", "", "key", "", "loading", "page", "", "<set-?>", "", "Lin/paperboxd/app/domain/model/UserProfile;", "users", "getUsers", "()Ljava/util/List;", "setUsers", "(Ljava/util/List;)V", "users$delegate", "Landroidx/compose/runtime/MutableState;", "load", "", "username", "mode", "Lin/paperboxd/app/ui/screens/profile/FollowListMode;", "loadMore", "app_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class FollowListViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.runtime.MutableState users$delegate = null;
    private int page = 1;
    private boolean hasMore = true;
    private boolean loading = false;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String key;
    
    @javax.inject.Inject()
    public FollowListViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<in.paperboxd.app.domain.model.UserProfile> getUsers() {
        return null;
    }
    
    private final void setUsers(java.util.List<in.paperboxd.app.domain.model.UserProfile> p0) {
    }
    
    public final void load(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.profile.FollowListMode mode) {
    }
    
    public final void loadMore(@org.jetbrains.annotations.NotNull()
    java.lang.String username, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.ui.screens.profile.FollowListMode mode) {
    }
}