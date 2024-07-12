package com.osh.sip;

import org.pjsip.pjsua2.OnCallMediaEventParam;

public interface MyAppObserver
{
    abstract void notifyRegState(int code, String reason, long expiration);
    abstract void notifyIncomingCall(MyCall call);
    abstract void notifyCallState(MyCall call);
    abstract void notifyCallMediaState(MyCall call);
    abstract void notifyBuddyState(MyBuddy buddy);
    abstract void notifyChangeNetwork();
    abstract void notifyCallMediaEvent(MyCall call, OnCallMediaEventParam prm);
}
