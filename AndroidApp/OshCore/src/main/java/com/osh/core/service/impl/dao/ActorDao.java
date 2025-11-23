package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.core.actor.DBActor;

import java.util.List;

@Dao
public interface ActorDao extends BaseDao<DBActor> {

    @Query("SELECT * FROM " + DBActor.TABLE_NAME)
    List<DBActor> getAll();

    @Query("SELECT COUNT(*) FROM " + DBActor.TABLE_NAME)
    int getCount();

}
