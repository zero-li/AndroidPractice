package com.zhhli.sip;

import android.content.Context;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_status_code;


/**
 * des:
 *
 * @author my
 */

interface SipListener {
    /**
     * 注册状态
     *
     * @param isReg   true ---> reg, false--->unregister
     * @param isRegOk
     * @param sipCode
     * @param desc
     */
    void notifyRegState(boolean isReg, boolean isRegOk, int sipCode, String desc);

    void notifyIncomingCall(MyCall call);

    /**
     *
     * @param callUrl
     * @param statusCode
     * @param codeStatus
     */
    void notifyCallState(String callUrl, int statusCode, String codeStatus);



}

public class PjsipSdk implements MyAppObserver {

    private MyApp app = null;
    private MyCall currentCall = null;
    private MyAccount account = null;
    private AccountConfig accCfg = null;
    private String server = null;

    private SipListener sipListener;

    private Context context;


    public PjsipSdk() {
        app = new MyApp();

    }

    public void init(Context context, SipListener sipListener) {
        this.context = context.getApplicationContext();
        this.sipListener = sipListener;
        if (sipListener == null) {
            throw new NullPointerException("sipListener = null");
        }
        if (context == null) {
            throw new NullPointerException("context = null");
        }

        app.init(this, context.getFilesDir().getAbsolutePath());
    }

    /**
     * 帐号设置
     * @param username 用户名
     * @param pwd       密码
     * @param server    服务器地址 IP:port
     * @param proxy     sbc 代理地址, 填空表示 不使用 sbc
     */
    public void setAccCfg(String username, String pwd, String server, String proxy) {
        accCfg = new AccountConfig();
        String acc_id = "sip:" + username + "@" + server;
        String registrar = "sip:" + server;


        accCfg.setIdUri(acc_id);
        accCfg.getRegConfig().setRegistrarUri(registrar);

        AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
        creds.clear();
        creds.add(new AuthCredInfo("Digest", "*", username, 0, pwd));


        if (proxy != null && proxy.length() != 0) {
            proxy = "sip:" + proxy;
            StringVector proxies = accCfg.getSipConfig().getProxies();
            proxies.clear();
            proxies.add(proxy);
        }
        /* Enable ICE 默认关闭 */
        accCfg.getNatConfig().setIceEnabled(false);


        MyAccount acc = new MyAccount(accCfg);
        try {
            acc.create(accCfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 呼出
     * @param calledNumber
     */
    public void callOut(String calledNumber) {
        String called_uri = String.format("sip:%s@%s", calledNumber, server);

        MyCall call = new MyCall(account, -1);
        CallOpParam prm = new CallOpParam(true);
        try {
            call.makeCall(called_uri, prm);
        } catch (Exception e) {
            call.delete();
            System.out.println(e);
            return;
        }

        currentCall = call;
        //showCallActivity();

    }

    public void answer() {
        CallOpParam prm = new CallOpParam();
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        try {
            currentCall.answer(prm);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void hangup() {
        if (currentCall != null) {
            CallOpParam prm = new CallOpParam();
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
            try {
                currentCall.hangup(prm);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void notifyRegState(pjsip_status_code code, String reason, int expiration) {
        String msg_str = "";
        boolean isReg;
        boolean isRegOk;
        if (expiration == 0) {
            msg_str += "Unregistration";
            isReg = false;
        } else {
            msg_str += "Registration";
            isReg = true;
        }

        int sipCode = code.swigValue();

        if (sipCode / 100 == 2) {
            msg_str += " successful";
            isRegOk = true;
        } else {
            isRegOk = false;
            msg_str += " failed: " + reason;
        }

        sipListener.notifyRegState(isReg, isRegOk, sipCode, msg_str);


    }

    @Override
    public void notifyIncomingCall(MyCall call) {

	    /* Incoming call */
        CallOpParam prm = new CallOpParam();

	    /* Only one call at anytime */
        if (currentCall != null) {
            prm.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);
            try {
                call.hangup(prm);
            } catch (Exception e) {
            }

            return;
        }

	    /* Answer with ringing */
        prm.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
        try {
            call.answer(prm);
        } catch (Exception e) {
        }

        currentCall = call;
        //showCallActivity();
        sipListener.notifyIncomingCall(call);

    }

    @Override
    public void notifyCallState(MyCall call) {

        /*
      0  PJSIP_INV_STATE_NULL 	Before INVITE is sent or received

      1  PJSIP_INV_STATE_CALLING 	After INVITE is sent

      2  PJSIP_INV_STATE_INCOMING 	After INVITE is received.

      3  PJSIP_INV_STATE_EARLY 	After response with To tag.

      4  PJSIP_INV_STATE_CONNECTING 	After 2xx is sent/received.

      5  PJSIP_INV_STATE_CONFIRMED 	After ACK is sent/received.

      6  PJSIP_INV_STATE_DISCONNECTED 	Session is terminated.
         */
        String call_state = "";

        CallInfo ci;
        try {
            ci = call.getInfo();
        } catch (Exception e) {
            ci = null;
            return;
        }

        if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAC) {
            // 主叫
           /* buttonAccept.setVisibility(View.GONE);*/
        }

        if (ci.getState().swigValue() <
                pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue()) {
            if (ci.getRole() == pjsip_role_e.PJSIP_ROLE_UAS) {
                call_state = "Incoming call..";
        /* Default button texts are already 'Accept' & 'Reject' */
            } else {
                /*buttonHangup.setText("Cancel");*/
                call_state = ci.getStateText();
            }
        } else if (ci.getState().swigValue() >=
                pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED.swigValue()) {
            /*buttonAccept.setVisibility(View.GONE);*/
            call_state = ci.getStateText();
            if (ci.getState() == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                /*buttonHangup.setText("Hangup");*/
            } else if (ci.getState() ==
                    pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                /*buttonHangup.setText("OK");*/
                call_state = "Call disconnected: " + ci.getLastReason();
            }
        }


        sipListener.notifyCallState(ci.getRemoteUri(), ci.getState().swigValue(), call_state);
    }

    @Override
    public void notifyCallMediaState(MyCall call) {
        // video use
    }

    @Override
    public void notifyBuddyState(MyBuddy buddy) {

    }

    @Override
    public void notifyChangeNetwork() {
        app.handleNetworkChange();
    }
}
