package in.paperboxd.app.data.remote;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.local.SecurePrefs;
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<SecurePrefs> securePrefsProvider;

  public AuthInterceptor_Factory(Provider<SecurePrefs> securePrefsProvider) {
    this.securePrefsProvider = securePrefsProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(securePrefsProvider.get());
  }

  public static AuthInterceptor_Factory create(Provider<SecurePrefs> securePrefsProvider) {
    return new AuthInterceptor_Factory(securePrefsProvider);
  }

  public static AuthInterceptor newInstance(SecurePrefs securePrefs) {
    return new AuthInterceptor(securePrefs);
  }
}
