package com.zimbra.cs.im.interop.yahoo;

import java.util.HashMap;

enum YMSGService {
    UNKNOWN(0),
    LOGON(1),
    LOGOFF(2),
    AWAY(3),
    BACK(4),
    IDLE(5),
    MESSAGE(6),
    NEW_CONTACT(0xf),
    ADD_IGNORE(0x11),
    PING(0x12),
    AUTH_RESPONSE(0x54),
    LIST(0x55),
    AUTH(0x57),
    ADDBUDDY(0x83),
    PICTURE(0xbe),
    ;
    
    private YMSGService(int num) {
        mNum = num;
    }
    
    private final int mNum;
    private static final HashMap<Integer, YMSGService> sKnownSvcMap = new HashMap<Integer, YMSGService>();
    static {
        for (YMSGService svc : YMSGService.values()) {
            sKnownSvcMap.put(svc.getValue(), svc);
        }
    }
    
    public final int getValue() { return mNum; }
    public static final YMSGService lookup(int num) {
        YMSGService toRet = sKnownSvcMap.get(num);
        if (toRet == null)
            return YMSGService.UNKNOWN;
        else
            return toRet;
    }
}
