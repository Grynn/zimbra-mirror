/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.briefcase;

import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZMessageComposeBean;
import com.zimbra.cs.taglib.bean.ZMessageBean;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.FilePart;

public class SaveBriefcaseTag extends ZimbraSimpleTag {

    private String mVar;

    private ZMessageComposeBean mCompose;
    private ZMessageBean mMessage;
    private String mFolderId;

    public void setCompose(ZMessageComposeBean compose) { mCompose = compose; }
    public void setMessage(ZMessageBean message) { mMessage = message; }

    public void setFolderId(String folderId) { mFolderId = folderId; }
    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pc = (PageContext) jctxt;

        try {

            ZMailbox mbox = getMailbox();

            if (mCompose != null && mCompose.getHasFileItems()) {
                List<FileItem> mFileItems = mCompose.getFileItems();
                int num = 0;
                for (FileItem item : mFileItems) {
                    if (item.getSize() > 0) num++;
                }
                String[] briefIds = new String[num];
                int i=0;
                try {
                    for (FileItem item : mFileItems) {
                        if (item.getSize() > 0 ) {
                            Part part = new FilePart(item.getFieldName(), new ZMessageComposeBean.UploadPartSource(item), item.getContentType(), "utf-8");
                            String attachmentUploadId = mbox.uploadAttachments(new Part[] { part }, 1000 * 60);
                            briefIds[i++] = mbox.createDocument(mFolderId, item.getName(), attachmentUploadId);
                        }
                    }
                } finally {
                    for (FileItem item : mFileItems) {
                        try { item.delete(); } catch (Exception e) { /* TODO: need logging infra */ }
                    }
                }
                
                jctxt.setAttribute(mVar, briefIds, PageContext.PAGE_SCOPE);
            }
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }

    }

}
