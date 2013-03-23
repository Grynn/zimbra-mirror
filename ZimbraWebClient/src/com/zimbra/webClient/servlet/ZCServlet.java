/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 VMware, Inc.
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

package com.zimbra.webClient.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.naming.*;

public class ZCServlet extends HttpServlet
{
    
    private static final String PARAM_AUTH_TOKEN = "authToken";
    private static final String PARAM_QUERY_STRING_TO_CARRY = "qs";
    private static final String PARAM_AUTH_TOKEN_LIFETIME = "atl";

    private static String redirectLocation;
    private static String httpsPort;
    private static String httpPort;
    private static String protocolMode;
    private static final String DEFAULT_HTTPS_PORT = "443";
    private static final String DEFAULT_HTTP_PORT = "80";
    private static final String PROTO_MIXED = "mixed";
    private static final String PROTO_HTTP = "http";
    private static final String PROTO_HTTPS = "https";
    
    
    static 
    {
        try {
            Context initCtx = new InitialContext();
            Context envCtx = (Context) initCtx.lookup("java:comp/env");
            protocolMode = (String) envCtx.lookup("protocolMode");
            httpsPort = (String) envCtx.lookup("httpsPort");
            if (httpsPort != null && httpsPort.equals(DEFAULT_HTTP_PORT)){
                httpsPort = "";
            } else {
                httpsPort = ":" + httpsPort;    
            }
            httpPort = (String) envCtx.lookup("httpPort");
            if (httpPort != null && httpPort.equals(DEFAULT_HTTP_PORT)){
                httpPort = "";
            } else {
                httpPort = ":" + httpPort;    
            }
            
        } catch (NamingException ne) {
            protocolMode = PROTO_HTTP;
            httpsPort = "";
            httpPort = "";
        }
    }

    public void doGet (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, java.io.IOException
    {
        doPost(req, resp);
    }
    
    public void doPost (HttpServletRequest req, HttpServletResponse resp) 
        throws ServletException, java.io.IOException
    {
        return;
    }

    protected boolean shouldRedirectUrl( HttpServletRequest req )
    {
        String proto = req.getScheme();
        boolean ret = false;
        String initMode = getReqParameter(req, "initMode");
        if ( !protocolMode.equals(proto) && !(protocolMode.equals(PROTO_MIXED)
                                              &&( (initMode == null) || 
                                                  initMode.equals(proto))) ) {
            ret = true;
        }
        return ret;
    }
    

    /**
     * getRedirectUrl assumes that the caller needs to redirect based
     * on the mode configured in the web.xml and the protocol being
     * used for the current request. If the caller is not sure, they
     * should call shouldRedirectUrl.
     * @see #shouldRedirectUrl
     */
    protected String getRedirectUrl( HttpServletRequest req )
    {
        return getRedirectUrl(req, null, null, true, false);
    }
    protected String getRedirectUrl( HttpServletRequest req, String uri, 
                                     String queryString, boolean absolute,
                                     boolean removeInitMode)
    {
        StringBuffer buf = new StringBuffer();
        String qs = req.getQueryString();
        StringBuffer tempQs = new StringBuffer();
        if (removeInitMode) {
            if (qs != null) {
                String [] pairs = qs.split("&");
                for ( int i = 0; i < pairs.length;++i) {
                    if (pairs[i].indexOf("initMode") == -1){
                        if (i > 0){
                            tempQs.append("&");
                        }
                        tempQs.append(pairs[i]);
                    }
                }   
            }
        }
        if (tempQs.length() > 0 ){
            qs = "?" + tempQs.toString();
        } else {
            qs = "";
        }
        
        //System.out.println("absolute? = " + absolute);
        if (absolute) {       
            String proto = req.getScheme();
            int port = req.getServerPort();
            String portString = port == 80? "" : ":" + port;
            if (protocolMode.equals(PROTO_MIXED)) {
                // look for the initMode query parameter.
                // if it's there redirect to that protocol, otherwise,
                // keep the protocol the same as the one used to
                // request this page.
                String initMode = getReqParameter(req, "initMode");
                if (initMode != null ) {
                    boolean inOnHttps = (proto.equals(PROTO_HTTPS));
                    proto = initMode;
                    if (inOnHttps && initMode.equals(PROTO_HTTPS) ||
                        !inOnHttps && initMode.equals(PROTO_HTTP)) {
                        //don't change the port
                    } else {
                        portString = proto.equals(PROTO_HTTP)? httpPort:
                            httpsPort;
                    }
                } else {
                    // proto remains unchanged;
                    // portString remains unchanged
                }
            } else if (protocolMode.equals(PROTO_HTTP)){
                if (proto.equals(PROTO_HTTPS)){
                    proto = PROTO_HTTP;
                    portString = httpPort;
                }
            } else {
                // https mode
                if (proto.equals(PROTO_HTTP)){
                    proto = PROTO_HTTPS;
                    portString = httpsPort;
                }
            }
            buf.append(proto);
            buf.append("://");
            buf.append(req.getServerName());
            buf.append(portString);
        }
        
        if (uri != null){
            buf.append(uri);
        } else {
            buf.append(req.getRequestURI());
        }
        buf.append(qs);
        if(queryString != null) {
            if (!qs.equals("")){
                buf.append("&");
            } else {
                buf.append("?");
            }
            buf.append(queryString);
        }
        return buf.toString();
    }
    
    protected String getReqParameter(HttpServletRequest req, String paramName,
                                     String defaultValue)
    {
        String val = req.getParameter(paramName);
        if (val == null || val.equals("")){
            val = defaultValue;
        }
        return val;
    }

    protected String getReqParameter(HttpServletRequest req, String paramName)
    {
        return getReqParameter(req, paramName, null);
    }
    
	
}
