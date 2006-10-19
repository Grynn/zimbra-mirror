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
package com.zimbra.cs.taglib.tag.contact;

import com.zimbra.cs.service.ServiceException;

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
            
            if (mAttrs.isEmpty())
                throw new JspTagException("no attrs specified for contact");

            String id = (mId == null || mId.length() == 0) ?
                    getMailbox().createContact(mFolderid, mTagids, mAttrs) :
                    getMailbox().modifyContact(mId, mReplace, mAttrs);
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
