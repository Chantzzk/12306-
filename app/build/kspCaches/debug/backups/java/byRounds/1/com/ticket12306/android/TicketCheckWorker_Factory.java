package com.ticket12306.android;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.ticket12306.android.booking.BookingManager;
import com.ticket12306.android.booking.TicketMonitor;
import com.ticket12306.android.data.local.dao.BookingLogDao;
import com.ticket12306.android.data.local.dao.BookingTaskDao;
import com.ticket12306.android.data.repository.TicketRepository;
import com.ticket12306.android.util.NotificationHelper;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class TicketCheckWorker_Factory {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  private final Provider<BookingTaskDao> bookingTaskDaoProvider;

  private final Provider<BookingLogDao> bookingLogDaoProvider;

  private final Provider<TicketMonitor> ticketMonitorProvider;

  private final Provider<BookingManager> bookingManagerProvider;

  public TicketCheckWorker_Factory(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider, Provider<TicketMonitor> ticketMonitorProvider,
      Provider<BookingManager> bookingManagerProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.notificationHelperProvider = notificationHelperProvider;
    this.bookingTaskDaoProvider = bookingTaskDaoProvider;
    this.bookingLogDaoProvider = bookingLogDaoProvider;
    this.ticketMonitorProvider = ticketMonitorProvider;
    this.bookingManagerProvider = bookingManagerProvider;
  }

  public TicketCheckWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, ticketRepositoryProvider.get(), notificationHelperProvider.get(), bookingTaskDaoProvider.get(), bookingLogDaoProvider.get(), ticketMonitorProvider.get(), bookingManagerProvider.get());
  }

  public static TicketCheckWorker_Factory create(
      Provider<TicketRepository> ticketRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider,
      Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider, Provider<TicketMonitor> ticketMonitorProvider,
      Provider<BookingManager> bookingManagerProvider) {
    return new TicketCheckWorker_Factory(ticketRepositoryProvider, notificationHelperProvider, bookingTaskDaoProvider, bookingLogDaoProvider, ticketMonitorProvider, bookingManagerProvider);
  }

  public static TicketCheckWorker newInstance(Context context, WorkerParameters workerParams,
      TicketRepository ticketRepository, NotificationHelper notificationHelper,
      BookingTaskDao bookingTaskDao, BookingLogDao bookingLogDao, TicketMonitor ticketMonitor,
      BookingManager bookingManager) {
    return new TicketCheckWorker(context, workerParams, ticketRepository, notificationHelper, bookingTaskDao, bookingLogDao, ticketMonitor, bookingManager);
  }
}
