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
 * Portions created by Zimbra are Copyright (C) 2007 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZPhone;
import com.zimbra.cs.zclient.ZVoiceMailItemHit;
import com.zimbra.common.service.ServiceException;

import java.util.Date;

public class ZVoiceMailItemHitBean extends ZSearchHitBean {

    private ZVoiceMailItemHit mHit;

    public ZVoiceMailItemHitBean(ZVoiceMailItemHit hit) {
        super(hit, HitType.voiceMailItem);
        mHit = hit;
    }

    public static ZVoiceMailItemHitBean deserialize(String value, String phone) throws ServiceException {
        return new ZVoiceMailItemHitBean(ZVoiceMailItemHit.deserialize(value, phone));
    }

    public String toString() { return mHit.toString(); }

    public boolean getIsFlagged() { return mHit.isFlagged(); }

    public boolean getIsUnheard() { return mHit.isUnheard(); }

    public boolean getIsPrivate() { return mHit.isPrivate(); }

    public ZPhone getCaller() { return mHit.getCaller(); }

    public String getDisplayCaller() { return mHit.getDisplayCaller(); }

    public String getSoundUrl() { return mHit.getSoundUrl(); }

    public Date getDate() { return new Date(mHit.getDate()); }

    public long getDuration() { return mHit.getDuration(); }

    public String getSerialize() { return  mHit.serialize(); }
}

