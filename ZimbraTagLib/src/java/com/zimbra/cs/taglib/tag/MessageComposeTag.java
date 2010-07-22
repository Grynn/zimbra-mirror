/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean.Action;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean.AppointmentOptions;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Calendar;

public class MessageComposeTag extends ZimbraSimpleTag {

    public static final String ACTION_NEW = "new";
    public static final String ACTION_APPT_EDIT = "apptedit";
    public static final String ACTION_APPT_NEW = "apptnew";
    public static final String ACTION_REPLY = "reply";
    public static final String ACTION_REPLY_ALL = "replyAll";
    public static final String ACTION_FORWARD = "forward";
    public static final String ACTION_RESEND = "resend";
    public static final String ACTION_DRAFT = "draft";
    public static final String ACTION_ACCEPT = "accept";
    public static final String ACTION_DECLINE = "decline";
    public static final String ACTION_TENTATIVE = "tentative";

    private String mVar;
    private ZMessageBean mMessage;
    private String mAction;
    private Calendar mDate;
    private String mInviteId;
    private String mExceptionInviteId;
    private boolean mUseInstance;
    private long mInstanceStartTime;
    private long mInstanceDuration;
    private boolean mIstask;
    private boolean mIsmobile;

    public void setVar(String var) { this.mVar = var; }

    public void setMessage(ZMessageBean message) { mMessage = message; }

    public void setAction(String action) { mAction = action; }

    public void setDate(Calendar date) { mDate = date; }

    public void setInviteId(String inviteId) { mInviteId = inviteId; }
    public void setExceptionInviteId(String exceptionInviteId) { mExceptionInviteId = exceptionInviteId; }
    public void setUseInstance(boolean useInstance) { mUseInstance = useInstance; }
    public void setIstask(boolean isTask) { mIstask = isTask; }
    public void setInstanceStartTime(long instanceStartTime) { mInstanceStartTime = instanceStartTime; }
    public void setInstanceDuration(long instanceDuration) { mInstanceDuration = instanceDuration; }
    public void setIsmobile(boolean isMobile) { mIsmobile = isMobile; }

    public void doTag() throws JspException, IOException {
        try {
            JspContext jctxt = getJspContext();
            PageContext pc = (PageContext) jctxt;
            ZMailbox mailbox = getMailbox();

            ZMessageComposeBean compose;

            if (ACTION_REPLY.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.REPLY, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_REPLY_ALL.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.REPLY_ALL, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_FORWARD.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.FORWARD, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_RESEND.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.RESEND, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_DRAFT.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.DRAFT, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_ACCEPT.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.INVITE_ACCEPT, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_DECLINE.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.INVITE_DECLINE, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_TENTATIVE.equals(mAction)) {
                compose = new ZMessageComposeBean(Action.INVITE_TENTATIVE, mMessage, mailbox, pc, null, mIsmobile);
            } else if (ACTION_APPT_NEW.equals(mAction)) {
                AppointmentOptions options = new AppointmentOptions();
                options.setDate(mDate);
                options.setIsTask(mIstask);
                compose = new ZMessageComposeBean(Action.APPT_NEW, null, mailbox, pc, options, mIsmobile);
            } else if (ACTION_APPT_EDIT.equals(mAction)) {
                AppointmentOptions options = new AppointmentOptions();
                options.setDate(mDate);
                options.setInviteId(mInviteId);
                options.setExceptionInviteId(mExceptionInviteId);
                options.setUseInstance(mUseInstance);
                options.setInstanceStartTime(mInstanceStartTime);
                options.setInstanceDuration(mInstanceDuration);
                options.setIsTask(mIstask);
                compose = new ZMessageComposeBean(Action.APPT_EDIT, mMessage, mailbox, pc, options, mIsmobile);
            } else {
                compose = new ZMessageComposeBean(Action.NEW, null, mailbox, pc, null, mIsmobile);
            }
            jctxt.setAttribute(mVar, compose, PageContext.PAGE_SCOPE);
            
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
