package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.datamodel.meta.KnownRoomActors;
import com.osh.device.KnownDevice;

import java.util.List;

@Dao
public interface KnownDevicesDao extends BaseDao<KnownDevice> {

    @Query("SELECT * FROM " + KnownDevice.TABLE_NAME)
    List<KnownDevice> getAll();
}
