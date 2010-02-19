/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yab;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.util.yauth.Auth;
import com.zimbra.cs.util.yauth.AuthenticationException;
import com.zimbra.cs.util.yauth.Authenticator;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.w3c.dom.Document;

public abstract class Request extends Entity {
    protected final Session session;
    protected final List<String> params;

    protected Request(Session session) {
        this.session = session;
        params = new ArrayList<String>();
    }
    
    protected boolean isPOST() {
        return false; // Default is GET request
    }

    protected abstract String getAction();
    
    public void addParam(String name, String value) {
        params.add(name + "=" + encode(value));
    }
    
    public void addParams(String... params) {
        for (String param : params) {
            int i = param.indexOf('=');
            if (i == -1) {
                throw new IllegalArgumentException(
                    "Invalid parameter specification: " + param);
            }
            addParam(param.substring(0, i), param.substring(i + 1));
        }
    }

    @SuppressWarnings("deprecation")
    public Response send() throws AuthenticationException, YabException, IOException {
        Authenticator auth = session.getAuthenticator();
        try {
            return sendRequest(auth.authenticate());
        } catch (HttpException e) {
            int code = e.getReasonCode();
            if (code == 401 || code == 403) {
                // Cached cookie expired or was invalid. Invalidate cookie and try again.
                Yab.debug("Invalidating possibly expired cookie and retrying auth");
                auth.invalidate();
                return sendRequest(auth.authenticate());
            } else {
                throw e;
            }
        }
    }

    @SuppressWarnings("deprecation")
    private Response sendRequest(Auth auth) throws YabException, IOException {
        HttpMethod method = getHttpMethod(auth);
        if (Yab.isDebug()) {
            if (isPOST()) {
                Yab.debug("Sending request: POST %s", method.getURI());
                if (session.isTrace()) {
                    Yab.debug("Request body:\n%s", Xml.toString(toXml()));
                }
            } else {
                Yab.debug("Sending request: GET %s", method.getURI());
            }
        }
        int code = HttpClientUtil.executeMethod(session.getHttpClient(), method);
        InputStream is = method.getResponseBodyAsStream();
        if (code != 200) {
            if (code != 401 && code != 403) {
                ErrorResult error = getErrorResult(method);
                if (error != null) {
                    throw new YabException(error);
                }
            }
            HttpException e = new HttpException(
                "HTTP request failed: " + code + ": " + method.getStatusText());
            e.setReason(method.getStatusText());
            e.setReasonCode(code);
            throw e;
        }
        Document doc = session.parseDocument(is);
        if (Yab.isDebug() && session.isTrace()) {
            Yab.debug("Response body:\n%s", Xml.toString(doc));
        }
        return parseResponse(doc);
    }

    private ErrorResult getErrorResult(HttpMethod method) throws IOException {
        InputStream is = method.getResponseBodyAsStream();
        try {
            Document doc = session.parseDocument(is);
            if (Yab.isDebug() && session.isTrace()) {
                Yab.debug("Response body:\n%s", Xml.toString(doc));
            }
            return ErrorResult.fromXml(doc.getDocumentElement());
        } catch (IOException e) {
            return null;
        }
    }

    protected abstract Response parseResponse(Document doc);

    private HttpMethod getHttpMethod(Auth auth) {
        String uri = Yab.BASE_URI + '/' + getAction();
        HttpMethod method = isPOST() ? new PostMethod(uri) : new GetMethod(uri);
        method.setQueryString(getQueryString(auth));
        method.addRequestHeader("Cookie", auth.getCookie());
        method.addRequestHeader("Content-Type", "application/" + session.getFormat());
        if (method instanceof PostMethod) {
            ((PostMethod) method).setRequestEntity(session.getRequestEntity(toXml()));
        }
        return method;
    }

    private String getQueryString(Auth auth) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(encode(auth.getAppId()));
        sb.append("&WSSID=").append(auth.getWSSID());
        sb.append("&format=").append(session.getFormat());
        for (String param : params) {
            sb.append('&').append(param);
        }
        return sb.toString();
    }

    private String encode(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new InternalError("UTF-8 encoding not found");
        }
    }
    
    private Document toXml() {
        Document doc = session.createDocument();
        doc.appendChild(toXml(doc));
        return doc;
    }
}
