/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2009 Zimbra, Inc.
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
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZFolder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class UpdateFolderTag extends ZimbraSimpleTag {

    private String mId;
    private ZFolder.Color mColor;
    private String mName;
    private String mParentId;
    private String mFlags;

    public void setId(String id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setParentid(String parentId) { mParentId = parentId; }
    public void setFlags(String flags) { mFlags = flags; }
    public void setColor(String color) throws ServiceException { mColor = ZFolder.Color.fromString(color); }


    public void doTag() throws JspException, IOException {
        try {
            getMailbox().updateFolder(
                    mId,
                    StringUtil.isNullOrEmpty(mName) ? null : mName,
                    StringUtil.isNullOrEmpty(mParentId) ? null : mParentId,
                    mColor,
                    mFlags == null ? null : mFlags,
                    null);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
