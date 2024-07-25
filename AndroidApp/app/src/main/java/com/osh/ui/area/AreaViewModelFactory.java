package com.osh.ui.area;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.osh.service.IServiceContext;

public class AreaViewModelFactory implements ViewModelProvider.Factory {

    private final IServiceContext serviceContext;

    private static AreaViewModelFactory instance;

    public static AreaViewModelFactory getInstance(IServiceContext serviceContext) {
        if (instance != null) return instance;
        instance = new AreaViewModelFactory(serviceContext);
        return instance;
    }

    private AreaViewModelFactory(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
        String areaId = extras.get(ViewModelProvider.NewInstanceFactory.VIEW_MODEL_KEY);
        return (T) new AreaViewModel(serviceContext, areaId);
    }
}
