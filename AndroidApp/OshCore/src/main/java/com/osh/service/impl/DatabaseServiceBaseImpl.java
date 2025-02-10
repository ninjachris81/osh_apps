package com.osh.service.impl;

import com.j256.ormlite.dao.DaoManager;
import com.osh.actor.ActorBase;
import com.osh.actor.AudioPlaybackActor;
import com.osh.actor.DBActor;
import com.osh.actor.DBAudioActor;
import com.osh.actor.DBShutterActor;
import com.osh.actor.ShutterActor;
import com.osh.datamodel.DatamodelBase;
import com.osh.datamodel.DynamicDatamodel;
import com.osh.datamodel.meta.AudioPlaybackSource;
import com.osh.datamodel.meta.KnownArea;
import com.osh.datamodel.meta.KnownRoom;
import com.osh.datamodel.meta.KnownRoomActors;
import com.osh.datamodel.meta.KnownRoomValues;
import com.osh.device.KnownDevice;
import com.osh.log.LogFacade;
import com.osh.service.IDatabaseService;
import com.osh.user.User;
import com.osh.value.DBValue;
import com.osh.value.StringValue;
import com.osh.value.ValueBase;
import com.osh.value.ValueGroup;
import com.osh.value.ValueType;

import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class DatabaseServiceBaseImpl implements IDatabaseService {

    private static final String TAG = DatabaseServiceBaseImpl.class.getName();

    protected abstract List<ValueGroup> loadValueGroups();
    protected abstract List<DBValue> loadValues();
    protected abstract List<DBActor> loadActors();
    protected abstract List<AudioPlaybackSource> loadAudioPlaybackSources();
    protected abstract List<KnownArea> loadKnownAreas();
    protected abstract List<KnownRoom> loadKnownRooms();
    protected abstract List<DBShutterActor> loadShutterActors(String id, String valueGroupId);
    protected abstract List<DBAudioActor> loadAudioActors(String id, String valueGroupId);
    protected abstract List<KnownRoomValues> loadKnownRoomValues();
    protected abstract List<KnownRoomActors> loadKnownRoomActors();
    protected abstract List<KnownDevice> loadKnownDevices();
    protected abstract List<User> loadUsers();

    @Override
    public final DatamodelBase loadDatamodel() {
        DatamodelBase datamodel = new DynamicDatamodel();
        try {
            loadValues(datamodel);

            loadMetadata(datamodel);

            mergeDatamodel(datamodel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return datamodel;
    }

    private void loadValues(DatamodelBase datamodel) throws SQLException {

        for (ValueGroup valueGroup : loadValueGroups()) {
            datamodel.addValueGroup(valueGroup.getId());
        }

        for (DBValue value : loadValues()) {
            ValueGroup valueGroup = datamodel.getValueGroup(value.getValueGroupId());
            ValueBase val = null;

            if (value.getClassType().equals("BooleanValue")) {
                val = datamodel.addBooleanValue(valueGroup, value.getId(), ValueType.of(value.getValueType()), value.getValueTimeout());
            } else if (value.getClassType().equals("IntegerValue")) {
                val = datamodel.addIntegerValue(valueGroup, value.getId(), ValueType.of(value.getValueType()), value.getValueTimeout());
            } else if (value.getClassType().equals("LongValue")) {
                val = datamodel.addLongValue(valueGroup, value.getId(), ValueType.of(value.getValueType()), value.getValueTimeout());
            } else if (value.getClassType().equals("DoubleValue")) {
                val = datamodel.addDoubleValue(valueGroup, value.getId(), ValueType.of(value.getValueType()), value.getValueTimeout());
            } else if (value.getClassType().equals("StringValue")) {
                val = datamodel.addStringValue(valueGroup, value.getId(), ValueType.of(value.getValueType()), value.getValueTimeout());
            } else if (value.getClassType().equals("EnumValue")) {
                val = datamodel.addEnumValue(valueGroup, value.getId(), ValueType.of(value.getValueType()), value.getValueTimeout(), value.getEnumCount());
            } else {
                LogFacade.w(TAG, "Unknown class type: " + value.getClassType());
            }
        }


        for (DBActor actor : loadActors()) {
            ValueGroup valueGroup = datamodel.getValueGroup(actor.getValueGroupId());
            ActorBase act = null;

            if (actor.getClassType().equals("DigitalActor")) {
                act = datamodel.addDigitalActor(valueGroup, actor.getId(), ValueType.of(actor.getValueType()), actor.getValueTimeout(), actor.isAsync());
            } else if (actor.getClassType().equals("ShutterActor")) {
                List<DBShutterActor> matchList = loadShutterActors(actor.getId(),valueGroup.getId());
                if (matchList.size() == 1) {
                    DBShutterActor shutterActor = matchList.get(0);
                    act = datamodel.addShutterActor(valueGroup, actor.getId(), ValueType.of(actor.getValueType()), actor.getValueTimeout(), shutterActor.isShutterTiltSupport(), shutterActor.getShutterFullCloseDuration(), shutterActor.getShutterFullTiltDuration());
                } else {
                    throw new RuntimeException("Unexpected mapping of actor " + actor.getId());
                }
            } else if (actor.getClassType().equals("ToggleActor")) {
                act = datamodel.addToggleActor(valueGroup, actor.getId());
            } else if (actor.getClassType().equals("ValueActor")) {
                act = datamodel.addValueActor(valueGroup, actor.getId(), ValueType.of(actor.getValueType()), actor.getValueTimeout());
            } else if (actor.getClassType().equals("AudioPlaybackActor")) {
                List<DBAudioActor> matchList = loadAudioActors(actor.getId(), valueGroup.getId());
                if (matchList.size() == 1) {
                    DBAudioActor audioActor = matchList.get(0);
                    act = datamodel.addAudioPlaybackActor(valueGroup, actor.getId(), ValueType.of(actor.getValueType()), actor.getValueTimeout(), audioActor.getAudioDeviceIds(), audioActor.getAudioActivationRelayId(), audioActor.getAudioVolume(), audioActor.getAudioVolumeId(), audioActor.getAudioUrl(), audioActor.getAudioUrlId(), audioActor.getAudioCurrentTitleId(), audioActor.getAudioName());
                    if (!StringUtils.isEmpty(audioActor.getAudioCurrentTitleId())) {
                        ((AudioPlaybackActor) act).setAudioCurrentTitleValue((StringValue) datamodel.getValue(audioActor.getAudioCurrentTitleId()));
                    }
                } else {
                    throw new RuntimeException("Unexpected mapping of actor " + actor.getId());
                }
            } else if (actor.getClassType().equals("DoorActor")) {
                act = datamodel.addDoorActor(valueGroup, actor.getId(), ValueType.of(actor.getValueType()), actor.getValueTimeout());
            } else {
                LogFacade.w(TAG, "Unknown class type: " + actor.getClassType());
            }
        }

        for (AudioPlaybackSource audioPlaybackSource : loadAudioPlaybackSources()) {
            datamodel.addAudioPlaybackSource(audioPlaybackSource);
        }
    }

    private void loadMetadata(DatamodelBase datamodel) throws SQLException {
        for (KnownArea knownArea : loadKnownAreas()) {
            datamodel.addKnownArea(knownArea.getId(), knownArea.getName());
        }

        for (KnownRoom knownRoom : loadKnownRooms()) {
            KnownArea knownArea = datamodel.getKnownArea(knownRoom.getKnownAreaId());
            datamodel.addKnownRoom(knownArea, knownRoom.getId(), knownRoom.getName());
        }

        for (KnownDevice knownDevice : loadKnownDevices()) {
            datamodel.addKnownDevice(knownDevice.getId(), knownDevice.getServiceId(), knownDevice.getName(), knownDevice.isMandatory());
        }

        for (User user : loadUsers()) {
            datamodel.addUser(user.getId(), user.getName(), user.getRights());
        }
    }

    private void mergeDatamodel(DatamodelBase datamodel) throws SQLException {
        for (KnownRoomValues knownRoomsValue : loadKnownRoomValues()) {
            KnownRoom knownRoom = datamodel.getKnownRoom(knownRoomsValue.getRoomId());
            ValueBase value = datamodel.getValue(knownRoomsValue.getValueId(), knownRoomsValue.getValueGroupId());
            knownRoom.addValue(value);
            value.setKnownRoom(knownRoom);
        }

        for (KnownRoomActors knownRoomActor : loadKnownRoomActors()) {
            KnownRoom knownRoom = datamodel.getKnownRoom(knownRoomActor.getRoomId());
            ActorBase actor = datamodel.getActor(knownRoomActor.getActorId(), knownRoomActor.getValueGroupId());
            knownRoom.addActor(actor);
        }
    }


}
