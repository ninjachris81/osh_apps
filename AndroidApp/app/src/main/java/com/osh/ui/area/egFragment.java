package com.osh.ui.area;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osh.R;
import com.osh.databinding.FragmentEgBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link egFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class egFragment extends AreaFragmentBase {

    private FragmentEgBinding binding;

    public egFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _onCreateView("eg");

        // Inflate the layout for this fragment
        //View root = super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_eg, container, false);

        binding.setAreaData(areaViewModel);

        setupArea();

        return binding.getRoot();
    }

    private void setupArea() {
        String relayValueGroupId= "allRelays0";
        String toggleValueGroupId = "lightToggles0";
        String shutterValueGroupId = "allShutters0";
        String shutterModeValueGroupId = "shutterModes0";
        String tempValueGroupId = "temps0";
        String humsValueGroupId = "hums0";
        String brightnessValueGroupId = "brightnesses";
        String windowStateGroupId = "allSwitches1";
        String presenceGroupdId = "presence";

        getChildFragmentManager().beginTransaction().add(R.id.roomFEG, new RoomFragment("feg", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "0", toggleValueGroupId, "lightFEG")
        ).commit();


        getChildFragmentManager().beginTransaction().add(R.id.roomAZ, new RoomFragment("az", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "1", toggleValueGroupId, "lightAZ")
                .withShutter(shutterValueGroupId, "0", shutterModeValueGroupId, "0")
                .withTemperature(tempValueGroupId, "25")
                .withHumidity(humsValueGroupId, "25")
                .withBrightness(brightnessValueGroupId, "25")
                .withPresence(presenceGroupdId, "25")
                .withWindowState(windowStateGroupId, "0")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomK, new RoomFragment("k", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "2", toggleValueGroupId, "lightK")
                .withShutter(shutterValueGroupId, "1", shutterModeValueGroupId, "1")
                .withWindowState(windowStateGroupId, "1")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomEZ, new RoomFragment("ez", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "3", toggleValueGroupId, "lightEZ")
                .withShutter(shutterValueGroupId, "2", shutterModeValueGroupId, "2")
                .withWindowState(windowStateGroupId, "2")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomWC, new RoomFragment("wc", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "4", toggleValueGroupId, "lightWC")
                .withShutter(shutterValueGroupId, "3", shutterModeValueGroupId, "3")
                .withWindowState(windowStateGroupId, "3")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomVZ, new RoomFragment("vz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "5", toggleValueGroupId, "lightVZ")
                .withShutter(shutterValueGroupId, "4", shutterModeValueGroupId, "4")
                .withWindowState(windowStateGroupId, "4")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomHFE, new RoomFragment("hfe", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "6", toggleValueGroupId, "lightHFE")
                .withWindowState("frontDoor", "2")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomNFE, new RoomFragment("nfe", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "7", toggleValueGroupId, "lightNFE")
                .withShutter(shutterValueGroupId, "5", shutterModeValueGroupId, "5")
                .withWindowState(windowStateGroupId, "5")
        ).commit();


    }


}