package com.osh.service.impl;

import static com.osh.doorunlock.DoorUnlockMessage.DU_ATTRIB_INITIATOR_ID;
import static com.osh.service.impl.psk.PSK;

import android.content.Context;
import android.util.Base64;

import com.osh.communication.MessageBase;
import com.osh.doorunlock.DoorUnlockMessage;
import com.osh.service.ICommunicationService;
import com.osh.log.LogFacade;
import com.osh.service.IDoorUnlockService;
import com.osh.user.User;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DoorUnlockServiceImpl implements IDoorUnlockService {

    public interface DoorUnlockCallback {

        void result(boolean success);

    }

    private static final String TAG = DoorUnlockServiceImpl.class.getName();

    private ICommunicationService communicationManager;

    private CallbackListener callbackListener;

    private ExecutorService executorService;

    private String deviceId;


    public DoorUnlockServiceImpl(ICommunicationService communicationManager, String deviceId) {
        this.communicationManager = communicationManager;
        communicationManager.registerMessageType(MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_DOOR_UNLOCK, this);
        this.deviceId = deviceId;
    }

    @Override
    public void setCallbackListener(CallbackListener callbackListener) {
        this.callbackListener = callbackListener;
    }

    @Override
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public MessageBase.MESSAGE_TYPE getMessageType() {
        return MessageBase.MESSAGE_TYPE.MESSAGE_TYPE_DOOR_UNLOCK;
    }

        @Override
    public void handleReceivedMessage(MessageBase msg) {
        if (msg instanceof DoorUnlockMessage) {
            DoorUnlockMessage dum = (DoorUnlockMessage) msg;
            if (dum.getValues().containsKey(DU_ATTRIB_INITIATOR_ID) && dum.getValues().get(DU_ATTRIB_INITIATOR_ID).toString().equals(deviceId) && dum.getValues().containsKey(DoorUnlockMessage.DU_ATTRIB_STAGE)) {
                DoorUnlockMessage.DU_AUTH_STAGE stage = DoorUnlockMessage.DU_AUTH_STAGE.values()[(int) dum.getValues().get(DoorUnlockMessage.DU_ATTRIB_STAGE)];

                switch (stage) {
                    case CHALLENGE_CREATED:
                        String oth = (String) dum.getValues().get(DoorUnlockMessage.DU_ATTRIB_OTH);
                        Long ts = (Long) dum.getValues().get(DoorUnlockMessage.DU_ATTRIB_TS);

                        if (!StringUtils.isEmpty(oth)) {
                            if (ts != null) {
                                respondChallenge(dum.getUserId(), dum.getDoorId(), ts, oth);
                            } else {
                                LogFacade.w(TAG,"TS cannot be empty");
                            }
                        } else {
                            LogFacade.w(TAG,"OTH cannot be empty");
                        }
                        break;
                    case CHALLENGE_SUCCESS:
                        LogFacade.i(TAG, "Auth Success");
                        if (callbackListener != null) {
                            callbackListener.onAuthSuccess();
                        }
                        break;
                    case CHALLENGE_FAILURE:
                        LogFacade.w(TAG, "Auth Failure");
                        if (callbackListener != null) {
                            callbackListener.onAuthFailure();
                        }
                        break;
                }
            }
        }
    }

    private String calculateChallenge(String userId, String doorId, long timestamp, String oth) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");

            md.update(String.valueOf(timestamp).getBytes(Charset.forName("ISO-8859-1")));       // Latin1
            md.update(oth.getBytes(Charset.forName("ISO-8859-1")));
            md.update(userId.getBytes(Charset.forName("ISO-8859-1")));
            md.update(PSK.getBytes(Charset.forName("ISO-8859-1")));
            md.update(doorId.getBytes(Charset.forName("ISO-8859-1")));

            return Base64.encodeToString(md.digest(), Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void respondChallenge(String userId, String doorId, long ts, String oth) {
        String resultHash = calculateChallenge(userId, doorId, ts, oth);

        LogFacade.d(TAG, resultHash);

        if (resultHash != null) {
            DoorUnlockMessage msg = new DoorUnlockMessage(userId, doorId, Map.of(
                    DoorUnlockMessage.DU_ATTRIB_STAGE, DoorUnlockMessage.DU_AUTH_STAGE.CHALLENGE_CALCULATED.ordinal(),
                    DoorUnlockMessage.DU_ATTRIB_OTH, oth,
                    DoorUnlockMessage.DU_ATTRIB_TS, ts,
                    DoorUnlockMessage.DU_ATTRIB_RESULT_HASH, resultHash,
                    DoorUnlockMessage.DU_ATTRIB_INITIATOR_ID, deviceId
                    ));
            communicationManager.sendMessage(msg);
        } else {
            LogFacade.w(TAG, "Error while calculating result hash");
        }
    }

    @Override
    public void requestChallenge(Context context, String userId, String doorId) {
        executorService = Executors.newFixedThreadPool(1);

        executorService.submit(() -> {
            DoorUnlockMessage msg = new DoorUnlockMessage(userId, doorId, Map.of(
                    DoorUnlockMessage.DU_ATTRIB_STAGE, DoorUnlockMessage.DU_AUTH_STAGE.CHALLENGE_REQUEST.ordinal(),
                    DoorUnlockMessage.DU_ATTRIB_INITIATOR_ID, deviceId)
            );

            int timeout = 4;

            while (timeout > 0) {
                if (communicationManager.sendMessage(msg)) {
                    break;
                } else {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    timeout--;
                }
            }
        });
    }

    @Override
    public void requestChallenge(Context context, User user, String doorId) {
        requestChallenge(context, user.getId(), doorId);
    }
}
