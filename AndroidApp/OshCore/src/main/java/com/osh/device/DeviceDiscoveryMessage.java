package com.osh.device;

import android.bluetooth.BluetoothClass;

import com.osh.communication.MessageBase;
import com.osh.communication.mqtt.MqttConstants;

import java.util.Map;

public class DeviceDiscoveryMessage extends MessageBase {
	
    private String deviceId;
    private String serviceId;
	private DeviceHealthState healthState = DeviceHealthState.Unknown;

	private long upTime;

	public Map<String, Object> getValues() {
		return Map.of(MqttConstants.MQTT_SINGLE_VALUE_ATTR, upTime, MqttConstants.MQTT_HEALTH_STATE, healthState);
	}

	public enum DeviceHealthState {
		Unknown,
		Healthy,
		HasWarnings,
		HasErrors;
	};

	public DeviceDiscoveryMessage(String deviceId, String serviceId, long upTime, DeviceHealthState healthState) {
		this.deviceId = deviceId;
		this.serviceId = serviceId;
		this.upTime = upTime;
		this.healthState = healthState;
	}

	@Override
	public MESSAGE_TYPE getMessageType() {
		return MESSAGE_TYPE.MESSAGE_TYPE_DEVICE_DISCOVERY;
	}

	@Override
	public String getFirstLevelId() {
		return deviceId;
	}

	@Override
	public String getSecondLevelId() {
		return serviceId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public long getUpTime() {
		return upTime;
	}

	public DeviceHealthState getHealthState() {
		return healthState;
	}
}
