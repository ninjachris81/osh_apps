package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;
import androidx.room.Transaction;

import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.datamodel.meta.KnownAreaUsers;
import com.osh.core.datamodel.meta.KnownRoomValues;
import com.osh.core.user.User;

import java.util.List;

@Dao
public interface KnownAreaUsersDao extends BaseDao<KnownArea> {

    @Query("SELECT * FROM " + KnownAreaUsers.TABLE_NAME + " WHERE user_id = :userId")
    List<KnownArea> getAreasByUser(String userId);


}
