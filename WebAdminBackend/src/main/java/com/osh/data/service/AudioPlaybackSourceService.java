package com.osh.data.service;

import com.osh.data.entity.AudioPlaybackSource;
import java.util.Optional;

import com.osh.data.repository.AudioPlaybackSourceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class AudioPlaybackSourceService extends AbstractEntityService<AudioPlaybackSource, AudioPlaybackSourceRepository> {

    public AudioPlaybackSourceService(AudioPlaybackSourceRepository repository) {
        super(repository);
    }



}
