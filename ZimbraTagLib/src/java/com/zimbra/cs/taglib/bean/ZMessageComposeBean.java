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
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage.MessagePart;
import com.zimbra.cs.zclient.ZMailbox.ZOutgoingMessage.AttachedMessagePart;
import com.zimbra.cs.service.ServiceException;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocaleSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

public class ZMessageComposeBean {

    public static class MessageAttachment {
        private String mId;
        private String mSubject;

        public MessageAttachment(String id, String subject) {
            mId = id;
            mSubject = subject;
        }

        public String getId() { return mId; }
        public String getSubject() { return mSubject; }
    }
    
    public static String CRLF = "\r\n";

    public enum Action { NEW, REPLY, REPLY_ALL, FORWARD, RESEND, DRAFT }

    private String mTo;
    private String mCc;
    private String mBcc;
    private String mFrom;
    private String mReplyTo;
    private String mReplyType;
    private String mSubject;
    private String mContentType = "text/plain";
    private String mContent;
    private String mMessageId; // zimbra internal message id of message for reply/forward
    private String mInReplyTo; // original message-id header
    private String mDraftId; // id of draft we are editting
    private List<MessageAttachment> mMessageAttachments;
    private Map<String,Boolean> mCheckedAttachmentNames = new HashMap<String, Boolean>();
    private List<ZMimePartBean> mOriginalAttachments;

    public ZMessageComposeBean() {
        mMessageAttachments = new ArrayList<MessageAttachment>();
        mOriginalAttachments = new ArrayList<ZMimePartBean>();
    }
    
    public void setTo(String to) { mTo = to; }
    public String getTo() { return mTo; }

    public void setContent(String content) { mContent = content; }
    public String getContent() { return mContent; }

    public void setContenttype(String contentType) { mContentType = contentType; }
    public String getContentType() { return mContentType; }

    public void setReplyType(String replyType) { mReplyType = replyType; }
    public String getReplyType() { return mReplyType; }

    public void setSubject(String subject) { mSubject = subject; }
    public String getSubject() { return mSubject; }

    public void setInReplyTo(String inReplyTo) { mInReplyTo = inReplyTo; }
    public String getInReplyTo() { return mInReplyTo; }

    public void setFrom(String from) { mFrom = from; }
    public String getFrom() { return mFrom; }

    public void setBcc(String bcc) { mBcc = bcc; }
    public String getBcc() { return mBcc; }

    public void setCc(String cc) { mCc = cc; }
    public String getCc() { return mCc; }

    public void setReplyTo(String replyTo) { mReplyTo = replyTo; }
    public String getReplyTo() { return mReplyTo; }

    public void setMessageId(String id) { mMessageId = id; }
    public String getMessageId() { return mMessageId; }

    public void setDraftId(String id) { mDraftId = id; }
    public String getDraftId() { return mDraftId; }

    public Map<String,Boolean> getCheckedAttachmentNames() { return mCheckedAttachmentNames; }
    public void setCheckedAttachmentName(String name) { mCheckedAttachmentNames.put(name,  true); }

    public void setOrignalAttachments(List<ZMimePartBean> attachments) { mOriginalAttachments = attachments; }
    public List<ZMimePartBean> getOriginalAttachments() { return mOriginalAttachments; }

    public void setMessageAttachments(List<MessageAttachment> attachments) { mMessageAttachments = attachments; }
    public List<MessageAttachment> getMessageAttachments() { return mMessageAttachments; }

