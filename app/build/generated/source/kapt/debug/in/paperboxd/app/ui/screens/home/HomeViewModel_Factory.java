package in.paperboxd.app.ui.screens.home;

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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<RecommendationRepository> recommendationRepositoryProvider;

  private final Provider<BookRepository> bookRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public HomeViewModel_Factory(Provider<Context> contextProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.contextProvider = contextProvider;
    this.recommendationRepositoryProvider = recommendationRepositoryProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(contextProvider.get(), recommendationRepositoryProvider.get(), bookRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<Context> contextProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new HomeViewModel_Factory(contextProvider, recommendationRepositoryProvider, bookRepositoryProvider, userRepositoryProvider);
  }

  public static HomeViewModel newInstance(Context context,
      RecommendationRepository recommendationRepository, BookRepository bookRepository,
      UserRepository userRepository) {
    return new HomeViewModel(context, recommendationRepository, bookRepository, userRepository);
  }
}
