package com.ticket12306.android.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ticket12306.android.data.local.database.Converters;
import com.ticket12306.android.data.model.BookingTask;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class BookingTaskDao_Impl implements BookingTaskDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<BookingTask> __insertionAdapterOfBookingTask;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<BookingTask> __deletionAdapterOfBookingTask;

  private final EntityDeletionOrUpdateAdapter<BookingTask> __updateAdapterOfBookingTask;

  private final SharedSQLiteStatement __preparedStmtOfDeleteBookingTaskById;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateBookingTask;

  private final SharedSQLiteStatement __preparedStmtOfActivateBookingTask;

  private final SharedSQLiteStatement __preparedStmtOfDeleteInactiveBookingTasks;

  private final SharedSQLiteStatement __preparedStmtOfUpdateTaskStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateRetryCount;

  private final SharedSQLiteStatement __preparedStmtOfResetTask;

  public BookingTaskDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfBookingTask = new EntityInsertionAdapter<BookingTask>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `booking_tasks` (`id`,`trainNumber`,`trainNo`,`departureStation`,`departureStationName`,`arrivalStation`,`arrivalStationName`,`departureDate`,`departureTime`,`arrivalTime`,`seatType`,`seatTypeName`,`passengerIds`,`passengerNames`,`autoBooking`,`isActive`,`createdAt`,`updatedAt`,`strategy`,`refreshInterval`,`maxRetryCount`,`seatPreferences`,`acceptWaitlist`,`currentRetryCount`,`status`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookingTask entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTrainNumber());
        statement.bindString(3, entity.getTrainNo());
        statement.bindString(4, entity.getDepartureStation());
        statement.bindString(5, entity.getDepartureStationName());
        statement.bindString(6, entity.getArrivalStation());
        statement.bindString(7, entity.getArrivalStationName());
        statement.bindString(8, entity.getDepartureDate());
        statement.bindString(9, entity.getDepartureTime());
        statement.bindString(10, entity.getArrivalTime());
        statement.bindString(11, entity.getSeatType());
        statement.bindString(12, entity.getSeatTypeName());
        final String _tmp = __converters.fromStringList(entity.getPassengerIds());
        statement.bindString(13, _tmp);
        final String _tmp_1 = __converters.fromStringList(entity.getPassengerNames());
        statement.bindString(14, _tmp_1);
        final int _tmp_2 = entity.getAutoBooking() ? 1 : 0;
        statement.bindLong(15, _tmp_2);
        final int _tmp_3 = entity.isActive() ? 1 : 0;
        statement.bindLong(16, _tmp_3);
        statement.bindLong(17, entity.getCreatedAt());
        statement.bindLong(18, entity.getUpdatedAt());
        statement.bindString(19, entity.getStrategy());
        statement.bindLong(20, entity.getRefreshInterval());
        statement.bindLong(21, entity.getMaxRetryCount());
        final String _tmp_4 = __converters.fromStringList(entity.getSeatPreferences());
        statement.bindString(22, _tmp_4);
        final int _tmp_5 = entity.getAcceptWaitlist() ? 1 : 0;
        statement.bindLong(23, _tmp_5);
        statement.bindLong(24, entity.getCurrentRetryCount());
        statement.bindString(25, entity.getStatus());
      }
    };
    this.__deletionAdapterOfBookingTask = new EntityDeletionOrUpdateAdapter<BookingTask>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `booking_tasks` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookingTask entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfBookingTask = new EntityDeletionOrUpdateAdapter<BookingTask>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `booking_tasks` SET `id` = ?,`trainNumber` = ?,`trainNo` = ?,`departureStation` = ?,`departureStationName` = ?,`arrivalStation` = ?,`arrivalStationName` = ?,`departureDate` = ?,`departureTime` = ?,`arrivalTime` = ?,`seatType` = ?,`seatTypeName` = ?,`passengerIds` = ?,`passengerNames` = ?,`autoBooking` = ?,`isActive` = ?,`createdAt` = ?,`updatedAt` = ?,`strategy` = ?,`refreshInterval` = ?,`maxRetryCount` = ?,`seatPreferences` = ?,`acceptWaitlist` = ?,`currentRetryCount` = ?,`status` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final BookingTask entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTrainNumber());
        statement.bindString(3, entity.getTrainNo());
        statement.bindString(4, entity.getDepartureStation());
        statement.bindString(5, entity.getDepartureStationName());
        statement.bindString(6, entity.getArrivalStation());
        statement.bindString(7, entity.getArrivalStationName());
        statement.bindString(8, entity.getDepartureDate());
        statement.bindString(9, entity.getDepartureTime());
        statement.bindString(10, entity.getArrivalTime());
        statement.bindString(11, entity.getSeatType());
        statement.bindString(12, entity.getSeatTypeName());
        final String _tmp = __converters.fromStringList(entity.getPassengerIds());
        statement.bindString(13, _tmp);
        final String _tmp_1 = __converters.fromStringList(entity.getPassengerNames());
        statement.bindString(14, _tmp_1);
        final int _tmp_2 = entity.getAutoBooking() ? 1 : 0;
        statement.bindLong(15, _tmp_2);
        final int _tmp_3 = entity.isActive() ? 1 : 0;
        statement.bindLong(16, _tmp_3);
        statement.bindLong(17, entity.getCreatedAt());
        statement.bindLong(18, entity.getUpdatedAt());
        statement.bindString(19, entity.getStrategy());
        statement.bindLong(20, entity.getRefreshInterval());
        statement.bindLong(21, entity.getMaxRetryCount());
        final String _tmp_4 = __converters.fromStringList(entity.getSeatPreferences());
        statement.bindString(22, _tmp_4);
        final int _tmp_5 = entity.getAcceptWaitlist() ? 1 : 0;
        statement.bindLong(23, _tmp_5);
        statement.bindLong(24, entity.getCurrentRetryCount());
        statement.bindString(25, entity.getStatus());
        statement.bindLong(26, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteBookingTaskById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM booking_tasks WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeactivateBookingTask = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE booking_tasks SET isActive = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfActivateBookingTask = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE booking_tasks SET isActive = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteInactiveBookingTasks = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM booking_tasks WHERE isActive = 0";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateTaskStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE booking_tasks SET status = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateRetryCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE booking_tasks SET currentRetryCount = ?, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfResetTask = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE booking_tasks SET isActive = 1, status = 'PENDING', currentRetryCount = 0, updatedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertBookingTask(final BookingTask task,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfBookingTask.insertAndReturnId(task);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookingTask(final BookingTask task,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfBookingTask.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateBookingTask(final BookingTask task,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfBookingTask.handle(task);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteBookingTaskById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteBookingTaskById.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteBookingTaskById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateBookingTask(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateBookingTask.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeactivateBookingTask.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object activateBookingTask(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfActivateBookingTask.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfActivateBookingTask.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteInactiveBookingTasks(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteInactiveBookingTasks.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteInactiveBookingTasks.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateTaskStatus(final long id, final String status, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateTaskStatus.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, status);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateTaskStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateRetryCount(final long id, final int retryCount, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateRetryCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, retryCount);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 3;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateRetryCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object resetTask(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfResetTask.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfResetTask.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<BookingTask>> getAllBookingTasks() {
    final String _sql = "SELECT * FROM booking_tasks ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"booking_tasks"}, new Callable<List<BookingTask>>() {
      @Override
      @NonNull
      public List<BookingTask> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTrainNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "trainNumber");
          final int _cursorIndexOfTrainNo = CursorUtil.getColumnIndexOrThrow(_cursor, "trainNo");
          final int _cursorIndexOfDepartureStation = CursorUtil.getColumnIndexOrThrow(_cursor, "departureStation");
          final int _cursorIndexOfDepartureStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureStationName");
          final int _cursorIndexOfArrivalStation = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalStation");
          final int _cursorIndexOfArrivalStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalStationName");
          final int _cursorIndexOfDepartureDate = CursorUtil.getColumnIndexOrThrow(_cursor, "departureDate");
          final int _cursorIndexOfDepartureTime = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTime");
          final int _cursorIndexOfArrivalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTime");
          final int _cursorIndexOfSeatType = CursorUtil.getColumnIndexOrThrow(_cursor, "seatType");
          final int _cursorIndexOfSeatTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "seatTypeName");
          final int _cursorIndexOfPassengerIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passengerIds");
          final int _cursorIndexOfPassengerNames = CursorUtil.getColumnIndexOrThrow(_cursor, "passengerNames");
          final int _cursorIndexOfAutoBooking = CursorUtil.getColumnIndexOrThrow(_cursor, "autoBooking");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfStrategy = CursorUtil.getColumnIndexOrThrow(_cursor, "strategy");
          final int _cursorIndexOfRefreshInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "refreshInterval");
          final int _cursorIndexOfMaxRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetryCount");
          final int _cursorIndexOfSeatPreferences = CursorUtil.getColumnIndexOrThrow(_cursor, "seatPreferences");
          final int _cursorIndexOfAcceptWaitlist = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptWaitlist");
          final int _cursorIndexOfCurrentRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRetryCount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<BookingTask> _result = new ArrayList<BookingTask>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookingTask _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTrainNumber;
            _tmpTrainNumber = _cursor.getString(_cursorIndexOfTrainNumber);
            final String _tmpTrainNo;
            _tmpTrainNo = _cursor.getString(_cursorIndexOfTrainNo);
            final String _tmpDepartureStation;
            _tmpDepartureStation = _cursor.getString(_cursorIndexOfDepartureStation);
            final String _tmpDepartureStationName;
            _tmpDepartureStationName = _cursor.getString(_cursorIndexOfDepartureStationName);
            final String _tmpArrivalStation;
            _tmpArrivalStation = _cursor.getString(_cursorIndexOfArrivalStation);
            final String _tmpArrivalStationName;
            _tmpArrivalStationName = _cursor.getString(_cursorIndexOfArrivalStationName);
            final String _tmpDepartureDate;
            _tmpDepartureDate = _cursor.getString(_cursorIndexOfDepartureDate);
            final String _tmpDepartureTime;
            _tmpDepartureTime = _cursor.getString(_cursorIndexOfDepartureTime);
            final String _tmpArrivalTime;
            _tmpArrivalTime = _cursor.getString(_cursorIndexOfArrivalTime);
            final String _tmpSeatType;
            _tmpSeatType = _cursor.getString(_cursorIndexOfSeatType);
            final String _tmpSeatTypeName;
            _tmpSeatTypeName = _cursor.getString(_cursorIndexOfSeatTypeName);
            final List<String> _tmpPassengerIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPassengerIds);
            _tmpPassengerIds = __converters.toStringList(_tmp);
            final List<String> _tmpPassengerNames;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPassengerNames);
            _tmpPassengerNames = __converters.toStringList(_tmp_1);
            final boolean _tmpAutoBooking;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfAutoBooking);
            _tmpAutoBooking = _tmp_2 != 0;
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpStrategy;
            _tmpStrategy = _cursor.getString(_cursorIndexOfStrategy);
            final int _tmpRefreshInterval;
            _tmpRefreshInterval = _cursor.getInt(_cursorIndexOfRefreshInterval);
            final int _tmpMaxRetryCount;
            _tmpMaxRetryCount = _cursor.getInt(_cursorIndexOfMaxRetryCount);
            final List<String> _tmpSeatPreferences;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfSeatPreferences);
            _tmpSeatPreferences = __converters.toStringList(_tmp_4);
            final boolean _tmpAcceptWaitlist;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfAcceptWaitlist);
            _tmpAcceptWaitlist = _tmp_5 != 0;
            final int _tmpCurrentRetryCount;
            _tmpCurrentRetryCount = _cursor.getInt(_cursorIndexOfCurrentRetryCount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new BookingTask(_tmpId,_tmpTrainNumber,_tmpTrainNo,_tmpDepartureStation,_tmpDepartureStationName,_tmpArrivalStation,_tmpArrivalStationName,_tmpDepartureDate,_tmpDepartureTime,_tmpArrivalTime,_tmpSeatType,_tmpSeatTypeName,_tmpPassengerIds,_tmpPassengerNames,_tmpAutoBooking,_tmpIsActive,_tmpCreatedAt,_tmpUpdatedAt,_tmpStrategy,_tmpRefreshInterval,_tmpMaxRetryCount,_tmpSeatPreferences,_tmpAcceptWaitlist,_tmpCurrentRetryCount,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<BookingTask>> getActiveBookingTasks() {
    final String _sql = "SELECT * FROM booking_tasks WHERE isActive = 1 ORDER BY createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"booking_tasks"}, new Callable<List<BookingTask>>() {
      @Override
      @NonNull
      public List<BookingTask> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTrainNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "trainNumber");
          final int _cursorIndexOfTrainNo = CursorUtil.getColumnIndexOrThrow(_cursor, "trainNo");
          final int _cursorIndexOfDepartureStation = CursorUtil.getColumnIndexOrThrow(_cursor, "departureStation");
          final int _cursorIndexOfDepartureStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureStationName");
          final int _cursorIndexOfArrivalStation = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalStation");
          final int _cursorIndexOfArrivalStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalStationName");
          final int _cursorIndexOfDepartureDate = CursorUtil.getColumnIndexOrThrow(_cursor, "departureDate");
          final int _cursorIndexOfDepartureTime = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTime");
          final int _cursorIndexOfArrivalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTime");
          final int _cursorIndexOfSeatType = CursorUtil.getColumnIndexOrThrow(_cursor, "seatType");
          final int _cursorIndexOfSeatTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "seatTypeName");
          final int _cursorIndexOfPassengerIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passengerIds");
          final int _cursorIndexOfPassengerNames = CursorUtil.getColumnIndexOrThrow(_cursor, "passengerNames");
          final int _cursorIndexOfAutoBooking = CursorUtil.getColumnIndexOrThrow(_cursor, "autoBooking");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfStrategy = CursorUtil.getColumnIndexOrThrow(_cursor, "strategy");
          final int _cursorIndexOfRefreshInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "refreshInterval");
          final int _cursorIndexOfMaxRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetryCount");
          final int _cursorIndexOfSeatPreferences = CursorUtil.getColumnIndexOrThrow(_cursor, "seatPreferences");
          final int _cursorIndexOfAcceptWaitlist = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptWaitlist");
          final int _cursorIndexOfCurrentRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRetryCount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final List<BookingTask> _result = new ArrayList<BookingTask>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final BookingTask _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTrainNumber;
            _tmpTrainNumber = _cursor.getString(_cursorIndexOfTrainNumber);
            final String _tmpTrainNo;
            _tmpTrainNo = _cursor.getString(_cursorIndexOfTrainNo);
            final String _tmpDepartureStation;
            _tmpDepartureStation = _cursor.getString(_cursorIndexOfDepartureStation);
            final String _tmpDepartureStationName;
            _tmpDepartureStationName = _cursor.getString(_cursorIndexOfDepartureStationName);
            final String _tmpArrivalStation;
            _tmpArrivalStation = _cursor.getString(_cursorIndexOfArrivalStation);
            final String _tmpArrivalStationName;
            _tmpArrivalStationName = _cursor.getString(_cursorIndexOfArrivalStationName);
            final String _tmpDepartureDate;
            _tmpDepartureDate = _cursor.getString(_cursorIndexOfDepartureDate);
            final String _tmpDepartureTime;
            _tmpDepartureTime = _cursor.getString(_cursorIndexOfDepartureTime);
            final String _tmpArrivalTime;
            _tmpArrivalTime = _cursor.getString(_cursorIndexOfArrivalTime);
            final String _tmpSeatType;
            _tmpSeatType = _cursor.getString(_cursorIndexOfSeatType);
            final String _tmpSeatTypeName;
            _tmpSeatTypeName = _cursor.getString(_cursorIndexOfSeatTypeName);
            final List<String> _tmpPassengerIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPassengerIds);
            _tmpPassengerIds = __converters.toStringList(_tmp);
            final List<String> _tmpPassengerNames;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPassengerNames);
            _tmpPassengerNames = __converters.toStringList(_tmp_1);
            final boolean _tmpAutoBooking;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfAutoBooking);
            _tmpAutoBooking = _tmp_2 != 0;
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpStrategy;
            _tmpStrategy = _cursor.getString(_cursorIndexOfStrategy);
            final int _tmpRefreshInterval;
            _tmpRefreshInterval = _cursor.getInt(_cursorIndexOfRefreshInterval);
            final int _tmpMaxRetryCount;
            _tmpMaxRetryCount = _cursor.getInt(_cursorIndexOfMaxRetryCount);
            final List<String> _tmpSeatPreferences;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfSeatPreferences);
            _tmpSeatPreferences = __converters.toStringList(_tmp_4);
            final boolean _tmpAcceptWaitlist;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfAcceptWaitlist);
            _tmpAcceptWaitlist = _tmp_5 != 0;
            final int _tmpCurrentRetryCount;
            _tmpCurrentRetryCount = _cursor.getInt(_cursorIndexOfCurrentRetryCount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _item = new BookingTask(_tmpId,_tmpTrainNumber,_tmpTrainNo,_tmpDepartureStation,_tmpDepartureStationName,_tmpArrivalStation,_tmpArrivalStationName,_tmpDepartureDate,_tmpDepartureTime,_tmpArrivalTime,_tmpSeatType,_tmpSeatTypeName,_tmpPassengerIds,_tmpPassengerNames,_tmpAutoBooking,_tmpIsActive,_tmpCreatedAt,_tmpUpdatedAt,_tmpStrategy,_tmpRefreshInterval,_tmpMaxRetryCount,_tmpSeatPreferences,_tmpAcceptWaitlist,_tmpCurrentRetryCount,_tmpStatus);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getBookingTaskById(final long id,
      final Continuation<? super BookingTask> $completion) {
    final String _sql = "SELECT * FROM booking_tasks WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<BookingTask>() {
      @Override
      @Nullable
      public BookingTask call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTrainNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "trainNumber");
          final int _cursorIndexOfTrainNo = CursorUtil.getColumnIndexOrThrow(_cursor, "trainNo");
          final int _cursorIndexOfDepartureStation = CursorUtil.getColumnIndexOrThrow(_cursor, "departureStation");
          final int _cursorIndexOfDepartureStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "departureStationName");
          final int _cursorIndexOfArrivalStation = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalStation");
          final int _cursorIndexOfArrivalStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalStationName");
          final int _cursorIndexOfDepartureDate = CursorUtil.getColumnIndexOrThrow(_cursor, "departureDate");
          final int _cursorIndexOfDepartureTime = CursorUtil.getColumnIndexOrThrow(_cursor, "departureTime");
          final int _cursorIndexOfArrivalTime = CursorUtil.getColumnIndexOrThrow(_cursor, "arrivalTime");
          final int _cursorIndexOfSeatType = CursorUtil.getColumnIndexOrThrow(_cursor, "seatType");
          final int _cursorIndexOfSeatTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "seatTypeName");
          final int _cursorIndexOfPassengerIds = CursorUtil.getColumnIndexOrThrow(_cursor, "passengerIds");
          final int _cursorIndexOfPassengerNames = CursorUtil.getColumnIndexOrThrow(_cursor, "passengerNames");
          final int _cursorIndexOfAutoBooking = CursorUtil.getColumnIndexOrThrow(_cursor, "autoBooking");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final int _cursorIndexOfStrategy = CursorUtil.getColumnIndexOrThrow(_cursor, "strategy");
          final int _cursorIndexOfRefreshInterval = CursorUtil.getColumnIndexOrThrow(_cursor, "refreshInterval");
          final int _cursorIndexOfMaxRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "maxRetryCount");
          final int _cursorIndexOfSeatPreferences = CursorUtil.getColumnIndexOrThrow(_cursor, "seatPreferences");
          final int _cursorIndexOfAcceptWaitlist = CursorUtil.getColumnIndexOrThrow(_cursor, "acceptWaitlist");
          final int _cursorIndexOfCurrentRetryCount = CursorUtil.getColumnIndexOrThrow(_cursor, "currentRetryCount");
          final int _cursorIndexOfStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "status");
          final BookingTask _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTrainNumber;
            _tmpTrainNumber = _cursor.getString(_cursorIndexOfTrainNumber);
            final String _tmpTrainNo;
            _tmpTrainNo = _cursor.getString(_cursorIndexOfTrainNo);
            final String _tmpDepartureStation;
            _tmpDepartureStation = _cursor.getString(_cursorIndexOfDepartureStation);
            final String _tmpDepartureStationName;
            _tmpDepartureStationName = _cursor.getString(_cursorIndexOfDepartureStationName);
            final String _tmpArrivalStation;
            _tmpArrivalStation = _cursor.getString(_cursorIndexOfArrivalStation);
            final String _tmpArrivalStationName;
            _tmpArrivalStationName = _cursor.getString(_cursorIndexOfArrivalStationName);
            final String _tmpDepartureDate;
            _tmpDepartureDate = _cursor.getString(_cursorIndexOfDepartureDate);
            final String _tmpDepartureTime;
            _tmpDepartureTime = _cursor.getString(_cursorIndexOfDepartureTime);
            final String _tmpArrivalTime;
            _tmpArrivalTime = _cursor.getString(_cursorIndexOfArrivalTime);
            final String _tmpSeatType;
            _tmpSeatType = _cursor.getString(_cursorIndexOfSeatType);
            final String _tmpSeatTypeName;
            _tmpSeatTypeName = _cursor.getString(_cursorIndexOfSeatTypeName);
            final List<String> _tmpPassengerIds;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfPassengerIds);
            _tmpPassengerIds = __converters.toStringList(_tmp);
            final List<String> _tmpPassengerNames;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfPassengerNames);
            _tmpPassengerNames = __converters.toStringList(_tmp_1);
            final boolean _tmpAutoBooking;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfAutoBooking);
            _tmpAutoBooking = _tmp_2 != 0;
            final boolean _tmpIsActive;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_3 != 0;
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            final String _tmpStrategy;
            _tmpStrategy = _cursor.getString(_cursorIndexOfStrategy);
            final int _tmpRefreshInterval;
            _tmpRefreshInterval = _cursor.getInt(_cursorIndexOfRefreshInterval);
            final int _tmpMaxRetryCount;
            _tmpMaxRetryCount = _cursor.getInt(_cursorIndexOfMaxRetryCount);
            final List<String> _tmpSeatPreferences;
            final String _tmp_4;
            _tmp_4 = _cursor.getString(_cursorIndexOfSeatPreferences);
            _tmpSeatPreferences = __converters.toStringList(_tmp_4);
            final boolean _tmpAcceptWaitlist;
            final int _tmp_5;
            _tmp_5 = _cursor.getInt(_cursorIndexOfAcceptWaitlist);
            _tmpAcceptWaitlist = _tmp_5 != 0;
            final int _tmpCurrentRetryCount;
            _tmpCurrentRetryCount = _cursor.getInt(_cursorIndexOfCurrentRetryCount);
            final String _tmpStatus;
            _tmpStatus = _cursor.getString(_cursorIndexOfStatus);
            _result = new BookingTask(_tmpId,_tmpTrainNumber,_tmpTrainNo,_tmpDepartureStation,_tmpDepartureStationName,_tmpArrivalStation,_tmpArrivalStationName,_tmpDepartureDate,_tmpDepartureTime,_tmpArrivalTime,_tmpSeatType,_tmpSeatTypeName,_tmpPassengerIds,_tmpPassengerNames,_tmpAutoBooking,_tmpIsActive,_tmpCreatedAt,_tmpUpdatedAt,_tmpStrategy,_tmpRefreshInterval,_tmpMaxRetryCount,_tmpSeatPreferences,_tmpAcceptWaitlist,_tmpCurrentRetryCount,_tmpStatus);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Integer> getActiveTaskCount() {
    final String _sql = "SELECT COUNT(*) FROM booking_tasks WHERE isActive = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"booking_tasks"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
