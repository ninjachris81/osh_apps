package com.osh.service.impl.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.osh.actor.DBActor;
import com.osh.actor.DBAudioActor;
import com.osh.actor.DBShutterActor;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.datamodel.meta.KnownRoomActors;
import com.osh.datamodel.meta.KnownRoomValues;
import com.osh.value.DBValue;
import com.osh.value.ValueGroup;

@Database(entities = {
        DBValue.class,
        ValueGroup.class,
        KnownRoom.class,
        KnownArea.class,
        DBActor.class,
        DBAudioActor.class,
        DBShutterActor.class,
        AudioPlaybackSource.class,
        KnownRoomValues.class,
        KnownRoomActors.class
}, version = 1)
public abstract class LocalAppDatabase extends RoomDatabase {

    public abstract MergeDao getMergeDao();

    public abstract ValueGroupDao getValueGroupDao();

    public abstract ValueDao getValueDao();

    public abstract KnownRoomDao getKnownRoomDao();

    public abstract KnownAreaDao getKnownAreaDao();

    public abstract ActorDao getActorDao();

    public abstract AudioActorDao getAudioActorDao();

    public abstract ShutterActorDao getShutterActorDao();

    public abstract AudioPlaybackSourceDao getAudioPlaybackSourceDao();

    public abstract KnownRoomValuesDao getKnownRoomValuesDao();

    public abstract KnownRoomActorsDao getKnownRoomActorsDao();
}