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

import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZMessageHit;

import java.util.Date;
import java.util.List;

public class ZMessageHitBean extends ZSearchHitBean {

    private ZMessageHit mHit;
    
    public ZMessageHitBean(ZMessageHit hit) {
        super(hit, HitType.message);
        mHit = hit;
    }

    public String getFlags() { return mHit.getFlags(); } 
    
    public String getFolderId() { return mHit.getFolderId(); }

    public long getSize() { return mHit.getSize(); }

    public Date getDate() { return new Date(mHit.getDate()); }
    
    public String getConversationId() { return mHit.getConversationId(); }

    public boolean getIsInvite() { return mHit.getIsInvite(); }
    
    public boolean getIsUnread() { return mHit.isUnread(); }

    public boolean getIsFlagged() { return mHit.isFlagged(); }

    public boolean getIsHighPriority() { return mHit.isHighPriority(); }

    public boolean getIsLowPriority() { return mHit.isLowPriority(); }

    public boolean getHasAttachment() { return mHit.hasAttachment(); }

    public boolean getIsRepliedTo() { return mHit.isRepliedTo(); }

    public boolean getIsSentByMe() { return mHit.isSentByMe(); }

    public boolean getIsForwarded() { return mHit.isForwarded(); } 

    public boolean getIsDraft() { return mHit.isDraft(); }

    public boolean getIsDeleted() { return mHit.isDeleted(); }

    public boolean getIsNotificationSent() { return mHit.isNotificationSent(); }
    
    /**
     * @return comma-separated list of tag ids
     */
    public String getTagIds() { return mHit.getTagIds(); }

    public String getSubject() { return mHit.getSubject(); }
    
    public boolean getHasFlags() { return mHit.hasFlags(); }
    
    public boolean getHasTags() { return mHit.hasTags(); }
    
    public String getFragment() { return mHit.getFragment(); }
    
    public ZEmailAddress getSender() { return mHit.getSender(); }

    public String getDisplaySender() { return BeanUtils.getAddr(mHit.getSender()); }

    public String getDisplayAddresses() { return BeanUtils.getAddrs(mHit.getAddresses()); }    

    public boolean getContentMatched() { return mHit.getContentMatched(); }

    public boolean getMessageMatched() { return mHit.getContentMatched() || mHit.getMimePartHits().size() > 0; }

    public ZMessageBean getMessage() {
        if (mHit.getMessage() != null)
            return new ZMessageBean(mHit.getMessage());
        else
            return null;
    }

    /**
     *  @return names (1.2.3...) of mime part(s) that matched, or empty list.
     */
    public List<String> getMimePartHits() { return mHit.getMimePartHits(); }

    public String getStatusImage() {
        if (getIsInvite())
            return "startup/ImgAppointment.gif";
        else if (getIsUnread())
            return "startup/ImgMsgStatusUnread.gif";
        else if (getIsDraft())
            return "startup/ImgMsgStatusDraft.gif";
        else if (getIsRepliedTo())
            return "startup/ImgMsgStatusReply.gif";
        else if (getIsForwarded())
            return "startup/ImgMsgStatusForward.gif";
        else if (getIsSentByMe())
            return "startup/ImgMsgStatusSent.gif";
        else
            return "startup/ImgMsgStatusRead.gif";
    }

    public String getStatusImageAltKey() {
        if (getIsInvite())
            return "ALT_MSG_STATUS_APPT";
        else if (getIsUnread())
            return "ALT_MSG_STATUS_UNREAD";
        else if (getIsDraft())
            return "ALT_MSG_STATUS_DRAFT";
        else if (getIsRepliedTo())
            return "ALT_MSG_STATUS_REPLIEDTO";
        else if (getIsForwarded())
            return "ALT_MSG_STATUS_FORWARDED";
        else if (getIsSentByMe())
            return "ALT_MSG_STATUS_SENTBYME";
        else
            return "ALT_MSG_STATUS_READ";
    }

}

