package com.osh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.ExperimentalBadgeUtils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StartActivity extends AppCompatActivity {

    private static final String TAG = StartActivity.class.getSimpleName();


    @OptIn(markerClass = ExperimentalBadgeUtils.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!((OshApplication) getApplication()).getApplicationConfig().isValid()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            TelephonyManager manager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);
            if(manager.getPhoneType() == TelephonyManager.PHONE_TYPE_NONE){
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }else{
                Intent intent = new Intent(this, MainActivityMobile.class);
                startActivity(intent);
            }

        }

        finish();
    }

}

