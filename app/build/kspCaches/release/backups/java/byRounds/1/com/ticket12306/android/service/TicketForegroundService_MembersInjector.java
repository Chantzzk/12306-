package com.ticket12306.android.service;

import com.ticket12306.android.data.repository.TicketRepository;
import com.ticket12306.android.util.NotificationHelper;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class TicketForegroundService_MembersInjector implements MembersInjector<TicketForegroundService> {
  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<NotificationHelper> notificationHelperProvider;

  public TicketForegroundService_MembersInjector(
      Provider<TicketRepository> ticketRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.notificationHelperProvider = notificationHelperProvider;
  }

  public static MembersInjector<TicketForegroundService> create(
      Provider<TicketRepository> ticketRepositoryProvider,
      Provider<NotificationHelper> notificationHelperProvider) {
    return new TicketForegroundService_MembersInjector(ticketRepositoryProvider, notificationHelperProvider);
  }

  @Override
  public void injectMembers(TicketForegroundService instance) {
    injectTicketRepository(instance, ticketRepositoryProvider.get());
    injectNotificationHelper(instance, notificationHelperProvider.get());
  }

  @InjectedFieldSignature("com.ticket12306.android.service.TicketForegroundService.ticketRepository")
  public static void injectTicketRepository(TicketForegroundService instance,
      TicketRepository ticketRepository) {
    instance.ticketRepository = ticketRepository;
  }

  @InjectedFieldSignature("com.ticket12306.android.service.TicketForegroundService.notificationHelper")
  public static void injectNotificationHelper(TicketForegroundService instance,
      NotificationHelper notificationHelper) {
    instance.notificationHelper = notificationHelper;
  }
}
