package in.paperboxd.app.ui.screens.scan;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.BookRepository;
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
public final class ScanFlowViewModel_Factory implements Factory<ScanFlowViewModel> {
  private final Provider<BookRepository> bookRepositoryProvider;

  private final Provider<Context> contextProvider;

  public ScanFlowViewModel_Factory(Provider<BookRepository> bookRepositoryProvider,
      Provider<Context> contextProvider) {
    this.bookRepositoryProvider = bookRepositoryProvider;
    this.contextProvider = contextProvider;
  }

  @Override
  public ScanFlowViewModel get() {
    return newInstance(bookRepositoryProvider.get(), contextProvider.get());
  }

  public static ScanFlowViewModel_Factory create(Provider<BookRepository> bookRepositoryProvider,
      Provider<Context> contextProvider) {
    return new ScanFlowViewModel_Factory(bookRepositoryProvider, contextProvider);
  }

  public static ScanFlowViewModel newInstance(BookRepository bookRepository, Context context) {
    return new ScanFlowViewModel(bookRepository, context);
  }
}
