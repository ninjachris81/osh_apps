package com.osh.actor;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.jetbrains.annotations.NotNull;

@DatabaseTable(tableName = DBAudioActor.TABLE_NAME)
@Entity(tableName = DBAudioActor.TABLE_NAME, primaryKeys = {"id", "value_group_id"})
public class DBAudioActor {

    @Ignore
    public static final String TABLE_NAME = "dm_actors_audio";

    @DatabaseField(uniqueCombo = true)
    @ColumnInfo(name = "id")
    @NotNull
    public String id;

    @DatabaseField(uniqueCombo = true, columnName = "value_group_id")
    @ColumnInfo(name = "value_group_id")
    @NotNull
    public String valueGroupId;

    @DatabaseField(columnName = "audio_device_ids")
    @ColumnInfo(name = "audio_device_ids")
    public String audioDeviceIds;

    @DatabaseField(columnName = "audio_activation_relay_id")
    @ColumnInfo(name = "audio_activation_relay_id")
    public String audioActivationRelayId;

    @DatabaseField(columnName = "audio_volume")
    @ColumnInfo(name = "audio_volume")
    public float audioVolume;

    @DatabaseField(columnName = "audio_volume_id")
    @ColumnInfo(name = "audio_volume_id")
    public String audioVolumeId;

    @DatabaseField(columnName = "audio_url")
    @ColumnInfo(name = "audio_url")
    public String audioUrl;

    @DatabaseField(columnName = "audio_url_id")
    @ColumnInfo(name = "audio_url_id")
    public String audioUrlId;

    @DatabaseField(columnName = "audio_current_title_id")
    @ColumnInfo(name = "audio_current_title_id")
    public String audioCurrentTitleId;

    public DBAudioActor() {
    }

    public DBAudioActor(@NotNull String id, @NotNull String valueGroupId, String audioDeviceIds, String audioActivationRelayId, float audioVolume, String audioVolumeId, String audioUrl, String audioUrlId, String audioCurrentTitleId) {
        this.id = id;
        this.valueGroupId = valueGroupId;
        this.audioDeviceIds = audioDeviceIds;
        this.audioActivationRelayId = audioActivationRelayId;
        this.audioVolume = audioVolume;
        this.audioVolumeId = audioVolumeId;
        this.audioUrl = audioUrl;
        this.audioUrlId = audioUrlId;
        this.audioCurrentTitleId = audioCurrentTitleId;
    }

    public String getId() {
        return id;
    }

    public String getValueGroupId() {
        return valueGroupId;
    }

    public String getAudioDeviceIds() {
        return audioDeviceIds;
    }

    public String getAudioActivationRelayId() {
        return audioActivationRelayId;
    }

    public float getAudioVolume() {
        return audioVolume;
    }

    public String getAudioVolumeId() {
        return audioVolumeId;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public String getAudioUrlId() {
        return audioUrlId;
    }

    public String getAudioCurrentTitleId() {
        return audioCurrentTitleId;
    }
}
