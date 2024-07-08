package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.hivemq.client.internal.mqtt.datatypes.MqttTopicFilterImplBuilder;
import com.osh.value.ValueGroup;

import java.util.List;

@Dao
public interface ValueGroupDao extends BaseDao<ValueGroup> {

    @Query("SELECT * FROM " + ValueGroup.TABLE_NAME)
    List<ValueGroup> getAll();

    @Query("SELECT COUNT(*) FROM " + ValueGroup.TABLE_NAME)
    int getCount();
}
