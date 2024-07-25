package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.hivemq.client.internal.mqtt.datatypes.MqttTopicFilterImplBuilder;
import com.osh.user.User;

import java.util.List;

@Dao
public interface UserDao extends BaseDao<User> {
    @Query("SELECT * FROM " + User.TABLE_NAME)
    List<User> getAll();

    @Delete
    void delete(User user);
}