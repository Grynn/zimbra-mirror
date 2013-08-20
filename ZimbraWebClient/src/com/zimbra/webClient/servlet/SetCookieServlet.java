/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
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

package com.zimbra.webClient.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.net.URL;

import javax.naming.*;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.ZimbraCookie;

@SuppressWarnings("serial")
public class SetCookieServlet extends ZCServlet
{
    
    private static final String PARAM_AUTH_TOKEN = "authToken";
    private static final String PARAM_REMEMBER_ME = "rememberMe";
    private static final String PARAM_AUTH_TOKEN_LIFETIME = "atl";
    private static final String DEFAULT_MAIL_URL = "/zimbra/mail";
    
    private static final String HEADER_HOST = "host";
    private static final String HEADER_REFERER = "referer";

    private static String redirectLocation;
    
    public void init(ServletConfig servletConfig) {
	try {
	    Context initCtx = new InitialContext();
	    Context envCtx = (Context) initCtx.lookup("java:comp/env");
	    redirectLocation = (String) envCtx.lookup("mailUrl");
	} catch (NamingException ne) {
	    ne.printStackTrace();
	}
        if (redirectLocation == null) {
            redirectLocation = DEFAULT_MAIL_URL;
	    // ZimbraLog.webclient.debug("Default redirectLocation ..." + redirectLocation);
        } else {
	    redirectLocation = redirectLocation + "/mail";
	    //ZimbraLog.webclient.debug("Setting redirectLocation to specified " + redirectLocation);
	}
    }

    public void doGet (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, IOException
    {
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }
    
    public void doPost (HttpServletRequest req, HttpServletResponse resp) {
        
        try {
            String authToken = getReqParameter(req, PARAM_AUTH_TOKEN);
            if (authToken == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
            
            
            String atl = getReqParameter(req, PARAM_AUTH_TOKEN_LIFETIME);
            String rememberMe = getReqParameter(req, PARAM_REMEMBER_ME);
            boolean doRemember = false;
            if (rememberMe != null) {
                doRemember = new Boolean(rememberMe).booleanValue();
            }

            int lifetime = -1;
            if (doRemember){
                try {
                    int lifetimeMs = Integer.parseInt(atl);
                    lifetime = lifetimeMs / 1000;
                } catch (NumberFormatException ne){
                    lifetime = -1;
                }
            }
            
            String host = req.getHeader(HEADER_HOST);
            String referer = req.getHeader(HEADER_REFERER);
            //System.out.println("Host == " + host + " referer = " + referer);
            boolean abs = true;
            if (!shouldRedirectUrl(req) && referer != null && (referer.matches("[^/]*//" + host + "/.*")) ) {
                abs = false;
            } else {
                abs = true;
            }
            String redirectTo = getRedirectUrl(req, redirectLocation, null, 
                                               abs, true);
            //System.out.println("RedirectTo = " + redirectTo);
            
            boolean secureCookie;
            if (abs) {
                URL url = new URL(redirectTo);
                secureCookie = "https".equalsIgnoreCase(url.getProtocol());
            } else {
                secureCookie = req.isSecure();
            }

            String authCookieVal = getCookieValue(req, ZimbraCookie.COOKIE_ZM_AUTH_TOKEN);
            if (!(authToken.equals(authCookieVal))) {
                Integer maxAge = null;
                if (lifetime != -1) {
                    maxAge = Integer.valueOf(lifetime);
                }
                
                ZimbraCookie.addHttpOnlyCookie(resp, 
                        ZimbraCookie.COOKIE_ZM_AUTH_TOKEN, authToken, 
                        ZimbraCookie.PATH_ROOT, maxAge, secureCookie);
            }
            
            resp.sendRedirect(redirectTo);
        } catch (IOException ie) {
	    // do nothing
        } catch (IllegalStateException is){
	    // do nothing
        } catch (Exception ex) {
        	ZimbraLog.webclient.warn("exception setting cookie", ex);
            if (!resp.isCommitted())
            	resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }    

    private String getCookieValue (HttpServletRequest req, String name) {
        Cookie[] cookies = req.getCookies();
        String value = null;
        if (cookies != null) {
            for (int idx = 0; idx < cookies.length; ++idx) {
                if (cookies[idx].getName().equals(name)){
                    value = cookies[idx].getValue();
                }
            }
        }
        return value;
    }
    
	
}
