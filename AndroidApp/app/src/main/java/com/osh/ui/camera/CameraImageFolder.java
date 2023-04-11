package com.osh.ui.camera;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class CameraImageFolder {

    static final DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern("yyyyMMdd");
    static final DateTimeFormatter targetFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    private String name;

    public CameraImageFolder(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        LocalDate date = LocalDate.parse(name, inputFormat);
        if (date.equals(LocalDate.now())) {
            return "Today";
        } else {
            return date.format(targetFormat);
        }
    }

    public String getName() {
        return name;
    }
}
