package com.osh.doorunlockapp;

import com.osh.service.IDoorUnlockService;

public interface IDoorUnlockCallbackListener extends IDoorUnlockService.CallbackListener {

    void onConnected();

    void onDisconnected();

    void onProgress(int percentage);

}
