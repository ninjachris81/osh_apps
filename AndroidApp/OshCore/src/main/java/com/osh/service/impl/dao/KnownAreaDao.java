package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;

import java.util.List;

@Dao
public interface KnownAreaDao extends BaseDao<KnownArea> {

    @Query("SELECT * FROM " + KnownArea.TABLE_NAME)
    List<KnownArea> getAll();

}
