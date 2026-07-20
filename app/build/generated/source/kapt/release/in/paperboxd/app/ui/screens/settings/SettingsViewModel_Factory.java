package in.paperboxd.app.ui.screens.settings;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.remote.ApiService;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<ApiService> apiProvider;

  public SettingsViewModel_Factory(Provider<ApiService> apiProvider) {
    this.apiProvider = apiProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(apiProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<ApiService> apiProvider) {
    return new SettingsViewModel_Factory(apiProvider);
  }

  public static SettingsViewModel newInstance(ApiService api) {
    return new SettingsViewModel(api);
  }
}
