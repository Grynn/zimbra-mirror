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
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
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

public class CreateContactTag extends ContactOpTag {

    private String mFolderid;
    private String mVar;
    private String mTagids;

    public void setFolderid(String folderid) { mFolderid = folderid; }
    public void setVar(String var) { mVar = var; }
    public void setTags(String tagids) { mTagids = tagids; }

    public void doTag() throws JspException, IOException {
        try {
            getJspBody().invoke(null);
            
            if (mAttrs.isEmpty() || allFieldsEmpty())
                throw ZTagLibException.EMPTY_CONTACT("can't create an empty contact", null);

            String id = getMailbox().createContact(mFolderid, mTagids, mAttrs);
            getJspContext().setAttribute(mVar, id, PageContext.PAGE_SCOPE);
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
