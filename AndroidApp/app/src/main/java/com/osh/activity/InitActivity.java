package com.osh.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import com.osh.R;
import com.osh.service.ICommunicationService;
import com.osh.service.IServiceContext;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class InitActivity extends AppCompatActivity {

    private static final String TAG = InitActivity.class.getName();
    @Inject
    Lazy<ICommunicationService> communicationService;

    @Inject
    Lazy<IServiceContext> serviceContext;

    ExecutorService service = Executors.newFixedThreadPool(1);

    public static Date createdNFCToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        Intent intent = getIntent();
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];

                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];

                    if (messages[i].getRecords().length == 1) {
                        String data = new String(messages[i].getRecords()[0].getPayload());

                        createdNFCToken = new Date();

                        if (!StringUtils.isEmpty(data)) {
                            communicationService.get().connectedState().addItemChangeListener(item -> {
                                if (item.booleanValue()) {
                                    startOpenDoorActivity(data);
                                }
                            }, true);

                        } else {
                            Log.w(TAG, "No doorid defined");
                        }
                        Log.i(TAG, data);
                    } else {
                        Log.w(TAG, "Invalid record size");
                    }
                }
            } else {
                Log.w(TAG, "No payload data");
            }
        } else {
            //service.submit(() -> {
                serviceContext.get();
                MainActivity.invokeActivity(this);
                finish();
            //});
            //service.shutdown();
        }
    }



    private void startOpenDoorActivity(String doorId) {
        if (communicationService.get().connectedState().getValue()) {
            DoorOpenActivity.invokeActivity(this, doorId, false);
            finish();
        }
    }
}