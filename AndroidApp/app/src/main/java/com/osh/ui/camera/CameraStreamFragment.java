package com.osh.ui.camera;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;

import com.osh.camera.config.CameraSource;
import com.osh.databinding.FragmentCameraDetailsBinding;

/**
 * A placeholder fragment containing a simple view.
 */
public class CameraStreamFragment extends Fragment {

    private FragmentCameraDetailsBinding binding;

    private ExoPlayer player;
    private CameraSource cameraSource;

    public CameraStreamFragment() {

    }

    public CameraStreamFragment(CameraSource cameraSource) {
        this.cameraSource = cameraSource;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        binding = FragmentCameraDetailsBinding.inflate(inflater, container, false);

        player = new ExoPlayer.Builder(getContext()).build();
        binding.videoSurface.setPlayer(player);
        binding.videoSurface.setOnClickListener(view -> {
            binding.videoSurface.setUseController(true);
        });

        MediaSource mediaSource = new RtspMediaSource.Factory()
                .createMediaSource(MediaItem.fromUri(cameraSource.getStreamUri()));
        player.setMediaSource(mediaSource);
        player.setPlayWhenReady(true);
        player.prepare();


        View root = binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        player.stop();
    }
}