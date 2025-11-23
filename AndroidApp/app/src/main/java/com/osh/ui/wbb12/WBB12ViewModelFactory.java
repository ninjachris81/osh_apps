package com.osh.ui.wbb12;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.osh.core.service.IServiceContext;
import com.osh.wbb12.service.IWBB12Service;

public class WBB12ViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;
    private final IWBB12Service wbb12Manager;
    private final IServiceContext serviceContext;


    public WBB12ViewModelFactory(Context context, IWBB12Service wbb12Manager, IServiceContext serviceContext) {
        this.context = context;
        this.wbb12Manager = wbb12Manager;
        this.serviceContext = serviceContext;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new WBB12ViewModel(context, wbb12Manager, serviceContext);
    }
}
