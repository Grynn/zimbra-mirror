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

/**
 * 
 */
public enum YMSGStatus {
    UNKNOWN(-1),
    NONE(0),
    AVAILABLE(0),
    BRB(1),
    BUSY(2),
    NOTATHOME(3),
    NOTATDESK(4),
    NOTINOFFICE(5),
    ONPHONE(6),
    ONVACATION(7),
    OUTTOLUNCH(8),
    STEPPEDOUT(9),
    INVISIBLE(12),
    TYPING(0x16),
    CUSTOM(99),
    IDLE(999),
    WEBLOGIN(0x5a55aa55),
    OFFLINE(0x5a55aa56),
    ;
    
    private YMSGStatus(long num) {
        mNum = num;
    }
    
    private final long mNum;
    private static final HashMap<Long, YMSGStatus> sKnownSvcMap = new HashMap<Long, YMSGStatus>();
    static {
        for (YMSGStatus status: YMSGStatus.values()) {
            sKnownSvcMap.put(status.getNum(), status);
        }
    }
    
    public final long getNum() { return mNum; }
    public static final YMSGStatus lookup(long num) {
        YMSGStatus toRet = sKnownSvcMap.get(num);
        if (toRet == null)
            return YMSGStatus.UNKNOWN;
        else
            return toRet;
    }
}
