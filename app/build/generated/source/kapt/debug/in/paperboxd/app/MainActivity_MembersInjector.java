package in.paperboxd.app;

import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<AppState> appStateProvider;

  public MainActivity_MembersInjector(Provider<AppState> appStateProvider) {
    this.appStateProvider = appStateProvider;
  }

  public static MembersInjector<MainActivity> create(Provider<AppState> appStateProvider) {
    return new MainActivity_MembersInjector(appStateProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectAppState(instance, appStateProvider.get());
  }

  @InjectedFieldSignature("in.paperboxd.app.MainActivity.appState")
  public static void injectAppState(MainActivity instance, AppState appState) {
    instance.appState = appState;
  }
}