    /**
     * construct a message compose bean based on action and state.
     * @param action what type of compose we are doing, must not be null.
     * @param msg Message for reply/replyAll/forward
     * @param identities List of identities to use
     * @param emailAddresses a list of all possible email addresses for this account
     * @param pc the JSP PageContext for localization information
     */
    public ZMessageComposeBean(Action action, ZMessageBean msg, List<ZIdentity> identities, Set<String> emailAddresses, PageContext pc) {
        if (msg != null) {
            setMessageId(msg.getId());
        }

        // compute identity
        ZIdentity identity = action == Action.NEW ?
                defaultIdentity(identities) :
                computeIdentity(msg, identities);

        switch (action) {
            case REPLY:
            case REPLY_ALL:
                if (msg == null) break;
                setSubject(getReplySubject(msg.getSubject(), pc)); // Subject:
                List<ZEmailAddress> toAddressList = new ArrayList<ZEmailAddress>();
                Set<String> toAddressSet = new HashSet<String>();
                setTo(getToAddress(msg.getEmailAddresses(), toAddressList, toAddressSet, emailAddresses)); // To:
                if (action == Action.REPLY_ALL)
                    setCc(getCcAddress(msg.getEmailAddresses(), toAddressSet, emailAddresses));   // Cc:
                setInReplyTo(msg.getMessageIdHeader()); // original message-id header
                setReplyType("r");                
                break;
            case FORWARD:
                if (msg == null) break;
                setSubject(getForwardSubject(msg.getSubject(), pc)); // Subject:
                setReplyType("w");
                break;
            case RESEND:
                if (msg == null) break;
                setSubject(msg.getSubject());
                setTo(msg.getDisplayTo());
                setCc(msg.getDisplayCc());
                addAttachments(msg, true);
            case DRAFT:
                if (msg == null) break;
                setSubject(msg.getSubject());
                setTo(msg.getDisplayTo());
                setCc(msg.getDisplayCc());
                addAttachments(msg, true);
                if (msg.getInReplyTo() != null)
                    setInReplyTo(msg.getInReplyTo());
                if (msg.getReplyType() != null)
                    setReplyType(msg.getReplyType());
            case NEW:
            default:
                break;
        }

        if (identity == null)
            return;

        // Reply-to:
        if (identity.getReplyToEnabled()) { 
            setReplyTo(identity.getReplyToEmailAddress().getFullAddress());
        }

        // from
        setFrom(identity.getFromEmailAddress().getFullAddress());

        if (action == Action.RESEND || action == Action.DRAFT) {
            if (msg != null)
                setContent(msg.getBody().getContent());
            return;
        }

        // signature
        String signature = identity.getSignatureEnabled() ? identity.getSignature() : null;
        boolean signatureTop = identity.getSignatureStyleTop();

        // see if we need to use default identity for the rest
        ZIdentity includeIdentity = (!identity.isDefault() && identity.getUseDefaultIdentitySettings()) ?
                defaultIdentity(identities) :
                identity;

        StringBuilder content = new StringBuilder();

        if (signatureTop && signature != null && signature.length() > 0) 
            content.append("\n\n\n").append(signature);

        if (action == Action.REPLY || action == Action.REPLY_ALL)
            replyInclude(msg, content, includeIdentity, pc);
        else if (action == Action.FORWARD)
            forwardInclude(msg, content, includeIdentity, pc);

        if (!signatureTop && signature != null && signature.length() > 0) {
            if (content.length() == 0)
                content.append("\n\n\n");
            content.append(signature);
        }

        setContent(content.toString());
    }

    private String getQuotedHeaders(ZMessageBean msg, PageContext pc) {
        StringBuilder headers = new StringBuilder();
        //from, to, cc, date, subject
        String fromHdr = msg.getDisplayFrom();
        if (fromHdr != null)
            headers.append(LocaleSupport.getLocalizedMessage(pc, "ZM_HEADER_FROM")).append(": ").append(fromHdr).append(CRLF);
        String toHdr = msg.getDisplayTo();
        if (toHdr != null)
            headers.append(LocaleSupport.getLocalizedMessage(pc, "ZM_HEADER_TO")).append(": ").append(toHdr).append(CRLF);
         String ccHdr = msg.getDisplayCc();
        if (ccHdr != null)
            headers.append(LocaleSupport.getLocalizedMessage(pc, "ZM_HEADER_CC")).append(": ").append(ccHdr).append(CRLF);

        headers.append(LocaleSupport.getLocalizedMessage(pc, "ZM_HEADER_SENT")).append(": ").append(msg.getDisplaySentDate()).append(CRLF);

        String subjectHdr = msg.getSubject();
        if (subjectHdr != null)
            headers.append(LocaleSupport.getLocalizedMessage(pc, "ZM_HEADER_SUBJECT")).append(": ").append(subjectHdr).append(CRLF);
        return headers.toString();
    }

