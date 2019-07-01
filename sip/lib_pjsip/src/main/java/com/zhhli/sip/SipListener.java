package com.zhhli.sip;

import com.zhhli.sip.pjsip.MyAccount;

/**
 * des:
 *
 * @author my
 */

public interface SipListener {
    /**
     * 注册成功
     */
    void onRegisterSuccess(MyAccount account);

    /**
     * 注册失败
     * @param sipCode 状态码
     */
    void onRegisterFail(MyAccount account, SipCode sipCode);

    /**
     * 来电
     * @param number 来电号码
     */
    void onIncomingCall(String number);


    /**
     * 对方振铃
     * @param number
     * @param sipCode
     */
    void onCallRing(String number, SipCode sipCode);

    /**
     * 已接通
     * @param number
     * @param sipCode
     */
    void onCallOK(String number, SipCode sipCode);

    /**
     * 已挂断
     * @param number
     * @param sipCode
     */
    void onCallEnd(String number, SipCode sipCode);



}
