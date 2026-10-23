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
        getChildFragmentManager().beginTransaction().replace(R.id.roomFEG, new RoomFragment("feg", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomAZ, new RoomFragment("az", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomK, new RoomFragment("k", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomEZ, new RoomFragment("ez", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomWC, new RoomFragment("wc", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomVZ, new RoomFragment("vz", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomHFE, new RoomFragment("hfe", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_TOP)
        ).commit();

        getChildFragmentManager().beginTransaction().replace(R.id.roomNFE, new RoomFragment("nfe", areaViewModel.getAreaId(), RoomViewModel.RoomPosition.POSITION_BOTTOM)
        ).commit();
    }


}