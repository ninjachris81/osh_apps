package com.osh.datamodel.meta;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = KnownRoomActors.TABLE_NAME)
@Entity(tableName =  KnownRoomActors.TABLE_NAME, primaryKeys = {"room_id", "actor_id", "value_group_id"})
public class KnownRoomActors {

    @Ignore
    public static final String TABLE_NAME = "dm_known_rooms_actors";

    @DatabaseField(columnName = "room_id")
    @NotNull
    @ColumnInfo(name = "room_id")
    public String roomId;


    @DatabaseField(columnName = "actor_id")
    @NotNull
    @ColumnInfo(name = "actor_id")
    public String actorId;

    @DatabaseField(columnName = "value_group_id")
    @NotNull
    @ColumnInfo(name = "value_group_id")
    public String valueGroupId;

    public String getRoomId() {
        return roomId;
    }

    public String getActorId() {
        return actorId;
    }

    public String getValueGroupId() {
        return valueGroupId;
    }

    public KnownRoomActors() {
    }

    public KnownRoomActors(@NotNull String roomId, @NotNull String valueGroupId, @NotNull String actorId) {
        this.roomId = roomId;
        this.valueGroupId = valueGroupId;
        this.actorId = actorId;
    }
}
