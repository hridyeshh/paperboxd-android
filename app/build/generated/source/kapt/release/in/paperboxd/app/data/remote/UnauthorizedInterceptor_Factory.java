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
public final class UnauthorizedInterceptor_Factory implements Factory<UnauthorizedInterceptor> {
  private final Provider<SecurePrefs> securePrefsProvider;

  private final Provider<SessionEvents> sessionEventsProvider;

  public UnauthorizedInterceptor_Factory(Provider<SecurePrefs> securePrefsProvider,
      Provider<SessionEvents> sessionEventsProvider) {
    this.securePrefsProvider = securePrefsProvider;
    this.sessionEventsProvider = sessionEventsProvider;
  }

  @Override
  public UnauthorizedInterceptor get() {
    return newInstance(securePrefsProvider.get(), sessionEventsProvider.get());
  }

  public static UnauthorizedInterceptor_Factory create(Provider<SecurePrefs> securePrefsProvider,
      Provider<SessionEvents> sessionEventsProvider) {
    return new UnauthorizedInterceptor_Factory(securePrefsProvider, sessionEventsProvider);
  }

  public static UnauthorizedInterceptor newInstance(SecurePrefs securePrefs,
      SessionEvents sessionEvents) {
    return new UnauthorizedInterceptor(securePrefs, sessionEvents);
  }
}
