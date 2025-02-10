package com.osh.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.PieData;
import com.osh.R;
import com.osh.activity.AudioActivity;
import com.osh.activity.CameraDetailsActivity;
import com.osh.activity.DoorOpenActivity;
import com.osh.activity.MainActivity;
import com.osh.activity.OshApplication;
import com.osh.activity.WebviewActivity;
import com.osh.databinding.FragmentHomeBinding;
import com.osh.service.IServiceContext;
import com.osh.wbb12.service.IWBB12Service;

public class HomeFragment extends Fragment implements HomeViewModel.IBatteryDataChangeListener {

    private FragmentHomeBinding binding;

    private IServiceContext serviceContext;

    private IWBB12Service wbb12Service;

    private HomeViewModel homeViewModel;

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        OshApplication app = (OshApplication) getActivity().getApplication();

        serviceContext = ((MainActivity) getActivity()).getServiceContext();
        wbb12Service = ((MainActivity) getActivity()).getWBB12Service();
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        homeViewModel = new ViewModelProvider(this, new HomeViewModelFactory(getContext(), serviceContext, wbb12Service, this)).get(HomeViewModel.class);

        homeViewModel.windowStateInfoText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Message m = new Message();
                m.what = MainActivity.MSG_TYPE.DIM_DISPLAY;
                m.arg1 = 0;
                ((MainActivity)getActivity()).getHandler().sendMessage(m);
            }
        });

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

        binding.btnFrontCamera.setOnClickListener(listener -> {
            CameraDetailsActivity.invokeActivity(getContext(), "frontDoor.door", "Front Door");
        });

        binding.btnBackCamera.setOnClickListener(listener -> {
            CameraDetailsActivity.invokeActivity(getContext(), "wintergarden.door", "Wintergarden");
        });

        binding.audioInfos.setOnClickListener(listener -> {
            AudioActivity.invokeActivity(getContext());
        });

        setupBattery(homeViewModel);

        binding.powerConsumption.setOnClickListener(v -> {openEnergyStatistics(app.getApplicationConfig().getGrafana().getEnergyUrl());});
        binding.currentTrend.setOnClickListener(v -> {openEnergyStatistics(app.getApplicationConfig().getGrafana().getEnergyUrl());});
        binding.currentMode.setOnClickListener(v -> {openEnergyStatistics(app.getApplicationConfig().getFronius().getInverterUrl());});

        binding.weatherToday.setOnClickListener(v -> {openWeatherDetails();});
        binding.weatherTomorrow.setOnClickListener(v -> {openWeatherDetails();});
        binding.weatherTomorrow2.setOnClickListener(v -> {openWeatherDetails();});

        binding.waterInfos.setOnClickListener(v -> {openWaterDetails(app.getApplicationConfig().getGrafana().getWaterUrl());});

        binding.windowStateInfos.setOnClickListener(v -> {
            // TODO
        });

        binding.wbb12Infos.setOnClickListener(v -> {
            // TODO
        });

        return binding.getRoot();
    }

    private void openWaterDetails(String url) {
        WebviewActivity.invokeActivity(getContext(), url, "Water Details", null);
    }

    private void openWeatherDetails() {
        WebviewActivity.invokeActivity(getContext(), null, "Current Weather", "<html><head></head><body><a class=\"weatherwidget-io\" href=\"https://forecast7.com/en/48d789d18/stuttgart/\" data-label_1=\"STUTTGART\" data-label_2=\"WEATHER\" data-theme=\"pure\" >STUTTGART WEATHER</a><script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src='https://weatherwidget.io/js/widget.min.js';fjs.parentNode.insertBefore(js,fjs);}}(document,'script','weatherwidget-io-js');</script></body></html>");
        //WebviewActivity.invokeActivity(getContext(), null, "Current Weather", "<html><head></head><body><div id=\"openweathermap-widget-11\"></div><script src='//openweathermap.org/themes/openweathermap/assets/vendor/owm/js/d3.min.js'></script><script>window.myWidgetParam ? window.myWidgetParam : window.myWidgetParam = [];  window.myWidgetParam.push({id: 11,cityid: '2825297',appid: '4ee988f21256351a70f3d304d3ea9157',units: 'metric',containerid: 'openweathermap-widget-11',  });  (function() {var script = document.createElement('script');script.async = true;script.charset = \"utf-8\";script.src = \"//openweathermap.org/themes/openweathermap/assets/vendor/owm/js/weather-widget-generator.js\";var s = document.getElementsByTagName('script')[0];s.parentNode.insertBefore(script, s);  })();</script></body></html>");
    }

    private void openEnergyStatistics(String energyUrl) {
        WebviewActivity.invokeActivity(getContext(), energyUrl, "Energy Statistics", null);
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
        if (binding!=null) {
            binding.batteryState.setCenterText(text);
            binding.batteryState.setData(data);
            binding.batteryState.notifyDataSetChanged();
            binding.batteryState.invalidate();
        }
    }
}