package com.osh.service;

import com.j256.ormlite.support.ConnectionSource;
import com.osh.datamodel.DatamodelBase;
import com.osh.utils.IObservableBoolean;
import com.osh.utils.IObservableListenerHolder;

import java.sql.SQLException;

public interface IDatabaseService {

    long getVersion();

    boolean canUpdate();

    boolean isEmpty();

    DatamodelBase loadDatamodel();

    void resetDatabase();
}
