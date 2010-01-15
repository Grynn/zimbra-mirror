/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
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

import com.zimbra.cs.zclient.ZConversation;
import com.zimbra.cs.zclient.ZEmailAddress;

import java.util.List;
import java.util.Date;
import java.util.ArrayList;

public class ZConversationBean {

    private ZConversation mConv;
    private List<ZMessageSummaryBean> mSummaries;

    public ZConversationBean(ZConversation conv) {
        mConv = conv;
    }

    /**
     * @return conversation's id
     */
    public String getId() { return mConv.getId(); }

    public boolean getHasFlags() { return mConv.hasFlags(); }

    public boolean getHasMultipleTags() { return mConv.hasTags() && mConv.getTagIds().indexOf(',') != -1; }

    public String getTagIds() { return mConv.getTagIds(); }

    public boolean getHasTags() { return mConv.hasTags(); }

    public boolean getIsUnread() { return mConv.isUnread(); }

    public boolean getIsFlagged() { return mConv.isFlagged(); }

    public boolean getIsDraft() { return mConv.isDraft(); }

    public boolean getIsRepliedTo() { return mConv.isRepliedTo(); }

    public boolean getIsForwarded() { return mConv.isForwarded(); }

    public boolean getIsSentByMe() { return mConv.isSentByMe(); }

    public boolean getHasAttachment() { return mConv.hasAttachment(); }

    public String getSubject() { return mConv.getSubject(); }

    public int getMessageCount() { return mConv.getMessageCount(); }

    public List<ZMessageSummaryBean> getMessageSummaries() {
        if (mSummaries == null) {
            mSummaries = new ArrayList<ZMessageSummaryBean>(mConv.getMessageSummaries().size());
            for (ZConversation.ZMessageSummary summ : mConv.getMessageSummaries()) {
                mSummaries.add(new ZMessageSummaryBean(summ));
            }
        }
        return mSummaries;
    }

    public static class ZMessageSummaryBean {

        private ZConversation.ZMessageSummary mSummary;

        public ZMessageSummaryBean(ZConversation.ZMessageSummary summary) {
            mSummary = summary;
        }

        public String getId() { return mSummary.getId(); }

        public String getFlags() { return mSummary.getFlags(); }

        public long getSize() { return mSummary.getSize(); }

        public ZEmailAddress getSender() { return mSummary.getSender(); }

        public String getDisplaySender() { return BeanUtils.getAddr(getSender()); }

        public String getFragment() { return mSummary.getFragment(); }

        public Date getDate() { return new Date(mSummary.getDate()); }

        /**
         * @return comma-separated list of tag ids
         */
        public String getTagIds() { return mSummary.getTagIds(); }
    }

}
