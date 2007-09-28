/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
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
    TYPING(0x4b),
    AUTH_RESPONSE(0x54),
    LIST(0x55),
    AUTH(0x57),
    ADDBUDDY(0x83),
    REMBUDDY(0x84),
    PICTURE(0xbe),
    Y6_STATUS_UPDATE(0xc6),
    STATUS_15(0xf0),
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
