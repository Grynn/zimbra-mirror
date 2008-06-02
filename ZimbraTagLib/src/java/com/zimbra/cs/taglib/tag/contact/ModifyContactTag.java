/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.ZTagLibException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ModifyContactTag extends ContactOpTag {

    private String mId;
    private String mVar;
    private String mFolderid;
    private String mTagids;
    private boolean mReplace;

    public void setId(String id) { mId = id; }
    public void setFolderid(String folderid) { mFolderid = folderid; }
    public void setVar(String var) { mVar = var; }
    public void setReplace(boolean replace) { mReplace = replace; }
    public void setTags(String tagids) { mTagids = tagids; } 

    public void doTag() throws JspException, IOException {
        try {
            getJspBody().invoke(null);

            if (mAttrs.isEmpty() || (mReplace && allFieldsEmpty())){
                throw ZTagLibException.EMPTY_CONTACT("can't set all fields to blank", null);
            }

            String id = (mId == null || mId.length() == 0) ?
                    getMailbox().createContact(mFolderid, mTagids, mAttrs) :
                    getMailbox().modifyContact(mId, mReplace, mAttrs);
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
