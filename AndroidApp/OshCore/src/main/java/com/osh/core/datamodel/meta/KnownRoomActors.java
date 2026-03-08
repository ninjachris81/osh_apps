package com.osh.core.datamodel.meta;

import androidx.room.ColumnInfo;
import androidx.room.Database;
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

    @DatabaseField(columnName = "order_index")
    @ColumnInfo(name = "order_index")
    public int orderIndex;

    public String getRoomId() {
        return roomId;
    }

    public String getActorId() {
        return actorId;
    }

    public String getValueGroupId() {
        return valueGroupId;
    }

    public int getOrderIndex() { return orderIndex; }

    public KnownRoomActors() {
    }

    @Ignore
    public KnownRoomActors(@NotNull String roomId, @NotNull String valueGroupId, @NotNull String actorId, @NotNull int orderIndex) {
        this.roomId = roomId;
        this.valueGroupId = valueGroupId;
        this.actorId = actorId;
        this.orderIndex = orderIndex;
    }
}
