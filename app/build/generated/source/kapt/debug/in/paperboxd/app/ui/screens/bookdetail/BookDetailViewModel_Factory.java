package in.paperboxd.app.ui.screens.bookdetail;

import androidx.lifecycle.SavedStateHandle;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.BookRepository;
import in.paperboxd.app.data.repository.RecommendationRepository;
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

  public BookDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.recommendationRepositoryProvider = recommendationRepositoryProvider;
  }

  @Override
  public BookDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), bookRepositoryProvider.get(), recommendationRepositoryProvider.get());
  }

  public static BookDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider) {
    return new BookDetailViewModel_Factory(savedStateHandleProvider, bookRepositoryProvider, recommendationRepositoryProvider);
  }

  public static BookDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      BookRepository bookRepository, RecommendationRepository recommendationRepository) {
    return new BookDetailViewModel(savedStateHandle, bookRepository, recommendationRepository);
  }
}
