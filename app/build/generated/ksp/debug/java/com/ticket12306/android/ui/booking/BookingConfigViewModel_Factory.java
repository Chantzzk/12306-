package com.ticket12306.android.ui.booking;

import androidx.lifecycle.SavedStateHandle;
import com.ticket12306.android.booking.BookingStateManager;
import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.repository.TicketRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
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
public final class BookingConfigViewModel_Factory implements Factory<BookingConfigViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<BookingStateManager> bookingStateManagerProvider;

  private final Provider<AppDatabase> databaseProvider;

  public BookingConfigViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingStateManager> bookingStateManagerProvider,
      Provider<AppDatabase> databaseProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.bookingStateManagerProvider = bookingStateManagerProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BookingConfigViewModel get() {
    return newInstance(savedStateHandleProvider.get(), ticketRepositoryProvider.get(), bookingStateManagerProvider.get(), databaseProvider.get());
  }

  public static BookingConfigViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingStateManager> bookingStateManagerProvider,
      Provider<AppDatabase> databaseProvider) {
    return new BookingConfigViewModel_Factory(savedStateHandleProvider, ticketRepositoryProvider, bookingStateManagerProvider, databaseProvider);
  }

  public static BookingConfigViewModel newInstance(SavedStateHandle savedStateHandle,
      TicketRepository ticketRepository, BookingStateManager bookingStateManager,
      AppDatabase database) {
    return new BookingConfigViewModel(savedStateHandle, ticketRepository, bookingStateManager, database);
  }
}
