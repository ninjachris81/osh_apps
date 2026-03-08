package com.osh.core.service.impl;

import com.osh.core.communication.MessageBase;
import com.osh.core.service.IDatamodelService;
import com.osh.core.service.IUserService;
import com.osh.core.user.User;

public class UserServiceImpl implements IUserService {

    private final IDatamodelService datamodelService;

    public UserServiceImpl(IDatamodelService datamodelService) {
        this.datamodelService = datamodelService;
    }

    @Override
    public MessageBase.MESSAGE_TYPE getMessageType() {
        return null;
    }

    @Override
    public void handleReceivedMessage(MessageBase msg) {

    }

    @Override
    public User getUser(String userId) {
        return datamodelService.getDatamodel().getUsers().get(userId);
    }

    @Override
    public boolean hasRight(String userId, String right) {
        User user = getUser(userId);
        if (user != null) {
            return user.hasRight(right);
        }
        return false;
    }
}
