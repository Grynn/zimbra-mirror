/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.taglib.bean.ZFolderBean;
import com.zimbra.cs.zclient.ZFolder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class CreateFolderTag extends ZimbraSimpleTag {

    private String mParentId;
    private String mName;
    private String mVar;
    private ZFolder.Color mColor;
    private ZFolder.View mView;
    private String mFlags;
    private String mUrl;

    public void setParentid(String parentid) { mParentId = parentid; }
    public void setName(String name) { mName = name; }
    public void setFlags(String flags) { mFlags = flags; }
    public void setVar(String var) { mVar = var; }
    public void setUrl(String url) { mUrl = url; }
    public void setColor(String color) throws ServiceException { mColor = ZFolder.Color.fromString(color); }
    public void setView(String view) throws ServiceException { mView = ZFolder.View.fromString(view); }    

    public void doTag() throws JspException, IOException {
        try {
            ZFolderBean result = new ZFolderBean(getMailbox().createFolder(mParentId, mName, mView, mColor, mFlags, mUrl));
            getJspContext().setAttribute(mVar, result, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
