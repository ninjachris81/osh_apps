package com.osh.activity;

import android.content.Context;

import com.osh.service.IActorService;
import com.osh.service.IAudioActorService;
import com.osh.service.IAudioSourceService;
import com.osh.service.ICommunicationService;
import com.osh.service.IDatabaseService;
import com.osh.service.IDatamodelService;
import com.osh.service.IDeviceDiscoveryService;
import com.osh.service.IServiceContext;
import com.osh.service.impl.ActorServiceImpl;
import com.osh.service.impl.AudioActorServiceImpl;
import com.osh.service.impl.AudioSourceServiceImpl;
import com.osh.service.impl.ClientDeviceDiscoveryServiceImpl;
import com.osh.service.impl.DatabaseServiceImpl;
import com.osh.service.impl.DatamodelServiceImpl;
import com.osh.service.impl.LocalDatabaseServiceImpl;
import com.osh.service.impl.MqttCommunicationServiceImpl;
import com.osh.config.IApplicationConfig;
import com.osh.service.impl.DoorUnlockServiceImpl;
import com.osh.service.IDoorUnlockService;
import com.osh.service.IValueService;
import com.osh.service.impl.ServiceContextImpl;
import com.osh.service.impl.ValueServiceImpl;
import com.osh.wbb12.service.IWBB12Service;
import com.osh.wbb12.service.impl.WBB12ServiceImpl;

import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class ServiceModules {

    static ExecutorService service = Executors.newFixedThreadPool(2);

    public static long updatedDBVersion = -1;

    @Provides
    @Singleton
    static IDatabaseService provideDatabaseService(@ApplicationContext Context context, IApplicationConfig applicationConfig) {
        try {
            return service.submit(() -> {
                try {
                    LocalDatabaseServiceImpl localDatabaseService = new LocalDatabaseServiceImpl(context, applicationConfig.getDatabase());
                    if (localDatabaseService.isEmpty() || localDatabaseService.canUpdate()) {
                        // if not, connect to real one and copy
                        final DatabaseServiceImpl databaseService = new DatabaseServiceImpl(applicationConfig.getDatabase(), true);
                        localDatabaseService.copyData(databaseService);
                        updatedDBVersion = databaseService.getVersion();
                    }

                    return localDatabaseService;
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).get();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    static IDatamodelService provideDatamodelService(ICommunicationService communicationService, IDatabaseService databaseService, IValueService valueService, IActorService actorService) {
        try {
            IDatamodelService returnService = service.submit(() -> {
                try {
                    return new DatamodelServiceImpl(databaseService, valueService, actorService);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }).get();

            communicationService.datamodelReady();

            return returnService;
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    static IActorService provideActorService(ICommunicationService communicationService, IValueService valueService) {
        return new ActorServiceImpl(communicationService, valueService);
    }

    @Provides
    @Singleton
    static IAudioActorService provideAudioActorService(IValueService valueService, IActorService actorService, IDatamodelService datamodelService) {
        return new AudioActorServiceImpl(valueService, actorService, datamodelService);
    }

    @Provides
    @Singleton
    static IAudioSourceService provideAudioSourceService(IDatamodelService datamodelService) {
        return new AudioSourceServiceImpl(datamodelService);
    }

    @Provides
    @Singleton
    static IValueService provideValueService(ICommunicationService communicationManager) {
        return new ValueServiceImpl(communicationManager);
    }

    @Provides
    @Singleton
    static ICommunicationService provideCommunicationManager(IApplicationConfig applicationConfig) {
        return new MqttCommunicationServiceImpl(applicationConfig.getMqtt());
    }

    @Provides
    @Singleton
    static IDoorUnlockService provideDoorUnlockManager(ICommunicationService communicationManager, IApplicationConfig applicationConfig) {
        return new DoorUnlockServiceImpl(communicationManager, applicationConfig.getMqtt().getClientId());
    }

    @Provides
    @Singleton
    static IWBB12Service provideWBB12Manager(IDatamodelService datamodelService, IValueService valueManager, IActorService actorService) {
        return new WBB12ServiceImpl(datamodelService, valueManager, actorService);
    }

    @Provides
    @Singleton
    static IDeviceDiscoveryService provideDeviceDiscoveryService(ICommunicationService communicationManager, IDatamodelService datamodelService, IApplicationConfig applicationConfig) {
        return new ClientDeviceDiscoveryServiceImpl(communicationManager, datamodelService, applicationConfig.getMqtt().getClientId());
    }

    @Provides
    @Singleton
    static IServiceContext provideServiceContext(IActorService actorService, IAudioActorService audioActorService, IAudioSourceService audioSourceService, IDoorUnlockService doorUnlockManager, IValueService valueService, IDatabaseService databaseService, IDatamodelService datamodelService, ICommunicationService communicationService, IDeviceDiscoveryService deviceDiscoveryService) {
        return new ServiceContextImpl(actorService, audioActorService, audioSourceService, doorUnlockManager, valueService, databaseService, datamodelService, communicationService, deviceDiscoveryService);
    }


}
