/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.cs.nginx;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.cs.extension.ZimbraExtension;

public class NginxLookupExtension implements ZimbraExtension {

    public static final String NAME = "nginx-lookup";
    
    public String getName() {
        return NAME;
    }
    
    public void init() throws ServiceException {
        ExtensionDispatcherServlet.register(this, new NginxLookupHandler());
    }
    
    public void destroy() {
        ExtensionDispatcherServlet.unregister(this);
    }

    @SuppressWarnings("serial")
    public static class NginxLookupException extends Exception {
        public NginxLookupException(String msg) {
            super(msg);
        }
    }
    
    private static class NginxLookupRequest {
        String user;
        String suffix;
        String pass;
        String proto;
        String clientIp;
        String serverIp;
        int loginAttempt;
        HttpServletRequest  httpReq;
        HttpServletResponse httpResp;
    }
    
    public static class NginxLookupHandler extends ExtensionHttpHandler {
        /* req headers */
        public static final String AUTH_METHOD        = "Auth-Method";
        public static final String AUTH_USER          = "Auth-User";
        public static final String AUTH_PASS          = "Auth-Pass";
        public static final String AUTH_PROTOCOL      = "Auth-Protocol";
        public static final String AUTH_LOGIN_ATTEMPT = "Auth-Login-Attempt";
        public static final String CLIENT_IP          = "Client-IP";
        public static final String SERVER_IP          = "X-Proxy-IP";
        
        /* resp headers */
        public static final String AUTH_STATUS = "Auth-Status";
        public static final String AUTH_SERVER = "Auth-Server";
        public static final String AUTH_PORT   = "Auth-Port";
        public static final String AUTH_WAIT   = "Auth-Wait";

        public static final long DEFAULT_WAIT_INTERVAL = 10;

        /* Generic Error Message for failure */
        public static final String ERRMSG = "login failed";

        /* protocols */
        public static final String IMAP     = "imap";
        public static final String IMAP_SSL = "imapssl";
        public static final String POP3     = "pop3";
        public static final String POP3_SSL = "pop3ssl";
        public static final String HTTP     = "http";
        
        private static final SearchControls USER_SC   = new SearchControls(SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);
        private static final SearchControls SERVER_SC = new SearchControls(SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);
        private static final SearchControls DOMAIN_SC = new SearchControls(SearchControls.SUBTREE_SCOPE, 1, 0, null, false, false);
        
        public static final Log logger = LogFactory.getLog("zimbra.nginx");
        
        public boolean hideFromDefaultPorts() {
            return true;
        }
        
        public void init(ZimbraExtension ext) throws ServiceException {
            super.init(ext);
            Config config = Provisioning.getInstance().getConfig();
            String attr;
            
            ArrayList<String> attrs = new ArrayList<String>();
            
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyMailHostAttribute);
            if (attr != null)
                attrs.add(attr);
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyUserNameAttribute);
            if (attr != null)
                attrs.add(attr);
            if (attrs.size() > 0)
                USER_SC.setReturningAttributes(attrs.toArray(new String[0]));
            
