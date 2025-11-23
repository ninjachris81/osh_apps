package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.osh.core.device.KnownDevice;

import java.util.List;

@Dao
public interface KnownDevicesDao extends BaseDao<KnownDevice> {

    @Query("SELECT * FROM " + KnownDevice.TABLE_NAME)
    List<KnownDevice> getAll();
}
