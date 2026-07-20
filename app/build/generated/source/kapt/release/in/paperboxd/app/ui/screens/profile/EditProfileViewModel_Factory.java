package in.paperboxd.app.ui.screens.profile;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.remote.ApiService;
import in.paperboxd.app.data.repository.AuthRepository;
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
public final class EditProfileViewModel_Factory implements Factory<EditProfileViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<ApiService> apiProvider;

  public EditProfileViewModel_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider, Provider<ApiService> apiProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
    this.apiProvider = apiProvider;
  }

  @Override
  public EditProfileViewModel get() {
    return newInstance(userRepositoryProvider.get(), authRepositoryProvider.get(), apiProvider.get());
  }

  public static EditProfileViewModel_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider, Provider<ApiService> apiProvider) {
    return new EditProfileViewModel_Factory(userRepositoryProvider, authRepositoryProvider, apiProvider);
  }

  public static EditProfileViewModel newInstance(UserRepository userRepository,
      AuthRepository authRepository, ApiService api) {
    return new EditProfileViewModel(userRepository, authRepository, api);
  }
}
