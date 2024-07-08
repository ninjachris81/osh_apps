package com.osh.service.impl;

import android.content.Context;

import com.osh.actor.DBActor;
import com.osh.actor.DBAudioActor;
import com.osh.actor.DBShutterActor;
import com.osh.database.config.DatabaseConfig;
import com.osh.datamodel.DatamodelBase;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.datamodel.meta.KnownRoomActors;
import com.osh.datamodel.meta.KnownRoomValues;
import com.osh.log.LogFacade;
import com.osh.service.IDatabaseService;
import com.osh.service.impl.dao.LocalAppDatabase;
import com.osh.value.DBValue;
import com.osh.value.ValueGroup;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.room.Room;

public class LocalDatabaseServiceImpl extends DatabaseServiceBaseImpl implements IDatabaseService {

    private static final String TAG = LocalDatabaseServiceImpl.class.getName();

    private final LocalAppDatabase appDB;

    public void copyData(IDatabaseService databaseService) {
        LogFacade.i(TAG, "Copying from remote database");
        DatamodelBase datamodel = databaseService.loadDatamodel();
        saveDatamodel(datamodel);
    }

    private void saveDatamodel(DatamodelBase datamodel) {
        LogFacade.i(TAG, "Saving datamodel");
        appDB.getMergeDao().mergeDatamodel(datamodel,
                appDB.getValueGroupDao(),
                appDB.getValueDao(),
                appDB.getKnownRoomDao(),
                appDB.getKnownAreaDao(),
                appDB.getActorDao(),
                appDB.getAudioActorDao(),
                appDB.getShutterActorDao(),
                appDB.getAudioPlaybackSourceDao()
        );
    }

    public LocalDatabaseServiceImpl(Context context, DatabaseConfig config) throws SQLException {
        appDB = Room.databaseBuilder(context, LocalAppDatabase.class, config.getName()).build();
    }

    @Override
    public boolean isEmpty() {
        return appDB.getValueGroupDao().getCount() == 0;
    }

    @Override
    protected List<ValueGroup> loadValueGroups() {
        return appDB.getValueGroupDao().getAll();
    }

    @Override
    protected List<DBValue> loadValues() {
        return appDB.getValueDao().getAll();
    }

    @Override
    protected List<DBActor> loadActors() {
        return appDB.getActorDao().getAll();
    }

    @Override
    protected List<AudioPlaybackSource> loadAudioPlaybackSources() {
        return appDB.getAudioPlaybackSourceDao().getAll();
    }

    @Override
    protected List<KnownArea> loadKnownAreas() {
        return appDB.getKnownAreaDao().getAll();
    }

    @Override
    protected List<KnownRoom> loadKnownRooms() {
        return appDB.getKnownRoomDao().getAll();
    }

    @Override
    protected List<DBShutterActor> loadShutterActors(String id, String valueGroupId) {
        return appDB.getShutterActorDao().getById(id, valueGroupId);
    }

    @Override
    protected List<DBAudioActor> loadAudioActors(String id, String valueGroupId) {
        return appDB.getAudioActorDao().getById(id, valueGroupId);
    }

    @Override
    protected List<KnownRoomValues> loadKnownRoomValues() {
        return appDB.getKnownRoomValuesDao().getAll();
    }

    @Override
    protected List<KnownRoomActors> loadKnownRoomActors() {
        return appDB.getKnownRoomActorsDao().getAll();
    }
}
