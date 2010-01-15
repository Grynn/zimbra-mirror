/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZFileUploaderBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZMailbox.ZImportContactsResult;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ImportContactsTag extends ZimbraSimpleTag {

    private String mVar;
    private String mFolderId;
    private String mType = ZMailbox.CONTACT_IMPORT_TYPE_CSV;
    private ZFileUploaderBean mUploader;

    public void setUploader(ZFileUploaderBean uploader) { mUploader = uploader; }

    public void setVar(String var) { mVar = var; }
    public void setFolderid(String folderid) { mFolderId = folderid; }
    public void setType(String type) { mType = type; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        try {
            ZMailbox mbox = getMailbox();
            String attachmentId = mUploader.getUploadId(mbox);
            if (attachmentId != null) {
                ZImportContactsResult result = mbox.importContacts(mFolderId, mType, attachmentId);
                jctxt.setAttribute(mVar, result, PageContext.PAGE_SCOPE);
            }
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
