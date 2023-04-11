package com.osh;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.osh.databinding.ActivityMainBinding;
import com.osh.log.LogFacade;
import com.osh.service.IServiceContext;
import com.osh.sip.OshAccount;
import com.osh.ui.area.AreaFragment;
import com.osh.ui.dashboard.DashboardFragment;
import com.osh.ui.home.HomeFragment;
import com.osh.ui.wbb12.WBB12Fragment;
import com.osh.utils.IObservableBoolean;
import com.osh.utils.ObservableBoolean;
import com.osh.wbb12.service.IWBB12Service;

import net.gotev.sipservice.SipAccountData;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject
    IWBB12Service wbb12Service;

    @Inject
    IServiceContext serviceContext;

    private OshAccount sipAccount;
    private ObservableBoolean sipConnectedState = new ObservableBoolean(false);
    private static final int REQUEST_PERMISSIONS_APP = 0x100;

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setMaterialIcon(0, MaterialDrawableBuilder.IconValue.HOME);
        setMaterialIcon(1, MaterialDrawableBuilder.IconValue.VIEW_DASHBOARD);
        setMaterialIcon(2, MaterialDrawableBuilder.IconValue.LAYERS);
        setMaterialIcon(3, MaterialDrawableBuilder.IconValue.RADIATOR);

        binding.navView.setOnItemSelectedListener(navListener);

        /*
        MaterialMenuInflater
                .with(this) // Provide the activity context
                .setDefaultColor(Color.BLACK)
                .inflate(R.menu.bottom_nav_menu, bottomNav.getMenu());
         */

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int lastNavId = sharedPref.getInt(getString(R.string.main_last_nav), -1);
        if (lastNavId != -1) {
            binding.navView.setSelectedItemId(lastNavId);
        }

        sipAccount = new OshAccount(new OshAccount.CallbackReceiver() {
            @Override
            public void registrationSuccess() {
                sipConnectedState.changeValue(true);
            }

            @Override
            public void registrationFailure(int registrationStateCode) {
                Toast.makeText(getApplicationContext(), "Registration failed " + registrationStateCode, Toast.LENGTH_LONG).show();
                sipConnectedState.changeValue(false);
            }

            @Override
            public void onIncomingCall(Context receiverContext, String accountID, int callID, String displayName, String remoteUri, boolean isVideo) {
                SipCallActivity.startActivityIn(receiverContext, accountID, callID, displayName, remoteUri, isVideo, ((OshApplication) getApplication()).getApplicationConfig().getSip().getRingVolume());
            }

            @Override
            public void onOutgoingCall(Context receiverContext, String accountID, int callID, String number, boolean isVideo, boolean isVideoConference) {
                SipCallActivity.startActivityOut(receiverContext, accountID, callID, number, isVideo, isVideoConference);
            }
        });

        sipAccount.onCreate(this);
        sipAccount.login(
                ((OshApplication) getApplication()).getApplicationConfig().getSip().getHost(),
                ((OshApplication) getApplication()).getApplicationConfig().getSip().getPort(),
                ((OshApplication) getApplication()).getApplicationConfig().getSip().getRealm(),
                ((OshApplication) getApplication()).getApplicationConfig().getSip().getUsername(),
                ((OshApplication) getApplication()).getApplicationConfig().getSip().getPassword()
                );


        requestPermissions();
    }

    private void setMaterialIcon(int i, MaterialDrawableBuilder.IconValue icon) {
        MenuItem item = binding.navView.getMenu().getItem(i);
        item.setIcon(MaterialDrawableBuilder.with(this).setIcon(icon).setColor(Color.BLACK).build());
    }

    private void requestPermissions() {
        String[] permissions = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.NFC,
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
        };
        if (!checkPermissionAllGranted(permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSIONS_APP);
        }
    }

    protected boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_APP) {
            boolean ok = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    ok = false;
                }
            }
            if (ok) {
                Toast.makeText(MainActivity.this, "Permission application is successful！", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                selectedFragment = HomeFragment.newInstance();
                break;
            case R.id.navigation_dashboard:
                selectedFragment = DashboardFragment.newInstance(serviceContext);
                break;
            case R.id.navigation_area:
                selectedFragment = AreaFragment.newInstance(serviceContext);
                break;
            case R.id.navigation_wbb12:
                selectedFragment = WBB12Fragment.newInstance(wbb12Service);
                break;
        }
        // It will help to replace the
        // one fragment to other.
        if (selectedFragment != null) {
            SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
            sharedPref.edit().putInt(getString(R.string.main_last_nav), item.getItemId()).apply();

            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_fragment_activity_main, selectedFragment).commit();
        }
        return true;
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu, menu);

        MaterialMenuInflater.with(this)
                .setDefaultColor(Color.WHITE)
                .inflate(R.menu.main_menu, menu);

        Drawable wifiOnIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.WIFI).setColor(Color.WHITE).build();
        Drawable wifiOffIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.WIFI_OFF).setColor(Color.WHITE).build();

        Drawable dbOnIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.DATABASE).setColor(Color.WHITE).build();
        Drawable dbOffIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.DATABASE_MINUS).setColor(Color.WHITE).build();

        Drawable sipOnIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.PHONE).setColor(Color.WHITE).build();
        Drawable sipOffIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.PHONE_MINUS).setColor(Color.WHITE).build();

        MenuItem mqttConnectedStateIcon = menu.findItem(R.id.mqtt_connected_state_icon);
        serviceContext.getCommunicationService().connectedState().addItemChangeListener(connectedState -> {
            runOnUiThread(() -> {
                mqttConnectedStateIcon.setIcon(connectedState ? wifiOnIcon : wifiOffIcon);
            });
        }, true);

        MenuItem dbConnectedStateIcon = menu.findItem(R.id.db_connected_state_icon);
        serviceContext.getDatamodelService().loadedState().addItemChangeListener(loadedState -> {
            runOnUiThread(() -> {
                dbConnectedStateIcon.setIcon(loadedState ? dbOnIcon : dbOffIcon);
            });
        }, true);

        MenuItem sipConnectedStateIcon = menu.findItem(R.id.sip_connected_state_icon);
        sipConnectedState.addItemChangeListener(connectedState -> {
            runOnUiThread(() -> {
                sipConnectedStateIcon.setIcon(connectedState ? sipOnIcon : sipOffIcon);
            });
        }, true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sipAccount.onDestroy();
    }
}

