package com.osh.core.service.impl;

import android.os.Build;

import com.osh.core.datamodel.DatamodelBase;
import com.osh.core.device.KnownDevice;
import com.osh.core.service.IDatamodelViewService;

import java.util.Collection;

public class DatamodelViewServiceImpl implements IDatamodelViewService {

	private final DatamodelBase datamodel;

	public DatamodelViewServiceImpl(DatamodelBase datamodel) {
		this.datamodel = datamodel;
	}

	@Override
	public Collection<KnownDevice> getMandatoryKnownDevices() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
			return datamodel.getKnownDevices().values().stream().filter(knownDevice -> knownDevice.isMandatory()).toList();
		} else {
			return datamodel.getKnownDevices().values();
		}
	}
}
