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
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class ModifyFolderFreeBusyTag extends ZimbraSimpleTag {

    private String mId;
    private boolean mExclude;

    public void setId(String id) { mId = id; }
    public void setExclude(boolean exclude) { mExclude = exclude; }


    public void doTag() throws JspException, IOException {
        try {
            getMailbox().modifyFolderExcludeFreeBusy(mId, mExclude);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
