package com.ticket12306.android.di;

import com.ticket12306.android.data.local.dao.BookingLogDao;
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
public final class AppModule_ProvideBookingLogDaoFactory implements Factory<BookingLogDao> {
  private final Provider<AppDatabase> databaseProvider;

  public AppModule_ProvideBookingLogDaoFactory(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  @Override
  public BookingLogDao get() {
    return provideBookingLogDao(databaseProvider.get());
  }

  public static AppModule_ProvideBookingLogDaoFactory create(
      Provider<AppDatabase> databaseProvider) {
    return new AppModule_ProvideBookingLogDaoFactory(databaseProvider);
  }

  public static BookingLogDao provideBookingLogDao(AppDatabase database) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideBookingLogDao(database));
  }
}
