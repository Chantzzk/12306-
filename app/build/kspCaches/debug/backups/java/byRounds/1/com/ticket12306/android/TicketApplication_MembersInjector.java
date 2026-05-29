package com.ticket12306.android;

import androidx.hilt.work.HiltWorkerFactory;
import com.ticket12306.android.booking.BookingStateManager;
import com.ticket12306.android.data.local.preferences.UserPreferences;
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
public final class TicketApplication_MembersInjector implements MembersInjector<TicketApplication> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  private final Provider<BookingStateManager> bookingStateManagerProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public TicketApplication_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<BookingStateManager> bookingStateManagerProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
    this.bookingStateManagerProvider = bookingStateManagerProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  public static MembersInjector<TicketApplication> create(
      Provider<HiltWorkerFactory> workerFactoryProvider,
      Provider<BookingStateManager> bookingStateManagerProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new TicketApplication_MembersInjector(workerFactoryProvider, bookingStateManagerProvider, userPreferencesProvider);
  }

  @Override
  public void injectMembers(TicketApplication instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
    injectBookingStateManager(instance, bookingStateManagerProvider.get());
    injectUserPreferences(instance, userPreferencesProvider.get());
  }

  @InjectedFieldSignature("com.ticket12306.android.TicketApplication.workerFactory")
  public static void injectWorkerFactory(TicketApplication instance,
      HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }

  @InjectedFieldSignature("com.ticket12306.android.TicketApplication.bookingStateManager")
  public static void injectBookingStateManager(TicketApplication instance,
      BookingStateManager bookingStateManager) {
    instance.bookingStateManager = bookingStateManager;
  }

  @InjectedFieldSignature("com.ticket12306.android.TicketApplication.userPreferences")
  public static void injectUserPreferences(TicketApplication instance,
      UserPreferences userPreferences) {
    instance.userPreferences = userPreferences;
  }
}
