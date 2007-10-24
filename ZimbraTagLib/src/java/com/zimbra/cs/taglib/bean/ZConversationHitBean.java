/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

import com.zimbra.cs.zclient.ZConversationHit;
import com.zimbra.cs.zclient.ZEmailAddress;

import java.util.Date;
import java.util.List;

public class ZConversationHitBean extends ZSearchHitBean {

    private ZConversationHit mHit;
    
    public ZConversationHitBean(ZConversationHit hit) {
        super(hit, HitType.conversation);
        mHit = hit;
    }

    public Date getDate() { return new Date(mHit.getDate()); }
    
    public boolean getHasFlags() { return mHit.hasFlags(); }
    
    public boolean getHasMultipleTags() { return mHit.hasTags() && mHit.getTagIds().indexOf(',') != -1; }
    
    public String getTagIds() { return mHit.getTagIds(); }
    
    public boolean getHasTags() { return mHit.hasTags(); }
    
    public boolean getIsUnread() { return mHit.isUnread(); }

    public boolean getIsFlagged() { return mHit.isFlagged(); }

    public boolean getIsHighPriority() { return mHit.isHighPriority(); }

    public boolean getIsLowPriority() { return mHit.isLowPriority(); }

    public boolean getIsDraft() { return mHit.isDraft(); }

    public boolean getIsSentByMe() { return mHit.isSentByMe(); }

    public boolean getHasAttachment() { return mHit.hasAttachment(); }

    public String getSubject() { return mHit.getSubject(); }
    
    public String getFragment() { return mHit.getFragment(); }
    
    public int getMessageCount() { return mHit.getMessageCount(); }
    
    public List<String> getMatchedMessageIds() { return mHit.getMatchedMessageIds(); }
    
    public List<ZEmailAddress> getRecipients() { return mHit.getRecipients(); }
    
    public String getDisplayRecipients() { return BeanUtils.getAddrs(mHit.getRecipients()); }    
}
