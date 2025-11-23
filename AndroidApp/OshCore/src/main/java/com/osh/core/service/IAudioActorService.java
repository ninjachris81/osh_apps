package com.osh.core.service;

import com.osh.core.actor.AudioPlaybackActor;
import com.osh.core.datamodel.meta.AudioPlaybackSource;
import com.osh.core.utils.ObservableInt;

import java.util.Collection;
import java.util.List;

public interface IAudioActorService {
    List<AudioPlaybackSource> getAudioPlaybackSources();

    Collection<AudioPlaybackActor> getAudioActors();

    List<AudioPlaybackActor> getAudioActorsByRoom(String roomId, boolean onlyDynamic);

    void startPlayback(AudioPlaybackActor actor, AudioPlaybackSource source);

    void stopPlayback(AudioPlaybackActor actor);

    ObservableInt getPlayingMusicCount();

    int getCount(List<AudioPlaybackActor.PLAYBACK_STATE> stateFilter, boolean onlyDynamic);

    AudioPlaybackSource getCurrentSource(AudioPlaybackActor audioPlaybackActor);

    void updateAudioVolume(AudioPlaybackActor actor, double value);
}
