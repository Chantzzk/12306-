package com.ticket12306.android;

import android.content.Context;
import androidx.work.WorkerParameters;
import com.ticket12306.android.booking.BookingManager;
import com.ticket12306.android.booking.BookingStateManager;
import com.ticket12306.android.booking.TicketMonitor;
import com.ticket12306.android.data.local.dao.BookingLogDao;
import com.ticket12306.android.data.local.dao.BookingTaskDao;
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
public final class BookingWorker_Factory {
  private final Provider<BookingTaskDao> bookingTaskDaoProvider;

  private final Provider<BookingLogDao> bookingLogDaoProvider;

  private final Provider<BookingManager> bookingManagerProvider;

  private final Provider<TicketMonitor> ticketMonitorProvider;

  private final Provider<BookingStateManager> bookingStateManagerProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public BookingWorker_Factory(Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider,
      Provider<BookingManager> bookingManagerProvider,
      Provider<TicketMonitor> ticketMonitorProvider,
      Provider<BookingStateManager> bookingStateManagerProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.bookingTaskDaoProvider = bookingTaskDaoProvider;
    this.bookingLogDaoProvider = bookingLogDaoProvider;
    this.bookingManagerProvider = bookingManagerProvider;
    this.ticketMonitorProvider = ticketMonitorProvider;
    this.bookingStateManagerProvider = bookingStateManagerProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  public BookingWorker get(Context context, WorkerParameters workerParams) {
    return newInstance(context, workerParams, bookingTaskDaoProvider.get(), bookingLogDaoProvider.get(), bookingManagerProvider.get(), ticketMonitorProvider.get(), bookingStateManagerProvider.get(), notificationHelperProvider.get());
  }

  public static BookingWorker_Factory create(Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider,
      Provider<BookingManager> bookingManagerProvider,
      Provider<TicketMonitor> ticketMonitorProvider,
      Provider<BookingStateManager> bookingStateManagerProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new BookingWorker_Factory(bookingTaskDaoProvider, bookingLogDaoProvider, bookingManagerProvider, ticketMonitorProvider, bookingStateManagerProvider, notificationHelperProvider);
  }

  public static BookingWorker newInstance(Context context, WorkerParameters workerParams,
      BookingTaskDao bookingTaskDao, BookingLogDao bookingLogDao, BookingManager bookingManager,
      TicketMonitor ticketMonitor, BookingStateManager bookingStateManager,
      NotificationHelper notificationHelper) {
    return new BookingWorker(context, workerParams, bookingTaskDao, bookingLogDao, bookingManager, ticketMonitor, bookingStateManager, notificationHelper);
  }
}
