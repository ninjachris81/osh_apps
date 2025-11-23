package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.core.datamodel.meta.KnownRoomActors;

import java.util.List;

@Dao
public interface KnownRoomActorsDao extends BaseDao<KnownRoomActors> {

    @Query("SELECT * FROM " + KnownRoomActors.TABLE_NAME)
    List<KnownRoomActors> getAll();

}
