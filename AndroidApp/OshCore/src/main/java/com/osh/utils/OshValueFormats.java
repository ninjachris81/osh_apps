package com.osh.utils;

import com.osh.value.ValueBase;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class OshValueFormats {


    public static String formatTime(long ms) {
        if (ms < 60000) {
            return Float.valueOf(ms / 1000).intValue() + " sec";
        } else if (ms < 3600000) {
            return Float.valueOf(ms / 60000).intValue() + " min";
        } else {
            return DurationFormatUtils.formatDuration(ms, "[d'd' HH:]mm");
        }
    }

    public static String format(ValueBase value) {
        // TODO
        return "";
    }



}
