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
