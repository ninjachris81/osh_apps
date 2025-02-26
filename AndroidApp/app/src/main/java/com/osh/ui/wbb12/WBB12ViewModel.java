package com.osh.ui.wbb12;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.osh.actor.ValueActor;
import com.osh.service.IServiceContext;
import com.osh.service.IValueService;
import com.osh.value.DoubleValue;
import com.osh.value.IntegerValue;
import com.osh.value.ValueBase;
import com.osh.wbb12.WBB12_Enums;
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

    private int heatCoil1Status = 0;
    private int heatCoil2Status = 0;

    final IWBB12Service wbb12Service;

    final IServiceContext serviceContext;

    public WBB12ViewModel(IWBB12Service wbb12Service, IServiceContext serviceContext) {
        this.wbb12Service = wbb12Service;
        this.serviceContext = serviceContext;

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

        ((IntegerValue) wbb12Service.getWBB12Value("wbb12.operatingDisplay")).addItemChangeListener(item -> {
            WBB12_Enums.Enum_OPERATING_DISPLAY display = WBB12_Enums.Enum_OPERATING_DISPLAY.get(item.getValue());
            mode.set(display.getShortName());
        }, true, () -> { return this != null; });


        ((IntegerValue) wbb12Service.getWBB12Value("wbb12.heatPumpConsumption")).addItemChangeListener(item -> {
            consumption.set(item.getValue());
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value("wbb12.outsideTemp1")).addItemChangeListener(item -> {
            outsideTemp.set(item.getValue());
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value("wbb12.hk3FlowTemp")).addItemChangeListener(item -> {
            roomTemp.set(item.getValue());
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value("wbb12.warmWaterTemp")).addItemChangeListener(item -> {
            waterTemp.set(item.getValue());
        }, true, () -> { return this != null; });



        ((IntegerValue) wbb12Service.getWBB12Value("wbb12.heatCoilStatusCoil1")).addItemChangeListener(item -> {
            heatCoil1Status = item.getValue();
            updateHeatCoil();
        }, true, () -> { return this != null; });

        ((IntegerValue) wbb12Service.getWBB12Value("wbb12.heatCoilStatusCoil1")).addItemChangeListener(item -> {
            heatCoil2Status = item.getValue();
            updateHeatCoil();
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value("wbb12.heatPumpFlowTemp")).addItemChangeListener(item -> {
            flowTempOut.set(item.getValue());
        }, true, () -> { return this != null; });

        ((DoubleValue) wbb12Service.getWBB12Value("wbb12.heatPumpReturnFlowTemp")).addItemChangeListener(item -> {
            flowTempIn.set(item.getValue());
        }, true, () -> { return this != null; });


        ((DoubleValue) serviceContext.getValueService().getValue("wbb12EnergyMeter.powerTotal")).addItemChangeListener(item -> {
            electricPower.set(item.getValue());
        }, true, () -> { return this != null; });

        ((DoubleValue) serviceContext.getValueService().getValue("wbb12EnergyMeter.energyTotal")).addItemChangeListener(item -> {
            electricPowerTotal.set(item.getValue());
        }, true, () -> { return this != null; });

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