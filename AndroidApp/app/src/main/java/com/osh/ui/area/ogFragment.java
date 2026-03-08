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
        getChildFragmentManager().beginTransaction().replace(R.id.roomSZ, new RoomFragment("sz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomUZ, new RoomFragment("uz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomB, new RoomFragmentB("b", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomWZ, new RoomFragmentWz("wz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomFOG, new RoomFragment("fog", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomHFO, new RoomFragment("hfo", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();
    }


}