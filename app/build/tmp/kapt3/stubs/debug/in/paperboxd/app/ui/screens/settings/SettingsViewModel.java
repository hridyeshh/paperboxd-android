package in.paperboxd.app.ui.screens.settings;

/**
 * Backing calls for the settings screen — mirrors iOS SettingsView's two live
 * endpoints: password reset (email link, since there is no in-app change) and
 * account deletion. Sign-out itself routes through AppState, not here.
 */
@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J*\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000b\u0010\fJ$\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00070\u00062\u0006\u0010\u000e\u001a\u00020\nH\u0086@\u00f8\u0001\u0000\u00f8\u0001\u0001\u00a2\u0006\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u0082\u0002\u000b\n\u0002\b!\n\u0005\b\u00a1\u001e0\u0001\u00a8\u0006\u0011"}, d2 = {"Lin/paperboxd/app/ui/screens/settings/SettingsViewModel;", "Landroidx/lifecycle/ViewModel;", "api", "Lin/paperboxd/app/data/remote/ApiService;", "(Lin/paperboxd/app/data/remote/ApiService;)V", "deleteAccount", "Lkotlin/Result;", "", "reasons", "", "", "deleteAccount-gIAlu-s", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "sendPasswordReset", "email", "sendPasswordReset-gIAlu-s", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@dagger.hilt.android.lifecycle.HiltViewModel()
public final class SettingsViewModel extends androidx.lifecycle.ViewModel {
    @org.jetbrains.annotations.NotNull()
    private final in.paperboxd.app.data.remote.ApiService api = null;
    
    @javax.inject.Inject()
    public SettingsViewModel(@org.jetbrains.annotations.NotNull()
    in.paperboxd.app.data.remote.ApiService api) {
        super();
    }
}