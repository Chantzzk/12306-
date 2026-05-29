package com.ticket12306.android.di;

import com.ticket12306.android.data.remote.api.TicketApi;
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
public final class AppModule_ProvideTicketApiFactory implements Factory<TicketApi> {
  @Override
  public TicketApi get() {
    return provideTicketApi();
  }

  public static AppModule_ProvideTicketApiFactory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TicketApi provideTicketApi() {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTicketApi());
  }

  private static final class InstanceHolder {
    private static final AppModule_ProvideTicketApiFactory INSTANCE = new AppModule_ProvideTicketApiFactory();
  }
}
