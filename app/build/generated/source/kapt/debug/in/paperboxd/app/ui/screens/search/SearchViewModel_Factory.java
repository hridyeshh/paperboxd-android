package in.paperboxd.app.ui.screens.search;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.BookRepository;
import in.paperboxd.app.data.repository.RecommendationRepository;
import in.paperboxd.app.data.repository.UserRepository;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SearchViewModel_Factory implements Factory<SearchViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<BookRepository> bookRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<RecommendationRepository> recommendationRepositoryProvider;

  public SearchViewModel_Factory(Provider<Context> contextProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.recommendationRepositoryProvider = recommendationRepositoryProvider;
  }

  @Override
  public SearchViewModel get() {
    return newInstance(contextProvider.get(), bookRepositoryProvider.get(), userRepositoryProvider.get(), recommendationRepositoryProvider.get());
  }

  public static SearchViewModel_Factory create(Provider<Context> contextProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider) {
    return new SearchViewModel_Factory(contextProvider, bookRepositoryProvider, userRepositoryProvider, recommendationRepositoryProvider);
  }

  public static SearchViewModel newInstance(Context context, BookRepository bookRepository,
      UserRepository userRepository, RecommendationRepository recommendationRepository) {
    return new SearchViewModel(context, bookRepository, userRepository, recommendationRepository);
  }
}
