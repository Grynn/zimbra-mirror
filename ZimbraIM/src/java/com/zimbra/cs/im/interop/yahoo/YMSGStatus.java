/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
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
    NONE(0), AVAILABLE(0),
    ;
    
    YMSGStatus(long num) { mNum = num; }
    public long getNum() { return mNum; }
    public static YMSGStatus lookup(long num) { return sMap.get(num); }
    
    private static final HashMap<Long, YMSGStatus> sMap = new HashMap<Long, YMSGStatus>();
    static {
        for (YMSGStatus s : YMSGStatus.values()) {
            sMap.put(s.getNum(), s); 
        }
    }
    private long mNum;
}
