package in.paperboxd.app.ui.screens.leaderboard;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.UserRepository;
import in.paperboxd.app.ui.components.CelebrationCenter;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class LeaderboardViewModel_Factory implements Factory<LeaderboardViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<CelebrationCenter> celebrationCenterProvider;

  public LeaderboardViewModel_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<CelebrationCenter> celebrationCenterProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.celebrationCenterProvider = celebrationCenterProvider;
  }

  @Override
  public LeaderboardViewModel get() {
    return newInstance(userRepositoryProvider.get(), celebrationCenterProvider.get());
  }

  public static LeaderboardViewModel_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<CelebrationCenter> celebrationCenterProvider) {
    return new LeaderboardViewModel_Factory(userRepositoryProvider, celebrationCenterProvider);
  }

  public static LeaderboardViewModel newInstance(UserRepository userRepository,
      CelebrationCenter celebrationCenter) {
    return new LeaderboardViewModel(userRepository, celebrationCenter);
  }
}
