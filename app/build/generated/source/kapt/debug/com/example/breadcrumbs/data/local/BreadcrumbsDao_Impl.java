package com.example.breadcrumbs.data.local;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class BreadcrumbsDao_Impl implements BreadcrumbsDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<LocalUser> __insertAdapterOfLocalUser;

  private final EntityInsertAdapter<LocalTrip> __insertAdapterOfLocalTrip;

  private final EntityInsertAdapter<LocalPoi> __insertAdapterOfLocalPoi;

  public BreadcrumbsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfLocalUser = new EntityInsertAdapter<LocalUser>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `users` (`id`,`name`,`bio`,`email`,`profilePictureUrl`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final LocalUser entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getBio() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getBio());
        }
        if (entity.getEmail() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getEmail());
        }
        if (entity.getProfilePictureUrl() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getProfilePictureUrl());
        }
      }
    };
    this.__insertAdapterOfLocalTrip = new EntityInsertAdapter<LocalTrip>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `trips` (`id`,`userId`,`title`,`startDate`,`endDate`,`coverImageUrl`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final LocalTrip entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
        if (entity.getUserId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getUserId());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getTitle());
        }
        statement.bindLong(4, entity.getStartDate());
        statement.bindLong(5, entity.getEndDate());
        if (entity.getCoverImageUrl() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getCoverImageUrl());
        }
      }
    };
    this.__insertAdapterOfLocalPoi = new EntityInsertAdapter<LocalPoi>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `pois` (`id`,`tripId`,`locationName`,`latitude`,`longitude`,`timestamp`,`description`,`imageUrl`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final LocalPoi entity) {
        if (entity.getId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindText(1, entity.getId());
        }
        if (entity.getTripId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getTripId());
        }
        if (entity.getLocationName() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getLocationName());
        }
        statement.bindDouble(4, entity.getLatitude());
        statement.bindDouble(5, entity.getLongitude());
        statement.bindLong(6, entity.getTimestamp());
        if (entity.getDescription() == null) {
          statement.bindNull(7);
        } else {
          statement.bindText(7, entity.getDescription());
        }
        if (entity.getImageUrl() == null) {
          statement.bindNull(8);
        } else {
          statement.bindText(8, entity.getImageUrl());
        }
      }
    };
  }

  @Override
  public Object insertUser(final LocalUser user, final Continuation<? super Unit> $completion) {
    if (user == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfLocalUser.insert(_connection, user);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertTrip(final LocalTrip trip, final Continuation<? super Unit> $completion) {
    if (trip == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfLocalTrip.insert(_connection, trip);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertTrips(final List<LocalTrip> trips,
      final Continuation<? super Unit> $completion) {
    if (trips == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfLocalTrip.insert(_connection, trips);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertPoi(final LocalPoi poi, final Continuation<? super Unit> $completion) {
    if (poi == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfLocalPoi.insert(_connection, poi);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object insertPois(final List<LocalPoi> pois,
      final Continuation<? super Unit> $completion) {
    if (pois == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __insertAdapterOfLocalPoi.insert(_connection, pois);
      return Unit.INSTANCE;
    }, $completion);
  }

  @Override
  public Object getUser(final String userId, final Continuation<? super LocalUser> $completion) {
    final String _sql = "SELECT * FROM users WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfBio = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "bio");
        final int _columnIndexOfEmail = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "email");
        final int _columnIndexOfProfilePictureUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "profilePictureUrl");
        final LocalUser _result;
        if (_stmt.step()) {
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpBio;
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null;
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio);
          }
          final String _tmpEmail;
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null;
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail);
          }
          final String _tmpProfilePictureUrl;
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null;
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl);
          }
          _result = new LocalUser(_tmpId,_tmpName,_tmpBio,_tmpEmail,_tmpProfilePictureUrl);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<LocalTrip>> getAllTrips() {
    final String _sql = "SELECT * FROM trips ORDER BY startDate DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"trips"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfStartDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "startDate");
        final int _columnIndexOfEndDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "endDate");
        final int _columnIndexOfCoverImageUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "coverImageUrl");
        final List<LocalTrip> _result = new ArrayList<LocalTrip>();
        while (_stmt.step()) {
          final LocalTrip _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpStartDate;
          _tmpStartDate = _stmt.getLong(_columnIndexOfStartDate);
          final long _tmpEndDate;
          _tmpEndDate = _stmt.getLong(_columnIndexOfEndDate);
          final String _tmpCoverImageUrl;
          if (_stmt.isNull(_columnIndexOfCoverImageUrl)) {
            _tmpCoverImageUrl = null;
          } else {
            _tmpCoverImageUrl = _stmt.getText(_columnIndexOfCoverImageUrl);
          }
          _item = new LocalTrip(_tmpId,_tmpUserId,_tmpTitle,_tmpStartDate,_tmpEndDate,_tmpCoverImageUrl);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getTrip(final String tripId, final Continuation<? super LocalTrip> $completion) {
    final String _sql = "SELECT * FROM trips WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (tripId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, tripId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfStartDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "startDate");
        final int _columnIndexOfEndDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "endDate");
        final int _columnIndexOfCoverImageUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "coverImageUrl");
        final LocalTrip _result;
        if (_stmt.step()) {
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpUserId;
          if (_stmt.isNull(_columnIndexOfUserId)) {
            _tmpUserId = null;
          } else {
            _tmpUserId = _stmt.getText(_columnIndexOfUserId);
          }
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpStartDate;
          _tmpStartDate = _stmt.getLong(_columnIndexOfStartDate);
          final long _tmpEndDate;
          _tmpEndDate = _stmt.getLong(_columnIndexOfEndDate);
          final String _tmpCoverImageUrl;
          if (_stmt.isNull(_columnIndexOfCoverImageUrl)) {
            _tmpCoverImageUrl = null;
          } else {
            _tmpCoverImageUrl = _stmt.getText(_columnIndexOfCoverImageUrl);
          }
          _result = new LocalTrip(_tmpId,_tmpUserId,_tmpTitle,_tmpStartDate,_tmpEndDate,_tmpCoverImageUrl);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Flow<List<LocalPoi>> getPoisForTrip(final String tripId) {
    final String _sql = "SELECT * FROM pois WHERE tripId = ? ORDER BY timestamp ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"pois"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (tripId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, tripId);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfTripId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "tripId");
        final int _columnIndexOfLocationName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "locationName");
        final int _columnIndexOfLatitude = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "latitude");
        final int _columnIndexOfLongitude = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "longitude");
        final int _columnIndexOfTimestamp = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timestamp");
        final int _columnIndexOfDescription = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "description");
        final int _columnIndexOfImageUrl = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "imageUrl");
        final List<LocalPoi> _result = new ArrayList<LocalPoi>();
        while (_stmt.step()) {
          final LocalPoi _item;
          final String _tmpId;
          if (_stmt.isNull(_columnIndexOfId)) {
            _tmpId = null;
          } else {
            _tmpId = _stmt.getText(_columnIndexOfId);
          }
          final String _tmpTripId;
          if (_stmt.isNull(_columnIndexOfTripId)) {
            _tmpTripId = null;
          } else {
            _tmpTripId = _stmt.getText(_columnIndexOfTripId);
          }
          final String _tmpLocationName;
          if (_stmt.isNull(_columnIndexOfLocationName)) {
            _tmpLocationName = null;
          } else {
            _tmpLocationName = _stmt.getText(_columnIndexOfLocationName);
          }
          final double _tmpLatitude;
          _tmpLatitude = _stmt.getDouble(_columnIndexOfLatitude);
          final double _tmpLongitude;
          _tmpLongitude = _stmt.getDouble(_columnIndexOfLongitude);
          final long _tmpTimestamp;
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp);
          final String _tmpDescription;
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null;
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription);
          }
          final String _tmpImageUrl;
          if (_stmt.isNull(_columnIndexOfImageUrl)) {
            _tmpImageUrl = null;
          } else {
            _tmpImageUrl = _stmt.getText(_columnIndexOfImageUrl);
          }
          _item = new LocalPoi(_tmpId,_tmpTripId,_tmpLocationName,_tmpLatitude,_tmpLongitude,_tmpTimestamp,_tmpDescription,_tmpImageUrl);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object deletePoisForTrip(final String tripId,
      final Continuation<? super Unit> $completion) {
    final String _sql = "DELETE FROM pois WHERE tripId = ?";
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (tripId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, tripId);
        }
        _stmt.step();
        return Unit.INSTANCE;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
