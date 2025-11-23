package com.osh.core.manager;

import com.osh.core.communication.MessageBase;

public interface IMqttSupport {
	
	public MessageBase.MESSAGE_TYPE getMessageType();

	public void handleReceivedMessage(MessageBase msg);

}
