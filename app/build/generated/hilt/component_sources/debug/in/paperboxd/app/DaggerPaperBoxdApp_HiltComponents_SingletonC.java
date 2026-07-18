package in.paperboxd.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SetBuilder;
import in.paperboxd.app.auth.google.GoogleSignInHelper;
import in.paperboxd.app.data.local.SecurePrefs;
import in.paperboxd.app.data.remote.ApiService;
import in.paperboxd.app.data.remote.AuthInterceptor;
import in.paperboxd.app.data.remote.NetworkModule_ProvideApiServiceFactory;
import in.paperboxd.app.data.remote.NetworkModule_ProvideOkHttpClientFactory;
import in.paperboxd.app.data.remote.NetworkModule_ProvideRetrofitFactory;
import in.paperboxd.app.data.remote.SessionEvents;
import in.paperboxd.app.data.remote.UnauthorizedInterceptor;
import in.paperboxd.app.data.repository.AuthRepository;
import in.paperboxd.app.data.repository.BookRepository;
import in.paperboxd.app.data.repository.DiaryRepository;
import in.paperboxd.app.data.repository.RecommendationRepository;
import in.paperboxd.app.data.repository.UserRepository;
import in.paperboxd.app.ui.components.CelebrationCenter;
import in.paperboxd.app.ui.components.CelebrationViewModel;
import in.paperboxd.app.ui.components.CelebrationViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.auth.AuthViewModel;
import in.paperboxd.app.ui.screens.auth.AuthViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel;
import in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.diary.DiaryEntryDetailViewModel;
import in.paperboxd.app.ui.screens.diary.DiaryEntryDetailViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.home.HomeViewModel;
import in.paperboxd.app.ui.screens.home.HomeViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.leaderboard.LeaderboardViewModel;
import in.paperboxd.app.ui.screens.leaderboard.LeaderboardViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel;
import in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.profile.EditProfileViewModel;
import in.paperboxd.app.ui.screens.profile.EditProfileViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.profile.FollowListViewModel;
import in.paperboxd.app.ui.screens.profile.FollowListViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.profile.ProfileViewModel;
import in.paperboxd.app.ui.screens.profile.ProfileViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.scan.ScanFlowViewModel;
import in.paperboxd.app.ui.screens.scan.ScanFlowViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.search.SearchViewModel;
import in.paperboxd.app.ui.screens.search.SearchViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel;
import in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.settings.SettingsViewModel;
import in.paperboxd.app.ui.screens.settings.SettingsViewModel_HiltModules_KeyModule_ProvideFactory;
import in.paperboxd.app.ui.screens.write.WriteViewModel;
import in.paperboxd.app.ui.screens.write.WriteViewModel_HiltModules_KeyModule_ProvideFactory;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;

