package in.paperboxd.app.ui.screens.settings;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.AuthRepository;
import in.paperboxd.app.data.repository.BookRepository;
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
public final class GoodreadsImportViewModel_Factory implements Factory<GoodreadsImportViewModel> {
  private final Provider<BookRepository> bookRepositoryProvider;

  private final Provider<AuthRepository> authRepositoryProvider;

  public GoodreadsImportViewModel_Factory(Provider<BookRepository> bookRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.authRepositoryProvider = authRepositoryProvider;
  }

  @Override
  public GoodreadsImportViewModel get() {
    return newInstance(bookRepositoryProvider.get(), authRepositoryProvider.get());
  }

  public static GoodreadsImportViewModel_Factory create(
      Provider<BookRepository> bookRepositoryProvider,
      Provider<AuthRepository> authRepositoryProvider) {
    return new GoodreadsImportViewModel_Factory(bookRepositoryProvider, authRepositoryProvider);
  }

  public static GoodreadsImportViewModel newInstance(BookRepository bookRepository,
      AuthRepository authRepository) {
    return new GoodreadsImportViewModel(bookRepository, authRepository);
  }
}
