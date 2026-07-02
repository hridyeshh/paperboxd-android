package in.paperboxd.app.ui.screens.write;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.repository.BookRepository;
import in.paperboxd.app.data.repository.DiaryRepository;
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
public final class WriteViewModel_Factory implements Factory<WriteViewModel> {
  private final Provider<DiaryRepository> diaryRepositoryProvider;

  private final Provider<BookRepository> bookRepositoryProvider;

  public WriteViewModel_Factory(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider) {
    this.diaryRepositoryProvider = diaryRepositoryProvider;
    this.bookRepositoryProvider = bookRepositoryProvider;
  }

  @Override
  public WriteViewModel get() {
    return newInstance(diaryRepositoryProvider.get(), bookRepositoryProvider.get());
  }

  public static WriteViewModel_Factory create(Provider<DiaryRepository> diaryRepositoryProvider,
      Provider<BookRepository> bookRepositoryProvider) {
    return new WriteViewModel_Factory(diaryRepositoryProvider, bookRepositoryProvider);
  }

  public static WriteViewModel newInstance(DiaryRepository diaryRepository,
      BookRepository bookRepository) {
    return new WriteViewModel(diaryRepository, bookRepository);
  }
}
