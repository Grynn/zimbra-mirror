/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZTaskHit;

import java.util.Date;

public class ZTaskHitBean extends ZSearchHitBean {

    private ZTaskHit mHit;

    public ZTaskHitBean(ZTaskHit hit) {
        super(hit, HitType.task);
        mHit = hit;
    }

    public String getInviteId() { return mHit.getInviteId(); }

    public String getFlags() { return mHit.getFlags(); }

    public String getFolderId() { return mHit.getFolderId(); }

    public long getSize() { return mHit.getSize(); }

    public Date getDate() { return mHit.getStartDate(); }

    public boolean getHasAttachment() { return mHit.getHasAttachment(); }

    /**
     * @return comma-separated list of tag ids
     */
    public String getTagIds() { return mHit.getTagIds(); }

    public String getSubject() { return mHit.getName(); }

    public boolean getHasFlags() { return mHit.hasFlags(); }

    public boolean getHasTags() { return mHit.getHasTags(); }

    public boolean getIsHigh() { return "1".equals(mHit.getPriority()); }

    public boolean getIsLow() { return "9".equals(mHit.getPriority()); }

    public String getPercentComplete() { return mHit.getPercentComplete(); }

    public boolean getHasDueDate() { return mHit.getDueDateTime() != 0; }

    public long getDueDateTime() { return mHit.getDueDateTime(); }

    public Date getDueDate() { return mHit.getDueDate(); }

    public String getStatus() { return mHit.getStatus(); }
    
    public String getPriorityImage() {
        if (getIsHigh()) {
            return "tasks/ImgTaskHigh.gif";
        } else if (getIsLow()) {
            return "tasks/ImgTaskLow.gif";
        } else {
            return "tasks/ImgTaskNormal.gif";
        }
    }

}
