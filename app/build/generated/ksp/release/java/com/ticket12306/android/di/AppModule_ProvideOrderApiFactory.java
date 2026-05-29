package com.ticket12306.android.di;

import com.ticket12306.android.data.remote.api.OrderApi;
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
public final class AppModule_ProvideOrderApiFactory implements Factory<OrderApi> {
  @Override
  public OrderApi get() {
    return provideOrderApi();
  }

  public static AppModule_ProvideOrderApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static OrderApi provideOrderApi() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideOrderApi());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideOrderApiFactory INSTANCE = new AppModule_ProvideOrderApiFactory();
  }
}
