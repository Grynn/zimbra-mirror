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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZSearchHit;

public abstract class ZSearchHitBean {
    
    public enum HitType { conversation, contact, message, voiceMailItem, call }
    
    private HitType mHitType;
    private ZSearchHit mHit;
    
    protected ZSearchHitBean(ZSearchHit hit, HitType hitType) {
        mHit = hit;
        mHitType = hitType;
    }
    
    public final String getId() { return mHit.getId(); }
    
    public final String getSortField() { return mHit.getSortField(); }
    
    public final float getScore() { return mHit.getScore(); }
    
    public final String getHitType() { return mHitType.name(); }

    public final boolean getIsConversation() { return mHitType == HitType.conversation; }
    
    public final boolean getIsMessage() { return mHitType == HitType.message; }
    
    public final boolean getIsContact() { return mHitType == HitType.contact; }

    public final boolean getIsVoiceMailItem() { return mHitType == HitType.voiceMailItem; }

    public final boolean getIsCall() { return mHitType == HitType.call; }

    public final ZConversationHitBean getConversationHit() { return getIsConversation() ? (ZConversationHitBean) this : null; }

    public final ZMessageHitBean getMessageHit() { return getIsMessage() ? (ZMessageHitBean) this : null; }

    public final ZContactHitBean getContactHit() { return getIsContact() ? (ZContactHitBean) this : null; }

    public final ZVoiceMailItemHitBean getVoiceMailItemHit() { return getIsVoiceMailItem() ? (ZVoiceMailItemHitBean) this : null; }

    public final ZCallHitBean getCallHit() { return getIsCall() ? (ZCallHitBean) this : null; }

}
