package com.osh.value.enums;

import androidx.annotation.NonNull;

import org.apache.commons.text.CaseUtils;

public enum HomeConnectOperationalState {
    UNKNOWN,
    INACTIVE,
    READY,
    DELAYED_START,
    RUN,
    PAUSE,
    ACTION_REQUIRED,
    FINISHED,
    ERROR,
    ABORTING;


    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case ACTION_REQUIRED:
                return "Action";
            case DELAYED_START:
                return "Delayed";
            default:
                return CaseUtils.toCamelCase(this.name().replaceAll("_", " "), true);
        }
    }
}
