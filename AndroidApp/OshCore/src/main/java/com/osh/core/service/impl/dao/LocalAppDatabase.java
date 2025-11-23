package com.osh.core.service.impl.dao;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.osh.core.actor.DBActor;
import com.osh.core.actor.DBAudioActor;
import com.osh.core.actor.DBShutterActor;
import com.osh.core.datamodel.meta.AudioPlaybackSource;
import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.datamodel.meta.KnownRoom;
import com.osh.core.datamodel.meta.KnownRoomActors;
import com.osh.core.datamodel.meta.KnownRoomValues;
import com.osh.core.device.KnownDevice;
import com.osh.core.service.impl.DatabaseVersion;
import com.osh.core.user.User;
import com.osh.core.value.DBValue;
import com.osh.core.value.ValueGroup;

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
        KnownRoomActors.class,
        KnownDevice.class,
        User.class,
        DatabaseVersion.class
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

    public abstract KnownDevicesDao getKnownDevicesDao();

    public abstract VersionDao getVersionDao();

    public abstract UserDao getUserDao();

}