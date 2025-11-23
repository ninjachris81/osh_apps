package com.osh.core.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.osh.core.service.impl.DatabaseVersion;

@Dao
public interface VersionDao {

    @Query("SELECT version FROM " + DatabaseVersion.TABLE_NAME)
    long getVersion();

    @Insert
    void setVersion(DatabaseVersion version);

    @Query("DELETE FROM " + DatabaseVersion.TABLE_NAME)
    void delete();
}
