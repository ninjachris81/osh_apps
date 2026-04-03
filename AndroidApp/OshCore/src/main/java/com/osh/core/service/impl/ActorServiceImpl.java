package com.osh.core.service.impl;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.osh.core.actor.ActorBase;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ActorMessage;
import com.osh.core.actor.ShutterActor;
import com.osh.core.communication.MessageBase;
import com.osh.core.communication.MessageBase.MESSAGE_TYPE;
import com.osh.core.datamodel.meta.KnownArea;
import com.osh.core.datamodel.meta.KnownRoom;
import com.osh.core.service.IActorService;
import com.osh.core.service.ICommunicationService;
import com.osh.core.service.IValueService;
import com.osh.core.value.ValueMessage;
import com.osh.core.value.ValueType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ActorServiceImpl implements IActorService {

	private static final Logger log = LoggerFactory.getLogger(ActorServiceImpl.class);

    protected Map<String, ActorBase> actors = new HashMap<>();

	private final ICommunicationService communicationService;

	private final IValueService valueService;

	public ActorServiceImpl(ICommunicationService communicationService, IValueService valueService) {
		this.communicationService = communicationService;
		this.valueService = valueService;
		communicationService.registerMessageType(getMessageType(), this);
	}

	@Override
	public void registerActor(ActorBase actor) {
		actors.put(actor.getFullId(), actor);
		valueService.registerValue(actor);
	}

	@Override
	public void publishCmd(ActorBase actor, ActorCmds actorCmd) {
		ActorMessage msg = new ActorMessage(actor, null, actorCmd);
		communicationService.sendMessage(msg);
	}

	@Override
	public void publishCmd(ActorBase actor, ActorCmds actorCmd, Object value) {
		ActorMessage msg = new ActorMessage(actor, value, actorCmd);
		communicationService.sendMessage(msg);

		if (actorCmd != ActorCmds.ACTOR_CMD_SET_VALUE) {
			ValueMessage valueMessage = new ValueMessage(actor);
			communicationService.sendMessage(valueMessage);
		}
	}

    @Override
	public ActorBase getActor(String fullId) {
		return actors.get(fullId);
	}

	@Override
	public List<ActorBase> getActors(Class<? extends ActorBase> classFilter) {
		return actors.values().stream().filter(a -> a.getClass().equals(classFilter)).collect(Collectors.toList());
	}

    @Override
    public List<ActorBase> getActors(KnownRoom knownRoom, ValueType valueType) {
        return knownRoom.getActors().values().stream().filter(a -> a.getKnownRoom() != null && Objects.equals(a.getKnownRoom().getId(), knownRoom.getId()) && (valueType != ValueType.VT_UNKNOWN && a.getValueType() == valueType)).collect(Collectors.toList());
    }

    @Override
    public List<ActorBase> getActors(KnownArea knownArea, ValueType valueType) {
        return actors.values().stream().filter(a -> a.getKnownRoom() != null &&  a.getKnownRoom().getKnownArea() != null && Objects.equals(a.getKnownRoom().getKnownArea().getId(), knownArea.getId()) && (valueType != ValueType.VT_UNKNOWN && a.getValueType() == valueType) ).collect(Collectors.toList());
    }

    @Override
    public <T extends ActorBase> List<T> getActors(Class<T> classFilter, ValueType valueType) {
        return (List<T>) actors.values().stream().filter(a -> (valueType != ValueType.VT_UNKNOWN && a.getValueType() == valueType) && a.getClass().equals(classFilter)).collect(Collectors.toList());
    }

    @Override
	public MESSAGE_TYPE getMessageType() {
		return MESSAGE_TYPE.MESSAGE_TYPE_ACTOR;
	}

	@Override
	public void handleReceivedMessage(MessageBase msg) {
		if (msg instanceof ActorMessage) {
			if (actors.containsKey(((ActorMessage) msg).getFullId())) {
				ActorBase actor = actors.get(((ActorMessage) msg).getFullId());
				actor.triggerCmd(((ActorMessage) msg).getCmd(), "Message received");
			}
		} else {
			log.warn("Invalid actor: " + ((ActorMessage) msg).getFullId());
		}
	}

}
