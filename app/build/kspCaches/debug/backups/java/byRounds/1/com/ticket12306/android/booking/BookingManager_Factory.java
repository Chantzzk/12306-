package com.ticket12306.android.booking;

import com.ticket12306.android.data.local.dao.BookingLogDao;
import com.ticket12306.android.data.local.dao.BookingTaskDao;
import com.ticket12306.android.data.repository.TicketRepository;
import com.ticket12306.android.util.NotificationHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class BookingManager_Factory implements Factory<BookingManager> {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<BookingTaskDao> bookingTaskDaoProvider;

  private final Provider<BookingLogDao> bookingLogDaoProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  private final Provider<TicketMonitor> ticketMonitorProvider;

  public BookingManager_Factory(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<TicketMonitor> ticketMonitorProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.bookingTaskDaoProvider = bookingTaskDaoProvider;
    this.bookingLogDaoProvider = bookingLogDaoProvider;
    this.notificationHelperProvider = notificationHelperProvider;
    this.ticketMonitorProvider = ticketMonitorProvider;
  }

  @Override
  public BookingManager get() {
    return newInstance(ticketRepositoryProvider.get(), bookingTaskDaoProvider.get(), bookingLogDaoProvider.get(), notificationHelperProvider.get(), ticketMonitorProvider.get());
  }

  public static BookingManager_Factory create(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<TicketMonitor> ticketMonitorProvider) {
    return new BookingManager_Factory(ticketRepositoryProvider, bookingTaskDaoProvider, bookingLogDaoProvider, notificationHelperProvider, ticketMonitorProvider);
  }

  public static BookingManager newInstance(TicketRepository ticketRepository,
      BookingTaskDao bookingTaskDao, BookingLogDao bookingLogDao,
      NotificationHelper notificationHelper, TicketMonitor ticketMonitor) {
    return new BookingManager(ticketRepository, bookingTaskDao, bookingLogDao, notificationHelper, ticketMonitor);
  }
}