@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class DaggerPaperBoxdApp_HiltComponents_SingletonC {
  private DaggerPaperBoxdApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public PaperBoxdApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements PaperBoxdApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public PaperBoxdApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements PaperBoxdApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public PaperBoxdApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements PaperBoxdApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public PaperBoxdApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements PaperBoxdApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PaperBoxdApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements PaperBoxdApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public PaperBoxdApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements PaperBoxdApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public PaperBoxdApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements PaperBoxdApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public PaperBoxdApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends PaperBoxdApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends PaperBoxdApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends PaperBoxdApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends PaperBoxdApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Set<String> getViewModelKeys() {
      return SetBuilder.<String>newSetBuilder(15).add(AuthViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(BookDetailViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(CelebrationViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(DiaryEntryDetailViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(EditProfileViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(FollowListViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(GoodreadsImportViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(HomeViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(LeaderboardViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(OnboardingViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ProfileViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(ScanFlowViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SearchViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(SettingsViewModel_HiltModules_KeyModule_ProvideFactory.provide()).add(WriteViewModel_HiltModules_KeyModule_ProvideFactory.provide()).build();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public void injectMainActivity(MainActivity arg0) {
      injectMainActivity2(arg0);
    }

    private MainActivity injectMainActivity2(MainActivity instance) {
      MainActivity_MembersInjector.injectAppState(instance, singletonCImpl.appStateProvider.get());
      return instance;
    }
  }

  private static final class ViewModelCImpl extends PaperBoxdApp_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<AuthViewModel> authViewModelProvider;

    private Provider<BookDetailViewModel> bookDetailViewModelProvider;

    private Provider<CelebrationViewModel> celebrationViewModelProvider;

    private Provider<DiaryEntryDetailViewModel> diaryEntryDetailViewModelProvider;

    private Provider<EditProfileViewModel> editProfileViewModelProvider;

    private Provider<FollowListViewModel> followListViewModelProvider;

    private Provider<GoodreadsImportViewModel> goodreadsImportViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<LeaderboardViewModel> leaderboardViewModelProvider;

    private Provider<OnboardingViewModel> onboardingViewModelProvider;

    private Provider<ProfileViewModel> profileViewModelProvider;

    private Provider<ScanFlowViewModel> scanFlowViewModelProvider;

    private Provider<SearchViewModel> searchViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<WriteViewModel> writeViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.authViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.bookDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.celebrationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.diaryEntryDetailViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.editProfileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.followListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.goodreadsImportViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.leaderboardViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.onboardingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.profileViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.scanFlowViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.searchViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.writeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
    }

    @Override
    public Map<String, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(15).put("in.paperboxd.app.ui.screens.auth.AuthViewModel", ((Provider) authViewModelProvider)).put("in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel", ((Provider) bookDetailViewModelProvider)).put("in.paperboxd.app.ui.components.CelebrationViewModel", ((Provider) celebrationViewModelProvider)).put("in.paperboxd.app.ui.screens.diary.DiaryEntryDetailViewModel", ((Provider) diaryEntryDetailViewModelProvider)).put("in.paperboxd.app.ui.screens.profile.EditProfileViewModel", ((Provider) editProfileViewModelProvider)).put("in.paperboxd.app.ui.screens.profile.FollowListViewModel", ((Provider) followListViewModelProvider)).put("in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel", ((Provider) goodreadsImportViewModelProvider)).put("in.paperboxd.app.ui.screens.home.HomeViewModel", ((Provider) homeViewModelProvider)).put("in.paperboxd.app.ui.screens.leaderboard.LeaderboardViewModel", ((Provider) leaderboardViewModelProvider)).put("in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel", ((Provider) onboardingViewModelProvider)).put("in.paperboxd.app.ui.screens.profile.ProfileViewModel", ((Provider) profileViewModelProvider)).put("in.paperboxd.app.ui.screens.scan.ScanFlowViewModel", ((Provider) scanFlowViewModelProvider)).put("in.paperboxd.app.ui.screens.search.SearchViewModel", ((Provider) searchViewModelProvider)).put("in.paperboxd.app.ui.screens.settings.SettingsViewModel", ((Provider) settingsViewModelProvider)).put("in.paperboxd.app.ui.screens.write.WriteViewModel", ((Provider) writeViewModelProvider)).build();
    }

    @Override
    public Map<String, Object> getHiltViewModelAssistedMap() {
      return Collections.<String, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // in.paperboxd.app.ui.screens.auth.AuthViewModel 
          return (T) new AuthViewModel(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.googleSignInHelperProvider.get());

          case 1: // in.paperboxd.app.ui.screens.bookdetail.BookDetailViewModel 
          return (T) new BookDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.bookRepositoryProvider.get(), singletonCImpl.recommendationRepositoryProvider.get(), singletonCImpl.userRepositoryProvider.get(), singletonCImpl.celebrationCenterProvider.get());

          case 2: // in.paperboxd.app.ui.components.CelebrationViewModel 
          return (T) new CelebrationViewModel(singletonCImpl.celebrationCenterProvider.get());

          case 3: // in.paperboxd.app.ui.screens.diary.DiaryEntryDetailViewModel 
          return (T) new DiaryEntryDetailViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.diaryRepositoryProvider.get());

          case 4: // in.paperboxd.app.ui.screens.profile.EditProfileViewModel 
          return (T) new EditProfileViewModel(singletonCImpl.userRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get(), singletonCImpl.provideApiServiceProvider.get());

          case 5: // in.paperboxd.app.ui.screens.profile.FollowListViewModel 
          return (T) new FollowListViewModel(singletonCImpl.userRepositoryProvider.get());

          case 6: // in.paperboxd.app.ui.screens.settings.GoodreadsImportViewModel 
          return (T) new GoodreadsImportViewModel(singletonCImpl.bookRepositoryProvider.get(), singletonCImpl.authRepositoryProvider.get());

          case 7: // in.paperboxd.app.ui.screens.home.HomeViewModel 
          return (T) new HomeViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.recommendationRepositoryProvider.get(), singletonCImpl.bookRepositoryProvider.get(), singletonCImpl.userRepositoryProvider.get());

          case 8: // in.paperboxd.app.ui.screens.leaderboard.LeaderboardViewModel 
          return (T) new LeaderboardViewModel(singletonCImpl.userRepositoryProvider.get(), singletonCImpl.celebrationCenterProvider.get());

          case 9: // in.paperboxd.app.ui.screens.onboarding.OnboardingViewModel 
          return (T) new OnboardingViewModel(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.bookRepositoryProvider.get(), singletonCImpl.recommendationRepositoryProvider.get());

          case 10: // in.paperboxd.app.ui.screens.profile.ProfileViewModel 
          return (T) new ProfileViewModel(singletonCImpl.userRepositoryProvider.get(), singletonCImpl.diaryRepositoryProvider.get());

          case 11: // in.paperboxd.app.ui.screens.scan.ScanFlowViewModel 
          return (T) new ScanFlowViewModel(singletonCImpl.bookRepositoryProvider.get(), ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // in.paperboxd.app.ui.screens.search.SearchViewModel 
          return (T) new SearchViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.bookRepositoryProvider.get(), singletonCImpl.userRepositoryProvider.get(), singletonCImpl.recommendationRepositoryProvider.get());

          case 13: // in.paperboxd.app.ui.screens.settings.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideApiServiceProvider.get());

          case 14: // in.paperboxd.app.ui.screens.write.WriteViewModel 
          return (T) new WriteViewModel(singletonCImpl.diaryRepositoryProvider.get(), singletonCImpl.bookRepositoryProvider.get(), singletonCImpl.userRepositoryProvider.get(), singletonCImpl.celebrationCenterProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends PaperBoxdApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends PaperBoxdApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends PaperBoxdApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<SecurePrefs> securePrefsProvider;

    private Provider<SessionEvents> sessionEventsProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<Retrofit> provideRetrofitProvider;

    private Provider<ApiService> provideApiServiceProvider;

    private Provider<AuthRepository> authRepositoryProvider;

    private Provider<AppState> appStateProvider;

    private Provider<GoogleSignInHelper> googleSignInHelperProvider;

    private Provider<BookRepository> bookRepositoryProvider;

    private Provider<RecommendationRepository> recommendationRepositoryProvider;

    private Provider<UserRepository> userRepositoryProvider;

    private Provider<CelebrationCenter> celebrationCenterProvider;

    private Provider<DiaryRepository> diaryRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private AuthInterceptor authInterceptor() {
      return new AuthInterceptor(securePrefsProvider.get());
    }

    private UnauthorizedInterceptor unauthorizedInterceptor() {
      return new UnauthorizedInterceptor(securePrefsProvider.get(), sessionEventsProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.securePrefsProvider = DoubleCheck.provider(new SwitchingProvider<SecurePrefs>(singletonCImpl, 5));
      this.sessionEventsProvider = DoubleCheck.provider(new SwitchingProvider<SessionEvents>(singletonCImpl, 6));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 4));
      this.provideRetrofitProvider = DoubleCheck.provider(new SwitchingProvider<Retrofit>(singletonCImpl, 3));
      this.provideApiServiceProvider = DoubleCheck.provider(new SwitchingProvider<ApiService>(singletonCImpl, 2));
      this.authRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AuthRepository>(singletonCImpl, 1));
      this.appStateProvider = DoubleCheck.provider(new SwitchingProvider<AppState>(singletonCImpl, 0));
      this.googleSignInHelperProvider = DoubleCheck.provider(new SwitchingProvider<GoogleSignInHelper>(singletonCImpl, 7));
      this.bookRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<BookRepository>(singletonCImpl, 8));
      this.recommendationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<RecommendationRepository>(singletonCImpl, 9));
      this.userRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserRepository>(singletonCImpl, 10));
      this.celebrationCenterProvider = DoubleCheck.provider(new SwitchingProvider<CelebrationCenter>(singletonCImpl, 11));
      this.diaryRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DiaryRepository>(singletonCImpl, 12));
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @Override
    public void injectPaperBoxdApp(PaperBoxdApp paperBoxdApp) {
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // in.paperboxd.app.AppState 
          return (T) new AppState(singletonCImpl.authRepositoryProvider.get(), singletonCImpl.sessionEventsProvider.get());

          case 1: // in.paperboxd.app.data.repository.AuthRepository 
          return (T) new AuthRepository(singletonCImpl.provideApiServiceProvider.get(), singletonCImpl.securePrefsProvider.get());

          case 2: // in.paperboxd.app.data.remote.ApiService 
          return (T) NetworkModule_ProvideApiServiceFactory.provideApiService(singletonCImpl.provideRetrofitProvider.get());

          case 3: // retrofit2.Retrofit 
          return (T) NetworkModule_ProvideRetrofitFactory.provideRetrofit(singletonCImpl.provideOkHttpClientProvider.get());

          case 4: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient(singletonCImpl.authInterceptor(), singletonCImpl.unauthorizedInterceptor());

          case 5: // in.paperboxd.app.data.local.SecurePrefs 
          return (T) new SecurePrefs(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // in.paperboxd.app.data.remote.SessionEvents 
          return (T) new SessionEvents();

          case 7: // in.paperboxd.app.auth.google.GoogleSignInHelper 
          return (T) new GoogleSignInHelper();

          case 8: // in.paperboxd.app.data.repository.BookRepository 
          return (T) new BookRepository(singletonCImpl.provideApiServiceProvider.get());

          case 9: // in.paperboxd.app.data.repository.RecommendationRepository 
          return (T) new RecommendationRepository(singletonCImpl.provideApiServiceProvider.get());

          case 10: // in.paperboxd.app.data.repository.UserRepository 
          return (T) new UserRepository(singletonCImpl.provideApiServiceProvider.get());

          case 11: // in.paperboxd.app.ui.components.CelebrationCenter 
          return (T) new CelebrationCenter(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 12: // in.paperboxd.app.data.repository.DiaryRepository 
          return (T) new DiaryRepository(singletonCImpl.provideApiServiceProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
