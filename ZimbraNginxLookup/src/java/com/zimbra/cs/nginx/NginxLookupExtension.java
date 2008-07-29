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
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Log;
import com.zimbra.common.util.LogFactory;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AuthContext;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.auth.AuthMechanism;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.account.ldap.ZimbraLdapContext;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning.DomainBy;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.AccessManager;
import com.zimbra.cs.extension.ExtensionDispatcherServlet;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.service.AuthProvider;

public class NginxLookupExtension implements ZimbraExtension {

    public static final String NAME = "nginx-lookup";
    
    private static NginxLookupCache<DomainInfo> sDomainCache =
        new NginxLookupCache<DomainInfo>(
                LC.ldap_cache_reverseproxylookup_domain_maxsize.intValue(),
                LC.ldap_cache_reverseproxylookup_domain_maxage.intValue() * Constants.MILLIS_PER_MINUTE); 

    private static NginxLookupCache<ServerInfo> sServerCache =
        new NginxLookupCache<ServerInfo>(
                LC.ldap_cache_reverseproxylookup_server_maxsize.intValue(),
                LC.ldap_cache_reverseproxylookup_server_maxage.intValue() * Constants.MILLIS_PER_MINUTE); 

    
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
        
        public NginxLookupException(String msg, Throwable cause) {
            super(msg, cause);
        }
    }
    
    private static class NginxLookupRequest {
        String user;
        String cuser;
        String pass;
        String proto;
        String authMethod;
        String clientIp;
        String serverIp;
        String serverHost;
        String principal;
        int loginAttempt;
        boolean isZimbraAdmin;
        String adminUser;
        String adminPass;
        HttpServletRequest  httpReq;
        HttpServletResponse httpResp;
    }
    
    public static class NginxLookupHandler extends ExtensionHttpHandler {
        /* req headers */
        public static final String AUTH_METHOD        = "Auth-Method";
        public static final String AUTH_USER          = "Auth-User";
        public static final String AUTH_PASS          = "Auth-Pass";
        public static final String AUTH_PROTOCOL      = "Auth-Protocol";
        public static final String AUTH_ZIMBRA_ADMIN  = "Auth-Zimbra-Admin";
        public static final String AUTH_LOGIN_ATTEMPT = "Auth-Login-Attempt";
        public static final String CLIENT_IP          = "Client-IP";
        public static final String SERVER_IP          = "X-Proxy-IP";
        public static final String SERVER_HOST        = "X-Proxy-Host";
        public static final String AUTH_ID            = "Auth-Id";
        public static final String AUTH_ADMIN_USER    = "Auth-Admin-User";
        public static final String AUTH_ADMIN_PASS    = "Auth-Admin-Pass";
        
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

        /* auth methods */
        public static final String AUTHMETH_PLAIN = "plain";
        public static final String AUTHMETH_OTHER = "other";
        public static final String AUTHMETH_ZIMBRAID = "zimbraId";
        public static final String AUTHMETH_GSSAPI = "gssapi";

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
        private NginxLookupRequest checkRequest(HttpServletRequest httpReq) throws NginxLookupException
        {
            /* Build the request object and extract the various request headers */

            NginxLookupRequest req = new NginxLookupRequest();

            /* NGINX will never pass any suffixes to the lookup servlet
               So no need to look for /tb|/wm|/ni in req.user
             */

            req.user            = httpReq.getHeader(AUTH_USER);             /* User whose route is to be looked up */
            req.pass            = httpReq.getHeader(AUTH_PASS);             /* Password */
            req.proto           = httpReq.getHeader(AUTH_PROTOCOL);         /* Protocol {imap|imaps|pop3|pop3s|http} */
            req.authMethod      = httpReq.getHeader(AUTH_METHOD);           /* Auth Method {passwd|plain|gssapi|other|zimbraId} */
            req.cuser           = httpReq.getHeader(AUTH_ID);               /* (GSSAPI) Authenticating Principal */
            req.adminUser       = httpReq.getHeader(AUTH_ADMIN_USER);       /* auth admin user, required for GSSAPI */
            req.adminPass       = httpReq.getHeader(AUTH_ADMIN_PASS);       /* auth admin password, , required for GSSAPI */
            req.clientIp        = httpReq.getHeader(CLIENT_IP);             /* Upstream Client IP */
            req.serverIp        = httpReq.getHeader(SERVER_IP);             /* Incoming Proxy Interface IP */
            req.serverHost      = httpReq.getHeader(SERVER_HOST);           /* (HTTP) Host header */
            req.loginAttempt    = 1;
            req.isZimbraAdmin   = false;
             

            /* Complain if any required fields are missing */

            if (req.user == null)
                throw new NginxLookupException("missing header field " + AUTH_USER);
            if (req.authMethod == null) 
                throw new NginxLookupException("missing header field " + AUTH_METHOD);
            if (req.proto == null)
                throw new NginxLookupException("missing header field " + AUTH_PROTOCOL);
            if (req.authMethod.equalsIgnoreCase(AUTHMETH_GSSAPI)) {
                if (req.cuser == null)
                    throw new NginxLookupException("(GSSAPI) missing header field " + AUTH_ID);
                
                if (req.adminUser == null)
                    throw new NginxLookupException("(GSSAPI) missing header field " + AUTH_ADMIN_USER);
                
                if (req.adminPass == null)
                    throw new NginxLookupException("(GSSAPI) missing header field " + AUTH_ADMIN_PASS);
                
                if (req.serverIp == null)
                    throw new NginxLookupException("(GSSAPI) missing header field " + SERVER_IP);
            }

            if (req.pass == null)   /* We should not complain on null password */
                req.pass = "";

            String val = httpReq.getHeader(AUTH_LOGIN_ATTEMPT);
            if (val != null) {
                try {
                    req.loginAttempt = Integer.parseInt(val);
                } catch (NumberFormatException e) {
                }
            }

            String isZimbraAdmin = httpReq.getHeader(AUTH_ZIMBRA_ADMIN);
            if (isZimbraAdmin != null) {
                req.isZimbraAdmin = Boolean.parseBoolean (isZimbraAdmin);
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
                if (val == null && required)
                    throw new NginxLookupException("missing attr in search result: "+attr);
            }
            return val;
        }
        
        private String getPortAttribute(NginxLookupRequest req) throws NginxLookupException
        {
            String proto = req.proto;

            if (IMAP.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyImapPortAttribute;
            else if (IMAP_SSL.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyImapSSLPortAttribute;
            else if (POP3.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyPop3PortAttribute;
            else if (POP3_SSL.equalsIgnoreCase(proto))
                return Provisioning.A_zimbraReverseProxyPop3SSLPortAttribute;
            else if (HTTP.equalsIgnoreCase(proto)) {
                if (req.isZimbraAdmin) {
                    return Provisioning.A_zimbraReverseProxyAdminPortAttribute;
                } else {
                    return Provisioning.A_zimbraReverseProxyHttpPortAttribute;
                }
            }
            else
                throw new NginxLookupException("unsupported protocol: "+proto);
        }
        
        private String searchDirectory(ZimbraLdapContext zlc, SearchControls sc, Config config, 
                                       String queryTemplate, String searchBase, String templateKey, String templateVal,
                                       String attr) throws NginxLookupException, NamingException {
            Object result = searchDir(zlc, sc, config, queryTemplate,  searchBase,  templateKey,  templateVal, attr);
            return (String)result;
        }
        
        private Map<String, String> searchDirectory(ZimbraLdapContext zlc, SearchControls sc, Config config, 
                                                    String queryTemplate, String searchBase, String templateKey, String templateVal,
                                                    Map<String, Boolean> attrs) throws NginxLookupException, NamingException {
            Object result = searchDir(zlc, sc, config, queryTemplate,  searchBase,  templateKey,  templateVal, attrs);
            return (Map<String, String>)result;
        }
        
        private Object searchDir(ZimbraLdapContext zlc, SearchControls sc, Config config, 
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
            
            NamingEnumeration ne = zlc.searchDir(base, query, sc);
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
        
        /**
         * verify that the request is from the legitimate nginx admin 
         * @throws NginxLookupException
         */
        private void verifyNginxAdmin(Provisioning prov, Config config, NginxLookupRequest req) throws ServiceException, NginxLookupException {
            Set<String> allowedServerIPs = config.getMultiAttrSet(Provisioning.A_zimbraReverseProxyAdminIPAddress);
            if (!allowedServerIPs.contains(req.serverIp))
                throw new NginxLookupException(SERVER_IP + " " + req.serverIp + " is not allowed");
                
            Account adminAcct = prov.get(AccountBy.name, req.adminUser);
            if (adminAcct == null)
                throw new NginxLookupException("admin account " + req.adminUser + " not found");
                
            // must be global admin
            boolean isAdmin= adminAcct.getBooleanAttr(Provisioning.A_zimbraIsAdminAccount, false);
            if (!isAdmin)
                throw new NginxLookupException("not an admin account");
                    
            Map<String, Object> authCtxt = new HashMap<String, Object>();
            authCtxt.put(AuthContext.AC_ORIGINATING_CLIENT_IP, req.clientIp);
            authCtxt.put(AuthContext.AC_ACCOUNT_NAME_PASSEDIN, req.adminUser);
            AuthMechanism.doZimbraAuth((LdapProvisioning)prov, null, adminAcct, req.adminPass, authCtxt);  
        }
        
        private String genAuthToken(Account authc, Config config, NginxLookupRequest req) throws ServiceException, NginxLookupException {
            Provisioning prov = Provisioning.getInstance();
            verifyNginxAdmin(prov, config, req);
            
            try {
                return AuthProvider.getAuthToken(authc).getEncoded();
            } catch (AuthTokenException e) {
                throw new NginxLookupException("failed to geenrate auth token for " + authc.getName(), e);
            }
        }
        
        private String getDomainNameByServerIp(ZimbraLdapContext zlc, Config config, String serverIp, String unqualifiedName) {
            String domainName = null;
            
            DomainInfo domainInfo = sDomainCache.get(serverIp);
            
            if (domainInfo == null) {
                try {
                    domainName = searchDirectory(
                                            zlc, 
                                            DOMAIN_SC, 
                                            config, 
                                            Provisioning.A_zimbraReverseProxyDomainNameQuery,
                                            Provisioning.A_zimbraReverseProxyDomainNameSearchBase,
                                            "IPADDR",
                                            serverIp,
                                            Provisioning.A_zimbraReverseProxyDomainNameAttribute);
                } catch (NginxLookupException e) {
                    logger.warn("domain not found for user " + unqualifiedName + ".  error: " + e.getMessage());
                } catch (NamingException e) {
                    logger.warn("domain not found for user " + unqualifiedName + ".  error: " + e.getMessage());
                }
                
                if (domainName != null)
                    sDomainCache.put(new DomainInfo(serverIp, domainName));
            } else 
                domainName = domainInfo.getDomainName();
            
            return domainName;
        }
        
        private String getPort(Map<String, String> vals, String lookupAttr, Config config) {
            String port = vals.get(lookupAttr);
            if (port == null) {
                logger.debug("using port from globalConfig");
                String bindPortAttr = config.getAttr(lookupAttr);
                if (bindPortAttr == null)
                    logger.warn("missing config attr: "+lookupAttr);
                else {
                    port = config.getAttr(bindPortAttr);
                    if (port == null)
                        logger.warn("missing config attr: "+bindPortAttr);
                }
            }
            return port;
        }
        
        private String getPortByMailhostAndProto(ZimbraLdapContext zlc, Config config, NginxLookupRequest req, String mailhost) throws NginxLookupException {
            String port = null;
            
            ServerInfo serverInfo = sServerCache.get(mailhost);
            if (serverInfo == null) {
                try {
                    // get all the ports and cache them
                    Map<String, Boolean> attrs = new HashMap<String, Boolean>();
                    attrs.put(Provisioning.A_zimbraReverseProxyHttpPortAttribute, false);
                    attrs.put(Provisioning.A_zimbraReverseProxyAdminPortAttribute, false);
                    attrs.put(Provisioning.A_zimbraReverseProxyPop3PortAttribute, false);
                    attrs.put(Provisioning.A_zimbraReverseProxyPop3SSLPortAttribute, false);
                    attrs.put(Provisioning.A_zimbraReverseProxyImapPortAttribute, false);
                    attrs.put(Provisioning.A_zimbraReverseProxyImapSSLPortAttribute, false);
                    
                    Map<String, String> vals = searchDirectory(zlc, 
                                                               SERVER_SC, 
                                                               config, 
                                                               Provisioning.A_zimbraReverseProxyPortQuery,
                                                               Provisioning.A_zimbraReverseProxyPortSearchBase,
                                                               "MAILHOST",
                                                               mailhost,
                                                               attrs);
                    
                    serverInfo = new ServerInfo(mailhost); 
                    serverInfo.setHttpPort(getPort(vals, Provisioning.A_zimbraReverseProxyHttpPortAttribute, config));
                    serverInfo.setHttpAdminPort(getPort(vals, Provisioning.A_zimbraReverseProxyAdminPortAttribute, config));
                    serverInfo.setPop3Port(getPort(vals, Provisioning.A_zimbraReverseProxyPop3PortAttribute, config));
                    serverInfo.setPop3SSLPort(getPort(vals, Provisioning.A_zimbraReverseProxyPop3SSLPortAttribute, config));
                    serverInfo.setImapPort(getPort(vals, Provisioning.A_zimbraReverseProxyImapPortAttribute, config));
                    serverInfo.setImapSSLPort(getPort(vals, Provisioning.A_zimbraReverseProxyImapSSLPortAttribute, config));
                    
                    sServerCache.put(serverInfo);
                    
                } catch (NamingException e) {
                    throw new NginxLookupException("naming exception: "+e.getMessage());
                }
            } 

            port = serverInfo.getPortForProto(req.proto, req.isZimbraAdmin);
            if (port == null)
                throw new NginxLookupException("missing port for protocol " + req.proto + " on server " + mailhost);
            
            return port;
        }
        
        private String qualifyUserName(ZimbraLdapContext zlc, Config config, NginxLookupRequest req, Provisioning prov, String unqualifiedName) {
            String domainName = null;
            
            if (HTTP.equalsIgnoreCase(req.proto))
            {
                /* For HTTP, we need to qualify user based on virtual-host header */
                if (req.serverHost != null) {
                    logger.info("looking up domain by virtualhost name");
                    Domain d = null;
                    try {
                        d = prov.get(DomainBy.virtualHostname, req.serverHost);
                    } catch (ServiceException e) {
                    }
                    if (d != null) {
                        domainName = d.getName();
                        logger.info("found domain:" + domainName + " for virtualhost:" + req.serverHost);
                    }
                }
            }
            else
            {
                /* For mail, we need to qualify user based on server-ip header */
                if (req.serverIp != null) {
                    domainName = getDomainNameByServerIp(zlc,config, req.serverIp, unqualifiedName);
                }
            }
                            
            if (domainName == null) {
                domainName = config.getAttr(Provisioning.A_zimbraDefaultDomainName);
                logger.debug("domain not found for user " + unqualifiedName + ", using default domain: " + (domainName==null?"null":domainName));
            }
                
            String qualifiedName = unqualifiedName;
            if (domainName != null) {
                qualifiedName = unqualifiedName + "@" + domainName;
                logger.debug(AUTH_USER + " " + unqualifiedName + " is replaced by " + qualifiedName + " for mailhost lookup");
            } else {
                logger.warn("domain not found for user " + unqualifiedName);
            }
            
            return qualifiedName;
        }

        /** Qualifies the user-name, if necessary, by suffixing "@domain"
            The domain to be suffixed is the domain object whose zimbraVirtualIPAddress matches the
            IP address specified by req.serverIP (X-Proxy-IP request header)
            @return Fully qualified user name (or user-id), else the original user name
         */
        private String getQualifiedUsername(ZimbraLdapContext zlc, Config config, NginxLookupRequest req) throws ServiceException, NginxLookupException
        {
            String aUser, cUser, qUser;

            aUser = req.user;               /* AUTHZ (whose route is being discovered) */
            cUser = req.cuser;              /* AUTHC (if GSSAPI) */
            qUser = aUser;                  /* Qualified AUTHZ (defaults to AUTHZ) */

            Provisioning prov = Provisioning.getInstance();
            Account gssapiAuthC = null;

            if (req.authMethod.equalsIgnoreCase(AUTHMETH_ZIMBRAID))
            {
                /* For auth-token based routing, aUser contains the zimbraId of the user
                   No qualification is performed in this case, because the ldap query
                   can handle route lookup by ID also
                 */
                return qUser;
            }
            else if (req.authMethod.equalsIgnoreCase(AUTHMETH_GSSAPI))
            {
                /* For GSSAPI, cUser specifies the authenticating kerberos principal
                   When no separate authorization ID was specified, then in this case, 
                   aUser is equal to cUser, and therefore, by transition, aUser is also
                   interpreted as a kerberos principal

                   If a separate authorization ID has been specified, then in this case, 
                   the authorization ID is treated in its own right as a fully qualified
                   or a partially qualified user name, and must be qualified according to 
                   the regular qualification logic (See bug 24792)

                 */

                boolean authzIsPrincipal;

                authzIsPrincipal = aUser.equalsIgnoreCase(cUser);

                gssapiAuthC = prov.get(AccountBy.krb5Principal,cUser);
                if (gssapiAuthC == null) {
                    throw new NginxLookupException("No account was found which has kerberos principal " + cUser);
                }

                /* overwrite request::cuser (authenticating identity for gssapi) */
                req.cuser = gssapiAuthC.getAttr(Provisioning.A_zimbraMailDeliveryAddress);

                if (authzIsPrincipal) {
                    qUser = gssapiAuthC.getAttr(Provisioning.A_zimbraMailDeliveryAddress);
                }

            }

            /* At this point, qUser is may not be fully qualified, and so the domain must be looked up
               depending upon which protocol is being used

               For HTTP, the host header must be used in order to lookup the domain by zimbraVirtualHostname
               For MAIL, the proxy ip must be used in order to lookup the domain by zimbraVirtualIPAddress
            */
            if (qUser.indexOf('@') == -1)
                qUser = qualifyUserName(zlc, config, req, prov, aUser);
            
            if (req.authMethod.equalsIgnoreCase(AUTHMETH_GSSAPI)) {
                /* Now, qUser is as qualified as it is ever going to get.
                   Perform access checks to see whether req.cuser is allowed to act as qUser.
                 */
                Account gssapiAuthZ = prov.get(AccountBy.name, qUser);
                if (gssapiAuthZ == null)
                    throw new NginxLookupException("account not found: " + qUser);
                
                if (!gssapiAuthC.getId().equals(gssapiAuthZ.getId()) && 
                    !AccessManager.getInstance().canAccessAccount(gssapiAuthC, gssapiAuthZ, true))
                    throw new NginxLookupException("authorization failed for " + gssapiAuthZ.getName() + " (authenticated user " + gssapiAuthC.getName() + " has insufficient rights)");
                    
                /* finally, all is well, send back an auth-token as a password
                   req.pass = "0_7e6c9784e1e3d27c311282220c2bc61e4db1bd48_69643d33363a66653664656239372d303162362d346463362d623662312d3265393634333238383931623b6578703d31333a313231353335393937333231333b747970653d363a7a696d6272613b";
                 */
                req.pass = genAuthToken(gssapiAuthC, config, req);
            }
            return qUser;
        }
        
        private void search(NginxLookupRequest req) throws NginxLookupException {
            ZimbraLdapContext zlc = null;
            try {
                zlc = new ZimbraLdapContext();
                Config config = Provisioning.getInstance().getConfig();
                String authUser = getQualifiedUsername(zlc, config, req);
                
                Map<String, Boolean> attrs = new HashMap<String, Boolean>();
                attrs.put(Provisioning.A_zimbraReverseProxyMailHostAttribute, true);
                attrs.put(Provisioning.A_zimbraReverseProxyUserNameAttribute, false);
                Map<String, String> vals = searchDirectory(zlc, 
                                                           USER_SC, 
                                                           config, 
                                                           Provisioning.A_zimbraReverseProxyMailHostQuery,
                                                           Provisioning.A_zimbraReverseProxyMailHostSearchBase,
                                                           "USER",
                                                           authUser,
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
                port = getPortByMailhostAndProto(zlc, config, req, mailhost);
                
                logger.debug("port="+port);
                sendResult(req, addr, port, authUser);
            } catch (NginxLookupException e) {
                throw e;
            } catch (ServiceException e) {
                throw new NginxLookupException("service exception: "+e.getMessage());
            } catch (NamingException e) {
                throw new NginxLookupException("naming exception: "+e.getMessage());
            } catch (UnknownHostException e) {
                throw new NginxLookupException("naming exception: "+e.getMessage());
            } finally {
                ZimbraLdapContext.closeContext(zlc);
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
                logger.debug("rewrite " + AUTH_USER + " to: " + authUser);
                resp.addHeader(AUTH_USER, authUser);
            }

            /* For GSSAPI, we also need to send back the overriden authenticating ID and the auth-token as password */

            if (req.authMethod.equalsIgnoreCase(AUTHMETH_GSSAPI)) {
                resp.addHeader(AUTH_ID, req.cuser);
                resp.addHeader(AUTH_PASS, req.pass);
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
    
    private static void doTest(String h_AUTH_METHOD,
                               String h_AUTH_USER,
                               String h_AUTH_PASS,
                               String h_AUTH_PROTOCOL,
                               String h_AUTH_LOGIN_ATTEMPT,
                               String h_CLIENT_IP,
                               String h_SERVER_IP,
                               String h_SERVER_HOST,
                               String h_AUTH_ID,
                               String h_AUTH_ADMIN_USER,
                               String h_AUTH_ADMIN_PASS,
                               boolean expectedOK) {
        String url = "http://localhost:7072/service/extension/nginx-lookup";
        
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(url);
        
        method.setRequestHeader("Host", "localhost");
        if (h_AUTH_METHOD != null)
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_METHOD, h_AUTH_METHOD);
        if (h_AUTH_USER != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_USER, h_AUTH_USER);
        if (h_AUTH_PASS != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PASS, h_AUTH_PASS);
        if (h_AUTH_PROTOCOL != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_PROTOCOL, h_AUTH_PROTOCOL);
        if (h_AUTH_LOGIN_ATTEMPT != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_LOGIN_ATTEMPT, h_AUTH_LOGIN_ATTEMPT);
        if (h_CLIENT_IP != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.CLIENT_IP, h_CLIENT_IP);
        if (h_SERVER_IP != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.SERVER_IP, h_SERVER_IP);
        if (h_SERVER_HOST != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.SERVER_HOST, h_SERVER_HOST);
        if (h_AUTH_ID != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_ID, h_AUTH_ID);
        if (h_AUTH_ADMIN_USER != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_ADMIN_USER, h_AUTH_ADMIN_USER);
        if (h_AUTH_ADMIN_PASS != null) 
            method.setRequestHeader(NginxLookupExtension.NginxLookupHandler.AUTH_ADMIN_PASS, h_AUTH_ADMIN_PASS);
        
       
        System.out.println("Request headers:");
        for (Header header : method.getRequestHeaders()) {
            System.out.print("    " + header.toString());
        }
        System.out.println();
        
        boolean isOK = false;
        try {
            int statusCode = client.executeMethod(method);
            
            System.out.println("Response headers:");
            for (Header header : method.getResponseHeaders()) {
                if (header.getName().equals(NginxLookupExtension.NginxLookupHandler.AUTH_STATUS) && 
                    "OK".equals(header.getValue()))
                    isOK = true;
                    
                System.out.print("    " + header.toString());
                
                if (header.getName().equals(NginxLookupExtension.NginxLookupHandler.AUTH_PASS)) {
                    try {
                        AuthToken at = AuthToken.getAuthToken(header.getValue());
                        String acctId = at.getAccountId();
                        String acctName = Provisioning.getInstance().get(AccountBy.id, acctId).getName();
                        System.out.println("        (Authed account: id=" + at.getAccountId() + ", name=" + acctName);
                    } catch (ServiceException e) {
                        System.out.println("        (Not a valid auth token)");
                    } catch (AuthTokenException e)  {
                        System.out.println("        (Not a valid auth token)");
                    }
                }
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.out.println();
        if (expectedOK == isOK)
            System.out.println("succeeded");
        else
            System.out.println("failed");
        
        System.out.println("\n=========================================\n");
    }
    
    public static void main(String args[]) {
        /*
        test("user1@phoebe.local", "test123", null);
        test("imapappendthunderbird1190418967@qa07.liquidsys.com/kk", "test123", null);
        test("user1", "test123", null);
        test("user2", "test123", "127.0.0.1");
        test("user3", "test123", "127.0.0.2");
        */
        
        /*
         * zmprov md phoebe.mac zimbraAuthKerberos5Realm ZIMBRA.COM zimbraVirtualIPAddress 13.12.11.10
         * zmprov ca nginx-admin@phoebe.mac test123 zimbraIsAdminAccount TRUE
         *
         * zmprov mcf zimbraReverseProxyAdminIPAddress 13.12.11.10 zimbraReverseProxyAdminAccount nginx-admin@phoebe.mac zimbraReverseProxyAdminAccountPassword test123
         * 
         */ 
        
        //     AUTH_METHOD  AUTH_USER                  AUTH_PASS  AUTH_PROTOCOL  AUTH_LOGIN_ATTEMPT  CLIENT_IP      SERVER_IP      SERVER_HOST  AUTH_ID                      AUTH_ADMIN_USER            AUTH_ADMIN_PASS
    //  doTest("plain",     "user1",                  "test123",  "imap",        "1",                "10.11.12.13", "127.0.0.1",   null,        null,                        null,                      null,            true);
        /*
        doTest("gssapi",    "user1",                   null,      "imap",        "1",                "10.11.12.13", "13.12.11.10", null,        "user1@ZIMBRA.COM",          "nginx-admin@phoebe.mac",  "test123",       true);
        doTest("gssapi",    "user1@phoebe.mac",        null,      "imap",        "1",                "10.11.12.13", "13.12.11.10", null,        "user1@ZIMBRA.COM",          "nginx-admin@phoebe.mac",  "test123",       true);
        doTest("gssapi",    "user1@ZIMBRA.COM",        null,      "imap",        "1",                "10.11.12.13", "13.12.11.10", null,        "user1@ZIMBRA.COM",          "nginx-admin@phoebe.mac",  "test123",       true);
        doTest("gssapi",    "user2",                   null,      "imap",        "1",                "10.11.12.13", "13.12.11.10", null,        "user1@ZIMBRA.COM",          "nginx-admin@phoebe.mac",  "test123",       false);
        doTest("gssapi",    "family-child1-visible",   null,      "imap",        "1",                "10.11.12.13", "13.12.11.10", null,        "family-parent@ZIMBRA.COM",  "nginx-admin@phoebe.mac",  "test123",       true);
        */
        
        doTest("plain",     "user1",                   null,      "imap",        "1",                "10.11.12.13", "13.12.11.10", null,        null,                        null,                      null,            true);
        doTest("plain",     "user1",                   null,      "imapssl",     "1",                "10.11.12.13", "13.12.11.10", null,        null,                        null,                      null,            true);
        doTest("plain",     "user1",                   null,      "pop3",        "1",                "10.11.12.13", "13.12.11.10", null,        null,                        null,                      null,            true);
        doTest("plain",     "user1",                   null,      "pop3ssl",     "1",                "10.11.12.13", "13.12.11.10", null,        null,                        null,                      null,            true);

        
   }
}
