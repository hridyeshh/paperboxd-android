package in.paperboxd.app;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.data.remote.SessionEvents;
import in.paperboxd.app.data.repository.AuthRepository;
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
public final class AppState_Factory implements Factory<AppState> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<SessionEvents> sessionEventsProvider;

  public AppState_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<SessionEvents> sessionEventsProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.sessionEventsProvider = sessionEventsProvider;
  }

  @Override
  public AppState get() {
    return newInstance(authRepositoryProvider.get(), sessionEventsProvider.get());
  }

  public static AppState_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<SessionEvents> sessionEventsProvider) {
    return new AppState_Factory(authRepositoryProvider, sessionEventsProvider);
  }

  public static AppState newInstance(AuthRepository authRepository, SessionEvents sessionEvents) {
    return new AppState(authRepository, sessionEvents);
  }
}
