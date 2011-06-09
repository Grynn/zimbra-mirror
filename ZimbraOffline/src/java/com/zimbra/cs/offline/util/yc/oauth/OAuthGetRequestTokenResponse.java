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

import com.zimbra.cs.offline.util.yc.YContactException;


public class OAuthGetRequestTokenResponse extends OAuthResponse {

    public OAuthGetRequestTokenResponse(String resp) throws YContactException {
        super(resp);
    }

    @Override
    protected void handleResponse() {
        String token = getByKey(OAuthConstants.OAUTH_TOKEN);
        String tokenSecret = getByKey(OAuthConstants.OAUTH_TOKEN_SECRET);
        String url = getByKey(OAuthConstants.OAUTH_REQUEST_AUTH_URL);
        OAuthToken otoken = new OAuthToken(token, tokenSecret);
        otoken.setNextUrl(url);
        this.setToken(otoken);
    }

}
