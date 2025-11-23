package com.osh.core.service;

import com.osh.core.actor.ActorBase;
import com.osh.core.actor.ActorCmds;
import com.osh.core.manager.IMqttSupport;

import java.util.List;

public interface IActorService extends IMqttSupport {

	void registerActor(ActorBase actorBase);

    void publishCmd(ActorBase actor, ActorCmds actorCmd);

    void publishCmd(ActorBase actor, ActorCmds actorCmd, Object value);

    ActorBase getActor(String fullId);

    List<ActorBase> getActors(Class<? extends ActorBase> classFilter);
}
