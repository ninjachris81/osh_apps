package com.osh.service.impl;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.osh.actor.ActorBase;
import com.osh.actor.AudioPlaybackActor;
import com.osh.actor.DBActor;
import com.osh.actor.DBAudioActor;
import com.osh.actor.DBShutterActor;
import com.osh.database.config.DatabaseConfig;
import com.osh.datamodel.DatamodelBase;
import com.osh.datamodel.DynamicDatamodel;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.datamodel.meta.KnownRoomActors;
import com.osh.datamodel.meta.KnownRoomValues;
import com.osh.log.LogFacade;
import com.osh.service.IDatabaseService;
import com.osh.value.DBValue;
import com.osh.value.StringValue;
import com.osh.value.ValueBase;
import com.osh.value.ValueGroup;
import com.osh.value.ValueType;

import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DatabaseServiceImpl extends DatabaseServiceBaseImpl implements IDatabaseService {

    private static final String TAG = DatabaseServiceImpl.class.getName();

    protected final Dao<ValueGroup, String> valueGroupDao;
    protected final Dao<DBValue, String> valueDao;

    protected final Dao<DBActor, String> actorDao;
    protected final Dao<DBAudioActor, String> audioActorDao;

    protected final Dao<AudioPlaybackSource, String> audioPlaybackSourceDao;
    protected final Dao<DBShutterActor, String> shutterActorDao;

    protected final Dao<KnownRoom, String> knownRoomsDao;

    protected final Dao<KnownArea, String> knownAreaDao;

    protected final Dao<KnownRoomValues, String> knownRoomValuesDao;

    protected final Dao<KnownRoomActors, String> knownRoomActorsDao;


    private final ConnectionSource connectionSource;

    public DatabaseServiceImpl(DatabaseConfig config) throws SQLException {
        String databaseUrl = "jdbc:postgresql://" + config.getHost() + ":" + config.getPort() + "/" + config.getName();
        // create a connection source to our database
        connectionSource = new JdbcConnectionSource(databaseUrl, config.getUsername(), config.getPassword());

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
}
