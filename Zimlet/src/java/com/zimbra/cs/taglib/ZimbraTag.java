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
 * Portions created by Zimbra are Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
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

    public Account getRequestAccount() throws ZimbraTagException, ServiceException {
    	HttpServletRequest req = (HttpServletRequest)pageContext.getRequest();
        Cookie cookies[] =  req.getCookies();
        String authTokenStr = null;
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(ZimbraServlet.COOKIE_ZM_AUTH_TOKEN)) {
                    authTokenStr = cookies[i].getValue();
                    break;
                }
            }
        }
        if (authTokenStr == null) {
        	throw ZimbraTagException.AUTH_FAILURE("no auth cookie");
        }
        AuthToken token;
        try {
        	token = AuthToken.getAuthToken(authTokenStr);
        } catch (AuthTokenException ate) {
        	throw ZimbraTagException.AUTH_FAILURE("cannot parse authtoken");
        }
        if (token.isExpired()) {
        	throw ZimbraTagException.AUTH_FAILURE("authtoken expired");
        }
        Provisioning prov = Provisioning.getInstance();
        Account acct = prov.get(Provisioning.AccountBy.id, token.getAccountId());
        if (acct == null) {
        	throw ZimbraTagException.AUTH_FAILURE("account not found "+token.getAccountId());
        }
        return acct;
    }

    public int doStartTag() throws JspTagException {
        try {
            Account acct = getRequestAccount();
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
            Account acct = getRequestAccount();
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
