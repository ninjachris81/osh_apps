package com.osh.actor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = DBShutterActor.TABLE_NAME)
@Entity(tableName = DBShutterActor.TABLE_NAME, primaryKeys = {"id", "value_group_id"})
public class DBShutterActor {

    @Ignore
    public static final String TABLE_NAME = "dm_actors_shutter";

    @DatabaseField(uniqueCombo = true)
    @ColumnInfo(name = "id")
    @NotNull
    public String id;

    @DatabaseField(uniqueCombo = true, columnName = "value_group_id")
    @ColumnInfo(name = "value_group_id")
    @NotNull
    public String valueGroupId;

    @DatabaseField(columnName = "shutter_tilt_support")
    @ColumnInfo(name = "shutter_tilt_support")
    public boolean shutterTiltSupport;

    @DatabaseField(columnName = "shutter_full_close_duration")
    @ColumnInfo(name = "shutter_full_close_duration")
    public long shutterFullCloseDuration;

    @DatabaseField(columnName = "shutter_full_tilt_duration")
    @ColumnInfo(name = "shutter_full_tilt_duration")
    public long shutterFullTiltDuration;

    public DBShutterActor() {
    }

    public DBShutterActor(@NotNull String id, @NotNull String valueGroupId, boolean shutterTiltSupport, long shutterFullCloseDuration, long shutterFullTiltDuration) {
        this.id = id;
        this.valueGroupId = valueGroupId;
        this.shutterTiltSupport = shutterTiltSupport;
        this.shutterFullCloseDuration = shutterFullCloseDuration;
        this.shutterFullTiltDuration = shutterFullTiltDuration;
    }

    public String getId() {
        return id;
    }

    public String getValueGroupId() {
        return valueGroupId;
    }

    public boolean isShutterTiltSupport() {
        return shutterTiltSupport;
    }

    public long getShutterFullCloseDuration() {
        return shutterFullCloseDuration;
    }

    public long getShutterFullTiltDuration() {
        return shutterFullTiltDuration;
    }
}
