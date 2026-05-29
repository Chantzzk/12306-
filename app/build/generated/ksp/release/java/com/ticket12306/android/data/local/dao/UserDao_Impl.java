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
import com.ticket12306.android.data.local.entity.UserEntity;
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
public final class UserDao_Impl implements UserDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserEntity> __insertionAdapterOfUserEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __deletionAdapterOfUserEntity;

  private final EntityDeletionOrUpdateAdapter<UserEntity> __updateAdapterOfUserEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteUserById;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAllUsers;

  private final SharedSQLiteStatement __preparedStmtOfLogoutAllUsers;

  private final SharedSQLiteStatement __preparedStmtOfLoginUser;

  private final SharedSQLiteStatement __preparedStmtOfLogoutUser;

  private final SharedSQLiteStatement __preparedStmtOfUpdateToken;

  public UserDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserEntity = new EntityInsertionAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users` (`id`,`username`,`password`,`realName`,`idType`,`idNo`,`phone`,`email`,`token`,`uamtk`,`newapptk`,`isLoggedIn`,`lastLoginTime`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUsername());
        statement.bindString(3, entity.getPassword());
        statement.bindString(4, entity.getRealName());
        statement.bindString(5, entity.getIdType());
        statement.bindString(6, entity.getIdNo());
        statement.bindString(7, entity.getPhone());
        statement.bindString(8, entity.getEmail());
        statement.bindString(9, entity.getToken());
        statement.bindString(10, entity.getUamtk());
        statement.bindString(11, entity.getNewapptk());
        final int _tmp = entity.isLoggedIn() ? 1 : 0;
        statement.bindLong(12, _tmp);
        statement.bindLong(13, entity.getLastLoginTime());
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `users` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfUserEntity = new EntityDeletionOrUpdateAdapter<UserEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `id` = ?,`username` = ?,`password` = ?,`realName` = ?,`idType` = ?,`idNo` = ?,`phone` = ?,`email` = ?,`token` = ?,`uamtk` = ?,`newapptk` = ?,`isLoggedIn` = ?,`lastLoginTime` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getUsername());
        statement.bindString(3, entity.getPassword());
        statement.bindString(4, entity.getRealName());
        statement.bindString(5, entity.getIdType());
        statement.bindString(6, entity.getIdNo());
        statement.bindString(7, entity.getPhone());
        statement.bindString(8, entity.getEmail());
        statement.bindString(9, entity.getToken());
        statement.bindString(10, entity.getUamtk());
        statement.bindString(11, entity.getNewapptk());
        final int _tmp = entity.isLoggedIn() ? 1 : 0;
        statement.bindLong(12, _tmp);
        statement.bindLong(13, entity.getLastLoginTime());
        statement.bindLong(14, entity.getCreatedAt());
        statement.bindLong(15, entity.getUpdatedAt());
        statement.bindLong(16, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteUserById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAllUsers = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM users";
        return _query;
      }
    };
    this.__preparedStmtOfLogoutAllUsers = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET isLoggedIn = 0";
        return _query;
      }
    };
    this.__preparedStmtOfLoginUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET isLoggedIn = 1, token = ?, uamtk = ?, newapptk = ?, lastLoginTime = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfLogoutUser = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET isLoggedIn = 0 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateToken = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE users SET token = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertUser(final UserEntity user, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfUserEntity.insertAndReturnId(user);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertUsers(final List<UserEntity> users,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserEntity.insert(users);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfUserEntity.handle(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateUser(final UserEntity user, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfUserEntity.handle(user);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object loginAndLogoutOthers(final UserEntity user, final String token, final String uamtk,
      final String newapptk, final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> UserDao.DefaultImpls.loginAndLogoutOthers(UserDao_Impl.this, user, token, uamtk, newapptk, __cont), $completion);
  }

  @Override
  public Object deleteUserById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteUserById.acquire();
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
          __preparedStmtOfDeleteUserById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAllUsers(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAllUsers.acquire();
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
          __preparedStmtOfDeleteAllUsers.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object logoutAllUsers(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLogoutAllUsers.acquire();
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
          __preparedStmtOfLogoutAllUsers.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object loginUser(final long id, final String token, final String uamtk,
      final String newapptk, final long loginTime, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLoginUser.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, token);
        _argIndex = 2;
        _stmt.bindString(_argIndex, uamtk);
        _argIndex = 3;
        _stmt.bindString(_argIndex, newapptk);
        _argIndex = 4;
        _stmt.bindLong(_argIndex, loginTime);
        _argIndex = 5;
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
          __preparedStmtOfLoginUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object logoutUser(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfLogoutUser.acquire();
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
          __preparedStmtOfLogoutUser.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateToken(final long id, final String token,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateToken.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, token);
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
          __preparedStmtOfUpdateToken.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserEntity> getLoggedInUser() {
    final String _sql = "SELECT * FROM users WHERE isLoggedIn = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"users"}, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfRealName = CursorUtil.getColumnIndexOrThrow(_cursor, "realName");
          final int _cursorIndexOfIdType = CursorUtil.getColumnIndexOrThrow(_cursor, "idType");
          final int _cursorIndexOfIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "idNo");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUamtk = CursorUtil.getColumnIndexOrThrow(_cursor, "uamtk");
          final int _cursorIndexOfNewapptk = CursorUtil.getColumnIndexOrThrow(_cursor, "newapptk");
          final int _cursorIndexOfIsLoggedIn = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoggedIn");
          final int _cursorIndexOfLastLoginTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPassword;
            _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            final String _tmpRealName;
            _tmpRealName = _cursor.getString(_cursorIndexOfRealName);
            final String _tmpIdType;
            _tmpIdType = _cursor.getString(_cursorIndexOfIdType);
            final String _tmpIdNo;
            _tmpIdNo = _cursor.getString(_cursorIndexOfIdNo);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUamtk;
            _tmpUamtk = _cursor.getString(_cursorIndexOfUamtk);
            final String _tmpNewapptk;
            _tmpNewapptk = _cursor.getString(_cursorIndexOfNewapptk);
            final boolean _tmpIsLoggedIn;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLoggedIn);
            _tmpIsLoggedIn = _tmp != 0;
            final long _tmpLastLoginTime;
            _tmpLastLoginTime = _cursor.getLong(_cursorIndexOfLastLoginTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserEntity(_tmpId,_tmpUsername,_tmpPassword,_tmpRealName,_tmpIdType,_tmpIdNo,_tmpPhone,_tmpEmail,_tmpToken,_tmpUamtk,_tmpNewapptk,_tmpIsLoggedIn,_tmpLastLoginTime,_tmpCreatedAt,_tmpUpdatedAt);
          } else {
            _result = null;
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
  public Object getUserById(final long id, final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfRealName = CursorUtil.getColumnIndexOrThrow(_cursor, "realName");
          final int _cursorIndexOfIdType = CursorUtil.getColumnIndexOrThrow(_cursor, "idType");
          final int _cursorIndexOfIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "idNo");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUamtk = CursorUtil.getColumnIndexOrThrow(_cursor, "uamtk");
          final int _cursorIndexOfNewapptk = CursorUtil.getColumnIndexOrThrow(_cursor, "newapptk");
          final int _cursorIndexOfIsLoggedIn = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoggedIn");
          final int _cursorIndexOfLastLoginTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPassword;
            _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            final String _tmpRealName;
            _tmpRealName = _cursor.getString(_cursorIndexOfRealName);
            final String _tmpIdType;
            _tmpIdType = _cursor.getString(_cursorIndexOfIdType);
            final String _tmpIdNo;
            _tmpIdNo = _cursor.getString(_cursorIndexOfIdNo);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUamtk;
            _tmpUamtk = _cursor.getString(_cursorIndexOfUamtk);
            final String _tmpNewapptk;
            _tmpNewapptk = _cursor.getString(_cursorIndexOfNewapptk);
            final boolean _tmpIsLoggedIn;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLoggedIn);
            _tmpIsLoggedIn = _tmp != 0;
            final long _tmpLastLoginTime;
            _tmpLastLoginTime = _cursor.getLong(_cursorIndexOfLastLoginTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserEntity(_tmpId,_tmpUsername,_tmpPassword,_tmpRealName,_tmpIdType,_tmpIdNo,_tmpPhone,_tmpEmail,_tmpToken,_tmpUamtk,_tmpNewapptk,_tmpIsLoggedIn,_tmpLastLoginTime,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getUserByUsername(final String username,
      final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE username = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, username);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfRealName = CursorUtil.getColumnIndexOrThrow(_cursor, "realName");
          final int _cursorIndexOfIdType = CursorUtil.getColumnIndexOrThrow(_cursor, "idType");
          final int _cursorIndexOfIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "idNo");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUamtk = CursorUtil.getColumnIndexOrThrow(_cursor, "uamtk");
          final int _cursorIndexOfNewapptk = CursorUtil.getColumnIndexOrThrow(_cursor, "newapptk");
          final int _cursorIndexOfIsLoggedIn = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoggedIn");
          final int _cursorIndexOfLastLoginTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPassword;
            _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            final String _tmpRealName;
            _tmpRealName = _cursor.getString(_cursorIndexOfRealName);
            final String _tmpIdType;
            _tmpIdType = _cursor.getString(_cursorIndexOfIdType);
            final String _tmpIdNo;
            _tmpIdNo = _cursor.getString(_cursorIndexOfIdNo);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUamtk;
            _tmpUamtk = _cursor.getString(_cursorIndexOfUamtk);
            final String _tmpNewapptk;
            _tmpNewapptk = _cursor.getString(_cursorIndexOfNewapptk);
            final boolean _tmpIsLoggedIn;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLoggedIn);
            _tmpIsLoggedIn = _tmp != 0;
            final long _tmpLastLoginTime;
            _tmpLastLoginTime = _cursor.getLong(_cursorIndexOfLastLoginTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserEntity(_tmpId,_tmpUsername,_tmpPassword,_tmpRealName,_tmpIdType,_tmpIdNo,_tmpPhone,_tmpEmail,_tmpToken,_tmpUamtk,_tmpNewapptk,_tmpIsLoggedIn,_tmpLastLoginTime,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<List<UserEntity>> getAllUsers() {
    final String _sql = "SELECT * FROM users ORDER BY lastLoginTime DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"users"}, new Callable<List<UserEntity>>() {
      @Override
      @NonNull
      public List<UserEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfRealName = CursorUtil.getColumnIndexOrThrow(_cursor, "realName");
          final int _cursorIndexOfIdType = CursorUtil.getColumnIndexOrThrow(_cursor, "idType");
          final int _cursorIndexOfIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "idNo");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUamtk = CursorUtil.getColumnIndexOrThrow(_cursor, "uamtk");
          final int _cursorIndexOfNewapptk = CursorUtil.getColumnIndexOrThrow(_cursor, "newapptk");
          final int _cursorIndexOfIsLoggedIn = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoggedIn");
          final int _cursorIndexOfLastLoginTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<UserEntity> _result = new ArrayList<UserEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final UserEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPassword;
            _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            final String _tmpRealName;
            _tmpRealName = _cursor.getString(_cursorIndexOfRealName);
            final String _tmpIdType;
            _tmpIdType = _cursor.getString(_cursorIndexOfIdType);
            final String _tmpIdNo;
            _tmpIdNo = _cursor.getString(_cursorIndexOfIdNo);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUamtk;
            _tmpUamtk = _cursor.getString(_cursorIndexOfUamtk);
            final String _tmpNewapptk;
            _tmpNewapptk = _cursor.getString(_cursorIndexOfNewapptk);
            final boolean _tmpIsLoggedIn;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLoggedIn);
            _tmpIsLoggedIn = _tmp != 0;
            final long _tmpLastLoginTime;
            _tmpLastLoginTime = _cursor.getLong(_cursorIndexOfLastLoginTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new UserEntity(_tmpId,_tmpUsername,_tmpPassword,_tmpRealName,_tmpIdType,_tmpIdNo,_tmpPhone,_tmpEmail,_tmpToken,_tmpUamtk,_tmpNewapptk,_tmpIsLoggedIn,_tmpLastLoginTime,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getLoggedInUserSync(final Continuation<? super UserEntity> $completion) {
    final String _sql = "SELECT * FROM users WHERE isLoggedIn = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserEntity>() {
      @Override
      @Nullable
      public UserEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfUsername = CursorUtil.getColumnIndexOrThrow(_cursor, "username");
          final int _cursorIndexOfPassword = CursorUtil.getColumnIndexOrThrow(_cursor, "password");
          final int _cursorIndexOfRealName = CursorUtil.getColumnIndexOrThrow(_cursor, "realName");
          final int _cursorIndexOfIdType = CursorUtil.getColumnIndexOrThrow(_cursor, "idType");
          final int _cursorIndexOfIdNo = CursorUtil.getColumnIndexOrThrow(_cursor, "idNo");
          final int _cursorIndexOfPhone = CursorUtil.getColumnIndexOrThrow(_cursor, "phone");
          final int _cursorIndexOfEmail = CursorUtil.getColumnIndexOrThrow(_cursor, "email");
          final int _cursorIndexOfToken = CursorUtil.getColumnIndexOrThrow(_cursor, "token");
          final int _cursorIndexOfUamtk = CursorUtil.getColumnIndexOrThrow(_cursor, "uamtk");
          final int _cursorIndexOfNewapptk = CursorUtil.getColumnIndexOrThrow(_cursor, "newapptk");
          final int _cursorIndexOfIsLoggedIn = CursorUtil.getColumnIndexOrThrow(_cursor, "isLoggedIn");
          final int _cursorIndexOfLastLoginTime = CursorUtil.getColumnIndexOrThrow(_cursor, "lastLoginTime");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final UserEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpUsername;
            _tmpUsername = _cursor.getString(_cursorIndexOfUsername);
            final String _tmpPassword;
            _tmpPassword = _cursor.getString(_cursorIndexOfPassword);
            final String _tmpRealName;
            _tmpRealName = _cursor.getString(_cursorIndexOfRealName);
            final String _tmpIdType;
            _tmpIdType = _cursor.getString(_cursorIndexOfIdType);
            final String _tmpIdNo;
            _tmpIdNo = _cursor.getString(_cursorIndexOfIdNo);
            final String _tmpPhone;
            _tmpPhone = _cursor.getString(_cursorIndexOfPhone);
            final String _tmpEmail;
            _tmpEmail = _cursor.getString(_cursorIndexOfEmail);
            final String _tmpToken;
            _tmpToken = _cursor.getString(_cursorIndexOfToken);
            final String _tmpUamtk;
            _tmpUamtk = _cursor.getString(_cursorIndexOfUamtk);
            final String _tmpNewapptk;
            _tmpNewapptk = _cursor.getString(_cursorIndexOfNewapptk);
            final boolean _tmpIsLoggedIn;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsLoggedIn);
            _tmpIsLoggedIn = _tmp != 0;
            final long _tmpLastLoginTime;
            _tmpLastLoginTime = _cursor.getLong(_cursorIndexOfLastLoginTime);
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new UserEntity(_tmpId,_tmpUsername,_tmpPassword,_tmpRealName,_tmpIdType,_tmpIdNo,_tmpPhone,_tmpEmail,_tmpToken,_tmpUamtk,_tmpNewapptk,_tmpIsLoggedIn,_tmpLastLoginTime,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getUserCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM users";
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
