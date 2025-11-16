package com.osh.service;

import com.osh.communication.MessageBase;
import com.osh.manager.IMqttSupport;
import com.osh.utils.IObservableBoolean;

public interface ICommunicationService {

    IObservableBoolean connectedState();

    void connectMqtt();

    void registerMessageType(MessageBase.MESSAGE_TYPE messageType, IMqttSupport service);

    public boolean sendMessage(MessageBase msg);

    boolean isConnected();

}
