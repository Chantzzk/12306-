package com.ticket12306.android.di;

import com.ticket12306.android.booking.TicketMonitor;
import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.repository.TicketRepository;
import com.ticket12306.android.util.NotificationHelper;
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
public final class AppModule_ProvideTicketMonitorFactory implements Factory<TicketMonitor> {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<AppDatabase> databaseProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public AppModule_ProvideTicketMonitorFactory(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<AppDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.databaseProvider = databaseProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  @Override
  public TicketMonitor get() {
    return provideTicketMonitor(ticketRepositoryProvider.get(), databaseProvider.get(), notificationHelperProvider.get());
  }

  public static AppModule_ProvideTicketMonitorFactory create(
      Provider<TicketRepository> ticketRepositoryProvider, Provider<AppDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new AppModule_ProvideTicketMonitorFactory(ticketRepositoryProvider, databaseProvider, notificationHelperProvider);
  }

  public static TicketMonitor provideTicketMonitor(TicketRepository ticketRepository,
      AppDatabase database, NotificationHelper notificationHelper) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideTicketMonitor(ticketRepository, database, notificationHelper));
  }
}
