package com.osh.ui.area;

import com.osh.R;
import com.osh.databinding.FragmentRoomMdgBinding;

import java.util.List;

public class RoomFragmentMdg extends RoomFragmentBase<FragmentRoomMdgBinding> {

    public RoomFragmentMdg() {
    }

    public RoomFragmentMdg(String roomId, String areaId, RoomViewModel.RoomPosition roomPosition) {
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
        return R.layout.fragment_room_mdg;
    }

    @Override
    protected List<Integer> getShutterModeButtons() {
        return List.of();
    }

    @Override
    protected List<Integer> getWindowStateIndicators() {
        return List.of(R.id.windowStateMDG0, R.id.windowStateMDG1, R.id.windowStateMDG2, R.id.windowStateMDG3);
    }
}
