package com.ticket12306.android;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.InstanceFactory;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class BookingWorker_AssistedFactory_Impl implements BookingWorker_AssistedFactory {
  private final BookingWorker_Factory delegateFactory;

  BookingWorker_AssistedFactory_Impl(BookingWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public BookingWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<BookingWorker_AssistedFactory> create(
      BookingWorker_Factory delegateFactory) {
    return InstanceFactory.create(new BookingWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<BookingWorker_AssistedFactory> createFactoryProvider(
      BookingWorker_Factory delegateFactory) {
    return InstanceFactory.create(new BookingWorker_AssistedFactory_Impl(delegateFactory));
  }
}
