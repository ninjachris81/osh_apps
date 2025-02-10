package com.osh.actor;

import com.osh.utils.ObservableDouble;
import com.osh.utils.ObservableString;
import com.osh.value.DoubleValue;
import com.osh.value.StringValue;
import com.osh.value.ValueGroup;
import com.osh.value.ValueType;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

public class AudioPlaybackActor extends ActorBase {

	private final String audioDeviceIds;

	private final String audioActivationRelayId;

	private final ObservableDouble audioVolume;
	private final float initialAudioVolume;
	private DoubleValue audioVolumeValue;
	private final String audioVolumeId;

	private final String initialAudioUrl;
	private final ObservableString audioUrl;
	private StringValue audioUrlValue;
	private final String audioUrlId;

	private StringValue audioCurrentTitleValue;

	private final String audioCurrentTitleId;
	private final ObservableString audioCurrentTitle;

	private final String audioName;

	public AudioPlaybackActor(ValueGroup valueGroup, String id, ValueType valueType, String audioDeviceIds, String audioActivationRelayId, float audioVolume, String audioVolumeId, String audioUrl, String audioUrlId, String audioCurrentTitleId, String audioName) {
		super(valueGroup, id, valueType);
		this.audioDeviceIds = audioDeviceIds;
		this.audioActivationRelayId = audioActivationRelayId;

		this.initialAudioVolume = audioVolume;
		this.audioVolume = new ObservableDouble((double) audioVolume);
		this.audioVolumeId = audioVolumeId;

		this.initialAudioUrl = audioUrl;
		this.audioUrl = new ObservableString(audioUrl);
		this.audioUrlId = audioUrlId;

		this.audioCurrentTitleId = audioCurrentTitleId;
		this.audioCurrentTitle = new ObservableString("");

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

	@Override
	protected Object _updateValue(Object newValue) {
		return newValue;
	}

	public String getAudioDeviceIds() {
		return audioDeviceIds;
	}

	public String getAudioActivationRelayId() {
		return audioActivationRelayId;
	}

	public ObservableDouble getAudioVolume() {
		return audioVolume;
	}

	public String getAudioVolumeId() {
		return audioVolumeId;
	}

	public ObservableString getAudioUrl() {
		return audioUrl;
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
		audioCurrentTitleValue.addItemChangeListener(item -> {
			audioCurrentTitle.changeValue(item.getValue());
		}, true);
	}

	public void setVolumeValue(DoubleValue volume) {
		this.audioVolumeValue = volume;
		audioVolumeValue.addItemChangeListener(item -> {
			audioVolume.changeValue(item.getValue());
		}, true);
	}

	public void setUrlValue(StringValue url) {
		this.audioUrlValue = url;
		audioUrlValue.addItemChangeListener(item -> {
			audioUrl.changeValue(item.getValue());
		}, true);
	}

	public DBAudioActor toDBAudioActor() {
		return new DBAudioActor(id, getValueGroup().getId(), audioDeviceIds, audioActivationRelayId, initialAudioVolume, audioVolumeId, initialAudioUrl, audioUrlId, audioCurrentTitleId, audioName);
	}
}
