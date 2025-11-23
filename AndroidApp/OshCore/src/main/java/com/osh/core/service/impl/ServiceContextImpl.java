package com.osh.core.service.impl;

import com.osh.core.service.IDeviceDiscoveryService;
import com.osh.core.service.IDoorUnlockService;
import com.osh.core.service.IActorService;
import com.osh.core.service.IAudioActorService;
import com.osh.core.service.IAudioSourceService;
import com.osh.core.service.ICommunicationService;
import com.osh.core.service.IDatabaseService;
import com.osh.core.service.IDatamodelService;
import com.osh.core.service.IServiceContext;
import com.osh.core.service.IValueService;

public class ServiceContextImpl implements IServiceContext {
    private final IActorService actorService;
    private final IAudioActorService audioActorService;
    private final IAudioSourceService audioSourceService;
    private final IDoorUnlockService doorUnlockManager;
    private final IValueService valueService;
    private final IDatabaseService databaseService;
    private final IDatamodelService datamodelService;
    private final ICommunicationService communicationService;

    private final IDeviceDiscoveryService deviceDiscoveryService;

    public ServiceContextImpl(IActorService actorService, IAudioActorService audioActorService, IAudioSourceService audioSourceService, IDoorUnlockService doorUnlockManager, IValueService valueService, IDatabaseService databaseService, IDatamodelService datamodelService, ICommunicationService communicationService, IDeviceDiscoveryService deviceDiscoveryService) {
        this.actorService = actorService;
        this.audioActorService = audioActorService;
        this.audioSourceService = audioSourceService;
        this.doorUnlockManager = doorUnlockManager;
        this.valueService = valueService;
        this.databaseService = databaseService;
        this.datamodelService = datamodelService;
        this.communicationService = communicationService;
        this.deviceDiscoveryService = deviceDiscoveryService;
    }

    @Override
    public IActorService getActorService() {
        return this.actorService;
    }

    @Override
    public IAudioActorService getAudioActorService() {
        return this.audioActorService;
    }

    @Override
    public IAudioSourceService getAudioSourceService() {
        return audioSourceService;
    }

    @Override
    public IDoorUnlockService getDoorUnlockManager() {
        return this.doorUnlockManager;
    }

    @Override
    public IValueService getValueService() {
        return this.valueService;
    }

    @Override
    public IDatabaseService getDatabaseService() {
        return this.databaseService;
    }

    @Override
    public IDatamodelService getDatamodelService() {
        return this.datamodelService;
    }

    @Override
    public ICommunicationService getCommunicationService() {
        return this.communicationService;
    }

    @Override
    public IDeviceDiscoveryService getDeviceDiscoveryService() {
        return this.deviceDiscoveryService;
    }
}
