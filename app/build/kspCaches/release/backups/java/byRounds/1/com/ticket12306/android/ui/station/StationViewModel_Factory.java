package com.ticket12306.android.ui.station;

import com.ticket12306.android.data.repository.StationRepository;
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
public final class StationViewModel_Factory implements Factory<StationViewModel> {
  private final Provider<StationRepository> stationRepositoryProvider;

  public StationViewModel_Factory(Provider<StationRepository> stationRepositoryProvider) {
    this.stationRepositoryProvider = stationRepositoryProvider;
  }

  @Override
  public StationViewModel get() {
    return newInstance(stationRepositoryProvider.get());
  }

  public static StationViewModel_Factory create(
      Provider<StationRepository> stationRepositoryProvider) {
    return new StationViewModel_Factory(stationRepositoryProvider);
  }

  public static StationViewModel newInstance(StationRepository stationRepository) {
    return new StationViewModel(stationRepository);
  }
}
