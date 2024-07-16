package com.osh.ui.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.PieData;
import com.osh.R;
import com.osh.activity.CameraDetailsActivity;
import com.osh.activity.DoorOpenActivity;
import com.osh.activity.MainActivity;
import com.osh.databinding.FragmentHomeBinding;
import com.osh.service.IServiceContext;
import com.osh.value.DoubleValue;
import com.osh.value.enums.EnergyTrend;
import com.osh.value.EnumValue;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

public class HomeFragment extends Fragment implements HomeViewModel.IBatteryDataChangeListener {

    private FragmentHomeBinding binding;

    private IServiceContext serviceContext;

    private HomeViewModel homeViewModel;

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        serviceContext = ((MainActivity) getActivity()).getServiceContext();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        homeViewModel = new ViewModelProvider(this, new HomeViewModelFactory(getContext(), serviceContext, this)).get(HomeViewModel.class);

        binding.setHomeData(homeViewModel);

        binding.unlockDoorSlider.setOnSlideCompleteListener(slideToActView -> {
            DoorOpenActivity.invokeActivity(getContext(), DoorOpenActivity.FRONT_DOOR_ID);
            binding.unlockDoorSlider.setCompleted(false, false);
        });

        /*
        binding.btnUnlockDoor.setIcon(MaterialDrawableBuilder.with(getContext()).setIcon(MaterialDrawableBuilder.IconValue.LOCK_OPEN).setColor(Color.WHITE).build());
        binding.btnUnlockDoor.setOnClickListener(listener -> {
            DoorOpenActivity.invokeActivity(getContext(), DoorOpenActivity.FRONT_DOOR_ID);
        });
         */

        binding.btnFrontCamera.setIcon(MaterialDrawableBuilder.with(getContext()).setIcon(MaterialDrawableBuilder.IconValue.CCTV).setColor(Color.WHITE).build());
        binding.btnFrontCamera.setOnClickListener(listener -> {
            CameraDetailsActivity.invokeActivity(getContext());
        });

        setupBattery(homeViewModel);

        return binding.getRoot();
    }

    private void setupBattery(HomeViewModel homeViewModel) {
        binding.batteryState.setUsePercentValues(true);
        binding.batteryState.setDrawCenterText(true);
        binding.batteryState.setDrawHoleEnabled(true);
        binding.batteryState.setHoleColor(Color.LTGRAY);
        binding.batteryState.setHoleRadius(60);
        binding.batteryState.setCenterTextSize(20);
        //binding.batteryState.setCenterTextOffset(-5, 0);
        binding.batteryState.setDrawEntryLabels(false);
        binding.batteryState.getDescription().setEnabled(false);
        binding.batteryState.setDrawEntryLabels(false);
        binding.batteryState.setRotationEnabled(false);
        binding.batteryState.setHighlightPerTapEnabled(false);
        binding.batteryState.getLegend().setEnabled(false);

        binding.batteryState.setCenterText(homeViewModel.batteryText);
        binding.batteryState.setData(homeViewModel.batteryData);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onBatteryDataChanged(String text, PieData data) {
        binding.batteryState.setCenterText(text);
        binding.batteryState.setData(data);
        binding.batteryState.notifyDataSetChanged();
        binding.batteryState.invalidate();
    }
}