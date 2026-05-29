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
public final class TicketCheckWorker_AssistedFactory_Impl implements TicketCheckWorker_AssistedFactory {
  private final TicketCheckWorker_Factory delegateFactory;

  TicketCheckWorker_AssistedFactory_Impl(TicketCheckWorker_Factory delegateFactory) {
    this.delegateFactory = delegateFactory;
  }

  @Override
  public TicketCheckWorker create(Context p0, WorkerParameters p1) {
    return delegateFactory.get(p0, p1);
  }

  public static Provider<TicketCheckWorker_AssistedFactory> create(
      TicketCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new TicketCheckWorker_AssistedFactory_Impl(delegateFactory));
  }

  public static dagger.internal.Provider<TicketCheckWorker_AssistedFactory> createFactoryProvider(
      TicketCheckWorker_Factory delegateFactory) {
    return InstanceFactory.create(new TicketCheckWorker_AssistedFactory_Impl(delegateFactory));
  }
}
