package com.ticket12306.android.di;

import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.local.preferences.UserPreferences;
import com.ticket12306.android.data.remote.api.OrderApi;
import com.ticket12306.android.data.remote.api.UserApi;
import com.ticket12306.android.data.repository.UserRepository;
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
public final class AppModule_ProvideUserRepositoryFactory implements Factory<UserRepository> {
  private final Provider<UserApi> userApiProvider;

  private final Provider<OrderApi> orderApiProvider;

  private final Provider<AppDatabase> databaseProvider;

  private final Provider<UserPreferences> userPreferencesProvider;

  public AppModule_ProvideUserRepositoryFactory(Provider<UserApi> userApiProvider,
      Provider<OrderApi> orderApiProvider, Provider<AppDatabase> databaseProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    this.userApiProvider = userApiProvider;
    this.orderApiProvider = orderApiProvider;
    this.databaseProvider = databaseProvider;
    this.userPreferencesProvider = userPreferencesProvider;
  }

  @Override
  public UserRepository get() {
    return provideUserRepository(userApiProvider.get(), orderApiProvider.get(), databaseProvider.get(), userPreferencesProvider.get());
  }

  public static AppModule_ProvideUserRepositoryFactory create(Provider<UserApi> userApiProvider,
      Provider<OrderApi> orderApiProvider, Provider<AppDatabase> databaseProvider,
      Provider<UserPreferences> userPreferencesProvider) {
    return new AppModule_ProvideUserRepositoryFactory(userApiProvider, orderApiProvider, databaseProvider, userPreferencesProvider);
  }

  public static UserRepository provideUserRepository(UserApi userApi, OrderApi orderApi,
      AppDatabase database, UserPreferences userPreferences) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUserRepository(userApi, orderApi, database, userPreferences));
  }
}
