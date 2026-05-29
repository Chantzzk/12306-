package com.ticket12306.android.di;

import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.local.preferences.UserPreferences;
import com.ticket12306.android.data.remote.api.StationApi;
import com.ticket12306.android.data.repository.StationRepository;
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
public final class AppModule_ProvideStationRepositoryFactory implements Factory<StationRepository> {
  private final Provider<StationApi> stationApiProvider;

  private final Provider<AppDatabase> databaseProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public AppModule_ProvideStationRepositoryFactory(Provider<StationApi> stationApiProvider,
      Provider<AppDatabase> databaseProvider, Provider<UserPreferences> userPreferencesProvider) {
    this.stationApiProvider = stationApiProvider;
    this.databaseProvider = databaseProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public StationRepository get() {
    return provideStationRepository(stationApiProvider.get(), databaseProvider.get(), userPreferencesProvider.get());
  }

  public static AppModule_ProvideStationRepositoryFactory create(
      Provider<StationApi> stationApiProvider, Provider<AppDatabase> databaseProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new AppModule_ProvideStationRepositoryFactory(stationApiProvider, databaseProvider, userPreferencesProvider);
  }

  public static StationRepository provideStationRepository(StationApi stationApi,
      AppDatabase database, UserPreferences userPreferences) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideStationRepository(stationApi, database, userPreferences));
  }
}
