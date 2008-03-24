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

import com.zimbra.common.util.DateUtil;
import com.zimbra.cs.zclient.ZEmailAddress;
import com.zimbra.cs.zclient.ZInvite;
import com.zimbra.cs.zclient.ZMessage;
import com.zimbra.cs.zclient.ZMessage.ZMimePart;
import com.zimbra.cs.zclient.ZShare;

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
    private ZShare mShare = null;

    public ZMessageBean(ZMessage msg) {
		mMsg = msg;
	}
	   
    public String getId() { return mMsg.getId(); }

    public int getExternalImageCount() {
        return mExternalImages;
    }

    public ZInvite getInvite() { return mMsg.getInvite(); }

    public synchronized ZShare getShare() {
        return mMsg.getShare();
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

    public boolean getIsHighPriority() { return mMsg.isHighPriority(); }

    public boolean getIsLowPriority() { return mMsg.isLowPriority(); }

    public boolean getHasAttachment() { return mMsg.hasAttachment(); }

    public boolean getIsRepliedTo() { return mMsg.isRepliedTo(); }

    public boolean getIsSentByMe() { return mMsg.isSentByMe(); }

    public boolean getIsForwarded() { return mMsg.isForwarded(); }

    public boolean getIsDraft() { return mMsg.isDraft(); }

    public boolean getIsDeleted() { return mMsg.isDeleted(); }

    public boolean getIsNotificationSent() { return mMsg.isNotificationSent(); }
    
    public String getSubject() { return mMsg.getSubject(); }

    public String getFragment() { return mMsg.getFragment(); }

    public String getFolderId() { return mMsg.getFolderId(); }
    
    public String getConversationId() { return mMsg.getConversationId(); }

    public String getReplyType() { return mMsg.getReplyType(); }

    public String getInReplyTo() { return mMsg.getInReplyTo(); }
    
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
        //if (part.isBody()) return;
        if (mUsedParts.contains(part.getPartName())) return;

        boolean rfc822 = ZMimePartBean.CT_MSG_RFC822.equalsIgnoreCase(part.getContentType());
        String ct = part.getContentType() != null ? part.getContentType().toLowerCase() : "";
        
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

    public String getAttachmentIds() {
        StringBuilder sb = new StringBuilder();
        for (ZMimePartBean part : getAttachments()) {
            if (sb.length() > 0) sb.append(",");
            sb.append(part.getPartName());
        }
        return sb.toString();
    }

    public synchronized List<ZMimePartBean> getAttachments() {
        if (mAttachments == null) {
            mAttachments = new ArrayList<ZMimePartBean>();
            ZMimePart top = mMsg.getMimeStructure();
            //if (top.getContentType().equalsIgnoreCase(Mime.CT_MULTIPART_MIXED)) {
            if (top != null) {
                for (ZMimePart child: top.getChildren()) {
                    addAttachments(mAttachments, child);
                }
                if (mAttachments.isEmpty() && !top.isBody()) {
                    ZMimePartBean bb = new ZMimePartBean(top);
                    if (!bb.isBody() && (bb.getIsVideo() || bb.getIsImage() || bb.getIsAudio() || bb.getIsApp())) {
                        mAttachments.add(bb);
                    }
                }
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

    private static Pattern sIMG = Pattern.compile("(<IMG[^>]+)dfsrc=\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);


    private static String escapeDollarSign(String value) {
        if (value == null || value.length() == 0 || value.indexOf('$') == -1)
            return value;
        return value.replace("$", "\\$");
    }

    String getBodyHtmlContent(ZMimePartBean part) {
        if (part == null) return null;

        ZMimePart parent = part.getMimePart().getParent();

        boolean isRelated = false;

        while (parent != null) {
            isRelated = parent.getContentType().equals(ZMimePartBean.CT_MULTI_RELATED);
            if (isRelated)
                break;
            else
                parent = parent.getParent();
        }

        Matcher m = sIMG.matcher(part.getContent());

        if (isRelated) {
            StringBuffer sb = new StringBuffer();
            while (m.find()) {
                String src = m.group(2);
                if (src != null && src.startsWith("cid:")) {
                    src = resolveContentId(src.substring(4), parent);
                } else if (src != null && src.indexOf(':') == -1) {
                    src = resolveContentLocation(src, parent);
                }
                if (src != null)
                    m.appendReplacement(sb, escapeDollarSign(m.group(1) + "src=\"" + src + "\""));
                else {
                    //mExternalImages++;
                    m.appendReplacement(sb, escapeDollarSign(m.group(0)));
                }
            }
            m.appendTail(sb);
            return sb.toString();
        } else {
            while (m.find()) {
                mExternalImages++;
            }
            return part.getContent();
        }
    }

    public String toString() {
        return mMsg.toString();
    }

    private String resolveContentId(String origcid, ZMimePart parent) {
        String cid = "<" + origcid + ">";
        for (ZMimePart part : parent.getChildren()) {
            String partCid = part.getContentId();
            if (cid.equals(partCid)) {
                mUsedParts.add(part.getPartName());
                // TODO: move this into ZMimePart                
                return "/home/~/?id="+mMsg.getId()+"&part="+part.getPartName()+"&auth=co";
            }
        }
        return null;
    }

    private String resolveContentLocation(String src, ZMimePart parent) {
        for (ZMimePart part : parent.getChildren()) {
            String partCL = part.getContentLocation();
            if (src.equals(partCL)) {
                mUsedParts.add(part.getPartName());
                // TODO: move this into ZMimePart
                return "/home/~/?id="+mMsg.getId()+"&part="+part.getPartName()+"&auth=co";
            }
        }
        return null;
    }

    public static String getHtmlContent(ZMimePartBean part, ZMessageBean message) {
        if (part.getIsTextHtml()) {
            return message.getBodyHtmlContent(part);
        } else if (part.getIsTextPlain()) {
            return BeanUtils.textToHtml(part.getContent());
        } else {
            return "";
        }
    }

    public static List<ZMimePartBean> getAdditionalBodies(ZMimePartBean body, ZMessageBean message) {
        List<ZMimePartBean> result = new ArrayList<ZMimePartBean>();
        if (body != null) {
            ZMimePart top = message.getMimeStructure();
            for (ZMimePart child: top.getChildren()) {
                addBody(result, body.getMimePart(), child);
            }
        }
        return result;
    }

    private static void addBody(List<ZMimePartBean> result, ZMimePart body, ZMimePart child) {
        if (body != child && child.isBody()) {
            ZMimePartBean mpb = new ZMimePartBean(child);
            if (mpb.getIsTextHtml() || mpb.getIsTextPlain()) {
                result.add(mpb);
            }
        }
        for (ZMimePart c: child.getChildren()) {
            addBody(result, body, c);
        }
    }

    public static ZMimePartBean getPart(ZMessageBean message, String partName) {
        List<ZMimePartBean> result = new ArrayList<ZMimePartBean>();
        ZMimePart top = message.getMimeStructure();
        for (ZMimePart child: top.getChildren()) {
            ZMimePartBean mpb = getPartInternal(child, partName);
            if (mpb != null) return mpb;
        }
        return null;
    }

    private static ZMimePartBean getPartInternal(ZMimePart child, String partName) {
        if (child.getPartName().equals(partName)) {
            return new ZMimePartBean(child);
        }
        for (ZMimePart c: child.getChildren()) {
            ZMimePartBean b = getPartInternal(c, partName);
            if (b != null) return b;
        }
        return null;
    }
}
