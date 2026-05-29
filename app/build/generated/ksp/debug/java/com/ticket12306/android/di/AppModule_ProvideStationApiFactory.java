package com.ticket12306.android.di;

import com.ticket12306.android.data.remote.api.StationApi;
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
public final class AppModule_ProvideStationApiFactory implements Factory<StationApi> {
  @Override
  public StationApi get() {
    return provideStationApi();
  }

  public static AppModule_ProvideStationApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static StationApi provideStationApi() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideStationApi());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideStationApiFactory INSTANCE = new AppModule_ProvideStationApiFactory();
  }
}
