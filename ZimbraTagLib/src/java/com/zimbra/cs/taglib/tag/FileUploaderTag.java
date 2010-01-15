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

package com.zimbra.cs.taglib.tag;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZFileUploaderBean;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class FileUploaderTag extends ZimbraSimpleTag {

    private String mVar;

    public void setVar(String var) { this.mVar = var; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pc = (PageContext) jctxt;
        ZFileUploaderBean fup = (ZFileUploaderBean) jctxt.getAttribute(mVar, PageContext.REQUEST_SCOPE);
        if (fup == null) {
            try {
                jctxt.setAttribute(mVar, new ZFileUploaderBean(pc, getMailbox()), PageContext.REQUEST_SCOPE);
            } catch (ServiceException e) {
                throw new JspTagException("file upload failed", e);
            }
        }
    }
}
