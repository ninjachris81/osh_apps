package com.osh.ui.home;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.number.LocalizedNumberFormatter;
import android.icu.number.Notation;
import android.icu.number.NumberFormatter;
import android.icu.number.Precision;
import android.icu.util.MeasureUnit;
import android.media.MediaPlayer;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.osh.R;
import com.osh.service.IServiceContext;
import com.osh.utils.OshValueFormats;
import com.osh.value.DoubleValue;
import com.osh.value.EnumValue;
import com.osh.value.IntegerValue;
import com.osh.value.enums.EnergyMode;
import com.osh.value.enums.EnergyTrend;
import com.osh.value.enums.HomeConnectOperationalState;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HomeViewModel extends ViewModel {

    private final IBatteryDataChangeListener batteryDataChangeListener;
    public String batteryText = "...";
    public final PieData batteryData = new PieData();

    public interface IBatteryDataChangeListener {
        void onBatteryDataChanged(String text, PieData data);
    }


    public final ObservableField<String> currentModeText = new ObservableField<>("");
    public final ObservableField<Drawable> currentModeIcon = new ObservableField<>();

    public final List<ObservableField<String>> homeApplianceTexts = new ArrayList<>();
    public final List<ObservableField<Drawable>> homeApplianceIcons = new ArrayList<>();
    public final List<ObservableField<ColorStateList>> homeApplianceIconTints = new ArrayList<>();

    public final ObservableField<Drawable> currentTrendIcon = new ObservableField<>();
    public final ObservableField<ColorStateList> currentTrendIconTint = new ObservableField<>();

    private final MediaPlayer finishedSound;

    public final ObservableField<String> powerConsumptionText = new ObservableField<>();

    private final IServiceContext serviceContext;

    private double lastGridPower = 0;
    private double lastBatteryPower = 0;
    private double lastGeneratorPower = 0;

    private double lastTrendDetail = 0;

    private List<Integer> homeAppliances = new ArrayList<>();
    private Map<Integer, Integer> lastRemainingSec = new ConcurrentHashMap<>();
    private Map<Integer, HomeConnectOperationalState> lastOperationState = new ConcurrentHashMap<>();

    private static LocalizedNumberFormatter kiloWattsFormatter = NumberFormatter.withLocale(Locale.ENGLISH).unit(MeasureUnit.KILOWATT).notation(Notation.compactShort()).precision(Precision.fixedFraction(1));

    private static final int COLOR_ORANGE = Color.parseColor("#ffa500");

    private final Context context;

    public HomeViewModel(Context context, IServiceContext serviceContext, IBatteryDataChangeListener batteryDataChangeListener) {
        this.context = context;
        this.serviceContext = serviceContext;
        this.batteryDataChangeListener = batteryDataChangeListener;

        finishedSound = MediaPlayer.create(context, R.raw.success1);

        setupCommon();
        setupBattery();
        setupCurrentMode();
        setupHomeAppliances();
        setupConsumption();
        setupTrend();
    }

    private void setupTrend() {
        EnumValue currentTrend = (EnumValue) serviceContext.getValueService().getValue("energy.currentTrend");
        currentTrend.addItemChangeListener(item -> {
            EnergyTrend mode = EnergyTrend.values()[item.getValue(0)];
                switch(mode) {
                    case POSITIVE:
                        currentTrendIcon.set(AppCompatResources.getDrawable(context, R.drawable.arrow_top_right_thick));
                        currentTrendIconTint.set(ColorStateList.valueOf(Color.GREEN));
                        break;
                    case NEUTRAL:
                        currentTrendIcon.set(AppCompatResources.getDrawable(context, R.drawable.arrow_right_thick));
                        currentTrendIconTint.set(ColorStateList.valueOf(COLOR_ORANGE));
                        break;
                    case NEGATIVE:
                        currentTrendIcon.set(AppCompatResources.getDrawable(context, R.drawable.arrow_bottom_right_thick));
                        currentTrendIconTint.set(ColorStateList.valueOf(Color.RED));
                        break;
                }
        }, true, () -> { return this != null; });
    }

    private void setupConsumption() {
        DoubleValue consumptionPower = (DoubleValue) serviceContext.getValueService().getValue("energy.consumptionPower");
        consumptionPower.addItemChangeListener(item -> {
            powerConsumptionText.set(kiloWattsFormatter.format(Double.valueOf(-item.getValue(Double.valueOf(0)) / 1000)).toString());
        }, true, () -> { return this != null; });
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

    private void setupBattery() {
        batteryData.setValueFormatter(new PercentFormatter());
        batteryData.setDataSet(getDataset(0));
        batteryData.setDrawValues(false);
        batteryData.setValueTextSize(0);

        DoubleValue batteryState = (DoubleValue) serviceContext.getValueService().getValue("energy.batteryState");
        batteryState.addItemChangeListener((item) -> {
            Double newValue = item.getValue(Double.valueOf(0));
            if (newValue < 0.99) {
                batteryText = Double.valueOf(newValue * 100).intValue() + "%";
            } else {
                batteryText = "\uD83D\uDD0B";
            }

            batteryData.setDataSet(getDataset(newValue.floatValue()));
            batteryData.setValueTextSize(0);

            batteryDataChangeListener.onBatteryDataChanged(batteryText, batteryData);
        }, true, () -> { return this!=null; });
    }

    private PieDataSet getDataset(float value) {
        PieEntry e = new PieEntry(value);
        if (lastBatteryPower < 0 && value < 0.99) {
            e.setIcon(MaterialDrawableBuilder.with(context).setIcon(MaterialDrawableBuilder.IconValue.ARROW_UP_BOLD).setColor(Color.BLACK).build());
        } else if (lastBatteryPower > 0 && value < 0.99) {
            e.setIcon(MaterialDrawableBuilder.with(context).setIcon(MaterialDrawableBuilder.IconValue.ARROW_DOWN_BOLD).setColor(Color.BLACK).build());
        } else {
            e.setIcon(null);
        }
        PieDataSet dataSet = new PieDataSet(List.of(e, new PieEntry(1-value)), "");
        int color = value > 0.6 ? Color.GREEN : value > 0.3 ? COLOR_ORANGE : value == 0.0 ? Color.GRAY : Color.RED;
        dataSet.setColors(color, com.google.android.material.R.color.design_default_color_primary);
        return dataSet;
    }

    private void setupCurrentMode() {
        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.cloud_question_outline));

        EnumValue currentModeValue = (EnumValue) serviceContext.getValueService().getValue("energy.currentMode");
        currentModeValue.addItemChangeListener(item -> {
            EnergyMode mode = EnergyMode.values()[item.getValue(0)];
                switch(mode) {
                    case MAINLY_PV:
                        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_solar_panel));
                        currentModeText.set(kiloWattsFormatter.format(Double.valueOf(lastGeneratorPower / 1000)).toString());
                        break;
                    case MAINLY_GRID:
                        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.transmission_tower));
                        currentModeText.set(kiloWattsFormatter.format(Double.valueOf(lastGridPower / 1000)).toString());
                        break;
                    case MAINLY_BATTERY:
                        // TODO: change symbol according to state
                        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.battery_50));
                        currentModeText.set(kiloWattsFormatter.format(Double.valueOf(lastBatteryPower / 1000)).toString());
                        break;
                }
        }, true, () -> { return this != null; });
    }

    private void setupHomeAppliances() {
        setupDishwasher();
        setupWashingMachine();
    }

    private void setupWashingMachine() {
        registerHomeAppliance("washingmachine.operationState", "washingmachine.remainingTime", R.drawable.washing_machine, R.drawable.washing_machine_off, R.drawable.washing_machine_alert);
    }

    private void setupDishwasher() {
        registerHomeAppliance("dishwasher.operationState", "dishwasher.remainingTime", R.drawable.dishwasher, R.drawable.dishwasher_off, R.drawable.dishwasher_alert);
    }

    private int registerHomeAppliance(String operationStateFullId, String remainingTimeFullId, int onIcon, int offIcon, int alertIcon) {
        homeApplianceIcons.add(new ObservableField<>(AppCompatResources.getDrawable(context, R.drawable.cloud_question_outline)));
        homeApplianceIconTints.add(new ObservableField<>(ColorStateList.valueOf(Color.WHITE)));
        homeApplianceTexts.add(new ObservableField<>(""));

        int index = homeAppliances.size();
        homeAppliances.add(index);
        lastOperationState.put(index, HomeConnectOperationalState.UNKNOWN);
        lastRemainingSec.put(index, 0);

        EnumValue operationState = (EnumValue) serviceContext.getValueService().getValue(operationStateFullId);
        operationState.addItemChangeListener(item -> {
            refreshHomeAppliance(index, HomeConnectOperationalState.values()[item.getValue(0)], lastRemainingSec.get(index), onIcon, offIcon, alertIcon);
        }, true, () -> { return this != null; });

        IntegerValue remainingTime = (IntegerValue) serviceContext.getValueService().getValue(remainingTimeFullId);
        remainingTime.addItemChangeListener(item -> {
            refreshHomeAppliance(index, lastOperationState.get(index), item.getValue(Integer.valueOf(0)), onIcon, offIcon, alertIcon);
        }, true, () -> { return this != null; });

        return index;
    }

    private void refreshHomeAppliance(int index, HomeConnectOperationalState state, int remainingSec, int onIcon, int offIcon, int alertIcon) {
        if (lastOperationState.get(index) == HomeConnectOperationalState.RUN && state == HomeConnectOperationalState.FINISHED) {
            finishedSound.start();
        }

        lastOperationState.put(index, state);
        lastRemainingSec.put(index, remainingSec);

            String labelText = "";
            switch(state) {
                case UNKNOWN:
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, R.drawable.cloud_question_outline));
                    homeApplianceIconTints.get(index).set(ColorStateList.valueOf(Color.WHITE));
                    labelText = "";
                    break;
                case ACTION_REQUIRED:
                case ERROR:
                case PAUSE:
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, alertIcon));
                    homeApplianceIconTints.get(index).set(ColorStateList.valueOf(Color.RED));
                    labelText = state.toString();
                    break;
                case INACTIVE:
                case ABORTING:
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, offIcon));
                    homeApplianceIconTints.get(index).set(ColorStateList.valueOf(Color.GRAY));
                    labelText = state.toString();
                    break;
                case DELAYED_START:
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, onIcon));
                    homeApplianceIconTints.get(index).set(ColorStateList.valueOf(Color.GRAY));
                    labelText = state.toString();
                    break;
                case RUN:
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, onIcon));
                    homeApplianceIconTints.get(index).set(ColorStateList.valueOf(Color.WHITE));
                    labelText = remainingSec > 0 ? OshValueFormats.formatTime(remainingSec * 1000) : "...";
                    break;
                case READY:
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, onIcon));
                    homeApplianceIconTints.get(index).set(ColorStateList.valueOf(Color.WHITE));
                    labelText = state.toString();
                    break;
                case FINISHED:
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, onIcon));
                    homeApplianceIconTints.get(index).set(ColorStateList.valueOf(Color.GREEN));
                    labelText = state.toString();
                    break;
            }

        homeApplianceTexts.get(index).set(labelText);
    }


}