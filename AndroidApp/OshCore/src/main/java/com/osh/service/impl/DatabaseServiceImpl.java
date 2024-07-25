package com.osh.service.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.osh.actor.DBActor;
import com.osh.actor.DBAudioActor;
import com.osh.actor.DBShutterActor;
import com.osh.database.config.DatabaseConfig;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.datamodel.meta.KnownRoomActors;
import com.osh.datamodel.meta.KnownRoomValues;
import com.osh.device.KnownDevice;
import com.osh.service.IDatabaseService;
import com.osh.user.User;
import com.osh.value.DBValue;
import com.osh.value.ValueGroup;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DatabaseServiceImpl extends DatabaseServiceBaseImpl implements IDatabaseService {

    private static final String TAG = DatabaseServiceImpl.class.getName();

    protected Dao<ValueGroup, String> valueGroupDao;
    protected Dao<DBValue, String> valueDao;

    protected Dao<DBActor, String> actorDao;
    protected Dao<DBAudioActor, String> audioActorDao;

    protected Dao<AudioPlaybackSource, String> audioPlaybackSourceDao;
    protected Dao<DBShutterActor, String> shutterActorDao;

    protected Dao<KnownRoom, String> knownRoomsDao;

    protected Dao<KnownArea, String> knownAreaDao;

    protected Dao<KnownRoomValues, String> knownRoomValuesDao;

    protected Dao<KnownRoomActors, String> knownRoomActorsDao;

    protected Dao<KnownDevice, String> knownDevicesDao;

    protected Dao<User, String> userDao;

    private final ConnectionSource connectionSource;

    public DatabaseServiceImpl(DatabaseConfig config, boolean initDao) throws SQLException {
        String databaseUrl = "jdbc:postgresql://" + config.getHost() + ":" + config.getPort() + "/" + config.getName();
        // create a connection source to our database
        connectionSource = new JdbcConnectionSource(databaseUrl, config.getUsername(), config.getPassword());

        if (initDao) {
            initDao();
        }
    }

    private void initDao() throws SQLException {
        valueGroupDao = DaoManager.createDao(connectionSource, ValueGroup.class);
        valueDao = DaoManager.createDao(connectionSource, DBValue.class);
        actorDao = DaoManager.createDao(connectionSource, DBActor.class);
        audioActorDao = DaoManager.createDao(connectionSource, DBAudioActor.class);
        audioPlaybackSourceDao = DaoManager.createDao(connectionSource, AudioPlaybackSource.class);
        shutterActorDao = DaoManager.createDao(connectionSource, DBShutterActor.class);
        knownRoomsDao = DaoManager.createDao(connectionSource, KnownRoom.class);
        knownAreaDao = DaoManager.createDao(connectionSource, KnownArea.class);
        knownRoomValuesDao = DaoManager.createDao(connectionSource, KnownRoomValues.class);
        knownRoomActorsDao = DaoManager.createDao(connectionSource, KnownRoomActors.class);
        knownDevicesDao = DaoManager.createDao(connectionSource, KnownDevice.class);
        userDao = DaoManager.createDao(connectionSource, User.class);
    }

    @Override
    public long getVersion() {
        try {
            return connectionSource.getReadOnlyConnection("dm_version").queryForLong("SELECT version FROM dm_version");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean canUpdate() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        try {
            return valueGroupDao.countOf() == 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void resetDatabase() {
        throw new UnsupportedOperationException();
    }

    @Override
    protected List<ValueGroup> loadValueGroups() {
        try {
            return valueGroupDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<DBValue> loadValues() {
        try {
            return valueDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<DBActor> loadActors() {
        try {
            return actorDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<AudioPlaybackSource> loadAudioPlaybackSources() {
        try {
            return audioPlaybackSourceDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<KnownArea> loadKnownAreas() {
        try {
            return knownAreaDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<KnownRoom> loadKnownRooms() {
        try {
            return knownRoomsDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<DBShutterActor> loadShutterActors(String id, String valueGroupId) {
        try {
            return shutterActorDao.queryForFieldValues(Map.of("id", id, "value_group_id", valueGroupId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<DBAudioActor> loadAudioActors(String id, String valueGroupId) {
        try {
            return audioActorDao.queryForFieldValues(Map.of("id", id, "value_group_id", valueGroupId));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<KnownRoomValues> loadKnownRoomValues() {
        try {
            return knownRoomValuesDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<KnownRoomActors> loadKnownRoomActors() {
        try {
            return knownRoomActorsDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<KnownDevice> loadKnownDevices() {
        try {
            return knownDevicesDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected List<User> loadUsers() {
        try {
            return userDao.queryForAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
