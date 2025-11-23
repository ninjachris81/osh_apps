package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.core.datamodel.meta.KnownRoom;

import java.util.List;

@Dao
public interface KnownRoomDao extends BaseDao<KnownRoom> {

    @Query("SELECT * FROM " + KnownRoom.TABLE_NAME)
    List<KnownRoom> getAll();
}
