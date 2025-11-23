package com.osh.wbb12.service;

import com.osh.core.manager.IMqttSupport;
import com.osh.core.utils.ObservableBoolean;
import com.osh.core.value.ValueBase;
import com.osh.wbb12.WBB12_Holding_Registers;
import com.osh.wbb12.WBB12_Input_Registers;
import com.osh.wbb12.service.impl.WBB12ServiceImpl;

import java.util.Set;

public interface IWBB12Service extends IMqttSupport {
    Set<String> getWBB12Keys();

    ValueBase getWBB12Value(String fullId);
    ValueBase getWBB12Value(WBB12_Holding_Registers reg);
    ValueBase getWBB12Value(WBB12_Input_Registers reg);

    WBB12ServiceImpl.WBB12Format getWBB12InputFormat(String fullId);

    ObservableBoolean errorState();

    void addWarmwaterPush(int minutes);
}
