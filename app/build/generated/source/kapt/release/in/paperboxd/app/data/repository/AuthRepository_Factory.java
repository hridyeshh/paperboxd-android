package in.paperboxd.app.data.repository;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.local.SecurePrefs;
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
public final class AuthRepository_Factory implements Factory<AuthRepository> {
  private final Provider<ApiService> apiProvider;

  private final Provider<SecurePrefs> securePrefsProvider;

  public AuthRepository_Factory(Provider<ApiService> apiProvider,
      Provider<SecurePrefs> securePrefsProvider) {
    this.apiProvider = apiProvider;
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public AuthRepository get() {
    return newInstance(apiProvider.get(), securePrefsProvider.get());
  }

  public static AuthRepository_Factory create(Provider<ApiService> apiProvider,
      Provider<SecurePrefs> securePrefsProvider) {
    return new AuthRepository_Factory(apiProvider, securePrefsProvider);
  }

  public static AuthRepository newInstance(ApiService api, SecurePrefs securePrefs) {
    return new AuthRepository(api, securePrefs);
  }
}
