package com.osh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.osh.R;
import com.osh.actor.AudioPlaybackActor;
import com.osh.databinding.ActivityAudioBinding;
import com.osh.databinding.ActivityCameraDetailsBinding;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.log.LogFacade;
import com.osh.service.IServiceContext;
import com.osh.ui.components.ArrayAdapterBase;
import com.osh.ui.components.AudioPlaybackActorArrayAdapter;
import com.osh.ui.components.AudioPlaybackSourceArrayAdapter;
import com.osh.utils.IItemChangeListener;
import com.osh.value.DoubleValue;
import com.osh.value.StringValue;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AudioActivity extends AppCompatActivity {

    @Inject
    IServiceContext serviceContext;

    private ActivityAudioBinding binding;

    private List<AudioPlaybackActor> audioActors;
    private List<AudioPlaybackSource> audioSources;

    private AudioPlaybackActor selectedActor;
    private AudioPlaybackSource selectedSource;

    private AudioPlaybackActorArrayAdapter audioActorArrayAdapter;
    private AudioPlaybackSourceArrayAdapter audioSourceArrayAdapter;
    private IItemChangeListener<AudioPlaybackActor> currentActorChangeListener;
    private IItemChangeListener<DoubleValue> currentVolumeChangeListener;


    private String TAG = getClass().getName();
    private IItemChangeListener<StringValue> currentTitleChangeListener;

    public static void invokeActivity(Context context) {
        Intent intent = new Intent(context, AudioActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAudioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Toolbar myToolbar = binding.myToolbar;
        myToolbar.setTitle("Audio");
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        audioActors = new ArrayList<>();
        for (AudioPlaybackActor audioActor : serviceContext.getAudioActorService().getAudioActors()) {
            if (!StringUtils.isEmpty(audioActor.getAudioName())) {
                audioActors.add(audioActor);

                audioActor.addItemChangeListener(item -> {
                    // TODO
                }, true, () -> { return this != null; });
            }
        }

        audioActorArrayAdapter = new AudioPlaybackActorArrayAdapter(this, R.layout.spinner_dropdown_item, audioActors);

        binding.audioActorsListView.setAdapter(audioActorArrayAdapter);
        binding.audioActorsListView.setOnItemClickListener((adapterView, view, index, l) -> {
            selectAudioActor(audioActors.get(index));
        });


        audioSources = new ArrayList<>(serviceContext.getAudioActorService().getAudioPlaybackSources());

        audioSourceArrayAdapter = new AudioPlaybackSourceArrayAdapter(this, R.layout.spinner_dropdown_item_with_image, audioSources);
        binding.audioSourcesGridView.setAdapter(audioSourceArrayAdapter);
        binding.audioSourcesGridView.setOnItemClickListener((adapterView, view, index, l) -> {
            selectAudioSource(audioSources.get(index));
        });

        binding.playBtn.setOnClickListener(view -> {
            if (selectedActor.getPlaybackState() == AudioPlaybackActor.PLAYBACK_STATE.PLAYING) {
                serviceContext.getAudioActorService().stopPlayback(selectedActor);
            } else {
                serviceContext.getAudioActorService().startPlayback(selectedActor, selectedSource);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*
        if (binding.audioActorsListView.getCount()>0) {
            binding.audioActorsListView.setItemChecked(0, true);
            binding.audioActorsListView.performItemClick(binding.audioActorsListView.getSelectedView(), 0, 0);
        }
         */

        refreshState();
    }

    private void selectAudioActor(AudioPlaybackActor audioPlaybackActor) {
        if (selectedActor != null) {
            selectedActor.removeItemChangeListener(currentActorChangeListener);
        }
        if (currentVolumeChangeListener != null) {
            selectedActor.getAudioVolumeValue().removeItemChangeListener(currentVolumeChangeListener);
        }
        if (currentTitleChangeListener != null) {
            selectedActor.getAudioCurrentTitleValue().removeItemChangeListener(currentTitleChangeListener);
        }

        this.selectedActor = audioPlaybackActor;
        this.selectedSource = serviceContext.getAudioActorService().getCurrentSource(audioPlaybackActor);

        int position = selectedSource == null ? -1 : audioSources.indexOf(selectedSource);
        if (position >= 0) {
            binding.audioSourcesGridView.performItemClick(
                    binding.audioSourcesGridView.getAdapter().getView(position, null, null), position, position);
        }

        binding.audioSourcesGridView.requestFocusFromTouch();
        binding.audioSourcesGridView.setSelection(position);

        this.currentActorChangeListener = selectedActor.addItemChangeListener(item -> {
            refreshState();
        }, true, () -> { return this != null; });

        this.currentVolumeChangeListener = selectedActor.getAudioVolumeValue().addItemChangeListener(item -> {
            binding.audioVolume.setValue(item.getValue(0.0).floatValue());
        }, true, () -> { return this != null; });

        binding.audioVolume.addOnChangeListener((slider, value, fromUser) -> {
            if (fromUser) {
                serviceContext.getAudioActorService().updateAudioVolume(selectedActor, value);
            }
        });

        this.currentTitleChangeListener = selectedActor.getAudioCurrentTitleValue().addItemChangeListener(item -> {
            binding.currentTitle.setText(item.getValue("No title"));
        }, true, () -> { return this != null; });

        refreshState();
    }

    private void selectAudioSource(AudioPlaybackSource audioPlaybackSource) {
        this.selectedSource = audioPlaybackSource;
        refreshState();
    }

    private void refreshState() {
        runOnUiThread(() -> {
            if (selectedActor == null) {
                binding.playBtn.setEnabled(false);
            } else if (selectedActor.getPlaybackState() != AudioPlaybackActor.PLAYBACK_STATE.PLAYING && selectedSource != null) {
                binding.playBtn.setEnabled(true);
            } else if (selectedActor.getPlaybackState() == AudioPlaybackActor.PLAYBACK_STATE.PLAYING) {
                binding.playBtn.setEnabled(true);
            } else {
                binding.playBtn.setEnabled(false);
            }
            if (selectedActor != null) {
                if (selectedActor.getPlaybackState() == AudioPlaybackActor.PLAYBACK_STATE.PLAYING) {
                    binding.playBtn.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_stop_circle_outline));
                } else {
                    binding.playBtn.setIcon(AppCompatResources.getDrawable(this, R.drawable.ic_play_circle_outline));
                }
            }
        });
    }

}