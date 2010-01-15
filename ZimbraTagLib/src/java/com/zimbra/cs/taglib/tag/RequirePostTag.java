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
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.bean.ZMailboxBean;
import com.zimbra.common.service.ServiceException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class RequirePostTag extends ZimbraSimpleTag {

    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        PageContext pageContext = (PageContext) ctxt;
        HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
        try {
            if (!request.getMethod().equals("POST"))
            throw ServiceException.INVALID_REQUEST("only valid with POST", null);
        } catch (ServiceException e) {
            throw new JspTagException(e.getMessage(), e);
        }
    }
}
