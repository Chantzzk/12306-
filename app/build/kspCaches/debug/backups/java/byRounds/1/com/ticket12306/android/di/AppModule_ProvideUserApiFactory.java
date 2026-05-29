package com.ticket12306.android.di;

import com.ticket12306.android.data.remote.api.UserApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvideUserApiFactory implements Factory<UserApi> {
  @Override
  public UserApi get() {
    return provideUserApi();
  }

  public static AppModule_ProvideUserApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static UserApi provideUserApi() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUserApi());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideUserApiFactory INSTANCE = new AppModule_ProvideUserApiFactory();
  }
}
