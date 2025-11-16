package com.osh.ui.wbb12;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;

import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.osh.R;
import com.osh.actor.ValueActor;
import com.osh.service.IServiceContext;
import com.osh.utils.IItemChangeListener;
import com.osh.value.DoubleValue;
import com.osh.value.IntegerValue;
import com.osh.value.ValueBase;
import com.osh.wbb12.WBB12_Enums;
import com.osh.wbb12.WBB12_Holding_Registers;
import com.osh.wbb12.WBB12_Input_Registers;
import com.osh.wbb12.service.IWBB12Service;

import java.util.ArrayList;
import java.util.List;

public class WBB12ViewModel extends ViewModel {

    final List<MutableLiveData<String>> data = new ArrayList<>();
    final List<String> dataLabels = new ArrayList<>();

    public final ObservableField<Integer> consumption = new ObservableField<>(0);
    public final ObservableField<Double> outsideTemp = new ObservableField<>(0.0);
    public final ObservableField<Double> roomTemp = new ObservableField<>(0.0);
    public final ObservableField<Double> waterTemp = new ObservableField<>(0.0);

    public final ObservableField<String> heatCoil = new ObservableField<>("");

    public final ObservableField<String> mode = new ObservableField<>("");

    public final ObservableField<Double> flowTempOut = new ObservableField<>(0.0);
    public final ObservableField<Double> flowTempIn = new ObservableField<>(0.0);

    public final ObservableField<Double> electricPower = new ObservableField<>(0.0);
    public final ObservableField<Double> electricPowerTotal = new ObservableField<>(0.0);

    public final ObservableField<String> energyStats = new ObservableField<>("...");

    public final ObservableField<String> coilStats = new ObservableField<>("...");

    public final ObservableField<ColorStateList> wwPushIconTint = new ObservableField<>();
    public final ObservableField<String> wwPushLabel15 = new ObservableField<>("Start 15 min");

    private int heatCoil1Status = 0;
    private int heatCoil2Status = 0;

    private int energyStatsTotalToday = 0;
    private int energyStatsTotalYesterday = 0;
    private int energyStatsTotalMonth = 0;
    private int energyStatsTotalYear = 0;

    private int coil1WorkingHours = 0;
    private int coil2WorkingHours = 0;

    private int wwPush = 0;

    final IWBB12Service wbb12Service;

    final IServiceContext serviceContext;

    private static int COLOR_ORANGE;

