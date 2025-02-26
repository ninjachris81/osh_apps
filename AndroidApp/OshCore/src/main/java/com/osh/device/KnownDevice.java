package com.osh.device;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@Entity(tableName = KnownDevice.TABLE_NAME)
@DatabaseTable(tableName = KnownDevice.TABLE_NAME)
public class KnownDevice extends DeviceBase {

	@Ignore
    public static final String TABLE_NAME = "dm_known_devices";

	@DatabaseField(canBeNull = false)
	@NotNull
	@ColumnInfo(name = "name")
	public String name;

	@DatabaseField(canBeNull = false)
	@ColumnInfo(name = "mandatory")
	@NotNull
	public boolean mandatory;


	@Ignore
	public KnownDevice(String id, String serviceId, String name, boolean mandatory) {
		super(id, serviceId);
		this.name = name;
		this.mandatory = mandatory;
	}

	public KnownDevice() {
		super();
	}

	public String getName() {
		return name;
	}

	public boolean isMandatory() {
		return mandatory;
	}
}
