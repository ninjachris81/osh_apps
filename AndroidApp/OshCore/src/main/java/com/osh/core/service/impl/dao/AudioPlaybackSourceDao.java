package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.core.datamodel.meta.AudioPlaybackSource;

import java.util.List;

@Dao
public interface AudioPlaybackSourceDao extends BaseDao<AudioPlaybackSource> {

    @Query("SELECT * FROM " + AudioPlaybackSource.TABLE_NAME)
    List<AudioPlaybackSource> getAll();

    @Query("SELECT COUNT(*) FROM " + AudioPlaybackSource.TABLE_NAME)
    int getCount();
}
