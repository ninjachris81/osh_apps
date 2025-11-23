package com.osh.ui.components;

import android.content.Context;

import androidx.annotation.NonNull;

import com.osh.core.actor.AudioPlaybackActor;

import java.util.List;

public class AudioPlaybackActorArrayAdapter extends TextSpinnerArrayAdapter {

    public AudioPlaybackActorArrayAdapter(@NonNull Context context, int resource, @NonNull List<AudioPlaybackActor> audioPlaybackActors) {
        super(context, resource);
        for (AudioPlaybackActor actor : audioPlaybackActors) {
            super.add(actor.getAudioName());;
        }
    }

}
