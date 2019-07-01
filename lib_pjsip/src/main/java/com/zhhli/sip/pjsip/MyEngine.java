/* $Id: MyEngine.java 5361 2016-06-28 14:32:08Z nanang $ */
/*
 * Copyright (C) 2013 Teluu Inc. (http://www.teluu.com)
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.zhhli.sip.pjsip;

import android.util.Log;

import com.zhhli.sip.util.SipAudioManager;
import com.zhhli.sip.SipCode;
import com.zhhli.sip.util.SipUtils;
import com.zhhli.sip.UIThread;

import org.pjsip.pjsua2.AudDevManager;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.CodecInfo;
import org.pjsip.pjsua2.CodecInfoVector;
import org.pjsip.pjsua2.CodecParam;
import org.pjsip.pjsua2.CodecParamInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.IpChangeParam;
import org.pjsip.pjsua2.LogConfig;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pj_log_decoration;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsip_transport_type_e;
import org.pjsip.pjsua2.pjsua_call_flag;
import org.pjsip.pjsua2.pjsua_call_media_status;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class MyEngine implements MyAppObserver {

    static {
        System.loadLibrary("bcg729");
        System.loadLibrary("pjsua2");
        System.out.println("Library loaded");
    }



    static Endpoint ep = new Endpoint();

    private final int LOG_LEVEL = 4;
    public ArrayList<MyAccount> accList = new ArrayList<MyAccount>();

    private EpConfig epConfig = new EpConfig();
    /* Maintain reference to log writer to avoid premature cleanup by GC */
    private MyLogWriter logWriter;

    private UIThread uiThread;

    private MyCall currentCall = null;

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(2);


    private MyEngine(){}

    private static class MyEngineHolder{
        static MyEngine INSTANCE = new MyEngine();
    }

    public static MyEngine getInstance(){
        return MyEngineHolder.INSTANCE;
    }



    public void init(UIThread uiThread) {
        this.uiThread = uiThread;
        /* Create endpoint */
        try {
            ep.libCreate();
        } catch (Exception e) {
            return;
        }

        /* Override log level setting */
        epConfig.getLogConfig().setLevel(LOG_LEVEL);
        epConfig.getLogConfig().setConsoleLevel(LOG_LEVEL);

        /* Set log config. */
        LogConfig log_cfg = epConfig.getLogConfig();
        logWriter = new MyLogWriter();
        log_cfg.setWriter(logWriter);
        log_cfg.setDecor(log_cfg.getDecor() &
                ~(pj_log_decoration.PJ_LOG_HAS_CR.swigValue() |
                        pj_log_decoration.PJ_LOG_HAS_NEWLINE.swigValue()));


        /* Set ua config. */
        UaConfig ua_cfg = epConfig.getUaConfig();
        ua_cfg.setUserAgent("Pjsua2 Android " + ep.libVersion().getFull());


        /* Init endpoint */
        try {
            ep.libInit(epConfig);
        } catch (Exception e) {
            return;
        }

        /* Create transports. */
        try {
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, new TransportConfig());
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, new TransportConfig());
            ep.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TLS, new TransportConfig());
        } catch (Exception e) {
            System.out.println(e);
        }


        try {
            ep.libStart();
        } catch (Exception e) {
            return;
        }

        // add zero-li
        setCodecPriority();
    }





    public void utilLogWrite(int prmLevel, String tag, String prmMsg){
        ep.utilLogWrite(prmLevel, tag, prmMsg);
    }




    public void deinit() {

        /* Try force GC to avoid late destroy of PJ objects as they should be
         * deleted before lib is destroyed.
         */
        Runtime.getRuntime().gc();

        /* Shutdown pjsua. Note that Endpoint destructor will also invoke
         * libDestroy(), so this will be a test of double libDestroy().
         */
        try {
            ep.libDestroy();
        } catch (Exception e) {
        }

        /* Force delete Endpoint here, to avoid deletion from a non-
         * registered thread (by GC?).
         */
        ep.delete();
        ep = null;
    }

    /**
     * 1. 不使用 speex, G722, GSM
     * 2. iLBC 的优先级排在 pcmu和pcma 后面
     */
    private void setCodecPriority() {
        try {
            CodecInfoVector codecInfoVector = ep.codecEnum();
            for (int i = 0; i < codecInfoVector.size(); i++) {
                CodecInfo codecInfo = codecInfoVector.get(i);
                Log.e("setCodecPriority", "before: " + codecInfo.toString() + "\n");

                String codecId = codecInfo.getCodecId();
                if (codecInfo.getCodecId().contains("speex")
                        || codecInfo.getCodecId().contains("G722")
                        || codecInfo.getCodecId().contains("GSM")) {
                    short s = 0;
                    ep.codecSetPriority(codecId, s);
                }

                if (codecInfo.getCodecId().contains("iLBC")) {
                    ep.codecSetPriority(codecId, (short) 1);

                }

                CodecParam codecParam = ep.codecGetParam(codecId);
                CodecParamInfo codecParamInfo = codecParam.getInfo();
                Log.e("setCodecPriority", "before: " + codecParamInfo.toString() + "\n");

            }


            codecInfoVector = ep.codecEnum();
            for (int i = 0; i < codecInfoVector.size(); i++) {

                CodecInfo codecInfo = codecInfoVector.get(i);
                Log.e("setCodecPriority", "after: " + codecInfo.toString() + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean delAccount(MyAccount account) {
        try {
            account.delete();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }


    public void register(MyAccount account) {
        try {
            account.setRegistration(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void unregister(MyAccount account) {
        try {
            account.setRegistration(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param callUri <sip:%s@%s;box_uid=%d;a=%d>
     * @param account
     * @return
     */
    public SipCode callOut(String callUri, MyAccount account) {
        if (!account.isReg()) {
            utilLogWrite(4,"sip","sip未注册成功！");
            return SipCode.refuseUse();
        }

        if (currentCall != null || SipAudioManager.getInstance().isPSTNCalling()){
            //系统忙。
            return SipCode.busy();
        }

        if (account.isUseTLS()) {
            account.modifySRTPMandatory();
        }

        MyCall call = new MyCall(account, -1);
        CallOpParam prm = new CallOpParam(true);

        try {
            call.makeCall(callUri, prm);
            currentCall = call;
            return SipCode.ok();
        } catch (Exception e) {
            call.delete();
            e.printStackTrace();
            return SipCode.refuseUse();
        }


    }


    public void hangup() {
        if (currentCall != null) {
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);

            try {
                currentCall.hangup(prm);
                resetCurrentCall();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void resetCurrentCall() {
        if (currentCall != null) {
            if (currentCall.account.isUseTLS()) {
                currentCall.account.modifySRTPOptional();
            }
            currentCall.delete();
            currentCall = null;
        }
    }


    public boolean answerCall() {
        if (currentCall == null) {
            return false;
        }

        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            currentCall.answer(prm);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    public boolean setHold(boolean hold) {
        if (currentCall == null) {
            return false;
        }
        CallOpParam param = new CallOpParam();

        try {
            if (hold) {
                currentCall.setHold(param);
            } else {
                CallSetting opt = param.getOpt();
                opt.setAudioCount(1);
                opt.setVideoCount(0);
                opt.setFlag(pjsua_call_flag.PJSUA_CALL_UNHOLD.swigValue());

                currentCall.reinvite(param);
            }
        } catch (Exception exc) {
            String operation = hold ? "hold" : "unhold";
            return false;
        }

        return true;
    }


    public void sendDTMF(String digits) {
        if (currentCall == null) {
            return;
        }

        try {
            currentCall.dialDtmf(digits);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean localMute = false;

    /**
     * Utility method to mute/unmute the device microphone during a call.
     *
     * @param mute true to mute the microphone, false to un-mute it
     */
    public void setMute(boolean mute) {
        if (currentCall == null) {
            return;
        }
        // return immediately if we are not changing the current state
        if ((localMute && mute) || (!localMute && !mute)) {
            return;
        }

        CallInfo info;
        try {
            info = currentCall.getInfo();
        } catch (Exception exc) {
            return;
        }

        for (int i = 0; i < info.getMedia().size(); i++) {
            Media media = currentCall.getMedia(i);
            CallMediaInfo mediaInfo = info.getMedia().get(i);

            if (mediaInfo.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO
                    && media != null
                    && mediaInfo.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE) {
                AudioMedia audioMedia = AudioMedia.typecastFromMedia(media);

                // connect or disconnect the captured audio
                try {
                    AudDevManager mgr = ep.audDevManager();

                    if (mute) {
                        mgr.getCaptureDevMedia().stopTransmit(audioMedia);
                        localMute = true;
                    } else {
                        mgr.getCaptureDevMedia().startTransmit(audioMedia);
                        localMute = false;
                    }

                } catch (Exception exc) {
                    utilLogWrite(LOG_LEVEL,"setMute", "setMute: error while connecting audio media to sound device "+exc);
                }
            }
        }
    }

    @Override
    public void notifyRegState(MyAccount account) {
        uiThread.postMainEvent(UIThread.MSG_TYPE.REG_STATE, account);
    }

    @Override
    public void notifyIncomingCall(MyCall call) {
        CallOpParam prm = new CallOpParam();
        /* Only one call at anytime */
        if (currentCall != null || SipAudioManager.getInstance().isPSTNCalling()) {
            // 多路呼叫
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);
            try {
                call.hangup(prm);
            } catch (Exception e) {
            }

            deleteCall(call);
            return;
        }

        String number = "";
        try {
            CallInfo callInfo = call.getInfo();

            number = SipUtils.getNumber(callInfo.getRemoteUri());

        } catch (Exception e) {
            e.printStackTrace();
        }

        currentCall = call;

        /*shVoIPListener.onIncomingCall(number);*/

        uiThread.postMainEvent(UIThread.MSG_TYPE.INCOMING_CALL, number);
    }

    /**
     * gc
     * https://www.pjsip.org/docs/book-latest/html/intro_pjsua2.html#problems-with-garbage-collection
     * 延迟 1秒销毁call,
     * 解决 即时销毁，会发出 sip 500
     *
     * @param call
     */
    private void deleteCall(final Call call) {
        executorService.schedule(new Runnable() {
            @Override
            public void run() {
                if (call != null) {
                    call.delete();
                }
            }
        }, 1000, TimeUnit.MILLISECONDS);

    }

    @Override
    public void notifyCallState(MyCall call) {
        if (currentCall == null || call == null || call.getId() != currentCall.getId()) {
            System.out.println("Call state event received, but call info is invalid");
            return;
        }

        if (!call.isActive()) {
            uiThread.postMainEvent(UIThread.MSG_TYPE.CALL_END, null);
            resetCurrentCall();
            return;
        }

        CallInfo callInfo = null;
        try {
            callInfo = call.getInfo();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }


        //==========================================================


        String call_state = "";

        if (callInfo.getState() == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
            //振铃中
            uiThread.postMainEvent(UIThread.MSG_TYPE.CALL_RING, callInfo);
        }

        // before ack
        if (callInfo.getState().swigValue() < pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue()) {
            if (callInfo.getRole() == pjsip_role_e.PJSIP_ROLE_UAS) {
                /* Default button texts are already 'Accept' & 'Reject' */
                // 被叫
                call_state = "Incoming call..";
            } else {
                /*buttonHangup.setText("Cancel");*/
                call_state = callInfo.getStateText();
            }
        } else if (callInfo.getState().swigValue() >= pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue()) {
            /*buttonAccept.setVisibility(View.GONE);*/
            call_state = callInfo.getStateText();
            if (callInfo.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                /*buttonHangup.setText("Hangup");*/
                uiThread.postMainEvent(UIThread.MSG_TYPE.CALL_OK, callInfo);
            } else if (callInfo.getState() == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                /*buttonHangup.setText("OK");*/
                call_state = "Call disconnected: " + callInfo.getLastReason();
                uiThread.postMainEvent(UIThread.MSG_TYPE.CALL_END, callInfo);
                resetCurrentCall();
            }
        }

        Log.d("notifyCallState", "call_state:" + call_state + "\t sipCode: "
                + callInfo.getLastStatusCode().swigValue());


    }

    @Override
    public void notifyCallMediaState(MyCall call) {

    }

    @Override
    public void notifyBuddyState(MyBuddy buddy) {

    }

    @Override
    public void notifyChangeNetwork() {
        try {
            System.out.println("Network change detected");
            IpChangeParam changeParam = new IpChangeParam();
            ep.handleIpChange(changeParam);
        } catch (Exception e) {
            System.out.println(e);
        }
    }



}
