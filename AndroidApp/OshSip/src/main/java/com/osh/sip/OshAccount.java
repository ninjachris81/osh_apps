package com.osh.sip;

import static android.content.Context.RECEIVER_NOT_EXPORTED;

import android.content.Context;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import net.gotev.sipservice.BroadcastEventEmitter;
import net.gotev.sipservice.BroadcastEventReceiver;
import net.gotev.sipservice.SipAccountData;
import net.gotev.sipservice.SipServiceCommand;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.pjsip_status_code;

public class OshAccount extends MyAccount {














    private CallbackReceiver callbackReceiver;

    public interface CallbackReceiver {

        void registrationSuccess();

        void registrationFailure(int registrationStateCode);

        void onIncomingCall(Context receiverContext, String accountID, int callID, String displayName, String remoteUri, boolean isVideo);

        void onOutgoingCall(Context receiverContext, String accountID, int callID, String number, boolean isVideo, boolean isVideoConference);
    }

    private SipAccountData mAccount;
    private String mAccountId;

    private Context context;

    public OshAccount(AccountConfig config) {
        super(config);
    }

    public void setCallbackReceiver(CallbackReceiver callbackReceiver) {
        this.callbackReceiver = callbackReceiver;
    }

    public void onCreate(Context context) {
        this.context = context;
        mReceiver.register(context);
    }

    public void login(String sipHost, long sipPort, String sipRealm, String sipUsername, String sipPassword) {
        mAccount = new SipAccountData();
        mAccount.setHost(sipHost);
        mAccount.setPort(sipPort);
        mAccount.setRealm(sipRealm); //realm：sip:1004@192.168.2.243
        mAccount.setUsername(sipUsername);
        mAccount.setPassword(sipPassword);
        tryLogin();
    }

    private void tryLogin() {
        mAccountId = SipServiceCommand.setAccount(context, mAccount);
    }

    public void onDestroy() {
        mReceiver.unregister(context);
        if (mAccount != null) {
            SipServiceCommand.removeAccount(context, mAccountId);
        }
    }

    public void makeCall(String callNumber) {
        SipServiceCommand.makeCall(context, mAccountId, callNumber, false, false);
    }

    public BroadcastEventReceiver mReceiver = new BroadcastEventReceiver() {
        @Override
        public void onRegistration(String accountID, int registrationStateCode) {
            super.onRegistration(accountID, registrationStateCode);

            if (registrationStateCode == pjsip_status_code.PJSIP_SC_OK) {
                callbackReceiver.registrationSuccess();
            } else {
                callbackReceiver.registrationFailure(registrationStateCode);
                // try re-login
                tryLogin();
            }
        }

        @Override
        public void onIncomingCall(String accountID, int callID, String displayName, String remoteUri, boolean isVideo) {
            super.onIncomingCall(accountID, callID, displayName, remoteUri, isVideo);
            callbackReceiver.onIncomingCall(getReceiverContext(), accountID, callID, displayName, remoteUri, isVideo);
        }

        @Override
        public void onOutgoingCall(String accountID, int callID, String number, boolean isVideo, boolean isVideoConference, boolean isTransfer) {
            super.onOutgoingCall(accountID, callID, number, isVideo, isVideoConference, isTransfer);
            callbackReceiver.onOutgoingCall(getReceiverContext(), accountID, callID, number, isVideo, isVideoConference);
        }
    };

}
