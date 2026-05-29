package com.ticket12306.android.di;

import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.local.preferences.UserPreferences;
import com.ticket12306.android.data.remote.api.OrderApi;
import com.ticket12306.android.data.remote.api.TicketApi;
import com.ticket12306.android.data.repository.TicketRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
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
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AppModule_ProvideTicketRepositoryFactory implements Factory<TicketRepository> {
  private final Provider<TicketApi> ticketApiProvider;

  private final Provider<OrderApi> orderApiProvider;

  private final Provider<AppDatabase> databaseProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public AppModule_ProvideTicketRepositoryFactory(Provider<TicketApi> ticketApiProvider,
      Provider<OrderApi> orderApiProvider, Provider<AppDatabase> databaseProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.ticketApiProvider = ticketApiProvider;
    this.orderApiProvider = orderApiProvider;
    this.databaseProvider = databaseProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public TicketRepository get() {
    return provideTicketRepository(ticketApiProvider.get(), orderApiProvider.get(), databaseProvider.get(), userPreferencesProvider.get());
  }

  public static AppModule_ProvideTicketRepositoryFactory create(
      Provider<TicketApi> ticketApiProvider, Provider<OrderApi> orderApiProvider,
      Provider<AppDatabase> databaseProvider, Provider<UserPreferences> userPreferencesProvider) {
    return new AppModule_ProvideTicketRepositoryFactory(ticketApiProvider, orderApiProvider, databaseProvider, userPreferencesProvider);
  }

  public static TicketRepository provideTicketRepository(TicketApi ticketApi, OrderApi orderApi,
      AppDatabase database, UserPreferences userPreferences) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTicketRepository(ticketApi, orderApi, database, userPreferences));
  }
}
