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
package com.zimbra.cs.taglib.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;

public class PrefTag extends ZimbraSimpleTag {

    private String mName;
    private String mValue;

    public void setValue(String value) { mValue = value; }
    public void setName(String name) { mName = name; }

    public void doTag() throws JspException {
        ModifyPrefsTag op = (ModifyPrefsTag) findAncestorWithClass(this, ModifyPrefsTag.class);
        if (op == null)
                throw new JspTagException("The pref tag must be used within a modifyPrefs tag");
        op.addPref(mName, mValue);
    }

}
