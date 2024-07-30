package com.osh.grafana.config;

public class GrafanaConfig {

    String wbb12Url;
    String energyUrl;
    String waterUrl;

    public String getWbb12Url() {
        return wbb12Url;
    }

    public void setWbb12Url(String wbb12Url) {
        this.wbb12Url = wbb12Url;
    }

    public String getEnergyUrl() {
        return energyUrl;
    }

    public void setEnergyUrl(String energyUrl) {
        this.energyUrl = energyUrl;
    }

    public String getWaterUrl() {
        return waterUrl;
    }

    public void setWaterUrl(String waterUrl) {
        this.waterUrl = waterUrl;
    }
}
