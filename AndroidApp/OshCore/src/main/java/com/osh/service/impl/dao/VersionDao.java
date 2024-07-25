package com.osh.service.impl.dao;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Query;

import com.osh.service.impl.DatabaseVersion;
import com.osh.value.ValueGroup;

@Dao
public interface VersionDao {

    @Query("SELECT version FROM " + DatabaseVersion.TABLE_NAME)
    long getVersion();

    @Insert
    void setVersion(DatabaseVersion version);

    @Query("DELETE FROM " + DatabaseVersion.TABLE_NAME)
    void delete();
}
