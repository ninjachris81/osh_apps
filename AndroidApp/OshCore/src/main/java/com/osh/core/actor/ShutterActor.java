package com.osh.core.actor;

import com.osh.core.value.ValueGroup;
import com.osh.core.value.ValueType;

public class ShutterActor extends ActorBase<ShutterActor, Integer> {

    public static final int SHUTTER_OPERATION_MODE_AUTO = 0;
    public static final int SHUTTER_OPERATION_MODE_MANUAL = 1;

    public static final int SHUTTER_STATE_OPENED = 0;
    public static final int SHUTTER_STATE_CLOSED = 100;

    public static final int SHUTTER_TILT_OPENED = 100;
    public static final int SHUTTER_TILT_CLOSED = 0;

    private final boolean tiltSupport;
    private final long fullCloseDuration;
    private final long fullTiltDuration;

    public ShutterActor(ValueGroup valueGroup, String id, ValueType valueType, boolean tiltSupport, long fullCloseDuration, long fullTiltDuration) {
        super(valueGroup, id, valueType);
        this.tiltSupport = tiltSupport;
        this.fullCloseDuration = fullCloseDuration;
        this.fullTiltDuration = fullTiltDuration;
    }
    @Override
    public boolean cmdSupported(ActorCmds cmd) {
        switch(cmd) {
            case ACTOR_CMD_STOP:
            case ACTOR_CMD_UP:
            case ACTOR_CMD_DOWN:
            case ACTOR_CMD_SHUTTER_HALF_CLOSE:
            case ACTOR_CMD_SHUTTER_HALF_OPEN:
            case ACTOR_CMD_SHUTTER_FULL_OPEN:
            case ACTOR_CMD_SHUTTER_TURN_OPEN:
            case ACTOR_CMD_SHUTTER_TURN_CLOSE:
                return true;
            default:
                return false;
        }
    }

    @Override
    protected void _triggerCmd(ActorCmds cmd) {

    }

    @Override
    protected Integer _updateValue(Object newValue) {
        if (newValue instanceof Integer) {
            return (Integer) newValue;
        } else {
            return null;
        }
    }

    public boolean isTiltSupport() {
        return tiltSupport;
    }

    public long getFullCloseDuration() {
        return fullCloseDuration;
    }

    public long getFullTiltDuration() {
        return fullTiltDuration;
    }

    public String getCloseAndTiltState() {
        if (this.value == null) return "";

        if (getCloseState() != SHUTTER_STATE_CLOSED && getCloseState() != SHUTTER_STATE_OPENED) return getCloseStateAsString();
        if (isTiltSupport()) {
            if (getTiltState() != SHUTTER_TILT_CLOSED && getTiltState() != SHUTTER_TILT_OPENED) return getTiltStateAsString();
            return getCloseStateAsString() + ", " + getTiltStateAsString();
        } else {
            return getCloseStateAsString();
        }
    }

    public String getCloseStateAsString() {
        if (this.value == null) return "";
        if (getCloseState() == SHUTTER_STATE_OPENED) return "Opened";
        if (getCloseState() == SHUTTER_STATE_CLOSED) return "Closed";
        return getCloseState() + " %";
    }

    public int getCloseState() {
        if (this.value == null) return 0;
        return this.value.shortValue() & 0x00FF;
    }

    public String getTiltStateAsString() {
        if (this.value == null) return "";
        if (!isTiltSupport()) return "";
        if (getTiltState() == SHUTTER_TILT_OPENED) return "Tilted";
        if (getTiltState() == SHUTTER_TILT_CLOSED) return "Closed";
        return "Tilting " + getTiltState() + " %";
    }

    public int getTiltState() {
        return (this.value & 0xFF00) >> 8;
    }

    public DBShutterActor toDBShutterActor() {
        return new DBShutterActor(id, getValueGroup().getId(), tiltSupport, fullCloseDuration, fullTiltDuration);
    }
}
