package com.osh.ui.area;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.osh.R;
import com.osh.activity.MainActivity;
import com.osh.databinding.FragmentAreaBinding;
import com.osh.service.IServiceContext;

public class AreaFragment extends Fragment {

    private FragmentAreaBinding binding;

    private AreaOverlayViewModel areaOverlayViewModel;
    private ViewPager2 viewPager;

    AreaPagerAdapter areaPagerAdapter;

    private IServiceContext serviceContext;
    private Observable.OnPropertyChangedCallback overlayCallback;

    public enum AreaOverlays {
        NONE,
        LIGHTS,
        SHUTTERS,
        SENSORS,
        PRESENCE,
        AUDIO,
        DEVICES
    }

    public AreaFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        serviceContext = ((MainActivity) getActivity()).getServiceContext();

        areaOverlayViewModel = new ViewModelProvider(this, AreaOverlayViewModelFactory.getInstance()).get(AreaOverlayViewModel.class);

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_area, container, false);
        binding.setAreaOverlayData(areaOverlayViewModel);
        binding.setLifecycleOwner(this);

        areaPagerAdapter = new AreaPagerAdapter(getChildFragmentManager(), getLifecycle());
        viewPager = binding.areaViewPager;
        viewPager.setAdapter(areaPagerAdapter);

        TabLayout tabLayout = binding.tabLayout;
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(areaPagerAdapter.getTitle(position))
        ).attach();

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        areaOverlayViewModel.currentOverlay.removeOnPropertyChangedCallback(overlayCallback);
    }

    @Override
    public void onResume() {
        super.onResume();

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.area_last_tab), tab.getPosition());
                editor.apply();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        binding.tabLayout.getTabAt(sharedPref.getInt(getString(R.string.area_last_tab), 0)).select();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        areaOverlayViewModel.currentOverlay.set(AreaOverlays.values()[sharedPref.getInt(getString(R.string.area_last_overlay), 0)]);
        overlayCallback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                sharedPref.edit().putInt(getString(R.string.area_last_overlay), areaOverlayViewModel.currentOverlay.get().ordinal()).commit();
            }
        };

        areaOverlayViewModel.currentOverlay.addOnPropertyChangedCallback(overlayCallback);

        ChipGroup overlaySelection = binding.overlaySelectionGroup;
        if (overlaySelection != null) {
            overlaySelection.setOnCheckedStateChangeListener((group, checkedIds) -> {
                if (checkedIds.isEmpty()) {
                    areaOverlayViewModel.currentOverlay.set(AreaOverlays.NONE);
                } else {
                    switch(checkedIds.get(0)) {
                        case R.id.overlay_lights:
                            areaOverlayViewModel.currentOverlay.set(AreaOverlays.LIGHTS);
                            break;
                        case R.id.overlay_shutters:
                            areaOverlayViewModel.currentOverlay.set(AreaOverlays.SHUTTERS);
                            break;
                        case R.id.overlay_sensors:
                            areaOverlayViewModel.currentOverlay.set(AreaOverlays.SENSORS);
                            break;
                        case R.id.overlay_audio:
                            areaOverlayViewModel.currentOverlay.set(AreaOverlays.AUDIO);
                            break;
                        case R.id.overlay_presence:
                            areaOverlayViewModel.currentOverlay.set(AreaOverlays.PRESENCE);
                            break;
                    }
                }
            });
        }
    }
}