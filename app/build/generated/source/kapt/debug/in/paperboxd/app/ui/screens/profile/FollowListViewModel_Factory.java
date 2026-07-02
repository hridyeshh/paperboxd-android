package in.paperboxd.app.ui.screens.profile;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.UserRepository;
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
public final class FollowListViewModel_Factory implements Factory<FollowListViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  public FollowListViewModel_Factory(Provider<UserRepository> userRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public FollowListViewModel get() {
    return newInstance(userRepositoryProvider.get());
  }

  public static FollowListViewModel_Factory create(
      Provider<UserRepository> userRepositoryProvider) {
    return new FollowListViewModel_Factory(userRepositoryProvider);
  }

  public static FollowListViewModel newInstance(UserRepository userRepository) {
    return new FollowListViewModel(userRepository);
  }
}
