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

/**
 * 
 */
public class YahooMessage {
    YahooMessage(long time, String from, String to, String message) {
        mTime = time;
        mFrom = from;
        mTo = to;
        mMessage = message;
    }
    
    public long getTime() { return mTime; }
    public String getFrom() { return mFrom; }
    public String getTo() { return mTo; }
    public String getMessage() { return mMessage; }
    
    public String toString() {
        StringBuilder sb = new StringBuilder("MSG(from=");
        sb.append(mFrom).append(", to=").append(mTo);
        sb.append(", time=").append(mTime).append(") ").append(mMessage);
        return sb.toString();
    }
    
    private long mTime;
    private String mFrom;
    private String mTo;
    private String mMessage;
}
