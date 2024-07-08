package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.datamodel.meta.KnownRoom;
import com.osh.datamodel.meta.KnownRoomValues;

import java.util.List;

@Dao
public interface KnownRoomValuesDao extends BaseDao<KnownRoomValues> {

    @Query("SELECT * FROM " + KnownRoomValues.TABLE_NAME)
    List<KnownRoomValues> getAll();

}
