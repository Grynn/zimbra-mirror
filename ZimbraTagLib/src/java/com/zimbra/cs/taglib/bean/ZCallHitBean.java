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

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZCallHit;
import com.zimbra.cs.zclient.ZPhone;

import java.util.Date;

public class ZCallHitBean extends ZSearchHitBean {

    private ZCallHit mHit;

    public ZCallHitBean(ZCallHit hit) {
        super(hit, HitType.call);
        mHit = hit;
    }

    public String toString() { return mHit.toString(); }

    public ZPhone getCaller() { return mHit.getCaller(); }

    public String getDisplayCaller() { return mHit.getDisplayCaller(); }

	public String getDisplayRecipient() { return mHit.getDisplayRecipient(); }

    public Date getDate() { return new Date(mHit.getDate()); }

    public long getDuration() { return mHit.getDuration(); }

}
