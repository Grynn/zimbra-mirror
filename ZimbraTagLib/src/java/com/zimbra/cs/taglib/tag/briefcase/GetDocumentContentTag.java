/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.briefcase;

import com.zimbra.common.mime.MimeCompoundHeader;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZFolderBean;
import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZDocument;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.net.URLEncoder;
import java.io.*;
import java.nio.CharBuffer;

public class GetDocumentContentTag extends ZimbraSimpleTag {

    private String mVar;
    private String mId;
    private String mCharset = "UTF-8";
    private ZMailboxBean mMailbox;

    public void setId(String id) { this.mId = id; }
    public void setVar(String var) { this.mVar = var; }
    public void setCharset(String charset) { this.mCharset = charset; }
    public void setBox(ZMailboxBean mailbox) { this.mMailbox = mailbox; }

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pc = (PageContext) jctxt;
        try {
            ZMailbox mbox = mMailbox != null ? mMailbox.getMailbox() :  getMailbox();
			ZDocument doc = mbox.getDocument(this.mId);
            ZFolderBean fb =  new ZFolderBean(mbox.getFolderById(doc.getFolderId()));
            if(fb != null){
                InputStream is = mbox.getRESTResource(fb.getRootRelativePathURLEncoded()+"/"+(URLEncoder.encode(doc.getName(),"UTF-8").replace("+", "%20"))+"?fmt=native");
                InputStreamReader isr = new InputStreamReader(is,mCharset);
                StringBuffer buffer = new StringBuffer();
                Reader in = new BufferedReader(isr);
                int ch;
                while ((ch = in.read()) > -1) {
                    buffer.append((char)ch);
                }
                isr.close();
                in.close();
                jctxt.setAttribute(mVar, buffer.toString(), PageContext.PAGE_SCOPE);
            }

        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }

    }

}