package com.osh.core.configuration;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.osh.core.actor.DBShutterActor;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = StaticConfigurationItem.TABLE_NAME)
@Entity(tableName = StaticConfigurationItem.TABLE_NAME)
public class StaticConfigurationItem {

    @Ignore
    public static final String TABLE_NAME = "dm_static_configuration";

    @DatabaseField(id = true)
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    public String id;

    @DatabaseField(canBeNull = false)
    @NotNull
    @ColumnInfo(name = "value")
    public String value;

    public StaticConfigurationItem() {
    }

    @Ignore
    public StaticConfigurationItem(String id, String value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

}
