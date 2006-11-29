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

    public static final String F_actionSend = "actionSend";
    public static final String F_actionCancel = "actionCancel";
    public static final String F_actionDraft = "actionDraft";
    public static final String F_doAction = "doAction";
    public static final String F_doComposeAction = "doComposeAction";            

    private static final long DEFAULT_MAX_SIZE = 100 * 1024 * 1024;

    private boolean mIsUpload;
    private List<FileItem> mItems;
    private ZMessageComposeBean mComposeBean;
    private boolean mIsSend;
    private boolean mIsCancel;
    private boolean mIsDraft;

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

    private ZMessageComposeBean getComposeBean(List<FileItem> items) {
        ZMessageComposeBean compose = new ZMessageComposeBean();
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
                } else if (name.equals(F_actionCancel)) {
                    mIsCancel = true;
                } else if (name.equals(F_actionSend)) {
                    mIsSend = true;
                } else if (name.equals(F_actionDraft)) {
                    mIsDraft = true;
                }
            }
        }
        return compose;
    }

    public List<FileItem> getItems() {
        return mItems;
    }

    public boolean getIsUpload() { return mIsUpload;}

    public ZMessageComposeBean getCompose() { return mComposeBean; }

    public boolean getIsCancel() { return mIsCancel; }

    public boolean getIsDraft() { return mIsDraft; }

    public boolean getIsSend() { return mIsSend; }
    
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
