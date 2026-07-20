package in.paperboxd.app.ui.screens.home;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B)\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u001c\u001a\u00020\u001d2\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001fH\u0002J\b\u0010!\u001a\u00020\"H\u0002J\u0010\u0010#\u001a\u00020$2\b\b\u0002\u0010%\u001a\u00020\u001dJ\u0006\u0010&\u001a\u00020$J\u0012\u0010\'\u001a\u0004\u0018\u00010(2\u0006\u0010)\u001a\u00020\"H\u0002J\u000e\u0010*\u001a\u00020$2\u0006\u0010+\u001a\u00020\"R\u0014\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n \u0010*\u0004\u0018\u00010\u000f0\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\r0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R(\u0010\u0017\u001a\u0004\u0018\u00010\u00162\b\u0010\u0015\u001a\u0004\u0018\u00010\u0016@FX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001bR\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2 = {"Lin/paperboxd/app/ui/screens/home/HomeViewModel;", "Landroidx/lifecycle/ViewModel;", "context", "Landroid/content/Context;", "recommendationRepository", "Lin/paperboxd/app/data/repository/RecommendationRepository;", "bookRepository", "Lin/paperboxd/app/data/repository/BookRepository;", "userRepository", "Lin/paperboxd/app/data/repository/UserRepository;", "(Landroid/content/Context;Lin/paperboxd/app/data/repository/RecommendationRepository;Lin/paperboxd/app/data/repository/BookRepository;Lin/paperboxd/app/data/repository/UserRepository;)V", "_state", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lin/paperboxd/app/ui/screens/home/HomeUiState;", "prefs", "Landroid/content/SharedPreferences;", "kotlin.jvm.PlatformType", "state", "Lkotlinx/coroutines/flow/StateFlow;", "getState", "()Lkotlinx/coroutines/flow/StateFlow;", "value", "Lin/paperboxd/app/domain/model/User;", "user", "getUser", "()Lin/paperboxd/app/domain/model/User;", "setUser", "(Lin/paperboxd/app/domain/model/User;)V", "computeUnread", "", "activities", "", "Lin/paperboxd/app/domain/model/ActivityItem;", "lastViewedKey", "", "load", "", "refreshing", "markActivitiesViewed", "parseInstant", "Ljava/time/Instant;", "iso", "trackImpression", "bookId", "app_release"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class HomeViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.RecommendationRepository recommendationRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.BookRepository bookRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.repository.UserRepository userRepository = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<in.paperboxd.app.ui.screens.home.HomeUiState> _state = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.home.HomeUiState> state = null;
    private final android.content.SharedPreferences prefs = null;
    @org.jetbrains.annotations.Nullable()
    private in.paperboxd.app.domain.model.User user;
    
    @javax.inject.Inject()
    public HomeViewModel(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.RecommendationRepository recommendationRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.BookRepository bookRepository, @org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.repository.UserRepository userRepository) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<in.paperboxd.app.ui.screens.home.HomeUiState> getState() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final in.paperboxd.app.domain.model.User getUser() {
        return null;
    }
    
    public final void setUser(@org.jetbrains.annotations.Nullable()
    in.paperboxd.app.domain.model.User value) {
    }
    
    private final java.lang.String lastViewedKey() {
        return null;
    }
    
    public final void load(boolean refreshing) {
    }
    
    private final boolean computeUnread(java.util.List<in.paperboxd.app.domain.model.ActivityItem> activities) {
        return false;
    }
    
    public final void markActivitiesViewed() {
    }
    
    public final void trackImpression(@org.jetbrains.annotations.NotNull()
    java.lang.String bookId) {
    }
    
    private final java.time.Instant parseInstant(java.lang.String iso) {
        return null;
    }
}