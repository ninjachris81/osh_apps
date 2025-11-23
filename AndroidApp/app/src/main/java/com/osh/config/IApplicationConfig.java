package com.osh.config;

import com.osh.core.camera.config.CameraConfig;
import com.osh.core.communication.mqtt.config.MqttConfig;
import com.osh.core.database.config.DatabaseConfig;
import com.osh.core.datamodel.config.DatamodelConfig;
import com.osh.fronius.config.FroniusConfig;
import com.osh.grafana.config.GrafanaConfig;
import com.osh.sip.SipConfig;
import com.osh.core.user.config.UserConfig;

public interface IApplicationConfig {

    UserConfig getUser();

    MqttConfig getMqtt();

    DatamodelConfig getDatamodel();

    DatabaseConfig getDatabase();

    SipConfig getSip();

    CameraConfig getCamera();

    GrafanaConfig getGrafana();

    FroniusConfig getFronius();

    boolean isValid();
}
