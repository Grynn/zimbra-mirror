/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.cs.offline.yab;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineLC;

/**
 * Implementation of Yahoo "Raw Auth" aka "Token Login v2"
 * See http://twiki.corp.yahoo.com/view/Membership/OpenTokenLogin
 */
public class RawAuth {
    private final String appId;
    private final String user;
    private String cookie;
    private String wssId;
    private long expiration;

    private static final String BASE_URI = OfflineLC.zdesktop_yauth_baseuri.value();
    private static final String GET_AUTH_TOKEN = BASE_URI + "/get_auth_token";
    private static final String GET_AUTH = BASE_URI + "/get_auth";

    private static final String LOGIN = "login";
    private static final String PASSWD = "passwd";
    private static final String APPID = "appid";
    private static final String TOKEN = "token";

    private static final String AUTH_TOKEN = "AuthToken";
    private static final String COOKIE = "Cookie";
    private static final String WSSID = "WSSID";
    private static final String EXPIRATION = "Expiration";
    private static final String ERROR = "Error";
    private static final String ERROR_DESCRIPTION = "ErrorDescription";

    public static RawAuth authenticate(String appId, String user, String pass)
            throws ServiceException {
        RawAuth auth = new RawAuth(appId, user);
        auth.authenticate(pass);
        return auth;
    }

    private RawAuth(String appId, String user) {
        this.appId = appId;
        this.user = user;
    }

    public String getAppId() {
        return appId;
    }

    public String getUser() {
        return user;
    }
    
    public String getCookie() {
        return cookie;
    }
    
    public String getWSSID() {
        return wssId;
    }

    public long getExpiration() {
        return expiration;
    }

    public NameValuePair[] getParams() {
        return new NameValuePair[] {
            new NameValuePair(APPID, appId), new NameValuePair(WSSID, wssId)
        };
    }

    public Header[] getHeaders() {
        return new Header[] { new Header(COOKIE, cookie) };
    }
    
    private void authenticate(String pass) throws ServiceException {
        // Get auth token
        NameValuePair nvpAppId = new NameValuePair(APPID, appId);
        Response res = doGet(GET_AUTH_TOKEN, nvpAppId,
            new NameValuePair(LOGIN, user), new NameValuePair(PASSWD, pass));
        String token = res.getRequiredValue(AUTH_TOKEN);
        // Get cookie
        res = doGet(GET_AUTH, nvpAppId, new NameValuePair(TOKEN, token));
        cookie = res.getRequiredValue(COOKIE);
        wssId = res.getRequiredValue(WSSID);
        String s = res.getRequiredValue(EXPIRATION);
        try {
            expiration = System.currentTimeMillis() + Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw badResponse(GET_AUTH_TOKEN,
                "Invalid integer value for field '" + EXPIRATION + "'");
        }
    }

    private Response doGet(String uri, NameValuePair... params) throws ServiceException {
        GetMethod get = new GetMethod(uri);
        get.setQueryString(params);
        try {
            // XXX Should we share a single HttpClient() instance?
            int code = new HttpClient().executeMethod(get);
            Response res = new Response(uri, get);
            switch (code) {
            case 200:
                return res;
            case 403:
                throw OfflineServiceException.AUTH_FAILED(user,
                    "Authentication failed: " + res.getErrorMessage());
            default:
                throw badResponse(uri, "Unexpected response code: " + code);
            }
        } catch (IOException e) {
            throw ServiceException.FAILURE(
                "I/O error in '" + getRequestType(uri) + "' request", e);
        }
    }

    private static class Response {
        final String uri;
        final List<String> names = new ArrayList<String>(5);
        final List<String> values = new ArrayList<String>(5);

        Response(String uri, HttpMethod method) throws IOException {
            this.uri = uri;
            String body = method.getResponseBodyAsString();
            for (String line : body.split("\\r?\\n")) {
                int i = line.indexOf('=');
                if (i != -1) {
                    names.add(line.substring(0, i));
                    values.add(line.substring(i + 1));
                }
            }
        }

        String getRequiredValue(String name) throws ServiceException {
            String value = getValue(name);
            if (value == null) {
                throw badResponse(uri, "Missing required '" + name + "' field");
            }
            return value;
        }
        
        String getValue(String name) {
            for (int i = 0; i < names.size(); i++) {
                if (names.get(i).equalsIgnoreCase(name)) {
                    return values.get(i);
                }
            }
            return null;
        }

        String getErrorMessage() {
            String error = getValue(ERROR);
            String description = getValue(ERROR_DESCRIPTION);
            if (error != null) {
                return description != null ? error + ": " + description : error;
            }
            return description != null ? description : "Unknown error";
        }
    }

    private static ServiceException badResponse(String uri, String msg) {
        return OfflineServiceException.UNEXPECTED(
            "Bad '" + getRequestType(uri) + "' response: " + msg);
    }

    private static String getRequestType(String uri) {
        int i = uri.lastIndexOf('/');
        return i != -1 ? uri.substring(i) : "";
    }

    public static void main(String[] args) throws Exception {
        RawAuth yauth = authenticate(OfflineLC.zdesktop_yauth_appid.value(), "jjztest", "test1234");
        System.out.println("cookie=" + yauth.cookie);
        System.out.println("wssid=" + yauth.wssId);
        System.out.println("expiration=" + yauth.expiration);
    }
}
