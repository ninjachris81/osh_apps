package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.core.datamodel.meta.KnownArea;

import java.util.List;

@Dao
public interface KnownAreaDao extends BaseDao<KnownArea> {

    @Query("SELECT * FROM " + KnownArea.TABLE_NAME)
    List<KnownArea> getAll();

}
