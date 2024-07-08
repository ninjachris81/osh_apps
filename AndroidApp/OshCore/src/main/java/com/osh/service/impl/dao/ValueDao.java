package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.value.DBValue;

import java.util.List;

@Dao
public interface ValueDao extends BaseDao<DBValue> {

    @Query("SELECT * FROM " + DBValue.TABLE_NAME)
    List<DBValue> getAll();

    @Query("SELECT COUNT(*) FROM " + DBValue.TABLE_NAME)
    int getCount();
}