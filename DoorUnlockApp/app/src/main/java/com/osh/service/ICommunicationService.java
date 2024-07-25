package com.osh.service;

import com.osh.communication.MessageBase;
import com.osh.manager.IMqttSupport;

public interface ICommunicationService {

    void connectMqtt();

    void registerMessageType(MessageBase.MESSAGE_TYPE messageType, IMqttSupport service);

    public boolean sendMessage(MessageBase msg);

    boolean isConnected();

}
