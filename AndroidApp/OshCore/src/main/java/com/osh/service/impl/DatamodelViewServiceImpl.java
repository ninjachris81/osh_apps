package com.osh.service.impl;

import android.os.Build;

import com.osh.datamodel.DatamodelBase;
import com.osh.datamodel.DatamodelView;
import com.osh.device.KnownDevice;
import com.osh.repository.IDatamodelViewRepository;
import com.osh.service.IDatamodelService;
import com.osh.service.IDatamodelViewService;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
