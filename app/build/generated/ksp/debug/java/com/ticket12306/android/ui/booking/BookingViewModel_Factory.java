package com.ticket12306.android.ui.booking;

import com.ticket12306.android.booking.BookingStateManager;
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
public final class BookingViewModel_Factory implements Factory<BookingViewModel> {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<BookingStateManager> bookingStateManagerProvider;

  public BookingViewModel_Factory(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingStateManager> bookingStateManagerProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.bookingStateManagerProvider = bookingStateManagerProvider;
  }

  @Override
  public BookingViewModel get() {
    return newInstance(ticketRepositoryProvider.get(), bookingStateManagerProvider.get());
  }

  public static BookingViewModel_Factory create(Provider<TicketRepository> ticketRepositoryProvider,
      Provider<BookingStateManager> bookingStateManagerProvider) {
    return new BookingViewModel_Factory(ticketRepositoryProvider, bookingStateManagerProvider);
  }

  public static BookingViewModel newInstance(TicketRepository ticketRepository,
      BookingStateManager bookingStateManager) {
    return new BookingViewModel(ticketRepository, bookingStateManager);
  }
}
