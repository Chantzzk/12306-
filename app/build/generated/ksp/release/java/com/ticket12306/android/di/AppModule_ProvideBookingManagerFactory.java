package com.ticket12306.android.di;

import com.ticket12306.android.booking.BookingManager;
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
public final class AppModule_ProvideBookingManagerFactory implements Factory<BookingManager> {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<AppDatabase> databaseProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  private final Provider<TicketMonitor> ticketMonitorProvider;

  public AppModule_ProvideBookingManagerFactory(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<AppDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<TicketMonitor> ticketMonitorProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.databaseProvider = databaseProvider;
    this.notificationHelperProvider = notificationHelperProvider;
    this.ticketMonitorProvider = ticketMonitorProvider;
  }

  @Override
  public BookingManager get() {
    return provideBookingManager(ticketRepositoryProvider.get(), databaseProvider.get(), notificationHelperProvider.get(), ticketMonitorProvider.get());
  }

  public static AppModule_ProvideBookingManagerFactory create(
      Provider<TicketRepository> ticketRepositoryProvider, Provider<AppDatabase> databaseProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<TicketMonitor> ticketMonitorProvider) {
    return new AppModule_ProvideBookingManagerFactory(ticketRepositoryProvider, databaseProvider, notificationHelperProvider, ticketMonitorProvider);
  }

  public static BookingManager provideBookingManager(TicketRepository ticketRepository,
      AppDatabase database, NotificationHelper notificationHelper, TicketMonitor ticketMonitor) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBookingManager(ticketRepository, database, notificationHelper, ticketMonitor));
  }
}
