package com.osh.user;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.osh.SerializableIdentifyable;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

@Entity(tableName = User.TABLE_NAME)
@DatabaseTable(tableName = User.TABLE_NAME)
public class User {

    @Ignore
    public static final String TABLE_NAME = "dm_users";

    @DatabaseField(columnName = "id")
    @ColumnInfo(name = "id")
    @NotNull
    @PrimaryKey
    public String id;

    @DatabaseField(columnName = "name", canBeNull = false)
    @NotNull
    @ColumnInfo(name = "name")
    private String name;

    @DatabaseField(columnName = "rights", canBeNull = false)
    @ColumnInfo(name = "rights")
    private String rights;

    public User() {
    }

    public User(@NotNull String id, @NotNull String name, String rights) {
        this.id = id;
        this.name = name;
        this.rights = rights;
    }

    public @NotNull String getId() {
        return id;
    }

    public void setId(@NotNull String id) {
        this.id = id;
    }

    public @NotNull String getName() {
        return name;
    }

    public String getRights() {
        return rights;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    public void setRights(String rights) {
        this.rights = rights;
    }

    public boolean hasRight(String right) {
        if (right != null) {
            return Arrays.asList(right.split(" ")).contains(right);
        } else {
            return false;
        }
    }
}
