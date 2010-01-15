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

import com.zimbra.cs.taglib.bean.ZUserAgentBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class GetUserAgentTag extends ZimbraSimpleTag {

    private static final String UA_SESSION = "ZUserAgentBean.SESSION";
    private String mVar;
    private boolean mSession = true;

    public void setVar(String var) { this.mVar = var; }
    public void setSession(boolean session) { this.mSession = session; }

    public void doTag() throws JspException, IOException {
        JspContext ctxt = getJspContext();
        ctxt.setAttribute(mVar, getUserAgent(ctxt, mSession),  PageContext.REQUEST_SCOPE);
    }

    public static ZUserAgentBean getUserAgent(JspContext ctxt, boolean session) {
        PageContext pctxt = (PageContext) ctxt;
        HttpServletRequest req = (HttpServletRequest) pctxt.getRequest();
        ZUserAgentBean ua;
        if (session) {
            ua = (ZUserAgentBean) ctxt.getAttribute(UA_SESSION, PageContext.SESSION_SCOPE);
            if ( ua == null) {
                ua = new ZUserAgentBean(req.getHeader("User-Agent"));
                ctxt.setAttribute(UA_SESSION, ua,  PageContext.SESSION_SCOPE);
            }

        } else {
            ua = new ZUserAgentBean(req.getHeader("User-Agent"));
        }
        return ua;
    }
    
}
