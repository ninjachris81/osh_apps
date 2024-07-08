package com.osh.actor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = DBActor.TABLE_NAME)
@Entity(tableName = DBActor.TABLE_NAME, primaryKeys = {"id", "value_group_id"})
public class DBActor {

    @Ignore
    public static final String TABLE_NAME = "dm_actors";

    @DatabaseField(uniqueCombo = true)
    @ColumnInfo(name = "id")
    @NotNull
    public String id;

    @DatabaseField(canBeNull = false, columnName = "class_type")
    @ColumnInfo(name = "class_type")
    @NotNull
    public String classType;

    @DatabaseField(uniqueCombo = true, columnName = "value_group_id")
    @ColumnInfo(name = "value_group_id")
    @NotNull
    public String valueGroupId;

    @DatabaseField(columnName = "value_type")
    @ColumnInfo(name = "value_type")
    public int valueType;

    @DatabaseField(columnName = "value_timeout")
    @ColumnInfo(name = "value_timeout")
    public int valueTimeout;

    @DatabaseField(columnName = "is_async")
    @ColumnInfo(name = "enum_count")
    public boolean isAsync;


    public DBActor() {
    }

    public DBActor(@NotNull String id, @NotNull String classType, @NotNull String valueGroupId, int valueType, int valueTimeout, boolean isAsync) {
        this.id = id;
        this.classType = classType;
        this.valueGroupId = valueGroupId;
        this.valueType = valueType;
        this.valueTimeout = valueTimeout;
        this.isAsync = isAsync;
    }

    public String getId() {
        return id;
    }

    public String getClassType() {
        return classType;
    }

    public String getValueGroupId() {
        return valueGroupId;
    }

    public int getValueType() {
        return valueType;
    }

    public int getValueTimeout() {
        return valueTimeout;
    }

    public boolean isAsync() {
        return isAsync;
    }

}
