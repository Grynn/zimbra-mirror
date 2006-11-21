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
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.ZSendMessageResponse;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendMessageTag extends ZimbraSimpleTag {

    private String mVar;
    private String mTo;
    private String mCc;
    private String mBcc;
    private String mFrom;
    private String mSubject;
    private String mContentType = "text/plain";
    private String mContent;
    private String mOrigId;
    private String mMessages;

    public void setVar(String var) { this.mVar = var; }
    
    public void setTo(String to) { mTo = to; }

    public void setContent(String content) { mContent = content; }

    public void setContenttype(String contentType) { mContentType = contentType; }

    public void setSubject(String subject) { mSubject = subject; }

    public void setOrigmessageid(String origId) { mOrigId = origId; }

    public void setFrom(String from) { mFrom = from; }

    public void setBcc(String bcc) { mBcc = bcc; }

    public void setCc(String cc) { mCc = cc; }

    public void setMessages(String messages) { mMessages = messages; }
    
    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();

            List<ZEmailAddress> addrs = new ArrayList<ZEmailAddress>();

            if (mTo != null && mTo.length() > 0)
                addrs.addAll(ZEmailAddress.parseAddresses(mTo, ZEmailAddress.EMAIL_TYPE_TO));

            if (mCc != null && mCc.length() > 0)
                addrs.addAll(ZEmailAddress.parseAddresses(mCc, ZEmailAddress.EMAIL_TYPE_CC));

            if (mFrom != null && mFrom.length() > 0)
                addrs.addAll(ZEmailAddress.parseAddresses(mFrom, ZEmailAddress.EMAIL_TYPE_FROM));

            if (mBcc != null && mBcc.length() > 0)
                addrs.addAll(ZEmailAddress.parseAddresses(mBcc, ZEmailAddress.EMAIL_TYPE_BCC));

            List<String> messages;

            if (mMessages != null && mMessages.length() > 0) {
                messages = new ArrayList<String>();
                messages.add(mMessages);
            } else {
                messages = null;
            }

            ZSendMessageResponse response =
                    mbox.sendMessage(
                            addrs,
                            mSubject,
                            mOrigId != null && mOrigId.length() == 0 ? null : mOrigId, 
                            mContentType,
                            mContent,
                            null, // upload id
                            messages,
                            null, // message parts to attach
                            null); /// contact ids to attach

            jctxt.setAttribute(mVar, response, PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            getJspContext().getOut().write(e.toString());
        }
    }



}
