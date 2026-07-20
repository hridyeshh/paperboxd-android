package in.paperboxd.app.data.remote;

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
public final class SessionEvents_Factory implements Factory<SessionEvents> {
  @Override
  public SessionEvents get() {
    return newInstance();
  }

  public static SessionEvents_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static SessionEvents newInstance() {
    return new SessionEvents();
  }

  private static final class InstanceHolder {
    private static final SessionEvents_Factory INSTANCE = new SessionEvents_Factory();
  }
}
