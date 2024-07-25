package com.osh.activity;

import static android.widget.Toast.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
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
import android.util.Log;
import android.view.View;

import com.osh.databinding.ActivityDoorOpenBinding;
import com.osh.service.IDoorUnlockService;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.concurrent.Executor;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DoorOpenActivity extends AppCompatActivity implements IDoorUnlockService.CallbackListener {

    public static final String FRONT_DOOR_ID = "frontDoor.door";
    private ActivityDoorOpenBinding binding;
    public static final String DU_EXTRA_DOOR_ID = "doorId";
    private static final String TAG = DoorOpenActivity.class.getName();

    public static final String REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT = "com.osh.action.requestDoorUnlockChallenge";

    @Inject
    IDoorUnlockService doorUnlockManager;

    CountDownTimer finishTimer;

    public static void invokeActivity(Context context, String doorId) {
        invokeActivity(context, doorId, true);
    }

    public static void invokeActivity(Context context, String doorId, boolean fromApp) {
        Intent intent = new Intent(DoorOpenActivity.REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT);
        intent.putExtra(DoorOpenActivity.DU_EXTRA_DOOR_ID, doorId);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDoorOpenBinding.inflate(getLayoutInflater());

        binding.tryAgainBtn.setOnClickListener(view -> {
            executeAuth(this, "frontDoor.door");
        });
        binding.tryAgainBtn.setVisibility(View.INVISIBLE);

        setContentView(binding.getRoot());

        doorUnlockManager.setCallbackListener(this);

        finishTimer = new CountDownTimer(4000, 1000) {
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
        if (REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT.equals(intent.getAction())) {
            Log.i(TAG, "Requesting challenge");

            String doorId = intent.getStringExtra(DU_EXTRA_DOOR_ID);

            if (!StringUtils.isEmpty(doorId)) {
                executeAuth(this, doorId);
            } else {
                Log.w(TAG, "No doorid defined");
            }
        } else {
            Log.w(TAG, "Ignoring intent");
        }
    }

    private void executeAuth(Context context, String doorId) {
        Executor executor;
        BiometricPrompt biometricPrompt;
        BiometricPrompt.PromptInfo promptInfo;

        String userId = ((OshApplication)getApplication()).getApplicationConfig().getUser().getUserId();

        executor = ContextCompat.getMainExecutor(this);

        if (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
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
                    proceedUnlock(context, userId, doorId);
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
            // show prompt
            new AlertDialog.Builder(this)
                    .setTitle("Open door")
                    .setMessage("Open door now?")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("Unlock", (dialogInterface, i) -> {
                        proceedUnlock(context, userId, doorId);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }
    }

    private void proceedUnlock(Context context, String userId, String doorId) {
        doorUnlockManager.requestChallenge(context, userId, doorId);
    }

    @Override
    public void onAuthSuccess() {
        runOnUiThread(() -> {
            binding.tryAgainBtn.setVisibility(View.GONE);
            binding.doorOpenStatus.setText("Door opened");
            binding.animationView.playAnimation();
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