package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.osh.value.ValueGroup;

@Dao
public interface BaseDao<CLASS_TYPE> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CLASS_TYPE valueGroup);

}
