package com.osh.ui.dialogs;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.osh.R;
import com.osh.activity.OshApplication;
import com.osh.camera.config.CameraSource;
import com.osh.databinding.FragmentImageDetailsDialogBinding;
import com.osh.databinding.FragmentVideoDetailsDialogBinding;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowVideoDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowVideoDetailsFragment extends DialogFragment {

    FragmentVideoDetailsDialogBinding binding;
    private File videoFile;

    private ExoPlayer player;

    public ShowVideoDetailsFragment() {
        // Required empty public constructor
    }

    public static ShowVideoDetailsFragment newInstance() {
        ShowVideoDetailsFragment fragment = new ShowVideoDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentVideoDetailsDialogBinding.inflate(getLayoutInflater(), container, false);

        Button closeBtn = binding.closeBtn;
        closeBtn.setOnClickListener(view -> {
            dismiss();
        });

        DefaultMediaSourceFactory mediaSourceFactory = new DefaultMediaSourceFactory(getContext());
        MediaSource mediaSource = mediaSourceFactory.createMediaSource(MediaItem.fromUri(videoFile.getPath()));
        player = new ExoPlayer.Builder(getContext()).setMediaSourceFactory(mediaSourceFactory).build();
        player.setMediaSource(mediaSource);
        player.setPlayWhenReady(true);
        player.prepare();

        binding.videoDetails.setPlayer(player);
        binding.videoDetails.setOnClickListener(view -> {
            binding.videoDetails.setUseController(true);
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Window window = getDialog().getWindow();
        if(window == null) return;
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int) getResources().getDimension(R.dimen.image_dialog_width);
        params.height = (int) getResources().getDimension(R.dimen.image_dialog_height);
        window.setAttributes(params);
    }

    public void setVideoFile(File videoFile) {
        this.videoFile = videoFile;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        player.stop();
    }
}