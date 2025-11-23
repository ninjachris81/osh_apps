package com.osh.core.datamodel;

import com.osh.core.service.IDatamodelService;

public class DBDatamodelLoader extends DatamodelLoaderBase {
	
	public static final String LOADER_TYPE_NAME = "db";
	
	private final IDatamodelService datamodelService;
	private final String datamodelName;

	public DBDatamodelLoader(IDatamodelService datamodelService, String datamodelName) {
		super("DBDatamodelLoader");
		this.datamodelService = datamodelService;
		this.datamodelName = datamodelName;
	}

	@Override
	public DatamodelBase load() {
		//return datamodelService.findById(datamodelName, EnumSet.noneOf(LOAD_MODIFIERS.class));
		return null;
	}

	@Override
	public void save(DatamodelBase datamodel) {
		//datamodelService.save((DBDatamodel) datamodel);
	}

	@Override
	public String typeName() {
		return LOADER_TYPE_NAME;
	}

}
