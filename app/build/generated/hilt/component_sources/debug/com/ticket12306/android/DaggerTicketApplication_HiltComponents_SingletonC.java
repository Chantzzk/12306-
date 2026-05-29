package com.ticket12306.android;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.ticket12306.android.booking.BookingManager;
import com.ticket12306.android.booking.BookingStateManager;
import com.ticket12306.android.booking.TicketMonitor;
import com.ticket12306.android.data.local.dao.BookingLogDao;
import com.ticket12306.android.data.local.dao.BookingTaskDao;
import com.ticket12306.android.data.local.database.AppDatabase;
import com.ticket12306.android.data.local.preferences.UserPreferences;
import com.ticket12306.android.data.remote.api.OrderApi;
import com.ticket12306.android.data.remote.api.StationApi;
import com.ticket12306.android.data.remote.api.TicketApi;
import com.ticket12306.android.data.remote.api.UserApi;
import com.ticket12306.android.data.repository.StationRepository;
import com.ticket12306.android.data.repository.TicketRepository;
import com.ticket12306.android.data.repository.UserRepository;
import com.ticket12306.android.di.AppModule_ProvideAppDatabaseFactory;
import com.ticket12306.android.di.AppModule_ProvideBookingLogDaoFactory;
import com.ticket12306.android.di.AppModule_ProvideBookingManagerFactory;
import com.ticket12306.android.di.AppModule_ProvideBookingStateManagerFactory;
import com.ticket12306.android.di.AppModule_ProvideBookingTaskDaoFactory;
import com.ticket12306.android.di.AppModule_ProvideNotificationHelperFactory;
import com.ticket12306.android.di.AppModule_ProvideOrderApiFactory;
import com.ticket12306.android.di.AppModule_ProvideStationApiFactory;
import com.ticket12306.android.di.AppModule_ProvideStationRepositoryFactory;
import com.ticket12306.android.di.AppModule_ProvideTicketApiFactory;
import com.ticket12306.android.di.AppModule_ProvideTicketMonitorFactory;
import com.ticket12306.android.di.AppModule_ProvideTicketRepositoryFactory;
import com.ticket12306.android.di.AppModule_ProvideUserApiFactory;
import com.ticket12306.android.di.AppModule_ProvideUserPreferencesFactory;
import com.ticket12306.android.di.AppModule_ProvideUserRepositoryFactory;
import com.ticket12306.android.service.TicketForegroundService;
import com.ticket12306.android.service.TicketForegroundService_MembersInjector;
import com.ticket12306.android.ui.booking.BookingConfigFragment;
import com.ticket12306.android.ui.booking.BookingConfigViewModel;
import com.ticket12306.android.ui.booking.BookingConfigViewModel_HiltModules;
import com.ticket12306.android.ui.booking.BookingConfigViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.booking.BookingConfigViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.booking.BookingFragment;
import com.ticket12306.android.ui.booking.BookingViewModel;
import com.ticket12306.android.ui.booking.BookingViewModel_HiltModules;
import com.ticket12306.android.ui.booking.BookingViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.booking.BookingViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.login.LoginActivity;
import com.ticket12306.android.ui.login.LoginViewModel;
import com.ticket12306.android.ui.login.LoginViewModel_HiltModules;
import com.ticket12306.android.ui.login.LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.login.LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.main.MainActivity;
import com.ticket12306.android.ui.main.TicketFragment;
import com.ticket12306.android.ui.order.OrderDetailFragment;
import com.ticket12306.android.ui.order.OrderFragment;
import com.ticket12306.android.ui.order.OrderViewModel;
import com.ticket12306.android.ui.order.OrderViewModel_HiltModules;
import com.ticket12306.android.ui.order.OrderViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.order.OrderViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.profile.AboutFragment;
import com.ticket12306.android.ui.profile.PassengerManageFragment;
import com.ticket12306.android.ui.profile.PassengerManageViewModel;
import com.ticket12306.android.ui.profile.PassengerManageViewModel_HiltModules;
import com.ticket12306.android.ui.profile.PassengerManageViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.profile.PassengerManageViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.profile.ProfileFragment;
import com.ticket12306.android.ui.profile.QueryHistoryFragment;
import com.ticket12306.android.ui.profile.QueryHistoryFragment_MembersInjector;
import com.ticket12306.android.ui.profile.SettingsFragment;
import com.ticket12306.android.ui.profile.SettingsViewModel;
import com.ticket12306.android.ui.profile.SettingsViewModel_HiltModules;
import com.ticket12306.android.ui.profile.SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.profile.SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.station.StationFragment;
import com.ticket12306.android.ui.station.StationSelectFragment;
import com.ticket12306.android.ui.station.StationViewModel;
import com.ticket12306.android.ui.station.StationViewModel_HiltModules;
import com.ticket12306.android.ui.station.StationViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.station.StationViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.ticket.SeatSelectFragment;
import com.ticket12306.android.ui.ticket.SeatSelectViewModel;
import com.ticket12306.android.ui.ticket.SeatSelectViewModel_HiltModules;
import com.ticket12306.android.ui.ticket.SeatSelectViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.ticket.SeatSelectViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.ui.ticket.TicketDetailFragment;
import com.ticket12306.android.ui.ticket.TicketViewModel;
import com.ticket12306.android.ui.ticket.TicketViewModel_HiltModules;
import com.ticket12306.android.ui.ticket.TicketViewModel_HiltModules_BindsModule_Binds_LazyMapKey;
import com.ticket12306.android.ui.ticket.TicketViewModel_HiltModules_KeyModule_Provide_LazyMapKey;
import com.ticket12306.android.util.NotificationHelper;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerTicketApplication_HiltComponents_SingletonC {
  private DaggerTicketApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public TicketApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements TicketApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public TicketApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements TicketApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public TicketApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements TicketApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public TicketApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements TicketApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TicketApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements TicketApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public TicketApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements TicketApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public TicketApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements TicketApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public TicketApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends TicketApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends TicketApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public void injectBookingConfigFragment(BookingConfigFragment bookingConfigFragment) {
    }

    @Override
    public void injectBookingFragment(BookingFragment bookingFragment) {
    }

    @Override
    public void injectTicketFragment(TicketFragment ticketFragment) {
    }

    @Override
    public void injectOrderDetailFragment(OrderDetailFragment orderDetailFragment) {
    }

    @Override
    public void injectOrderFragment(OrderFragment orderFragment) {
    }

    @Override
    public void injectAboutFragment(AboutFragment aboutFragment) {
    }

    @Override
    public void injectPassengerManageFragment(PassengerManageFragment passengerManageFragment) {
    }

    @Override
    public void injectProfileFragment(ProfileFragment profileFragment) {
    }

    @Override
    public void injectQueryHistoryFragment(QueryHistoryFragment queryHistoryFragment) {
      injectQueryHistoryFragment2(queryHistoryFragment);
    }

    @Override
    public void injectSettingsFragment(SettingsFragment settingsFragment) {
    }

    @Override
    public void injectStationFragment(StationFragment stationFragment) {
    }

    @Override
    public void injectStationSelectFragment(StationSelectFragment stationSelectFragment) {
    }

    @Override
    public void injectSeatSelectFragment(SeatSelectFragment seatSelectFragment) {
    }

    @Override
    public void injectTicketDetailFragment(TicketDetailFragment ticketDetailFragment) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }

    @CanIgnoreReturnValue
    private QueryHistoryFragment injectQueryHistoryFragment2(QueryHistoryFragment instance) {
      QueryHistoryFragment_MembersInjector.injectDatabase(instance, singletonCImpl.provideAppDatabaseProvider.get());
      return instance;
    }
  }

  private static final class ViewCImpl extends TicketApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends TicketApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectLoginActivity(LoginActivity loginActivity) {
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(9).put(BookingConfigViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, BookingConfigViewModel_HiltModules.KeyModule.provide()).put(BookingViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, BookingViewModel_HiltModules.KeyModule.provide()).put(LoginViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, LoginViewModel_HiltModules.KeyModule.provide()).put(OrderViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, OrderViewModel_HiltModules.KeyModule.provide()).put(PassengerManageViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, PassengerManageViewModel_HiltModules.KeyModule.provide()).put(SeatSelectViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SeatSelectViewModel_HiltModules.KeyModule.provide()).put(SettingsViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, SettingsViewModel_HiltModules.KeyModule.provide()).put(StationViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, StationViewModel_HiltModules.KeyModule.provide()).put(TicketViewModel_HiltModules_KeyModule_Provide_LazyMapKey.lazyClassKeyName, TicketViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }
  }

  private static final class ViewModelCImpl extends TicketApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<BookingConfigViewModel> bookingConfigViewModelProvider;

    private Provider<BookingViewModel> bookingViewModelProvider;

    private Provider<LoginViewModel> loginViewModelProvider;

    private Provider<OrderViewModel> orderViewModelProvider;

    private Provider<PassengerManageViewModel> passengerManageViewModelProvider;

    private Provider<SeatSelectViewModel> seatSelectViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<StationViewModel> stationViewModelProvider;

    private Provider<TicketViewModel> ticketViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.bookingConfigViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.bookingViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.loginViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.orderViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.passengerManageViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.seatSelectViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.stationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.ticketViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(9).put(BookingConfigViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) bookingConfigViewModelProvider)).put(BookingViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) bookingViewModelProvider)).put(LoginViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) loginViewModelProvider)).put(OrderViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) orderViewModelProvider)).put(PassengerManageViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) passengerManageViewModelProvider)).put(SeatSelectViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) seatSelectViewModelProvider)).put(SettingsViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) settingsViewModelProvider)).put(StationViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) stationViewModelProvider)).put(TicketViewModel_HiltModules_BindsModule_Binds_LazyMapKey.lazyClassKeyName, ((Provider) ticketViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ticket12306.android.ui.booking.BookingConfigViewModel 
          return (T) new BookingConfigViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.provideTicketRepositoryProvider.get(), singletonCImpl.provideBookingStateManagerProvider.get(), singletonCImpl.provideAppDatabaseProvider.get());

          case 1: // com.ticket12306.android.ui.booking.BookingViewModel 
          return (T) new BookingViewModel(singletonCImpl.provideTicketRepositoryProvider.get(), singletonCImpl.provideBookingStateManagerProvider.get());

          case 2: // com.ticket12306.android.ui.login.LoginViewModel 
          return (T) new LoginViewModel(singletonCImpl.provideUserRepositoryProvider.get());

          case 3: // com.ticket12306.android.ui.order.OrderViewModel 
          return (T) new OrderViewModel(singletonCImpl.provideTicketRepositoryProvider.get());

          case 4: // com.ticket12306.android.ui.profile.PassengerManageViewModel 
          return (T) new PassengerManageViewModel(singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.provideUserRepositoryProvider.get());

          case 5: // com.ticket12306.android.ui.ticket.SeatSelectViewModel 
          return (T) new SeatSelectViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.provideTicketRepositoryProvider.get(), singletonCImpl.provideUserRepositoryProvider.get(), singletonCImpl.provideAppDatabaseProvider.get());

          case 6: // com.ticket12306.android.ui.profile.SettingsViewModel 
          return (T) new SettingsViewModel(singletonCImpl.provideUserPreferencesProvider.get());

          case 7: // com.ticket12306.android.ui.station.StationViewModel 
          return (T) new StationViewModel(singletonCImpl.provideStationRepositoryProvider.get());

          case 8: // com.ticket12306.android.ui.ticket.TicketViewModel 
          return (T) new TicketViewModel(singletonCImpl.provideTicketRepositoryProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends TicketApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends TicketApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectTicketForegroundService(TicketForegroundService ticketForegroundService) {
      injectTicketForegroundService2(ticketForegroundService);
    }

    @CanIgnoreReturnValue
    private TicketForegroundService injectTicketForegroundService2(
        TicketForegroundService instance) {
      TicketForegroundService_MembersInjector.injectTicketRepository(instance, singletonCImpl.provideTicketRepositoryProvider.get());
      TicketForegroundService_MembersInjector.injectNotificationHelper(instance, singletonCImpl.provideNotificationHelperProvider.get());
      return instance;
    }
  }

  private static final class SingletonCImpl extends TicketApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<AppDatabase> provideAppDatabaseProvider;

    private Provider<BookingTaskDao> provideBookingTaskDaoProvider;

    private Provider<BookingLogDao> provideBookingLogDaoProvider;

    private Provider<TicketApi> provideTicketApiProvider;

    private Provider<OrderApi> provideOrderApiProvider;

    private Provider<UserPreferences> provideUserPreferencesProvider;

    private Provider<TicketRepository> provideTicketRepositoryProvider;

    private Provider<NotificationHelper> provideNotificationHelperProvider;

    private Provider<TicketMonitor> provideTicketMonitorProvider;

    private Provider<BookingManager> provideBookingManagerProvider;

    private Provider<BookingStateManager> provideBookingStateManagerProvider;

    private Provider<BookingWorker_AssistedFactory> bookingWorker_AssistedFactoryProvider;

    private Provider<TicketCheckWorker_AssistedFactory> ticketCheckWorker_AssistedFactoryProvider;

    private Provider<UserApi> provideUserApiProvider;

    private Provider<UserRepository> provideUserRepositoryProvider;

    private Provider<StationApi> provideStationApiProvider;

    private Provider<StationRepository> provideStationRepositoryProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private Map<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return MapBuilder.<String, javax.inject.Provider<WorkerAssistedFactory<? extends ListenableWorker>>>newMapBuilder(2).put("com.ticket12306.android.BookingWorker", ((Provider) bookingWorker_AssistedFactoryProvider)).put("com.ticket12306.android.TicketCheckWorker", ((Provider) ticketCheckWorker_AssistedFactoryProvider)).build();
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideAppDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<AppDatabase>(singletonCImpl, 2));
      this.provideBookingTaskDaoProvider = DoubleCheck.provider(new SwitchingProvider<BookingTaskDao>(singletonCImpl, 1));
      this.provideBookingLogDaoProvider = DoubleCheck.provider(new SwitchingProvider<BookingLogDao>(singletonCImpl, 3));
      this.provideTicketApiProvider = DoubleCheck.provider(new SwitchingProvider<TicketApi>(singletonCImpl, 6));
      this.provideOrderApiProvider = DoubleCheck.provider(new SwitchingProvider<OrderApi>(singletonCImpl, 7));
      this.provideUserPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<UserPreferences>(singletonCImpl, 8));
      this.provideTicketRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<TicketRepository>(singletonCImpl, 5));
      this.provideNotificationHelperProvider = DoubleCheck.provider(new SwitchingProvider<NotificationHelper>(singletonCImpl, 9));
      this.provideTicketMonitorProvider = DoubleCheck.provider(new SwitchingProvider<TicketMonitor>(singletonCImpl, 10));
      this.provideBookingManagerProvider = DoubleCheck.provider(new SwitchingProvider<BookingManager>(singletonCImpl, 4));
      this.provideBookingStateManagerProvider = DoubleCheck.provider(new SwitchingProvider<BookingStateManager>(singletonCImpl, 11));
      this.bookingWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<BookingWorker_AssistedFactory>(singletonCImpl, 0));
      this.ticketCheckWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<TicketCheckWorker_AssistedFactory>(singletonCImpl, 12));
      this.provideUserApiProvider = DoubleCheck.provider(new SwitchingProvider<UserApi>(singletonCImpl, 14));
      this.provideUserRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<UserRepository>(singletonCImpl, 13));
      this.provideStationApiProvider = DoubleCheck.provider(new SwitchingProvider<StationApi>(singletonCImpl, 16));
      this.provideStationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<StationRepository>(singletonCImpl, 15));
    }

    @Override
    public void injectTicketApplication(TicketApplication ticketApplication) {
      injectTicketApplication2(ticketApplication);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private TicketApplication injectTicketApplication2(TicketApplication instance) {
      TicketApplication_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      TicketApplication_MembersInjector.injectBookingStateManager(instance, provideBookingStateManagerProvider.get());
      TicketApplication_MembersInjector.injectUserPreferences(instance, provideUserPreferencesProvider.get());
      return instance;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.ticket12306.android.BookingWorker_AssistedFactory 
          return (T) new BookingWorker_AssistedFactory() {
            @Override
            public BookingWorker create(Context context, WorkerParameters workerParams) {
              return new BookingWorker(context, workerParams, singletonCImpl.provideBookingTaskDaoProvider.get(), singletonCImpl.provideBookingLogDaoProvider.get(), singletonCImpl.provideBookingManagerProvider.get(), singletonCImpl.provideTicketMonitorProvider.get(), singletonCImpl.provideBookingStateManagerProvider.get(), singletonCImpl.provideNotificationHelperProvider.get());
            }
          };

          case 1: // com.ticket12306.android.data.local.dao.BookingTaskDao 
          return (T) AppModule_ProvideBookingTaskDaoFactory.provideBookingTaskDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 2: // com.ticket12306.android.data.local.database.AppDatabase 
          return (T) AppModule_ProvideAppDatabaseFactory.provideAppDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.ticket12306.android.data.local.dao.BookingLogDao 
          return (T) AppModule_ProvideBookingLogDaoFactory.provideBookingLogDao(singletonCImpl.provideAppDatabaseProvider.get());

          case 4: // com.ticket12306.android.booking.BookingManager 
          return (T) AppModule_ProvideBookingManagerFactory.provideBookingManager(singletonCImpl.provideTicketRepositoryProvider.get(), singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.provideNotificationHelperProvider.get(), singletonCImpl.provideTicketMonitorProvider.get());

          case 5: // com.ticket12306.android.data.repository.TicketRepository 
          return (T) AppModule_ProvideTicketRepositoryFactory.provideTicketRepository(singletonCImpl.provideTicketApiProvider.get(), singletonCImpl.provideOrderApiProvider.get(), singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.provideUserPreferencesProvider.get());

          case 6: // com.ticket12306.android.data.remote.api.TicketApi 
          return (T) AppModule_ProvideTicketApiFactory.provideTicketApi();

          case 7: // com.ticket12306.android.data.remote.api.OrderApi 
          return (T) AppModule_ProvideOrderApiFactory.provideOrderApi();

          case 8: // com.ticket12306.android.data.local.preferences.UserPreferences 
          return (T) AppModule_ProvideUserPreferencesFactory.provideUserPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.ticket12306.android.util.NotificationHelper 
          return (T) AppModule_ProvideNotificationHelperFactory.provideNotificationHelper(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // com.ticket12306.android.booking.TicketMonitor 
          return (T) AppModule_ProvideTicketMonitorFactory.provideTicketMonitor(singletonCImpl.provideTicketRepositoryProvider.get(), singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.provideNotificationHelperProvider.get());

          case 11: // com.ticket12306.android.booking.BookingStateManager 
          return (T) AppModule_ProvideBookingStateManagerFactory.provideBookingStateManager(singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.provideBookingManagerProvider.get());

          case 12: // com.ticket12306.android.TicketCheckWorker_AssistedFactory 
          return (T) new TicketCheckWorker_AssistedFactory() {
            @Override
            public TicketCheckWorker create(Context context2, WorkerParameters workerParams2) {
              return new TicketCheckWorker(context2, workerParams2, singletonCImpl.provideTicketRepositoryProvider.get(), singletonCImpl.provideNotificationHelperProvider.get(), singletonCImpl.provideBookingTaskDaoProvider.get(), singletonCImpl.provideBookingLogDaoProvider.get(), singletonCImpl.provideTicketMonitorProvider.get(), singletonCImpl.provideBookingManagerProvider.get());
            }
          };

          case 13: // com.ticket12306.android.data.repository.UserRepository 
          return (T) AppModule_ProvideUserRepositoryFactory.provideUserRepository(singletonCImpl.provideUserApiProvider.get(), singletonCImpl.provideOrderApiProvider.get(), singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.provideUserPreferencesProvider.get());

          case 14: // com.ticket12306.android.data.remote.api.UserApi 
          return (T) AppModule_ProvideUserApiFactory.provideUserApi();

          case 15: // com.ticket12306.android.data.repository.StationRepository 
          return (T) AppModule_ProvideStationRepositoryFactory.provideStationRepository(singletonCImpl.provideStationApiProvider.get(), singletonCImpl.provideAppDatabaseProvider.get(), singletonCImpl.provideUserPreferencesProvider.get());

          case 16: // com.ticket12306.android.data.remote.api.StationApi 
          return (T) AppModule_ProvideStationApiFactory.provideStationApi();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
