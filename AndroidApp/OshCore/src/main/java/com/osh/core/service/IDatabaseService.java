package com.osh.core.service;

import com.osh.core.datamodel.DatamodelBase;

public interface IDatabaseService {

    long getVersion();

    boolean canUpdate();

    boolean isEmpty();

    DatamodelBase loadDatamodel();

    void resetDatabase();
}
