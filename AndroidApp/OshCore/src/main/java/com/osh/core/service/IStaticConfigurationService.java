package com.osh.core.service;

public interface IStaticConfigurationService {

    static final String CONFIG_BATTERY_CAPACITY = "battery_capacity";

    String getValue(String id, String defaultValue);

    int getValue(String id, int defaultValue);

    double getValue(String id, double defaultValue);
}
