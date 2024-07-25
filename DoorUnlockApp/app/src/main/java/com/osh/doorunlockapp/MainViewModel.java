package com.osh.doorunlockapp;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.service.ICommunicationService;
import com.osh.service.IDoorUnlockService;
import com.osh.service.impl.DoorUnlockServiceImpl;
import com.osh.service.impl.MqttCommunicationServiceImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel implements IDoorUnlockService.CallbackListener {

    private final IDoorUnlockService.CallbackListener callbackListener;

    public interface ProgressHander {
        void onProgress(int percentage);
    }

    private Executor executor = Executors.newSingleThreadExecutor();

    private IDoorUnlockService doorUnlockManager;
    private ICommunicationService communicationService;

    public MainViewModel(IDoorUnlockService.CallbackListener callbackListener, MqttConfig mqttConfig, String userId, String deviceId) {
        this.callbackListener = callbackListener;
        this.communicationService = new MqttCommunicationServiceImpl(mqttConfig);
        this.doorUnlockManager = new DoorUnlockServiceImpl(communicationService, deviceId);

        executor.execute(() -> {
            doorUnlockManager.setCallbackListener(this);
            communicationService.connectMqtt();

        });
    }

    public boolean isReady() {
        return communicationService.isConnected();
    }

    public void executeUnlock(Context context, String doorId, String userId) {
        doorUnlockManager.requestChallenge(context, userId, doorId);
    }

    public boolean waitUntilConnected(ProgressHander progressHander) {
        int timeout = 10;

        while (!communicationService.isConnected() && timeout > 0) {
            try {
                progressHander.onProgress(100 - (timeout * 10));
                Thread.sleep(1000);
                timeout--;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return timeout > 0;
    }

    @Override
    public void onAuthSuccess() {
        callbackListener.onAuthSuccess();
    }

    @Override
    public void onAuthFailure() {
        callbackListener.onAuthFailure();
    }

}
