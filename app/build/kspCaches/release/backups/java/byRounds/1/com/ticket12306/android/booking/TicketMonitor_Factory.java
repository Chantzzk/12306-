package com.ticket12306.android.booking;

import com.ticket12306.android.data.local.dao.BookingLogDao;
import com.ticket12306.android.data.local.dao.BookingTaskDao;
import com.ticket12306.android.data.repository.TicketRepository;
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
public final class TicketMonitor_Factory implements Factory<TicketMonitor> {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<BookingTaskDao> bookingTaskDaoProvider;

  private final Provider<BookingLogDao> bookingLogDaoProvider;

  public TicketMonitor_Factory(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.bookingTaskDaoProvider = bookingTaskDaoProvider;
    this.bookingLogDaoProvider = bookingLogDaoProvider;
  }

  @Override
  public TicketMonitor get() {
    return newInstance(ticketRepositoryProvider.get(), bookingTaskDaoProvider.get(), bookingLogDaoProvider.get());
  }

  public static TicketMonitor_Factory create(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider) {
    return new TicketMonitor_Factory(ticketRepositoryProvider, bookingTaskDaoProvider, bookingLogDaoProvider);
  }

  public static TicketMonitor newInstance(TicketRepository ticketRepository,
      BookingTaskDao bookingTaskDao, BookingLogDao bookingLogDao) {
    return new TicketMonitor(ticketRepository, bookingTaskDao, bookingLogDao);
  }
}
