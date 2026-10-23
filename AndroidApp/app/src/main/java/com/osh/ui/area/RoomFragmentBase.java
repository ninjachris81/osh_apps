package com.osh.ui.area;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.osh.R;
import com.osh.activity.MainActivity;
import com.osh.core.actor.AudioPlaybackActor;
import com.osh.core.log.LogFacade;
import com.osh.ui.components.WindowStateIndicator;
import com.osh.ui.dialogs.SelectAudioDialogFragment;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.DigitalActor;
import com.osh.core.actor.ShutterActor;
import com.osh.core.actor.ToggleActor;
import com.osh.core.service.IServiceContext;
import com.osh.ui.components.ShutterModeButton;
import com.osh.core.value.BooleanValue;
import com.osh.core.value.DoubleValue;
import com.osh.core.value.EnumValue;
import com.osh.core.value.ValueBase;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class RoomFragmentBase<BINDING_TYPE extends ViewDataBinding> extends Fragment {

    public class ShutterInfo {
        public String shutterValueGroupId;
        public String shutterId;
        public String shutterModeValueGroupId;
        public String shutterModeId;

        public ShutterInfo(String shutterValueGroupId, String shutterId, String shutterModeValueGroupId, String shutterModeId) {
            this.shutterValueGroupId = shutterValueGroupId;
            this.shutterId = shutterId;
            this.shutterModeValueGroupId = shutterModeValueGroupId;
            this.shutterModeId = shutterModeId;
        }
    }

    public class LightInfo {
        public String lightRelayValueGroupId;
        public String lightRelayId;
        public String lightToggleValueGroupId;
        public String lightToggleId;

        public LightInfo(String lightRelayValueGroupId, String lightRelayId, String lightToggleValueGroupId, String lightToggleId) {
            this.lightRelayValueGroupId = lightRelayValueGroupId;
            this.lightRelayId = lightRelayId;
            this.lightToggleValueGroupId = lightToggleValueGroupId;
            this.lightToggleId = lightToggleId;
        }
    }

    public class SensorInfo {
        public final List<String> temperatureIds = new ArrayList<>();
        public final List<String> humidityIds = new ArrayList<>();
        public final List<String> windowStateIds = new ArrayList<>();

        public final List<String> brightnessIds = new ArrayList<>();
        public final List<String> presenceIds = new ArrayList<>();
    }

    protected AreaViewModel areaViewModel;
    protected AreaOverlayViewModel areaOverlayViewModel;
    protected IServiceContext serviceContext;
    protected BINDING_TYPE binding;

    protected RoomViewModel2 roomViewModel;
    protected RoomViewModel.RoomPosition roomPosition;
    private View roomBackground;

    protected abstract void setBindingData();

    @LayoutRes
    protected abstract int getLayout();

    protected abstract List<Integer> getShutterModeButtons();
    protected abstract List<Integer> getWindowStateIndicators();

    protected String roomId;
    protected String areaId;

    protected TimerTask shutterRepeatTimerTask;
    protected Timer shutterRepeatTimer = new Timer();

    public RoomFragmentBase() {
        // Required empty public constructor
    }

    public RoomFragmentBase(String roomId, String areaId, RoomViewModel.RoomPosition roomPosition) {
        this.roomId = roomId;
        this.areaId = areaId;
        this.roomPosition = roomPosition;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            this.roomId = savedInstanceState.getString("roomId");
            this.areaId = savedInstanceState.getString("areaId");
            this.roomPosition = RoomViewModel.RoomPosition.valueOf(savedInstanceState.getString("roomPosition"));
        }

        MainActivity mainActivity = ((MainActivity) getActivity());
        this.serviceContext = mainActivity.getServiceContext();

        roomViewModel = new ViewModelProvider(mainActivity, RoomViewModelFactory.getInstance(serviceContext)).get(roomId, RoomViewModel2.class);
        roomViewModel.roomPosition.set(roomPosition);
        areaViewModel = new ViewModelProvider(mainActivity, AreaViewModelFactory.getInstance(serviceContext)).get(areaId, AreaViewModel.class);
        areaOverlayViewModel = new ViewModelProvider(mainActivity, AreaOverlayViewModelFactory.getInstance()).get(AreaOverlayViewModel.class);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("roomId", roomId);
        outState.putString("areaId", areaId);
        outState.putString("roomPosition", roomPosition.name());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            this.roomId = savedInstanceState.getString("roomId");
            this.areaId = savedInstanceState.getString("areaId");
            this.roomPosition = RoomViewModel.RoomPosition.valueOf(savedInstanceState.getString("roomPosition"));
        }

        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false);

        setBindingData();

        roomBackground = binding.getRoot().findViewById(R.id.roomBackground);
        if (roomBackground == null) {
            throw new RuntimeException("No room background defined!");
        }

        initRoom();

        if (!roomViewModel.shutterAutoModes.isEmpty() && roomViewModel.shutterAutoModes.size() != getShutterModeButtons().size()) {
            throw new RuntimeException("Shutter info mismatch for room " + this.roomViewModel.getRoom().getId());
        }

        for (int i = 0;i<roomViewModel.shutterAutoModes.size();i++) {
            bindShutter(getShutterModeButtons().get(i), i);
        }

        for (int i = 0;i<roomViewModel.windowStates.size();i++) {
            bindWindowState(getWindowStateIndicators().get(i), i);
        }

        return binding.getRoot();
    }

    protected void initRoom() {
        roomBackground.setOnClickListener((item) -> {
            handleBackgroundClickEvent();
        });
    }

    protected void handleBackgroundClickEvent() {
        switch(areaOverlayViewModel.currentOverlay.get()) {
            case LIGHTS:
                handleBackgroundClickEventLight(0);
                break;
            case AUDIO:
                handleBackgroundClickEventAudio();
                break;
        }
    }

    protected void handleBackgroundClickEventLight(int index) {
        ToggleActor lightToggleActor = roomViewModel.roomLightToggleActors.get(index);
        serviceContext.getActorService().publishCmd(lightToggleActor, ActorCmds.ACTOR_CMD_TOGGLE);
    }

    protected void handleBackgroundClickEventAudio() {
        List<AudioPlaybackActor> audioActors = serviceContext.getAudioActorService().getAudioActorsByRoom(roomViewModel.getRoom().getId(), true);
        if (!audioActors.isEmpty()) {
            SelectAudioDialogFragment dialog = SelectAudioDialogFragment.newInstance();
            dialog.setAudioActors(audioActors);
            dialog.setAudioPlaybackSources(serviceContext.getAudioSourceService().getAudioPlaybackSources());
            dialog.setStartCallback((actor, source) -> {
                actor.getAudioUrlValue().updateValue(source.getSourceUrl(), false);
                serviceContext.getValueService().publishValue(actor.getAudioUrlValue());
                serviceContext.getActorService().publishCmd(actor, ActorCmds.ACTOR_CMD_START);
                roomViewModel.activePlayback.set(actor);
            });
            dialog.setStopAudioCallbackhandler((actor) -> {
                serviceContext.getActorService().publishCmd(actor, ActorCmds.ACTOR_CMD_STOP);
                //model.activePlayback.set(null);
            });
            dialog.show(getChildFragmentManager(), SelectAudioDialogFragment.TAG);
        } else {
            Toast.makeText(getContext(),"No audio actor", Toast.LENGTH_SHORT).show();
        }
    }

    /*
    public RoomFragmentBase withLight(String lightRelayValueGroupId, String lightRelayId, String lightToggleValueGroupId, String lightToggleId) {
        lightInfos.add(new LightInfo(lightRelayValueGroupId, lightRelayId, lightToggleValueGroupId, lightToggleId));
        return this;
    }

    public RoomFragmentBase withTemperature(String valueGroupId, String id) {
        sensorInfos.temperatureIds.add(ValueBase.getFullId(valueGroupId, id));
        return this;
    }

    public RoomFragmentBase withHumidity(String valueGroupId, String id) {
        sensorInfos.humidityIds.add(ValueBase.getFullId(valueGroupId, id));
        return this;
    }
    public RoomFragmentBase withWindowState(String valueGroupId, String id) {
        sensorInfos.windowStateIds.add(ValueBase.getFullId(valueGroupId, id));
        return this;
    }

    public RoomFragmentBase withBrightness(String valueGroupId, String id) {
        sensorInfos.brightnessIds.add(ValueBase.getFullId(valueGroupId, id));
        return this;
    }

    public RoomFragmentBase withPresence(String valueGroupId, String id) {
        sensorInfos.presenceIds.add(ValueBase.getFullId(valueGroupId, id));
        return this;
    }

    public RoomFragmentBase withShutter(String shutterValueGroupId, String shutterId, String shutterModeValueGroupId, String shutterModeId) {
        shutterInfos.add(new ShutterInfo(shutterValueGroupId, shutterId, shutterModeValueGroupId, shutterModeId));
        return this;
    }
    */

    protected void bindWindowState(@IdRes int windowStateIndicator, int index) {
        WindowStateIndicator stateIndicator = binding.getRoot().findViewById(windowStateIndicator);
    }

    protected void bindShutter(@IdRes int shutterModeButton, int index) {
        ShutterModeButton modeButton = binding.getRoot().findViewById(shutterModeButton);

        ShutterActor shutterActor = roomViewModel.roomShutterActors.get(index);
        EnumValue shutterMode = roomViewModel.roomShutterAutoModes.get(index);

        modeButton.setAutoClickListener(view -> {
            shutterMode.updateValue(shutterMode.getValue(ShutterActor.SHUTTER_OPERATION_MODE_AUTO) == ShutterActor.SHUTTER_OPERATION_MODE_AUTO ? ShutterActor.SHUTTER_OPERATION_MODE_MANUAL : ShutterActor.SHUTTER_OPERATION_MODE_AUTO);
            serviceContext.getValueService().publishValue(shutterMode);
        });

        modeButton.setUpClickListener(new ShutterModeButton.OnClickListener() {
            @Override
            public void onTap(View view) {
                shutterMode.updateValue(ShutterActor.SHUTTER_OPERATION_MODE_MANUAL);
                serviceContext.getValueService().publishValue(shutterMode);
                serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_UP);
            }

            @Override
            public void onLongClick(View view, boolean release) {
                if (release) {
                    if (shutterRepeatTimerTask != null) shutterRepeatTimerTask.cancel();
                    serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_STOP);
                } else {
                    scheduleNewTimerTask(new TimerTask() {
                        @Override
                        public void run() {
                            serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_SHUTTER_MANUAL_UP);
                        }
                    });
                }
            }
        });


        modeButton.setDownClickListener(new ShutterModeButton.OnClickListener() {
            @Override
            public void onTap(View view) {
                shutterMode.updateValue(ShutterActor.SHUTTER_OPERATION_MODE_MANUAL);
                serviceContext.getValueService().publishValue(shutterMode);
                serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_DOWN);
            }

            @Override
            public void onLongClick(View view, boolean release) {
                if (release) {
                    if (shutterRepeatTimerTask != null) shutterRepeatTimerTask.cancel();
                    serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_STOP);
                } else {
                    scheduleNewTimerTask(new TimerTask() {
                        @Override
                        public void run() {
                            serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_SHUTTER_MANUAL_DOWN);
                        }
                    });
                }
            }
        });
    }

    void scheduleNewTimerTask(TimerTask timerTask) {
        if (shutterRepeatTimerTask != null) shutterRepeatTimerTask.cancel();
        shutterRepeatTimerTask = timerTask;
        shutterRepeatTimer.scheduleAtFixedRate(shutterRepeatTimerTask, 0, 500);
    }

    protected void setBackgroundForOverlay(View roomBackground) {
        switch(areaOverlayViewModel.currentOverlay.get()) {
            case LIGHTS:
                setBackgroundRecursive(roomBackground, R.drawable.light_background);
                break;
            case PRESENCE:
                setBackgroundRecursive(roomBackground, R.drawable.presence_background);
                break;
            default:
                setBackgroundRecursive(roomBackground, null);
        }
    }

    protected void setBackgroundRecursive(View view, Integer resId) {
        if (view instanceof ViewGroup) {
            for (int i = 0;i<((ViewGroup) view).getChildCount();i++) {
                setBackgroundRecursive(((ViewGroup) view).getChildAt(i), resId);
            }
        } else {
            if (resId == null) {
                view.setBackground(null);
            } else {
                view.setBackgroundResource(resId);
            }
        }
    }
}