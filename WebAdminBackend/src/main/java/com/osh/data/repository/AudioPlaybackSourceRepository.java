package com.osh.data.repository;

import com.osh.data.entity.AudioPlaybackSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.w3c.dom.Entity;

public interface AudioPlaybackSourceRepository extends EntityRepository<AudioPlaybackSource> {

}
