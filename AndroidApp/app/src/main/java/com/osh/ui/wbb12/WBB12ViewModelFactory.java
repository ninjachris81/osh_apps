package com.osh.ui.wbb12;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.service.IServiceContext;
import com.osh.service.IValueService;
import com.osh.wbb12.service.IWBB12Service;

public class WBB12ViewModelFactory implements ViewModelProvider.Factory {

    private final IWBB12Service wbb12Manager;
    private final IServiceContext serviceContext;


    public WBB12ViewModelFactory(IWBB12Service wbb12Manager, IServiceContext serviceContext) {
        this.wbb12Manager = wbb12Manager;
        this.serviceContext = serviceContext;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new WBB12ViewModel(wbb12Manager, serviceContext);
    }
}
