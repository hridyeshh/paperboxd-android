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
public final class DiaryRepository_Factory implements Factory<DiaryRepository> {
  private final Provider<ApiService> apiProvider;

  public DiaryRepository_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public DiaryRepository get() {
    return newInstance(apiProvider.get());
  }

  public static DiaryRepository_Factory create(Provider<ApiService> apiProvider) {
    return new DiaryRepository_Factory(apiProvider);
  }

  public static DiaryRepository newInstance(ApiService api) {
    return new DiaryRepository(api);
  }
}
