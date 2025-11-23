package com.osh.core.service;

import com.osh.core.datamodel.meta.AudioPlaybackSource;

import java.util.List;

public interface IAudioSourceService {

    List<AudioPlaybackSource> getAudioPlaybackSources();
}