    private void forwardInclude(ZMessageBean msg, StringBuilder content, ZIdentity identity, PageContext pc) {
        if (identity.getForwardIncludeAsAttachment()) {
            mMessageAttachments = new ArrayList<MessageAttachment>();
            mMessageAttachments.add(new MessageAttachment(msg.getId(), msg.getSubject()));
        } else if (identity.getForwardIncludeBody()) {
            content.append(CRLF).append(CRLF).append(LocaleSupport.getLocalizedMessage(pc, "ZM_forwardedMessage")).append(CRLF);
            content.append(getQuotedHeaders(msg, pc)).append(CRLF);
            content.append(msg.getBody().getContent());
            content.append(CRLF);
            addAttachments(msg, true);
        } else if (identity.getForwardIncludeBodyWithPrefx()) {
            content.append(CRLF).append(CRLF).append(LocaleSupport.getLocalizedMessage(pc, "ZM_forwardPrefix", new Object[] {msg.getDisplayFrom()})).append(CRLF);
            content.append(getQuotedBody(msg, identity));
            content.append(CRLF);
            addAttachments(msg, true);
        }
    }

    private void replyInclude(ZMessageBean msg, StringBuilder content, ZIdentity identity, PageContext pc) {
        if (identity.getReplyIncludeNone()) {
            // nothing to see, move along
        } else if (identity.getReplyIncludeBody()) {
            content.append(CRLF).append(CRLF).append(LocaleSupport.getLocalizedMessage(pc, "ZM_originalMessage")).append(CRLF);
            content.append(getQuotedHeaders(msg, pc)).append(CRLF);
            content.append(msg.getBody().getContent());
            content.append(CRLF);
            addAttachments(msg, false);
        } else if (identity.getReplyIncludeBodyWithPrefx()) {
            content.append(CRLF).append(CRLF).append(LocaleSupport.getLocalizedMessage(pc, "ZM_replyPrefix", new Object[] {msg.getDisplayFrom()})).append(CRLF);
            content.append(getQuotedBody(msg, identity));
            content.append(CRLF);
            addAttachments(msg, false);
        } else if (identity.getReplyIncludeSmart()) {
            // TODO: duh
        } else if (identity.getReplyIncludeAsAttachment()) {
            mMessageAttachments = new ArrayList<MessageAttachment>();
            mMessageAttachments.add(new MessageAttachment(msg.getId(), msg.getSubject()));
        }
    }

    private void addAttachments(ZMessageBean msg, boolean checked) {
        List<ZMimePartBean> attachments = msg.getAttachments();
        setOrignalAttachments(attachments);
        if (checked) {
            for (ZMimePartBean part : attachments) {
                setCheckedAttachmentName(part.getPartName());
            }
        }
    }

    private String getQuotedBody(ZMessageBean msg, ZIdentity identity) {
        String prefixChar = identity.getForwardReplyPrefixChar();
        prefixChar = (prefixChar == null) ? "> " : prefixChar + " ";
        return BeanUtils.prefixContent(msg.getBody().getContent(), prefixChar);
    }

    private ZIdentity computeIdentity(ZMessageBean msg, List<ZIdentity> identities) {

        if (identities.size() == 1)
            return identities.get(0);

        if (msg == null)
            return defaultIdentity(identities);

        List<ZEmailAddress> addressList = new ArrayList<ZEmailAddress>();
        for (ZEmailAddress address: msg.getEmailAddresses()) {
            if (ZEmailAddress.EMAIL_TYPE_TO.equals(address.getType()) ||
                    ZEmailAddress.EMAIL_TYPE_CC.equals(address.getType())) {
                addressList.add(address);
            }
        }
        
        for (ZIdentity identity: identities) {
            for (ZEmailAddress address : addressList) {
                if (identity.containsAddress(address))
                    return identity;
            }
        }
        
        String folderId = msg.getFolderId();
        
        for (ZIdentity identity: identities) {
            if (identity.containsFolderId(folderId))
                return identity;
        }

        return defaultIdentity(identities);
        
    }

    private ZIdentity defaultIdentity(List<ZIdentity> identities) {
        if (identities.size() == 1)
            return identities.get(0);
        
        for (ZIdentity identity: identities) {
            if (identity.isDefault())
                return identity;
        }
        return identities.get(0);
    }


    private static String getReplySubject(String subject, PageContext pc) {
        String REPLY_PREFIX = LocaleSupport.getLocalizedMessage(pc, "ZM_replySubjectPrefix");                
        if (subject == null) subject = "";
        if ((subject.length() > 3) && subject.substring(0, 3).equalsIgnoreCase(REPLY_PREFIX))
            return subject;
        else
            return REPLY_PREFIX+" "+subject;
    }

