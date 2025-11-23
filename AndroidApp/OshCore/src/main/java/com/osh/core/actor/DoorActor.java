package com.osh.core.actor;

import com.osh.core.value.ValueGroup;
import com.osh.core.value.ValueType;

public class DoorActor extends ActorBase<DoorActor, Boolean> {

    public DoorActor(ValueGroup valueGroup, String id, ValueType valueType) {
        super(valueGroup, id, valueType);
    }
    @Override
    public boolean cmdSupported(ActorCmds cmd) {
        return false;
    }

    @Override
    protected void _triggerCmd(ActorCmds cmd) {

    }

    @Override
    protected Boolean _updateValue(Object newValue) {
        return null;
    }
}
