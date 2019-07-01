package com.zhhli.sip;

/**
 * des:
 *
 * @author zhhli
 * @date 2019/7/1
 */
public interface UIThread {

    interface MSG_TYPE {
        int REG_STATE = 10;
        int INCOMING_CALL = 20;
        int CALL_RING = 30;
        int CALL_OK = 31;
        int CALL_END = 32;
        int CALL_MEDIA_STATE = 4;
        int CHANGE_NETWORK = 5;
    }


    void postMainEvent(int eventCode, Object data);


}
