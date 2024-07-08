package com.osh.actor;

import com.osh.value.ValueBase;
import com.osh.value.ValueGroup;
import com.osh.value.ValueType;

import java.util.Map;

public abstract class ActorBase<VALUE_TYPE extends ActorBase, NATIVE_TYPE> extends ValueBase<VALUE_TYPE, NATIVE_TYPE> {

	protected Map<String, Object> config;
	public abstract boolean cmdSupported(ActorCmds cmd);
    protected abstract void _triggerCmd(ActorCmds cmd);

	public ActorBase(ValueGroup valueGroup, String id, ValueType valueType) {
		super(valueGroup, id, valueType);
	}

	public ActorBase() {
	}
	
	public void triggerCmd(ActorCmds cmd, String reason) {
	    if (cmdSupported(cmd)) {
	        _triggerCmd(cmd);
	    }
	}

	public DBActor toDBActor() {
		if (this instanceof DigitalActor) {
			return new DBActor(id, this.getClass().getSimpleName(), getValueGroup().getId(), getValueType().getValue(), getValueTimeout(), ((DigitalActor) this).isAsync);
		} else if (this instanceof  DoorActor) {
			return new DBActor(id, this.getClass().getSimpleName(), getValueGroup().getId(), getValueType().getValue(), getValueTimeout(), false);
		} else if (this instanceof  ToggleActor) {
			return new DBActor(id, this.getClass().getSimpleName(), getValueGroup().getId(), getValueType().getValue(), getValueTimeout(), false);
		} else if (this instanceof  ValueActor) {
			return new DBActor(id, this.getClass().getSimpleName(), getValueGroup().getId(), getValueType().getValue(), getValueTimeout(), false);
		} else if (this instanceof  ShutterActor) {
			return new DBActor(id, this.getClass().getSimpleName(), getValueGroup().getId(), getValueType().getValue(), getValueTimeout(), false);
		} else if (this instanceof  AudioPlaybackActor) {
			return new DBActor(id, this.getClass().getSimpleName(), getValueGroup().getId(), getValueType().getValue(), getValueTimeout(), false);
		} else {
			throw new RuntimeException("Unsupported type");
		}
	}
}
