/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;

import javax.servlet.jsp.JspTagException;
import java.util.HashMap;
import java.util.Map;

public class ContactOpTag extends ZimbraSimpleTag {

    protected boolean mForce;
    protected Map<String, String> mAttrs = new HashMap<String,String>();

    public void setForce(boolean force) { mForce = force; }

    public void addAttr(String name, String value) throws JspTagException {
        if (!mForce) {
            try {
                ContactConstants.Attr.fromString(name); // make sure it is a known attr name
            } catch (ServiceException e) {
                throw new JspTagException(e);
            }
        }
        mAttrs.put(name, value);
    }

    protected boolean allFieldsEmpty() {
        for (Map.Entry<String,String> entry : mAttrs.entrySet()) {
            if (entry.getValue() != null && entry.getValue().trim().length() > 0 && !entry.getKey().equalsIgnoreCase(ContactConstants.A_fileAs)){
                return false;
            }
        }
        return true;
    }
}
