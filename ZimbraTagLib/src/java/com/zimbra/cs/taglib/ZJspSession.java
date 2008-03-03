/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.taglib.bean.BeanUtils;
import com.zimbra.cs.zclient.ZAuthResult;
import com.zimbra.cs.zclient.ZFolder;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.cs.account.Provisioning;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.core.Config;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ZJspSession {
 
    public static final String ATTR_SESSION = ZJspSession.class.getCanonicalName()+".session";
    private static final String ATTR_TEMP_AUTHTOKEN = ZJspSession.class.getCanonicalName()+".authToken";    
 
    // AP-TODO: COOKIE_NAME is no longer used, retire
    public static final String COOKIE_NAME = "ZM_AUTH_TOKEN";
    public static final String ZM_LAST_SERVER_COOKIE_NAME = "ZM_LAST_SERVER";

    
    private static final String CONFIG_ZIMBRA_SOAP_URL = "zimbra.soap.url";
    private static final String CONFIG_ZIMBRA_JSP_SESSION_TIMEOUT = "zimbra.jsp.session.timeout";            
    private static final String CONFIG_ZIMBRA_SEARCH_USE_OFFSET = "zimbra.search.useoffset";

    public static final String Q_ZAUTHTOKEN = "zauthtoken";
    public static final String Q_ZINITMODE = "zinitmode";
    public static final String Q_ZREMBERME = "zrememberme";
    public static final String Q_ZLASTSERVER = "zlastserver";

    private ZMailbox mMbox;
    private ZAuthToken mAuthToken;
 
    public ZJspSession(ZAuthToken authToken, ZMailbox mbox) {
        mAuthToken = authToken;
        mMbox = mbox;
    }
    
    public ZMailbox getMailbox() { return mMbox; }
    public ZAuthToken getAuthToken() { return mAuthToken; }

    private static String sSoapUrl = null;

	private static final String DEFAULT_HTTPS_PORT = "443";
	private static final String DEFAULT_HTTP_PORT = "80";
	private static final String PROTO_MIXED = "mixed";
	private static final String PROTO_HTTP = "http";
	private static final String PROTO_HTTPS = "https";

    private static final String sProtocolMode = BeanUtils.getEnvString("protocolMode", PROTO_HTTP);
    private static final boolean MODE_HTTP = sProtocolMode.equals(PROTO_HTTP);
    private static final boolean MODE_MIXED = sProtocolMode.equals(PROTO_MIXED);
    private static final boolean MODE_HTTPS = sProtocolMode.equals(PROTO_HTTPS);

    private static final String sHttpsPort = BeanUtils.getEnvString("httpsPort", DEFAULT_HTTPS_PORT);
    private static final String sHttpPort = BeanUtils.getEnvString("httpPort", DEFAULT_HTTP_PORT);

    private static final String sAdminUrl = BeanUtils.getEnvString("adminUrl", null);


    public static boolean secureAuthTokenCookie(HttpServletRequest request) {
        String initMode = request.getParameter(Q_ZINITMODE);
        boolean currentHttps = request.getScheme().equals(PROTO_HTTPS);
        return MODE_HTTPS || (currentHttps && (initMode == null || initMode.equals(PROTO_HTTPS))); 
    }

    private static void addParam(StringBuilder query, String name, String value) {
        if (query.length() > 0) query.append('&');
        if (value == null) value = "";
        try {
            query.append(name).append("=").append(URLEncoder.encode(value, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            // this should never happen...
            query.append(name).append("=").append(URLEncoder.encode(value));
        }
    }

    private static boolean isInQueryString(HttpServletRequest req, String name) {
        String qs = req.getQueryString();
        return (!(qs == null || qs.length() == 0)) && qs.indexOf(name + "=") != -1; 
    }
    
    private static String generateQueryString(HttpServletRequest req, Map<String,String> toAdd, Set<String> toRemove) {
        StringBuilder query = new StringBuilder();
        Enumeration names = req.getParameterNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            if (toRemove != null && !toRemove.contains(name) && isInQueryString(req, name)) {
                String values[] = req.getParameterValues(name);
                if (values != null) {
                    for (String value : values) {
                        addParam(query, name, value);
                    }
                }
            }
        }
        if (toAdd != null) {
            for (Entry<String, String> entry : toAdd.entrySet()) {
                addParam(query, entry.getKey(), entry.getValue());
            }
        }

        return query.length() > 0 ? "?" + query.toString()  : "";
    }

    private static String getRedirect(HttpServletRequest request,
                                      String proto,
                                      String host,
                                      String path,
                                      Map<String,String> paramsToAdd,
                                      Set<String> paramsToRemove)
    {
        if (path == null || path.equals(""))
            path = "/";

        String contextPath = request.getContextPath();
        if(contextPath.equals("/")) contextPath = "";

        String qs = generateQueryString(request, paramsToAdd, paramsToRemove);

        if (proto.equals(PROTO_HTTPS)) {
            String httpsPort = (sHttpsPort != null && sHttpsPort.equals(DEFAULT_HTTPS_PORT)) ? "" : ":" + sHttpsPort;
            return PROTO_HTTPS + "://" + host + httpsPort + contextPath + path + qs;
        } else if (proto.equals(PROTO_HTTP)) {
            String httpPort = (sHttpPort != null && sHttpPort.equals(DEFAULT_HTTP_PORT)) ? "" : ":" + sHttpPort;
            return PROTO_HTTP + "://" + host + httpPort + contextPath + path + qs;
        } else {
            return null;
        }
    }
    
    public static String getPostLoginRedirectUrl(PageContext context, String path, ZAuthResult authResult, boolean rememberMe, boolean needRefer) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        HttpServletResponse response = (HttpServletResponse) context.getResponse();

        String initMode = request.getParameter(Q_ZINITMODE);
        boolean hasIniitMode = initMode != null;
        boolean needsAuthtokenRemoved = request.getParameter(Q_ZAUTHTOKEN) != null;

        // see if we don't need to redirect
        if (!(needRefer || needsAuthtokenRemoved || hasIniitMode))
            return null;


        Map<String,String> toAdd = new HashMap<String, String>();
        Set<String> toRemove = new HashSet<String>();

        String proto;
        if (hasIniitMode && !needRefer) {
            if (MODE_MIXED && initMode.equals(PROTO_HTTP) && !request.getScheme().equals(PROTO_HTTP)) {
                proto = PROTO_HTTP;
            } else if (MODE_MIXED && initMode.equals(PROTO_HTTPS) && !request.getScheme().equals(PROTO_HTTPS)) {
                proto = PROTO_HTTPS;
            } else if (MODE_HTTPS) {
                proto = PROTO_HTTPS;
            } else {
                proto = PROTO_HTTP;
            }
            toRemove.add(Q_ZINITMODE);
        } else {
            proto = request.getScheme();
        }

        String host;
        if (needRefer) {
            host = authResult.getRefer();
            toAdd.put(Q_ZAUTHTOKEN, authResult.getAuthToken().getValue());
            if (rememberMe) {
                toAdd.put(Q_ZREMBERME, "1");
                Cookie lastServerCookie = new Cookie(ZJspSession.ZM_LAST_SERVER_COOKIE_NAME, host);
                long timeLeft = authResult.getExpires() - System.currentTimeMillis();
                if (timeLeft > 0) lastServerCookie.setMaxAge((int) (timeLeft/1000));
                lastServerCookie.setPath("/");
                response.addCookie(lastServerCookie);
            }
        } else {
            host = request.getServerName();
        }

        if (needsAuthtokenRemoved) {
            // strip off authtoken/rememberme if present
            toRemove.add(Q_ZAUTHTOKEN);
            toRemove.add(Q_ZREMBERME);
        }

        return getRedirect(request, proto, host, path, toAdd, toRemove);
    }

    public static String getChangePasswordUrl(PageContext context, String path) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        String proto = MODE_HTTP ? PROTO_HTTP : PROTO_HTTPS;
        return getRedirect(request, proto, request.getServerName(), path, null, null);
    }

    private static int[] sAdminPorts = null;

    private static synchronized boolean isAdminPort(int port, PageContext context) {
        if (sAdminPorts == null) {
            String portsStr = context.getServletContext().getInitParameter("admin.allowed.ports");
            String ports[] = portsStr != null ? portsStr.split(",") : null;
            if (ports != null) {
                sAdminPorts = new int[ports.length];
                int i=0;
                for (String p : ports) {
                    try { sAdminPorts[i] = Integer.parseInt(p.trim()); }
                    catch (NumberFormatException nfe) { sAdminPorts[i] = -1; }
                    i++;
                }
            } else {
                sAdminPorts = new int[0];
            }
        }
        for (int p : sAdminPorts) {
            if (p == port) return true;
        }
        return false;
    }

    public static String getAdminLoginRedirectUrl(PageContext context, String defaultPath) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest();
        if (isAdminPort(request.getServerPort(), context)) {
            String qs = request.getQueryString();
            String path = sAdminUrl != null ? sAdminUrl : defaultPath;
            if(qs != null)
                path = path + "?" + qs;
            return path;
        } else {
            return null;
        }
    }

    private static String getLastServer(HttpServletRequest request) {
        // make sure we aren't in a redirect loop
        if ("1".equals(request.getParameter(Q_ZLASTSERVER))) return null;
        String lastServer;
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return null;
        for (Cookie c : cookies){
            if (c.getName().equals(ZM_LAST_SERVER_COOKIE_NAME)) {
                lastServer = c.getValue();
                if (lastServer != null && !request.getServerName().equalsIgnoreCase(lastServer)) {
                    return lastServer;
                } else{
                    return null;
                }
            }
        }
        return null;
    }

    public static String getPreLoginRedirectUrl(PageContext context, String path) {
        HttpServletRequest request = (HttpServletRequest) context.getRequest();

        String lastServer = getLastServer(request);
        boolean CURRENT_HTTP = request.getScheme().equals(PROTO_HTTP);

        if (lastServer != null) {
            Map<String,String> toAdd = new HashMap<String, String>();
            toAdd.put(Q_ZLASTSERVER, "1"); // to hopefully prevent redirect loops
            return getRedirect(request, request.getScheme(), lastServer, path, toAdd, null);
        } 

        if (  ((MODE_MIXED || MODE_HTTPS) && CURRENT_HTTP) || (!CURRENT_HTTP && MODE_HTTP)) {
            Map<String,String> toAdd = new HashMap<String, String>();
            toAdd.put(Q_ZINITMODE, PROTO_HTTP);
            return getRedirect(request, PROTO_HTTPS, request.getServerName(), path, toAdd, null);
        } else {
            return null;
        }
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
            
    public static ZAuthToken getAuthToken(PageContext context) {
        // check here first, in case we are logging in and cookie isn't set yet.
        // String authToken = (String) context.getAttribute(ATTR_TEMP_AUTHTOKEN, PageContext.REQUEST_SCOPE);
        ZAuthToken authToken = (ZAuthToken) context.getAttribute(ATTR_TEMP_AUTHTOKEN, PageContext.REQUEST_SCOPE);
        if (authToken != null) return authToken;
        
        HttpServletRequest request= (HttpServletRequest) context.getRequest();
        Cookie[] cookies = request.getCookies();
        ZAuthToken zat = new ZAuthToken(cookies, false);
        if (zat.isEmpty())
            return null;
        else
            return zat;
    }

    public static boolean hasSession(PageContext context) {
        HttpServletRequest req = (HttpServletRequest) context.getRequest();
        if (req.getSession(false) == null)
            return false;

        ZAuthToken authToken = getAuthToken(context);
        ZJspSession sess = (ZJspSession) context.getAttribute(ATTR_SESSION, PageContext.SESSION_SCOPE);
        // see if we have a session that matches auth token
        return sess != null && sess.getAuthToken().equals(authToken);
    }

    public static ZJspSession getSession(PageContext context) throws ServiceException {
        ZJspSession sess = (ZJspSession) context.getAttribute(ATTR_SESSION, PageContext.SESSION_SCOPE);
        ZAuthToken authToken = getAuthToken(context);
        
        // see if we have a session that matches auth token
        if (sess != null && sess.getAuthToken().equals(authToken)) {
            return sess;
        }

        if (authToken == null || authToken.isEmpty()) {
            return null;    
        } else {
            // see if we can get a mailbox from the auth token
            ZMailbox.Options options = new ZMailbox.Options(authToken, getSoapURL(context));
            options.setClientIp(context.getRequest().getRemoteAddr());

            //options.setAuthAuthToken(true);
            ZMailbox mbox = ZMailbox.getMailbox(options);
            mbox.getAccountInfo(false);
            return setSession(context, mbox);
        }
    }

    public static ZMailbox getRestMailbox(PageContext context, String authToken, String targetAccountId) throws ServiceException {
        if (authToken == null || authToken.length() == 0) {
            return null;
        } else {
            // see if we can get a mailbox from the auth token
            ZMailbox.Options options = new ZMailbox.Options(authToken, getSoapURL(context));
            options.setNoSession(true);
            options.setAuthAuthToken(false);
            options.setTargetAccount(targetAccountId);
            options.setTargetAccountBy(Provisioning.AccountBy.id);
            options.setClientIp(context.getRequest().getRemoteAddr());
            return ZMailbox.getMailbox(options);
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
            //context.getSession().invalidate();
        } catch (Exception e) {
            // ignore if the session is already gone
        }
    }

}
