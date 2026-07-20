package in.paperboxd.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.remote.ApiService;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class RecommendationRepository_Factory implements Factory<RecommendationRepository> {
  private final Provider<ApiService> apiProvider;

  public RecommendationRepository_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public RecommendationRepository get() {
    return newInstance(apiProvider.get());
  }

  public static RecommendationRepository_Factory create(Provider<ApiService> apiProvider) {
    return new RecommendationRepository_Factory(apiProvider);
  }

  public static RecommendationRepository newInstance(ApiService api) {
    return new RecommendationRepository(api);
  }
}
