package com.osh.core.service.impl;


import com.osh.core.actor.ActorBase;
import com.osh.core.datamodel.DatamodelBase;
import com.osh.core.service.IActorService;
import com.osh.core.service.IDatabaseService;
import com.osh.core.service.IDatamodelService;
import com.osh.core.service.IValueService;
import com.osh.core.utils.IObservableBoolean;
import com.osh.core.utils.ObservableBoolean;
import com.osh.core.value.ValueBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class DatamodelServiceImpl implements IDatamodelService {

	private static final Logger log = LoggerFactory.getLogger(DatamodelServiceImpl.class);

	private final ObservableBoolean loadedState;

	private DatamodelBase datamodel;

	private final IDatabaseService databaseService;
	private final IValueService valueService;
	private final IActorService actorService;

	public DatamodelServiceImpl(IDatabaseService databaseService, IValueService valueService, IActorService actorService) throws SQLException {
		this.databaseService = databaseService;
		this.valueService = valueService;
		this.actorService = actorService;

		loadedState = new ObservableBoolean(false);

		datamodel = databaseService.loadDatamodel();

		registerData();

		loadedState.changeValue(true);

	}

	private void registerData() {

		for (ValueBase value : datamodel.getValues().values()) {
			valueService.registerValue(value);
		}

		for (ActorBase actor : datamodel.getActors().values()) {
			actorService.registerActor(actor);
		}
	}


	@Override
	public DatamodelBase getDatamodel() {
		return datamodel;
	}

	@Override
	public void save() {

	}

	@Override
	public IObservableBoolean loadedState() {
		return loadedState;
	}
}