    private static String getForwardSubject(String subject, PageContext pc) {
        String FORWARD_PREFIX = LocaleSupport.getLocalizedMessage(pc, "ZM_forwardSubjectPrefix");
        if (subject == null) subject = "";
        if ((subject.length() > 3) && subject.substring(0, 3).equalsIgnoreCase(FORWARD_PREFIX))
            return subject;
        else
            return FORWARD_PREFIX+" "+subject;
    }

    private static String getToAddress(List<ZEmailAddress> emailAddresses, List<ZEmailAddress> toAddressList, Set<String> toAddresses, Set<String> aliases) {
        for (ZEmailAddress address : emailAddresses) {
            if (ZEmailAddress.EMAIL_TYPE_REPLY_TO.equals(address.getType())) {
                if (aliases.contains(address.getAddress().toLowerCase()))
                    return "";
                toAddresses.add(address.getAddress());
                toAddressList.add(address);
                return address.getFullAddress();
            }
        }
        StringBuilder sb = new StringBuilder();
        for (ZEmailAddress address : emailAddresses) {
            if (ZEmailAddress.EMAIL_TYPE_FROM.equals(address.getType())) {
                if (!aliases.contains(address.getAddress().toLowerCase())) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(address.getFullAddress());
                    toAddressList.add(address);                
                    toAddresses.add(address.getAddress());
                }
            }
        }
        return sb.toString();
    }

    private static String getCcAddress(List<ZEmailAddress> emailAddresses, Set<String> toAddresses, Set<String> aliases) {
        StringBuilder sb = new StringBuilder();
        for (ZEmailAddress address : emailAddresses) {
            if (ZEmailAddress.EMAIL_TYPE_TO.equals(address.getType()) ||
                    ZEmailAddress.EMAIL_TYPE_CC.equals(address.getType())) {
                String a = address.getAddress().toLowerCase();
                if (!toAddresses.contains(a) && !aliases.contains(a)) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(address.getFullAddress());
                }
            }
        }
        return sb.toString();
    }

    public ZOutgoingMessage toOutgoingMessage() throws ServiceException {

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

        ZOutgoingMessage m = new ZOutgoingMessage();
        
        if (mMessageAttachments != null && mMessageAttachments.size() > 0) {
            List<String> messages = new ArrayList<String>();
            for (MessageAttachment ma : mMessageAttachments) {
                messages.add(ma.getId());
            }
            m.setMessageIdsToAttach(messages);
        }

        if (mCheckedAttachmentNames != null && mCheckedAttachmentNames.size() > 0) {
            List<AttachedMessagePart> attachments = new ArrayList<AttachedMessagePart>();
            for (Map.Entry<String,Boolean> entry : mCheckedAttachmentNames.entrySet()) {
                if (entry.getValue())
                    attachments.add(new AttachedMessagePart(mMessageId, entry.getKey()));
            }
            m.setMessagePartsToAttach(attachments);
        }

        /*
        if (mOriginalAttachments != null && mOriginalAttachments.size() > 0) {
            List<AttachedMessagePart> attachments = m.getMessagePartsToAttach();
            if (attachments == null) attachments = new ArrayList<AttachedMessagePart>();
            for (ZMimePartBean part : mOriginalAttachments) {
                attachments.add(new AttachedMessagePart(mMessageId, part.getPartName()));
            }
            m.setMessagePartsToAttach(attachments);
        }
        */

        m.setAddresses(addrs);

        m.setSubject(mSubject);

        if (mInReplyTo != null && mInReplyTo.length() > 0)
            m.setInReplyTo(mInReplyTo);

        m.setMessageParts(new ArrayList<MessagePart>());
        m.getMessageParts().add(new MessagePart(mContentType, mContent));

        if (mMessageId != null && mMessageId.length() > 0)
            m.setOriginalMessageId(mMessageId);

        if (mReplyType != null && mReplyType.length() > 0)
            m.setReplyType(mReplyType);

        m.setMessageParts(new ArrayList<MessagePart>());
        m.getMessageParts().add(new MessagePart(mContentType, mContent != null ? mContent : ""));

        return m;
    }
}
