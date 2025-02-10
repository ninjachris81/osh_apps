package com.osh.ui.area;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osh.R;
import com.osh.databinding.FragmentEgBinding;
import com.osh.databinding.FragmentWgBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link egFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class wgFragment extends AreaFragmentBase {

    private FragmentWgBinding binding;

    public wgFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _onCreateView("wg");

        // Inflate the layout for this fragment
        //View root = super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_wg, container, false);

        binding.setAreaData(areaViewModel);

        setupArea();

        return binding.getRoot();
    }

    private void setupArea() {
        String relayValueGroupId= "allRelays1";
        String toggleValueGroupId = "lightToggles2";
        String shutterValueGroupId = "allShutters0";
        String shutterModeValueGroupId = "shutterModes0";
        String tempValueGroupId = "temps";
        String humsValueGroupId = "hums";
        String brightnessValueGroupId = "brightnesses";
        String windowStateGroupId = "allSwitches1";
        String presenceGroupdId = "presence";

        getChildFragmentManager().beginTransaction().add(R.id.roomWG1, new RoomFragment("wg1", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "8", toggleValueGroupId, "lightWG1")
        ).commit();


        getChildFragmentManager().beginTransaction().add(R.id.roomWG2, new RoomFragment("wg2", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "9", toggleValueGroupId, "lightWG2")
        ).commit();

    }
}