package com.osh.config;

import com.osh.camera.config.CameraConfig;
import com.osh.communication.mqtt.config.MqttConfig;
import com.osh.database.config.DatabaseConfig;
import com.osh.datamodel.config.DatamodelConfig;
import com.osh.fronius.config.FroniusConfig;
import com.osh.grafana.config.GrafanaConfig;
import com.osh.sip.SipConfig;
import com.osh.user.config.UserConfig;

import org.apache.commons.lang3.StringUtils;

public class ApplicationConfig implements IApplicationConfig {

	private final UserConfig userConfig = new UserConfig();
	private final MqttConfig mqtt = new MqttConfig();
	
	private final DatamodelConfig datamodel = new DatamodelConfig();

	private final DatabaseConfig database = new DatabaseConfig();


	private final SipConfig sip = new SipConfig();

	private final CameraConfig camera = new CameraConfig();

	private final GrafanaConfig grafana = new GrafanaConfig();

	private final FroniusConfig fronius = new FroniusConfig();

	@Override
	public UserConfig getUser() {
		return userConfig;
	}

	@Override
	public MqttConfig getMqtt() {
		return mqtt;
	}

	@Override
	public DatamodelConfig getDatamodel() {
		return datamodel;
	}

	@Override
	public DatabaseConfig getDatabase() {
		return database;
	}

	@Override
	public SipConfig getSip() {
		return sip;
	}

	@Override
	public CameraConfig getCamera() {
		return camera;
	}

	@Override
	public GrafanaConfig getGrafana() {
		return grafana;
	}

	@Override
	public FroniusConfig getFronius() {
		return fronius;
	}

	@Override
	public boolean isValid() {
		return !StringUtils.isEmpty(getMqtt().getServerHost()) &&
				!StringUtils.isEmpty(getDatabase().getHost()) &&
				!StringUtils.isEmpty(getSip().getUsername())
				;
	}
}
