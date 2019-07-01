package com.zhhli.sip;

import java.util.HashMap;

/**
 * des:
 * Created by zhh_li
 * on 2018/10/8.
 */
public class SipCode {

    public interface Code {
        int PJSIP_SC_NULL = 0;
        int PJSIP_SC_TRYING = 100;
        int PJSIP_SC_RINGING = 180;
        int PJSIP_SC_CALL_BEING_FORWARDED = 181;
        int PJSIP_SC_QUEUED = 182;
        int PJSIP_SC_PROGRESS = 183;
        int PJSIP_SC_OK = 200;
        int PJSIP_SC_ACCEPTED = 202;
        int PJSIP_SC_MULTIPLE_CHOICES = 300;
        int PJSIP_SC_MOVED_PERMANENTLY = 301;
        int PJSIP_SC_MOVED_TEMPORARILY = 302;
        int PJSIP_SC_USE_PROXY = 305;
        int PJSIP_SC_ALTERNATIVE_SERVICE = 380;
        int PJSIP_SC_BAD_REQUEST = 400;
        int PJSIP_SC_UNAUTHORIZED = 401;
        int PJSIP_SC_PAYMENT_REQUIRED = 402;
        int PJSIP_SC_FORBIDDEN = 403;
        int PJSIP_SC_NOT_FOUND = 404;
        int PJSIP_SC_METHOD_NOT_ALLOWED = 405;
        int PJSIP_SC_NOT_ACCEPTABLE = 406;
        int PJSIP_SC_PROXY_AUTHENTICATION_REQUIRED = 407;
        int PJSIP_SC_REQUEST_TIMEOUT = 408;
        int PJSIP_SC_GONE = 410;
        int PJSIP_SC_REQUEST_ENTITY_TOO_LARGE = 413;
        int PJSIP_SC_REQUEST_URI_TOO_LONG = 414;
        int PJSIP_SC_UNSUPPORTED_MEDIA_TYPE = 415;
        int PJSIP_SC_UNSUPPORTED_URI_SCHEME = 416;
        int PJSIP_SC_BAD_EXTENSION = 420;
        int PJSIP_SC_EXTENSION_REQUIRED = 421;
        int PJSIP_SC_SESSION_TIMER_TOO_SMALL = 422;
        int PJSIP_SC_INTERVAL_TOO_BRIEF = 423;
        int PJSIP_SC_TEMPORARILY_UNAVAILABLE = 480;
        int PJSIP_SC_CALL_TSX_DOES_NOT_EXIST = 481;
        int PJSIP_SC_LOOP_DETECTED = 482;
        int PJSIP_SC_TOO_MANY_HOPS = 483;
        int PJSIP_SC_ADDRESS_INCOMPLETE = 484;
        int PJSIP_AC_AMBIGUOUS = 485;
        int PJSIP_SC_BUSY_HERE = 486;
        int PJSIP_SC_REQUEST_TERMINATED = 487;
        int PJSIP_SC_NOT_ACCEPTABLE_HERE = 488;
        int PJSIP_SC_BAD_EVENT = 489;
        int PJSIP_SC_REQUEST_UPDATED = 490;
        int PJSIP_SC_REQUEST_PENDING = 491;
        int PJSIP_SC_UNDECIPHERABLE = 493;
        int PJSIP_SC_INTERNAL_SERVER_ERROR = 500;
        int PJSIP_SC_NOT_IMPLEMENTED = 501;
        int PJSIP_SC_BAD_GATEWAY = 502;
        int PJSIP_SC_SERVICE_UNAVAILABLE = 503;
        int PJSIP_SC_SERVER_TIMEOUT = 504;
        int PJSIP_SC_VERSION_NOT_SUPPORTED = 505;
        int PJSIP_SC_MESSAGE_TOO_LARGE = 513;
        int PJSIP_SC_PRECONDITION_FAILURE = 580;
        int PJSIP_SC_BUSY_EVERYWHERE = 600;
        int PJSIP_SC_DECLINE = 603;
        int PJSIP_SC_DOES_NOT_EXIST_ANYWHERE = 604;
        int PJSIP_SC_NOT_ACCEPTABLE_ANYWHERE = 606;
        int PJSIP_SC_TSX_TIMEOUT = PJSIP_SC_REQUEST_TIMEOUT;
        int PJSIP_SC_TSX_TRANSPORT_ERROR = PJSIP_SC_SERVICE_UNAVAILABLE;
        int PJSIP_SC__force_32bit = 0x7FFFFFFF;
    }

