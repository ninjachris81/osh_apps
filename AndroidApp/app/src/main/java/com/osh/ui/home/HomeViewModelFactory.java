package com.osh.ui.home;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.service.IServiceContext;
import com.osh.ui.dashboard.DashboardViewModel;

public class HomeViewModelFactory  implements ViewModelProvider.Factory {

    private final Context context;
    private final IServiceContext serviceContext;
    private final HomeViewModel.IBatteryDataChangeListener batteryDataChangeListener;

    public HomeViewModelFactory(Context context, IServiceContext serviceContext, HomeViewModel.IBatteryDataChangeListener batteryDataChangeListener) {
        this.context = context;
        this.serviceContext = serviceContext;
        this.batteryDataChangeListener = batteryDataChangeListener;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new HomeViewModel(context, serviceContext, batteryDataChangeListener);
    }
}
