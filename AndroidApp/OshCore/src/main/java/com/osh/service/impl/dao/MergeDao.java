package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Transaction;

import com.osh.actor.ActorBase;
import com.osh.actor.AudioPlaybackActor;
import com.osh.actor.DBActor;
import com.osh.actor.DBShutterActor;
import com.osh.actor.ShutterActor;
import com.osh.datamodel.DatamodelBase;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.log.LogFacade;
import com.osh.service.impl.LocalDatabaseServiceImpl;
import com.osh.value.DBValue;
import com.osh.value.ValueBase;
import com.osh.value.ValueGroup;

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
                               AudioPlaybackSourceDao audioPlaybackSourceDao
                               ) {

        for(KnownArea knownArea : datamodel.getKnownAreas()) {
            LogFacade.d(TAG, "Inserting known area " + knownArea.getId());
            knownAreaDao.insert(knownArea);
        }

        for (KnownRoom knownRoom : datamodel.getKnownRooms().values()) {
            LogFacade.d(TAG, "Inserting known room " + knownRoom.getId());
            knownRoomDao.insert(knownRoom);
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

        for (AudioPlaybackSource audioPlaybackSource : datamodel.getAudioPlaybackSources().values()) {
            LogFacade.d(TAG, "Inserting audio playback source " + audioPlaybackSource.getId());
            audioPlaybackSourceDao.insert(audioPlaybackSource);
        }

    }

}
