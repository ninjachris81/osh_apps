package com.osh.ui.area;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableDouble;
import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.ViewModel;

import com.osh.actor.AudioPlaybackActor;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.service.IServiceContext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoomViewModel extends ViewModel implements Serializable {

    public enum RoomPosition {
        POSITION_TOP,
        POSITION_BOTTOM
    }

    private final KnownRoom room;

    public final ObservableField<String> name = new ObservableField<>("Unknown Room");
    //public final ObservableBoolean backgroundVisible = new ObservableBoolean(false);

    public final ObservableField<AudioPlaybackActor> activePlayback = new ObservableField<>();

    //public final ObservableInt active

    public final List<ObservableBoolean> shutterAutoModes = new ArrayList<>();
    public final List<ObservableField<String>> shutterStates = new ArrayList<>();
    public final ObservableField<RoomPosition> roomPosition = new ObservableField<>(RoomPosition.POSITION_TOP);

    public final List<ObservableBoolean> lightStates = new ArrayList<>();

    public final List<ObservableDouble> temperatures = new ArrayList<>();

    public final List<ObservableDouble> humidities = new ArrayList<>();
    public final List<ObservableBoolean> windowStates = new ArrayList<>();
    public final List<ObservableDouble> brightnesses = new ArrayList<>();

    public final List<ObservableBoolean> roomPresences = new ArrayList<>();

    public RoomViewModel(IServiceContext serviceContext, String roomId) {
        this.room = serviceContext.getDatamodelService().getDatamodel().getKnownRoom(roomId);
        this.name.set(room.getName());
    }

    public KnownRoom getRoom() {
        return room;
    }

    public void initShutters(int count) {
        for (int i = 0;i<count;i++) {
            shutterAutoModes.add(new ObservableBoolean(false));
            shutterStates.add(new ObservableField<>(""));
        }
    }

    public void initLightStates(int count) {
        for (int i = 0;i<count;i++) {
            lightStates.add(new ObservableBoolean(false));
        }
    }

    public void initTemperatures(int count) {
        for (int i = 0;i<count;i++) {
            temperatures.add(new ObservableDouble(-1f));
        }
    }

    public void initHumidities(int count) {
        for (int i = 0;i<count;i++) {
            humidities.add(new ObservableDouble(-1f));
        }
    }

    public void initWindowStates(int count) {
        for (int i = 0;i<count;i++) {
            windowStates.add(new ObservableBoolean(false));
        }
    }

    public void initBrightnesses(int count) {
        for (int i = 0;i<count;i++) {
            brightnesses.add(new ObservableDouble(-1f));
        }
    }

    public void initPresences(int count) {
        for (int i = 0;i<count;i++) {
            roomPresences.add(new ObservableBoolean(false));
        }
    }


}
