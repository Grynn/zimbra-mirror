/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yc.oauth;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.zimbra.common.httpclient.HttpClientUtil;
import com.zimbra.common.util.ZimbraHttpConnectionManager;
import com.zimbra.cs.offline.OfflineLog;

public abstract class OAuthRequest {

    protected Map<String, String> params = new HashMap<String, String>();
    private OAuthToken token;
    protected static HttpClient httpClient = ZimbraHttpConnectionManager.getInternalHttpConnMgr().newHttpClient();

    public OAuthRequest(OAuthToken token) {
        this.token = token;
    }

    public Map<String, String> getParams() {
        return params;
    }

    protected void addParam(String key, String value) {
        this.params.put(key, value);
    }

    protected void removeParam(String key) {
        this.params.remove(key);
    }

    public OAuthToken getToken() {
        return token;
    }

    protected abstract String getEndpointURL();

    protected abstract String getHttpMethod();

    protected String getVerifier() {
        return "";
    }

    protected void fillParams() throws OAuthException {
        doFillSpecificParams();
        doSign();
    }

    protected abstract void doFillSpecificParams();

    protected abstract String getStep();

    private void doSign() throws OAuthException {
        try {
            String signature = OAuthHelper.getSignature(
                    OAuthHelper.getBaseString(this.params, getEndpointURL(), getHttpMethod(), getStep()),
                    OAuthConstants.OAUTH_CONSUMER_SECRET_VALUE, getToken().getTokenSecret());
            this.addParam(OAuthConstants.OAUTH_SIGNATURE, signature);
        } catch (com.zimbra.cs.offline.util.yc.oauth.OAuthException e) {
            throw new OAuthException("Generate signature error at " + getStep(), "", false, null, null);
        }
    }

    public String send() throws OAuthException {

        try {
            fillParams();

            HttpMethod httpMethod = "POST".equals(getHttpMethod()) ? new PostMethod(getEndpointURL()) : new GetMethod(
                    getEndpointURL());
            OfflineLog.yab.info("[Yahoo OAuth] sending request [%s], token {%s}", getEndpointURL(), getToken());
            String header = OAuthHelper.extractHeader(this.params);
            httpMethod.setRequestHeader("Authorization", header);
            int code = HttpClientUtil.executeMethod(httpClient, httpMethod);
            if (code != 200) {
                throw OAuthException.handle(code, getStep());
            }
            InputStream stream = httpMethod.getResponseBodyAsStream();
            String resp = OAuthHelper.getStreamContents(stream);
            return resp;
        } catch (Exception e) {
            OfflineLog.yab.error("sending oauth request error", e);
            StringBuilder msg = new StringBuilder();
            msg.append("error when sending req at ").append(getStep());
            if (e instanceof OAuthException) {
                msg.append(" Internal reason: " + e.getMessage());
            }
            throw new OAuthException(msg.toString(), "", false, e, null);
        }
    }
}
