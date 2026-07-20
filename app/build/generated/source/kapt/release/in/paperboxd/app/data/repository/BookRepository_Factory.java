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
public final class BookRepository_Factory implements Factory<BookRepository> {
  private final Provider<ApiService> apiProvider;

  public BookRepository_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public BookRepository get() {
    return newInstance(apiProvider.get());
  }

  public static BookRepository_Factory create(Provider<ApiService> apiProvider) {
    return new BookRepository_Factory(apiProvider);
  }

  public static BookRepository newInstance(ApiService api) {
    return new BookRepository(api);
  }
}
