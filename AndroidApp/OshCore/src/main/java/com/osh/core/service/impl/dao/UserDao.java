package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;

import com.osh.core.user.User;

import java.util.List;

@Dao
public interface UserDao extends BaseDao<User> {
    @Query("SELECT * FROM " + User.TABLE_NAME)
    List<User> getAll();

    @Query("SELECT * FROM " + User.TABLE_NAME + " WHERE id = :id")
    User getById(String id);

    @Delete
    void delete(User user);
}