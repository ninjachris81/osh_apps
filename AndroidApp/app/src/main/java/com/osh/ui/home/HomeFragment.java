package com.osh.ui.home;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.icu.number.LocalizedNumberFormatter;
import android.icu.number.Notation;
import android.icu.number.NumberFormatter;
import android.icu.number.Precision;
import android.icu.util.MeasureUnit;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.google.android.material.button.MaterialButton;
import com.osh.R;
import com.osh.activity.CameraDetailsActivity;
import com.osh.activity.DoorOpenActivity;
import com.osh.activity.MainActivity;
import com.osh.databinding.FragmentHomeBinding;
import com.osh.service.IServiceContext;
import com.osh.utils.OshValueFormats;
import com.osh.value.DoubleValue;
import com.osh.value.enums.EnergyMode;
import com.osh.value.enums.EnergyTrend;
import com.osh.value.EnumValue;
import com.osh.value.IntegerValue;
import com.osh.value.enums.HomeConnectOperationalState;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    private IServiceContext serviceContext;

    private PieData data = new PieData();

    private double lastGridPower = 0;
    private double lastBatteryPower = 0;
    private double lastGeneratorPower = 0;

    private List<Integer> homeAppliances = new ArrayList<>();
    private Map<Integer, Integer> lastRemainingSec = new ConcurrentHashMap<>();
    private Map<Integer, HomeConnectOperationalState> lastOperationState = new ConcurrentHashMap<>();

    private MediaPlayer finishedSound;

    private static final int COLOR_ORANGE = Color.parseColor("#ffa500");

    private static LocalizedNumberFormatter kiloWattsFormatter = NumberFormatter.withLocale(Locale.ENGLISH).unit(MeasureUnit.KILOWATT).notation(Notation.compactShort()).precision(Precision.fixedFraction(1));

    public HomeFragment() {
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        serviceContext = ((MainActivity) getActivity()).getServiceContext();

        finishedSound = MediaPlayer.create(getActivity(), R.raw.success1);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        setupCommon();
        setupBattery();
        setupCurrentMode();
        setupTrend();
        setupConsumption();

        setupDishwasher();
        setupWashingMachine();

        return root;
    }

    private int registerHomeAppliance(MaterialButton button, String operationStateFullId, String remainingTimeFullId, int onIcon, int offIcon, int alertIcon) {
        int index = homeAppliances.size();
        lastOperationState.put(index, HomeConnectOperationalState.UNKNOWN);
        lastRemainingSec.put(index, 0);

        EnumValue operationState = (EnumValue) serviceContext.getValueService().getValue(operationStateFullId);
        operationState.addItemChangeListener(item -> {
            refreshHomeAppliance(index, button, HomeConnectOperationalState.values()[item.getValue(0)], lastRemainingSec.get(index), onIcon, offIcon, alertIcon);
        }, true, () -> { return binding != null; });

        IntegerValue remainingTime = (IntegerValue) serviceContext.getValueService().getValue(remainingTimeFullId);
        remainingTime.addItemChangeListener(item -> {
            refreshHomeAppliance(index, button, lastOperationState.get(index), item.getValue(Integer.valueOf(0)), onIcon, offIcon, alertIcon);
        }, true, () -> { return binding != null; });

        return index;
    }

    private void setupWashingMachine() {
        registerHomeAppliance(binding.washingMachineInfos, "washingmachine.operationState", "washingmachine.remainingTime", R.drawable.washing_machine, R.drawable.washing_machine_off, R.drawable.washing_machine_alert);

    }

    private void setupDishwasher() {
        registerHomeAppliance(binding.dishwasherInfos,"dishwasher.operationState", "dishwasher.remainingTime", R.drawable.dishwasher, R.drawable.dishwasher_off, R.drawable.dishwasher_alert);
    }

    private void refreshHomeAppliance(int index, MaterialButton targetButton, HomeConnectOperationalState state, int remainingSec, int onIcon, int offIcon, int alertIcon) {
        boolean toggledToFinished = false;

        if (lastOperationState.get(index) == HomeConnectOperationalState.RUN && state == HomeConnectOperationalState.FINISHED) {
            toggledToFinished = true;
            finishedSound.start();
        }

        lastOperationState.put(index, state);
        lastRemainingSec.put(index, remainingSec);

        getActivity().runOnUiThread(() -> {
            String labelText = "";
            switch(state) {
                case UNKNOWN:
                    targetButton.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.cloud_question_outline));
                    targetButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
                    labelText = "";
                    break;
                case ACTION_REQUIRED:
                case ERROR:
                case PAUSE:
                    targetButton.setIcon(AppCompatResources.getDrawable(getContext(), alertIcon));
                    targetButton.setIconTint(ColorStateList.valueOf(Color.RED));
                    labelText = state.toString();
                    break;
                case INACTIVE:
                case ABORTING:
                    targetButton.setIcon(AppCompatResources.getDrawable(getContext(), offIcon));
                    targetButton.setIconTint(ColorStateList.valueOf(Color.GRAY));
                    labelText = state.toString();
                    break;
                case DELAYED_START:
                    targetButton.setIcon(AppCompatResources.getDrawable(getContext(), onIcon));
                    targetButton.setIconTint(ColorStateList.valueOf(Color.GRAY));
                    labelText = state.toString();
                    break;
                case RUN:
                    targetButton.setIcon(AppCompatResources.getDrawable(getContext(), onIcon));
                    targetButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
                    labelText = OshValueFormats.formatTime(remainingSec * 1000);
                    break;
                case READY:
                    targetButton.setIcon(AppCompatResources.getDrawable(getContext(), onIcon));
                    targetButton.setIconTint(ColorStateList.valueOf(Color.WHITE));
                    labelText = state.toString();
                    break;
                case FINISHED:
                    targetButton.setIcon(AppCompatResources.getDrawable(getContext(), onIcon));
                    targetButton.setIconTint(ColorStateList.valueOf(Color.GREEN));
                    labelText = state.toString();
                    break;
            }

            targetButton.setText(labelText);
        });

    }

    private void setupConsumption() {
        DoubleValue consumptionPower = (DoubleValue) serviceContext.getValueService().getValue("energy.consumptionPower");
        consumptionPower.addItemChangeListener(item -> {
            getActivity().runOnUiThread(() -> {
                binding.powerConsumption.setText(kiloWattsFormatter.format(Double.valueOf(-item.getValue(Double.valueOf(0)) / 1000)));
            });
        }, true, () -> { return binding != null; });
    }

    private void setupCommon() {


        DoubleValue batteryPower = (DoubleValue) serviceContext.getValueService().getValue("energy.batteryPower");
        batteryPower.addItemChangeListener(item -> {
            lastBatteryPower = batteryPower.getValue(Double.valueOf(0)).doubleValue();
        });

        DoubleValue gridPower = (DoubleValue) serviceContext.getValueService().getValue("energy.gridPower");
        gridPower.addItemChangeListener(item -> {
            lastGridPower = gridPower.getValue(Double.valueOf(0)).doubleValue();
        });

        DoubleValue generatorPower = (DoubleValue) serviceContext.getValueService().getValue("energy.generatorPower");
        generatorPower.addItemChangeListener(item -> {
            lastGeneratorPower = generatorPower.getValue(Double.valueOf(0)).doubleValue();
        });
    }

    private void setupCurrentMode() {
        EnumValue currentMode = (EnumValue) serviceContext.getValueService().getValue("energy.currentMode");
        currentMode.addItemChangeListener(item -> {
            EnergyMode mode = EnergyMode.values()[item.getValue(0)];
            getActivity().runOnUiThread(() -> {
                switch(mode) {
                    case MAINLY_PV:
                        binding.currentMode.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.ic_solar_panel));
                        binding.currentMode.setText(kiloWattsFormatter.format(Double.valueOf(lastGeneratorPower / 1000)));
                        break;
                    case MAINLY_GRID:
                        binding.currentMode.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.transmission_tower));
                        binding.currentMode.setText(kiloWattsFormatter.format(Double.valueOf(lastGeneratorPower / 1000)));
                        break;
                    case MAINLY_BATTERY:
                        // TODO: change symbol according to state
                        binding.currentMode.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.battery_50));
                        binding.currentMode.setText(kiloWattsFormatter.format(Double.valueOf(lastGeneratorPower / 1000)));
                        break;
                }


            });
        }, true, () -> { return binding != null; });
    }

    private void setupTrend() {
        EnumValue currentTrend = (EnumValue) serviceContext.getValueService().getValue("energy.currentTrend");
        currentTrend.addItemChangeListener(item -> {
            EnergyTrend mode = EnergyTrend.values()[item.getValue(0)];
            getActivity().runOnUiThread(() -> {
                switch(mode) {
                    case POSITIVE:
                        binding.currentTrend.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.arrow_top_right_thick));
                        binding.currentTrend.setIconTint(ColorStateList.valueOf(Color.GREEN));
                        break;
                    case NEUTRAL:
                        binding.currentTrend.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.arrow_right_thick));
                        binding.currentTrend.setIconTint(ColorStateList.valueOf(COLOR_ORANGE));
                        break;
                    case NEGATIVE:
                        binding.currentTrend.setIcon(AppCompatResources.getDrawable(getContext(), R.drawable.arrow_bottom_right_thick));
                        binding.currentTrend.setIconTint(ColorStateList.valueOf(Color.RED));
                        break;
                }


            });
        }, true, () -> { return binding != null; });
    }

    private void setupBattery() {
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

        data.setValueFormatter(new PercentFormatter());

        data.setDataSet(getDataset(0));
        data.setDrawValues(false);
        data.setValueTextSize(0);

        DoubleValue batteryState = (DoubleValue) serviceContext.getValueService().getValue("energy.batteryState");
        batteryState.addItemChangeListener((item) -> {
            updateBatteryState(item.getValue());
        }, true, () -> { return binding!=null; });

        binding.batteryState.setData(data);
    }

    private void updateBatteryState(Double value) {
        if (value != null) {
            data.setDataSet(getDataset(value.floatValue()));
            //data.setDrawValues(false);
            data.setValueTextSize(0);
            if (value.floatValue() < 0.99) {
                binding.batteryState.setCenterText(Float.valueOf (value.floatValue() * 100).intValue() + "%");
            } else {
                binding.batteryState.setCenterText("\uD83D\uDD0B");
            }
            binding.batteryState.notifyDataSetChanged();
            binding.batteryState.invalidate();
        }
    }

    private PieDataSet getDataset(float value) {
        PieEntry e = new PieEntry(value);
        if (lastBatteryPower < 0 && value < 0.99) {
            e.setIcon(MaterialDrawableBuilder.with(getContext()).setIcon(MaterialDrawableBuilder.IconValue.ARROW_UP_BOLD).setColor(Color.BLACK).build());
        } else if (lastBatteryPower > 0 && value < 0.99) {
            e.setIcon(MaterialDrawableBuilder.with(getContext()).setIcon(MaterialDrawableBuilder.IconValue.ARROW_DOWN_BOLD).setColor(Color.BLACK).build());
        } else {
            e.setIcon(null);
        }
        PieDataSet dataSet = new PieDataSet(List.of(e, new PieEntry(1-value)), "");
        int color = value > 0.6 ? Color.GREEN : value > 0.3 ? COLOR_ORANGE : value == 0.0 ? Color.GRAY : Color.RED;
        dataSet.setColors(color, com.google.android.material.R.color.design_default_color_primary);
        return dataSet;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        finishedSound.release();
        binding = null;
    }
}