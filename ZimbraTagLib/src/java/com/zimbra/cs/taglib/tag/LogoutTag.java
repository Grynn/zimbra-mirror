/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.tag;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.ZimbraAuthToken;
import com.zimbra.cs.taglib.ZJspSession;

public class LogoutTag extends ZimbraSimpleTag {
    
    public void doTag() throws JspException, IOException {
        JspContext jctxt = getJspContext();
        PageContext pageContext = (PageContext) jctxt;
        ZAuthToken authToken = ZJspSession.getAuthToken(pageContext);
        try {
        	if(authToken != null) {
        		AuthToken at = ZimbraAuthToken.getAuthToken(authToken.getValue());
        		if(at != null) {
        			at.deRegister();
        		}
        	}
        } catch (AuthTokenException e) {
			throw new JspTagException(e.getMessage(), e);
		} finally {
	        HttpServletResponse response = (HttpServletResponse) pageContext.getResponse(); 
	        ZAuthToken.clearCookies(response);
	        ZJspSession.clearSession(pageContext);
		}
    }
}
