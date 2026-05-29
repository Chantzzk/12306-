package com.ticket12306.android.ui.profile;

import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.repository.UserRepository;
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
public final class PassengerManageViewModel_Factory implements Factory<PassengerManageViewModel> {
  private final Provider<AppDatabase> databaseProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public PassengerManageViewModel_Factory(Provider<AppDatabase> databaseProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.databaseProvider = databaseProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public PassengerManageViewModel get() {
    return newInstance(databaseProvider.get(), userRepositoryProvider.get());
  }

  public static PassengerManageViewModel_Factory create(Provider<AppDatabase> databaseProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new PassengerManageViewModel_Factory(databaseProvider, userRepositoryProvider);
  }

  public static PassengerManageViewModel newInstance(AppDatabase database,
      UserRepository userRepository) {
    return new PassengerManageViewModel(database, userRepository);
  }
}
