package com.osh.core.service;

import com.osh.core.datamodel.meta.KnownRoom;
import com.osh.core.manager.IMqttSupport;
import com.osh.core.value.ValueBase;
import com.osh.core.value.ValueType;

import java.util.List;

public interface IValueService extends IMqttSupport {

	void registerValue(ValueBase valueBase);

	ValueBase getValue(String fullId);

    void publishValue(ValueBase value);

    List<ValueBase> getValues(KnownRoom room, ValueType valueType);
}
