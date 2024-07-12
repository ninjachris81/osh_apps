package com.osh.sip;

import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.OnCallMediaEventParam;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.VideoPreview;
import org.pjsip.pjsua2.VideoPreviewOpParam;
import org.pjsip.pjsua2.VideoWindow;
import org.pjsip.pjsua2.pjmedia_dir;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsua2;
import org.pjsip.pjsua2.pjsua_call_media_status;

public class MyCall extends Call
{
    public VideoWindow vidWin;
    public VideoPreview vidPrev;

    public boolean vidPrevStarted;

    MyCall(MyAccount acc, int call_id)
    {
        super(acc, call_id);
        vidWin = null;
        vidPrev = null;
        vidPrevStarted = false;
    }

    @Override
    public void onCallState(OnCallStateParam prm)
    {
        try {
            CallInfo ci = getInfo();
            if (ci.getState() ==
                    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED)
            {
                if (vidPrevStarted)
                    vidPrev.stop();

                MyApp.ep.utilLogWrite(3, "MyCall", this.dump(true, ""));
            }
        } catch (Exception e) {
        }

        // Should not delete this call instance (self) in this context,
        // so the observer should manage this call instance deletion
        // out of this callback context.
        MyApp.observer.notifyCallState(this);
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm)
    {
        CallInfo ci;
        try {
            ci = getInfo();
        } catch (Exception e) {
            return;
        }

        CallMediaInfoVector cmiv = ci.getMedia();

        for (int i = 0; i < cmiv.size(); i++) {
            CallMediaInfo cmi = cmiv.get(i);
            if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                    (cmi.getStatus() ==
                            pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                            cmi.getStatus() ==
                                    pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD))
            {
                // connect ports
                try {
                    AudioMedia am = getAudioMedia(i);
                    MyApp.ep.audDevManager().getCaptureDevMedia().
                            startTransmit(am);
                    am.startTransmit(MyApp.ep.audDevManager().
                            getPlaybackDevMedia());
                } catch (Exception e) {
                    System.out.println("Failed connecting media ports" +
                            e.getMessage());
                    continue;
                }
            } else if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_VIDEO &&
                    cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE)
            {
                /* If videoPreview was started, stop it first in case capture device has changed */
                if (vidPrevStarted) {
                    try {
                        vidPrevStarted = false;
                        vidPrev.stop();
                        vidPrev.delete();
                        vidPrev = null;
                    } catch (Exception e) {}
                }

                if (cmi.getVideoIncomingWindowId() != pjsua2.INVALID_ID)
                    vidWin = new VideoWindow(cmi.getVideoIncomingWindowId());

                if ((cmi.getDir() & pjmedia_dir.PJMEDIA_DIR_ENCODING) != 0) {
                    vidPrev = new VideoPreview(cmi.getVideoCapDev());
                    if (!vidPrevStarted) {
                        try {
                            vidPrev.start(new VideoPreviewOpParam());
                            vidPrevStarted = true;
                        } catch (Exception e) {
                            System.out.println("Failed start video preview" +
                                    e.getMessage());
                            continue;
                        }
                    }
                }
            }
        }

        MyApp.observer.notifyCallMediaState(this);
    }

    @Override
    public void onCallMediaEvent(OnCallMediaEventParam prm) {
        MyApp.observer.notifyCallMediaEvent(this, prm);
    }
}
