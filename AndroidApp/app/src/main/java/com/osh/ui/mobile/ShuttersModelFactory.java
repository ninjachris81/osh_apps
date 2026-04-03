package com.osh.ui.mobile;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.core.service.IServiceContext;

public class ShuttersModelFactory implements ViewModelProvider.Factory {

    private final IServiceContext serviceContext;

    public ShuttersModelFactory(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ShuttersModel(serviceContext);
    }
}
