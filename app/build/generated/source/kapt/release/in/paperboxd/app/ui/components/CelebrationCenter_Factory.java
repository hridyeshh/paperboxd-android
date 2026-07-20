package in.paperboxd.app.ui.components;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class CelebrationCenter_Factory implements Factory<CelebrationCenter> {
  private final Provider<Context> contextProvider;

  public CelebrationCenter_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public CelebrationCenter get() {
    return newInstance(contextProvider.get());
  }

  public static CelebrationCenter_Factory create(Provider<Context> contextProvider) {
    return new CelebrationCenter_Factory(contextProvider);
  }

  public static CelebrationCenter newInstance(Context context) {
    return new CelebrationCenter(context);
  }
}
