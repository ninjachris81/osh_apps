package com.osh.datamodel.meta;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

import java.sql.Blob;

@DatabaseTable(tableName = AudioPlaybackSource.TABLE_NAME)
@Entity(tableName = AudioPlaybackSource.TABLE_NAME)
public class AudioPlaybackSource {

    @Ignore
    public static final String TABLE_NAME = "dm_audio_playback_sources";

    @DatabaseField(id = true)
    @PrimaryKey
    @NotNull
    @ColumnInfo(name = "id")
    public String id;

    @DatabaseField(canBeNull = false)
    @ColumnInfo(name = "name")
    @NotNull
    public String name;

    @DatabaseField(canBeNull = false, columnName = "source_url")
    @ColumnInfo(name = "source_url")
    @NotNull
    public String sourceUrl;

    @DatabaseField(dataType = DataType.BYTE_ARRAY, columnName = "image_icon")
    @ColumnInfo(name = "image_icon")
    public byte[] imageIcon;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public Bitmap getImage() {
        if (imageIcon.length > 0) {
            return BitmapFactory.decodeByteArray(imageIcon, 0, imageIcon.length);
        } else {
            return null;
        }
    }

}
