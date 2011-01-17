/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.security.openid.consumer;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.servlet.ZimbraServlet;
import org.openid4java.OpenIDException;
import org.openid4java.consumer.ConsumerException;
import org.openid4java.consumer.ConsumerManager;
import org.openid4java.consumer.VerificationResult;
import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.discovery.Identifier;
import org.openid4java.discovery.UrlIdentifier;
import org.openid4java.message.AuthRequest;
import org.openid4java.message.ParameterList;
import org.openid4java.util.HttpClientFactory;
import org.openid4java.util.ProxyProperties;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

/**
 * OpenID Consumer HTTP Handler
 */
public class OpenIDConsumerHandler extends ExtensionHttpHandler {

    private static final String COOKIE_ZM_OPENID_DISCOVERY_INFO = "ZM_OPENID_DISCOVERY_INFO";

    private ConsumerManager manager;

    @Override
    public void init(ZimbraExtension ext) throws ServiceException {
        super.init(ext);
        configureHttpProxy();
        try {
            manager = new ConsumerManager();
            // don't use associations, use stateless mode, to allow the extension to work
            // in a multi-node setup
            manager.setMaxAssocAttempts(0);
        } catch (ConsumerException e) {
            ZimbraLog.extensions.error("OpenID error code %s", e.getErrorCode());
            throw ServiceException.FAILURE("Error in initializing OpenID ConsumerManager", e);
        }
    }

    private static void configureHttpProxy() throws ServiceException {
        String url = Provisioning.getInstance().getLocalServer().getAttr(Provisioning.A_zimbraHttpProxyURL, null);
        if (url == null) return;

        ProxyProperties proxyProps = new ProxyProperties();
        URI sProxyUri;
        try {
            sProxyUri = new URI(url);
        } catch (URISyntaxException e) {
            throw ServiceException.FAILURE("invalid zimbraHttpProxyURL", e);
        }
        proxyProps.setProxyHostName(sProxyUri.getHost());
        proxyProps.setProxyPort(sProxyUri.getPort());
        String userInfo = sProxyUri.getUserInfo();
        if (userInfo != null) {
            int i = userInfo.indexOf(':');
            if (i != -1) {
                proxyProps.setUserName(userInfo.substring(0, i));
                proxyProps.setPassword(userInfo.substring(i + 1));
            }
        }
        HttpClientFactory.setProxyProperties(proxyProps);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        doPost(req, resp);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if ("true".equals(req.getParameter("is_return"))) {
            processReturn(req, resp);
        } else {
            String userSuppliedId = req.getParameter("openid_identifier");
            if (userSuppliedId == null)
                throw new ServletException("Invalid request");
            else
                authRequest(userSuppliedId, req, resp);
        }
    }

    private void processReturn(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Identifier identifier = verifyResponse(req);
        ZimbraLog.extensions.debug("claimed identifier: %s", identifier);
        if (identifier == null) {
            throw new ServletException("Authentication failed");
        } else {
            String openId = identifier.getIdentifier();
            try {
                Provisioning prov = Provisioning.getInstance();
                AuthToken authToken = getZimbraAuthToken(req);
                if (authToken == null) {
                    // lookup the account corresponding to the open-id
                    Account acct = prov.getAccountByForeignPrincipal(openId);
                    if (acct == null) {
                        throw new ServletException("No user account found corresponding to OpenID " + openId);
                    }

                    // add a zimbra cookie to the response
                    authToken = AuthProvider.getAuthToken(acct);
                    authToken.encode(resp, false, req.getScheme().equals("https"));

                    // redirect to the correct URL
                    Server server = acct.getServer();
                    if (server == null) {
                        throw new ServletException("Server not found corresponding to account " + acct.getName());
                    }
                    resp.sendRedirect(ZimbraServlet.getServiceUrl(server,
                                                                  prov.getDomain(acct),
                                                                  server.getAttr(Provisioning.A_zimbraMailURL)));
                } else {
                    // user already has a valid login session => this request is for
                    // "linking" open-id with the user account
                    Account acct = prov.getAccountById(authToken.getAccountId());
                    acct.addForeignPrincipal(openId);
                    resp.getOutputStream().print("Success");
                }
            } catch (ServiceException e) {
                ZimbraLog.extensions.warn("Unexpected error after having verified OpenID authentication response", e);
                throw new ServletException(e.getMessage());
            }
        }
    }

