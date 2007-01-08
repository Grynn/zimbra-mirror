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
import com.zimbra.cs.taglib.bean.BeanUtils;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import java.util.HashMap;
import java.util.regex.Pattern;

public class ZJspSession {
 
    private static final String ATTR_SESSION = ZJspSession.class.getCanonicalName()+".session";
    private static final String ATTR_TEMP_AUTHTOKEN = ZJspSession.class.getCanonicalName()+".authToken";    
 
    public static final String COOKIE_NAME = "ZM_AUTH_TOKEN";
    
    private static final String CONFIG_ZIMBRA_SOAP_URL = "zimbra.soap.url";
    private static final String CONFIG_ZIMBRA_JSP_SESSION_TIMEOUT = "zimbra.jsp.session.timeout";            
    private static final String CONFIG_ZIMBRA_SEARCH_USE_OFFSET = "zimbra.search.useoffset";

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

    private static final String sProtocolMode = BeanUtils.getEnvString("protocolMode", PROTO_HTTP);
    private static final String sHttpsPort = BeanUtils.getEnvString("httpsPort", DEFAULT_HTTPS_PORT);
    private static final String sHttpPort = BeanUtils.getEnvString("httpPort", DEFAULT_HTTP_PORT);

    private static final Pattern sInitModePattern = Pattern.compile("&?initMode=https?", Pattern.CASE_INSENSITIVE);

    private static String getRedirect(HttpServletRequest request, String desiredProto, String initProto, String path) {
        String currentProto = request.getScheme();
        String qs = request.getQueryString();
        boolean emptyQs = qs == null || qs.equals("");
        if (path == null || path.equals(""))
            path = "/h/";

        if (initProto != null) {
                qs = "?" + (emptyQs ? "" :  qs + "&") + "initMode=" + initProto;
        } else if (!emptyQs) {
            // strip initMode off if it exists
            qs = "?" + sInitModePattern.matcher(qs).replaceAll("");
            if (qs.length() == 1) qs = "";
        }

        String contextPath = request.getContextPath();
        if(contextPath.equals("/")) contextPath = "";

        if (qs == null) qs = "";
        
        if (desiredProto.equals(PROTO_HTTPS)) {
            String httpsPort = (sHttpsPort != null && sHttpsPort.equals(DEFAULT_HTTPS_PORT)) ? "" : ":" + sHttpsPort;
            return PROTO_HTTPS + "://" + request.getServerName() + httpsPort + contextPath + path + qs;
        } else if (desiredProto.equals(PROTO_HTTP)) {
            String httpPort = (sHttpPort != null && sHttpPort.equals(DEFAULT_HTTP_PORT)) ? "" : ":" + sHttpPort;
            return PROTO_HTTP + "://" + request.getServerName() + httpPort + contextPath + path + qs;
        } else {
            return null;
        }
    }

    public static String getChangePasswordUrl(PageContext context, String path) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        String currentProto = request.getScheme();

        String proto = sProtocolMode.equals(PROTO_HTTP) ? PROTO_HTTP : PROTO_HTTPS;
        return getRedirect(request, proto, null, path);
    }

    public static String getPostLoginRedirectUrl(PageContext context, String path) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        String currentProto = request.getScheme();

        String initMode = request.getParameter("initMode");

        if (initMode == null || initMode.equals(currentProto) || !(initMode.equals(PROTO_HTTP) || initMode.equals(PROTO_HTTPS)))
            return null;

        return getRedirect(request, sProtocolMode.equals(PROTO_MIXED) ? initMode : sProtocolMode, null, path);
    }

    public static String getPreLoginRedirectUrl(PageContext context, String path) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        String currentProto = request.getScheme();

        if ((sProtocolMode.equals(PROTO_MIXED) || sProtocolMode.equals(PROTO_HTTPS)) &&
                currentProto.equals(PROTO_HTTP)) {
            return getRedirect(request, PROTO_HTTPS, PROTO_HTTP, path);
        } else if (currentProto.equals(PROTO_HTTPS) && sProtocolMode.equals(PROTO_HTTP)) {
            return getRedirect(request, PROTO_HTTP, PROTO_HTTPS, path);
        }
        return null;
    }

    public static boolean getSearchUseOffset(PageContext context) {
        String useOffset = (String) Config.find(context, CONFIG_ZIMBRA_SEARCH_USE_OFFSET);
        return useOffset != null && (useOffset.equalsIgnoreCase("true") || useOffset.equalsIgnoreCase("1"));
    }
    
    public static synchronized String getSoapURL(PageContext context) {
        if (sSoapUrl == null) {
            sSoapUrl = (String) Config.find(context, CONFIG_ZIMBRA_SOAP_URL);
            if (sSoapUrl == null) {
                if (sProtocolMode.equalsIgnoreCase(PROTO_HTTPS)) {
                    String httpsPort = (sHttpsPort != null && sHttpsPort.equals(DEFAULT_HTTPS_PORT)) ? "" : ":" + sHttpsPort;
                    sSoapUrl = "https://localhost" + httpsPort +"/service/soap";
                } else {
                    String httpPort = (sHttpPort != null && sHttpPort.equals(DEFAULT_HTTP_PORT)) ? "" : ":" + sHttpPort;
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
            mbox.noOp();
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
    
    public static ZJspSession setSession(PageContext context, ZMailbox mbox) throws ServiceException {
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
