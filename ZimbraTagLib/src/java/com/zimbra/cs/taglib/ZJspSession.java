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
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.zclient.ZFolder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.jstl.core.Config;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.Context;
import java.util.HashMap;

public class ZJspSession {
 
    private static final String ATTR_SESSION = ZJspSession.class.getCanonicalName()+".session";
    private static final String ATTR_TEMP_AUTHTOKEN = ZJspSession.class.getCanonicalName()+".authToken";    
 
    public static final String COOKIE_NAME = "ZM_AUTH_TOKEN";
    
    private static final String CONFIG_ZIMBRA_SOAP_URL = "zimbra.soap.url";
    private static final String CONFIG_ZIMBRA_JSP_SESSION_TIMEOUT = "zimbra.jsp.session.timeout";            
    
    //TODO: get from config
    //public static final String SOAP_URL = "http://localhost:7070/service/soap";
    
    private ZMailbox mMbox;
    private String mAuthToken;
 
    public ZJspSession(String authToken, ZMailbox mbox) {
        mAuthToken = authToken;
        mMbox = mbox;
    }
    
    public ZMailbox getMailbox() { return mMbox; }
    public String getAuthToken() { return mAuthToken; }

    private static String sSoapUrl = null;

	private static final String DEFAULT_HTTPS_PORT = "443";
	private static final String DEFAULT_HTTP_PORT = "80";
	private static final String PROTO_MIXED = "mixed";
	private static final String PROTO_HTTP = "http";
	private static final String PROTO_HTTPS = "https";

    public static synchronized String getSoapURL(PageContext context) {
        if (sSoapUrl == null) {
            sSoapUrl = (String) Config.find(context, CONFIG_ZIMBRA_SOAP_URL);
            if (sSoapUrl == null) {
                // TODO: this code sohuld be shared with Login.JSP
                String protocolMode = null;
                String httpsPort = null;
                String httpPort = null;
                try {
                    Context initCtx = new InitialContext();
                    Context envCtx = (Context) initCtx.lookup("java:comp/env");
                    protocolMode = (String) envCtx.lookup("protocolMode");
                    httpsPort = (String) envCtx.lookup("httpsPort");
                    if (httpsPort != null && httpsPort.equals(DEFAULT_HTTP_PORT)) {
                        httpsPort = "";
                    } else {
                        httpsPort = ":" + httpsPort;
                    }
                    httpPort = (String) envCtx.lookup("httpPort");
                    if (httpPort != null && httpPort.equals(DEFAULT_HTTP_PORT)) {
                        httpPort = "";
                    } else {
                        httpPort = ":" + httpPort;
                    }
                } catch (NamingException ne) {
                    protocolMode = PROTO_HTTP;
                    httpsPort = DEFAULT_HTTPS_PORT;
                    httpPort = DEFAULT_HTTP_PORT;
                }
                if (protocolMode.equalsIgnoreCase(PROTO_HTTPS)) {
                    sSoapUrl = "https://localhost" + httpsPort +"/service/soap";
                } else {
                    sSoapUrl = "http://localhost" + httpPort +"/service/soap";
                }
            }
        }
        return sSoapUrl;
    }
    
    public static ZMailbox getZMailbox(PageContext context) throws JspException { 
        try {
            ZJspSession session = ZJspSession.getSession(context);
            if (session == null ) {
                throw ServiceException.AUTH_REQUIRED();
            } else {
                return session.getMailbox();
            }
        } catch (ServiceException e) {
            throw new JspTagException("getMailbox", e);
        }
    }
            
    public static String getAuthToken(PageContext context) {
        // check here first, in case we are logging in and cookie isn't set yet.
        String authToken = (String) context.getAttribute(ATTR_TEMP_AUTHTOKEN, PageContext.REQUEST_SCOPE);
        if (authToken != null) return authToken;
        
        HttpServletRequest request= (HttpServletRequest) context.getRequest();
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies){
            if (c.getName().equals(COOKIE_NAME)) {
                return c.getValue();
            }
        }
        return null;
    }
    
    public static ZJspSession getSession(PageContext context) throws ServiceException {
        ZJspSession sess = (ZJspSession) context.getAttribute(ATTR_SESSION, PageContext.SESSION_SCOPE);
        String authToken = getAuthToken(context);
        
        // see if we have a session that matches auth token
        if (sess != null && sess.getAuthToken().equals(authToken)) {
            return sess;
        }

        if (authToken == null || authToken.length() == 0) {
            return null;    
        } else {
            // see if we can get a mailbox from the auth token
            ZMailbox.Options options = new ZMailbox.Options(authToken, getSoapURL(context));
            ZMailbox mbox = ZMailbox.getMailbox(options);
            return setSession(context, mbox);
        }
    }

    public static void setCollapsed(ZFolder folder, HashMap<String,String> expanded) {
        if (!folder.getSubFolders().isEmpty()) {
            expanded.put(folder.getId(), "collapse");
            for (ZFolder child : folder.getSubFolders()) {
                setCollapsed(child, expanded);
            }
        }
    }
    
    public static ZJspSession setSession(PageContext context, ZMailbox mbox) {
        ZJspSession sess = new ZJspSession(mbox.getAuthToken(), mbox);
        // save auth token for duration of request (chicken/egg in getSession)
        context.setAttribute(ATTR_TEMP_AUTHTOKEN, mbox.getAuthToken(), PageContext.REQUEST_SCOPE);
        context.setAttribute(ATTR_SESSION, sess, PageContext.SESSION_SCOPE);
        HashMap<String,String> expanded = new HashMap<String, String>();
        for (ZFolder f : mbox.getUserRoot().getSubFolders()) {
            setCollapsed(f, expanded);
        }
        context.setAttribute("expanded", expanded, PageContext.SESSION_SCOPE);
        String timeOutStr = (String) Config.find(context, CONFIG_ZIMBRA_JSP_SESSION_TIMEOUT);
        if (timeOutStr != null) {
            try {
                context.getSession().setMaxInactiveInterval(Integer.parseInt(timeOutStr));
            } catch (NumberFormatException e) {
                // TODO: log
            }
        }
        return sess;
    }
    
    public static void clearSession(PageContext context) {
        try {
            context.getSession().invalidate();
        } catch (Exception e) {
            // ignore if the session is already gone
        }
    }

}
