package com.ticket12306.android.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ticket12306.android.data.model.Station;
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
public final class StationDao_Impl implements StationDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Station> __insertionAdapterOfStation;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllStations;

  public StationDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfStation = new EntityInsertionAdapter<Station>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `stations` (`code`,`name`,`pinyin`,`pinyinInitial`,`province`,`city`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Station entity) {
        statement.bindString(1, entity.getCode());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getPinyin());
        statement.bindString(4, entity.getPinyinInitial());
        if (entity.getProvince() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getProvince());
        }
        if (entity.getCity() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCity());
        }
      }
    };
    this.__preparedStmtOfDeleteAllStations = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM stations";
        return _query;
      }
    };
  }

  @Override
  public Object insertStations(final List<Station> stations,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStation.insert(stations);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertStation(final Station station, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfStation.insert(station);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object replaceAllStations(final List<Station> stations,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> StationDao.DefaultImpls.replaceAllStations(StationDao_Impl.this, stations, __cont), $completion);
  }

  @Override
  public Object deleteAllStations(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllStations.acquire();
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
          __preparedStmtOfDeleteAllStations.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Station>> getAllStations() {
    final String _sql = "SELECT * FROM stations ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stations"}, new Callable<List<Station>>() {
      @Override
      @NonNull
      public List<Station> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPinyin = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyin");
          final int _cursorIndexOfPinyinInitial = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyinInitial");
          final int _cursorIndexOfProvince = CursorUtil.getColumnIndexOrThrow(_cursor, "province");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final List<Station> _result = new ArrayList<Station>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Station _item;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPinyin;
            _tmpPinyin = _cursor.getString(_cursorIndexOfPinyin);
            final String _tmpPinyinInitial;
            _tmpPinyinInitial = _cursor.getString(_cursorIndexOfPinyinInitial);
            final String _tmpProvince;
            if (_cursor.isNull(_cursorIndexOfProvince)) {
              _tmpProvince = null;
            } else {
              _tmpProvince = _cursor.getString(_cursorIndexOfProvince);
            }
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            _item = new Station(_tmpCode,_tmpName,_tmpPinyin,_tmpPinyinInitial,_tmpProvince,_tmpCity);
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
  public Flow<List<Station>> searchStations(final String query) {
    final String _sql = "\n"
            + "        SELECT * FROM stations \n"
            + "        WHERE name LIKE '%' || ? || '%' \n"
            + "           OR pinyin LIKE '%' || ? || '%' \n"
            + "           OR pinyinInitial LIKE '%' || ? || '%' \n"
            + "           OR code LIKE '%' || ? || '%'\n"
            + "           OR city LIKE '%' || ? || '%'\n"
            + "        ORDER BY \n"
            + "            CASE WHEN name = ? THEN 0 ELSE 1 END,\n"
            + "            CASE WHEN name LIKE ? || '%' THEN 0 ELSE 1 END,\n"
            + "            CASE WHEN city = ? THEN 0 ELSE 1 END,\n"
            + "            CASE WHEN pinyin LIKE ? || '%' THEN 0 ELSE 1 END,\n"
            + "            CASE WHEN pinyinInitial = ? THEN 0 ELSE 1 END,\n"
            + "            name ASC\n"
            + "    ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 10);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    _argIndex = 4;
    _statement.bindString(_argIndex, query);
    _argIndex = 5;
    _statement.bindString(_argIndex, query);
    _argIndex = 6;
    _statement.bindString(_argIndex, query);
    _argIndex = 7;
    _statement.bindString(_argIndex, query);
    _argIndex = 8;
    _statement.bindString(_argIndex, query);
    _argIndex = 9;
    _statement.bindString(_argIndex, query);
    _argIndex = 10;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stations"}, new Callable<List<Station>>() {
      @Override
      @NonNull
      public List<Station> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPinyin = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyin");
          final int _cursorIndexOfPinyinInitial = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyinInitial");
          final int _cursorIndexOfProvince = CursorUtil.getColumnIndexOrThrow(_cursor, "province");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final List<Station> _result = new ArrayList<Station>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Station _item;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPinyin;
            _tmpPinyin = _cursor.getString(_cursorIndexOfPinyin);
            final String _tmpPinyinInitial;
            _tmpPinyinInitial = _cursor.getString(_cursorIndexOfPinyinInitial);
            final String _tmpProvince;
            if (_cursor.isNull(_cursorIndexOfProvince)) {
              _tmpProvince = null;
            } else {
              _tmpProvince = _cursor.getString(_cursorIndexOfProvince);
            }
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            _item = new Station(_tmpCode,_tmpName,_tmpPinyin,_tmpPinyinInitial,_tmpProvince,_tmpCity);
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
  public Flow<List<Station>> searchByCity(final String city) {
    final String _sql = "SELECT * FROM stations WHERE city LIKE '%' || ? || '%' ORDER BY name ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, city);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stations"}, new Callable<List<Station>>() {
      @Override
      @NonNull
      public List<Station> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPinyin = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyin");
          final int _cursorIndexOfPinyinInitial = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyinInitial");
          final int _cursorIndexOfProvince = CursorUtil.getColumnIndexOrThrow(_cursor, "province");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final List<Station> _result = new ArrayList<Station>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Station _item;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPinyin;
            _tmpPinyin = _cursor.getString(_cursorIndexOfPinyin);
            final String _tmpPinyinInitial;
            _tmpPinyinInitial = _cursor.getString(_cursorIndexOfPinyinInitial);
            final String _tmpProvince;
            if (_cursor.isNull(_cursorIndexOfProvince)) {
              _tmpProvince = null;
            } else {
              _tmpProvince = _cursor.getString(_cursorIndexOfProvince);
            }
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            _item = new Station(_tmpCode,_tmpName,_tmpPinyin,_tmpPinyinInitial,_tmpProvince,_tmpCity);
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
  public Object getStationByCode(final String code,
      final Continuation<? super Station> $completion) {
    final String _sql = "SELECT * FROM stations WHERE code = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, code);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Station>() {
      @Override
      @Nullable
      public Station call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPinyin = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyin");
          final int _cursorIndexOfPinyinInitial = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyinInitial");
          final int _cursorIndexOfProvince = CursorUtil.getColumnIndexOrThrow(_cursor, "province");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final Station _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPinyin;
            _tmpPinyin = _cursor.getString(_cursorIndexOfPinyin);
            final String _tmpPinyinInitial;
            _tmpPinyinInitial = _cursor.getString(_cursorIndexOfPinyinInitial);
            final String _tmpProvince;
            if (_cursor.isNull(_cursorIndexOfProvince)) {
              _tmpProvince = null;
            } else {
              _tmpProvince = _cursor.getString(_cursorIndexOfProvince);
            }
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            _result = new Station(_tmpCode,_tmpName,_tmpPinyin,_tmpPinyinInitial,_tmpProvince,_tmpCity);
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
  public Object getStationByName(final String name,
      final Continuation<? super Station> $completion) {
    final String _sql = "SELECT * FROM stations WHERE name = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, name);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Station>() {
      @Override
      @Nullable
      public Station call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPinyin = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyin");
          final int _cursorIndexOfPinyinInitial = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyinInitial");
          final int _cursorIndexOfProvince = CursorUtil.getColumnIndexOrThrow(_cursor, "province");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final Station _result;
          if (_cursor.moveToFirst()) {
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPinyin;
            _tmpPinyin = _cursor.getString(_cursorIndexOfPinyin);
            final String _tmpPinyinInitial;
            _tmpPinyinInitial = _cursor.getString(_cursorIndexOfPinyinInitial);
            final String _tmpProvince;
            if (_cursor.isNull(_cursorIndexOfProvince)) {
              _tmpProvince = null;
            } else {
              _tmpProvince = _cursor.getString(_cursorIndexOfProvince);
            }
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            _result = new Station(_tmpCode,_tmpName,_tmpPinyin,_tmpPinyinInitial,_tmpProvince,_tmpCity);
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
  public Flow<List<Station>> getStationsByCodes(final List<String> codes) {
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT * FROM stations WHERE code IN (");
    final int _inputSize = codes.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(") ORDER BY name ASC");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (String _item : codes) {
      _statement.bindString(_argIndex, _item);
      _argIndex++;
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"stations"}, new Callable<List<Station>>() {
      @Override
      @NonNull
      public List<Station> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfCode = CursorUtil.getColumnIndexOrThrow(_cursor, "code");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPinyin = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyin");
          final int _cursorIndexOfPinyinInitial = CursorUtil.getColumnIndexOrThrow(_cursor, "pinyinInitial");
          final int _cursorIndexOfProvince = CursorUtil.getColumnIndexOrThrow(_cursor, "province");
          final int _cursorIndexOfCity = CursorUtil.getColumnIndexOrThrow(_cursor, "city");
          final List<Station> _result = new ArrayList<Station>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Station _item_1;
            final String _tmpCode;
            _tmpCode = _cursor.getString(_cursorIndexOfCode);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPinyin;
            _tmpPinyin = _cursor.getString(_cursorIndexOfPinyin);
            final String _tmpPinyinInitial;
            _tmpPinyinInitial = _cursor.getString(_cursorIndexOfPinyinInitial);
            final String _tmpProvince;
            if (_cursor.isNull(_cursorIndexOfProvince)) {
              _tmpProvince = null;
            } else {
              _tmpProvince = _cursor.getString(_cursorIndexOfProvince);
            }
            final String _tmpCity;
            if (_cursor.isNull(_cursorIndexOfCity)) {
              _tmpCity = null;
            } else {
              _tmpCity = _cursor.getString(_cursorIndexOfCity);
            }
            _item_1 = new Station(_tmpCode,_tmpName,_tmpPinyin,_tmpPinyinInitial,_tmpProvince,_tmpCity);
            _result.add(_item_1);
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
  public Object getStationCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM stations";
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
