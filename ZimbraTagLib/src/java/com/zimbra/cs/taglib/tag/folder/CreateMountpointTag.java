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
package com.zimbra.cs.taglib.tag.folder;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZFolderBean;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox.OwnerBy;
import com.zimbra.cs.zclient.ZMailbox.SharedItemBy;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class CreateMountpointTag extends ZimbraSimpleTag {

    private String mParentId;
    private String mName;
    private String mVar;
    private ZFolder.Color mColor;
    private ZFolder.View mView;
    private String mFlags;
    private OwnerBy mOwnerBy;
    private String mOwner;
    private SharedItemBy mSharedItemBy;
    private String mSharedItem;

    public void setParentid(String parentid) { mParentId = parentid; }
    public void setName(String name) { mName = name; }
    public void setFlags(String flags) { mFlags = flags; }
    public void setVar(String var) { mVar = var; }
    public void setColor(String color) throws ServiceException { mColor = ZFolder.Color.fromString(color); }
    public void setView(String view) throws ServiceException { mView = ZFolder.View.fromString(view); }
    public void setOwnerby(String ownerBy) throws ServiceException { mOwnerBy = OwnerBy.fromString(ownerBy); }
    public void setOwner(String owner) { mOwner = owner; }
    public void setShareditemby(String sharedItemBy) throws ServiceException { mSharedItemBy = SharedItemBy.fromString(sharedItemBy); }
    public void setShareditem(String sharedItem) { mSharedItem = sharedItem; }

    public void doTag() throws JspException, IOException {
        try {
            ZFolderBean result = new ZFolderBean(getMailbox().createMountpoint(mParentId, mName, mView, mColor, mFlags, mOwnerBy, mOwner, mSharedItemBy, mSharedItem));
            getJspContext().setAttribute(mVar, result, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
