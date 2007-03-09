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

import com.zimbra.cs.taglib.bean.ZMessageComposeBean.MessageAttachment;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ZComposeUploaderBean {

    public static final String F_to = "to";
    public static final String F_attendees = "attendees";
    public static final String F_cc = "cc";
    public static final String F_bcc = "bcc";
    public static final String F_subject = "subject";
    public static final String F_messageAttachment = "messageAttachment";
    public static final String F_originalAttachment = "originalAttachment";
    public static final String F_body = "body";
    public static final String F_replyto = "replyto";
    public static final String F_from = "from";
    public static final String F_inreplyto = "inreplyto";
    public static final String F_messageid = "messageid";
    public static final String F_draftid = "draftid";
    public static final String F_fileUpload = "fileUpload";
    public static final String F_contactSearchQuery  = "contactSearchQuery";
    public static final String F_contactLocation = "contactLocation";

    public static final String F_addTo = "addTo";
    public static final String F_addCc = "addCc";
    public static final String F_addBcc = "addBcc";

    public static final String F_pendingTo = "pendingTo";
    public static final String F_pendingCc = "pendingCc";
    public static final String F_pendingBcc = "pendingBcc";

    public static final String F_actionSend = "actionSend";
    public static final String F_actionCancel = "actionCancel";
    public static final String F_actionDraft = "actionDraft";
    public static final String F_actionAttachDone = "actionAttachDone";
    public static final String F_actionAttachCancel = "actionAttachCancel";
    public static final String F_actionAttachAdd = "actionAttachAdd";

    public static final String F_actionContactDone = "actionContactDone";
    public static final String F_actionContactCancel = "actionContactCancel";
    public static final String F_actionContactAdd = "actionContactAdd";
    public static final String F_actionContactSearch = "actionContactSearch";

    public static final String F_doAction = "doAction";
    public static final String F_doComposeAction = "doComposeAction";            

    private static final long DEFAULT_MAX_SIZE = 100 * 1024 * 1024;

    private boolean mIsUpload;
    private List<FileItem> mItems;
    private ZMessageComposeBean mComposeBean;
    private boolean mIsSend;
    private boolean mIsCancel;
    private boolean mIsDraft;
    private boolean mIsAttachAdd;
    private boolean mIsAttachDone;
    private boolean mIsAttachCancel;
    private boolean mIsContactAdd;
    private boolean mIsContactDone;
    private boolean mIsContactCancel;
    private boolean mIsContactSearch;
    private String mContactSearchQuery;
    private String mPendingTo;
    private String mPendingCc;
    private String mPendingBcc;
    private String mContactLocation;

    public ZComposeUploaderBean(HttpServletRequest req) throws JspTagException {
            DiskFileUpload upload = getUploader();
            try {

                mIsUpload = DiskFileUpload.isMultipartContent(req);
                if (mIsUpload) {
                    mItems = upload.parseRequest(req);
                    mComposeBean = getComposeBean(mItems);
                }
            } catch (FileUploadBase.SizeLimitExceededException e) {
                // at least one file was over max allowed size
                throw new JspTagException(ZTagLibException.UPLOAD_SIZE_LIMIT_EXCEEDED("size limit exceeded", e));
            } catch (FileUploadBase.InvalidContentTypeException e) {
                // at least one file was of a type not allowed
                throw new JspTagException(ZTagLibException.UPLOAD_FAILED(e.getMessage(), e));
            } catch (FileUploadException e) {
            	// parse of request failed for some other reason
                throw new JspTagException(ZTagLibException.UPLOAD_FAILED(e.getMessage(), e));
            }
	}

    private boolean isAction(String name, String action) {
        return name.equals(action) || name.equals(action+".x");
    }

    private ZMessageComposeBean getComposeBean(List<FileItem> items) {
        ZMessageComposeBean compose = new ZMessageComposeBean();
        StringBuilder addTo = null, addCc = null, addBcc = null;

        // TODO: Just toss into some sort of hash before this ends up on thedailywtf
        for (FileItem item : items) {
            if (!item.isFormField()) {
                // deal with attachment uploads later
                if (item.getFieldName().equals(F_fileUpload) && item.getName() != null && item.getName().length() > 0) {
                    compose.addFileItem(item);
                }
            } else {
                String name = item.getFieldName();
                String value;
                try { value = item.getString("utf-8"); } catch (UnsupportedEncodingException e) { value = item.getString();}
                if (name.equals(F_to)) {
                    compose.setTo(value);
                } else if (name.equals(F_attendees)) {
                    compose.setAttendees(value);
                } else if (name.equals(F_cc)) {
                    compose.setCc(value);
                } else if (name.equals(F_bcc)) {
                    compose.setBcc(value);
                } else if (name.equals(F_subject)) {
                    compose.setSubject(value);
                } else if (name.equals(F_messageAttachment)) {
                    int i = value.indexOf(':');
                    String id = i == -1 ? value : value.substring(0, i);
                    String subject = i == -1 ? null : value.substring(i+1);
                    compose.getMessageAttachments().add(new MessageAttachment(id, subject));
                } else if (name.equals(F_originalAttachment)) {
                    compose.setCheckedAttachmentName(value);
                } else if (name.equals(F_body)) {
                    compose.setContent(value);
                } else if (name.equals(F_replyto)) {
                    compose.setReplyTo(value);
                } else if (name.equals(F_from)) {
                    compose.setFrom(value);
                } else if (name.equals(F_inreplyto)) {
                    compose.setInReplyTo(value);
                } else if (name.equals(F_messageid)) {
                    compose.setMessageId(value);
                } else if (name.equals(F_draftid)) {
                    compose.setDraftId(value);
                } else if (isAction(name, F_actionCancel)) {
                    mIsCancel = true;
                } else if (isAction(name, F_actionSend)) {
                    mIsSend = true;
                } else if (isAction(name, F_actionDraft)) {
                    mIsDraft = true;
                } else if (isAction(name, F_actionAttachDone)) {
                    mIsAttachDone = true;
                } else if (isAction(name, F_actionAttachCancel)) {
                    mIsAttachCancel = true;
                } else if (isAction(name, F_actionAttachAdd)) {
                    mIsAttachAdd = true;
                } else if (isAction(name, F_actionContactDone)) {
                    mIsContactDone = true;
                } else if (isAction(name, F_actionContactCancel)) {
                    mIsContactCancel = true;
                } else if (isAction(name, F_actionContactAdd)) {
                    mIsContactAdd = true;
                } else if (isAction(name, F_actionContactSearch)) {
                    mIsContactSearch = true;
                } else if (name.equals(F_contactSearchQuery)) {
                    mContactSearchQuery = value;
                } else if (name.equals(F_addTo)) {
                    if (addTo == null) addTo = new StringBuilder();
                    if (addTo.length() > 0) addTo.append(", ");
                    addTo.append(value);
                } else if (name.equals(F_addCc)) {
                    if (addCc == null) addCc = new StringBuilder();
                    if (addCc.length() > 0) addCc.append(", ");
                    addCc.append(value);
                } else if (name.equals(F_addBcc)) {
                    if (addBcc == null) addBcc = new StringBuilder();
                    if (addBcc.length() > 0) addBcc.append(", ");
                    addBcc.append(value);
                } else if (name.equals(F_pendingTo)) {
                    mPendingTo = value;
                } else if (name.equals(F_pendingCc)) {
                    mPendingCc = value;
                } else if (name.equals(F_pendingBcc)) {
                    mPendingBcc = value;
                } else if (name.equals(F_contactLocation)) {
                    mContactLocation = value;
                }
            }
        }

        if (getIsContactDone()) {
            if (mPendingTo != null) compose.setTo(addToList(compose.getTo(), mPendingTo));
            if (mPendingCc != null) compose.setCc(addToList(compose.getCc(), mPendingCc));
            if (mPendingBcc != null) compose.setBcc(addToList(compose.getBcc(), mPendingBcc));
            if (addTo != null) compose.setTo(addToList(compose.getTo(), addTo.toString()));
            if (addCc != null) compose.setCc(addToList(compose.getCc(), addCc.toString()));
            if (addBcc != null) compose.setBcc(addToList(compose.getBcc(), addBcc.toString()));
        } else {
            if (addTo != null) mPendingTo = addToList(mPendingTo, addTo.toString());
            if (addCc != null) mPendingCc = addToList(mPendingCc, addCc.toString());
            if (addBcc != null) mPendingBcc = addToList(mPendingBcc, addBcc.toString());
        }
        
        return compose;
    }

    private String addToList(String currentValue, String newValue) {
        currentValue = currentValue.trim();
        if (currentValue != null && currentValue.length() > 1) {
            if (currentValue.charAt(currentValue.length()-1) == ',')
                return currentValue + " " + newValue;
            else
                return currentValue + ", " + newValue;

        } else {
            return newValue;
        }
    }

    public List<FileItem> getItems() {
        return mItems;
    }

    public boolean getIsUpload() { return mIsUpload;}

    public ZMessageComposeBean getCompose() { return mComposeBean; }

    public boolean getIsCancel() { return mIsCancel; }

    public boolean getIsDraft() { return mIsDraft; }

    public boolean getIsSend() { return mIsSend; }

    public boolean getIsAttachCancel() { return mIsAttachCancel; }

    public boolean getIsAttachDone() { return mIsAttachDone; }

    public boolean getIsAttachAdd() { return mIsAttachAdd; }

    public boolean getIsContactCancel() { return mIsContactCancel; }

    public boolean getIsContactDone() { return mIsContactDone; }

    public boolean getIsContactAdd() { return mIsContactAdd; }

    public boolean getIsContactSearch() { return mIsContactSearch; }

    public String getContactSearchQuery() { return mContactSearchQuery; }

    public String getPendingTo() { return mPendingTo; }

    public String getPendingCc() { return mPendingCc; }

    public String getPendingBcc() { return mPendingBcc; }

    public String getContactLocation() { return mContactLocation; }
    
    private static DiskFileUpload getUploader() {
        // look up the maximum file size for uploads
        // TODO: get from config,
        long maxSize = DEFAULT_MAX_SIZE;

        DiskFileUpload upload = new DiskFileUpload();
        upload.setSizeThreshold(4096);     // in-memory limit
        upload.setSizeMax(maxSize);
        upload.setRepositoryPath(getTempDirectory());
        return upload;
    }

    private static String getTempDirectory() {
    	return System.getProperty("java.io.tmpdir", "/tmp");
    }
}
