package com.osh.ui.camera;

import android.graphics.drawable.Drawable;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class CameraImageContent {

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
    }

    /**
     * A placeholder item representing a piece of content.
     */
    public static class ThumbnailImageItem {
        static final String INPUT_FORMAT = "yyMMddHHmmss";
        static final DateTimeFormatter inputFormat = DateTimeFormatter.ofPattern(INPUT_FORMAT);
        static final DateTimeFormatter targetFormat = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

        public final String name;
        public final Drawable imageContent;
        public final String details;
        public String folder;

        public String getDateString() {
            String returnString = name;
            if (name.startsWith("A")) returnString = returnString.substring(1);

            returnString = returnString.substring(0, INPUT_FORMAT.length());
            return LocalDateTime.parse(returnString, inputFormat).format(targetFormat);
        }

        public ThumbnailImageItem(String folder, String name, Drawable imageContent, String details) {
            this.folder = folder;
            this.name = name;
            this.imageContent = imageContent;
            this.details = details;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}