    interface InviteState{
        String PJSIP_INV_STATE_NULL         = "Before INVITE is sent or received";
        String PJSIP_INV_STATE_CALLING      = "After INVITE is sent";
        String PJSIP_INV_STATE_INCOMING     = "After INVITE is received.";
        String PJSIP_INV_STATE_EARLY        = "After response with To tag.";
        String PJSIP_INV_STATE_CONNECTING   = "After 2xx is sent/received.";
        String PJSIP_INV_STATE_CONFIRMED    = "After ACK is sent/received.";
        String PJSIP_INV_STATE_DISCONNECTED = "Session is terminated.";
    }

    /**
     * 1xx = 通知性应答
     * 2xx = 成功应答
     * 3xx = 转接应答
     * 4xx = 呼叫失败
     * 5xx = 服务器失败
     * 6xx = 全局失败
     */
    private final static HashMap<Integer, String> map = new HashMap<Integer, String>() {
        {

            put(100, "正在尝试");
            put(180, "正在拨打");
            put(181, "正被转接");
            put(182, "正在排队");
            put(183, "振铃中...");

            put(200, "OK");
            put(202, "被接受：用于转介");

            put(300, "多项选择");
            put(301, "被永久迁移");
            put(302, "被暂时迁移");
            put(305, "使用代理服务器");
            put(380, "替代服务");

            put(400, "呼叫不当");
            put(401, "未经授权：只供注册机构使用，代理服务器应使用代理服务器授权407");
            put(402, "要求付费（预订为将来使用)");
            put(403, "被禁止的");
            put(404, "未发现：未发现用户");
            put(405, "不允许的方法");
            put(406, "不可接受");
            put(407, "需要代理服务器授权");
            put(408, "呼叫超时：在预定时间内无法找到用户");
            put(410, "已消失：用户曾经存在，但已从此处消失");
            put(413, "呼叫实体过大");
            put(414, "呼叫URI过长");
            put(415, "不支持的媒体类型");
            put(416, "不支持的URI方案");
            put(420, "不当扩展：使用了不当SIP协议扩展，服务器无法理解该扩展");
            put(421, "需要扩展");
            put(423, "时间间隔过短");
            put(480, "暂时不可使用");
            put(481, "通话/事务不存在");
            put(482, "检测到循环");
            put(483, "跳数过多");
            put(484, "地址不全");
            put(485, "模糊不清");
            put(486, "此处太忙");
            put(487, "呼叫被终止");
            put(488, "此处不可接受");
            put(491, "呼叫待批");
            put(493, "无法解读：无法解读 S/MIME文体部分");

            put(500, "服务器内部错误");
            put(501, "无法实施：SIP呼叫方法在此处无法实施");
            put(502, "不当网关");
            put(503, "服务不可使用");
            put(504, "服务器超时");
            put(505, "不支持该版本：服务器不支持SIP协议的这个版本");
            put(513, "消息过长");

            put(600, "各处均忙");
            put(603, "拒绝");
            put(604, "无处存在");
            put(606, "不可使用");
        }
    };

    int code;

    public SipCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String getValue(){
        return map.get(code);
    }


    public static SipCode refuseUse(){
        return new SipCode(606);
    }

    public static SipCode busy(){
        return new SipCode(486);
    }
    public static SipCode ok(){
        return new SipCode(200);
    }
}
