package com.ticket12306.android.data.local.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.ticket12306.android.data.local.dao.BookingLogDao;
import com.ticket12306.android.data.local.dao.BookingLogDao_Impl;
import com.ticket12306.android.data.local.dao.BookingTaskDao;
import com.ticket12306.android.data.local.dao.BookingTaskDao_Impl;
import com.ticket12306.android.data.local.dao.PassengerDao;
import com.ticket12306.android.data.local.dao.PassengerDao_Impl;
import com.ticket12306.android.data.local.dao.QueryHistoryDao;
import com.ticket12306.android.data.local.dao.QueryHistoryDao_Impl;
import com.ticket12306.android.data.local.dao.StationDao;
import com.ticket12306.android.data.local.dao.StationDao_Impl;
import com.ticket12306.android.data.local.dao.UserDao;
import com.ticket12306.android.data.local.dao.UserDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile StationDao _stationDao;

  private volatile PassengerDao _passengerDao;

  private volatile BookingTaskDao _bookingTaskDao;

  private volatile BookingLogDao _bookingLogDao;

  private volatile UserDao _userDao;

  private volatile QueryHistoryDao _queryHistoryDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `stations` (`code` TEXT NOT NULL, `name` TEXT NOT NULL, `pinyin` TEXT NOT NULL, `pinyinInitial` TEXT NOT NULL, `province` TEXT, `city` TEXT, PRIMARY KEY(`code`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `passengers` (`code` TEXT NOT NULL, `passenger_name` TEXT NOT NULL, `sex_code` TEXT NOT NULL, `sex_name` TEXT NOT NULL, `born_date` TEXT NOT NULL, `country_code` TEXT NOT NULL, `passenger_id_type_code` TEXT NOT NULL, `passenger_id_type_name` TEXT NOT NULL, `passenger_id_no` TEXT NOT NULL, `passenger_type` TEXT NOT NULL, `passenger_flag` TEXT NOT NULL, `passenger_name_en` TEXT NOT NULL, PRIMARY KEY(`code`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `booking_tasks` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `trainNumber` TEXT NOT NULL, `trainNo` TEXT NOT NULL, `departureStation` TEXT NOT NULL, `departureStationName` TEXT NOT NULL, `arrivalStation` TEXT NOT NULL, `arrivalStationName` TEXT NOT NULL, `departureDate` TEXT NOT NULL, `departureTime` TEXT NOT NULL, `arrivalTime` TEXT NOT NULL, `seatType` TEXT NOT NULL, `seatTypeName` TEXT NOT NULL, `passengerIds` TEXT NOT NULL, `passengerNames` TEXT NOT NULL, `autoBooking` INTEGER NOT NULL, `isActive` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL, `strategy` TEXT NOT NULL, `refreshInterval` INTEGER NOT NULL, `maxRetryCount` INTEGER NOT NULL, `seatPreferences` TEXT NOT NULL, `acceptWaitlist` INTEGER NOT NULL, `currentRetryCount` INTEGER NOT NULL, `status` TEXT NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `users` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `username` TEXT NOT NULL, `password` TEXT NOT NULL, `realName` TEXT NOT NULL, `idType` TEXT NOT NULL, `idNo` TEXT NOT NULL, `phone` TEXT NOT NULL, `email` TEXT NOT NULL, `token` TEXT NOT NULL, `uamtk` TEXT NOT NULL, `newapptk` TEXT NOT NULL, `isLoggedIn` INTEGER NOT NULL, `lastLoginTime` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `query_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `userId` INTEGER NOT NULL, `fromStationCode` TEXT NOT NULL, `fromStationName` TEXT NOT NULL, `toStationCode` TEXT NOT NULL, `toStationName` TEXT NOT NULL, `trainDate` TEXT NOT NULL, `queryTime` INTEGER NOT NULL, `queryType` TEXT NOT NULL, FOREIGN KEY(`userId`) REFERENCES `users`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_query_history_userId` ON `query_history` (`userId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_query_history_queryTime` ON `query_history` (`queryTime`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `booking_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `taskId` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `type` TEXT NOT NULL, `message` TEXT NOT NULL, `extraData` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'b6867d2a492c54ea0b989bb6b9d96be6')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `stations`");
        db.execSQL("DROP TABLE IF EXISTS `passengers`");
        db.execSQL("DROP TABLE IF EXISTS `booking_tasks`");
        db.execSQL("DROP TABLE IF EXISTS `users`");
        db.execSQL("DROP TABLE IF EXISTS `query_history`");
        db.execSQL("DROP TABLE IF EXISTS `booking_logs`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsStations = new HashMap<String, TableInfo.Column>(6);
        _columnsStations.put("code", new TableInfo.Column("code", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStations.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStations.put("pinyin", new TableInfo.Column("pinyin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStations.put("pinyinInitial", new TableInfo.Column("pinyinInitial", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStations.put("province", new TableInfo.Column("province", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsStations.put("city", new TableInfo.Column("city", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysStations = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesStations = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoStations = new TableInfo("stations", _columnsStations, _foreignKeysStations, _indicesStations);
        final TableInfo _existingStations = TableInfo.read(db, "stations");
        if (!_infoStations.equals(_existingStations)) {
          return new RoomOpenHelper.ValidationResult(false, "stations(com.ticket12306.android.data.model.Station).\n"
                  + " Expected:\n" + _infoStations + "\n"
                  + " Found:\n" + _existingStations);
        }
        final HashMap<String, TableInfo.Column> _columnsPassengers = new HashMap<String, TableInfo.Column>(12);
        _columnsPassengers.put("code", new TableInfo.Column("code", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("passenger_name", new TableInfo.Column("passenger_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("sex_code", new TableInfo.Column("sex_code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("sex_name", new TableInfo.Column("sex_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("born_date", new TableInfo.Column("born_date", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("country_code", new TableInfo.Column("country_code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("passenger_id_type_code", new TableInfo.Column("passenger_id_type_code", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("passenger_id_type_name", new TableInfo.Column("passenger_id_type_name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("passenger_id_no", new TableInfo.Column("passenger_id_no", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("passenger_type", new TableInfo.Column("passenger_type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("passenger_flag", new TableInfo.Column("passenger_flag", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPassengers.put("passenger_name_en", new TableInfo.Column("passenger_name_en", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPassengers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPassengers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPassengers = new TableInfo("passengers", _columnsPassengers, _foreignKeysPassengers, _indicesPassengers);
        final TableInfo _existingPassengers = TableInfo.read(db, "passengers");
        if (!_infoPassengers.equals(_existingPassengers)) {
          return new RoomOpenHelper.ValidationResult(false, "passengers(com.ticket12306.android.data.model.Passenger).\n"
                  + " Expected:\n" + _infoPassengers + "\n"
                  + " Found:\n" + _existingPassengers);
        }
        final HashMap<String, TableInfo.Column> _columnsBookingTasks = new HashMap<String, TableInfo.Column>(25);
        _columnsBookingTasks.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("trainNumber", new TableInfo.Column("trainNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("trainNo", new TableInfo.Column("trainNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("departureStation", new TableInfo.Column("departureStation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("departureStationName", new TableInfo.Column("departureStationName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("arrivalStation", new TableInfo.Column("arrivalStation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("arrivalStationName", new TableInfo.Column("arrivalStationName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("departureDate", new TableInfo.Column("departureDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("departureTime", new TableInfo.Column("departureTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("arrivalTime", new TableInfo.Column("arrivalTime", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("seatType", new TableInfo.Column("seatType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("seatTypeName", new TableInfo.Column("seatTypeName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("passengerIds", new TableInfo.Column("passengerIds", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("passengerNames", new TableInfo.Column("passengerNames", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("autoBooking", new TableInfo.Column("autoBooking", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("strategy", new TableInfo.Column("strategy", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("refreshInterval", new TableInfo.Column("refreshInterval", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("maxRetryCount", new TableInfo.Column("maxRetryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("seatPreferences", new TableInfo.Column("seatPreferences", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("acceptWaitlist", new TableInfo.Column("acceptWaitlist", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("currentRetryCount", new TableInfo.Column("currentRetryCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingTasks.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookingTasks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookingTasks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookingTasks = new TableInfo("booking_tasks", _columnsBookingTasks, _foreignKeysBookingTasks, _indicesBookingTasks);
        final TableInfo _existingBookingTasks = TableInfo.read(db, "booking_tasks");
        if (!_infoBookingTasks.equals(_existingBookingTasks)) {
          return new RoomOpenHelper.ValidationResult(false, "booking_tasks(com.ticket12306.android.data.model.BookingTask).\n"
                  + " Expected:\n" + _infoBookingTasks + "\n"
                  + " Found:\n" + _existingBookingTasks);
        }
        final HashMap<String, TableInfo.Column> _columnsUsers = new HashMap<String, TableInfo.Column>(15);
        _columnsUsers.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("username", new TableInfo.Column("username", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("password", new TableInfo.Column("password", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("realName", new TableInfo.Column("realName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("idType", new TableInfo.Column("idType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("idNo", new TableInfo.Column("idNo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("phone", new TableInfo.Column("phone", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("email", new TableInfo.Column("email", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("token", new TableInfo.Column("token", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("uamtk", new TableInfo.Column("uamtk", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("newapptk", new TableInfo.Column("newapptk", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("isLoggedIn", new TableInfo.Column("isLoggedIn", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("lastLoginTime", new TableInfo.Column("lastLoginTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsUsers.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysUsers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesUsers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoUsers = new TableInfo("users", _columnsUsers, _foreignKeysUsers, _indicesUsers);
        final TableInfo _existingUsers = TableInfo.read(db, "users");
        if (!_infoUsers.equals(_existingUsers)) {
          return new RoomOpenHelper.ValidationResult(false, "users(com.ticket12306.android.data.local.entity.UserEntity).\n"
                  + " Expected:\n" + _infoUsers + "\n"
                  + " Found:\n" + _existingUsers);
        }
        final HashMap<String, TableInfo.Column> _columnsQueryHistory = new HashMap<String, TableInfo.Column>(9);
        _columnsQueryHistory.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("userId", new TableInfo.Column("userId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("fromStationCode", new TableInfo.Column("fromStationCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("fromStationName", new TableInfo.Column("fromStationName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("toStationCode", new TableInfo.Column("toStationCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("toStationName", new TableInfo.Column("toStationName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("trainDate", new TableInfo.Column("trainDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("queryTime", new TableInfo.Column("queryTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsQueryHistory.put("queryType", new TableInfo.Column("queryType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysQueryHistory = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysQueryHistory.add(new TableInfo.ForeignKey("users", "CASCADE", "NO ACTION", Arrays.asList("userId"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesQueryHistory = new HashSet<TableInfo.Index>(2);
        _indicesQueryHistory.add(new TableInfo.Index("index_query_history_userId", false, Arrays.asList("userId"), Arrays.asList("ASC")));
        _indicesQueryHistory.add(new TableInfo.Index("index_query_history_queryTime", false, Arrays.asList("queryTime"), Arrays.asList("ASC")));
        final TableInfo _infoQueryHistory = new TableInfo("query_history", _columnsQueryHistory, _foreignKeysQueryHistory, _indicesQueryHistory);
        final TableInfo _existingQueryHistory = TableInfo.read(db, "query_history");
        if (!_infoQueryHistory.equals(_existingQueryHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "query_history(com.ticket12306.android.data.local.entity.QueryHistoryEntity).\n"
                  + " Expected:\n" + _infoQueryHistory + "\n"
                  + " Found:\n" + _existingQueryHistory);
        }
        final HashMap<String, TableInfo.Column> _columnsBookingLogs = new HashMap<String, TableInfo.Column>(6);
        _columnsBookingLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingLogs.put("taskId", new TableInfo.Column("taskId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingLogs.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingLogs.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingLogs.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookingLogs.put("extraData", new TableInfo.Column("extraData", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookingLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookingLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookingLogs = new TableInfo("booking_logs", _columnsBookingLogs, _foreignKeysBookingLogs, _indicesBookingLogs);
        final TableInfo _existingBookingLogs = TableInfo.read(db, "booking_logs");
        if (!_infoBookingLogs.equals(_existingBookingLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "booking_logs(com.ticket12306.android.data.model.BookingLog).\n"
                  + " Expected:\n" + _infoBookingLogs + "\n"
                  + " Found:\n" + _existingBookingLogs);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "b6867d2a492c54ea0b989bb6b9d96be6", "c7706328180b6663b643192dbd6b6721");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "stations","passengers","booking_tasks","users","query_history","booking_logs");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `stations`");
      _db.execSQL("DELETE FROM `passengers`");
      _db.execSQL("DELETE FROM `booking_tasks`");
      _db.execSQL("DELETE FROM `users`");
      _db.execSQL("DELETE FROM `query_history`");
      _db.execSQL("DELETE FROM `booking_logs`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(StationDao.class, StationDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PassengerDao.class, PassengerDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookingTaskDao.class, BookingTaskDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookingLogDao.class, BookingLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(UserDao.class, UserDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(QueryHistoryDao.class, QueryHistoryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public StationDao stationDao() {
    if (_stationDao != null) {
      return _stationDao;
    } else {
      synchronized(this) {
        if(_stationDao == null) {
          _stationDao = new StationDao_Impl(this);
        }
        return _stationDao;
      }
    }
  }

  @Override
  public PassengerDao passengerDao() {
    if (_passengerDao != null) {
      return _passengerDao;
    } else {
      synchronized(this) {
        if(_passengerDao == null) {
          _passengerDao = new PassengerDao_Impl(this);
        }
        return _passengerDao;
      }
    }
  }

  @Override
  public BookingTaskDao bookingTaskDao() {
    if (_bookingTaskDao != null) {
      return _bookingTaskDao;
    } else {
      synchronized(this) {
        if(_bookingTaskDao == null) {
          _bookingTaskDao = new BookingTaskDao_Impl(this);
        }
        return _bookingTaskDao;
      }
    }
  }

  @Override
  public BookingLogDao bookingLogDao() {
    if (_bookingLogDao != null) {
      return _bookingLogDao;
    } else {
      synchronized(this) {
        if(_bookingLogDao == null) {
          _bookingLogDao = new BookingLogDao_Impl(this);
        }
        return _bookingLogDao;
      }
    }
  }

  @Override
  public UserDao userDao() {
    if (_userDao != null) {
      return _userDao;
    } else {
      synchronized(this) {
        if(_userDao == null) {
          _userDao = new UserDao_Impl(this);
        }
        return _userDao;
      }
    }
  }

  @Override
  public QueryHistoryDao queryHistoryDao() {
    if (_queryHistoryDao != null) {
      return _queryHistoryDao;
    } else {
      synchronized(this) {
        if(_queryHistoryDao == null) {
          _queryHistoryDao = new QueryHistoryDao_Impl(this);
        }
        return _queryHistoryDao;
      }
    }
  }
}
