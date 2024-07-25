package com.osh.service;

import com.osh.device.DeviceBase;
import com.osh.manager.IMqttSupport;

import java.util.Collection;

public interface IDeviceDiscoveryService extends IMqttSupport {

    String getDeviceId();

    Collection<DeviceBase> getDeviceList();

}
