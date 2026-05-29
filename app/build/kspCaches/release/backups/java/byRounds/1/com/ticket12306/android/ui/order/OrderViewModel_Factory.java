package com.ticket12306.android.ui.order;

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
public final class OrderViewModel_Factory implements Factory<OrderViewModel> {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  public OrderViewModel_Factory(Provider<TicketRepository> ticketRepositoryProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
  }

  @Override
  public OrderViewModel get() {
    return newInstance(ticketRepositoryProvider.get());
  }

  public static OrderViewModel_Factory create(Provider<TicketRepository> ticketRepositoryProvider) {
    return new OrderViewModel_Factory(ticketRepositoryProvider);
  }

  public static OrderViewModel newInstance(TicketRepository ticketRepository) {
    return new OrderViewModel(ticketRepository);
  }
}
