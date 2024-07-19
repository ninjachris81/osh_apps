package com.osh.ui.area;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.osh.R;
import com.osh.databinding.FragmentOgBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ogFragment extends AreaFragmentBase {

    private FragmentOgBinding binding;

    public ogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _onCreateView("og");

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_og, container, false);

        binding.setAreaData(areaViewModel);

        setupArea();

        return binding.getRoot();
    }

    private void setupArea() {
        String relayValueGroupId= "allRelays0";
        String toggleValueGroupId = "lightToggles0";
        String shutterValueGroupId = "allShutters0";
        String shutterModeValueGroupId = "shutterModes0";

        getChildFragmentManager().beginTransaction().replace(R.id.roomSZ, new RoomFragment("sz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "16", toggleValueGroupId, "lightSZ")
                .withShutter(shutterValueGroupId, "6", shutterModeValueGroupId, "6"))
                .commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomUZ, new RoomFragment("uz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "17", toggleValueGroupId, "lightUZ")
                .withShutter(shutterValueGroupId, "7", shutterModeValueGroupId, "7"))
                .commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomB, new RoomFragmentB("b", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "18", toggleValueGroupId, "lightB_All")
                .withLight(relayValueGroupId, "19", toggleValueGroupId, "lightB_All")
                .withShutter(shutterValueGroupId, "8", shutterModeValueGroupId, "8")
                .withShutter(shutterValueGroupId, "9", shutterModeValueGroupId, "9"))
                .commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomWZ, new RoomFragmentWz("wz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "20", toggleValueGroupId, "lightWZ1")
                .withLight(relayValueGroupId, "21", toggleValueGroupId, "lightWZ2")
                .withShutter(shutterValueGroupId, "10", shutterModeValueGroupId, "10")
                .withShutter(shutterValueGroupId, "11", shutterModeValueGroupId, "11"))
                .commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomFOG, new RoomFragment("fog", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "22", toggleValueGroupId, "lightFOG"))
                .commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomHFO, new RoomFragment("hfo", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "23", toggleValueGroupId, "lightHFO"))
                .commit();
    }


}