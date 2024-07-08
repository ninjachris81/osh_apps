package com.osh.value;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = DBValue.TABLE_NAME)
@Entity(tableName = DBValue.TABLE_NAME, primaryKeys = {"id", "value_group_id"})
public class DBValue {

    @Ignore
    public static final String TABLE_NAME = "dm_values";

    @DatabaseField(uniqueCombo = true, columnName = "id")
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

    @DatabaseField(columnName = "enum_count")
    @ColumnInfo(name = "enum_count")
    public int enumCount;

    public DBValue() {

    }

    public DBValue(String valueGroupId, String id, String classType, ValueType valueType, int valueTimeout, int enumCount) {
        this.valueGroupId = valueGroupId;
        this.id = id;
        this.classType = classType;
        this.valueType = valueType.getValue();
        this.valueTimeout = valueTimeout;
        this.enumCount = enumCount;
    }

    public DBValue(ValueBase value) {
        this(value.getValueGroup().getId(),
                value.getId(),
                value.getClass().getName(),
                value.getValueType(),
                value.getValueTimeout(),
                value.getEnumCount());
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

    public int getEnumCount() {
        return enumCount;
    }
}
