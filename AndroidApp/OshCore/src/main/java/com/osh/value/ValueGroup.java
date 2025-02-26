package com.osh.value;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = ValueGroup.TABLE_NAME)
@Entity(tableName = ValueGroup.TABLE_NAME)
public class ValueGroup {

	@Ignore
	public static final String TABLE_NAME = "dm_value_groups";

	@DatabaseField(id = true)
	@PrimaryKey
	@NotNull
	public String id;

	public ValueGroup() {
	}

	@Ignore
	public ValueGroup(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
