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
package com.zimbra.cs.taglib.tag.signature;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.tag.ZimbraSimpleTag;
import com.zimbra.cs.zclient.ZSignature;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import java.io.IOException;

public class ModifySignatureTag extends ZimbraSimpleTag {

    private String mId;
    private String mName;
    private String mValue;
    private String mType = "text/plain";
    
    public void setId(String id) { mId = id; }
    public void setName(String name) { mName = name; }
    public void setValue(String value) { mValue = value; }
    public void setType(String type) { mType = type; }
    
    public void doTag() throws JspException, IOException {
        try {
            getMailbox().modifySignature(new ZSignature(mId, mName, mValue, mType));
        } catch (ServiceException e) {
            throw new JspTagException(e);
        }
    }
}
