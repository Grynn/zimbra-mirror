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

import org.apache.commons.httpclient.methods.PutMethod;

import com.google.gdata.client.authn.oauth.OAuthUtil;
import com.zimbra.common.httpclient.HttpClientUtil;
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

            PutMethod putMethod = new PutMethod(getEndpointURL());
            putMethod.setRequestBody(this.request);
            String header = OAuthHelper.extractHeader(this.params);
            putMethod.setRequestHeader("Authorization", header);
            int code = HttpClientUtil.executeMethod(httpClient, putMethod);
            if (code != 200) {
                throw OAuthException.handle(code, getStep());
            }
            InputStream stream = putMethod.getResponseBodyAsStream();
            String resp = OAuthHelper.getStreamContents(stream);
            return resp;
        } catch (Exception e) {
            OfflineLog.yab.error("sending oauth put request error", e);
            StringBuilder msg = new StringBuilder();
            msg.append("error when sending req at ").append(getStep());
            if (e instanceof OAuthException) {
                msg.append(" Internal reason: " + e.getMessage());
                throw new OAuthException(msg.toString(), ((OAuthException) e).getCode(), false, e, null);
            }
            throw new OAuthException(msg.toString(), "", false, e, null);
        }
    }
}
