package in.paperboxd.app.ui.screens.diary;

import androidx.lifecycle.SavedStateHandle;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class DiaryEntryDetailViewModel_Factory implements Factory<DiaryEntryDetailViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<DiaryRepository> diaryRepositoryProvider;

  public DiaryEntryDetailViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.diaryRepositoryProvider = diaryRepositoryProvider;
  }

  @Override
  public DiaryEntryDetailViewModel get() {
    return newInstance(savedStateHandleProvider.get(), diaryRepositoryProvider.get());
  }

  public static DiaryEntryDetailViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<DiaryRepository> diaryRepositoryProvider) {
    return new DiaryEntryDetailViewModel_Factory(savedStateHandleProvider, diaryRepositoryProvider);
  }

  public static DiaryEntryDetailViewModel newInstance(SavedStateHandle savedStateHandle,
      DiaryRepository diaryRepository) {
    return new DiaryEntryDetailViewModel(savedStateHandle, diaryRepository);
  }
}
