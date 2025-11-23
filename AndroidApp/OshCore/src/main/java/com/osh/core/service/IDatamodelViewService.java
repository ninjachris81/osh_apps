package com.osh.core.service;

import com.osh.core.device.KnownDevice;

import java.util.Collection;

public interface IDatamodelViewService {

    Collection<KnownDevice> getMandatoryKnownDevices();

}
