package com.osh.core.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.osh.core.actor.ActorBase;
import com.osh.core.communication.MessageBase;
import com.osh.core.communication.MessageBase.MESSAGE_TYPE;
import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.datamodel.meta.KnownRoom;
import com.osh.core.service.ICommunicationService;
import com.osh.core.service.IValueService;
import com.osh.core.utils.IObservableManager;
import com.osh.core.utils.ObservableManagerImpl;
import com.osh.core.value.ValueBase;
import com.osh.core.value.ValueMessage;
import com.osh.core.value.ValueType;

public class ValueServiceImpl implements IValueService {
	
	private static final Logger log = LoggerFactory.getLogger(ValueServiceImpl.class);

	protected final Map<String, ValueBase> values = new HashMap<>();

	protected final IObservableManager observableManager = new ObservableManagerImpl();

	protected final ICommunicationService communicationService;

	public ValueServiceImpl(ICommunicationService communicationService) {
		this.communicationService = communicationService;
		communicationService.registerMessageType(getMessageType(), this);
	}

	@Override
	public MESSAGE_TYPE getMessageType() {
		return MESSAGE_TYPE.MESSAGE_TYPE_VALUE;
	}

	@Override
	public void handleReceivedMessage(MessageBase msg) {
		if (msg instanceof ValueMessage) {
			log.info("Value received {}", msg);
			
			valueReceived(((ValueMessage)msg).getValueGroupId(), ((ValueMessage)msg).getValueId(), ((ValueMessage)msg).getRawValue());
		}
	}

	private void valueReceived(String valueGroupId, String valueId, Object newValue) {
	    ValueBase value = getValue(valueGroupId, valueId);
	    if (value != null) {
	        valueReceived(value, newValue);
	    } else {
	        log.warn("Value not registered {}.{}", valueGroupId, valueId);
	    }
	}

	private void valueReceived(ValueBase value, Object newValue) {
	    if (value != null) {
	        value.updateValue(newValue);
	        if (value.isPersistValue()) {
	        	// TODO
	            //m_databaseManager->simpleSet(VALUE_PREFIX, value->fullId(), value->rawValue());
	        }
	    }
	}

	private ValueBase getValue(String valueGroupId, String valueId) {
	    return getValue(ValueBase.getFullId(valueGroupId, valueId));
	}

	@Override
	public ValueBase getValue(String fullId) {
	    if (values.containsKey(fullId)) {
	        return values.get(fullId);
	    } else {
	        return null;
	    }
	}

	@Override
	public void registerValue(ValueBase value) {
		values.put(value.getFullId(), value);
	}

	@Override
	public void publishValue(ValueBase value) {
		ValueMessage msg = new ValueMessage(value);
		communicationService.sendMessage(msg);

	}

    @Override
    public List<ValueBase> getValues(KnownRoom knownRoom, ValueType valueType) {
        return knownRoom.getValues().values().stream().filter(a -> a.getKnownRoom() != null && Objects.equals(knownRoom.getId(), a.getKnownRoom().getId()) && (valueType != ValueType.VT_UNKNOWN && a.getValueType() == valueType)).collect(Collectors.toList());
    }

	@Override
	public List<ValueBase> getValues(KnownArea area, ValueType valueType) {
		return values.values().stream().filter(a -> a.getKnownRoom() != null &&  a.getKnownRoom().getKnownArea() != null && Objects.equals(a.getKnownRoom().getKnownArea().getId(), area.getId()) && (valueType != ValueType.VT_UNKNOWN && a.getValueType() == valueType) ).collect(Collectors.toList());
	}

}
