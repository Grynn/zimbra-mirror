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
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage;
import com.zimbra.cs.zclient.ZMailbox.ZSendMessageResponse;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage.AttachedMessagePart;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage.MessagePart;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SendMessageTag extends ZimbraSimpleTag {

    private String mVar;
    private String mTo;
    private String mReplyTo;
    private String mCc;
    private String mBcc;
    private String mFrom;
    private String mSubject;
    private String mContentType = "text/plain";
    private String mContent;
    private String mReplyType;
    private String mInReplyTo;
    private String mMessageId;    
    private String mMessages;
    private String mAttachments;
    private ZMessageComposeBean mCompose;

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }
    
    public void setVar(String var) { this.mVar = var; }
    
    public void setTo(String to) { mTo = to; }

    public void setReplyto(String replyTo) { mReplyTo = replyTo; }
    
    public void setReplytype(String replyType) { mReplyType = replyType; }

    public void setContent(String content) { mContent = content; }

    public void setContenttype(String contentType) { mContentType = contentType; }

    public void setSubject(String subject) { mSubject = subject; }

    public void setMessageid(String id) { mMessageId = id; }

    public void setInreplyto(String inReplyto) { mInReplyTo = inReplyto; }

    public void setFrom(String from) { mFrom = from; }

    public void setBcc(String bcc) { mBcc = bcc; }

    public void setCc(String cc) { mCc = cc; }

    public void setMessages(String messages) { mMessages = messages; }

    public void setAttachments(String attachments) { mAttachments = attachments; }
    
    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            
            ZOutgoingMessage m = mCompose != null ? mCompose.toOutgoingMessage() :  getOutgoingMessage();
            
            ZSendMessageResponse response = mbox.sendMessage(m, null, false);
            jctxt.setAttribute(mVar, response, PageContext.PAGE_SCOPE);

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }

    private ZOutgoingMessage getOutgoingMessage() throws ServiceException {

        List<ZEmailAddress> addrs = new ArrayList<ZEmailAddress>();

        if (mTo != null && mTo.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mTo, ZEmailAddress.EMAIL_TYPE_TO));

        if (mReplyTo != null && mReplyTo.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mReplyTo, ZEmailAddress.EMAIL_TYPE_REPLY_TO));

        if (mCc != null && mCc.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mCc, ZEmailAddress.EMAIL_TYPE_CC));

        if (mFrom != null && mFrom.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mFrom, ZEmailAddress.EMAIL_TYPE_FROM));

        if (mBcc != null && mBcc.length() > 0)
            addrs.addAll(ZEmailAddress.parseAddresses(mBcc, ZEmailAddress.EMAIL_TYPE_BCC));

        List<String> messages;

        if (mMessages != null && mMessages.length() > 0) {
            messages = new ArrayList<String>();
            for (String m : mMessages.split(",")) {
                messages.add(m);
            }
        } else {
            messages = null;
        }

        List<AttachedMessagePart> attachments;
        if (mAttachments != null && mAttachments.length() > 0) {
            attachments = new ArrayList<AttachedMessagePart>();
            for (String partName : mAttachments.split(",")) {
                attachments.add(new AttachedMessagePart(mMessageId, partName));
            }
        } else {
            attachments = null;
        }

        ZOutgoingMessage m = new ZOutgoingMessage();

        m.setAddresses(addrs);

        m.setSubject(mSubject);

        if (mInReplyTo != null && mInReplyTo.length() > 0)
            m.setInReplyTo(mInReplyTo);

        m.setMessageParts(new ArrayList<MessagePart>());
        m.getMessageParts().add(new MessagePart(mContentType, mContent));

        m.setMessageIdsToAttach(messages);

        m.setMessagePartsToAttach(attachments);

        if (mMessageId != null && mMessageId.length() > 0)
            m.setOriginalMessageId(mMessageId);

        if (mReplyType != null && mReplyType.length() > 0)
            m.setReplyType(mReplyType);

        return m;

    }



}
