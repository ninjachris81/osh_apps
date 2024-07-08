package com.osh.service.impl;


import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.osh.actor.ActorBase;
import com.osh.actor.AudioPlaybackActor;
import com.osh.actor.DBActor;
import com.osh.actor.DBAudioActor;
import com.osh.datamodel.DatamodelBase;
import com.osh.datamodel.DynamicDatamodel;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.datamodel.meta.KnownRoomActors;
import com.osh.datamodel.meta.KnownRoomValues;
import com.osh.service.IActorService;
import com.osh.service.ICommunicationService;
import com.osh.service.IDatabaseService;
import com.osh.service.IDatamodelService;
import com.osh.service.IValueService;
import com.osh.utils.IObservableBoolean;
import com.osh.utils.ObservableBoolean;
import com.osh.actor.DBShutterActor;
import com.osh.value.DBValue;
import com.osh.value.StringValue;
import com.osh.value.ValueBase;
import com.osh.value.ValueGroup;
import com.osh.value.ValueType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DatamodelServiceImpl implements IDatamodelService {

	private static final Logger log = LoggerFactory.getLogger(DatamodelServiceImpl.class);

	private final ObservableBoolean loadedState;

	private DatamodelBase datamodel;

	private final IDatabaseService databaseService;
	private final IValueService valueService;
	private final IActorService actorService;

	private final ICommunicationService communicationService;

	public DatamodelServiceImpl(ICommunicationService communicationService, IDatabaseService databaseService, IValueService valueService, IActorService actorService) throws SQLException {
		this.communicationService = communicationService;
		this.databaseService = databaseService;
		this.valueService = valueService;
		this.actorService = actorService;

		loadedState = new ObservableBoolean(false);

		datamodel = databaseService.loadDatamodel();

		registerData();

		loadedState.changeValue(true);

		communicationService.datamodelReady();
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
