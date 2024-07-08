package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.datamodel.meta.KnownRoomActors;

import java.util.List;

@Dao
public interface KnownRoomActorsDao extends BaseDao<KnownRoomActors> {

    @Query("SELECT * FROM " + KnownRoomActors.TABLE_NAME)
    List<KnownRoomActors> getAll();

}
