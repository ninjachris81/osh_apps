package com.osh.doorunlockapp;

import static android.Manifest.permission_group.LOCATION;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.PatternMatcher;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModel;

import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.log.LogFacade;
import com.osh.service.ICommunicationService;
import com.osh.service.IDoorUnlockService;
import com.osh.service.impl.DoorUnlockServiceImpl;
import com.osh.service.impl.MqttCommunicationServiceImpl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainViewModel extends ViewModel implements IDoorUnlockService.CallbackListener {

    private final String TAG = MainViewModel.class.getName();

    private final IDoorUnlockCallbackListener callbackListener;

    @Override
    public void onAuthSuccess() {
        callbackListener.onAuthSuccess();
    }

    @Override
    public void onAuthFailure() {
        callbackListener.onAuthFailure();
    }

    @Override
    public void onGeneralError() {
        callbackListener.onGeneralError();
    }

    private Executor executor = Executors.newSingleThreadExecutor();

    private IDoorUnlockService doorUnlockManager;
    private ICommunicationService communicationService;


    private final Timer timer = new Timer();

    public MainViewModel(IDoorUnlockCallbackListener callbackListener, MqttConfig mqttConfig, String userId, String deviceId) {
        this.callbackListener = callbackListener;
        this.communicationService = new MqttCommunicationServiceImpl(mqttConfig);
        this.doorUnlockManager = new DoorUnlockServiceImpl(communicationService, deviceId);

        TimerTask t = new TimerTask() {

            int timeout = 10;

            @Override
            public void run() {
                timeout--;
                callbackListener.onProgress((10 - timeout) * 10);

                if (timeout <= 0) {
                    this.cancel();
                }
            }
        };
        timer.schedule(t,1000,1000);

        communicationService.connectedState().addItemChangeListener(item -> {
            timer.cancel();

            if (item) {
                callbackListener.onConnected();
            } else {
                callbackListener.onDisconnected();
            }
        });

        executor.execute(() -> {
            doorUnlockManager.setCallbackListener(this);
            communicationService.connectMqtt();
        });
    }

    public void executeUnlock(Context context, String doorId, String userId) {
        doorUnlockManager.requestChallenge(context, userId, doorId);
    }

}
