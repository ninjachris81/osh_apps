package com.osh.ui.home;

import android.content.Context;
import android.util.Log;

import com.osh.core.actor.ActorBase;
import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;
import com.osh.wbb12.service.IWBB12Service;

import java.util.ArrayList;
import java.util.List;

public class HomeViewMobileModel extends HomeViewModelBase {

    public interface ActiveLightsChangedListener {
        void onListChanged();
    }

    public List<ToggleActor> activeLights = new ArrayList<>();
    protected ActiveLightsChangedListener activeLightsChangedListener;

    public HomeViewMobileModel(Context context, IServiceContext serviceContext, IWBB12Service wbb12Service, IBatteryDataChangeListener batteryDataChangeListener) {
        super(context, serviceContext, wbb12Service, batteryDataChangeListener);

        setupActiveLights();
    }

    public void setActiveLightsChangedListener(ActiveLightsChangedListener activeLightsChangedListener) {
        this.activeLightsChangedListener = activeLightsChangedListener;
    }

    private void setupActiveLights() {
        List<String> lights = List.of("lightAZ", "lightB1", "lightB2", "lightB_All", "lightEZ", "lightFEG", "lightFOG", "lightHFE", "lightHFO", "lightK", "lightNFE", "lightSZ", "lightUZ", "lightVZ", "lightWC", "lightWZ1", "lightWZ2", "lightWZ_All");

        for (String light : lights) {
            Log.i(TAG, "Adding light " + light);
            ToggleActor lightToggleActor = (ToggleActor) serviceContext.getActorService().getActor("lightToggles0." + light);

            lightToggleActor.addItemChangeListener(item -> {
                if (item.getValue(false)) {
                    activeLights.add(lightToggleActor);
                } else {
                    activeLights.remove(lightToggleActor);
                }
                if (activeLightsChangedListener != null) {
                    activeLightsChangedListener.onListChanged();
                }
            }, true, () -> { return this != null; });
        }
    }
}
