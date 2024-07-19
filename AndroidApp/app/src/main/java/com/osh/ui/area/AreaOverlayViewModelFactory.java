package com.osh.ui.area;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.service.IServiceContext;

public class AreaOverlayViewModelFactory implements ViewModelProvider.Factory {

    private static AreaOverlayViewModelFactory instance;

    public static AreaOverlayViewModelFactory getInstance() {
        if (instance != null) return instance;
        return new AreaOverlayViewModelFactory();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new AreaOverlayViewModel();
    }
}
