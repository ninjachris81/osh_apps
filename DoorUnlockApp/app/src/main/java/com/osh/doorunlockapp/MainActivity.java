package com.osh.doorunlockapp;

import static android.widget.Toast.LENGTH_SHORT;
import static android.widget.Toast.makeText;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.provider.Settings;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.osh.SettingsActivity;
import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.log.LogFacade;
import com.osh.service.IDoorUnlockService;

import org.apache.commons.lang3.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements IDoorUnlockCallbackListener {

    enum UIState {
        DISCONNECTED,
        SCAN_READY,
        UNLOCKING,
        AUTH_SUCCESS,
        AUTH_FAILED,
        ERROR
    }

    private static final String TAG = MainActivity.class.getName();

    public static final String FRONT_DOOR_ID = "frontDoor.door";

    public static final String REQUEST_DOOR_UNLOCK_CHALLENGE_INTENT = "com.osh.action.requestDoorUnlockChallenge";
    private static final int REQUEST_LOCATION_PERMISSION = 66;

    private String userId;
    private String deviceId;
    private String wifiName;
    private boolean bioAuthRequired = true;
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
        this.wifiName = prefs.getString(getString(R.string.wifi_name_key), "");
        this.bioAuthRequired = prefs.getBoolean(getString(R.string.bio_auth_key), true);

        config.setClientId(prefs.getString(getString(R.string.mqtt_client_id_key), ""));
        if (config.getClientId().isEmpty()) {
            // set new random client ID
            String clientId = "AndroidClient_" + System.currentTimeMillis();
            prefs.edit().putString(getString(R.string.mqtt_client_id_key), clientId).commit();
            config.setClientId(clientId);
        }
        config.setServerHost(prefs.getString(getString(R.string.mqtt_host_key), "127.0.0.1"));
        config.setServerPort(Integer.parseInt(prefs.getString(getString(R.string.mqtt_port_key), "1883")));

        if (userId.isEmpty() || deviceId.isEmpty() || wifiName.isEmpty()) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            makeText(getApplicationContext(),
                    "Invalid settings", LENGTH_SHORT)
                    .show();
        } else {

            findViewById(R.id.mainLayout).setOnLongClickListener(v -> {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            });

            TextView userLabel = findViewById(R.id.userLabel);
            userLabel.setText("User " + userId);

            statusIcon = findViewById(R.id.statusIcon);

            progressBar = findViewById(R.id.progressBar);

            label = findViewById(R.id.statusLabel);

            this.mainViewModel = new ViewModelProvider(this, new MainViewModelFactory(this, config, userId, deviceId)).get(MainViewModel.class);

            final Intent intent = getIntent();

            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            } else {
                checkWifi();

                handleIntent(intent);
            }
        }
    }


    private void checkWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        if (wifiInfo.getSSID() != null && !wifiInfo.getSSID().equals(WifiManager.UNKNOWN_SSID)) {
            String realWifiName = wifiInfo.getSSID().replace("\"", "");

            if (!wifiName.equals(realWifiName)) {
                LogFacade.i(TAG, "Connected to wifi: " + realWifiName + ", trying to connect to " + wifiName);

                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

                /*

                WifiNetworkSuggestion networkSuggestion = new WifiNetworkSuggestion.Builder()
                        .setSsid(wifiName)
                        .build();
                List<WifiNetworkSuggestion> suggestions = new ArrayList<>();
                suggestions.add(networkSuggestion);
                int status = wifiManager.addNetworkSuggestions(suggestions);

                if (WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS == status) {
                    LogFacade.i(TAG, "Success");
                }

                 */

                /*
                final NetworkSpecifier specifier =
                        new WifiNetworkSpecifier.Builder()
                                .setSsid(URLEncoder.encode(wifiName, "UTF-8"))
                                //.setSsidPattern(new PatternMatcher(wifiName, PatternMatcher.PATTERN_PREFIX))
                                .build();
                final NetworkRequest request =
                        new NetworkRequest.Builder()
                                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                                .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                                .setNetworkSpecifier(specifier)
                                .build();

                final ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                    @Override
                    public void onAvailable(@NonNull Network network) {
                        super.onAvailable(network);
                    }

                    @Override
                    public void onUnavailable() {
                        super.onUnavailable();
                    }
                };
                connectivityManager.requestNetwork(request, networkCallback);
                 */
            } else {
                LogFacade.i(TAG, "Already connected to wifi");
            }
        } else {
            LogFacade.i(TAG, "Unknown SSID or permission error");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                if (grantResults.length > 0) {
                    boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (locationAccepted) {
                        finish();
                    }
                }
                break;
        }
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
        updateUIState(UIState.UNLOCKING);

        if (this.bioAuthRequired) {
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
        } else {
            mainViewModel.executeUnlock(this, doorId, userId);
        }
    }

    private CountDownTimer resetTimer;

    private void updateUIState(UIState state) {
        runOnUiThread(() -> {
            switch (state) {
                case DISCONNECTED:
                    label.setText("Disconnected");
                    statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_database_off));
                    statusIcon.setColorFilter(Color.RED);
                    break;
                case AUTH_SUCCESS:
                    label.setText("Success");
                    statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_door_open));
                    statusIcon.setColorFilter(Color.GREEN);

                    resetTimer = new CountDownTimer(3000, 1000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            updateUIState(UIState.SCAN_READY);
                        }
                    };
                    resetTimer.start();


                    break;
                case AUTH_FAILED:
                    label.setText("Failed");
                    statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_lock_open_alert));
                    statusIcon.setColorFilter(Color.RED);
                    break;
                case SCAN_READY:
                    label.setText("Ready - Please scan RFID Tag");
                    statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tag_text_outline));
                    statusIcon.setColorFilter(Color.WHITE);
                    break;
                case UNLOCKING:
                    label.setText("Unlocking...");
                    statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_tag_text_outline));
                    break;
                case ERROR:
                    label.setText("General error");
                    statusIcon.setImageDrawable(AppCompatResources.getDrawable(this, R.drawable.ic_lock_open_alert));
                    statusIcon.setColorFilter(Color.RED);
                    break;
            }
            progressBar.setVisibility(View.INVISIBLE);
        });
    }


    @Override
    public void onConnected() {
        updateUIState(UIState.SCAN_READY);
    }

    @Override
    public void onDisconnected() {
        updateUIState(UIState.DISCONNECTED);
    }

    @Override
    public void onProgress(int percentage) {
        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(percentage);
        });
    }

    @Override
    public void onAuthSuccess() {
        updateUIState(UIState.AUTH_SUCCESS);
    }

    @Override
    public void onAuthFailure() {
        updateUIState(UIState.AUTH_FAILED);
    }

    @Override
    public void onGeneralError() {
        updateUIState(UIState.ERROR);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (resetTimer != null) {
            resetTimer.cancel();
        }
    }
}