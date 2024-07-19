package com.osh.ui.area;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.osh.service.IServiceContext;

public class RoomViewModelFactory  implements ViewModelProvider.Factory {

    private final IServiceContext serviceContext;

    public RoomViewModelFactory(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass, @NonNull CreationExtras extras) {
        String roomKey = extras.get(ViewModelProvider.NewInstanceFactory.VIEW_MODEL_KEY);
        return (T) new RoomViewModel(serviceContext, roomKey.toString());
    }
}
