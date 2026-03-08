package com.osh.core.datamodel.meta;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = KnownAreaUsers.TABLE_NAME)
@Entity(tableName =  KnownAreaUsers.TABLE_NAME, primaryKeys = {"known_area_id", "user_id"})
public class KnownAreaUsers {

    @Ignore
    public static final String TABLE_NAME = "dm_known_areas_users";

    @DatabaseField(columnName = "known_area_id")
    @NotNull
    @ColumnInfo(name = "known_area_id")
    public String knownAreaId;

    @DatabaseField(columnName = "user_id")
    @NotNull
    @ColumnInfo(name = "user_id")
    public String userId;

    public @NotNull String getKnownAreaId() {
        return knownAreaId;
    }

    public @NotNull String getUserId() {
        return userId;
    }

    public KnownAreaUsers() {
    }

    @Ignore
    public KnownAreaUsers(@NotNull String knownAreaId, @NotNull String userId) {
        this.knownAreaId = knownAreaId;
        this.userId = userId;
    }
}
