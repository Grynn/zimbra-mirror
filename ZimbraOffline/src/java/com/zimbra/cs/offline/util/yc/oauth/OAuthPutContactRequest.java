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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gdata.client.authn.oauth.OAuthUtil;
import com.zimbra.cs.offline.OfflineLog;

public class OAuthPutContactRequest extends OAuthRequest {

    String request;

    public OAuthPutContactRequest(OAuthToken token, String req) {
        super(token);
        this.request = req;
    }

    @Override
    protected String getEndpointURL() {
        String url = String.format(OAuthConstants.OAUTH_PUT_CONTACTS_URL, getToken().getGuid());
        OfflineLog.yab.debug("put contacts url: %s", url);
        return url;
    }

    @Override
    protected String getHttpMethod() {
        return OAuthConstants.PUT_METHOD;
    }

    @Override
    protected void doFillSpecificParams() {
        this.addParam(OAuthConstants.OAUTH_CONSUMER_KEY, OAuthConstants.OAUTH_CONSUMER_KEY_VALUE);
        this.addParam(OAuthConstants.OAUTH_NONCE, OAuthUtil.getNonce());
        this.addParam(OAuthConstants.OAUTH_SIGNATURE_METHOD, OAuthHelper.getSignatureMethod());
        this.addParam(OAuthConstants.OAUTH_TIMESTAMP, OAuthUtil.getTimestamp());
        this.addParam(OAuthConstants.OAUTH_VERSION, "1.0");
        this.addParam(OAuthConstants.OAUTH_TOKEN, getToken().getToken());
    }

    @Override
    protected String getStep() {
        return "Put contact";
    }

    @Override
    public String send() throws OAuthException {
        try {
            fillParams();

            HttpURLConnection connection = (HttpURLConnection) new URL(getEndpointURL()).openConnection();
            connection.setRequestMethod(getHttpMethod());
            String header = OAuthHelper.extractHeader(this.params);
            connection.setRequestProperty("Authorization", header);
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(this.request);
            writer.close();

            connection.connect();
            int code = connection.getResponseCode();
            if (code != 200) {
                OfflineLog.yab.debug("yahoo sync put failed, http error code: %s", code);
                throw OAuthException.handle(code, getStep());
            }
            InputStream stream = connection.getInputStream();
            String resp = OAuthHelper.getStreamContents(stream);
            return resp;
        } catch (Exception e) {
            throw new OAuthException("error when sending req at " + getStep(), "", false, e, null);
        }
    }
}
