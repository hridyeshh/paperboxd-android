package in.paperboxd.app.ui.components;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
public final class CelebrationViewModel_Factory implements Factory<CelebrationViewModel> {
  private final Provider<CelebrationCenter> centerProvider;

  public CelebrationViewModel_Factory(Provider<CelebrationCenter> centerProvider) {
    this.centerProvider = centerProvider;
  }

  @Override
  public CelebrationViewModel get() {
    return newInstance(centerProvider.get());
  }

  public static CelebrationViewModel_Factory create(Provider<CelebrationCenter> centerProvider) {
    return new CelebrationViewModel_Factory(centerProvider);
  }

  public static CelebrationViewModel newInstance(CelebrationCenter center) {
    return new CelebrationViewModel(center);
  }
}
