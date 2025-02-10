package com.osh.ui.area;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.osh.R;
import com.osh.databinding.FragmentBasementBinding;

public class basementFragment extends AreaFragmentBase {

    private FragmentBasementBinding binding;

    public basementFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _onCreateView("basement");
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_basement, container, false);

        binding.setAreaData(areaViewModel);

        setupArea();

        return binding.getRoot();
    }


    private void setupArea() {
        String relayValueGroupId= "allRelays1";
        String toggleValueGroupId = "lightToggles1";
        String tempValueGroupId = "temps0";
        String humsValueGroupId = "hums0";
        String brightnessValueGroupId = "brightnesses";
        String presenceGroupdId = "presence";

        getChildFragmentManager().beginTransaction().add(R.id.roomFB, new RoomFragment("fb", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                //.withLight(relayValueGroupId, "0", toggleValueGroupId, "lightFB")
        ).commit();


        getChildFragmentManager().beginTransaction().add(R.id.roomL2, new RoomFragment("l2", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "10", toggleValueGroupId, "lightL2")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomL3, new RoomFragment("l3", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "12", toggleValueGroupId, "lightL3")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomWS, new RoomFragment("ws", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "14", toggleValueGroupId, "lightWS")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomHW, new RoomFragment("hw", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "15", toggleValueGroupId, "lightHW")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomHFB, new RoomFragment("hfb", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
                .withLight(relayValueGroupId, "11", toggleValueGroupId, "lightHFB")
        ).commit();

        getChildFragmentManager().beginTransaction().add(R.id.roomL1, new RoomFragment("l1", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
                .withLight(relayValueGroupId, "13", toggleValueGroupId, "lightL1")
        ).commit();


    }

}