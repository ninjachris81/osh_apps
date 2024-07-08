package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.datamodel.meta.KnownRoom;
import com.osh.value.DBValue;

import java.util.List;

@Dao
public interface KnownRoomDao extends BaseDao<KnownRoom> {

    @Query("SELECT * FROM " + KnownRoom.TABLE_NAME)
    List<KnownRoom> getAll();
}
