package in.paperboxd.app.auth.google;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

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
public final class GoogleSignInHelper_Factory implements Factory<GoogleSignInHelper> {
  @Override
  public GoogleSignInHelper get() {
    return newInstance();
  }

  public static GoogleSignInHelper_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static GoogleSignInHelper newInstance() {
    return new GoogleSignInHelper();
  }

  private static final class InstanceHolder {
    private static final GoogleSignInHelper_Factory INSTANCE = new GoogleSignInHelper_Factory();
  }
}
