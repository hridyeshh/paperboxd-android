package in.paperboxd.app.ui.screens.write;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.BookRepository;
import in.paperboxd.app.data.repository.DiaryRepository;
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
public final class WriteViewModel_Factory implements Factory<WriteViewModel> {
  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<BookRepository> bookRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<CelebrationCenter> celebrationCenterProvider;

  public WriteViewModel_Factory(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<CelebrationCenter> celebrationCenterProvider) {
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.celebrationCenterProvider = celebrationCenterProvider;
  }

  @Override
  public WriteViewModel get() {
    return newInstance(diaryRepositoryProvider.get(), bookRepositoryProvider.get(), userRepositoryProvider.get(), celebrationCenterProvider.get());
  }

  public static WriteViewModel_Factory create(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<CelebrationCenter> celebrationCenterProvider) {
    return new WriteViewModel_Factory(diaryRepositoryProvider, bookRepositoryProvider, userRepositoryProvider, celebrationCenterProvider);
  }

  public static WriteViewModel newInstance(DiaryRepository diaryRepository,
      BookRepository bookRepository, UserRepository userRepository,
      CelebrationCenter celebrationCenter) {
    return new WriteViewModel(diaryRepository, bookRepository, userRepository, celebrationCenter);
  }
}
