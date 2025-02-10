package com.osh.activity;

import android.app.ComponentCaller;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.media3.common.MediaItem;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.rtsp.RtspMediaSource;
import androidx.media3.exoplayer.source.MediaSource;

import com.osh.R;
import com.osh.camera.config.CameraSource;
import com.osh.databinding.ActivitySipCallBinding;

import net.gotev.sipservice.SipServiceCommand;

import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.OnCallMediaEventParam;
import org.pjsip.pjsua2.StreamInfo;
import org.pjsip.pjsua2.VideoWindow;
import org.pjsip.pjsua2.VideoWindowHandle;
import org.pjsip.pjsua2.VideoWindowInfo;
import org.pjsip.pjsua2.pjmedia_event_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_status_code;

class VideoSurfaceHandler implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private VideoWindow videoWindow = null;
    private boolean active = false;

    public VideoSurfaceHandler(SurfaceHolder holder_) {
        holder = holder_;
    }

    public void setVideoWindow(VideoWindow vw) {
        videoWindow = vw;
        active = true;
        setSurfaceHolder(holder);
    }

    public void resetVideoWindow() {
        active = false;
        videoWindow = null;
    }

    private void setSurfaceHolder(SurfaceHolder holder) {
        if (!active) return;

        try {
            VideoWindowHandle wh = new VideoWindowHandle();
            wh.getHandle().setWindow(holder != null? holder.getSurface() : null);
            videoWindow.setWindow(wh);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        setSurfaceHolder(holder);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) { }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        setSurfaceHolder(null);
    }
}

public class SipCallActivity extends AppCompatActivity implements Handler.Callback {
    private static final String TAG = "SipCallActivity";

    private ActivitySipCallBinding binding;

    private VideoSurfaceHandler localVideoHandler;
    private VideoSurfaceHandler remoteVideoHandler;

    private static CallInfo lastCallInfo;

    private String mAccountID;
    private String mDisplayName;
    private int mCallID;
    private boolean mIsVideo;
    private String mNumber;
    private boolean micMute;

    private ToneGenerator ringToneGenerator;
    private ExoPlayer player;
    private Drawable micOnIcon;
    private Drawable micOffIcon;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySipCallBinding.inflate(getLayoutInflater());

        ringToneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, getIntent().getIntExtra("ringVolume", 80));

        SurfaceView surfaceInVideo = binding.surfaceIncomingVideo;
        SurfaceView surfacePreview = binding.surfaceIncomingVideo;

        /* Avoid visible black boxes (blank video views) initially */
        if (MainActivity.currentCall == null ||
                MainActivity.currentCall.vidWin == null)
        {
            surfaceInVideo.setVisibility(View.GONE);
            surfacePreview.setVisibility(View.GONE);
        }

        localVideoHandler = new VideoSurfaceHandler(surfacePreview.getHolder());
        remoteVideoHandler = new VideoSurfaceHandler(surfaceInVideo.getHolder());
        surfaceInVideo.getHolder().addCallback(remoteVideoHandler);
        surfacePreview.getHolder().addCallback(localVideoHandler);

        if (MainActivity.currentCall != null) {
            try {
                lastCallInfo = MainActivity.currentCall.getInfo();
                updateCallState(lastCallInfo);
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            updateCallState(lastCallInfo);
        }

        /*
        binding.buttonAccept.setOnClickListener(v -> {
            acceptCall(v);
        });

        binding.buttonHangup.setOnClickListener(v -> {
            hangupCall(v);
        });

        binding.btnCancel.setOnClickListener(v -> {
            hangupCall(v);
        });
         */

        binding.btnMuteMic.setOnClickListener(v -> {
            micMute = !micMute;
            SipServiceCommand.setCallMute(this, mAccountID, mCallID, micMute);
            binding.btnMuteMic.setIcon(micMute ? micOffIcon : micOnIcon);
            binding.btnMuteMic.setText(micMute ? "Muted" : "Mute");
        });
        binding.btnHangUp.setOnClickListener(v -> {
            hangupCall(v);
        });
        binding.btnSwitchCamera.setOnClickListener(v -> {
            // TODO
        });

        binding.unlockDoor.setOnClickListener(view -> {
            SipServiceCommand.hangUpActiveCalls(this, mAccountID);
            DoorOpenActivity.invokeActivity(view.getContext(), DoorOpenActivity.FRONT_DOOR_ID);
        });

        setContentView(binding.getRoot());

        initData();

        player = new ExoPlayer.Builder(this).build();
        binding.videoSurface.setPlayer(player);

        CameraSource cameraSource = ((OshApplication) getApplication()).getApplicationConfig().getCamera().getCameraSource("frontDoor.door");

        MediaSource mediaSource = new RtspMediaSource.Factory()
                        .createMediaSource(MediaItem.fromUri(cameraSource.getStreamUri()));
        player.setMediaSource(mediaSource);
        player.setPlayWhenReady(true);
        player.prepare();
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        super.onNewIntent(newIntent);

        if (newIntent.getIntExtra("msgType", 0) == MainActivity.MSG_TYPE.CALL_STATE) {
            int state = newIntent.getIntExtra("state", 0);

            if (state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                try {
                    ringToneGenerator.stopTone();
                    finish();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    public void acceptCall(View view)
    {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            MainActivity.currentCall.answer(prm);
        } catch (Exception e) {
            System.out.println(e);
        }

        view.setVisibility(View.GONE);
    }

    public void hangupCall(View view)
    {
        localVideoHandler.resetVideoWindow();
        remoteVideoHandler.resetVideoWindow();

        ringToneGenerator.stopTone();

        if (MainActivity.currentCall != null) {
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
            try {
                MainActivity.currentCall.hangup(prm);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void initData() {
        if (getIntent().hasExtra("msgType")) {
            switch(getIntent().getIntExtra("msgType", 0)) {
                case MainActivity.MSG_TYPE.CALL_MEDIA_EVENT:
                    handleCallMediaEvent(getIntent().getIntExtra("evType", 0), getIntent().getLongExtra("medIdx", 0));
                    break;
                case MainActivity.MSG_TYPE.CALL_STATE:
                    handleCallStateEvent(getIntent().getIntExtra("state", 0));
                    break;
                case MainActivity.MSG_TYPE.CALL_MEDIA_STATE:
                    handleCallMediaState();
                    break;
            }
        }

        /*
        mAccountID = getIntent().getStringExtra("accountID");
        mCallID = getIntent().getIntExtra("callID", -1);
        mType = getIntent().getIntExtra("type", -1);
        mDisplayName = getIntent().getStringExtra("displayName");
        mRemoteUri = getIntent().getStringExtra("remoteUri");
        mNumber = getIntent().getStringExtra("number");
        mIsVideo = getIntent().getBooleanExtra("isVideo", false);
        mIsVideoConference = getIntent().getBooleanExtra("isVideoConference", false);
         */

        //showLayout(mType);
        //binding.textViewPeer.setText(String.format("%s", mDisplayName));
        //binding.tvOutCallInfo.setText(String.format("You are calling %s", mNumber));

        micOnIcon = AppCompatResources.getDrawable(this, R.drawable.ic_microphone);
        micOffIcon = AppCompatResources.getDrawable(this, R.drawable.ic_microphone_off);

        binding.btnMuteMic.setIcon(micOnIcon);
        binding.btnSwitchCamera.setVisibility(mIsVideo ? View.VISIBLE : View.GONE);

        /*
        SurfaceHolder holder = binding.svLocal.getHolder();
        holder.addCallback(this);

        binding.svRemote.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                SipServiceCommand.setupIncomingVideoFeed(SipCallActivity.this, mAccountID, mCallID, surfaceHolder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                SipServiceCommand.setupIncomingVideoFeed(SipCallActivity.this, mAccountID, mCallID, null);
            }
        });*/
    }

    /*
    private void showLayout(int type) {
        if (type == TYPE_INCOMING_CALL) {
            binding.layoutIncomingCall.setVisibility(View.VISIBLE);
            binding.layoutOutCall.setVisibility(View.GONE);
            binding.layoutConnected.setVisibility(View.GONE);
            ringToneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);
        } else if (type == TYPE_OUT_CALL) {
            binding.layoutIncomingCall.setVisibility(View.GONE);
            binding.layoutOutCall.setVisibility(View.VISIBLE);
            binding.layoutConnected.setVisibility(View.GONE);
            ringToneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);
        } else if (type == TYPE_CALL_CONNECTED) {
            binding.layoutIncomingCall.setVisibility(View.GONE);
            binding.layoutOutCall.setVisibility(View.GONE);
            binding.layoutConnected.setVisibility(View.VISIBLE);
            ringToneGenerator.stopTone();
        } else {
            TextView textView = new TextView(this);
            textView.setText("ERROR~~~~~~~~~~~~~");
            binding.parent.addView(textView);
        }
    }
     */

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mReceiver.unregister(this);
        player.stop();
    }

    /*
    public BroadcastEventReceiver mReceiver = new BroadcastEventReceiver() {

        @Override
        public void onIncomingCall(String accountID, int callID, String displayName, String remoteUri, boolean isVideo) {
            super.onIncomingCall(accountID, callID, displayName, remoteUri, isVideo);
            Toast.makeText(getReceiverContext(), String.format("Incoming Call From [%s]", remoteUri), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallState(String accountID, int callID, int callStateCode, int callStatusCode, long connectTimestamp) {
            super.onCallState(accountID, callID, callStateCode, callStatusCode, connectTimestamp);
            if (pjsip_inv_state.PJSIP_INV_STATE_CALLING == callStateCode) {
                binding.textViewCallState.setText("calling");
                ringToneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);
            } else if (pjsip_inv_state.PJSIP_INV_STATE_INCOMING == callStateCode) {
                binding.textViewCallState.setText("incoming");
            } else if (pjsip_inv_state.PJSIP_INV_STATE_EARLY == callStateCode) {
                binding.textViewCallState.setText("early");
            } else if (pjsip_inv_state.PJSIP_INV_STATE_CONNECTING == callStateCode) {
                binding.textViewCallState.setText("connecting");
            } else if (pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED == callStateCode) {
                binding.textViewCallState.setText("confirmed");
                showLayout(TYPE_CALL_CONNECTED);
                ringToneGenerator.stopTone();
            } else if (pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED == callStateCode) {
                ringToneGenerator.stopTone();
                finish();
            } else if (pjsip_inv_state.PJSIP_INV_STATE_NULL == callStateCode) {
                Toast.makeText(getReceiverContext(), "Unknown Error", Toast.LENGTH_SHORT).show();
                ringToneGenerator.stopTone();
                finish();
            }
        }

        @Override
        public void onCallMediaState(String accountID, int callID, MediaState stateType, boolean stateValue) {
            super.onCallMediaState(accountID, callID, stateType, stateValue);
        }

        @Override
        public void onOutgoingCall(String accountID, int callID, String number, boolean isVideo, boolean isVideoConference, boolean isTransfer) {
            super.onOutgoingCall(accountID, callID, number, isVideo, isVideoConference, isTransfer);
        }

        @Override
        public void onStackStatus(boolean started) {
            super.onStackStatus(started);
        }

        @Override
        public void onReceivedCodecPriorities(ArrayList<CodecPriority> codecPriorities) {
            super.onReceivedCodecPriorities(codecPriorities);
        }

        @Override
        public void onCodecPrioritiesSetStatus(boolean success) {
            super.onCodecPrioritiesSetStatus(success);
        }

        @Override
        public void onMissedCall(String displayName, String uri) {
            super.onMissedCall(displayName, uri);
        }

        @Override
        protected void onVideoSize(int width, int height) {
            super.onVideoSize(width, height);
        }

        @Override
        protected void onCallStats(int callID, int duration, String audioCodec, int callStatusCode, RtpStreamStats rx, RtpStreamStats tx) {
            super.onCallStats(callID, duration, audioCodec, callStatusCode, rx, tx);
        }
    };

     */


    private void updateCallState(CallInfo ci) {
        if (ci.getState() < pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
            ringToneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);

        } else if (ci.getState() >= pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
            ringToneGenerator.stopTone();
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message m) {

        if (m.what == MainActivity.MSG_TYPE.CALL_STATE) {
            lastCallInfo = (CallInfo) m.obj;
            handleCallStateEvent(lastCallInfo.getState());
        } else if (m.what == MainActivity.MSG_TYPE.CALL_MEDIA_STATE) {
            handleCallMediaState();
        } else if (m.what == MainActivity.MSG_TYPE.CALL_MEDIA_EVENT) {
            OnCallMediaEventParam prm = (OnCallMediaEventParam)m.obj;
            handleCallMediaEvent(prm.getEv().getType(), prm.getMedIdx());
        } else {

            /* Message not handled */
            return false;

        }

        return true;

    }

    private void handleCallMediaState() {
        if (MainActivity.currentCall.vidWin != null) {
            /* Set capture orientation according to current
             * device orientation.
             */
            onConfigurationChanged(getResources().getConfiguration());

        }

        if (MainActivity.currentCall.vidPrev != null) {
            localVideoHandler.setVideoWindow(MainActivity.currentCall.vidPrev.getVideoWindow());
            setupVideoPreviewLayout();
        }
    }

    private void handleCallStateEvent(int state) {
        if (state==pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
            localVideoHandler.resetVideoWindow();
            remoteVideoHandler.resetVideoWindow();
        }

        updateCallState(lastCallInfo);
    }

    private void handleCallMediaEvent(int type, long medIdx) {

        if (type == pjmedia_event_type.PJMEDIA_EVENT_FMT_CHANGED &&
                medIdx == MainActivity.currentCall.vidGetStreamIdx() &&
                MainActivity.currentCall.vidWin != null)
        {
            CallMediaInfo cmi;
            try {
                CallInfo ci = MainActivity.currentCall.getInfo();
                cmi = ci.getMedia().get((int)medIdx);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            remoteVideoHandler.setVideoWindow(cmi.getVideoWindow());
            setupIncomingVideoLayout();
        }

    }

    private void setupIncomingVideoLayout()
    {
        try {
            StreamInfo si = MainActivity.currentCall.getStreamInfo(MainActivity.currentCall.vidGetStreamIdx());
            int w = (int)si.getVidCodecParam().getDecFmt().getWidth();
            int h = (int)si.getVidCodecParam().getDecFmt().getHeight();

            /* Adjust width to match the parent layout */
            RelativeLayout videoLayout = findViewById(R.id.bottom_layout);
            h = (int)((double)videoLayout.getMeasuredWidth() / w * h);
            w = videoLayout.getMeasuredWidth();

            /* Also adjust height to match the parent layout */
            if (h > videoLayout.getMeasuredHeight()) {
                w = (int)((double)videoLayout.getMeasuredHeight() / h * w);
                h = videoLayout.getMeasuredHeight();
            }
            System.out.println("Remote video size=" + w + "x" + h);

            /* Resize the remote video surface */
            SurfaceView svRemoteVideo = findViewById(R.id.surfaceIncomingVideo);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
            params.leftMargin = (w == videoLayout.getMeasuredWidth())? 0 : (videoLayout.getMeasuredWidth()-w)/2;
            params.topMargin = 0;
            svRemoteVideo.setLayoutParams(params);
            svRemoteVideo.setVisibility(View.VISIBLE);

            /* Put local preview always on top */
            if (MainActivity.currentCall.vidPrevStarted) {
                SurfaceView surfacePreview = findViewById(R.id.surfacePreviewCapture);
                surfacePreview.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void setupVideoPreviewLayout()
    {
        /* Resize the preview video surface */
        try {
            int w, h;
            SurfaceView surfacePreview = findViewById(R.id.surfacePreviewCapture);
            VideoWindowInfo vwi = MainActivity.currentCall.vidPrev.getVideoWindow().getInfo();
            w = (int) vwi.getSize().getW();
            h = (int) vwi.getSize().getH();

            /* Adjust width to match the parent layout */
            RelativeLayout videoLayout = findViewById(R.id.bottom_layout);
            h = (int) ((double) videoLayout.getMeasuredWidth() / 2 / w * h);
            w = videoLayout.getMeasuredWidth() / 2;

            /* Also adjust height to match the parent layout */
            if (h > videoLayout.getMeasuredHeight() / 2) {
                w = (int) ((double) videoLayout.getMeasuredHeight() / 2 / h * w);
                h = videoLayout.getMeasuredHeight() / 2;
            }
            System.out.println("Preview video size=" + w + "x" + h);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
            params.leftMargin = videoLayout.getMeasuredWidth() - w;
            params.topMargin = videoLayout.getMeasuredHeight() - h;
            surfacePreview.setLayoutParams(params);
            surfacePreview.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}