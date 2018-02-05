package com.zhhli.sip;

import android.content.Context;

import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.AuthCredInfoVector;
import org.pjsip.pjsua2.StringVector;

/**
 * des:
 *
 * @author my
 */

public class PjsipSdk {

    private MyApp app = null;
    private MyCall currentCall = null;
    private MyAccount account = null;
    private AccountConfig accCfg = null;

    private MyAppObserver myAppObserver;

    private Context context;


    public PjsipSdk() {
        app = new MyApp();

    }

    public void init(Context context, MyAppObserver myAppObserver){
        this.context = context.getApplicationContext();
        this.myAppObserver = myAppObserver;
        if (myAppObserver == null){
            throw new NullPointerException("myAppObserver = null");
        }
        if(context == null){
            throw new NullPointerException("context = null");
        }

        app.init(myAppObserver,context.getFilesDir().getAbsolutePath());
    }

    private void setAccCfgTest(String username,String pwd,String service, String proxy){
        accCfg = new AccountConfig();
        accCfg.setIdUri("sip:159@shbox");
        accCfg.getNatConfig().setIceEnabled(false);
        accCfg.getVideoConfig().setAutoTransmitOutgoing(true);
        accCfg.getVideoConfig().setAutoShowIncoming(true);

        // zhhli
        accCfg.getRegConfig().setRegistrarUri("sip:139.224.68.122:6060");
        AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
        creds.clear();
        creds.add(new AuthCredInfo("Digest", "*", "159", 0,
                "123456"));
        account = app.addAcc(accCfg);
    }

    public void setAccCfg(String username, String pwd, String server, String proxy){
        accCfg = new AccountConfig();
        String acc_id = "sip:"+ username+"@"+server;
        String registrar = "sip:"+ server;

        accCfg.setIdUri(acc_id);
        accCfg.getRegConfig().setRegistrarUri(registrar);

        AuthCredInfoVector creds = accCfg.getSipConfig().getAuthCreds();
        creds.clear();
        creds.add(new AuthCredInfo("Digest", "*", username, 0, pwd));


        if (proxy!= null && proxy.length() != 0) {
            StringVector proxies = accCfg.getSipConfig().getProxies();
            proxies.clear();
            proxies.add(proxy);
        }
        /* Enable ICE */
        accCfg.getNatConfig().setIceEnabled(false);


        MyAccount acc = new MyAccount(accCfg);
        try {
            acc.create(accCfg);
        } catch (Exception e) {
            acc = null;
        }



    }




}
