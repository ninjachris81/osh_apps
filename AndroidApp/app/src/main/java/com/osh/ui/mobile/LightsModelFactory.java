package com.osh.ui.mobile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.core.service.IServiceContext;

public class LightsModelFactory implements ViewModelProvider.Factory {

    private final IServiceContext serviceContext;

    public LightsModelFactory(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new LightsModel(serviceContext);
    }
}
