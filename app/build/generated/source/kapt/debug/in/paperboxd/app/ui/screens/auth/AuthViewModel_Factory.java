package in.paperboxd.app.ui.screens.auth;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import in.paperboxd.app.auth.google.GoogleSignInHelper;
import in.paperboxd.app.data.repository.AuthRepository;
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
public final class AuthViewModel_Factory implements Factory<AuthViewModel> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<GoogleSignInHelper> googleSignInHelperProvider;

  public AuthViewModel_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.googleSignInHelperProvider = googleSignInHelperProvider;
  }

  @Override
  public AuthViewModel get() {
    return newInstance(authRepositoryProvider.get(), googleSignInHelperProvider.get());
  }

  public static AuthViewModel_Factory create(Provider<AuthRepository> authRepositoryProvider,
      Provider<GoogleSignInHelper> googleSignInHelperProvider) {
    return new AuthViewModel_Factory(authRepositoryProvider, googleSignInHelperProvider);
  }

  public static AuthViewModel newInstance(AuthRepository authRepository,
      GoogleSignInHelper googleSignInHelper) {
    return new AuthViewModel(authRepository, googleSignInHelper);
  }
}
