package com.osh.ui.area;

import androidx.lifecycle.ViewModel;

import com.osh.core.service.IServiceContext;

public class AreaViewModel extends ViewModel {

    private final IServiceContext serviceContext;
    private final String areaId;

    public String getAreaId() {
        return areaId;
    }

    public AreaViewModel(IServiceContext serviceContext, String areaId) {
        this.serviceContext = serviceContext;
        this.areaId = areaId;
    }

}