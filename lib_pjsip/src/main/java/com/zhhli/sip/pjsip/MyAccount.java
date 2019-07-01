package com.zhhli.sip.pjsip;

import android.text.TextUtils;

import com.zhhli.sip.SipCode;

import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AccountInfo;
import org.pjsip.pjsua2.AccountMediaConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.BuddyConfig;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnInstantMessageParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.pjmedia_srtp_use;

import java.util.ArrayList;

/**
 * des:
 *
 * @author zhhli
 * @date 2019/7/1
 */
public class MyAccount extends Account {
    public AccountConfig cfg;

    private boolean isRegActive = false;


    private SipCode sipCode ;
    private String state = "";


    public SipCode getSipCode() {
        return sipCode;
    }

    public String getState() {
        return state;

    }



    private MyAccount(AccountConfig config) {
        super();
        cfg = config;
    }

    public Builder getBuilder() {
        return builder;
    }

    public MyAccount setBuilder(Builder builder) {
        this.builder = builder;
        return this;
    }

    private Builder builder;


    public boolean isUseTLS() {
        return builder.useTLS;
    }


    public MyAccount modifySRTPMandatory() {
        AccountMediaConfig mediaConfig = new AccountMediaConfig();
        mediaConfig.setSrtpUse(pjmedia_srtp_use.PJMEDIA_SRTP_MANDATORY);
        mediaConfig.setSrtpSecureSignaling(0);

        cfg.setMediaConfig(mediaConfig);

        try {
            modify(cfg);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public MyAccount modifySRTPOptional() {
        AccountMediaConfig mediaConfig = new AccountMediaConfig();
        mediaConfig.setSrtpUse(pjmedia_srtp_use.PJMEDIA_SRTP_OPTIONAL);
        mediaConfig.setSrtpSecureSignaling(0);

        cfg.setMediaConfig(mediaConfig);

        try {
            modify(cfg);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        String msgStr = "";
        if (prm.getExpiration() == 0) {
            msgStr += "Unregistration";
            isRegActive = false;
        } else {
            msgStr += "Registration";
        }

        int code = prm.getCode().swigValue();

        if (code / 100 == 2) {
            msgStr += " successful";
            isRegActive = true;
        }else if(code == 403){
            isRegActive = false;
            msgStr += " failed: " + prm.getReason();
            try {
                setRegistration(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            isRegActive = false;
            msgStr += " failed: " + prm.getReason();
        }

        String state = msgStr;

        sipCode = new SipCode(code);

        MyEngine.getInstance().notifyRegState(this);
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        System.out.println("======== Incoming call ======== ");
        MyCall call = new MyCall(this, prm.getCallId());
        MyEngine.getInstance().notifyIncomingCall(call);
    }

    public AccountInfo getAccountInfo() {
        try {
            return getInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private boolean checkInfoNotNull(){
        return getAccountInfo() != null;
    }

    public boolean isReg(){
        if(checkInfoNotNull()){
            getAccountInfo().getRegIsActive();
        }

        return isRegActive ;
    }


    public String getCallUri(String number) {

        String transport = "udp";
        if (isUseTLS()) {
            transport = "tls";
        }
        String uri = String.format("sip:%s@%s;transport=%s", number, getBuilder().getServer(), transport);
        return uri;
    }

    public static class Builder {
        private String username;
        private String pwd;
        private String server;
        private String proxy;

        private boolean useTLS;

        public Builder() {
            useTLS = false;
        }

        public String getUsername() {
            return username;
        }

        public String getPwd() {
            return pwd;
        }

        public String getServer() {
            return server;
        }

        public String getProxy() {
            return proxy;
        }

        public Builder setUsername(String username) {
            this.username = username;
            return this;
        }

        public Builder setPwd(String pwd) {
            this.pwd = pwd;
            return this;
        }

        public Builder setServer(String server) {
            this.server = server;
            return this;
        }

        public Builder setProxy(String proxy) {
            this.proxy = proxy;
            return this;
        }

        public boolean isUseTLS() {
            return useTLS;
        }

        public Builder setUseTLS(boolean useTLS) {
            this.useTLS = useTLS;
            return this;
        }

        public MyAccount build(){

            if(TextUtils.isEmpty(server)
                    || TextUtils.isEmpty(username)
                    || TextUtils.isEmpty(pwd)){
                return null;
            }

            AccountConfig accountConfig = new AccountConfig();
            String accountId = String.format("sip:%s@%s",username,server);
            accountConfig.setIdUri(accountId);

            String registrar = String.format("sip:%s;transport=%s", server,
                    useTLS ? "tls" : "udp");
            accountConfig.getRegConfig().setRegistrarUri(registrar);

            if (proxy != null && proxy.length() != 0) {
                proxy = String.format("sip:%s;transport=%s", proxy,
                        useTLS ? "tls" : "udp");
                StringVector proxies = accountConfig.getSipConfig().getProxies();
                proxies.clear();
                proxies.add(proxy);
            }

            if (useTLS) {
                AccountMediaConfig mediaConfig = new AccountMediaConfig();
                mediaConfig.setSrtpUse(pjmedia_srtp_use.PJMEDIA_SRTP_OPTIONAL);
                mediaConfig.setSrtpSecureSignaling(0);
                accountConfig.setMediaConfig(mediaConfig);
            }

            accountConfig.getNatConfig().setIceEnabled(false);

            AuthCredInfoVector creds = accountConfig.getSipConfig().getAuthCreds();
            creds.clear();
            creds.add(new AuthCredInfo("Digest", "*", username, 0, pwd));


            MyAccount acc = new MyAccount(accountConfig);
            try {
                acc.create(accountConfig);
            } catch (Exception e) {
                e.printStackTrace();
            }

            acc.setBuilder(this);
            return acc;
        }
    }
}
