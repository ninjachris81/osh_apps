package com.osh.core.service;

import com.osh.core.manager.IMqttSupport;
import com.osh.core.user.User;

public interface IUserService  extends IMqttSupport {

    User getUser(String userId);

    boolean hasRight(String userId, String right);

}
