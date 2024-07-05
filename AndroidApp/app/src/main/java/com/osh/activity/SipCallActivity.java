package com.osh.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.osh.R;
import com.osh.camera.config.CameraSource;
import com.osh.databinding.ActivitySipCallBinding;

import net.gotev.sipservice.BroadcastEventReceiver;
import net.gotev.sipservice.CodecPriority;
import net.gotev.sipservice.MediaState;
import net.gotev.sipservice.RtpStreamStats;
import net.gotev.sipservice.SipServiceCommand;
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;

import org.pjsip.pjsua2.pjsip_inv_state;

import java.util.ArrayList;

public class SipCallActivity extends AppCompatActivity implements SurfaceHolder.Callback {
    private static final String TAG = "SipCallActivity";

    private ActivitySipCallBinding binding;


    private String mAccountID;
    private String mDisplayName;
    private String mRemoteUri;
    private int mCallID;
    private boolean mIsVideo;
    private int mType;
    private String mNumber;
    private boolean mIsVideoConference;
    private boolean micMute;

    public static final int TYPE_INCOMING_CALL = 646;
    public static final int TYPE_OUT_CALL = 647;
    public static final int TYPE_CALL_CONNECTED = 648;

    private ToneGenerator ringToneGenerator;
    private ExoPlayer player;
    private Drawable micOnIcon;
    private Drawable micOffIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySipCallBinding.inflate(getLayoutInflater());

        binding.buttonAccept.setOnClickListener(this::onViewClicked);
        binding.buttonHangup.setOnClickListener(this::onViewClicked);
        binding.btnCancel.setOnClickListener(this::onViewClicked);
        binding.btnMuteMic.setOnClickListener(this::onViewClicked);
        binding.btnHangUp.setOnClickListener(this::onViewClicked);
        binding.btnSwitchCamera.setOnClickListener(this::onViewClicked);
        binding.unlockDoor.setOnClickListener(this::onViewClicked);
        setContentView(binding.getRoot());

        ringToneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, getIntent().getIntExtra("ringVolume", 80));

        registReceiver();
        initData();

        player = new ExoPlayer.Builder(this).build();
        binding.videoSurface.setPlayer(player);

        CameraSource cameraSource = ((OshApplication) getApplication()).getApplicationConfig().getCamera().getCameraSource("frontDoor.door");

        MediaSource mediaSource = new RtspMediaSource.Factory()
                        .createMediaSource(MediaItem.fromUri(cameraSource.getStreamUri()));
        player.setMediaSource(mediaSource);
        player.setPlayWhenReady(true);
        player.prepare();

        binding.unlockDoor.setOnClickListener(view -> {
            SipServiceCommand.hangUpActiveCalls(this, mAccountID);
            finish();
            DoorOpenActivity.invokeActivity(view.getContext(), DoorOpenActivity.FRONT_DOOR_ID);
        });
    }

    private void registReceiver() {
        mReceiver.register(this);
    }

    private void initData() {
        mAccountID = getIntent().getStringExtra("accountID");
        mCallID = getIntent().getIntExtra("callID", -1);
        mType = getIntent().getIntExtra("type", -1);
        mDisplayName = getIntent().getStringExtra("displayName");
        mRemoteUri = getIntent().getStringExtra("remoteUri");
        mNumber = getIntent().getStringExtra("number");
        mIsVideo = getIntent().getBooleanExtra("isVideo", false);
        mIsVideoConference = getIntent().getBooleanExtra("isVideoConference", false);

        showLayout(mType);
        binding.textViewPeer.setText(String.format("%s", mDisplayName));
        binding.tvOutCallInfo.setText(String.format("You are calling %s", mNumber));

        binding.buttonAccept.setIcon(MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.CHECK).setColor(Color.WHITE).build());
        binding.buttonHangup.setIcon(MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.CANCEL).setColor(Color.WHITE).build());

        binding.btnCancel.setIcon(MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.CANCEL).setColor(Color.WHITE).build());

        micOnIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.MICROPHONE).setColor(Color.WHITE).build();
        micOffIcon = MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.MICROPHONE_OFF).setColor(Color.WHITE).build();

        binding.btnMuteMic.setIcon(micOnIcon);
        binding.btnHangUp.setIcon(MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.PHONE_HANGUP).setColor(Color.WHITE).build());
        binding.btnSwitchCamera.setIcon(MaterialDrawableBuilder.with(this).setIcon(MaterialDrawableBuilder.IconValue.CAMERA_SWITCH).setColor(Color.WHITE).build());
        binding.btnSwitchCamera.setVisibility(mIsVideo ? View.VISIBLE : View.GONE);

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
        });
    }

    public static void startActivityIn(Context context, String accountID, int callID, String displayName, String remoteUri, boolean isVideo, int ringVolume) {
        Intent intent = new Intent(context, SipCallActivity.class);
        intent.putExtra("accountID", accountID);
        intent.putExtra("callID", callID);
        intent.putExtra("displayName", displayName);
        intent.putExtra("remoteUri", remoteUri);
        intent.putExtra("isVideo", isVideo);
        intent.putExtra("type", TYPE_INCOMING_CALL);
        intent.putExtra("ringVolume", ringVolume);
        context.startActivity(intent);
    }

    public static void startActivityOut(Context context, String accountID, int callID, String number, boolean isVideo, boolean isVideoConference) {
        Intent intent = new Intent(context, SipCallActivity.class);
        intent.putExtra("accountID", accountID);
        intent.putExtra("callID", callID);
        intent.putExtra("number", number);
        intent.putExtra("isVideo", isVideo);
        intent.putExtra("isVideoConference", isVideoConference);
        intent.putExtra("type", TYPE_OUT_CALL);
        context.startActivity(intent);
    }

    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buttonAccept:
                SipServiceCommand.acceptIncomingCall(this, mAccountID, mCallID, mIsVideo);
                break;
            case R.id.buttonHangup:
                SipServiceCommand.declineIncomingCall(this, mAccountID, mCallID);
                finish();
                break;
            case R.id.unlock_door:
            case R.id.btnCancel:
                SipServiceCommand.hangUpActiveCalls(this, mAccountID);
                finish();
                break;
            case R.id.btnMuteMic:
                micMute = !micMute;
                SipServiceCommand.setCallMute(this, mAccountID, mCallID, micMute);
                binding.btnMuteMic.setIcon(micMute ? micOffIcon : micOnIcon);
                binding.btnMuteMic.setText(micMute ? "Muted" : "Mute");
                break;
            case R.id.btnHangUp:
                SipServiceCommand.hangUpCall(this, mAccountID, mCallID);
                finish();
                break;
            case R.id.btnSwitchCamera:
                SipServiceCommand.switchVideoCaptureDevice(this,mAccountID,mCallID);
                break;
        }
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mReceiver.unregister(this);
        player.stop();
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        SipServiceCommand.startVideoPreview(SipCallActivity.this, mAccountID, mCallID, binding.svLocal.getHolder().getSurface());

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
    }

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
}