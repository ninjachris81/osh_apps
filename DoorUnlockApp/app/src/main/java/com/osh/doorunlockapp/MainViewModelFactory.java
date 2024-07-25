package com.osh.doorunlockapp;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.service.IDoorUnlockService;

public class MainViewModelFactory implements ViewModelProvider.Factory {

    private static MainViewModelFactory instance;

    private final MqttConfig mqttConfig;
    private final String userId;
    private final String deviceId;
    private final IDoorUnlockService.CallbackListener callbackListener;

    public MainViewModelFactory(IDoorUnlockService.CallbackListener callbackListener, MqttConfig mqttConfig, String userId, String deviceId) {
        this.callbackListener = callbackListener;
        this.mqttConfig = mqttConfig;
        this.userId = userId;
        this.deviceId = deviceId;
    }

    public static MainViewModelFactory getInstance(IDoorUnlockService.CallbackListener callbackListener, MqttConfig mqttConfig, String userId, String deviceId) {
        if (instance != null) return instance;
        instance = new MainViewModelFactory(callbackListener, mqttConfig, userId, deviceId);
        return instance;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MainViewModel(callbackListener, mqttConfig, userId, deviceId);
    }
}
