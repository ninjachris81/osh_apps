package com.osh.actor;

import androidx.room.util.StringUtil;

import com.osh.utils.ObservableDouble;
import com.osh.utils.ObservableString;
import com.osh.value.DoubleValue;
import com.osh.value.IntegerValue;
import com.osh.value.StringValue;
import com.osh.value.ValueGroup;
import com.osh.value.ValueType;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

public class AudioPlaybackActor extends ActorBase<AudioPlaybackActor, Integer> {

	private final String audioDeviceIds;

	private final String audioActivationRelayId;

	private final float initialAudioVolume;
	private DoubleValue audioVolumeValue;
	private final String audioVolumeId;

	private final String initialAudioUrl;
	private StringValue audioUrlValue;
	private final String audioUrlId;

	private StringValue audioCurrentTitleValue;
	private final String audioCurrentTitleId;

	private final String audioName;

	@Override
	protected Integer _updateValue(Object newValue) {
		if (newValue instanceof Integer) {
			return (Integer) newValue;
		} else {
			return null;
		}
	}

	public enum PLAYBACK_STATE {
		UNKNOWN(-1),
		STOPPED(0),
		PLAYING(1),
		PAUSED(2),
		ERROR(10);

		private final int value;

		PLAYBACK_STATE(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static PLAYBACK_STATE getPlaybackState(int value) {
			for (PLAYBACK_STATE state : PLAYBACK_STATE.values()) {
				if (state.getValue() == value) return state;
			}

			return UNKNOWN;
		}
	}

	public AudioPlaybackActor(ValueGroup valueGroup, String id, ValueType valueType, String audioDeviceIds, String audioActivationRelayId, float audioVolume, String audioVolumeId, String audioUrl, String audioUrlId, String audioCurrentTitleId, String audioName) {
		super(valueGroup, id, valueType);
		this.audioDeviceIds = audioDeviceIds;
		this.audioActivationRelayId = audioActivationRelayId;

		this.initialAudioVolume = audioVolume;
		this.audioVolumeId = audioVolumeId;

		this.initialAudioUrl = audioUrl;
		this.audioUrlId = audioUrlId;

		this.audioCurrentTitleId = audioCurrentTitleId;

		this.audioName = audioName;
	}

	@Override
	public boolean cmdSupported(ActorCmds cmd) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void _triggerCmd(ActorCmds cmd) {
		// TODO Auto-generated method stub
		
	}

	public PLAYBACK_STATE getPlaybackState() {
		return PLAYBACK_STATE.getPlaybackState(this.getValue(-1));
	}

	public boolean isDynamic() {
		return audioUrlId != null;
	}

	public boolean isMusicActor() {
		return isDynamic() && !StringUtils.isEmpty(audioName) && StringUtils.isEmpty(audioVolumeId);
	}

	public String getAudioDeviceIds() {
		return audioDeviceIds;
	}

	public String getAudioActivationRelayId() {
		return audioActivationRelayId;
	}

	public String getAudioVolumeId() {
		return audioVolumeId;
	}

	public String getAudioUrlId() {
		return audioUrlId;
	}

	public DoubleValue getAudioVolumeValue() {
		return audioVolumeValue;
	}

	public StringValue getAudioUrlValue() {
		return audioUrlValue;
	}

	public StringValue getAudioCurrentTitleValue() {
		return audioCurrentTitleValue;
	}

	public String getAudioCurrentTitleId() {
		return audioCurrentTitleId;
	}

	public String getAudioName() {
		return audioName;
	}

	public void setAudioCurrentTitleValue(StringValue audioCurrentTitleValue) {
		this.audioCurrentTitleValue = audioCurrentTitleValue;
	}

	public void setVolumeValue(DoubleValue volume) {
		this.audioVolumeValue = volume;
		this.audioVolumeValue.updateValue(initialAudioVolume);
	}

	public void setUrlValue(StringValue url) {
		this.audioUrlValue = url;
		this.audioUrlValue.updateValue(initialAudioUrl);
	}

	public DBAudioActor toDBAudioActor() {
		return new DBAudioActor(id, getValueGroup().getId(), audioDeviceIds, audioActivationRelayId, initialAudioVolume, audioVolumeId, initialAudioUrl, audioUrlId, audioCurrentTitleId, audioName);
	}
}
