package com.osh.core.service;

import com.osh.core.manager.IMqttSupport;
import com.osh.core.value.ValueBase;

public interface IValueService extends IMqttSupport {

	void registerValue(ValueBase valueBase);

	ValueBase getValue(String fullId);

    void publishValue(ValueBase value);
}
