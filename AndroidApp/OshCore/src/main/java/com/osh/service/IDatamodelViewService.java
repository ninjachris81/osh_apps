package com.osh.service;

import com.osh.datamodel.DatamodelView;
import com.osh.device.KnownDevice;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public interface IDatamodelViewService {

    Collection<KnownDevice> getMandatoryKnownDevices();

}
