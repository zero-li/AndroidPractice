package com.zhhli.sip.pjsip;

import org.pjsip.pjsua2.pjsip_status_code;

/**
 * des:
 *
 * @author zhhli
 * @date 2019/7/1
 */ /* Interface to separate UI & engine a bit better */
interface MyAppObserver {
    void notifyRegState(MyAccount myAccount);

    void notifyIncomingCall(MyCall call);

    void notifyCallState(MyCall call);

    void notifyCallMediaState(MyCall call);

    void notifyBuddyState(MyBuddy buddy);

    void notifyChangeNetwork();

}
