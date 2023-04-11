package com.osh;

import static android.widget.Toast.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.osh.config.IApplicationConfig;
import com.osh.databinding.ActivityDoorOpenBinding;
import com.osh.databinding.ActivitySipCallBinding;
import com.osh.service.IDoorUnlockService;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DoorOpenActivity extends AppCompatActivity implements IDoorUnlockService.CallbackListener {

    private ActivityDoorOpenBinding binding;
    public static final String DU_EXTRA_DOOR_ID = "doorId";
    public static final String DU_EXTRA_FROM_APP = "fromApp";
    private static final String TAG = DoorOpenActivity.class.getName();

    public static final String REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT = "com.osh.action.requestDoorUnlockChallenge";

    @Inject
    IDoorUnlockService doorUnlockManager;

    CountDownTimer finishTimer;

    public static void invokeActivity(Context context, String doorId) {
        Intent intent = new Intent(DoorOpenActivity.REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT);
        intent.putExtra(DoorOpenActivity.DU_EXTRA_DOOR_ID, doorId);
        intent.putExtra(DoorOpenActivity.DU_EXTRA_FROM_APP, true);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoorOpenBinding.inflate(getLayoutInflater());

        binding.tryAgainBtn.setOnClickListener(view -> {
            executeAuth("frontDoor.door", true);
        });

        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        doorUnlockManager.setCallbackListener(this);

        finishTimer = new CountDownTimer(3000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                finish();
            }
        };

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];

                Log.i(TAG, "Messages: " + rawMessages.length);

                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];

                    if (messages[i].getRecords().length == 1) {
                        String data = new String(messages[i].getRecords()[0].getPayload());

                        if (!StringUtils.isEmpty(data)) {
                            Log.i(TAG, "Requesting challenge");
                            executeAuth(data, false);
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
        } else if (REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT.equals(intent.getAction())) {
            Log.i(TAG, "Requesting challenge");

            String doorId = intent.getStringExtra(DU_EXTRA_DOOR_ID);
            boolean fromApp = intent.getBooleanExtra(DU_EXTRA_FROM_APP, false);

            if (!StringUtils.isEmpty(doorId)) {
                executeAuth(doorId, fromApp);
            } else {
                Log.w(TAG, "No doorid defined");
            }
        } else {
            Log.w(TAG, "Ignoring intent");
        }
    }

    private void executeAuth(String doorId, boolean fromApp) {
        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;

        String userId = ((OshApplication)getApplication()).getApplicationConfig().getUser().getUserId();

        executor = ContextCompat.getMainExecutor(this);

        if (fromApp) {
            biometricPrompt = new BiometricPrompt(this,
                    executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode,
                                                  @NonNull CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    makeText(getApplicationContext(),
                            "Authentication error: " + errString, LENGTH_SHORT)
                            .show();
                }

                @Override
                public void onAuthenticationSucceeded(
                        @NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    doorUnlockManager.requestChallenge(userId, doorId);
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                    makeText(getApplicationContext(), "Bio Authentication failed",
                            LENGTH_SHORT)
                            .show();
                }
            });

            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric login")
                    .setNegativeButtonText("Cancel")
                    .setSubtitle("Log in to unlock door")
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                    .build();

            biometricPrompt.authenticate(promptInfo);
        } else {
            // directly send
            doorUnlockManager.requestChallenge(userId, doorId);
        }
    }

    @Override
    public void onAuthSuccess() {
        runOnUiThread(() -> {
            binding.tryAgainBtn.setVisibility(View.GONE);
            binding.doorOpenStatus.setText("Door opened");
        });

        finishTimer.cancel();
        finishTimer.start();
    }

    @Override
    public void onAuthFailure() {
        runOnUiThread(() -> {
            binding.tryAgainBtn.setVisibility(View.VISIBLE);
            binding.tryAgainBtn.invalidate();
            binding.tryAgainBtn.requestLayout();
            binding.doorOpenStatus.setText("Failed to open door");
            binding.tryAgainBtn.invalidate();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (finishTimer != null) {
            finishTimer.cancel();
        }
    }
}