            attrs.clear();
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyPop3PortAttribute);
            if (attr != null)
                attrs.add(attr);
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyPop3SSLPortAttribute);
            if (attr != null)
                attrs.add(attr);
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyImapPortAttribute);
            if (attr != null)
                attrs.add(attr);
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyImapSSLPortAttribute);
            if (attr != null)
                attrs.add(attr);
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyHttpPortAttribute);
            if (attr != null)
                attrs.add(attr);
            if (attrs.size() > 0)
                SERVER_SC.setReturningAttributes(attrs.toArray(new String[0]));
            
            attrs.clear();
            attr = config.getAttr(Provisioning.A_zimbraReverseProxyDomainNameAttribute);
            if (attr != null)
                attrs.add(attr);
            if (attrs.size() > 0)
                DOMAIN_SC.setReturningAttributes(attrs.toArray(new String[0]));
        }
        
        public void doGet(HttpServletRequest httpReq, HttpServletResponse resp) throws IOException, ServletException {
            try {
                NginxLookupRequest req = checkRequest(httpReq);
                req.httpReq  = httpReq;
                req.httpResp = resp;
                search(req);
            } catch (NginxLookupException ex) {
                sendError(resp, ex.getMessage());
            }
        }

        /**
         * Parse the HTTP request headers and construct the NginxLookupRequest object
         * @param httpReq The HTTP Servlet Request object
         * @return    NginxLookupRequest object containing details of the lookup request
         * @throws NginxLookupException
         */
        private NginxLookupRequest checkRequest(HttpServletRequest httpReq) throws NginxLookupException {
            NginxLookupRequest req = new NginxLookupRequest();
            String user = httpReq.getHeader(AUTH_USER);
            // req.user = httpReq.getHeader(AUTH_USER);
            req.pass = httpReq.getHeader(AUTH_PASS);
            req.proto = httpReq.getHeader(AUTH_PROTOCOL);
            if (user == null)
                throw new NginxLookupException("missing header field "+AUTH_USER);
            
            /* PR 20542 - If the user name ends with any zimbra supported extension (/tb|/wm|/ni)
             * then we need to strip off that extension before proceeding
             */
            if (user.endsWith ("/tb") || user.endsWith("/wm") || user.endsWith("/ni")) {
                req.suffix = user.substring (user.length() - 3);
                req.user = user.substring (0, user.length() - 3);
            } else {
                req.user = user;
                req.suffix = null;
            }
            if (req.pass == null)
                throw new NginxLookupException("missing header field "+AUTH_PASS);
            if (req.proto == null)
                throw new NginxLookupException("missing header field "+AUTH_PROTOCOL);
            req.clientIp = httpReq.getHeader(CLIENT_IP);
            req.serverIp = httpReq.getHeader(SERVER_IP);
            
            String val = httpReq.getHeader(AUTH_LOGIN_ATTEMPT);
            if (val != null) {
                try {
                    req.loginAttempt = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                }
            }
            return req;
        }
        
        private Map<String, String> lookupAttrs(Config config, SearchResult sr, Map<String, Boolean> keys) throws NginxLookupException, NamingException {
            Map<String, String> vals = new HashMap<String, String>();
            for (Map.Entry<String, Boolean> keyEntry : keys.entrySet()) {
                String key = keyEntry.getKey();
                String val = lookupAttr(config, sr, key, keyEntry.getValue());
                if (val != null)
                    vals.put(key, val);
            }
            
            return vals;
        }
        
        private String lookupAttr(Config config, SearchResult sr, String key, Boolean required) throws NginxLookupException, NamingException {
            String val = null;
            String attr = config.getAttr(key);
            if (attr == null && required)
                throw new NginxLookupException("missing attr in config: "+key);
            if (attr != null) {
                val = LdapUtil.getAttrString(sr.getAttributes(), attr);
                if (val == null)
                    throw new NginxLookupException("missing attr in search result: "+attr);
            }
            return val;
        }
        
        private String getAttrForProto(String proto) throws NginxLookupException {
            if (IMAP.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyImapPortAttribute;
            else if (IMAP_SSL.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyImapSSLPortAttribute;
            else if (POP3.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyPop3PortAttribute;
            else if (POP3_SSL.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyPop3SSLPortAttribute;
            else if (HTTP.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyHttpPortAttribute;
            else
                throw new NginxLookupException("unsupported protocol: "+proto);
        }
        
        private String searchDirectory(DirContext ctxt, SearchControls sc, Config config, 
                                       String queryTemplate, String searchBase, String templateKey, String templateVal,
                                       String attr) throws NginxLookupException, NamingException {
            Object result = searchDir(ctxt, sc, config, queryTemplate,  searchBase,  templateKey,  templateVal, attr);
            return (String)result;
        }
        
        private Map<String, String> searchDirectory(DirContext ctxt, SearchControls sc, Config config, 
                String queryTemplate, String searchBase, String templateKey, String templateVal,
                Map<String, Boolean> attrs) throws NginxLookupException, NamingException {
            Object result = searchDir(ctxt, sc, config, queryTemplate,  searchBase,  templateKey,  templateVal, attrs);
            return (Map<String, String>)result;
        }
        
        private Object searchDir(DirContext ctxt, SearchControls sc, Config config, 
                                 String queryTemplate, String searchBase, String templateKey, String templateVal,
                                 Object attrs) throws NginxLookupException, NamingException {
            HashMap<String, String> kv = new HashMap<String,String>();
            kv.put(templateKey, LdapUtil.escapeSearchFilterArg(templateVal));
            String query = config.getAttr(queryTemplate);
            String base  = config.getAttr(searchBase);
            if (query == null)
            throw new NginxLookupException("empty attribute: "+queryTemplate);
            
            logger.debug("query template attr=" + queryTemplate + ", query template=" + query);
            query = StringUtil.fillTemplate(query, kv);
            logger.debug("query=" + query);
            
            if (base == null)
            base = "";
            
            NamingEnumeration ne = LdapUtil.searchDir(ctxt, base, query, sc);
            try {
                if (!ne.hasMore())
                    throw new NginxLookupException("query returned empty result: "+query);
                SearchResult sr = (SearchResult) ne.next();
                if (attrs instanceof String)
                    return lookupAttr(config, sr, (String)attrs, Boolean.TRUE);
                else
                    return lookupAttrs(config, sr, (Map<String, Boolean>)attrs);
            } finally {
                if (ne != null)
                    ne.close();
            }
        }
        
        private String userByVirtualIP(DirContext ctxt, Config config, NginxLookupRequest req) {
                
            boolean hasDomain = (req.user.indexOf('@') != -1);
                
            // get user by virtual domain only if domain is not already present
            if (hasDomain)
                return null;
                
            String domainName = null;
            if (req.serverIp != null) {
                try {
                    domainName = searchDirectory(
                                            ctxt, 
                                            DOMAIN_SC, 
                                            config, 
                                            Provisioning.A_zimbraReverseProxyDomainNameQuery,
                                            Provisioning.A_zimbraReverseProxyDomainNameSearchBase,
                                            "IPADDR",
                                            req.serverIp,
                                            Provisioning.A_zimbraReverseProxyDomainNameAttribute);
                } catch (NginxLookupException e) {
                    logger.warn("domain not found for user " + req.user + ".  error: " + e.getMessage());
                } catch (NamingException e) {
                    logger.warn("domain not found for user " + req.user + ".  error: " + e.getMessage());
                }
            }
                            
            if (domainName == null) {
                domainName = config.getAttr(Provisioning.A_zimbraDefaultDomainName);
                logger.debug("domain not found for user " + req.user + ", using default domain: " + (domainName==null?"null":domainName));
            }
                
            if (domainName != null) {
                String userName = req.user + "@" + domainName;
                logger.debug(AUTH_USER + " " + req.user + " is replaced by " + userName + " for mailhost lookup");
                return userName;
            } else {
                logger.warn("domain not found for user " + req.user);
                return null;
            }    
        }
        
        private void search(NginxLookupRequest req) throws NginxLookupException {
            DirContext ctxt = null;
            try {
                ctxt = LdapUtil.getDirContext();
                Config config = Provisioning.getInstance().getConfig();
                String authUser = userByVirtualIP(ctxt, config, req);
                
                Map<String, Boolean> attrs = new HashMap<String, Boolean>();
                attrs.put(Provisioning.A_zimbraReverseProxyMailHostAttribute, true);
                attrs.put(Provisioning.A_zimbraReverseProxyUserNameAttribute, false);
                Map<String, String> vals = searchDirectory(ctxt, 
                                                           USER_SC, 
                                                           config, 
                                                           Provisioning.A_zimbraReverseProxyMailHostQuery,
                                                           Provisioning.A_zimbraReverseProxyMailHostSearchBase,
                                                           "USER",
                                                           (authUser==null)? req.user : authUser,
                                                           attrs);
                String mailhost = vals.get(Provisioning.A_zimbraReverseProxyMailHostAttribute);
                String userName = vals.get(Provisioning.A_zimbraReverseProxyUserNameAttribute);
                if (userName != null)
                    authUser = userName;

                if (mailhost == null)
                    throw new NginxLookupException("mailhost not found for user: "+req.user);
                
                String addr = InetAddress.getByName(mailhost).getHostAddress();
                logger.debug("mailhost="+mailhost+" ("+addr+")");
                String port = null;
                try {
                    port = searchDirectory(ctxt, 
                                           SERVER_SC, 
                                           config, 
                                           Provisioning.A_zimbraReverseProxyPortQuery,
                                           Provisioning.A_zimbraReverseProxyPortSearchBase,
                                           "MAILHOST",
                                           mailhost,
                                           getAttrForProto(req.proto));
                } catch (NginxLookupException e) {
                    // the server does not have bind port overrides.
                    logger.debug("using port from globalConfig");
                    String lookupAttr = getAttrForProto(req.proto);
                    String bindPortAttr = config.getAttr(lookupAttr);
                    if (bindPortAttr == null)
                        throw new NginxLookupException("missing config attr: "+lookupAttr);
                    port = config.getAttr(bindPortAttr);
                    if (bindPortAttr == null)
                        throw new NginxLookupException("missing config attr: "+bindPortAttr);
                }

                logger.debug("port="+port);
                sendResult(req, addr, port, authUser);
            } catch (ServiceException e) {
                throw new NginxLookupException("service exception: "+e.getMessage());
            } catch (NamingException e) {
                throw new NginxLookupException("naming exception: "+e.getMessage());
            } catch (UnknownHostException e) {
                throw new NginxLookupException("naming exception: "+e.getMessage());
            } finally {
                if (ctxt != null)
                    LdapUtil.closeContext(ctxt);
            }
        }

        /**
         * Send the routing information HTTP response back to the NGINX IMAP proxy
         * @param req    The HTTP request object
         * @param server    The requested mail server name
         * @param port        The requested mail server port
         * @param authUser    If not null, then this value is sent back to override the login 
         *                     user name, (usually) with a domain suffix added
         */
        private void sendResult(NginxLookupRequest req, String server, String port, String authUser) {
            HttpServletResponse resp = req.httpResp;
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.addHeader(AUTH_STATUS, "OK");
            resp.addHeader(AUTH_SERVER, server);
            resp.addHeader(AUTH_PORT, port);
            
            if (authUser != null) {
                /* Append any login extension if necessary */
                if (req.suffix != null) {
                    authUser = authUser + req.suffix;
                }
                logger.debug("rewrite " + AUTH_USER + " to: " + authUser);
                resp.addHeader(AUTH_USER, authUser);
            }
        }

        /** 
         * Indicate an error to the calling (NGINX) proxy
         * @param resp  The HTTP response object
         * @param msg   The error message (a generic error message is sent back to the caller, the original message is logged)
         */
        private void sendError(HttpServletResponse resp, String msg) {
            
            logger.info ("Error while looking up IMAP/POP route information: " + msg);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.addHeader(AUTH_STATUS, ERRMSG);
            
            String waitInterval = null;
            try {
                Config config = Provisioning.getInstance().getConfig();
                long wi = config.getTimeIntervalSecs(Provisioning.A_zimbraReverseProxyAuthWaitInterval, DEFAULT_WAIT_INTERVAL);
                waitInterval = "" + wi;
            } catch (ServiceException e) {
                logger.warn("cannot get config");
                waitInterval = "" + DEFAULT_WAIT_INTERVAL;
            }
            resp.addHeader(AUTH_WAIT, waitInterval);
        }
    }

    private static void test(String user, String pass, String serverIp) {
        String url = "http://localhost:7072/service/extension/nginx-lookup";
        
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        
        method.setRequestHeader("Host", "localhost");
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_METHOD, "plain");
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER, user);
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PASS, pass);
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PROTOCOL, "imap");
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_LOGIN_ATTEMPT, "1");
        method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.CLIENT_IP, "127.0.0.1");
        
        if (serverIp != null)
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.SERVER_IP, serverIp);
        
        try {
            int statusCode = client.executeMethod(method);
        
            Header authStatus = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_STATUS);
            Header authServer = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_SERVER);
            Header authPort = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PORT);
            Header authUser = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER);
            Header authWait = method.getResponseHeader(NginxLookupExtension.NginxLookupHandler.AUTH_WAIT);
            
            System.out.println("===== user:" + user + " pass: " + pass + " serverIp:" + serverIp);
            
            System.out.println(NginxLookupExtension.NginxLookupHandler.AUTH_STATUS + ": " + ((authStatus==null)?"(null)":authStatus.getValue()));
            System.out.println(NginxLookupExtension.NginxLookupHandler.AUTH_SERVER + ": " + ((authServer==null)?"(null)":authServer.getValue()));
            System.out.println(NginxLookupExtension.NginxLookupHandler.AUTH_PORT + ": " + ((authPort==null)?"(null)":authPort.getValue()));
            System.out.println(NginxLookupExtension.NginxLookupHandler.AUTH_USER + ": " + ((authUser==null)?"(null)":authUser.getValue()));
            System.out.println(NginxLookupExtension.NginxLookupHandler.AUTH_WAIT + ": " + ((authWait==null)?"(null)":authWait.getValue()));
            System.out.println();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String args[]) {
        test("user1@phoebe.local", "test123", null);
        test("imapappendthunderbird1190418967@qa07.liquidsys.com/kk", "test123", null);
        test("user1", "test123", null);
        test("user2", "test123", "127.0.0.1");
        test("user3", "test123", "127.0.0.2");
    }
}
