package com.osh.ui.home;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.service.IServiceContext;
import com.osh.ui.dashboard.DashboardViewModel;
import com.osh.wbb12.service.IWBB12Service;

public class HomeViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;
    private final IServiceContext serviceContext;
    private final IWBB12Service wbb12Service;
    private final HomeViewModel.IBatteryDataChangeListener batteryDataChangeListener;

    public HomeViewModelFactory(Context context, IServiceContext serviceContext, IWBB12Service wbb12Service, HomeViewModel.IBatteryDataChangeListener batteryDataChangeListener) {
        this.context = context;
        this.serviceContext = serviceContext;
        this.wbb12Service = wbb12Service;
        this.batteryDataChangeListener = batteryDataChangeListener;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new HomeViewModel(context, serviceContext, wbb12Service, batteryDataChangeListener);
    }
}
