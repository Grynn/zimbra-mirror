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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.zimbra.cs.offline.util.yc.YContactException;

public class OAuthGetTokenResponse extends OAuthResponse {

    public OAuthGetTokenResponse(String resp) throws YContactException {
        super(resp);
    }

    @Override
    protected void handleResponse() throws YContactException {
        String token;
        try {
            token = URLDecoder.decode(getByKey(OAuthConstants.OAUTH_TOKEN), "UTF-8");
            String tokenSecret = URLDecoder.decode(getByKey(OAuthConstants.OAUTH_TOKEN_SECRET), "UTF-8");
            OAuthToken otoken = new OAuthToken(token, tokenSecret);
            otoken.setSessionHandle(getByKey(OAuthConstants.OAUTH_SESSION_HANDLE));
            otoken.setGuid(getByKey(OAuthConstants.OAUTH_YAHOO_GUID));
            this.setToken(otoken);
        } catch (UnsupportedEncodingException e) {
            throw new OAuthException("error when decoding token", "", false, e, null);
        }
    }
}
