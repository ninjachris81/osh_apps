package com.osh.core.service;

import com.osh.core.actor.ActorBase;
import com.osh.core.actor.ActorCmds;
import com.osh.core.actor.ShutterActor;
import com.osh.core.datamodel.meta.KnownRoom;
import com.osh.core.manager.IMqttSupport;
import com.osh.core.value.ValueType;

import java.util.List;

public interface IActorService extends IMqttSupport {

	void registerActor(ActorBase actorBase);

    void publishCmd(ActorBase actor, ActorCmds actorCmd);

    void publishCmd(ActorBase actor, ActorCmds actorCmd, Object value);

    ActorBase getActor(String fullId);

    List<ActorBase> getActors(Class<? extends ActorBase> classFilter);

    List<ActorBase> getActors(KnownRoom knownRoom, ValueType valueType);

}
