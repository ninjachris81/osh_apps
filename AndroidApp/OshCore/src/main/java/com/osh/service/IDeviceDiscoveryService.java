package com.osh.service;

import com.osh.device.DeviceBase;
import com.osh.manager.IMqttSupport;
import com.osh.utils.ObservableInt;

import java.util.Collection;

public interface IDeviceDiscoveryService extends IMqttSupport {

    ObservableInt errorCount();

    String getDeviceId();

    Collection<DeviceBase> getDeviceList();

}
