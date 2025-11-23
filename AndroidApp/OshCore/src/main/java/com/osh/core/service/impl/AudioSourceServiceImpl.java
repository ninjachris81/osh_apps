package com.osh.core.service.impl;

import com.osh.core.datamodel.meta.AudioPlaybackSource;
import com.osh.core.service.IAudioSourceService;
import com.osh.core.service.IDatamodelService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AudioSourceServiceImpl implements IAudioSourceService {


    private final IDatamodelService datamodelService;

    public AudioSourceServiceImpl(IDatamodelService datamodelService) {
        this.datamodelService = datamodelService;
    }

    @Override
    public List<AudioPlaybackSource> getAudioPlaybackSources() {
        List<AudioPlaybackSource> returnList = new ArrayList<>(datamodelService.getDatamodel().getAudioPlaybackSources().values());
        returnList.sort(Comparator.comparing(AudioPlaybackSource::getName));
        return returnList;
    }
}
