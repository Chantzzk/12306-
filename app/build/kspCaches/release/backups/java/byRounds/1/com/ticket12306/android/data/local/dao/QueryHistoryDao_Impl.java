package com.ticket12306.android.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.ticket12306.android.data.local.database.Converters;
import com.ticket12306.android.data.local.entity.QueryHistoryEntity;
import com.ticket12306.android.data.local.entity.QueryType;
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
public final class QueryHistoryDao_Impl implements QueryHistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<QueryHistoryEntity> __insertionAdapterOfQueryHistoryEntity;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<QueryHistoryEntity> __deletionAdapterOfQueryHistoryEntity;

  private final EntityDeletionOrUpdateAdapter<QueryHistoryEntity> __updateAdapterOfQueryHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteQueryHistoryById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteQueryHistoryByUser;

  private final SharedSQLiteStatement __preparedStmtOfDeleteQueryHistoryByUserAndType;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllQueryHistory;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOldQueryHistory;

  public QueryHistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfQueryHistoryEntity = new EntityInsertionAdapter<QueryHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `query_history` (`id`,`userId`,`fromStationCode`,`fromStationName`,`toStationCode`,`toStationName`,`trainDate`,`queryTime`,`queryType`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QueryHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getFromStationCode());
        statement.bindString(4, entity.getFromStationName());
        statement.bindString(5, entity.getToStationCode());
        statement.bindString(6, entity.getToStationName());
        statement.bindString(7, entity.getTrainDate());
        statement.bindLong(8, entity.getQueryTime());
        final String _tmp = __converters.fromQueryType(entity.getQueryType());
        statement.bindString(9, _tmp);
      }
    };
    this.__deletionAdapterOfQueryHistoryEntity = new EntityDeletionOrUpdateAdapter<QueryHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `query_history` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QueryHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfQueryHistoryEntity = new EntityDeletionOrUpdateAdapter<QueryHistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `query_history` SET `id` = ?,`userId` = ?,`fromStationCode` = ?,`fromStationName` = ?,`toStationCode` = ?,`toStationName` = ?,`trainDate` = ?,`queryTime` = ?,`queryType` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final QueryHistoryEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getUserId());
        statement.bindString(3, entity.getFromStationCode());
        statement.bindString(4, entity.getFromStationName());
        statement.bindString(5, entity.getToStationCode());
        statement.bindString(6, entity.getToStationName());
        statement.bindString(7, entity.getTrainDate());
        statement.bindLong(8, entity.getQueryTime());
        final String _tmp = __converters.fromQueryType(entity.getQueryType());
        statement.bindString(9, _tmp);
        statement.bindLong(10, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteQueryHistoryById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM query_history WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteQueryHistoryByUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM query_history WHERE userId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteQueryHistoryByUserAndType = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM query_history WHERE userId = ? AND queryType = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllQueryHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM query_history";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOldQueryHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM query_history WHERE queryTime < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertQueryHistory(final QueryHistoryEntity history,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfQueryHistoryEntity.insertAndReturnId(history);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertQueryHistoryList(final List<QueryHistoryEntity> histories,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfQueryHistoryEntity.insert(histories);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQueryHistory(final QueryHistoryEntity history,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfQueryHistoryEntity.handle(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateQueryHistory(final QueryHistoryEntity history,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfQueryHistoryEntity.handle(history);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertOrUpdateQueryHistory(final QueryHistoryEntity history,
      final Continuation<? super Long> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> QueryHistoryDao.DefaultImpls.insertOrUpdateQueryHistory(QueryHistoryDao_Impl.this, history, __cont), $completion);
  }

  @Override
  public Object deleteQueryHistoryById(final long id,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteQueryHistoryById.acquire();
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
          __preparedStmtOfDeleteQueryHistoryById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQueryHistoryByUser(final long userId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteQueryHistoryByUser.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
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
          __preparedStmtOfDeleteQueryHistoryByUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteQueryHistoryByUserAndType(final long userId, final QueryType queryType,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteQueryHistoryByUserAndType.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        _argIndex = 2;
        final String _tmp = __converters.fromQueryType(queryType);
        _stmt.bindString(_argIndex, _tmp);
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
          __preparedStmtOfDeleteQueryHistoryByUserAndType.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllQueryHistory(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllQueryHistory.acquire();
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
          __preparedStmtOfDeleteAllQueryHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOldQueryHistory(final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOldQueryHistory.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfDeleteOldQueryHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<QueryHistoryEntity>> getQueryHistoryByUser(final long userId) {
    final String _sql = "SELECT * FROM query_history WHERE userId = ? ORDER BY queryTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"query_history"}, new Callable<List<QueryHistoryEntity>>() {
      @Override
      @NonNull
      public List<QueryHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFromStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationCode");
          final int _cursorIndexOfFromStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationName");
          final int _cursorIndexOfToStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationCode");
          final int _cursorIndexOfToStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationName");
          final int _cursorIndexOfTrainDate = CursorUtil.getColumnIndexOrThrow(_cursor, "trainDate");
          final int _cursorIndexOfQueryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "queryTime");
          final int _cursorIndexOfQueryType = CursorUtil.getColumnIndexOrThrow(_cursor, "queryType");
          final List<QueryHistoryEntity> _result = new ArrayList<QueryHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QueryHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpFromStationCode;
            _tmpFromStationCode = _cursor.getString(_cursorIndexOfFromStationCode);
            final String _tmpFromStationName;
            _tmpFromStationName = _cursor.getString(_cursorIndexOfFromStationName);
            final String _tmpToStationCode;
            _tmpToStationCode = _cursor.getString(_cursorIndexOfToStationCode);
            final String _tmpToStationName;
            _tmpToStationName = _cursor.getString(_cursorIndexOfToStationName);
            final String _tmpTrainDate;
            _tmpTrainDate = _cursor.getString(_cursorIndexOfTrainDate);
            final long _tmpQueryTime;
            _tmpQueryTime = _cursor.getLong(_cursorIndexOfQueryTime);
            final QueryType _tmpQueryType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQueryType);
            _tmpQueryType = __converters.toQueryType(_tmp);
            _item = new QueryHistoryEntity(_tmpId,_tmpUserId,_tmpFromStationCode,_tmpFromStationName,_tmpToStationCode,_tmpToStationName,_tmpTrainDate,_tmpQueryTime,_tmpQueryType);
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
  public Flow<List<QueryHistoryEntity>> getQueryHistoryByUserAndType(final long userId,
      final QueryType queryType) {
    final String _sql = "SELECT * FROM query_history WHERE userId = ? AND queryType = ? ORDER BY queryTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    final String _tmp = __converters.fromQueryType(queryType);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"query_history"}, new Callable<List<QueryHistoryEntity>>() {
      @Override
      @NonNull
      public List<QueryHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFromStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationCode");
          final int _cursorIndexOfFromStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationName");
          final int _cursorIndexOfToStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationCode");
          final int _cursorIndexOfToStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationName");
          final int _cursorIndexOfTrainDate = CursorUtil.getColumnIndexOrThrow(_cursor, "trainDate");
          final int _cursorIndexOfQueryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "queryTime");
          final int _cursorIndexOfQueryType = CursorUtil.getColumnIndexOrThrow(_cursor, "queryType");
          final List<QueryHistoryEntity> _result = new ArrayList<QueryHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QueryHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpFromStationCode;
            _tmpFromStationCode = _cursor.getString(_cursorIndexOfFromStationCode);
            final String _tmpFromStationName;
            _tmpFromStationName = _cursor.getString(_cursorIndexOfFromStationName);
            final String _tmpToStationCode;
            _tmpToStationCode = _cursor.getString(_cursorIndexOfToStationCode);
            final String _tmpToStationName;
            _tmpToStationName = _cursor.getString(_cursorIndexOfToStationName);
            final String _tmpTrainDate;
            _tmpTrainDate = _cursor.getString(_cursorIndexOfTrainDate);
            final long _tmpQueryTime;
            _tmpQueryTime = _cursor.getLong(_cursorIndexOfQueryTime);
            final QueryType _tmpQueryType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfQueryType);
            _tmpQueryType = __converters.toQueryType(_tmp_1);
            _item = new QueryHistoryEntity(_tmpId,_tmpUserId,_tmpFromStationCode,_tmpFromStationName,_tmpToStationCode,_tmpToStationName,_tmpTrainDate,_tmpQueryTime,_tmpQueryType);
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
  public Flow<List<QueryHistoryEntity>> getRecentQueryHistory(final long userId, final int limit) {
    final String _sql = "SELECT * FROM query_history WHERE userId = ? ORDER BY queryTime DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"query_history"}, new Callable<List<QueryHistoryEntity>>() {
      @Override
      @NonNull
      public List<QueryHistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFromStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationCode");
          final int _cursorIndexOfFromStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationName");
          final int _cursorIndexOfToStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationCode");
          final int _cursorIndexOfToStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationName");
          final int _cursorIndexOfTrainDate = CursorUtil.getColumnIndexOrThrow(_cursor, "trainDate");
          final int _cursorIndexOfQueryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "queryTime");
          final int _cursorIndexOfQueryType = CursorUtil.getColumnIndexOrThrow(_cursor, "queryType");
          final List<QueryHistoryEntity> _result = new ArrayList<QueryHistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final QueryHistoryEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpFromStationCode;
            _tmpFromStationCode = _cursor.getString(_cursorIndexOfFromStationCode);
            final String _tmpFromStationName;
            _tmpFromStationName = _cursor.getString(_cursorIndexOfFromStationName);
            final String _tmpToStationCode;
            _tmpToStationCode = _cursor.getString(_cursorIndexOfToStationCode);
            final String _tmpToStationName;
            _tmpToStationName = _cursor.getString(_cursorIndexOfToStationName);
            final String _tmpTrainDate;
            _tmpTrainDate = _cursor.getString(_cursorIndexOfTrainDate);
            final long _tmpQueryTime;
            _tmpQueryTime = _cursor.getLong(_cursorIndexOfQueryTime);
            final QueryType _tmpQueryType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQueryType);
            _tmpQueryType = __converters.toQueryType(_tmp);
            _item = new QueryHistoryEntity(_tmpId,_tmpUserId,_tmpFromStationCode,_tmpFromStationName,_tmpToStationCode,_tmpToStationName,_tmpTrainDate,_tmpQueryTime,_tmpQueryType);
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
  public Object getQueryHistoryById(final long id,
      final Continuation<? super QueryHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM query_history WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QueryHistoryEntity>() {
      @Override
      @Nullable
      public QueryHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFromStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationCode");
          final int _cursorIndexOfFromStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationName");
          final int _cursorIndexOfToStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationCode");
          final int _cursorIndexOfToStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationName");
          final int _cursorIndexOfTrainDate = CursorUtil.getColumnIndexOrThrow(_cursor, "trainDate");
          final int _cursorIndexOfQueryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "queryTime");
          final int _cursorIndexOfQueryType = CursorUtil.getColumnIndexOrThrow(_cursor, "queryType");
          final QueryHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpFromStationCode;
            _tmpFromStationCode = _cursor.getString(_cursorIndexOfFromStationCode);
            final String _tmpFromStationName;
            _tmpFromStationName = _cursor.getString(_cursorIndexOfFromStationName);
            final String _tmpToStationCode;
            _tmpToStationCode = _cursor.getString(_cursorIndexOfToStationCode);
            final String _tmpToStationName;
            _tmpToStationName = _cursor.getString(_cursorIndexOfToStationName);
            final String _tmpTrainDate;
            _tmpTrainDate = _cursor.getString(_cursorIndexOfTrainDate);
            final long _tmpQueryTime;
            _tmpQueryTime = _cursor.getLong(_cursorIndexOfQueryTime);
            final QueryType _tmpQueryType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQueryType);
            _tmpQueryType = __converters.toQueryType(_tmp);
            _result = new QueryHistoryEntity(_tmpId,_tmpUserId,_tmpFromStationCode,_tmpFromStationName,_tmpToStationCode,_tmpToStationName,_tmpTrainDate,_tmpQueryTime,_tmpQueryType);
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
  public Object findDuplicateQuery(final long userId, final String fromCode, final String toCode,
      final String trainDate, final Continuation<? super QueryHistoryEntity> $completion) {
    final String _sql = "SELECT * FROM query_history WHERE userId = ? AND fromStationCode = ? AND toStationCode = ? AND trainDate = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 4);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindString(_argIndex, fromCode);
    _argIndex = 3;
    _statement.bindString(_argIndex, toCode);
    _argIndex = 4;
    _statement.bindString(_argIndex, trainDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<QueryHistoryEntity>() {
      @Override
      @Nullable
      public QueryHistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUserId = CursorUtil.getColumnIndexOrThrow(_cursor, "userId");
          final int _cursorIndexOfFromStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationCode");
          final int _cursorIndexOfFromStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "fromStationName");
          final int _cursorIndexOfToStationCode = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationCode");
          final int _cursorIndexOfToStationName = CursorUtil.getColumnIndexOrThrow(_cursor, "toStationName");
          final int _cursorIndexOfTrainDate = CursorUtil.getColumnIndexOrThrow(_cursor, "trainDate");
          final int _cursorIndexOfQueryTime = CursorUtil.getColumnIndexOrThrow(_cursor, "queryTime");
          final int _cursorIndexOfQueryType = CursorUtil.getColumnIndexOrThrow(_cursor, "queryType");
          final QueryHistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpUserId;
            _tmpUserId = _cursor.getLong(_cursorIndexOfUserId);
            final String _tmpFromStationCode;
            _tmpFromStationCode = _cursor.getString(_cursorIndexOfFromStationCode);
            final String _tmpFromStationName;
            _tmpFromStationName = _cursor.getString(_cursorIndexOfFromStationName);
            final String _tmpToStationCode;
            _tmpToStationCode = _cursor.getString(_cursorIndexOfToStationCode);
            final String _tmpToStationName;
            _tmpToStationName = _cursor.getString(_cursorIndexOfToStationName);
            final String _tmpTrainDate;
            _tmpTrainDate = _cursor.getString(_cursorIndexOfTrainDate);
            final long _tmpQueryTime;
            _tmpQueryTime = _cursor.getLong(_cursorIndexOfQueryTime);
            final QueryType _tmpQueryType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfQueryType);
            _tmpQueryType = __converters.toQueryType(_tmp);
            _result = new QueryHistoryEntity(_tmpId,_tmpUserId,_tmpFromStationCode,_tmpFromStationName,_tmpToStationCode,_tmpToStationName,_tmpTrainDate,_tmpQueryTime,_tmpQueryType);
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
  public Object getQueryHistoryCount(final long userId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM query_history WHERE userId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
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

  @Override
  public Flow<List<RouteInfo>> getDistinctRoutes(final long userId, final int limit) {
    final String _sql = "SELECT DISTINCT fromStationCode, fromStationName, toStationCode, toStationName FROM query_history WHERE userId = ? ORDER BY queryTime DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, userId);
    _argIndex = 2;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"query_history"}, new Callable<List<RouteInfo>>() {
      @Override
      @NonNull
      public List<RouteInfo> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfFromStationCode = 0;
          final int _cursorIndexOfFromStationName = 1;
          final int _cursorIndexOfToStationCode = 2;
          final int _cursorIndexOfToStationName = 3;
          final List<RouteInfo> _result = new ArrayList<RouteInfo>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final RouteInfo _item;
            final String _tmpFromStationCode;
            _tmpFromStationCode = _cursor.getString(_cursorIndexOfFromStationCode);
            final String _tmpFromStationName;
            _tmpFromStationName = _cursor.getString(_cursorIndexOfFromStationName);
            final String _tmpToStationCode;
            _tmpToStationCode = _cursor.getString(_cursorIndexOfToStationCode);
            final String _tmpToStationName;
            _tmpToStationName = _cursor.getString(_cursorIndexOfToStationName);
            _item = new RouteInfo(_tmpFromStationCode,_tmpFromStationName,_tmpToStationCode,_tmpToStationName);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
