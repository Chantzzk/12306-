package com.ticket12306.android.booking;

import com.ticket12306.android.data.local.dao.BookingLogDao;
import com.ticket12306.android.data.local.dao.BookingTaskDao;
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
public final class BookingStateManager_Factory implements Factory<BookingStateManager> {
  private final Provider<BookingTaskDao> bookingTaskDaoProvider;

  private final Provider<BookingLogDao> bookingLogDaoProvider;

  private final Provider<BookingManager> bookingManagerProvider;

  public BookingStateManager_Factory(Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider,
      Provider<BookingManager> bookingManagerProvider) {
    this.bookingTaskDaoProvider = bookingTaskDaoProvider;
    this.bookingLogDaoProvider = bookingLogDaoProvider;
    this.bookingManagerProvider = bookingManagerProvider;
  }

  @Override
  public BookingStateManager get() {
    return newInstance(bookingTaskDaoProvider.get(), bookingLogDaoProvider.get(), bookingManagerProvider.get());
  }

  public static BookingStateManager_Factory create(Provider<BookingTaskDao> bookingTaskDaoProvider,
      Provider<BookingLogDao> bookingLogDaoProvider,
      Provider<BookingManager> bookingManagerProvider) {
    return new BookingStateManager_Factory(bookingTaskDaoProvider, bookingLogDaoProvider, bookingManagerProvider);
  }

  public static BookingStateManager newInstance(BookingTaskDao bookingTaskDao,
      BookingLogDao bookingLogDao, BookingManager bookingManager) {
    return new BookingStateManager(bookingTaskDao, bookingLogDao, bookingManager);
  }
}
