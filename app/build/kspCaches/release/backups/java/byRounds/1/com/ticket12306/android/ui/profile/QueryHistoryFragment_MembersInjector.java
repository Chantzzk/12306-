package com.ticket12306.android.ui.profile;

import com.ticket12306.android.data.local.database.AppDatabase;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class QueryHistoryFragment_MembersInjector implements MembersInjector<QueryHistoryFragment> {
  private final Provider<AppDatabase> databaseProvider;

  public QueryHistoryFragment_MembersInjector(Provider<AppDatabase> databaseProvider) {
    this.databaseProvider = databaseProvider;
  }

  public static MembersInjector<QueryHistoryFragment> create(
      Provider<AppDatabase> databaseProvider) {
    return new QueryHistoryFragment_MembersInjector(databaseProvider);
  }

  @Override
  public void injectMembers(QueryHistoryFragment instance) {
    injectDatabase(instance, databaseProvider.get());
  }

  @InjectedFieldSignature("com.ticket12306.android.ui.profile.QueryHistoryFragment.database")
  public static void injectDatabase(QueryHistoryFragment instance, AppDatabase database) {
    instance.database = database;
  }
}
