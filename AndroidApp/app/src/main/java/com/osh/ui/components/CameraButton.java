package com.osh.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;
import androidx.media3.ui.PlayerView;

import com.osh.R;
import com.osh.camera.config.CameraSource;

public class CameraButton extends LinearLayout {

    private ExoPlayer player;
    private MediaSource mediaSource;

    private Drawable mIcon;

    public CameraButton(Context context) {
        super(context);
        init(context, null, 0);
    }

    public CameraButton(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public CameraButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    @OptIn(markerClass = UnstableApi.class)
    private void init(Context context, AttributeSet attrs, int defStyle) {

        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CameraButton, defStyle, 0);

        String mVideoUrl = a.getString(R.styleable.CameraButton_videoUrl);

        String mLabelTitle = a.getString(R.styleable.CameraButton_buttonLabelTitle);
        float mLabelSize = a.getDimension(R.styleable.CameraButton_buttonLabelSize, 16);
        int mLabelColor = a.getColor(R.styleable.CameraButton_buttonLabelColor, -1);

        if (a.hasValue(R.styleable.CameraButton_buttonIcon)) {
            mIcon = a.getDrawable(
                    R.styleable.CameraButton_buttonIcon);
            mIcon.setCallback(this);
        }

        int mIconColor = a.getColor(R.styleable.CameraButton_buttonIconColor, -1);
        float mIconSize = a.getDimension(R.styleable.CameraButton_buttonIconSize, 70);

        View.inflate(context, R.layout.camera_button, this);

        TextView labelView = findViewById(R.id.cameraLabelTitle);
        labelView.setText(mLabelTitle);
        labelView.setTextColor(mLabelColor);
        labelView.setTextSize(mLabelSize);

        ImageView iconView = findViewById(R.id.cameraIcon);
        iconView.setImageDrawable(mIcon);
        iconView.setColorFilter(mIconColor);

        iconView.getLayoutParams().height = (int) mIconSize;
        iconView.getLayoutParams().width = (int) mIconSize;
        iconView.requestLayout();

        PlayerView videoSurface = findViewById(R.id.video_surface);
        player = new ExoPlayer.Builder(getContext()).build();
        videoSurface.setPlayer(player);
        videoSurface.setOnClickListener(view -> {
            videoSurface.setUseController(true);
        });

        player.setPlayWhenReady(true);
        player.prepare();
        setVideoUrl(mVideoUrl);

        a.recycle();
    }

    @OptIn(markerClass = UnstableApi.class)
    public void setVideoUrl(String videoUrl) {

        if (videoUrl != null) {
            mediaSource = new RtspMediaSource.Factory()
                    .createMediaSource(MediaItem.fromUri(videoUrl));
            player.setMediaSource(mediaSource);
        }
    }

    public void setVideoEnable(boolean enable) {
        if (enable) {
            player.play();
        } else {
            player.pause();
        }
    }
}
