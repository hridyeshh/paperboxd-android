package in.paperboxd.app.ui.screens.profile;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.DiaryRepository;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  public ProfileViewModel_Factory(Provider<UserRepository> userRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    this.userRepositoryProvider = userRepositoryProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(userRepositoryProvider.get(), diaryRepositoryProvider.get());
  }

  public static ProfileViewModel_Factory create(Provider<UserRepository> userRepositoryProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    return new ProfileViewModel_Factory(userRepositoryProvider, diaryRepositoryProvider);
  }

  public static ProfileViewModel newInstance(UserRepository userRepository,
      DiaryRepository diaryRepository) {
    return new ProfileViewModel(userRepository, diaryRepository);
  }
}
