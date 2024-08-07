package com.osh.ui.area;

import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.renderscript.Sampler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.osh.R;
import com.osh.activity.MainActivity;
import com.osh.actor.AudioPlaybackActor;
import com.osh.ui.components.WindowStateIndicator;
import com.osh.ui.dialogs.SelectAudioDialogFragment;
import com.osh.actor.ActorCmds;
import com.osh.actor.DigitalActor;
import com.osh.actor.ShutterActor;
import com.osh.actor.ToggleActor;
import com.osh.service.IServiceContext;
import com.osh.ui.components.ShutterModeButton;
import com.osh.ui.home.HomeViewModel;
import com.osh.ui.home.HomeViewModelFactory;
import com.osh.value.BooleanValue;
import com.osh.value.DoubleValue;
import com.osh.value.EnumValue;
import com.osh.value.ValueBase;

import java.util.ArrayList;
import java.util.List;

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

    protected List<ShutterInfo> shutterInfos = new ArrayList<>();

    protected List<LightInfo> lightInfos = new ArrayList<>();

    protected final SensorInfo sensorInfos = new SensorInfo();

    protected RoomViewModel roomViewModel;
    protected RoomViewModel.RoomPosition roomPosition;
    private View roomBackground;

    protected abstract void setBindingData();

    @LayoutRes
    protected abstract int getLayout();

    protected abstract List<Integer> getShutterModeButtons();
    protected abstract List<Integer> getWindowStateIndicators();

    protected String roomId;
    protected String areaId;

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
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        this.serviceContext = ((MainActivity) getActivity()).getServiceContext();

        if (savedInstanceState != null) {
            this.roomId = savedInstanceState.getString("roomId");
            this.areaId = savedInstanceState.getString("areaId");
            this.roomPosition = RoomViewModel.RoomPosition.valueOf(savedInstanceState.getString("roomPosition"));
        }

        binding = DataBindingUtil.inflate(inflater, getLayout(), container, false);

        roomViewModel = new ViewModelProvider(this, new RoomViewModelFactory(serviceContext)).get(roomId, RoomViewModel.class);
        roomViewModel.roomPosition.set(roomPosition);
        areaViewModel = new ViewModelProvider(getParentFragment(), AreaViewModelFactory.getInstance(serviceContext)).get(areaId, AreaViewModel.class);
        areaOverlayViewModel = new ViewModelProvider(getParentFragment().getParentFragment(), AreaOverlayViewModelFactory.getInstance()).get(AreaOverlayViewModel.class);

        setBindingData();

        roomBackground = binding.getRoot().findViewById(R.id.roomBackground);
        if (roomBackground == null) {
            throw new RuntimeException("No room background defined!");
        }

        initRoom();

        for (int i = 0;i<lightInfos.size();i++) {
            bindLight(lightInfos.get(i), i);
        }

        if (!shutterInfos.isEmpty() && shutterInfos.size() != getShutterModeButtons().size()) {
            throw new RuntimeException("Shutter info mismatch for room " + this.roomViewModel.getRoom().getId());
        }

        for (int i = 0;i<shutterInfos.size();i++) {
            bindShutter(shutterInfos.get(i), getShutterModeButtons().get(i), i);
        }

        for (int i = 0;i<sensorInfos.temperatureIds.size();i++) {
            bindTemperatureSensor(sensorInfos.temperatureIds.get(i), i);
        }

        for (int i = 0;i<sensorInfos.humidityIds.size();i++) {
            bindHumiditySensor(sensorInfos.humidityIds.get(i), i);
        }

        for (int i = 0;i<sensorInfos.windowStateIds.size();i++) {
            bindWindowState(sensorInfos.windowStateIds.get(i), getWindowStateIndicators().get(i), i);
        }

        for (int i = 0;i<sensorInfos.brightnessIds.size();i++) {
            bindBrightnessSensor(sensorInfos.brightnessIds.get(i), i);
        }

        for (int i = 0;i<sensorInfos.presenceIds.size();i++) {
            bindPresence(sensorInfos.presenceIds.get(i), i);
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
        ToggleActor lightToggleActor = (ToggleActor) serviceContext.getDatamodelService().getDatamodel().getActor(lightInfos.get(index).lightToggleId, lightInfos.get(index).lightToggleValueGroupId);
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

    protected void bindTemperatureSensor(String id, int index) {
        DoubleValue value = (DoubleValue) serviceContext.getDatamodelService().getDatamodel().getValue(id);
        if (value != null) {
            value.addItemChangeListener(item -> {
                if (roomViewModel!=null) {
                    roomViewModel.temperatures.get(index).set(item.getValue(-1.0));
                }
            }, true, () -> {return roomViewModel!=null;});
        } else {
            throw new RuntimeException("Sensor value not found: " + id);
        }
    }

    protected  void bindHumiditySensor(String id, int index) {
        DoubleValue value = (DoubleValue) serviceContext.getDatamodelService().getDatamodel().getValue(id);
        if (value != null) {
            value.addItemChangeListener(item -> {
                if (roomViewModel!=null) {
                    roomViewModel.humidities.get(index).set(item.getValue(-1.0));
                }
            }, true, () -> { return roomViewModel!=null;});
        } else {
            throw new RuntimeException("Sensor value not found: " + id);
        }
    }


    protected void bindWindowState(String id, @IdRes int windowStateIndicator, int index) {
        WindowStateIndicator stateIndicator = binding.getRoot().findViewById(windowStateIndicator);
        stateIndicator.setVisibility(View.VISIBLE);

        BooleanValue value = (BooleanValue) serviceContext.getValueService().getValue(id);
        if (value != null) {
            value.addItemChangeListener(item -> {
                if (roomViewModel!=null) {
                    roomViewModel.windowStates.get(index).set(item.getValue(false));
                }
            }, true, () -> {return roomViewModel!=null;});
        } else {
            throw new RuntimeException("Sensor value not found: " + id);
        }
    }

    protected void bindBrightnessSensor(String id, int index) {
        DoubleValue value = (DoubleValue) serviceContext.getValueService().getValue(id);
        if (value != null) {
            value.addItemChangeListener(item -> {
                if (roomViewModel!=null) {
                    roomViewModel.brightnesses.get(index).set(item.getValue(-1.0));
                }
            }, true, () -> { return roomViewModel!=null; });
        } else {
            throw new RuntimeException("Sensor value not found: " + id);
        }
    }

    protected void bindPresence(String id, int index) {
        BooleanValue value = (BooleanValue) serviceContext.getValueService().getValue(id);

        if (value != null) {
            value.addItemChangeListener(item -> {
                if (areaViewModel!=null && roomViewModel!=null) {
                    if (areaOverlayViewModel.currentOverlay.get() == AreaFragment.AreaOverlays.PRESENCE) {
                        roomViewModel.roomPresences.get(index).set(item.getValue(false));
                        //roomViewModel.backgroundVisible.set(item.getValue(false));
                    }
                }
            }, true, () -> { return areaViewModel!=null && roomViewModel!=null; });
        } else {
            throw new RuntimeException("Sensor value not found: " + id);
        }
    }

    protected void bindLight(LightInfo lightInfo, int index) {
        DigitalActor lightActor = (DigitalActor) serviceContext.getDatamodelService().getDatamodel().getActor(lightInfo.lightRelayId, lightInfo.lightRelayValueGroupId);

        lightActor.addItemChangeListener(isEnabled -> {
            if (roomViewModel!=null) {
                roomViewModel.lightStates.get(index).set(isEnabled.getValue(false));
            }
        }, true, () -> { return roomViewModel!=null;});
    }

    public RoomFragmentBase withShutter(String shutterValueGroupId, String shutterId, String shutterModeValueGroupId, String shutterModeId) {
        shutterInfos.add(new ShutterInfo(shutterValueGroupId, shutterId, shutterModeValueGroupId, shutterModeId));
        return this;
    }


    protected void bindShutter(ShutterInfo shutterInfo, @IdRes int shutterModeButton, int index) {
        ShutterModeButton modeButton = binding.getRoot().findViewById(shutterModeButton);
        modeButton.setVisibility(View.VISIBLE);

        ShutterActor shutterActor = (ShutterActor) serviceContext.getDatamodelService().getDatamodel().getActor(shutterInfo.shutterId, shutterInfo.shutterValueGroupId);
        EnumValue shutterMode = (EnumValue) serviceContext.getDatamodelService().getDatamodel().getValue(shutterInfo.shutterModeId, shutterInfo.shutterModeValueGroupId);

        shutterMode.addItemChangeListener(item -> {
            roomViewModel.shutterAutoModes.get(index).set(item.getValue(ShutterActor.SHUTTER_OPERATION_MODE_AUTO) == ShutterActor.SHUTTER_OPERATION_MODE_AUTO);
        }, true, () -> {return roomViewModel!=null; });

        shutterActor.addItemChangeListener(item -> {
            roomViewModel.shutterStates.get(index).set(item.getStateAsString());
        }, true, () -> {return roomViewModel!=null; });

        modeButton.setAutoClickListener(view -> {
            shutterMode.updateValue(shutterMode.getValue(ShutterActor.SHUTTER_OPERATION_MODE_AUTO) == ShutterActor.SHUTTER_OPERATION_MODE_AUTO ? ShutterActor.SHUTTER_OPERATION_MODE_MANUAL : ShutterActor.SHUTTER_OPERATION_MODE_AUTO);
            serviceContext.getValueService().publishValue(shutterMode);
        });

        modeButton.setUpClickListener(view -> {
            shutterMode.updateValue(ShutterActor.SHUTTER_OPERATION_MODE_MANUAL);
            serviceContext.getValueService().publishValue(shutterMode);
            serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_UP);
        });

        modeButton.setDownClickListener(view -> {
            shutterMode.updateValue(ShutterActor.SHUTTER_OPERATION_MODE_MANUAL);
            serviceContext.getValueService().publishValue(shutterMode);
            serviceContext.getActorService().publishCmd(shutterActor, ActorCmds.ACTOR_CMD_DOWN);
        });
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