package com.osh.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraManager;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.osh.R;
import com.osh.databinding.ActivityMainBinding;
import com.osh.service.IServiceContext;
import com.osh.sip.MyAccount;
import com.osh.sip.MyApp;
import com.osh.sip.MyAppObserver;
import com.osh.sip.MyBuddy;
import com.osh.sip.MyCall;
import com.osh.sip.OshAccount;
import com.osh.ui.area.AreaFragment;
import com.osh.ui.dashboard.DashboardFragment;
import com.osh.ui.home.HomeFragment;
import com.osh.ui.wbb12.WBB12Fragment;
import com.osh.utils.ObservableBoolean;
import com.osh.wbb12.service.IWBB12Service;

import net.gotev.sipservice.SipCall;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialMenuInflater;

import org.pjsip.PjCameraInfo2;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.OnCallMediaEventParam;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_status_code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.Lazy;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements Handler.Callback, MyAppObserver {

    public static MyApp app = null;
    public static MyCall currentCall = null;
    public static MyAccount account = null;
    public static AccountConfig accCfg = null;
    private String lastRegStatus = "";

    ArrayList<Map<String, String>> buddyList;

    private final Handler handler = new Handler(this);

    @Override
    public boolean handleMessage(@NonNull Message m) {
        if (m.what == 0) {
            /*
            app.deinit();
            finish();
            Runtime.getRuntime().gc();
            android.os.Process.killProcess(android.os.Process.myPid());
             */
        } else if (m.what == MSG_TYPE.CALL_STATE) {

            CallInfo ci = (CallInfo) m.obj;

            if (currentCall == null || ci == null || ci.getId() != currentCall.getId()) {
                System.out.println("Call state event received, but call info is invalid");
                return true;
            }

            Intent intent = new Intent(this, SipCallActivity.class);
            intent.putExtra("msgType", MSG_TYPE.CALL_STATE);
            intent.putExtra("state", ci.getState());
            startActivity(intent);

            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
            {
                currentCall.delete();
                currentCall = null;
            }

        } else if (m.what == MSG_TYPE.CALL_MEDIA_STATE) {

            Intent intent = new Intent(this, SipCallActivity.class);
            intent.putExtra("msgType", MSG_TYPE.CALL_MEDIA_STATE);
            startActivity(intent);

        } else if (m.what == MSG_TYPE.BUDDY_STATE) {

            MyBuddy buddy = (MyBuddy) m.obj;
            int idx = account.buddyList.indexOf(buddy);

            /* Update buddy status text, if buddy is valid and
             * the buddy lists in account and UI are sync-ed.
             */
            if (idx >= 0 && account.buddyList.size() == buddyList.size())
            {
                //buddyList.get(idx).put("status", buddy.getStatusText());
                //buddyListAdapter.notifyDataSetChanged();
                // TODO: selection color/mark is gone after this,
                //       dont know how to return it back.
                //buddyListView.setSelection(buddyListSelectedIdx);
                //buddyListView.performItemClick(buddyListView,
                //                                   buddyListSelectedIdx,
                //                                   buddyListView.
                //                  getItemIdAtPosition(buddyListSelectedIdx));

                /* Return back Call activity */
                notifyCallState(currentCall);
            }

        } else if (m.what == MSG_TYPE.REG_STATE) {

            String msg_str = (String) m.obj;
            lastRegStatus = msg_str;

        } else if (m.what == MSG_TYPE.INCOMING_CALL) {

            /* Incoming call */
            final MyCall call = (MyCall) m.obj;
            CallOpParam prm = new CallOpParam(true);

            /* Only one call at anytime */
            if (currentCall != null) {
                prm.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);
                try {
                    call.hangup(prm);
                } catch (Exception e) {}

                call.delete();
                return true;
            }

            /* Answer with ringing */
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
            try {
                call.answer(prm);
            } catch (Exception e) {}

            currentCall = call;
            showCallActivity();

        } else if (m.what == MSG_TYPE.CHANGE_NETWORK) {
            app.handleNetworkChange();
        } else {

            /* Message not handled */
            return false;

        }

        return true;
    }

    public class MSG_TYPE
    {
        public final static int INCOMING_CALL = 1;
        public final static int CALL_STATE = 2;
        public final static int REG_STATE = 3;
        public final static int BUDDY_STATE = 4;
        public final static int CALL_MEDIA_STATE = 5;
        public final static int CHANGE_NETWORK = 6;
        public final static int CALL_MEDIA_EVENT = 7;
    }


    @Inject
    Lazy<IWBB12Service> wbb12Service;

    @Inject
    IServiceContext serviceContext;

    private OshAccount sipAccount;
    private ObservableBoolean sipConnectedState = new ObservableBoolean(false);
    private static final int REQUEST_PERMISSIONS_APP = 0x100;

    private ActivityMainBinding binding;

    public IServiceContext getServiceContext() {
        return serviceContext;
    }

    public IWBB12Service getWBB12Service() {
        return wbb12Service.get();
    }

    public static void invokeActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = binding.bottomNav;
        NavigationUI.setupWithNavController(bottomNav, navController);

        Toolbar myToolbar = binding.myToolbar;
        setSupportActionBar(myToolbar);

        setMaterialIcon(0, MaterialDrawableBuilder.IconValue.HOME);
        setMaterialIcon(1, MaterialDrawableBuilder.IconValue.VIEW_DASHBOARD);
        setMaterialIcon(2, MaterialDrawableBuilder.IconValue.LAYERS);
        setMaterialIcon(3, MaterialDrawableBuilder.IconValue.RADIATOR);
        setMaterialIcon(4, MaterialDrawableBuilder.IconValue.MATRIX);

        //binding.navView.setOnItemSelectedListener(navListener);

        /*
        MaterialMenuInflater
                .with(this) // Provide the activity context
                .setDefaultColor(Color.BLACK)
                .inflate(R.menu.bottom_nav_menu, bottomNav.getMenu());
         */

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        int lastNavId = sharedPref.getInt(getString(R.string.main_last_nav), -1);
        if (lastNavId != -1) {
            binding.bottomNav.setSelectedItemId(lastNavId);
        }

        CameraManager cm = (CameraManager)getSystemService(Context.CAMERA_SERVICE);
        PjCameraInfo2.SetCameraManager(cm);

        if (app == null) {
            app = new MyApp();
            // Wait for GDB to init, for native debugging only
            if (false &&
                    (getApplicationInfo().flags &
                            ApplicationInfo.FLAG_DEBUGGABLE) != 0)
            {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {}
            }

            app.init(this, getFilesDir().getAbsolutePath());
        }

        if (app.accList.size() == 0) {
            accCfg = new AccountConfig();
            accCfg.setIdUri("sip:" + ((OshApplication) getApplication()).getApplicationConfig().getSip().getUsername() + "@" + ((OshApplication) getApplication()).getApplicationConfig().getSip().getHost());
            accCfg.getRegConfig().setRegistrarUri("sip:" + ((OshApplication) getApplication()).getApplicationConfig().getSip().getHost());
            accCfg.getSipConfig().getAuthCreds().clear();
            accCfg.getSipConfig().getAuthCreds().add(new AuthCredInfo("Digest", ((OshApplication) getApplication()).getApplicationConfig().getSip().getRealm(), ((OshApplication) getApplication()).getApplicationConfig().getSip().getUsername(), 0, ((OshApplication) getApplication()).getApplicationConfig().getSip().getPassword()));
            accCfg.getNatConfig().setIceEnabled(true);
            accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
            accCfg.getVideoConfig().setAutoShowIncoming(true);
            account = app.addAcc(accCfg);
        } else {
            account = app.accList.get(0);
            accCfg = account.cfg;
        }

        buddyList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < account.buddyList.size(); i++) {
            buddyList.add(putData(account.buddyList.get(i).cfg.getUri(),
                    account.buddyList.get(i).getStatusText()));
        }


        /*
        sipAccount = new OshAccount(new OshAccount.CallbackReceiver() {
            @Override
            public void registrationSuccess() {
                sipConnectedState.changeValue(true);
            }

            @Override
            public void registrationFailure(int registrationStateCode) {
                //Toast.makeText(getApplicationContext(), "Registration failed " + registrationStateCode, Toast.LENGTH_LONG).show();
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
         */


        requestPermissions();
    }

    private HashMap<String, String> putData(String uri, String status)
    {
        HashMap<String, String> item = new HashMap<String, String>();
        item.put("uri", uri);
        item.put("status", status);
        return item;
    }

    private void showCallActivity()
    {
        Intent intent = new Intent(this, SipCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void setMaterialIcon(int i, MaterialDrawableBuilder.IconValue icon) {
        MenuItem item = binding.bottomNav.getMenu().getItem(i);
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

    /*
    private final NavigationBarView.OnItemSelectedListener navListener = item -> {
        // By using switch we can easily get
        // the selected fragment
        // by using there id.
        Fragment selectedFragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_home:
                selectedFragment = HomeFragment.newInstance(serviceContext);
                break;
            case R.id.navigation_dashboard:
                selectedFragment = DashboardFragment.newInstance(serviceContext);
                break;
            case R.id.navigation_area:
                selectedFragment = AreaFragment.newInstance(serviceContext);
                break;
            case R.id.navigation_wbb12:
                selectedFragment = WBB12Fragment.newInstance(wbb12Service.get());
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
    };*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.main_menu, menu);

        MaterialMenuInflater.with(this)
                .inflate(R.menu.main_menu, menu);

        Drawable wifiOnIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.WIFI).build();
        Drawable wifiOffIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.WIFI_OFF).build();

        Drawable dbOnIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.DATABASE).build();
        Drawable dbOffIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.DATABASE_MINUS).build();

        Drawable sipOnIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.PHONE).build();
        Drawable sipOffIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.PHONE_MINUS).build();

        MenuItem mqttConnectedStateIcon = menu.findItem(R.id.mqtt_connected_state_icon);
        serviceContext.getCommunicationService().connectedState().addItemChangeListener(connectedState -> {
            runOnUiThread(() -> {
                mqttConnectedStateIcon.setIcon(connectedState ? wifiOnIcon : wifiOffIcon);
            });
        }, true, () -> {return mqttConnectedStateIcon!=null;});

        MenuItem dbConnectedStateIcon = menu.findItem(R.id.db_connected_state_icon);
        serviceContext.getDatamodelService().loadedState().addItemChangeListener(loadedState -> {
            runOnUiThread(() -> {
                dbConnectedStateIcon.setIcon(loadedState ? dbOnIcon : dbOffIcon);
            });
        }, true, () -> {return dbConnectedStateIcon!=null;});

        dbConnectedStateIcon.setOnMenuItemClickListener(item -> {
            final Context context = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    long version = serviceContext.getDatabaseService().getVersion();
                    boolean canUpdate = serviceContext.getDatabaseService().canUpdate();

                    runOnUiThread(()->{
                        if (canUpdate) {
                            new AlertDialog.Builder(context)
                                    .setTitle("Update database")
                                    .setMessage("Update local database ?")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                        serviceContext.getDatabaseService().resetDatabase();
                                        finishAndRemoveTask();
                                        System.exit(0);
                                    })
                                    .setNegativeButton(android.R.string.no, null)
                                    .show();
                        } else {
                            Toast.makeText(context, "Database Version: " + version, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).start();
            return true;
        });

        MenuItem sipConnectedStateIcon = menu.findItem(R.id.sip_connected_state_icon);
        sipConnectedState.addItemChangeListener(connectedState -> {
            runOnUiThread(() -> {
                sipConnectedStateIcon.setIcon(connectedState ? sipOnIcon : sipOffIcon);
            });
        }, true, () -> {return sipConnectedStateIcon!=null;});

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        if (item.getItemId() == R.id.menu_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.exitapp) {
            new AlertDialog.Builder(this)
                    .setTitle("Exit app")
                    .setMessage("Exit app ?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        finishAndRemoveTask();
                        System.exit(0);
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .show();
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

    @Override
    public void notifyRegState(int code, String reason, long expiration) {
        boolean success = false;
        String msg_str = "";
        if (expiration == 0)
            msg_str += "Unregistration";
        else
            msg_str += "Registration";

        success = (code / 100) == 2;

        sipConnectedState.changeValue(success);

        if (code/100 == 2)
            msg_str += " successful";
        else
            msg_str += " failed: " + reason;

        Message m = Message.obtain(handler, MSG_TYPE.REG_STATE, msg_str);
        m.sendToTarget();
    }

    @Override
    public void notifyIncomingCall(MyCall call) {
        Message m = Message.obtain(handler, MSG_TYPE.INCOMING_CALL, call);
        m.sendToTarget();
    }

    @Override
    public void notifyCallState(MyCall call) {
        if (currentCall == null || call.getId() != currentCall.getId())
            return;

        CallInfo ci = null;
        try {
            ci = call.getInfo();
        } catch (Exception e) {}

        if (ci == null)
            return;

        Message m = Message.obtain(handler, MSG_TYPE.CALL_STATE, ci);
        m.sendToTarget();
    }

    @Override
    public void notifyCallMediaState(MyCall call) {
        Message m = Message.obtain(handler, MSG_TYPE.CALL_MEDIA_STATE, null);
        m.sendToTarget();
    }

    @Override
    public void notifyBuddyState(MyBuddy buddy) {
        Message m = Message.obtain(handler, MSG_TYPE.BUDDY_STATE, buddy);
        m.sendToTarget();
    }

    @Override
    public void notifyChangeNetwork() {
        Message m = Message.obtain(handler, MSG_TYPE.CHANGE_NETWORK, null);
        m.sendToTarget();
    }

    @Override
    public void notifyCallMediaEvent(MyCall call, OnCallMediaEventParam prm) {
        Intent intent = new Intent(this, SipCallActivity.class);
        intent.putExtra("msgType", MSG_TYPE.CALL_MEDIA_EVENT);
        intent.putExtra("evType", prm.getEv().getType());
        intent.putExtra("medIdx", prm.getMedIdx());
        startActivity(intent);

        /*
        if (CallActivity.handler_ != null) {
            Message m = Message.obtain( CallActivity.handler_,
                    MSG_TYPE.CALL_MEDIA_EVENT, prm);
            m.sendToTarget();
        }
         */
    }
}

