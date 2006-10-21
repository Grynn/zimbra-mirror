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

import com.zimbra.common.util.DateUtil;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZMessage;
import com.zimbra.cs.zclient.ZMessage.ZMimePart;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZMessageBean {

	private ZMessage mMsg;
    private Set<String> mUsedParts = new HashSet<String>();
    private int mExternalImages = 0;

    public ZMessageBean(ZMessage msg) {
		mMsg = msg;
	}
	   
    public String getId() { return mMsg.getId(); }

    public int getExternalImageCount() {
        return mExternalImages;
    }
    
    /**
     * @return comma-separated list of tag ids
     */
    public String getTagIds() { return mMsg.getTagIds(); }

    public String getPartName() { return mMsg.getPartName(); }

    public String getFlags() { return mMsg.getFlags(); }
    
    public boolean getHasFlags() { return mMsg.hasFlags(); }
    
    public boolean getHasTags() { return mMsg.hasTags(); }
    
    public boolean getIsUnread() { return mMsg.isUnread(); }

    public boolean getIsFlagged() { return mMsg.isFlagged(); }

    public boolean getHasAttachment() { return mMsg.hasAttachment(); }

    public boolean getIsRepliedTo() { return mMsg.isRepliedTo(); }

    public boolean getIsSentByMe() { return mMsg.isSentByMe(); }

    public boolean getIsForwarded() { return mMsg.isForwarded(); }

    public boolean getIsDraft() { return mMsg.isDraft(); }

    public boolean getIsDeleted() { return mMsg.isDeleted(); }

    public boolean getIsNotificationSent() { return mMsg.isNotificationSent(); }
    
    public String getSubject() { return mMsg.getSubject(); }
    
    public String getFolderId() { return mMsg.getFolderId(); }
    
    public String getConversationId() { return mMsg.getConversationId(); }

    public Date getReceivedDate() { return new Date(mMsg.getReceivedDate()); }
    
    public Date getSentDate() { return new Date(mMsg.getSentDate()); }
    
    public String getDisplaySentDate() { return DateUtil.toRFC822Date(getSentDate()); }

    public String getMessageIdHeader() { return mMsg.getMessageIdHeader(); }
    
    public List<ZEmailAddress> getEmailAddresses() { return mMsg.getEmailAddresses(); }
    
    public ZMimePart getMimeStructure() { return mMsg.getMimeStructure(); }

    public long getSize() { return mMsg.getSize(); }
    
    /** @return content of the message, if raw is specified. if message too big or not ASCII, a content servlet URL is returned */
    public String getContent() { return mMsg.getContent(); }
    
    /** @return if raw is specified and message too big or not ASCII, a content servlet URL is returned */
    public String getContentURL() { return mMsg.getContentURL(); }

    public String getDisplayBody() {
        ZMimePart body = getBody(mMsg.getMimeStructure());
        return body == null ? null : body.getContent();
    }

    public String getDisplayBodyHtml() {
        ZMimePart body = getBody(mMsg.getMimeStructure());
        return body == null ? null : BeanUtils.textToHtml(body.getContent());
    }

    // TODO: LOTS OF CRAP. handle html, format text -> html, etc
    private ZMimePart getBody(ZMimePart mp) {
        if (mp == null) return null;
        else if (mp.isBody()) return mp;
        for (ZMimePart child : mp.getChildren()) {
            ZMimePart cmp = getBody(child);
            if (cmp != null) return cmp;
        }
        return null;
    }

    public ZMimePartBean getBody() {
        ZMimePart body = getBody(mMsg.getMimeStructure());
        return body == null ? null : new ZMimePartBean(body);
    }

    private List<ZMimePartBean> mAttachments;

    private void  addAttachments(List<ZMimePartBean> list, ZMimePart part) {
        if (part.isBody()) return;
        if (mUsedParts.contains(part.getPartName())) return;

        boolean rfc822 = ZMimePartBean.CT_MSG_RFC822.equalsIgnoreCase(part.getContentType());

        if (
                rfc822 ||
                part.getContentLocation() != null ||
                "attachment".equalsIgnoreCase(part.getContentDispostion()) ||
                part.getContentId() != null ||
                part.getFileName()!= null) {
            if (!ZMimePartBean.isIgnoredPArt(part))
                list.add(new ZMimePartBean(part));
        }

        if (rfc822) return;

        for (ZMimePart child: part.getChildren()) {
            addAttachments(list, child);
        }
    }

    public synchronized List<ZMimePartBean> getAttachments() {
        if (mAttachments == null) {
            mAttachments = new ArrayList<ZMimePartBean>();
            ZMimePart top = mMsg.getMimeStructure();
            //if (top.getContentType().equalsIgnoreCase(Mime.CT_MULTIPART_MIXED)) {
            for (ZMimePart child: top.getChildren()) {
                addAttachments(mAttachments, child);
            }
        }
        return mAttachments;
    }

    public int getNumberOfAttachments() {
        return getAttachments().size();
    }
    
    public String getDisplayTo() {
        return BeanUtils.getHeaderAddrs(getEmailAddresses(), ZEmailAddress.EMAIL_TYPE_TO);
    }
    
    public String getDisplayFrom() {
        return BeanUtils.getHeaderAddrs(getEmailAddresses(), ZEmailAddress.EMAIL_TYPE_FROM);
    }
    
    public String getDisplayCc() {
        return BeanUtils.getHeaderAddrs(getEmailAddresses(), ZEmailAddress.EMAIL_TYPE_CC);
    }
    
    public String getDisplayBcc() {
        return BeanUtils.getHeaderAddrs(getEmailAddresses(), ZEmailAddress.EMAIL_TYPE_BCC);
    }
    
    public String getDisplaySender() {
        return BeanUtils.getHeaderAddrs(getEmailAddresses(), ZEmailAddress.EMAIL_TYPE_SENDER);
    }

    public String getDisplayReplyTo() {
        return BeanUtils.getHeaderAddrs(getEmailAddresses(), ZEmailAddress.EMAIL_TYPE_REPLY_TO);
    }

    private static Pattern sIMG = Pattern.compile("(<IMG.+)dfsrc=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);

    public String getBodyHtmlContent() {
        ZMimePart root = mMsg.getMimeStructure();
        ZMimePart body = getBody(root);
        if (root == null || body == null) return null;

        boolean isRelated = root.getContentType().equals(ZMimePartBean.CT_MULTI_RELATED);

        Matcher m = sIMG.matcher(body.getContent());

        if (isRelated) {
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String src = m.group(2);
                if (src != null && src.startsWith("cid:")) {
                    src = resolveContentId(src.substring(4), root);
                } else if (src != null && src.indexOf(':') == -1) {
                    src = resolveContentLocation(src, root);
                }
                if (src != null)
                    m.appendReplacement(sb, m.group(1) + "src=\"" + src + "\"");
                else {
                    mExternalImages++;
                    m.appendReplacement(sb, m.group(0));
                }
            }
            m.appendTail(sb);
            return sb.toString();
        } else {
            while (m.find()) {
                mExternalImages++;
            }
            return body.getContent();
        }
    }

    private String resolveContentId(String origcid, ZMimePart root) {
        String cid = "<" + origcid + ">";
        for (ZMimePart part : root.getChildren()) {
            String partCid = part.getContentId();
            if (cid.equals(partCid)) {
                mUsedParts.add(part.getPartName());
                // TODO: move this into ZMimePart                
                return "/home/~/?id="+mMsg.getId()+"&part="+part.getPartName()+"&auth=co";
            }
        }
        return null;
    }

    private String resolveContentLocation(String src, ZMimePart root) {
        for (ZMimePart part : root.getChildren()) {
            String partCL = part.getContentLocation();
            if (src.equals(partCL)) {
                mUsedParts.add(part.getPartName());
                // TODO: move this into ZMimePart
                return "/home/~/?id="+mMsg.getId()+"&part="+part.getPartName()+"&auth=co";
            }
        }
        return null;
    }
}
