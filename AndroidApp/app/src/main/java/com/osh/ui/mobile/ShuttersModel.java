package com.osh.ui.mobile;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.osh.core.actor.ShutterActor;
import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.ValueType;

import java.util.ArrayList;
import java.util.List;

public class ShuttersModel extends ViewModel {

    private final IServiceContext serviceContext;

    public final List<ObservableBoolean> shutterAutoModes = new ArrayList<>();

    public ShuttersModel(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;

        setupActiveShutters();
    }

    private void setupActiveShutters() {
        List<ShutterActor> shutterActors = serviceContext.getActorService().getActors(ShutterActor.class, ValueType.VT_RELAY_SHUTTER);

        for (ShutterActor shutterActor : shutterActors) {
            shutterActor.addItemChangeListener(item -> {
            }, true, () -> {return this != null;});
        }
    }
}
