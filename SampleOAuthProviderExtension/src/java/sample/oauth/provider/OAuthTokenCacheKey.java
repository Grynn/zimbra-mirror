/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package sample.oauth.provider;

import com.zimbra.common.util.memcached.MemcachedKey;
import com.zimbra.cs.memcached.MemcachedKeyPrefix;

import net.oauth.OAuthAccessor;

public class OAuthTokenCacheKey implements MemcachedKey {
	
	public static final String REQUEST_TOKEN_PREFIX = "req:";
    public static final String ACCESS_TOKEN_PREFIX = "acc:";
    
    private String mToken;
    private String mKeyPrefix;
    private String mKeyVal;

    public OAuthTokenCacheKey(String consumer_token,String key_prefix) {
    	
    	mToken = consumer_token;
    	mKeyPrefix = key_prefix;
    	mKeyVal = mToken;
    }

    public String getCounsumerToken() { return mToken; }
    
    public boolean equals(Object other) {
        if (other instanceof OAuthTokenCacheKey) {
            OAuthTokenCacheKey otherKey = (OAuthTokenCacheKey) other;
            return mKeyVal.equals(otherKey.mKeyVal);
        }
        return false;
    }

    public int hashCode()    { return mKeyVal.hashCode(); }
    public String toString() { return mKeyVal; }

    // MemcachedKey interface
    public String getKeyPrefix() { return mKeyPrefix; }
    public String getKeyValue() { return mKeyVal; }
}
