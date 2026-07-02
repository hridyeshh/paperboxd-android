package in.paperboxd.app.ui.screens.onboarding;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.AuthRepository;
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
public final class OnboardingViewModel_Factory implements Factory<OnboardingViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<BookRepository> bookRepositoryProvider;

  private final Provider<RecommendationRepository> recommendationRepositoryProvider;

  public OnboardingViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.recommendationRepositoryProvider = recommendationRepositoryProvider;
  }

  @Override
  public OnboardingViewModel get() {
    return newInstance(authRepositoryProvider.get(), bookRepositoryProvider.get(), recommendationRepositoryProvider.get());
  }

  public static OnboardingViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider,
      Provider<RecommendationRepository> recommendationRepositoryProvider) {
    return new OnboardingViewModel_Factory(authRepositoryProvider, bookRepositoryProvider, recommendationRepositoryProvider);
  }

  public static OnboardingViewModel newInstance(AuthRepository authRepository,
      BookRepository bookRepository, RecommendationRepository recommendationRepository) {
    return new OnboardingViewModel(authRepository, bookRepository, recommendationRepository);
  }
}
