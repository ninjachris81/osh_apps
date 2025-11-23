package com.osh.core.service;

import com.osh.core.device.DeviceBase;
import com.osh.core.manager.IMqttSupport;
import com.osh.core.utils.ObservableInt;

import java.util.Collection;

public interface IDeviceDiscoveryService extends IMqttSupport {

    ObservableInt errorCount();

    String getDeviceId();

    Collection<DeviceBase> getDeviceList();

}
