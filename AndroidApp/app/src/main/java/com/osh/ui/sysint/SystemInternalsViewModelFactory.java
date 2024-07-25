package com.osh.ui.sysint;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.service.IDeviceDiscoveryService;

public class SystemInternalsViewModelFactory implements ViewModelProvider.Factory {

    private final IDeviceDiscoveryService deviceDiscoveryService;

    public SystemInternalsViewModelFactory(IDeviceDiscoveryService deviceDiscoveryService) {
        this.deviceDiscoveryService = deviceDiscoveryService;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SystemInternalsViewModel(deviceDiscoveryService);
    }
}
