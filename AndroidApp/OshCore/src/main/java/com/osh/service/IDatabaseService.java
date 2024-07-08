package com.osh.service;

import com.j256.ormlite.support.ConnectionSource;
import com.osh.datamodel.DatamodelBase;
import com.osh.utils.IObservableBoolean;
import com.osh.utils.IObservableListenerHolder;

public interface IDatabaseService {

    boolean isEmpty();

    DatamodelBase loadDatamodel();

}
