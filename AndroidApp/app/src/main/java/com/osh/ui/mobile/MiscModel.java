package com.osh.ui.mobile;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;

public class MiscModel extends ViewModel {

    private final IServiceContext serviceContext;

    public ObservableField<Boolean> airConditionerState = new ObservableField<>(false);

    public MiscModel(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;

        airConditionerState.set(true);
        setupAirConditioner();
    }

    private void setupAirConditioner() {
        ((ToggleActor) serviceContext.getActorService().getActor("airconditioner.dg")).addItemChangeListener(item -> {
            airConditionerState.set(item.getValue(false));
        }, true, () -> {return this != null;});
    }
}
