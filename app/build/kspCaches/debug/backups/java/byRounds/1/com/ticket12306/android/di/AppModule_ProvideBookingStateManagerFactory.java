package com.ticket12306.android.di;

import com.ticket12306.android.booking.BookingManager;
import com.ticket12306.android.booking.BookingStateManager;
import com.ticket12306.android.data.local.database.AppDatabase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
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
public final class AppModule_ProvideBookingStateManagerFactory implements Factory<BookingStateManager> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<BookingManager> bookingManagerProvider;

  public AppModule_ProvideBookingStateManagerFactory(Provider<AppDatabase> databaseProvider,
      Provider<BookingManager> bookingManagerProvider) {
    this.databaseProvider = databaseProvider;
    this.bookingManagerProvider = bookingManagerProvider;
  }

  @Override
  public BookingStateManager get() {
    return provideBookingStateManager(databaseProvider.get(), bookingManagerProvider.get());
  }

  public static AppModule_ProvideBookingStateManagerFactory create(
      Provider<AppDatabase> databaseProvider, Provider<BookingManager> bookingManagerProvider) {
    return new AppModule_ProvideBookingStateManagerFactory(databaseProvider, bookingManagerProvider);
  }

  public static BookingStateManager provideBookingStateManager(AppDatabase database,
      BookingManager bookingManager) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBookingStateManager(database, bookingManager));
  }
}
