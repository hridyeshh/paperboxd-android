package in.paperboxd.app.ui.screens.bookdetail;

import androidx.lifecycle.SavedStateHandle;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.BookRepository;
import in.paperboxd.app.data.repository.RecommendationRepository;
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
public final class BookDetailViewModel_Factory implements Factory<BookDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<BookRepository> bookRepositoryProvider;

  private final Provider<RecommendationRepository> recommendationRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<CelebrationCenter> celebrationCenterProvider;

  public BookDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<CelebrationCenter> celebrationCenterProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.recommendationRepositoryProvider = recommendationRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.celebrationCenterProvider = celebrationCenterProvider;
  }

  @Override
  public BookDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), bookRepositoryProvider.get(), recommendationRepositoryProvider.get(), userRepositoryProvider.get(), celebrationCenterProvider.get());
  }

  public static BookDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<CelebrationCenter> celebrationCenterProvider) {
    return new BookDetailViewModel_Factory(savedStateHandleProvider, bookRepositoryProvider, recommendationRepositoryProvider, userRepositoryProvider, celebrationCenterProvider);
  }

  public static BookDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      BookRepository bookRepository, RecommendationRepository recommendationRepository,
      UserRepository userRepository, CelebrationCenter celebrationCenter) {
    return new BookDetailViewModel(savedStateHandle, bookRepository, recommendationRepository, userRepository, celebrationCenter);
  }
}