    public WBB12ViewModel(Context context, IWBB12Service wbb12Service, IServiceContext serviceContext) {
        this.wbb12Service = wbb12Service;
        this.serviceContext = serviceContext;

        COLOR_ORANGE = ResourcesCompat.getColor(context.getResources(), R.color.orange, null);

        for (String key : wbb12Service.getWBB12Keys()) {
            ValueBase val = wbb12Service.getWBB12Value(key);

            MutableLiveData<String> liveData = new MutableLiveData();
            setValue(liveData, val);

            ((ValueBase) val).addItemChangeListener(item -> {
                if (item instanceof DoubleValue) {
                    setValue(liveData, (ValueBase) item);
                } else if (item instanceof IntegerValue) {
                    setValue(liveData, (ValueBase) item);
                } else if (item instanceof ValueActor) {
                    setValue(liveData, (ValueBase) item);
                } else {
                    // TODO: error
                }
            }, true, () -> {return liveData!=null;});

            data.add(liveData);
            dataLabels.add(wbb12Service.getWBB12Value(key).getId());
        }

        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.OPERATING_DISPLAY)).addItemChangeListener(item -> {
            WBB12_Enums.Enum_OPERATING_DISPLAY display = WBB12_Enums.Enum_OPERATING_DISPLAY.get(item.getValue(0));
            mode.set(display.getShortName());
        }, true, () -> { return this != null; });


        ((IntegerValue) serviceContext.getValueService().getValue("wbb12.heatPumpConsumption")).addItemChangeListener(item -> {
            consumption.set(item.getValue(0));
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.OUTSIDE_TEMP_1)).addItemChangeListener(item -> {
            outsideTemp.set(item.getValue(0.0));
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.HK3_FLOW_TEMP)).addItemChangeListener(item -> {
            roomTemp.set(item.getValue(0.0));
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.WARM_WATER_TEMP)).addItemChangeListener(item -> {
            waterTemp.set(item.getValue(0.0));
        }, true, () -> { return this != null; });



        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.HEAT_COIL_STATUS_COIL1)).addItemChangeListener(item -> {
            heatCoil1Status = item.getValue(0);
            updateHeatCoil();
        }, true, () -> { return this != null; });

        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.HEAT_COIL_STATUS_COIL2)).addItemChangeListener(item -> {
            heatCoil2Status = item.getValue(0);
            updateHeatCoil();
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.HEAT_PUMP_FLOW_TEMP)).addItemChangeListener(item -> {
            flowTempOut.set(item.getValue(0.0));
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.HEAT_PUMP_RETURN_FLOW_TEMP)).addItemChangeListener(item -> {
            flowTempIn.set(item.getValue(0.0));
        }, true, () -> { return this != null; });


        ((DoubleValue) serviceContext.getValueService().getValue("wbb12EnergyMeter.powerTotal")).addItemChangeListener(item -> {
            electricPower.set(item.getValue(0.0));
        }, true, () -> { return this != null; });

        ((DoubleValue) serviceContext.getValueService().getValue("wbb12EnergyMeter.energyTotal")).addItemChangeListener(item -> {
            electricPowerTotal.set(item.getValue(0.0));
        }, true, () -> { return this != null; });


        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.ENERGY_TOTAL_TODAY)).addItemChangeListener(item -> {
            energyStatsTotalToday = item.getValue(0);
            updateStats();
        }, true, () -> { return this != null; });

        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.ENERGY_TOTAL_YESTERDAY)).addItemChangeListener(item -> {
            energyStatsTotalYesterday = item.getValue(0);
            updateStats();
        }, true, () -> { return this != null; });

        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.ENERGY_TOTAL_MONTH)).addItemChangeListener(item -> {
            energyStatsTotalMonth = item.getValue(0);
            updateStats();
        }, true, () -> { return this != null; });

        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.ENERGY_TOTAL_YEAR)).addItemChangeListener(item -> {
            energyStatsTotalYear = item.getValue(0);
            updateStats();
        }, true, () -> { return this != null; });


        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.HEAT_COIL_WORKING_HOURS_COIL1)).addItemChangeListener(item -> {
            coil1WorkingHours = item.getValue(0);
            updateCoilStats();
        }, true, () -> { return this != null; });

        ((IntegerValue) wbb12Service.getWBB12Value(WBB12_Input_Registers.HEAT_COIL_WORKING_HOURS_COIL2)).addItemChangeListener(item -> {
            coil2WorkingHours = item.getValue(0);
            updateCoilStats();
        }, true, () -> { return this != null; });

        wbb12Service.getWBB12Value(WBB12_Holding_Registers.WARM_WATER_PUSH).addItemChangeListener((IItemChangeListener<ValueActor>) item -> {
            if (item.getValue() == null) {
                wwPush = 0;
            } else {
                wwPush = (int) item.getValue();
            }
            updateWarmWater();

        }, true);

    }

    private void updateWarmWater() {
        if (wwPush > 0) {
            wwPushIconTint.set(ColorStateList.valueOf(COLOR_ORANGE));
            wwPushLabel15.set(wwPush + " min");
        } else {
            wwPushIconTint.set(ColorStateList.valueOf(Color.WHITE));
            wwPushLabel15.set("Start 15 min");
        }
    }

    private void updateCoilStats() {
        StringBuffer s = new StringBuffer();
        s.append("Coil1: ").append(coil1WorkingHours).append("h\n");
        s.append("Coil2: ").append(coil2WorkingHours).append("h");
        coilStats.set(s.toString());
    }

    private void updateStats() {
        StringBuffer s = new StringBuffer();
        s.append("Today: ").append(energyStatsTotalToday).append("kWh\n");
        s.append("Yesterday: ").append(energyStatsTotalYesterday).append("kWh\n");
        s.append("Month: ").append(energyStatsTotalMonth).append("kWh\n");
        s.append("Year: ").append(energyStatsTotalYear).append("kWh");
        energyStats.set(s.toString());
    }

    private void updateHeatCoil() {
        int coilCount = 0;
        if (heatCoil1Status != 0) coilCount++;
        if (heatCoil2Status != 0) coilCount++;

        if (coilCount == 0) {
            heatCoil.set("OFF");
        } else {
            heatCoil.set(coilCount + " ON");
        }
    }

    public void setValue(MutableLiveData<String> liveData, ValueBase val) {
        liveData.postValue(wbb12Service.getWBB12InputFormat(val.getFullId()).getUnit().withUnit(val, wbb12Service.getWBB12InputFormat(val.getFullId()).getEnumType()));
    }
}