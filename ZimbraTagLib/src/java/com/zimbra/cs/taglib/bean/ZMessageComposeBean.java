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

import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZIdentity;

import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class ZMessageComposeBean {

    public static String REPLY_PREFIX = "Re:";
    public static String FORWARD_PREFIX = "Fwd:";

    private String mTo;
    private String mCc;
    private String mBcc;
    private String mFrom;
    private String mSubject;
    private String mContentType = "text/plain";
    private String mContent;
    private String mOrigId;

    public void setTo(String to) { mTo = to; }
    public String getTo() { return mTo; }

    public void setContent(String content) { mContent = content; }
    public String getContent() { return mContent; }

    public void setContenttype(String contentType) { mContentType = contentType; }
    public String getContentType() { return mContentType; }

    public void setSubject(String subject) { mSubject = subject; }
    public String getSubject() { return mSubject; }

    public void setOrigId(String origId) { mOrigId = origId; }
    public String getOrigId() { return mOrigId; }

    public void setFrom(String from) { mFrom = from; }
    public String getFrom() { return mFrom; }

    public void setBcc(String bcc) { mBcc = bcc; }
    public String getBcc() { return mBcc; }

    public void setCc(String cc) { mCc = cc; }
    public String getCc() { return mCc; }
    
    public ZMessageComposeBean() {
		
	}

    public static ZMessageComposeBean reply(ZMessageBean msg, ZIdentity identity) {
        ZMessageComposeBean reply = new ZMessageComposeBean();
        //reply.setFrom(identity.
        Set<String> toAddresses = new HashSet<String>();
        reply.setTo(getToAddress(msg.getEmailAddresses(), toAddresses));
        reply.setSubject(getReplySubject(msg.getSubject()));
        return reply;
    }

    private static String getReplySubject(String subject) {
        if (subject == null) subject = "";
        if ((subject.length() > 3) && subject.substring(0, 3).equalsIgnoreCase(REPLY_PREFIX))
            return subject;
        else
            return REPLY_PREFIX+" "+subject;
    }

    private static String getForwardSubject(String subject) {
        if (subject == null) subject = "";
        if ((subject.length() > 3) && subject.substring(0, 3).equalsIgnoreCase(FORWARD_PREFIX))
            return subject;
        else
            return FORWARD_PREFIX+" "+subject;
    }


    public static ZMessageComposeBean replyAll(ZMessageBean msg, ZIdentity identity) {
        ZMessageComposeBean replyAll = new ZMessageComposeBean();
        //reply.setFrom(identity.
        Set<String> toAddresses = new HashSet<String>();
        replyAll.setTo(getToAddress(msg.getEmailAddresses(), toAddresses));
        replyAll.setCc(getCcAddress(msg.getEmailAddresses(), toAddresses));
        replyAll.setSubject(getReplySubject(msg.getSubject()));        
        return replyAll;
    }

    public static ZMessageComposeBean forward(ZMessageBean msg, ZIdentity identity) {
        ZMessageComposeBean forward = new ZMessageComposeBean();
        //reply.setFrom(identity.
        forward.setSubject(getForwardSubject(msg.getSubject()));        
        return forward;
    }

    public static ZMessageComposeBean newMessage(ZIdentity identity) {
        ZMessageComposeBean newMessage = new ZMessageComposeBean();
        return newMessage;
    }

    private static String getToAddress(List<ZEmailAddress> emailAddresses, Set<String> toAddresses) {
        for (ZEmailAddress address : emailAddresses) {
            if (ZEmailAddress.EMAIL_TYPE_REPLY_TO.equals(address.getType())) {
                toAddresses.add(address.getAddress());
                return address.getFullAddress();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (ZEmailAddress address : emailAddresses) {
            if (ZEmailAddress.EMAIL_TYPE_FROM.equals(address.getType())) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(address.getFullAddress());
                toAddresses.add(address.getAddress());
            }
        }
        return sb.toString();
    }

    private static String getCcAddress(List<ZEmailAddress> emailAddresses, Set<String> toAddresses) {
        StringBuilder sb = new StringBuilder();
        for (ZEmailAddress address : emailAddresses) {
            if (ZEmailAddress.EMAIL_TYPE_TO.equals(address.getType()) ||
                    ZEmailAddress.EMAIL_TYPE_CC.equals(address.getType())) {
                if (!toAddresses.contains(address.getAddress())) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(address.getFullAddress());
                }
            }
        }
        return sb.toString();
    }
}
