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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zimbra.cs.offline.util.yc.YContactException;

public abstract class OAuthResponse {

    private String rawResp;
    private OAuthToken token;
    
    public OAuthResponse(String resp) throws YContactException {
        this.rawResp = resp;
        handleResponse();
    }
    
    protected abstract void handleResponse() throws YContactException;

    protected String getRawResponse() {
        return this.rawResp;
    }
    
    public OAuthToken getToken() {
        return this.token;
    }
    
    public void setToken(OAuthToken token) {
        this.token = token;
    }
    
    protected String getByKey(String key) {
        Pattern p = Pattern.compile(key+"=([^&]+)");
        Matcher matcher = p.matcher(this.rawResp);
        if (matcher.find() && matcher.groupCount() > 0) {
            return matcher.group(1);
        }
        return "";
    }
    
    public String toString() {
        return this.rawResp;
    }
}