    private void authRequest(String userSuppliedId, HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        try {
            // configure the return_to URL where your application will receive
            // the authentication responses from the OpenID provider
            String returnToUrl = req.getRequestURL().toString() + "?is_return=true";

            // perform discovery on the user-supplied identifier
            List discoveries = manager.discover(userSuppliedId);

            // attempt to associate with the OpenID provider
            // and retrieve one service endpoint for authentication
            DiscoveryInformation discovered = manager.associate(discoveries);
            if (discovered == null) {
                throw new ServletException("No OP endpoints discovered");
            }

            // store the discovery information in a cookie
            Cookie cookie = new Cookie(COOKIE_ZM_OPENID_DISCOVERY_INFO, serialize(discovered));
            cookie.setPath("/");
            resp.addCookie(cookie);

            // obtain a AuthRequest message to be sent to the OpenID provider
            AuthRequest authReq = manager.authenticate(discovered, returnToUrl);

            if (!discovered.isVersion2()) {
                // Option 1: GET HTTP-redirect to the OpenID Provider endpoint
                // The only method supported in OpenID 1.x
                // redirect-URL usually limited ~2048 bytes
                resp.sendRedirect(authReq.getDestinationUrl(true));
            } else {
                // Option 2: HTML FORM Redirection (Allows payloads >2048 bytes)
                req.setAttribute("message", authReq);
                HttpServlet servlet = ZimbraServlet.getServlet("ExtensionDispatcherServlet");
                servlet.getServletContext().getContext("/zimbra").
                        getRequestDispatcher("/public/formredirection.jsp").forward(req, resp);
            }
        } catch (OpenIDException e) {
            ZimbraLog.extensions.debug("OpenID error code %s", e.getErrorCode(), e);
            throw new ServletException(e.getMessage());
        }
    }

    /**
     * Returns a valid auth token if the request contains one.
     *
     * @param req
     * @return
     */
    private static AuthToken getZimbraAuthToken(HttpServletRequest req) {
        String encodedToken = getCookieValue(req, ZimbraServlet.COOKIE_ZM_AUTH_TOKEN);
        if (encodedToken == null)
            return null;
        AuthToken authToken;
        try {
            authToken = AuthProvider.getAuthToken(encodedToken);
        } catch (AuthTokenException e) {
            // invalid token, no problem
            return null;
        }
        return authToken.isExpired() ? null : authToken;
    }

    private static String getCookieValue(HttpServletRequest req, String cookieName) {
        Cookie cookies[] =  req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName))
                    return cookie.getValue();
            }
        }
        return null;
    }

    private Identifier verifyResponse(HttpServletRequest req) throws ServletException {
        try {
            // extract the parameters from the authentication response
            // (which comes in as a HTTP request from the OpenID provider)
            ParameterList response = new ParameterList(req.getParameterMap());

            // retrieve the previously stored discovery information
            String serializedDiscInfo = getCookieValue(req, COOKIE_ZM_OPENID_DISCOVERY_INFO);
            if (serializedDiscInfo == null) {
                throw new ServletException("Request missing discovery information");
            }
            DiscoveryInformation discovered = deserializeDiscoveryInfo(serializedDiscInfo);

            // extract the receiving URL from the HTTP request
            StringBuffer receivingURL = req.getRequestURL();
            String queryString = req.getQueryString();
            if (queryString != null && queryString.length() > 0)
                receivingURL.append("?").append(req.getQueryString());

            // verify the response
            VerificationResult verification = manager.verify(receivingURL.toString(), response, discovered);

            // examine the verification result and extract the verified identifier
            return verification.getVerifiedId();
        } catch (OpenIDException e) {
            ZimbraLog.extensions.info("OpenID error code %s", e.getErrorCode(), e);
            throw new ServletException(e.getMessage());
        } catch (ServiceException e) {
            ZimbraLog.extensions.warn("Unexpected error", e);
            throw new ServletException(e.getMessage());
        }
    }

    @Override
    public String getPath() {
        return "/openid/consumer";
    }

    private static String serialize(DiscoveryInformation discInfo) {
        return new StringBuilder().
                append(discInfo.getOPEndpoint()).append(",").
                append(discInfo.getVersion()).append(",").
                append(discInfo.getClaimedIdentifier() == null ? " " : discInfo.getClaimedIdentifier()).append(",").
                append(discInfo.getDelegateIdentifier() == null ? " " : discInfo.getDelegateIdentifier()).append(",").
                toString();
    }

    private static DiscoveryInformation deserializeDiscoveryInfo(String serializedObj) throws ServiceException {
        String[] parts = serializedObj.split(",");
        if (parts.length < 4)
            throw ServiceException.FAILURE("Invalid serialized value for DiscoveryInformation", null);
        try {
            return new DiscoveryInformation(new URL(parts[0]),
                                            parts[2].trim().isEmpty() ? null : new UrlIdentifier(parts[2]),
                                            parts[3].trim().isEmpty() ? null : parts[3],
                                            parts[1]);
        } catch (Exception e) {
            throw ServiceException.FAILURE("Error in instantiating DiscoveryInformation", e);
        }
    }
}
