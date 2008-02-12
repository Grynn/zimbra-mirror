/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.mailbox.Mailbox.OperationContext;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.servlet.ZimbraServlet;

public class ZimbraTag extends BodyTagSupport {

    /**
     * Override getContentStart and getContentEnd
     */
    public String getContentStart(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        return "";
    }

    public String getContentEnd(Account acct, OperationContext octxt) throws ZimbraTagException, ServiceException {
        return "";
    }

    private AuthToken getAuthToken() throws ZimbraTagException, ServiceException {
        HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
        
        AuthToken token = null;
        try {
            token = AuthProvider.getAuthToken(req, false);
            if (token == null)
                throw ZimbraTagException.AUTH_FAILURE("no auth cookie");
        } catch (AuthTokenException ate) {
            throw ZimbraTagException.AUTH_FAILURE("cannot parse authtoken");
        }

        if (token.isExpired()) {
            throw ZimbraTagException.AUTH_FAILURE("authtoken expired");
        }
        
        return token;
    }
    
    private Account getRequestAccount(AuthToken token) throws ZimbraTagException, ServiceException {
    	Provisioning prov = Provisioning.getInstance();
        Account acct = prov.get(Provisioning.AccountBy.id, token.getAccountId());
        if (acct == null) {
        	throw ZimbraTagException.AUTH_FAILURE("account not found "+token.getAccountId());
        }
        return acct;
    }

    public int doStartTag() throws JspTagException {
        try {
            AuthToken authToken = getAuthToken();
            Account acct = getRequestAccount(authToken);
            OperationContext octxt = new OperationContext(acct);

            String content = getContentStart(acct, octxt);
            if (content.length() > 0) {
                JspWriter out = pageContext.getOut();
                out.print(content);
            }
        } catch (IOException ioe) {
        	throw ZimbraTagException.IO_ERROR(ioe);
        } catch (ServiceException se){
        	throw ZimbraTagException.SERVICE_ERROR(se);
        }
        return SKIP_BODY;
    }

    public int doEndTag() throws JspTagException {
        try {
            AuthToken authToken = getAuthToken();
            Account acct = getRequestAccount(authToken);
            OperationContext octxt = new OperationContext(acct);

            String content = getContentEnd(acct, octxt);
            JspWriter out = pageContext.getOut();
            out.print(content);
        } catch (IOException ioe) {
        	throw ZimbraTagException.IO_ERROR(ioe);
        } catch (ServiceException se){
        	throw ZimbraTagException.SERVICE_ERROR(se);
        }
        return EVAL_PAGE;
    }
}
