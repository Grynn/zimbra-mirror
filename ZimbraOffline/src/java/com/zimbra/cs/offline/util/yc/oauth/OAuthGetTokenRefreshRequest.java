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

public class OAuthGetTokenRefreshRequest extends OAuthGetTokenRequest {

    public OAuthGetTokenRefreshRequest(OAuthToken token) {
        super(token, null);
    }

    @Override
    protected void doFillSpecificParams() {
        super.doFillSpecificParams();
        this.removeParam(OAuthConstants.OAUTH_VERIFIER);
        this.addParam(OAuthConstants.OAUTH_SESSION_HANDLE, getToken().getSessionHandle());
    }

    @Override
    protected String getVerifier() {
        return "n";
    }

    @Override
    protected String getStep() {
        return "(step 4, refresh)";
    }
}
