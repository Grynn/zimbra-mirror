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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.zimbra.cs.offline.OfflineLog;

public abstract class OAuthRequest {

    protected Map<String, String> params = new HashMap<String, String>();
    private OAuthToken token;

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

            HttpURLConnection connection = (HttpURLConnection) new URL(getEndpointURL()).openConnection();
            connection.setRequestMethod(getHttpMethod());
            String header = OAuthHelper.extractHeader(this.params);
            connection.setRequestProperty("Authorization", header);

            connection.connect();
            int code = connection.getResponseCode();
            if (code != 200) {
                throw OAuthException.handle(code, getStep());
            }
            Map<String, String> respHeaders = new HashMap<String, String>();
            for (String key : connection.getHeaderFields().keySet()) {
                respHeaders.put(key, connection.getHeaderFields().get(key).get(0));
            }
            OfflineLog.yab.debug("resp header, %s", respHeaders);

            InputStream stream = connection.getInputStream();
            String resp = OAuthHelper.getStreamContents(stream);
            return resp;
        } catch (Exception e) {
            throw new OAuthException("error when sending req at " + getStep(), "", false, e, null);
        }
    }
}
