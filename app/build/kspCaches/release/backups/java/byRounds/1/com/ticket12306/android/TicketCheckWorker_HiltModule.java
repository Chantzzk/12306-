package com.ticket12306.android;

import androidx.hilt.work.WorkerAssistedFactory;
import androidx.work.ListenableWorker;
import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.codegen.OriginatingElement;
import dagger.hilt.components.SingletonComponent;
import dagger.multibindings.IntoMap;
import dagger.multibindings.StringKey;
import javax.annotation.processing.Generated;

@Generated("androidx.hilt.AndroidXHiltProcessor")
@Module
@InstallIn(SingletonComponent.class)
@OriginatingElement(
    topLevelClass = TicketCheckWorker.class
)
public interface TicketCheckWorker_HiltModule {
  @Binds
  @IntoMap
  @StringKey("com.ticket12306.android.TicketCheckWorker")
  WorkerAssistedFactory<? extends ListenableWorker> bind(TicketCheckWorker_AssistedFactory factory);
}
