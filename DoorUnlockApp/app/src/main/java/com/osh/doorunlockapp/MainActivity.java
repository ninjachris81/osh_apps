package com.osh.doorunlockapp;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.osh.SettingsActivity;
import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.service.IDoorUnlockService;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements IDoorUnlockService.CallbackListener {

    private static final String TAG = MainActivity.class.getName();

    public static final String FRONT_DOOR_ID = "frontDoor.door";

    public static final String REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT = "com.osh.action.requestDoorUnlockChallenge";

    private String userId;
    private String deviceId;
    private final MqttConfig config = new MqttConfig();

    private MainViewModel mainViewModel;

    private TextView label;
    private ProgressBar progressBar;
    private ImageView statusIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getDefaultSharedPreferences(this);

        this.userId = prefs.getString(getString(R.string.user_settings_key), "");
        this.deviceId = prefs.getString(getString(R.string.device_id_key), "");

        config.setClientId(prefs.getString(getString(R.string.mqtt_client_id_key), ""));
        if (config.getClientId().isEmpty()) {
            // set new random client ID
            String clientId = "AndroidClient_" + System.currentTimeMillis();
            prefs.edit().putString(getString(R.string.mqtt_client_id_key), clientId).commit();
            config.setClientId(clientId);
        }
        config.setServerHost(prefs.getString(getString(R.string.mqtt_host_key), "127.0.0.1"));
        config.setServerPort(Integer.parseInt(prefs.getString(getString(R.string.mqtt_port_key), "1883")));

        if (userId.isEmpty() || deviceId.isEmpty()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            makeText(getApplicationContext(),
                    "Invalid settings", LENGTH_SHORT)
                    .show();
        }

        TextView userLabel = findViewById(R.id.userLabel);
        userLabel.setText("User " + userId);

        statusIcon = findViewById(R.id.statusIcon);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        label = findViewById(R.id.statusLabel);

        this.mainViewModel = new ViewModelProvider(this, MainViewModelFactory.getInstance(this, config, userId, deviceId)).get(MainViewModel.class);

        Executor executor = Executors.newSingleThreadExecutor();

        final Intent intent = getIntent();

        executor.execute(() -> {
            runOnUiThread(() -> {
                label.setText("Connecting...");
            });

            if (mainViewModel.waitUntilConnected(percentage -> {
                runOnUiThread(() -> {
                    progressBar.setProgress(percentage);
                });
            })) {
                runOnUiThread(() -> {
                    label.setText("Ready - Please scan RFID Tag");
                    statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tag_text_outline));
                    progressBar.setVisibility(View.INVISIBLE);
                });

                handleIntent(intent);
            } else {
                runOnUiThread(() -> {
                    makeText(getApplicationContext(), "Timeout while connecting",
                            LENGTH_SHORT)
                            .show();
                });
            }
        });
    }

    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(@NonNull Intent intent) {
        if (intent != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Parcelable[] rawMessages =
                    intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (rawMessages != null) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];

                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];

                    if (messages[i].getRecords().length == 1) {
                        String data = new String(messages[i].getRecords()[0].getPayload());

                        if (!StringUtils.isEmpty(data)) {
                            executeAuth(data, userId);
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
        }
    }

    private void executeAuth(String doorId, String userId) {
        Executor executor;

        final Context context = this;

        executor = ContextCompat.getMainExecutor(context);

        executor.execute(() -> {
            BiometricPrompt biometricPrompt;
            BiometricPrompt.PromptInfo promptInfo;

            if (BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS) {
                biometricPrompt = new BiometricPrompt(this,
                        executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode,
                                                      @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == 13) {
                            label.setText("Biometric auth canceled");
                            statusIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_cancel));
                        } else {
                            makeText(getApplicationContext(),
                                    "Authentication error: " + errString, LENGTH_SHORT)
                                    .show();
                            statusIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_cancel));
                        }
                    }

                    @Override
                    public void onAuthenticationSucceeded(
                            @NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        mainViewModel.executeUnlock(context, doorId, userId);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        label.setText("Process aborted");
                        statusIcon.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.ic_lock_open_alert));
                    }
                });

                promptInfo = new BiometricPrompt.PromptInfo.Builder()
                        .setTitle("Biometric login")
                        .setNegativeButtonText("Cancel")
                        .setSubtitle("Log in to unlock door")
                        .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                        .build();

                biometricPrompt.authenticate(promptInfo);
            }
        });
    }


    @Override
    public void onAuthSuccess() {
        label.setText("Success");
        statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_door_open));

        Executor executor = ContextCompat.getMainExecutor(this);
        executor.execute(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            finish();
        });
    }

    @Override
    public void onAuthFailure() {
        label.setText("Failed");
        statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_lock_open_alert));
    }
}