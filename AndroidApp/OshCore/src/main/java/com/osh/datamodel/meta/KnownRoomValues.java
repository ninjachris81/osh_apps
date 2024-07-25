package com.osh.datamodel.meta;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = KnownRoomValues.TABLE_NAME)
@Entity(tableName =  KnownRoomValues.TABLE_NAME, primaryKeys = {"room_id", "value_id", "value_group_id"})
public class KnownRoomValues {

    @Ignore
    public static final String TABLE_NAME = "dm_known_rooms_values";

    @DatabaseField(columnName = "room_id")
    @NotNull
    @ColumnInfo(name = "room_id")
    public String roomId;

    @DatabaseField(columnName = "value_id")
    @NotNull
    @ColumnInfo(name = "value_id")
    public String valueId;

    @DatabaseField(columnName = "value_group_id")
    @NotNull
    @ColumnInfo(name = "value_group_id")
    public String valueGroupId;

    public String getRoomId() {
        return roomId;
    }

    public String getValueId() {
        return valueId;
    }

    public String getValueGroupId() {
        return valueGroupId;
    }

    public KnownRoomValues() {
    }

    @Ignore
    public KnownRoomValues(@NotNull String roomId, @NotNull String valueGroupId, @NotNull String valueId) {
        this.roomId = roomId;
        this.valueGroupId = valueGroupId;
        this.valueId = valueId;
    }
}
