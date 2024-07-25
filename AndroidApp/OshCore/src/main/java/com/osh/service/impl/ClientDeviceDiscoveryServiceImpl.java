package com.osh.service.impl;

import com.osh.communication.MessageBase;
import com.osh.communication.MessageBase.MESSAGE_TYPE;
import com.osh.device.DeviceBase;
import com.osh.device.DeviceDiscoveryMessage;
import com.osh.device.KnownDevice;
import com.osh.service.ICommunicationService;
import com.osh.service.IDatamodelService;
import com.osh.service.IDeviceDiscoveryService;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ClientDeviceDiscoveryServiceImpl implements IDeviceDiscoveryService {

	private final String clientId;

	private final Map<String, DeviceBase> allDevices = Collections.synchronizedMap(new HashMap<>());

	public ClientDeviceDiscoveryServiceImpl(ICommunicationService communicationService, IDatamodelService datamodelService, String clientId) {
		this.clientId = clientId;
		communicationService.registerMessageType(MESSAGE_TYPE.MESSAGE_TYPE_DEVICE_DISCOVERY, this);

		Map<String, KnownDevice> knownDevices = datamodelService.getDatamodel().getKnownDevices();
		for (KnownDevice knownDevice : knownDevices.values()) {
			allDevices.put(knownDevice.getFullId(), new KnownDevice(knownDevice.getId(), knownDevice.getServiceId(), knownDevice.getName()));
		}
	}

	@Override
	public MESSAGE_TYPE getMessageType() {
		return MESSAGE_TYPE.MESSAGE_TYPE_DEVICE_DISCOVERY;
	}

	@Override
	public void handleReceivedMessage(MessageBase msg) {
		DeviceDiscoveryMessage ddMsg = (DeviceDiscoveryMessage) msg;

		final String id = KnownDevice.getFullId(ddMsg.getDeviceId(), ddMsg.getServiceId());
		if (allDevices.containsKey(id)) {
			DeviceBase dev = allDevices.get(id);
			dev.updatePing();
			dev.setUpTime(ddMsg.getUpTime());
			dev.setHealthState(ddMsg.getHealthState());
			allDevices.put(id, dev);
		} else {
			DeviceBase unknownDevice = new DeviceBase(ddMsg.getDeviceId(), ddMsg.getServiceId());
			allDevices.put(unknownDevice.getFullId(), unknownDevice);
		}
	}

	@Override
	public String getDeviceId() {
		return clientId;
	}

	@Override
	public Collection<DeviceBase> getDeviceList() {
		return allDevices.values();
	}

	// TODO: implement bc


}
