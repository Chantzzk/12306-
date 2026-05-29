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
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ticket12306.android.data.model.Passenger;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
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
public final class PassengerDao_Impl implements PassengerDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Passenger> __insertionAdapterOfPassenger;

  private final EntityDeletionOrUpdateAdapter<Passenger> __deletionAdapterOfPassenger;

  private final EntityDeletionOrUpdateAdapter<Passenger> __updateAdapterOfPassenger;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllPassengers;

  public PassengerDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPassenger = new EntityInsertionAdapter<Passenger>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `passengers` (`code`,`passenger_name`,`sex_code`,`sex_name`,`born_date`,`country_code`,`passenger_id_type_code`,`passenger_id_type_name`,`passenger_id_no`,`passenger_type`,`passenger_flag`,`passenger_name_en`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Passenger entity) {
        statement.bindString(1, entity.getCode());
        statement.bindString(2, entity.getPassenger_name());
        statement.bindString(3, entity.getSex_code());
        statement.bindString(4, entity.getSex_name());
        statement.bindString(5, entity.getBorn_date());
        statement.bindString(6, entity.getCountry_code());
        statement.bindString(7, entity.getPassenger_id_type_code());
        statement.bindString(8, entity.getPassenger_id_type_name());
        statement.bindString(9, entity.getPassenger_id_no());
        statement.bindString(10, entity.getPassenger_type());
        statement.bindString(11, entity.getPassenger_flag());
        statement.bindString(12, entity.getPassenger_name_en());
      }
    };
    this.__deletionAdapterOfPassenger = new EntityDeletionOrUpdateAdapter<Passenger>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `passengers` WHERE `code` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Passenger entity) {
        statement.bindString(1, entity.getCode());
      }
    };
    this.__updateAdapterOfPassenger = new EntityDeletionOrUpdateAdapter<Passenger>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `passengers` SET `code` = ?,`passenger_name` = ?,`sex_code` = ?,`sex_name` = ?,`born_date` = ?,`country_code` = ?,`passenger_id_type_code` = ?,`passenger_id_type_name` = ?,`passenger_id_no` = ?,`passenger_type` = ?,`passenger_flag` = ?,`passenger_name_en` = ? WHERE `code` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Passenger entity) {
        statement.bindString(1, entity.getCode());
        statement.bindString(2, entity.getPassenger_name());
        statement.bindString(3, entity.getSex_code());
        statement.bindString(4, entity.getSex_name());
        statement.bindString(5, entity.getBorn_date());
        statement.bindString(6, entity.getCountry_code());
        statement.bindString(7, entity.getPassenger_id_type_code());
        statement.bindString(8, entity.getPassenger_id_type_name());
        statement.bindString(9, entity.getPassenger_id_no());
        statement.bindString(10, entity.getPassenger_type());
        statement.bindString(11, entity.getPassenger_flag());
        statement.bindString(12, entity.getPassenger_name_en());
        statement.bindString(13, entity.getCode());
      }
    };
    this.__preparedStmtOfDeleteAllPassengers = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM passengers";
        return _query;
      }
    };
  }

  @Override
  public Object insertPassengers(final List<Passenger> passengers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPassenger.insert(passengers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertPassenger(final Passenger passenger,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPassenger.insert(passenger);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePassenger(final Passenger passenger,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfPassenger.handle(passenger);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePassenger(final Passenger passenger,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPassenger.handle(passenger);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllPassengers(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllPassengers.acquire();
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
          __preparedStmtOfDeleteAllPassengers.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Passenger>> getAllPassengers() {
    final String _sql = "SELECT * FROM passengers ORDER BY passenger_name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"passengers"}, new Callable<List<Passenger>>() {
      @Override
      @NonNull
      public List<Passenger> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfPassengerName = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_name");
          final int _cursorIndexOfSexCode = CursorUtil.getColumnIndexOrThrow(_cursor, "sex_code");
          final int _cursorIndexOfSexName = CursorUtil.getColumnIndexOrThrow(_cursor, "sex_name");
          final int _cursorIndexOfBornDate = CursorUtil.getColumnIndexOrThrow(_cursor, "born_date");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "country_code");
          final int _cursorIndexOfPassengerIdTypeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_type_code");
          final int _cursorIndexOfPassengerIdTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_type_name");
          final int _cursorIndexOfPassengerIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_no");
          final int _cursorIndexOfPassengerType = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_type");
          final int _cursorIndexOfPassengerFlag = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_flag");
          final int _cursorIndexOfPassengerNameEn = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_name_en");
          final List<Passenger> _result = new ArrayList<Passenger>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Passenger _item;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpPassenger_name;
            _tmpPassenger_name = _cursor.getString(_cursorIndexOfPassengerName);
            final String _tmpSex_code;
            _tmpSex_code = _cursor.getString(_cursorIndexOfSexCode);
            final String _tmpSex_name;
            _tmpSex_name = _cursor.getString(_cursorIndexOfSexName);
            final String _tmpBorn_date;
            _tmpBorn_date = _cursor.getString(_cursorIndexOfBornDate);
            final String _tmpCountry_code;
            _tmpCountry_code = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpPassenger_id_type_code;
            _tmpPassenger_id_type_code = _cursor.getString(_cursorIndexOfPassengerIdTypeCode);
            final String _tmpPassenger_id_type_name;
            _tmpPassenger_id_type_name = _cursor.getString(_cursorIndexOfPassengerIdTypeName);
            final String _tmpPassenger_id_no;
            _tmpPassenger_id_no = _cursor.getString(_cursorIndexOfPassengerIdNo);
            final String _tmpPassenger_type;
            _tmpPassenger_type = _cursor.getString(_cursorIndexOfPassengerType);
            final String _tmpPassenger_flag;
            _tmpPassenger_flag = _cursor.getString(_cursorIndexOfPassengerFlag);
            final String _tmpPassenger_name_en;
            _tmpPassenger_name_en = _cursor.getString(_cursorIndexOfPassengerNameEn);
            _item = new Passenger(_tmpCode,_tmpPassenger_name,_tmpSex_code,_tmpSex_name,_tmpBorn_date,_tmpCountry_code,_tmpPassenger_id_type_code,_tmpPassenger_id_type_name,_tmpPassenger_id_no,_tmpPassenger_type,_tmpPassenger_flag,_tmpPassenger_name_en);
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
  public Object getPassengersByCodes(final List<String> codes,
      final Continuation<? super List<Passenger>> $completion) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM passengers WHERE code IN (");
    final int _inputSize = codes.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : codes) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Passenger>>() {
      @Override
      @NonNull
      public List<Passenger> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfPassengerName = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_name");
          final int _cursorIndexOfSexCode = CursorUtil.getColumnIndexOrThrow(_cursor, "sex_code");
          final int _cursorIndexOfSexName = CursorUtil.getColumnIndexOrThrow(_cursor, "sex_name");
          final int _cursorIndexOfBornDate = CursorUtil.getColumnIndexOrThrow(_cursor, "born_date");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "country_code");
          final int _cursorIndexOfPassengerIdTypeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_type_code");
          final int _cursorIndexOfPassengerIdTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_type_name");
          final int _cursorIndexOfPassengerIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_no");
          final int _cursorIndexOfPassengerType = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_type");
          final int _cursorIndexOfPassengerFlag = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_flag");
          final int _cursorIndexOfPassengerNameEn = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_name_en");
          final List<Passenger> _result = new ArrayList<Passenger>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Passenger _item_1;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpPassenger_name;
            _tmpPassenger_name = _cursor.getString(_cursorIndexOfPassengerName);
            final String _tmpSex_code;
            _tmpSex_code = _cursor.getString(_cursorIndexOfSexCode);
            final String _tmpSex_name;
            _tmpSex_name = _cursor.getString(_cursorIndexOfSexName);
            final String _tmpBorn_date;
            _tmpBorn_date = _cursor.getString(_cursorIndexOfBornDate);
            final String _tmpCountry_code;
            _tmpCountry_code = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpPassenger_id_type_code;
            _tmpPassenger_id_type_code = _cursor.getString(_cursorIndexOfPassengerIdTypeCode);
            final String _tmpPassenger_id_type_name;
            _tmpPassenger_id_type_name = _cursor.getString(_cursorIndexOfPassengerIdTypeName);
            final String _tmpPassenger_id_no;
            _tmpPassenger_id_no = _cursor.getString(_cursorIndexOfPassengerIdNo);
            final String _tmpPassenger_type;
            _tmpPassenger_type = _cursor.getString(_cursorIndexOfPassengerType);
            final String _tmpPassenger_flag;
            _tmpPassenger_flag = _cursor.getString(_cursorIndexOfPassengerFlag);
            final String _tmpPassenger_name_en;
            _tmpPassenger_name_en = _cursor.getString(_cursorIndexOfPassengerNameEn);
            _item_1 = new Passenger(_tmpCode,_tmpPassenger_name,_tmpSex_code,_tmpSex_name,_tmpBorn_date,_tmpCountry_code,_tmpPassenger_id_type_code,_tmpPassenger_id_type_name,_tmpPassenger_id_no,_tmpPassenger_type,_tmpPassenger_flag,_tmpPassenger_name_en);
            _result.add(_item_1);
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
  public Object getPassengerByCode(final String code,
      final Continuation<? super Passenger> $completion) {
    final String _sql = "SELECT * FROM passengers WHERE code = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, code);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Passenger>() {
      @Override
      @Nullable
      public Passenger call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfPassengerName = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_name");
          final int _cursorIndexOfSexCode = CursorUtil.getColumnIndexOrThrow(_cursor, "sex_code");
          final int _cursorIndexOfSexName = CursorUtil.getColumnIndexOrThrow(_cursor, "sex_name");
          final int _cursorIndexOfBornDate = CursorUtil.getColumnIndexOrThrow(_cursor, "born_date");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "country_code");
          final int _cursorIndexOfPassengerIdTypeCode = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_type_code");
          final int _cursorIndexOfPassengerIdTypeName = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_type_name");
          final int _cursorIndexOfPassengerIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_id_no");
          final int _cursorIndexOfPassengerType = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_type");
          final int _cursorIndexOfPassengerFlag = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_flag");
          final int _cursorIndexOfPassengerNameEn = CursorUtil.getColumnIndexOrThrow(_cursor, "passenger_name_en");
          final Passenger _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpPassenger_name;
            _tmpPassenger_name = _cursor.getString(_cursorIndexOfPassengerName);
            final String _tmpSex_code;
            _tmpSex_code = _cursor.getString(_cursorIndexOfSexCode);
            final String _tmpSex_name;
            _tmpSex_name = _cursor.getString(_cursorIndexOfSexName);
            final String _tmpBorn_date;
            _tmpBorn_date = _cursor.getString(_cursorIndexOfBornDate);
            final String _tmpCountry_code;
            _tmpCountry_code = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpPassenger_id_type_code;
            _tmpPassenger_id_type_code = _cursor.getString(_cursorIndexOfPassengerIdTypeCode);
            final String _tmpPassenger_id_type_name;
            _tmpPassenger_id_type_name = _cursor.getString(_cursorIndexOfPassengerIdTypeName);
            final String _tmpPassenger_id_no;
            _tmpPassenger_id_no = _cursor.getString(_cursorIndexOfPassengerIdNo);
            final String _tmpPassenger_type;
            _tmpPassenger_type = _cursor.getString(_cursorIndexOfPassengerType);
            final String _tmpPassenger_flag;
            _tmpPassenger_flag = _cursor.getString(_cursorIndexOfPassengerFlag);
            final String _tmpPassenger_name_en;
            _tmpPassenger_name_en = _cursor.getString(_cursorIndexOfPassengerNameEn);
            _result = new Passenger(_tmpCode,_tmpPassenger_name,_tmpSex_code,_tmpSex_name,_tmpBorn_date,_tmpCountry_code,_tmpPassenger_id_type_code,_tmpPassenger_id_type_name,_tmpPassenger_id_no,_tmpPassenger_type,_tmpPassenger_flag,_tmpPassenger_name_en);
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
  public Object getPassengerCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM passengers";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
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
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
