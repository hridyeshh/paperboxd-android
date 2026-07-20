package in.paperboxd.app.ui.screens.jazy;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class JazyViewModel_Factory implements Factory<JazyViewModel> {
  private final Provider<BookRepository> bookRepositoryProvider;

  public JazyViewModel_Factory(Provider<BookRepository> bookRepositoryProvider) {
    this.bookRepositoryProvider = bookRepositoryProvider;
  }

  @Override
  public JazyViewModel get() {
    return newInstance(bookRepositoryProvider.get());
  }

  public static JazyViewModel_Factory create(Provider<BookRepository> bookRepositoryProvider) {
    return new JazyViewModel_Factory(bookRepositoryProvider);
  }

  public static JazyViewModel newInstance(BookRepository bookRepository) {
    return new JazyViewModel(bookRepository);
  }
}
