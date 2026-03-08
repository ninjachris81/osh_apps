package com.osh.ui.area;

import com.osh.R;
import com.osh.databinding.FragmentRoomBBinding;

import java.util.List;

public class RoomFragmentB extends RoomFragmentBase<FragmentRoomBBinding> {

    public RoomFragmentB() {
    }

    public RoomFragmentB(String roomId, String areaId, RoomViewModel.RoomPosition roomPosition) {
        super(roomId, areaId, roomPosition);
    }

    @Override
    protected void setBindingData() {
        binding.setAreaOverlayData(areaOverlayViewModel);
        binding.setAreaData(areaViewModel);
        binding.setRoomData(roomViewModel);
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_room_b;
    }

    @Override
    protected List<Integer> getShutterModeButtons() {
        return List.of(R.id.shutterModeButtonB0, R.id.shutterModeButtonB1);
    }

    @Override
    protected List<Integer> getWindowStateIndicators() {
        return List.of(R.id.windowStateB0, R.id.windowStateB1);
    }
}
