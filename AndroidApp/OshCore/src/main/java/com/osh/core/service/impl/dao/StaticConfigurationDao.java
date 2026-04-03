package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.core.actor.DBShutterActor;
import com.osh.core.configuration.StaticConfigurationItem;

import java.util.List;

@Dao
public interface StaticConfigurationDao extends BaseDao<StaticConfigurationItem> {

    @Query("SELECT * FROM " + StaticConfigurationItem.TABLE_NAME + " WHERE id=:id")
    StaticConfigurationItem getById(String id);

    @Query("SELECT * FROM " + StaticConfigurationItem.TABLE_NAME)
    List<StaticConfigurationItem> getAll();

}
