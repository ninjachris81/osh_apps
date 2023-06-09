package com.osh.service.impl;

import android.provider.MediaStore;

import com.osh.Identifyable;
import com.osh.actor.ActorBase;
import com.osh.actor.AudioPlaybackActor;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.service.IActorService;
import com.osh.service.IAudioActorService;
import com.osh.service.ICommunicationService;
import com.osh.service.IDatabaseService;
import com.osh.service.IDatamodelService;
import com.osh.value.DoubleValue;
import com.osh.value.StringValue;
import com.osh.value.ValueBase;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AudioActorServiceImpl implements IAudioActorService {

    private static final Logger log = LoggerFactory.getLogger(AudioActorServiceImpl.class);
    private final ICommunicationService communicationService;
    private final IActorService actorService;
    private final IDatamodelService datamodelService;

    protected Map<String, AudioPlaybackActor> audioActors = new HashMap<>();

    public AudioActorServiceImpl(ICommunicationService communicationService, IActorService actorService, IDatamodelService datamodelService) {
        this.communicationService = communicationService;
        this.actorService = actorService;
        this.datamodelService = datamodelService;

        datamodelService.loadedState().addItemChangeListener(item -> {
            if (item.booleanValue()) {
                registerAudioActors();
            }
        }, true);
    }

    private void registerAudioActors() {
        for (ActorBase actor : actorService.getActors(AudioPlaybackActor.class)) {
            registerAudioActor((AudioPlaybackActor) actor);
        }
    }

    private void registerAudioActor(AudioPlaybackActor audioPlaybackActor) {
        if (!StringUtils.isEmpty(audioPlaybackActor.getAudioVolumeId())) {
            ValueBase value = datamodelService.getDatamodel().getValue(audioPlaybackActor.getAudioVolumeId());
            if (value instanceof DoubleValue) {
                DoubleValue volume = (DoubleValue) value;
                audioPlaybackActor.setVolumeValue(volume);
            } else {
                throw new RuntimeException("Unexpected value type: " + value);
            }
        }

        if (!StringUtils.isEmpty(audioPlaybackActor.getAudioUrlId())) {
            ValueBase value = datamodelService.getDatamodel().getValue(audioPlaybackActor.getAudioUrlId());
            if (value instanceof StringValue) {
                StringValue url = (StringValue) value;
                audioPlaybackActor.setUrlValue(url);
            } else {
                throw new RuntimeException("Unexpected value type: " + value);
            }
        }

        audioActors.put(audioPlaybackActor.getFullId(), audioPlaybackActor);
    }

    @Override
    public List<AudioPlaybackActor> getAudioActors() {
        List<AudioPlaybackActor> returnList = new ArrayList<AudioPlaybackActor>(audioActors.values());
        Collections.sort(returnList, Comparator.comparing(Identifyable::getId));
        return returnList;
    }

    @Override
    public List<AudioPlaybackActor> getAudioActorsByRoom(String roomId, boolean onlyDynamic) {
        KnownRoom room = datamodelService.getDatamodel().getKnownRoom(roomId);
        List<AudioPlaybackActor> returnList = room.getActors().values().stream()
                .filter(actorBase -> actorBase instanceof AudioPlaybackActor)
                .map(actorBase -> ((AudioPlaybackActor) actorBase))
                .filter(audioPlaybackActor -> (onlyDynamic && audioPlaybackActor.getAudioUrlId() != null || !onlyDynamic))
                .sorted(Comparator.comparing(Identifyable::getId))
                .collect(Collectors.toList());
        return returnList;
    }
}
