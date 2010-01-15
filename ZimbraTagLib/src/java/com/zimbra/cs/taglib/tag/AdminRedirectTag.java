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
package com.zimbra.cs.taglib.tag;

import com.zimbra.cs.taglib.ZJspSession;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.SkipPageException;
import java.io.IOException;

public class AdminRedirectTag extends ZimbraSimpleTag {

    String mDefaultPath = "/zimbraAdmin";

    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pageContext = (PageContext) jctxt;

        String adminRedirect = ZJspSession.getAdminLoginRedirectUrl(pageContext, mDefaultPath);
        if (adminRedirect != null) {
            HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();
            response.sendRedirect(adminRedirect);
            throw new SkipPageException();
        }
    }
}
