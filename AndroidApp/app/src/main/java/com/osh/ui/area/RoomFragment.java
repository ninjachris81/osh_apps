package com.osh.ui.area;

import androidx.fragment.app.FragmentManager;

import com.osh.activity.MainActivity;
import com.osh.databinding.FragmentRoomBinding;
import com.osh.service.IServiceContext;

public class RoomFragment extends RoomFragmentBase<FragmentRoomBinding> {
    public RoomFragment() {
    }

    public RoomFragment(String roomId, String areaId, RoomViewModel.RoomPosition roomPosition) {
        super(roomId, areaId, roomPosition);
    }

    @Override
    protected void setBindingData() {
        binding.setAreaOverlayData(areaOverlayViewModel);
        binding.setAreaData(areaViewModel);
        binding.setRoomData(roomViewModel);
        roomViewModel.initLightStates(lightInfos.size());
        roomViewModel.initShutters(shutterInfos.size());
        roomViewModel.initTemperatures(sensorInfos.temperatureIds.size());
        roomViewModel.initHumidities(sensorInfos.humidityIds.size());
        roomViewModel.initWindowStates(sensorInfos.windowStateIds.size());
        roomViewModel.initBrightnesses(sensorInfos.brightnessIds.size());
        roomViewModel.initPresences(sensorInfos.presenceIds.size());
    }
}
