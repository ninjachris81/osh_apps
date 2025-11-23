package com.osh.core.service;

import com.osh.core.datamodel.DatamodelBase;
import com.osh.core.utils.IObservableBoolean;

public interface IDatamodelService {

	DatamodelBase getDatamodel();
	
	void save();

	IObservableBoolean loadedState();


}
