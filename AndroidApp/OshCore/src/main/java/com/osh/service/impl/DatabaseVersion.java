package com.osh.service.impl;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.osh.value.DBValue;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = DatabaseVersion.TABLE_NAME, primaryKeys = {"version"})
public class DatabaseVersion {

    @Ignore
    public static final String TABLE_NAME = "dm_version";

    @DatabaseField(uniqueCombo = true, columnName = "id")
    @ColumnInfo(name = "version")
    @NotNull
    public long version;

    public DatabaseVersion() {
    }

    @Ignore
    public DatabaseVersion(long version) {
        this.version = version;
    }
}
