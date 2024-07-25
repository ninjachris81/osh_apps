package com.osh.device;

import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.j256.ormlite.field.DatabaseField;
import com.osh.SerializableIdentifyable;

import org.jetbrains.annotations.NotNull;

public class DeviceBase {
	
	public static final String DEVICE_FULLID_SEP = ".";

	@DatabaseField(id = true)
	@PrimaryKey
	@NotNull
	@ColumnInfo(name = "id")
	public String id;

	@DatabaseField(columnName = "service_id", canBeNull = false)
	@NotNull
	@ColumnInfo(name = "service_id")
	public String serviceId;

	@Ignore
	DeviceDiscoveryMessage.DeviceHealthState healthState = DeviceDiscoveryMessage.DeviceHealthState.Unknown;

	@Ignore
	protected boolean isOnline = false;

	@Ignore
	protected long lastPing = 0;

	@Ignore
	protected long upTime = 0;
	
	public DeviceBase(String id, String serviceId) {
		this.id = id;
		this.serviceId = serviceId;
	}

	public DeviceBase() {
		super();
	}

	public String getFullId() {
	    return getFullId(id, serviceId);
	}

	public static String getFullId(String id, String serviceId) {
		return id + DEVICE_FULLID_SEP + serviceId;
	}

	public @NotNull String getServiceId() {
		return serviceId;
	}

	public @NotNull String getId() {
		return id;
	}

	public boolean isOnline() {
		return isOnline;
	}

	public long getLastPing() {
		return lastPing;
	}

	public long getUpTime() {
		return upTime;
	}

	public void setUpTime(long upTime) {
		this.upTime = upTime;
	}

	public void updatePing() {
		this.lastPing = System.currentTimeMillis();
		this.isOnline = true;
	}

	public DeviceDiscoveryMessage.DeviceHealthState getHealthState() {
		return healthState;
	}

	public void setHealthState(DeviceDiscoveryMessage.DeviceHealthState healthState) {
		this.healthState = healthState;
	}
}
