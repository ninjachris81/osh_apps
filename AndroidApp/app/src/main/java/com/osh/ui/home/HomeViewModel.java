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
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.osh.R;
import com.osh.log.LogFacade;
import com.osh.service.IServiceContext;
import com.osh.utils.OshValueFormats;
import com.osh.value.DoubleValue;
import com.osh.value.EnumValue;
import com.osh.value.IntegerValue;
import com.osh.value.StringValue;
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

    private static final String TAG = HomeViewModel.class.getName();

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

    public final ObservableField<Drawable> weatherTodayIcon = new ObservableField<>();
    public final ObservableField<Drawable> weatherTomorrowIcon = new ObservableField<>();
    public final ObservableField<Drawable> weatherTomorrow2Icon = new ObservableField<>();

    public final ObservableField<String> weatherTempToday = new ObservableField<>();
    public final ObservableField<String> weatherTempTomorrow = new ObservableField<>();
    public final ObservableField<String> weatherTempTomorrow2 = new ObservableField<>();

    public final List<ObservableField<Float>> waterLevels = new ArrayList<>();
    public final List<ObservableField<Float>> waterFlows = new ArrayList<>();

    private final MediaPlayer finishedSound;

    public final ObservableField<String> powerConsumptionText = new ObservableField<>();

    private final IServiceContext serviceContext;

    private double lastGridPower = 0;
    private double lastBatteryPower = 0;
    private double lastGeneratorPower = 0;

    private List<Integer> homeAppliances = new ArrayList<>();
    private Map<Integer, Integer> lastRemainingSec = new ConcurrentHashMap<>();
    private Map<Integer, HomeConnectOperationalState> lastOperationState = new ConcurrentHashMap<>();

    private static LocalizedNumberFormatter kiloWattsFormatter = NumberFormatter.withLocale(Locale.ENGLISH).unit(MeasureUnit.KILOWATT).notation(Notation.compactShort()).precision(Precision.fixedFraction(1));
    private static LocalizedNumberFormatter tempFormatter = NumberFormatter.withLocale(Locale.ENGLISH).unit(MeasureUnit.CELSIUS).notation(Notation.compactShort()).precision(Precision.integer());

    private static int COLOR_ORANGE;

    private final Context context;

    public HomeViewModel(Context context, IServiceContext serviceContext, IBatteryDataChangeListener batteryDataChangeListener) {
        this.context = context;
        this.serviceContext = serviceContext;
        this.batteryDataChangeListener = batteryDataChangeListener;

        COLOR_ORANGE = ResourcesCompat.getColor(context.getResources(), R.color.orange, null);

        finishedSound = MediaPlayer.create(context, R.raw.success1);

        setupCommon();
        setupBattery();
        setupCurrentMode();
        setupHomeAppliances();
        setupConsumption();
        setupTrend();
        setupWeather();

        setupWater();
    }

    private void setupWater() {
        registerWaterLevel("waterLevels0.0", "waterFlows0.0");
        registerWaterLevel("waterLevels0.1", "waterFlows0.1");
        registerWaterLevel("waterLevels0.2", "waterFlows0.2");
        registerWaterLevel("waterLevels0.3", "waterFlows0.3");
    }

    private void registerWaterLevel(String fullIdLevel, String fullIdFlow) {
        final int index = waterLevels.size();
        waterLevels.add(new ObservableField<>());
        DoubleValue valueLevel = (DoubleValue) serviceContext.getValueService().getValue(fullIdLevel);
        valueLevel.addItemChangeListener(item -> {waterLevels.get(index).set(item.getValue(Double.valueOf(0)).floatValue());}, true, () -> {return this != null;});

        final int index2 = waterFlows.size();
        waterFlows.add(new ObservableField<>());
        DoubleValue valueFlow = (DoubleValue) serviceContext.getValueService().getValue(fullIdFlow);
        valueFlow.addItemChangeListener(item -> {waterFlows.get(index2).set(item.getValue(Double.valueOf(0)).floatValue());}, true, () -> {return this != null;});
    }

    private void setupWeather() {
        StringValue descToday = (StringValue) serviceContext.getValueService().getValue("weather.descToday");
        descToday.addItemChangeListener(item -> {
            setWeatherIcon(item.getValue(""), weatherTodayIcon);
        }, true, () -> {return this!=null;});

        StringValue descTomorrow = (StringValue) serviceContext.getValueService().getValue("weather.descTomorrow");
        descTomorrow.addItemChangeListener(item -> {
            setWeatherIcon(item.getValue(""), weatherTomorrowIcon);
        }, true, () -> {return this!=null;});

        StringValue descTomorrow2 = (StringValue) serviceContext.getValueService().getValue("weather.descTomorrow2");
        descTomorrow2.addItemChangeListener(item -> {
            setWeatherIcon(item.getValue(""), weatherTomorrow2Icon);
        }, true, () -> {return this!=null;});


        DoubleValue tempToday = (DoubleValue) serviceContext.getValueService().getValue("weather.tempToday");
        tempToday.addItemChangeListener(item -> {
            weatherTempToday.set(tempFormatter.format(item.getValue(Double.valueOf(0))).toString());
        }, true, () -> {return this!=null;});

        DoubleValue tempTomorrow = (DoubleValue) serviceContext.getValueService().getValue("weather.tempTomorrow");
        tempTomorrow.addItemChangeListener(item -> {
            weatherTempTomorrow.set(tempFormatter.format(item.getValue(Double.valueOf(0))).toString());
        }, true, () -> {return this!=null;});

        DoubleValue tempTomorrow2 = (DoubleValue) serviceContext.getValueService().getValue("weather.tempTomorrow2");
        tempTomorrow2.addItemChangeListener(item -> {
            weatherTempTomorrow2.set(tempFormatter.format(item.getValue(Double.valueOf(0))).toString());
        }, true, () -> {return this!=null;});

    }

    private void setWeatherIcon(String weatherCondition, ObservableField<Drawable> icon) {
        String desc = weatherCondition.toLowerCase();

        if ((desc.equals("sky clear") || desc.equals("sky is clear"))) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_white_balance_sunny));
        } else if (desc.equals("few clouds")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_partly_cloudy));
        } else if (desc.equals("scattered clouds")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_cloudy));
        } else if (desc.equals("broken clouds") || desc.equals("overcast clouds")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_clouds));
        } else if (desc.equals("shower rain")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_partly_rainy));
        } else if (
                desc.equals("rain") ||
                desc.equals("light rain") ||
                desc.equals("moderate rain") ||
                desc.equals("heavy intensity rain") ||
                desc.equals("light intensity shower rain") ||
                desc.equals("shower rain") ||
                desc.equals("heavy intensity shower rain") ||
                desc.equals("ragged shower rain") ||
                desc.contains("drizzle")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_rainy));
        } else if (desc.equals("very heavy rain") || desc.equals("extreme rain") || desc.equals("moderate rain") || desc.equals("heavy intensity rain")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_pouring));
        } else if (desc.equals("freezing rain") || desc.equals("light rain and snow") || desc.equals("rain and snow")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_snowflake_melt));
        } else if (
                desc.equals("light snow") ||
                desc.equals("snow") ||
                desc.equals("sleet") ||
                desc.equals("light shower sleet") ||
                desc.equals("shower sleet") ||
                desc.equals("light shower snow") ||
                desc.equals("shower snow") ||
                desc.equals("snow")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_snowy));
        } else if (desc.equals("heavy snow") || desc.equals("heavy shower snow")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_snowy_heavy));
        } else if (
                desc.equals("mist") ||
                desc.equals("smoke") ||
                desc.equals("haze") ||
                desc.equals("sand/dust whirls") ||
                desc.equals("fog") ||
                desc.equals("sand") ||
                desc.equals("sand") ||
                desc.equals("sand") ||
                desc.equals("sand") ||
                desc.equals("sand") ||
                desc.equals("mist")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_fog));
        } else if (desc.equals("dust")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_dust));
        } else if (desc.equals("volcanic ash")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_volcano_outline));
        } else if (desc.equals("squalls") || desc.equals("tornado")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_tornado));
        } else if (desc.contains("thunderstorm")) {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_weather_lightning_rainy));
        } else {
            icon.set(AppCompatResources.getDrawable(context, R.drawable.ic_cloud_question_outline));
            LogFacade.w(TAG, "Unknown weather: " + desc);
        }
    }

    private void setupTrend() {
        EnumValue currentTrend = (EnumValue) serviceContext.getValueService().getValue("energy.currentTrend");
        currentTrend.addItemChangeListener(item -> {
            EnergyTrend mode = EnergyTrend.values()[item.getValue(0)];
                switch(mode) {
                    case POSITIVE:
                        currentTrendIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_arrow_top_right_thick));
                        currentTrendIconTint.set(ColorStateList.valueOf(Color.GREEN));
                        break;
                    case NEUTRAL:
                        currentTrendIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_arrow_right_thick));
                        currentTrendIconTint.set(ColorStateList.valueOf(COLOR_ORANGE));
                        break;
                    case NEGATIVE:
                        currentTrendIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_arrow_bottom_right_thick));
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
        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_cloud_question_outline));

        EnumValue currentModeValue = (EnumValue) serviceContext.getValueService().getValue("energy.currentMode");
        currentModeValue.addItemChangeListener(item -> {
            EnergyMode mode = EnergyMode.values()[item.getValue(0)];
                switch(mode) {
                    case MAINLY_PV:
                        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_solar_panel));
                        currentModeText.set(kiloWattsFormatter.format(Double.valueOf(lastGeneratorPower / 1000)).toString());
                        break;
                    case MAINLY_GRID:
                        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_transmission_tower));
                        currentModeText.set(kiloWattsFormatter.format(Double.valueOf(lastGridPower / 1000)).toString());
                        break;
                    case MAINLY_BATTERY:
                        // TODO: change symbol according to state
                        currentModeIcon.set(AppCompatResources.getDrawable(context, R.drawable.ic_battery_50));
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
        registerHomeAppliance("washingmachine.operationState", "washingmachine.remainingTime", R.drawable.ic_washing_machine, R.drawable.ic_washing_machine_off, R.drawable.ic_washing_machine_alert);
    }

    private void setupDishwasher() {
        registerHomeAppliance("dishwasher.operationState", "dishwasher.remainingTime", R.drawable.ic_dishwasher, R.drawable.ic_dishwasher_off, R.drawable.ic_dishwasher_alert);
    }

    private int registerHomeAppliance(String operationStateFullId, String remainingTimeFullId, int onIcon, int offIcon, int alertIcon) {
        homeApplianceIcons.add(new ObservableField<>(AppCompatResources.getDrawable(context, R.drawable.ic_cloud_question_outline)));
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
                    homeApplianceIcons.get(index).set(AppCompatResources.getDrawable(context, R.drawable.ic_cloud_question_outline));
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