package com.osh;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;

import android.app.Application;
import android.content.SharedPreferences;

import com.osh.config.IApplicationConfig;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class OshApplication extends Application {

    @Inject
    IApplicationConfig applicationConfig;

    @Override
    public void onCreate() {
        super.onCreate();

        loadApplicationConfig();
    }

    private void loadApplicationConfig() {
        SharedPreferences prefs = getDefaultSharedPreferences(this);

        applicationConfig.getUser().setUserId(prefs.getString(getString(R.string.user_id_key), "DefaultUser"));

        String clientId = prefs.getString(getString(R.string.mqtt_client_id_key), "");
        if (clientId.isEmpty()) {
            // set new random client ID
            clientId = "AndroidClient_" + System.currentTimeMillis();
            prefs.edit().putString(getString(R.string.mqtt_client_id_key), clientId).commit();
        }
        applicationConfig.getMqtt().setClientId(clientId);

        applicationConfig.getMqtt().setServerHost(prefs.getString(getString(R.string.mqtt_host_key), "127.0.0.1"));
        applicationConfig.getMqtt().setServerPort(Integer.parseInt(prefs.getString(getString(R.string.mqtt_port_key), "1883")));
        applicationConfig.getDatabase().setHost(prefs.getString(getString(R.string.db_host), "192.168.177.1"));
        applicationConfig.getDatabase().setPort(Integer.parseInt(prefs.getString(getString(R.string.db_port), "5432")));
        applicationConfig.getDatabase().setName(prefs.getString(getString(R.string.db_name), "osh"));
        applicationConfig.getDatabase().setUsername(prefs.getString(getString(R.string.db_username), "osh"));
        applicationConfig.getDatabase().setPassword(prefs.getString(getString(R.string.db_password), "osh"));

        applicationConfig.getSip().setHost(prefs.getString(getString(R.string.sip_host_key), "localhost"));
        applicationConfig.getSip().setPort(Integer.parseInt(prefs.getString(getString(R.string.sip_port_key), "5060")));
        applicationConfig.getSip().setRealm(prefs.getString(getString(R.string.sip_realm_key), "*"));
        applicationConfig.getSip().setUsername(prefs.getString(getString(R.string.sip_username_key), ""));
        applicationConfig.getSip().setPassword(prefs.getString(getString(R.string.sip_password_key), ""));
        applicationConfig.getSip().setRingVolume(Integer.parseInt(prefs.getString(getString(R.string.sip_ring_volume_key), "80")));

        applicationConfig.getCamera().addCameraSource(
                prefs.getString(getString(R.string.front_door_camera_id_key), "frontDoor.door"),
                prefs.getString(getString(R.string.front_door_stream_uri_key), "rtsp://localhost")
        );
        applicationConfig.getCamera().addCameraFTPSource(
                prefs.getString(getString(R.string.front_door_camera_ftp_id_key), "frontDoor.door"),
                prefs.getString(getString(R.string.front_door_camera_ftp_host_key), "localhost"),
                prefs.getString(getString(R.string.front_door_camera_ftp_username_key), ""),
                prefs.getString(getString(R.string.front_door_camera_ftp_password_key), ""),
                prefs.getString(getString(R.string.front_door_camera_ftp_remote_dir_key), "/home/pi")
            );

        applicationConfig.getGrafana().setWbb12Url(prefs.getString(getString(R.string.grafana_wbb12_url_key), "http://localhost:3000/grafana"));
    }

    public IApplicationConfig getApplicationConfig() {
        return applicationConfig;
    }
}
