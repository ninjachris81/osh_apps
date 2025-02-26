package com.osh.service.impl;

import com.osh.Identifyable;
import com.osh.actor.ActorBase;
import com.osh.actor.ActorCmds;
import com.osh.actor.AudioPlaybackActor;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.service.IActorService;
import com.osh.service.IAudioActorService;
import com.osh.service.IDatamodelService;
import com.osh.service.IValueService;
import com.osh.utils.ObservableInt;
import com.osh.value.DoubleValue;
import com.osh.value.StringValue;
import com.osh.value.ValueBase;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AudioActorServiceImpl implements IAudioActorService {

    private static final Logger log = LoggerFactory.getLogger(AudioActorServiceImpl.class);
    private final IValueService valueService;
    private final IActorService actorService;
    private final IDatamodelService datamodelService;

    protected Map<String, AudioPlaybackActor> audioActors = new HashMap<>();

    protected ObservableInt playingMusicActors = new ObservableInt(0);

    public AudioActorServiceImpl(IValueService valueService, IActorService actorService, IDatamodelService datamodelService) {
        this.valueService = valueService;
        this.actorService = actorService;
        this.datamodelService = datamodelService;

        datamodelService.loadedState().addItemChangeListener(item -> {
            if (item) {
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
        audioPlaybackActor.addItemChangeListener(item -> {
            if (item.isMusicActor()) {
                playingMusicActors.changeValue(getCount(List.of(AudioPlaybackActor.PLAYBACK_STATE.PLAYING), true));
            }
        }, true);

        if (!StringUtils.isEmpty(audioPlaybackActor.getAudioVolumeId())) {
            ValueBase value = datamodelService.getDatamodel().getValue(audioPlaybackActor.getAudioVolumeId());
            if (value instanceof DoubleValue) {
                audioPlaybackActor.setVolumeValue((DoubleValue) value);
            } else {
                throw new RuntimeException("Unexpected value type: " + value);
            }
        }

        if (!StringUtils.isEmpty(audioPlaybackActor.getAudioUrlId())) {
            ValueBase value = datamodelService.getDatamodel().getValue(audioPlaybackActor.getAudioUrlId());
            if (value instanceof StringValue) {
                audioPlaybackActor.setUrlValue((StringValue) value);
            } else {
                throw new RuntimeException("Unexpected value type: " + value);
            }
        }

        if (!StringUtils.isEmpty(audioPlaybackActor.getAudioCurrentTitleId())) {
            ValueBase value = datamodelService.getDatamodel().getValue(audioPlaybackActor.getAudioCurrentTitleId());
            if (value instanceof StringValue) {
                audioPlaybackActor.setAudioCurrentTitleValue((StringValue) value);
            } else {
                throw new RuntimeException("Unexpected value type: " + value);
            }
        }

        audioActors.put(audioPlaybackActor.getFullId(), audioPlaybackActor);
    }

    @Override
    public List<AudioPlaybackSource> getAudioPlaybackSources() {
        List<AudioPlaybackSource> returnList = new ArrayList<>(datamodelService.getDatamodel().getAudioPlaybackSources().values());
        returnList.sort(Comparator.comparing(AudioPlaybackSource::getName));
        return returnList;
    }

    @Override
    public Collection<AudioPlaybackActor> getAudioActors() {
        List<AudioPlaybackActor> returnList = new ArrayList<>(audioActors.values());
        returnList.sort(Comparator.comparing(AudioPlaybackActor::getId));
        return returnList;
    }

    @Override
    public List<AudioPlaybackActor> getAudioActorsByRoom(String roomId, boolean onlyDynamic) {
        KnownRoom room = datamodelService.getDatamodel().getKnownRoom(roomId);
        List<AudioPlaybackActor> returnList = room.getActors().values().stream()
                .filter(actorBase -> actorBase instanceof AudioPlaybackActor)
                .map(actorBase -> ((AudioPlaybackActor) actorBase))
                .filter(audioPlaybackActor -> (!onlyDynamic || audioPlaybackActor.isDynamic()))
                .sorted(Comparator.comparing(Identifyable::getId))
                .collect(Collectors.toList());
        return returnList;
    }

    @Override
    public void startPlayback(AudioPlaybackActor actor, AudioPlaybackSource source) {
        stopPlayback(actor);
        actor.getAudioUrlValue().updateValue(source.getSourceUrl(), false);
        valueService.publishValue(actor.getAudioUrlValue());
        actorService.publishCmd(actor, ActorCmds.ACTOR_CMD_START);
    }

    @Override
    public void stopPlayback(AudioPlaybackActor actor) {
        actorService.publishCmd(actor, ActorCmds.ACTOR_CMD_STOP);
    }

    @Override
    public ObservableInt getPlayingMusicCount() {
        return playingMusicActors;
    }

    @Override
    public int getCount(List<AudioPlaybackActor.PLAYBACK_STATE> stateFilter, boolean onlyMusic) {
        return (int) audioActors.values().stream().filter(audioPlaybackActor -> (!onlyMusic || audioPlaybackActor.isMusicActor()) && stateFilter.contains(audioPlaybackActor.getPlaybackState())).count();
    }

    @Override
    public AudioPlaybackSource getCurrentSource(AudioPlaybackActor audioPlaybackActor) {
        if (!StringUtils.isEmpty(audioPlaybackActor.getAudioUrlId())) {
            return datamodelService.getDatamodel().getAudioPlaybackSources().values().stream().filter(audioPlaybackSource -> audioPlaybackSource.getSourceUrl().equals(audioPlaybackActor.getAudioUrlValue().getValue())).findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public void updateAudioVolume(AudioPlaybackActor actor, double value) {
        actor.getAudioVolumeValue().updateValue(value);
        valueService.publishValue(actor.getAudioVolumeValue());
    }
}
