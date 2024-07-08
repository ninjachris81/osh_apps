package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.actor.DBShutterActor;

import java.util.List;

@Dao
public interface ShutterActorDao extends BaseDao<DBShutterActor> {

    @Query("SELECT * FROM " + DBShutterActor.TABLE_NAME)
    List<DBShutterActor> getAll();

    @Query("SELECT COUNT(*) FROM " + DBShutterActor.TABLE_NAME)
    int getCount();

    @Query("SELECT * FROM " + DBShutterActor.TABLE_NAME + " WHERE id=:id AND value_group_id = :valueGroupId")
    List<DBShutterActor> getById(String id, String valueGroupId);
}
