package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;

import com.osh.core.actor.ActorBase;
import com.osh.core.actor.AudioPlaybackActor;
import com.osh.core.actor.ShutterActor;
import com.osh.core.datamodel.DatamodelBase;
import com.osh.core.datamodel.meta.AudioPlaybackSource;
import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.datamodel.meta.KnownRoom;
import com.osh.core.datamodel.meta.KnownRoomActors;
import com.osh.core.datamodel.meta.KnownRoomValues;
import com.osh.core.device.KnownDevice;
import com.osh.core.log.LogFacade;
import com.osh.core.service.impl.DatabaseVersion;
import com.osh.core.service.impl.LocalDatabaseServiceImpl;
import com.osh.core.user.User;
import com.osh.core.value.ValueBase;
import com.osh.core.value.ValueGroup;

@Dao
public abstract class MergeDao {

    private static final String TAG = LocalDatabaseServiceImpl.class.getName();

    @Insert
    @Transaction
    public void mergeDatamodel(DatamodelBase datamodel,
                               ValueGroupDao valueGroupDao,
                               ValueDao valueDao,
                               KnownRoomDao knownRoomDao,
                               KnownAreaDao knownAreaDao,
                               ActorDao actorDao,
                               AudioActorDao audioActorDao,
                               ShutterActorDao shutterActorDao,
                               AudioPlaybackSourceDao audioPlaybackSourceDao,
                               KnownRoomValuesDao knownRoomValuesDao,
                               KnownRoomActorsDao knownRoomActorsDao,
                               KnownDevicesDao knownDevicesDao,
                               UserDao userDao,
                               VersionDao versionDao,
                               long version) {

        for(KnownArea knownArea : datamodel.getKnownAreas()) {
            LogFacade.d(TAG, "Inserting known area " + knownArea.getId());
            knownAreaDao.insert(knownArea);
        }

        for (ValueGroup valueGroup : datamodel.getValueGroups().values()) {
            LogFacade.d(TAG, "Inserting value group " + valueGroup.getId());
            valueGroupDao.insert(valueGroup);
        }

        for (ValueBase value : datamodel.getValues().values()) {
            LogFacade.d(TAG, "Inserting value " + value.getFullId());
            valueDao.insert(value.toDBValue());
        }

        for (ActorBase actor : datamodel.getActors().values()) {
            LogFacade.d(TAG, "Inserting actor " + actor.getFullId());
            actorDao.insert(actor.toDBActor());

            if (actor instanceof AudioPlaybackActor) {
                LogFacade.d(TAG, "Inserting audio actor " + actor.getFullId());
                audioActorDao.insert(((AudioPlaybackActor)actor).toDBAudioActor());
            } else if (actor instanceof ShutterActor) {
                LogFacade.d(TAG, "Inserting audio actor " + actor.getFullId());
                shutterActorDao.insert(((ShutterActor) actor).toDBShutterActor());
            }
        }

        for (KnownRoom knownRoom : datamodel.getKnownRooms().values()) {
            LogFacade.d(TAG, "Inserting known room " + knownRoom.getId());
            knownRoomDao.insert(knownRoom);

            for (ValueBase value : knownRoom.getValues().values()) {
                LogFacade.d(TAG, "Inserting known room value " + value.getId());
                knownRoomValuesDao.insert(new KnownRoomValues(knownRoom.getId(), value.getValueGroup().getId(), value.getId()));
            }

            for (ActorBase actor : knownRoom.getActors().values()) {
                knownRoomActorsDao.insert(new KnownRoomActors(knownRoom.getId(), actor.getValueGroup().getId(), actor.getId()));
            }
        }

        for (AudioPlaybackSource audioPlaybackSource : datamodel.getAudioPlaybackSources().values()) {
            LogFacade.d(TAG, "Inserting audio playback source " + audioPlaybackSource.getId());
            audioPlaybackSourceDao.insert(audioPlaybackSource);
        }

        for (KnownDevice knownDevice : datamodel.getKnownDevices().values()) {
            LogFacade.d(TAG, "Inserting known device " + knownDevice.getFullId());
            knownDevicesDao.insert(knownDevice);
        }

        for (User user : datamodel.getUsers().values()) {
            LogFacade.d(TAG, "Inserting user " + user.getId());
            userDao.insert(user);
        }

        versionDao.setVersion(new DatabaseVersion(version));
    }

}
