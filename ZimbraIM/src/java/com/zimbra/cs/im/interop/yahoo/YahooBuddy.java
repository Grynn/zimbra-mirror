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

import java.util.Formatter;

/**
 * 
 */
public class YahooBuddy {
    public boolean isTyping() {
        return mTyping;
    }
    public void setTyping(boolean typing) {
        this.mTyping = typing;
    }
    YahooBuddy(String name) {
        mName = name;
    }
    public String getName() { return mName; }
    public boolean isIgnore() { return mIgnore; }
    public YMSGStatus getStatus() { return mStatus; }
    
    void setIgnore(boolean truthines) { mIgnore = truthines; }
    void setStatus(YMSGStatus status) {
        mStatus = status;
    }
    
    public String toString() {
        return new Formatter().format("Buddy %s (%s%s%s)",
            mName, mStatus.toString(), 
            (mTyping?", TYPING":""),
            (mIgnore?", IGNORED":"")
            ).toString(); 
    }
    
    private YMSGStatus mStatus = YMSGStatus.OFFLINE;
    private String mName;
    private boolean mIgnore = false;
    private boolean mTyping = false;
}
