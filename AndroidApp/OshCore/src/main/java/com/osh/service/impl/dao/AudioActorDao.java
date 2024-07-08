package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.actor.DBActor;
import com.osh.actor.DBAudioActor;

import java.util.List;

@Dao
public interface AudioActorDao extends BaseDao<DBAudioActor> {

    @Query("SELECT * FROM " + DBAudioActor.TABLE_NAME)
    List<DBAudioActor> getAll();

    @Query("SELECT COUNT(*) FROM " + DBAudioActor.TABLE_NAME)
    int getCount();

    @Query("SELECT * FROM " + DBAudioActor.TABLE_NAME + " WHERE id=:id AND value_group_id=:valueGroupId")
    List<DBAudioActor> getById(String id, String valueGroupId);
}
