package com.osh.core.service;

import com.osh.core.communication.MessageBase;
import com.osh.core.manager.IMqttSupport;
import com.osh.core.utils.IObservableBoolean;

public interface ICommunicationService {

    void connectMqtt();

    void registerMessageType(MessageBase.MESSAGE_TYPE messageType, IMqttSupport service);

    public boolean sendMessage(MessageBase msg);

	public IObservableBoolean connectedState();

    public void datamodelReady();

}
