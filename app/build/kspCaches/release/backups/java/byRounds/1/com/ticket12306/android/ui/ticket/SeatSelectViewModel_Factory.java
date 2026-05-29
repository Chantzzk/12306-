package com.ticket12306.android.ui.ticket;

import androidx.lifecycle.SavedStateHandle;
import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.repository.TicketRepository;
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
public final class SeatSelectViewModel_Factory implements Factory<SeatSelectViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<TicketRepository> ticketRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  private final Provider<AppDatabase> databaseProvider;

  public SeatSelectViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<TicketRepository> ticketRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider, Provider<AppDatabase> databaseProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.ticketRepositoryProvider = ticketRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
    this.databaseProvider = databaseProvider;
  }

  @Override
  public SeatSelectViewModel get() {
    return newInstance(savedStateHandleProvider.get(), ticketRepositoryProvider.get(), userRepositoryProvider.get(), databaseProvider.get());
  }

  public static SeatSelectViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<TicketRepository> ticketRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider, Provider<AppDatabase> databaseProvider) {
    return new SeatSelectViewModel_Factory(savedStateHandleProvider, ticketRepositoryProvider, userRepositoryProvider, databaseProvider);
  }

  public static SeatSelectViewModel newInstance(SavedStateHandle savedStateHandle,
      TicketRepository ticketRepository, UserRepository userRepository, AppDatabase database) {
    return new SeatSelectViewModel(savedStateHandle, ticketRepository, userRepository, database);
  }
}
