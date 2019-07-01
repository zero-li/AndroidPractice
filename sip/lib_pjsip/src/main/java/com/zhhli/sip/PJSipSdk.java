package com.zhhli.sip;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.zhhli.sip.pjsip.MyAccount;
import com.zhhli.sip.pjsip.MyEngine;
import com.zhhli.sip.util.SipAudioManager;
import com.zhhli.sip.util.SipNetworkReceiver;
import com.zhhli.sip.util.SipUtils;

import org.pjsip.pjsua2.CallInfo;


public class PJSipSdk implements UIThread {

    private String TAG = getClass().getSimpleName();

    private MyEngine myEngine;
    private MyAccount account = null;


    private SipListener sipListener;

    private SipAudioManager sipAudioManager;
    private SipAudioManager.AudioListener audioListener;

    private Handler mainHandler;

    private SipNetworkReceiver.SHNetworkObserver networkObserver;


    private PJSipSdk() {
        mainHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                try {
                    return handleMainEvent(message);
                } catch (Exception e) {
                    Log.e(e.getMessage(), TAG + " handler msg.what : "
                            + message.what + " ====" + e.toString());
                }
                return false;
            }
        });

        audioListener = new SipAudioManager.AudioListener() {
            @Override
            public void play() {

            }

            @Override
            public void pause() {

            }
        };

        networkObserver = new SipNetworkReceiver.SHNetworkObserver() {
            @Override
            public void onChange(boolean connect) {
                if (connect && myEngine != null) {
                    myEngine.notifyChangeNetwork();
                }
            }
        };

        myEngine = MyEngine.getInstance();

    }

    private static class PJSipSdkHolder {
        static PJSipSdk INSTANCE = new PJSipSdk();
    }

    public static PJSipSdk getInstance(){
        return PJSipSdkHolder.INSTANCE;
    }

    public void init(Context context) {
        SipUtils.init(context);

        myEngine.init(this);

        sipAudioManager = SipAudioManager.getInstance();

        SipNetworkReceiver.registerReceiver(context, networkObserver);

    }
    public void setSipListener(SipListener sipListener) {
        this.sipListener = sipListener;
        if (sipListener == null) {
            throw new NullPointerException("sipListener = null");
        }
    }

    /**
     * 帐号设置
     * @param username 用户名
     * @param pwd       密码
     * @param server    服务器地址 IP:port
     * @param proxy     sbc 代理地址, 填空表示 不使用 sbc
     */
    public void addAccount(String username, String pwd, String server, String proxy, boolean userTLS) {
        MyAccount.Builder builder = new MyAccount.Builder()
                .setUsername(username)
                .setPwd(pwd)
                .setServer(server)
                .setProxy(proxy)
                .setUseTLS(userTLS);

        if (account != null) {
            myEngine.delAccount(account);
        }

        account = builder.build();
    }

    public void register() {
        if (account != null) {
            myEngine.register(account);
        }
    }

    public void unregister() {
        if (account != null) {
            myEngine.unregister(account);
        }
    }

    /**
     * 需要检查麦克风录音权限
     *
     * @param number 号码
     * @return 状态码
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public SipCode callOut(@NonNull String number) {

        String callUri = account.getCallUri(number);
        SipCode sipCode = myEngine.callOut(callUri, account);

        if (sipCode.getCode() == SipCode.Code.PJSIP_SC_OK) {
            sipAudioManager.requestAudioFocus(audioListener);
            sipAudioManager.setVoIPStreamMode();
        }

        if (ActivityCompat.checkSelfPermission(SipUtils.getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            return SipCode.refuseUse();
        }
        return sipCode;
    }

    /**
     * 需要检查麦克风录音权限
     */
    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    public void answerCall() {
        sipAudioManager.stopRingtone();
        sipAudioManager.setVoIPStreamMode();
        myEngine.answerCall();
    }

    public void hangup() {
        sipAudioManager.reset();
        myEngine.hangup();
    }

    public void setHoldOn(boolean hold) {
        myEngine.setHold(hold);
    }

    public void setSpeaker(boolean speaker) {
        sipAudioManager.setSpeaker(speaker);
    }

    public void setMute(boolean mute) {
        myEngine.setMute(mute);
    }

    public void sendDTMF(String str) {
        myEngine.sendDTMF(str);
    }



    public void deinit() {
//        NetworkReceiver.unregisterReceiver(SipUtils.getContext(), networkObserver);
        myEngine.deinit();
    }

    //-----==================================================================
    public void postMainEvent(int eventCode, Object data) {
        if (mainHandler != null) {
            mainHandler.sendMessage(mainHandler.obtainMessage(eventCode, data));
        }
    }

    private boolean handleMainEvent(Message message) {

        switch (message.what) {
            case MSG_TYPE.REG_STATE:
                MyAccount account = (MyAccount) message.obj;
                boolean success = account.isReg();
                if (success) {
                    sipListener.onRegisterSuccess(account);
                } else {
                    sipListener.onRegisterFail(account, account.getSipCode());
                }

                break;
            case MSG_TYPE.INCOMING_CALL:
                sipAudioManager.requestAudioFocus(audioListener);
                sipAudioManager.startRingtone();
                String number = (String) message.obj;
                sipListener.onIncomingCall(number);
                break;
            case MSG_TYPE.CALL_RING:
            case MSG_TYPE.CALL_OK:
                CallInfo callInfo = (CallInfo) message.obj;
                String numberRing = SipUtils.getNumber(callInfo.getRemoteUri());
                int lastStatusCode = callInfo.getLastStatusCode().swigValue();
                SipCode sipCode = new SipCode(lastStatusCode);

                if (message.what == MSG_TYPE.CALL_RING) {
                    sipListener.onCallRing(numberRing, sipCode);
                } else {
                    sipListener.onCallOK(numberRing, sipCode);
                }

                break;
            case MSG_TYPE.CALL_END:
                sipAudioManager.reset();
                CallInfo callInfoEnd = (CallInfo) message.obj;
                if (callInfoEnd != null) {
                    String numberEnd = SipUtils.getNumber(callInfoEnd.getRemoteUri());
                    int lastStatusCodeEnd = callInfoEnd.getLastStatusCode().swigValue();
                    SipCode sipCodeEnd = new SipCode(lastStatusCodeEnd);
                    sipListener.onCallEnd(numberEnd, sipCodeEnd);

                } else {
                    sipListener.onCallEnd("", new SipCode(SipCode.Code.PJSIP_SC_OK));
                }


                break;
            case MSG_TYPE.CALL_MEDIA_STATE:
                MyAccount call3 = (MyAccount) message.obj;

                break;
            case MSG_TYPE.CHANGE_NETWORK:

                break;
            default:

        }

        // Message not handled
        return false;
    }
}
