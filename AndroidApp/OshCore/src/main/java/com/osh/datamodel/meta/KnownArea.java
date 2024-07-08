package com.osh.datamodel.meta;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.osh.actor.ActorBase;
import com.osh.datamodel.ItemMetaInfo;
import com.osh.value.ValueBase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

@DatabaseTable(tableName = KnownArea.TABLE_NAME)
@Entity(tableName = KnownArea.TABLE_NAME)
public class KnownArea {

    @Ignore
    public static final String TABLE_NAME = "dm_known_areas";

    @DatabaseField(id = true)
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    public String id;

    @DatabaseField(canBeNull = false)
    @NotNull
    @ColumnInfo(name = "name")
    public String name;

    @DatabaseField(columnName = "display_order")
    @ColumnInfo(name = "display_order")
    public int displayOrder;

    @Ignore
    private Map<String, ActorBase> actors = new HashMap<>();

    @Ignore
    private Map<String, ValueBase> values = new HashMap<>();

    @Ignore
    private ItemMetaInfo meta = new ItemMetaInfo();

    public KnownArea() {
    }

    public KnownArea(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
}
