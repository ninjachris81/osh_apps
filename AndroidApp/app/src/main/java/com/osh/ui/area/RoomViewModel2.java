package com.osh.ui.area;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModel;

import com.osh.core.actor.ActorBase;
import com.osh.core.actor.AudioPlaybackActor;
import com.osh.core.actor.ShutterActor;
import com.osh.core.actor.ToggleActor;
import com.osh.core.datamodel.meta.KnownRoom;
import com.osh.core.log.LogFacade;
import com.osh.core.service.IServiceContext;
import com.osh.core.value.BooleanValue;
import com.osh.core.value.DoubleValue;
import com.osh.core.value.EnumValue;
import com.osh.core.value.IntegerValue;
import com.osh.core.value.ValueBase;
import com.osh.core.value.ValueType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomViewModel2 extends ViewModel implements Serializable {

    private final KnownRoom room;

    public final ObservableField<String> name = new ObservableField<>("Unknown Room");

    public final ObservableField<AudioPlaybackActor> activePlayback = new ObservableField<>();

    public final List<ShutterActor> roomShutterActors = new ArrayList<>();
    public final List<EnumValue> roomShutterAutoModes = new ArrayList<>();

    public final List<ObservableBoolean> shutterAutoModes = new ArrayList<>();
    public final List<ObservableField<String>> shutterStates = new ArrayList<>();
    public final List<ObservableBoolean> supportsTilts = new ArrayList<>();
    public final List<ObservableField<String>> shutterUpDownTimesInfo = new ArrayList<>();

    public final ObservableField<RoomViewModel.RoomPosition> roomPosition = new ObservableField<>(RoomViewModel.RoomPosition.POSITION_TOP);

    public final List<ToggleActor> roomLightToggleActors = new ArrayList<>();
    public final List<ObservableBoolean> lightStates = new ArrayList<>();

    public final List<ObservableDouble> temperatures = new ArrayList<>();

    public final List<ObservableDouble> humidities = new ArrayList<>();
    public final List<ObservableBoolean> windowStates = new ArrayList<>();
    public final List<ObservableDouble> brightnesses = new ArrayList<>();

    public final List<ObservableBoolean> roomPresences = new ArrayList<>();

    protected final IServiceContext serviceContext;


    public RoomViewModel2(IServiceContext serviceContext, String roomId) {
        this.serviceContext = serviceContext;
        this.room = serviceContext.getDatamodelService().getDatamodel().getKnownRoom(roomId);
        this.name.set(room.getFullName());

        LogFacade.i(RoomViewModel2.class.getName(), "Init room " + room.getId());

        initLightStates();
        initRoomPresences();
        initWindowState();
        initShutterStates();
        initBrightnesses();
        initHumidities();
        initTemperatures();
    }

    public KnownRoom getRoom() {
        return room;
    }

    private void initLightStates() {
        List<ActorBase> lightToggleActors = serviceContext.getActorService().getActors(room, ValueType.VT_VIRTUAL_ACTOR);
        for (int index = 0; index < lightToggleActors.size(); index++) {
            ToggleActor lightToggleActor = (ToggleActor) lightToggleActors.get(index);
            roomLightToggleActors.add(lightToggleActor);

            ObservableBoolean state = new ObservableBoolean(false);
            lightStates.add(state);

            lightToggleActor.addItemChangeListener(item -> {
                state.set(item.getValue(false));
            }, true, () -> {
                return this != null;
            });
        }
    }

    private void initRoomPresences() {
        // TODO
    }

    private void initWindowState() {
        List<ValueBase> windowStateValues = serviceContext.getValueService().getValues(room, ValueType.VT_REED_CONTACT);
        for (int index = 0; index < windowStateValues.size(); index++) {
            BooleanValue value = (BooleanValue) windowStateValues.get(index);
            ObservableBoolean state = new ObservableBoolean(false);
            windowStates.add(state);
            value.addItemChangeListener(item -> {
                state.set(item.getValue(false));
            }, true, () -> { return this!=null; });
        }
    }

    private void initShutterStates() {
        List<ActorBase> shutterActors = serviceContext.getActorService().getActors(room, ValueType.VT_RELAY_SHUTTER);
        List<ValueBase> shutterAutoModeValues = serviceContext.getValueService().getValues(room, ValueType.VT_SHUTTER_MODE);
        List<ValueBase> shutterUpTimeValues = serviceContext.getValueService().getValues(room, ValueType.VT_SHUTTER_UP_TIME);
        List<ValueBase> shutterDownTimeValues = serviceContext.getValueService().getValues(room, ValueType.VT_SHUTTER_DOWN_TIME);

        if (shutterActors.size() == shutterAutoModeValues.size() &&
            shutterActors.size() == shutterUpTimeValues.size()  &&
            shutterActors.size() == shutterDownTimeValues.size()) {

            for (int index = 0; index < shutterActors.size(); index++) {
                // STATES
                ShutterActor shutterActor = (ShutterActor) shutterActors.get(index);
                roomShutterActors.add(shutterActor);

                ObservableField<String> state = new ObservableField<>("");
                shutterStates.add(state);
                supportsTilts.add(new ObservableBoolean(shutterActor.isTiltSupport()));

                shutterActor.addItemChangeListener(item -> {
                    state.set(item.getCloseAndTiltState());
                }, true, () -> { return this!=null; });

                // AUTO MODES
                EnumValue value = (EnumValue) shutterAutoModeValues.get(index);
                roomShutterAutoModes.add(value);
                ObservableBoolean autoMode = new ObservableBoolean(false);

                shutterAutoModes.add(autoMode);

                value.addItemChangeListener(item -> {
                    autoMode.set(item.getValue(ShutterActor.SHUTTER_OPERATION_MODE_AUTO) == ShutterActor.SHUTTER_OPERATION_MODE_AUTO);
                }, true, () -> { return this!=null; });

                // UP/DOWN TIMES
                IntegerValue upTimeValue = (IntegerValue) shutterUpTimeValues.get(index);
                IntegerValue downTimeValue = (IntegerValue) shutterDownTimeValues.get(index);

                ObservableField<String> upDownTimeInfo = new ObservableField<>("");
                shutterUpDownTimesInfo.add(upDownTimeInfo);

                upTimeValue.addItemChangeListener(item -> {
                    upDownTimeInfo.set(calculateUpDownTimeInfo(shutterActor, autoMode, item.getValue(0), downTimeValue.getValue(0)));
                }, true, () -> { return this!=null; });

                downTimeValue.addItemChangeListener( item -> {
                    upDownTimeInfo.set(calculateUpDownTimeInfo(shutterActor, autoMode, upTimeValue.getValue(0), item.getValue(0)));
                }, true, () -> { return this!=null; });

            }
        } else {
            LogFacade.w("RoomViewModel", "Shutter actor count mismatch");
        }
    }

    private String calculateUpDownTimeInfo(ShutterActor shutterActor, ObservableBoolean autoMode, int upTime, int downTime) {
        if (autoMode.get()) {
            if (shutterActor.getCloseState() == ShutterActor.SHUTTER_STATE_OPENED) {
                long downDelta = downTime - System.currentTimeMillis() / 1000;
                return "DOWN: " + downDelta + "s";
            } else if (shutterActor.getCloseState() == ShutterActor.SHUTTER_STATE_CLOSED) {
                long upDelta = upTime - System.currentTimeMillis() / 1000;
                return "UP: " + upDelta + "s";
            } else {
                return "";
            }
        } else {
            return "MANUAL";
        }
    }

    private void initBrightnesses() {
        List<ValueBase> brightnessValues = serviceContext.getValueService().getValues(room, ValueType.VT_BRIGHTNESS);
        for (int index = 0; index < brightnessValues.size(); index++) {
            DoubleValue value = (DoubleValue) brightnessValues.get(index);
            ObservableDouble brightness = new ObservableDouble(-1f);
            brightnesses.add(brightness);

            value.addItemChangeListener(item -> {
                brightness.set(item.getValue(-1.0));
            }, true, () -> { return this!=null; });
        }
    }

    private void initHumidities() {
        List<ValueBase> humidityValues = serviceContext.getValueService().getValues(room, ValueType.VT_HUMIDITY);
        for (int index = 0; index < humidityValues.size(); index++) {
            DoubleValue value = (DoubleValue) humidityValues.get(index);
            ObservableDouble humidity = new ObservableDouble(-1f);
            humidities.add(humidity);

            value.addItemChangeListener(item -> {
                humidity.set(item.getValue(-1.0));
            }, true, () -> { return this!=null; });
        }
    }

    private void initTemperatures() {
        List<ValueBase> temperatureValues = serviceContext.getValueService().getValues(room, ValueType.VT_TEMP);
        for (int index = 0; index < temperatureValues.size(); index++) {
            DoubleValue value = (DoubleValue) temperatureValues.get(index);
            ObservableDouble temperature = new ObservableDouble(-1f);
            temperatures.add(temperature);

            value.addItemChangeListener(item -> {
                temperature.set(item.getValue(-1.0));
            }, true, () -> { return this!=null; });
        }
    }

}
