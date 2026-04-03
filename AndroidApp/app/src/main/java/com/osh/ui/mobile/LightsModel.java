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

public class LightsModel extends ViewModel {

    private final IServiceContext serviceContext;

    public ObservableField<String> activeLightsInfo = new ObservableField<>("");
    public ObservableField<Integer> activeLightsCount = new ObservableField<>(0);
    public List<ToggleActor> activeLights = new ArrayList<>();

    public LightsModel(IServiceContext serviceContext) {
        this.serviceContext = serviceContext;

        setupActiveLights();
    }

    private void setupActiveLights() {
        List<ToggleActor> lightActors = serviceContext.getActorService().getActors(ToggleActor.class, ValueType.VT_VIRTUAL_ACTOR);
        for (ToggleActor lightActor : lightActors) {
            lightActor.addItemChangeListener(item -> {
                recalculateActiveLights();
            }, true, () -> {return this != null;});
        }
    }

    private void recalculateActiveLights() {
        activeLights.clear();

        List<ToggleActor> lightActors = serviceContext.getActorService().getActors(ToggleActor.class, ValueType.VT_VIRTUAL_ACTOR);
        for (ToggleActor lightActor : lightActors) {
            if (lightActor.getValue(false)) {
                activeLights.add(lightActor);
            }
        }

        activeLightsCount.set(activeLights.size());
        activeLightsInfo.set(activeLights.isEmpty() ? "All lights off" : activeLights.size() + " light(s) on");
    }

}
