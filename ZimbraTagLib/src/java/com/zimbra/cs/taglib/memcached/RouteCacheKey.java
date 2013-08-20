/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.cs.taglib.memcached;

import com.zimbra.common.util.memcached.MemcachedKey;

public class RouteCacheKey implements MemcachedKey {
    private String mKeyStr;
    private String ROUTE_TAGS= "route:proto=";

    public RouteCacheKey(String protocolMode, String accountId) {
        mKeyStr = protocolMode + ";id=" + accountId;
    }

    public boolean equals(Object other) {
        if (other instanceof RouteCacheKey) {
            RouteCacheKey otherKey = (RouteCacheKey) other;
            return mKeyStr.equals(otherKey.mKeyStr);
        }
        return false;
    }

    public int hashCode() {
        return mKeyStr.hashCode();
    }

    // MemcachedKey interface
    public String getKeyPrefix() { return ROUTE_TAGS; }
    public String getKeyValue() { return mKeyStr; }
}