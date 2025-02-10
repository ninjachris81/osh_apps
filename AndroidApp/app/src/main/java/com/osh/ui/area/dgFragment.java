package com.osh.ui.area;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.osh.R;
import com.osh.databinding.FragmentDgBinding;
import com.osh.databinding.FragmentEgBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link dgFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class dgFragment extends AreaFragmentBase {

    private FragmentDgBinding binding;

    public dgFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _onCreateView("dg");

        // Inflate the layout for this fragment
        //View root = super.onCreateView(inflater, container, savedInstanceState);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dg, container, false);

        binding.setAreaData(areaViewModel);

        setupArea();

        return binding.getRoot();
    }

    private void setupArea() {
        String relayValueGroupId= "allRelays3";
        String toggleValueGroupId = "lightToggles2";
        String tempValueGroupId = "temps";
        String humsValueGroupId = "hums";
        String brightnessValueGroupId = "brightnesses";
        String windowStateGroupId = "allSwitches2";
        String presenceGroupdId = "presence";

        getChildFragmentManager().beginTransaction().add(R.id.roomDG, new RoomFragment("dg", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                //.withLight(relayValueGroupId, "0", toggleValueGroupId, "lightDG")
                //.withWindowState(windowStateGroupId, "1")
        ).commit();


        getChildFragmentManager().beginTransaction().add(R.id.roomBDG, new RoomFragment("bdg", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                //.withLight(relayValueGroupId, "1", toggleValueGroupId, "lightBDG")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomHFDG, new RoomFragment("hfdg", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                //.withLight(relayValueGroupId, "2", toggleValueGroupId, "lightHFDG")
        ).commit();
    }
}