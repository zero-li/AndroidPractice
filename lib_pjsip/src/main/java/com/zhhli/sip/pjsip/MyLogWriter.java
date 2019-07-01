package com.zhhli.sip.pjsip;

import org.pjsip.pjsua2.LogEntry;
import org.pjsip.pjsua2.LogWriter;

/**
 * des:
 *
 * @author zhhli
 * @date 2019/7/1
 */
class MyLogWriter extends LogWriter {
    @Override
    public void write(LogEntry entry) {
        System.out.println(entry.getMsg());
    }
}
