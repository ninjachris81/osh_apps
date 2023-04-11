package com.osh.config;

import com.osh.camera.config.CameraConfig;
import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.database.config.DatabaseConfig;
import com.osh.datamodel.config.DatamodelConfig;
import com.osh.grafana.config.GrafanaConfig;
import com.osh.sip.SipConfig;
import com.osh.user.config.UserConfig;

public interface IApplicationConfig {

    UserConfig getUser();

    MqttConfig getMqtt();

    DatamodelConfig getDatamodel();

    DatabaseConfig getDatabase();

    SipConfig getSip();

    CameraConfig getCamera();

    GrafanaConfig getGrafana();

}
