package com.osh.service;

import android.content.Context;

import com.osh.manager.IMqttSupport;
import com.osh.service.impl.DoorUnlockServiceImpl;
import com.osh.user.User;

public interface IDoorUnlockService extends IMqttSupport {

    void requestChallenge(Context context, String userId, String doorId);

    void requestChallenge(Context context, User user, String doorId);

    void setCallbackListener(CallbackListener listener);

    public interface CallbackListener {

        void onAuthSuccess();

        void onAuthFailure();

    }
}
