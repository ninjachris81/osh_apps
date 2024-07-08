package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.actor.DBAudioActor;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.value.DBValue;

import java.util.List;

@Dao
public interface AudioPlaybackSourceDao extends BaseDao<AudioPlaybackSource> {

    @Query("SELECT * FROM " + AudioPlaybackSource.TABLE_NAME)
    List<AudioPlaybackSource> getAll();

    @Query("SELECT COUNT(*) FROM " + AudioPlaybackSource.TABLE_NAME)
    int getCount();
}
