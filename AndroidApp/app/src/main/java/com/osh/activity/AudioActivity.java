package com.osh.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.osh.R;
import com.osh.actor.AudioPlaybackActor;
import com.osh.databinding.ActivityAudioBinding;
import com.osh.databinding.ActivityCameraDetailsBinding;
import com.osh.service.IServiceContext;
import com.osh.ui.components.ArrayAdapterBase;
import com.osh.ui.components.AudioPlaybackActorArrayAdapter;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AudioActivity extends AppCompatActivity {

    @Inject
    IServiceContext serviceContext;

    private ActivityAudioBinding binding;

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

        List<AudioPlaybackActor> audioActors = new ArrayList<>();
        List<String> audioActorLabels = new ArrayList<>();
        for (AudioPlaybackActor audioActor : serviceContext.getAudioActorService().getAudioActors()) {
            if (!StringUtils.isEmpty(audioActor.getAudioName())) {
                audioActorLabels.add(audioActor.getAudioName());
                audioActors.add(audioActor);
            }
        }

        ArrayAdapter<String> audioActorArrayAdapter = new ArrayAdapter<>(this, R.layout.spinner_dropdown_item, audioActorLabels);

        binding.audioActorsListView.setAdapter(audioActorArrayAdapter);
        binding.audioActorsListView.setOnItemClickListener((adapterView, view, index, l) -> {
            selectAudioActor(audioActors.get(index));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (binding.audioActorsListView.getCount()>0) {
            binding.audioActorsListView.setItemChecked(0, true);
            binding.audioActorsListView.performItemClick(binding.audioActorsListView.getSelectedView(), 0, 0);
        }
    }

    private void selectAudioActor(AudioPlaybackActor audioPlaybackActor) {

    }

}