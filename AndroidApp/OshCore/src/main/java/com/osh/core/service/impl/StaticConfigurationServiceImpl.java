package com.osh.core.service.impl;

import com.osh.core.configuration.StaticConfigurationItem;
import com.osh.core.service.IDatamodelService;
import com.osh.core.service.IStaticConfigurationService;

import java.util.Objects;

public class StaticConfigurationServiceImpl implements IStaticConfigurationService {

    private final IDatamodelService datamodelService;

    public StaticConfigurationServiceImpl(IDatamodelService datamodelService) {
        this.datamodelService = datamodelService;
    }

    @Override
    public String getValue(String id, String defaultValue) {
        StaticConfigurationItem item = datamodelService.getDatamodel().getStaticConfiguration().get(id);
        if (item == null) return defaultValue;
        return item.value;
    }

    @Override
    public int getValue(String id, int defaultValue) {
        return Integer.parseInt(getValue(id, String.valueOf(defaultValue)));
    }

    @Override
    public double getValue(String id, double defaultValue) {
        return Double.parseDouble(getValue(id, String.valueOf(defaultValue)));
    }
}
